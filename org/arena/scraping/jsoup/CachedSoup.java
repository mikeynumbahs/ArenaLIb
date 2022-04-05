package org.arena.scraping.jsoup;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.arena.date.DateTools;
import org.arena.io.Console;
import org.arena.io.SimpleFile;
import org.arena.table.StringTable;
import org.arena.util.ArenaList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class CachedSoup {
	protected URL url; 
	private File file;
	private Calendar lastWrite;
	private boolean jsonLoaded = false;
	protected boolean isCacheFile = false;
	protected boolean isLoaded = false;
	protected boolean wasCached = false;
	
	protected Document page;
	
	public final ArenaList<StringTable> tables;
	public final HashMap<String, JSONObject> jsonObjects;
	public final HashMap<URL, String> jsonLinks;
	
	protected String charset = "UTF-8";
	protected boolean stripScripts = false;
	protected boolean stripStyles = false;
	
	protected int cacheFrequency = 12;
	protected int maxAttempts = 5;
	protected long retrySleep = 2000;
	protected boolean minUpdateDaily = true;
	
	abstract protected String getCacheRootDir();
	abstract protected String getFileExt();
	abstract protected boolean isVolatile();
	
	
	public static final int TOO_MANY_REQUESTS = 429;
	public static final int SERVICE_UNAVAILABLE = 503;
	
	public CachedSoup(URL url) {
		this.url = url;
		tables = new ArenaList<>();
		jsonObjects = new HashMap<>();
		jsonLinks = new HashMap<>();
	}
	
	public CachedSoup(URL url, String charset) {
		this(url);
		this.charset = charset;
	}
	
	
	public boolean load() {
		if (isLoaded && !Options.refresh) return true;
		if (!Options.workOffline
		&& !Options.readOnly
		&& (!fileUpToDate() || Options.refresh) ) {
			if (downloadPage(url)) {
				addJsonSources();
				wasCached = cacheToDisk();
				isCacheFile = false;
				return isLoaded = true;
			} else return handleErr(file);
		} else {
			return isLoaded = loadFromDisk();
		}
	}
	
	
	protected boolean handleErr(File cacheFile) {
		Console.println(this + " Failed to load " + url);
		if (cacheFile.exists()) {
			Console.println("Will use cache file at " + cacheFile.getAbsolutePath());
			return loadFromDisk();
		} else {
			Console.println("Fatal error: No cache file available. \nReturning.");
			return false;
		}
	}
	
	
	public String getFileName() {
		String fileName = getURL().toString().replaceAll("[\\?\\%]", ".")	
								  .replace(getDomain(), getCacheRootDir())
								  .replaceAll("\\/+", File.separator);
	
		return fileName + "." + getFileExt(); 
	}

	
	protected boolean downloadPage(URL url) {
		return (page = download(url)) != null;
	}
	
	
	public void addJsonLink(URL url, String name) {
		jsonLinks.put(url, name);
	}
	
	
	protected String downloadJson(URL url) {
		Document doc = download(url);
		
		String json = "";
		if (doc != null && !isErrDoc(doc)) {
			json = doc.body().toString();
		}
		return json;
	}
	
	
	private void addJsonSources() {
		for (URL url : jsonLinks.keySet()) {
			String text = downloadJson(url);
			
			Element tag = new Element("json");
			tag.id(jsonLinks.get(url));
			tag.html(text);
			page.appendChild(tag);
		}
	}
	
	
	private Document download(URL url) {
		int attempts = 0;
		
		do {
			if (attempts > 0 && Debug.verboseLoad) 
				Console.println("Download attempt #" + (attempts+1));
			
			try {
				Connection conn = Jsoup.connect(url.toString());
				conn.userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0");
				conn.ignoreContentType(true);
				conn.referrer(getDomain());
				conn.header("Connection", "keep-alive")
					.header("Accept-Encoding", "gzip, deflate")
					.header("Accept-Language", "en-US,en;q=0.5");
				return Jsoup.parse(conn.get().html(), charset);
			} catch (Exception e) {
				if (e instanceof HttpStatusException) {
					HttpStatusException hse = (HttpStatusException)e;
					int status = hse.getStatusCode();
					Document err = CachedSoup.makeErrDoc(hse);
					
					if (status != TOO_MANY_REQUESTS
					&& status != SERVICE_UNAVAILABLE) {
						return err;
					}
				}
				
				if (Debug.verboseIO || Debug.verboseLoad) 
					e.printStackTrace();
			}
			
			sleep(retrySleep);
		} while (attempts++ < maxAttempts);
		return null;
	}
	
	
	public void sleep(long ms) {
		long start = Calendar.getInstance().getTimeInMillis();
		while ((Calendar.getInstance().getTimeInMillis() - start) < ms);
	}
	
	
	protected boolean loadFromDisk() {
		File file = getFile();
		try {
			page = Jsoup.parse(file, charset);
			if (Debug.verboseIO || Debug.verboseLoad) 
				Console.println("Cache file, " + file.getAbsolutePath() + " Loaded.");
			
			checkCache();
			
			setLastWrite(file.lastModified());
		} catch (Exception e) {
			if (Debug.verboseIO || Debug.verboseLoad) 
				e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	protected final void checkCache() {
		if (isErrDoc()) {
			if (Debug.verboseIO || Debug.verboseLoad) 
				Console.println("Err Document Loaded");
			
			int code = getErrDocCode();
			if (code == TOO_MANY_REQUESTS
			|| code == SERVICE_UNAVAILABLE) {
				if (Debug.verboseIO || Debug.verboseLoad)
					Console.println("Attempting to refresh cache with previous status code:  " + code);
				
				if (downloadPage(url)) {
					cacheToDisk(true);
				}
			}
		} else isCacheFile = true;	
	}
	
	
	public void stripScripts(boolean stripScripts) {
		this.stripScripts = stripScripts;
	}
	
	
	public void stripStyles(boolean stripStyles) {
		this.stripStyles = stripStyles;
	}
	
	
	protected boolean cacheToDisk() {
		return cacheToDisk(false);
	}
	
	
	protected boolean cacheToDisk(boolean forced) {
		File file = getFile();
		if (!forced && !Options.refresh && fileUpToDate() && !Options.readOnly)
			return false;
	
		if (Debug.verboseIO) 
			Console.println("Cache file being written: " + file.getAbsolutePath());
		
		if (SimpleFile.write(file.getAbsolutePath(), makeFile())) {
			setLastWrite(Calendar.getInstance());
			return true;
		}
	
		return false;
	}
	
	
	protected String makeFile() {
		if (stripScripts)
			page.select("script").remove();
		
		if (stripStyles)
			page.select("style").remove();
		
		return page.toString();
	}
	
	
	protected Calendar getLastWrite() {
		if (lastWrite == null) {
			File file = getFile();
			if (!file.exists()) return null;
			
			lastWrite = Calendar.getInstance();
			lastWrite.setTimeInMillis(file.lastModified());
		}
		return lastWrite;
	}
	
	
	public final Long lastWriteInMillis() {
		return getLastWrite().getTimeInMillis();
	}
	
	
	public final String getLastWriteTimeStamp() {
		return getLastWrite().getTime().toString();
	}
	
	
	protected void setLastWrite(long timeInMillis) {
		lastWrite = Calendar.getInstance();
		lastWrite.setTimeInMillis(timeInMillis);
	}

	
	protected void setLastWrite(Calendar lastWrite) {
		this.lastWrite = lastWrite;
	}
	
	
	protected File getFile() {
		if (file == null)
			file = new File(getFileName());
		
		return file;
	}
	
	
	public final boolean fileUpToDate() {
		File file = getFile();
		if (!file.exists()) {
			return false;
		} else {
			if (Options.readOnly) return true;
		}
		
		if (!isVolatile()) return true;
		
		Calendar now = Calendar.getInstance();
		Calendar lastModified = Calendar.getInstance();
		lastModified.setTimeInMillis(file.lastModified());
		Calendar xHoursAfter = (Calendar)lastModified.clone();
		xHoursAfter.set(Calendar.HOUR_OF_DAY, lastModified.get(Calendar.HOUR_OF_DAY)+cacheFrequency);
			
		boolean cachedWithinXhours = xHoursAfter.after(now);
		boolean minUpdated = minUpdateDaily ? DateTools.sameDay(lastModified, now) : true;
		
		return cachedWithinXhours && minUpdated;
	}
	
	
	public final String getDomain() {
		return url.toString().replaceAll("(.*://[^/]+).*", "$1");
	}
	
	
	public final URL getURL() {
		return url;
	}
	
	
	public final Boolean isCacheFile() {
		return isCacheFile;
	}
	
	
	public final boolean isLoaded() {
		return isLoaded;
	}
	
	
	public final boolean updated() {
		return this.wasCached == true;
	}
	
	
	public final Document getPage() {
		if (page == null) 
			downloadPage(url);
		
		return this.page;
	}
	
	
	public void loadJson() {
		if (jsonLoaded == true) 
			return;
		
		JSONParser parser = new JSONParser();
		getPage().select("json").forEach(ele -> {
			try {
				String id = ele.attr("id");
				JSONObject obj = (JSONObject)parser.parse(ele.text());
				jsonObjects.put(id, obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		jsonLoaded = true;
	}
	
	
	public void loadTables() {
		loadTables(false);
	}
	
	
	public void loadTables(boolean retainHtml) {
		tables.clear();
	
		getPage().select("table").forEach(e -> {
				String tableName = "";
				
				List<String[]> rh = new ArrayList<>();
				Elements header = e.getElementsByTag("thead");
				boolean hasHeader = header.size() != 0;
				if (hasHeader) {
					Elements headerRows = header.get(0).getElementsByTag("tr");
					if (headerRows.size() > 1) {
						String[] suspectedName = getTableRow(headerRows.remove(0), false, retainHtml);
						if (suspectedName.length == 1) 
							tableName = suspectedName[0];
						header = headerRows;
					}
					
					String[] aheader = getTableRow(header.get(0), false, retainHtml);
					if (aheader == null || aheader.length == 0)
						aheader = getTableRow(header.get(0), true, retainHtml);
					
					if (aheader.length != 0) 
						rh.add(aheader);
					else hasHeader = false;
				}
				
				
				Elements rows = e.getElementsByTag("tr");
				for (int i = hasHeader ? 1 : 0; i < rows.size(); i++) {
					String[] arow = getTableRow(rows.get(i), true, retainHtml);
					rh.add(arow);
				}
				if (rh.size() > 0) {
					StringTable newTable = new StringTable(rh, tableName, hasHeader);
					tables.add(newTable);
				}
			});
	}
	
	
	public final void trimTables() {
		if (tables != null) {
			tables.removeIf(e -> e == null || e.getLength() == 0);
		}
	}
	
	
	public final void trimTables(Function<StringTable, Boolean> function) {
		if (tables != null) {
			tables.removeIf(e -> function.apply(e));
		}
	}
	
	
	private String[] getTableRow(Element element, final boolean tdOrTh, final boolean retainHtml) {
		String[] row = element.getElementsByTag(tdOrTh ? "td" : "th")
				.stream()
				.map(cell -> 
					tdOrTh ? retainHtml ? cell.html() : cell.text() : cell.text().toUpperCase())
				.collect(Collectors.toList())
				.toArray(new String[] {});
		
		formatStrings(row);
		return row;
	}
	
	
	private void formatStrings(String[] string) {
		for (int i = 0; i < string.length; i++) {
			try {
				string[i] = removeHtmlCharCodes(string[i]);
			} catch (Exception e) {}
		}
	}
	
	
	public static String removeHtmlCharCodes(String string) {
		return string.replace("&amp;", "&")
					 .replace("&nbsp;", " ")
					 .replace("&quot;", "\"")
					 .replace("&#x27;", "'");
	}
	
	
	public StringTable getTableByName(String name) {
		if (tables != null) {
			for (StringTable t : tables) {
				if (t.getName().equals(name))
					return t;
				StringTable table = t.getChildByName(name);
				if (table != null)
					return t;
			}
		}
		return null;
	}
	
	
	public void printTables() {
		if (tables != null) {
			for (StringTable table : tables) {
				table.printTable();
			}
		}
	}
	
	
	public static final Document makeErrDoc(HttpStatusException hse) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		hse.printStackTrace(pw);
		String stackTrace = sw.toString().replaceAll("\\sat\\s", "<BR> at ");
		
		Document errDoc = Jsoup.parse(""
				+ "<!DOCTYPE html>"
				+ "<HTML><HEAD><TITLE>CachedSoup Generated Document</TITLE></HEAD>"
				
				+ "<RESPONSE STATUSCODE=\"" + hse.getStatusCode() + "\"" 
						 + " MESSAGE=\"" + hse.getMessage() + "\""
						 + " URL=\"" + hse.getUrl() + "\">"
				+ stackTrace
				+ "</RESPONSE>"
				+ "</HTML>");
	
		return errDoc;
	}
	
	
	public final boolean isErrDoc() {
		return isErrDoc(getPage());
	}
	
	
	public final boolean isErrDoc(Document page) {
		try {
			boolean title = page.title().equals("CachedSoup Generated Document");
			
			Element response = page.getElementsByTag("response").get(0);
			boolean status = response.hasAttr("statuscode");
			boolean message = response.hasAttr("message");
			boolean url = response.hasAttr("url");
			
			return (title && status && message && url);
		} catch (Exception oob) {
			return false;
		}
	}
	
	
	public final Integer getErrDocCode() {
		try {
			Document page = getPage();
			String code = page.getElementsByTag("response").get(0).attr("statuscode");
			return Integer.valueOf(code);
		} catch (Exception notErrDoc) {
			return null;
		}
	}
	
	
	public static class Options {
		public static boolean workOffline = false;
		public static boolean refresh = false;
		public static boolean readOnly = false;
	}
	
	
	public static class Debug {
		public static boolean verboseIO = false;
		public static boolean verboseLoad = false;
	}
}