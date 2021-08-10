package org.arena.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public interface Console {

	static void print(char[] chars) {
		System.out.print(chars);
	}
	
	static void print(CharSequence chars) {
		System.out.print(chars);
	}
	
	static void print(String string) {
		System.out.print(string);
	}
	
	static void print(Object object) {
		System.out.print(object);
	}
	
	static void println() {
		System.out.println();
	}
	
	static void println(char[] chars) {
		System.out.println(chars);
	}
	
	static void println(CharSequence chars) {
		System.out.println(chars);
	}
	
	static void println(String string) {
		System.out.println(string);
	}
	
	static void println(Object object) {
		System.out.println(object.toString());
	}
	
	static void printf(String format, Object... objects) {
		System.out.printf(format, objects);
	}
	
	static void printf(String format, Number... numbers) {
		printf(format, (Object[])numbers);
	}
	
	static void append(CharSequence c) {
		System.out.append(c);
	}
	
	static void append(String string) {
		System.out.append(string);
	}
	
	static void append(Object object) {
		System.out.append(object.toString());
	}
	
	static String getInput() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		return reader.readLine();
	}
	
	static String getInput(String msg) throws IOException {
		print(msg);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		return reader.readLine();
	}
}
