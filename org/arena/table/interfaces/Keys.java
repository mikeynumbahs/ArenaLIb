package org.arena.table.interfaces;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.arena.util.ArenaList;
import org.arena.util.UniqueList;

public interface Keys {
	
	static <K extends Keys> ArenaList<String[]> translateKeys(K set1, K set2) {
		ArenaList<String[]> keySet = new ArenaList<>();
		
		for (Method method : set1.getKeyMethods()) {
			String str = method.toString();
			try {
				String fnName = str.replaceAll(".*(get.*Key)\\(\\)", "$1");
				if (!fnName.equals(str)) {
					String key1 = method.invoke(set1).toString();
					String key2 = set2.getClass().getMethod(fnName).invoke(set2).toString();
					keySet.add(new String[] { key1, key2 });
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return keySet;
	}
	
	
	default UniqueList<Method> getKeyMethods() {
		UniqueList<Method> methods = new UniqueList<>();
		
		for (Method method : this.getClass().getMethods()) {
			String str = method.toString();
			if (str.matches("(?i).*\\.get.*Key\\(\\)")) {
				methods.add(method);
			}
		}

		return methods;
	}
	
	
	default Method getKeyMethod(String key) {
		for (Method method : getKeyMethods()) {
			try {
				if (invoke(method).toString().equals(key)) {
					return method;
				}
			} catch (Exception nully) {}
		}
		return null;
	}
	
	
	default ArenaList<Method> getKeyMethods(List<String> keys) {
		ArenaList<Method> methods = new ArenaList<>();
		for (String key : keys) {
			methods.add(getKeyMethod(key));
		}
		
		return methods;
	}
	
	
	default UniqueList<String> getKeyMethodNames() {
		UniqueList<String> keys = new UniqueList<>();
	
		for (Method method : this.getClass().getMethods()) {
			String str = method.toString();
			if (str.matches("(?i).*\\.get.*Key\\(\\)")) {
				keys.add(method.getName());
			}
		}
		
		return keys;
	}
	
	
	default ArenaList<String> getKeyMethodNames(List<String> keys) {
		ArenaList<String> names = new ArenaList<>();
		for (String key : keys) {
			try {
				Method method = getKeyMethod(key);
				names.add(method == null ? null : method.getName());
			} catch (Exception noMethod) {
				noMethod.printStackTrace();
			}
		}
		
		names.removeNull();
		return names;
	}

	
	default String getKey(String key) {
		return invoke("get"+key+"Key").toString();
	}
	
	
	default Object invoke(Method method) {
		return invoke(method.getName());
	}
	
	
	default Object invoke(String method) {
		try {
			return getClass().getMethod(method).invoke(this);
		} catch (NoSuchMethodException nm) {
			//nm.printStackTrace();
			return null;
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
			return null;
		} catch (IllegalAccessException ace) {
			ace.printStackTrace();
			return null;
		}
	}
}
