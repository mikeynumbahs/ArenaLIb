package org.arena.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public interface SimpleFile {
	static char comma = 222;
	
	
	
	static boolean write(String filename, String data) {
		return write(filename, data, true);
	}
	
	
	static boolean write(String filename, String[] data) {
		return write(filename, data, true);
	}
	
	
	static boolean writeCsv(String filename, String[][] data) {
		return writeCsv(filename, data, true);
	}
	
	
	static boolean write(String filename, Double[] data) {
		return write(filename, data, true);
	}
	
	
	static boolean writeCsv(String filename, List<List<String>> data) {
		return write(filename, SimpleFile.<String>arrayToString(data), true);
	}
	
	
	static boolean write(String filename, String[] data, boolean overWrite) {
		return write(filename, arrayToString(data), overWrite);
	}
	
	
	static boolean write(String filename, Double[] data, boolean overWrite) {
		return write(filename, arrayToString(data), overWrite);
	}
	
	
	static boolean writeCsv(String filename, String[][] data, boolean overWrite) {
		return write(filename, arrayToString(data), overWrite);
	}
	
	
	static boolean writeMatrix(String filename, Number[][] data) {
		return writeMatrix(filename, data, true);
	}
	
	
	static boolean writeMatrix(String filename, Number[][] matrix, boolean overWrite) {
		return write(filename, arrayToString(matrix), overWrite);
	}
	
	
	static boolean write(String filename, String data, boolean overWrite) {
		try {
			File f = new File(filename);
			
			if (f.exists()) {
				if (overWrite) {
					f.delete();
					if (Options.verbose) Console.println("Deleted " + filename);
				} else {
					if (Options.verbose) Console.println(filename + " exists. Returning.");
					return false;
				}
			}
			
			new File(f.getParent()).mkdirs();
			f.createNewFile();
			FileWriter writer = new FileWriter(f);
			
			writer.write(data);
			writer.flush();
			writer.close();
			
			if (Options.verbose)
				Console.println("Writing of " + filename + " succeeded");
			return true;
		} catch (Exception e) {
			if (Options.verbose) {
				Console.println("Failed to write to " + filename);
				handleError(e);
			}
			return false;
		}
	}
	
	
	static String load(String fileName) {
		return load(new File(fileName));
	}
	
	
	static String load(File file) {
		String text = new String();
		BufferedReader reader = getReader(file);
		String line = new String();
		try {
			while ((line = reader.readLine()) != null)
				text += line + "\n";
		} catch (IOException e) {
			if (Options.verbose) {
				Console.println("Failed to load: " + file);
				handleError(e);
			}
		}
		
		if (Options.verbose)
			Console.println("Loaded " + file.getAbsolutePath());
		return text;
	}
	
	
	static String[] loadAsArray(String filename) {
		String[] array = load(filename).split("\n");
		if (Options.verbose) 
			Console.println("\tReturning as array.");
		
		return array;
	}
	
	
	static String[][] loadCsv(String filename) {
		try {
			String[] file = loadAsArray(filename);
		
			List<String[]> csv = new ArrayList<>();
			for (int i = 0; i < file.length; i++) {
				List<String> temp = new ArrayList<>();
				String[] vals = file[i].split(", ");
				for (String val : vals)
					temp.add(val.replace(""+ comma, ",").trim());
				csv.add(temp.toArray(new String[] {}));
			}
		
			return csv.toArray(new String[][] {});
		} catch (Exception nocsv) {
			if (Options.verbose) {
				Console.println("Failed to load " + filename + " as a CSV file.");
				handleError(nocsv);
			}
			return null;
		}
	}
	
	
	static List<List<String>> loadCsvList(String filename) {
		String[][] file = loadCsv(filename);
		List<List<String>> load = new ArrayList<>();
		for (int i = 0; i < file.length; i++) 
			load.add(new LinkedList<String>(Arrays.asList(file[i])));
		return load;
	}
	
	
	static Double[][] loadMatrix(String filename) {
		try {
			String[] file = loadAsArray(filename);
		
			List<Double[]> csv = new ArrayList<>();
			for (int i = 0; i < file.length; i++) {
				List<Double> temp = new ArrayList<>();
				String[] vals = file[i].split(", ");
				for (String val : vals)
					temp.add(Double.valueOf(val.trim()));
				csv.add(temp.toArray(new Double[] {}));
			}
		
			return csv.toArray(new Double[][] {});
		} catch (Exception nocsv) {
			if (Options.verbose) {
				Console.println("Failed to load " + filename + " as a matrix.");
				handleError(nocsv);
			}
			return null;
		}
	}
	
	
	static BufferedReader getReader(String fileName) {
		return getReader(new File(fileName));	
	}
	
	
	static BufferedReader getReader(File file) {
		try {
			return new BufferedReader(new FileReader(file));	
		} catch (Exception e) {
			if (Options.verbose) {
				Console.println("Failed to load file reader for " + file.getAbsolutePath());
				handleError(e);
			}
		}
		return null;
	}
	
	
	static String arrayToString(Object[] data) {
		String string = new String();
		for (Object o : data) 
			string += o.toString() + "\n";
		
		return string;
	}
	
	
	static String arrayToString(Object[][] data) {
		String string = new String();
		for (int i = 0; i < data.length; i++) {
			String line = new String();
			for (int j = 0; j < data[i].length; j++) {
				boolean eol = j == data[i].length-1;
				String item = data[i][j].toString().replace(",", "" + comma);
				line += eol ? item : item + ", ";
			}
			string += line + "\n";
		}
	
		return string;
	}
	
	
	static <E extends Object> String arrayToString(List<List<E>> data) {
		String string = new String();
		for (int i = 0; i < data.size(); i++) {
			String line = new String();
			for (int j = 0; j < data.get(i).size(); j++) {
				boolean eol = j == data.get(i).size()-1;
				String item = data.get(i).get(j).toString().replace(",", "" + comma);
				line += eol ? item : item + ", ";
			}
			string += line + "\n";
		}
		return string;
	}
	
	
	static void handleError(Exception e) {
		if (Options.stacktrace) e.printStackTrace();  
		else Console.println("\t" + e.getMessage());
	}
	
	
	public static class Options {
		public static boolean verbose = false;
		public static boolean stacktrace = false;
	}
}