package org.arena.math.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.arena.io.Console;
import org.arena.util.UniqueList;

public class Expressions {
	private static String regexOps = "\\+|\\-|\\*|\\/|\\^|\\%";

	private static final char[] operators = new char[] { '+', '-', '*', '/', '^', '%' };
	
	protected static final char[] ooo = new char[]  { '^', '%', '*', '/', '+', '-' };
	protected static final char[] oood = new char[] { '^', '%', '*', '/' };
	protected static final char[] oooa = new char[] { '+', '-' };
	
	//ARRAYS AND LISTS FOR USER DEFINED OPERATORS AND FUNCTIONS
	protected char[] extOps = new char[] {};
	protected List<String> extOpDefs = new UniqueList<>();
	protected List<Function<Double[], Double>> extOpFn = new UniqueList<>();
	
	private static final String[] stdFn = new String[] { 
			"sqrt", "cbrt", "exp", "ln", "log", "logx", "cos", "sin", "tan", "cot", "sec", "csc", 
			"acos", "asin", "atan", "acot", "asec", "acsc", "cosh", "sinh", "tanh", "abs", 
			"inv", "logisfx", "mean", "meanxsd", "absmean", "sd"
	};
	
	private static final String[] stdConst = new String[] { "e", "pi", "phi" };
	
	//LISTS FOR USER DEFINED FUNCTIONS
	protected final List<String> extFnDefs = new UniqueList<>();
	protected final List<Function<Double[], Double>> extFn = new UniqueList<>();
	
	//LISTS FOR USER DEFINED CONSTANTS
	protected final List<String> extConstDefs = new UniqueList<>();
	protected final List<Supplier<Double>> extConsts = new UniqueList<>();	
	
	public Double eval(String expression) {
		return prepAndEval(expression);
	}
	
	private Double prepAndEval(String expression) {
		String prepared = expression.substring(0).replaceAll("\\s", "");
		while (prepared.indexOf("((") >= 0) 
			prepared = prepared.replace("((", "(1*(");
		
		prepared = processExtOps(prepared);
		prepared = processConstants(prepared);

		int lastIdx = -1;
	
		while ((lastIdx = prepared.lastIndexOf("(")) >= 0) {
			int close = prepared.indexOf(")", lastIdx);
			String nestedArgs = prepared.substring(lastIdx+1, close);

			Double nested = null;
			if (lastIdx > 0 && !isOperator(prepared.charAt(lastIdx-1))) {
				int fnNameEnd = lastIdx--;
   				  
				for (; lastIdx > 0 && !isOperator(prepared.charAt(lastIdx)) && !isBracket(prepared.charAt(lastIdx)); lastIdx--) 
					if (isOperator(prepared.charAt(lastIdx-1)) || isBracket(prepared.charAt(lastIdx-1))) 
   						  break;
   			
				String fnName = prepared.substring(lastIdx, fnNameEnd);
				String[] args = nestedArgs.split(",");
				Double[] argVals = new Double[args.length];

				for (int a = 0; a < argVals.length; a++) 
					argVals[a] = evalPreppedExpression(args[a].trim());
   				
				nested = evalFn(fnName, argVals);
			} else {
				nested = evalPreppedExpression(prepared.substring(lastIdx+1, close));
			}
   			
			String append = close < prepared.length()-1 ? prepared.substring(close+1) : "";
			prepared = (lastIdx != 0 ? prepared.substring(0, lastIdx) : "") + nested + append;
   	  } 

   	  return evalPreppedExpression(prepared);
	}
	
	private String processExtOps(String expr) {
		String processed = expr.substring(0);
		for (String op : extOpDefs)
			processed = processed.replaceAll("([0-9])"+op+"([0-9])", "$1"+extOps[extOpDefs.indexOf(op)]+"$2");
	
		return processed;
	}
	
	private String processConstants(String expr) {
		List<Integer> opidx = new ArrayList<>();
		for (int o = 0; o < expr.length(); o++) {
			char c = expr.charAt(o);
			if (isOperator(expr.charAt(o)) || isBracket(c))
				opidx.add(o);
		}
	
		if (!opidx.isEmpty()) {
			String lastTerm = expr.substring(opidx.get(opidx.size()-1)+1, expr.length());
			expr = expr.substring(0, opidx.get(opidx.size()-1) + 1) + translateConstant(lastTerm);

			for (int i = opidx.size()-1; i >= 1; i--) {
				String term = expr.substring(opidx.get(i-1)+1, opidx.get(i));
				expr = expr.substring(0, opidx.get(i-1)+1) + translateConstant(term) + expr.substring(opidx.get(i));
			}
			String firstTerm = expr.substring(0, opidx.get(0));
			expr = translateConstant(firstTerm) + expr.substring(opidx.get(0));
		} else {
			return translateConstant(expr);
		}
		
		return expr;
	}
	
	private Double evalPreppedExpression(String preppedExpression) {
		//Mask numbers ending in E+XXX or E-XXX to preserve scientific notation
		String redmod = preppedExpression.replaceAll("\\dE(-|\\+)", "XXX"); 
		String opmask = redmod.replaceAll("[^"+regexOps+"]", " ");
		List<String> exparray = new ArrayList<>();
		int offset = 0;

		for (int i = 0; i < opmask.length(); i++) {
			char c = opmask.charAt(i);
			if (c != ' ') {
				String substring = preppedExpression.substring(offset, i++).trim();
				if (!substring.isBlank()) 
					exparray.add(substring);
				
				exparray.add(""+c);
				offset = i;
			}
		}
			
		if (offset != preppedExpression.length())
			exparray.add(preppedExpression.substring(offset));
		return evalReducedExpression(exparray);
			
	}
		
	private Double evalReducedExpression(List<String> exparray) {
		for (char op : oood) {
			for (int i = exparray.size()-1; i > 0; i--) {
				String item = exparray.get(i);
				if (!item.isBlank() && item.equals(""+op)) {
					double result = 0;
					double left = i > 0 ? evalPreppedExpression(exparray.get(i-1)) : 0;
					double right = evalPreppedExpression(exparray.get(i+1));
					
					switch (op) {
						case '^' :
							result = Math.pow(left, right);
							break;
						case '%' :
							int whole = (int)(left/right);
							result = left - (right*whole);
							break;
						case '*' :
							result = left*right;
							break;
						case '/' :
							result = left/right;
							break;
					}
					exparray.set(i-1, ""+result);
					exparray.remove(i+1);
					exparray.remove(i--);
				}
			}
		}
	
		for (int i = 0; i < exparray.size()-1; i++) {
			String item = exparray.get(i);
			double result = 0;
			
			if (!item.isBlank() && isOperator(item.charAt(0))) {
				double right = evalPreppedExpression(exparray.get(i+1));
				double left = i == 0 ? 0 : evalPreppedExpression(exparray.get(i-1));
				if (isExtOp(item.charAt(0))) {
					int extOpIdx = getExtOpIdx(item.charAt(0));
					result = extOpFn.get(extOpIdx).apply(new Double[] { left, right });
				} else {
					boolean add = item.charAt(0) == '+';
					right *= add ? 1 : -1;
					result = left+right;
				}
				exparray.set(++i, ""+result);
			}
		}
		return Double.valueOf(exparray.get(exparray.size()-1));
	}
	
	public final boolean isOperator(char c) {
		return memberOfSet(c, operators) || memberOfSet(c, extOps);
	}
	
	public final boolean isBracket(char c) {
		return c == '(' || c == ')';
	}
	
	public final boolean isDistributive(char c) {
		return memberOfSet(c, oood);
	}
	
	public final boolean isAssociative(char c) {
		return memberOfSet(c, oooa);
	}
	
	public final boolean isFunction(String def) {
		return memberOfSet(def, stdFn) || memberOfSet(def, extFnDefs);
	}
	
	public final boolean isConstant(String def) {
		return memberOfSet(def, stdConst) || memberOfSet(def, extConstDefs);
	}
	
	private final boolean memberOfSet(char c, char[] set) {
		for (int i = 0; i < set.length; i++) 
			if (c == set[i])
				return true;
		return false;
	}
	
	private final boolean memberOfSet(String s, List<String> set) {
		for (int i = 0; i < set.size(); i++) 
			if (s.equals(set.get(i)))
				return true;
		return false;
	}
	
	private final boolean memberOfSet(String s, String[] set) {
		for (int i = 0; i < set.length; i++) 
			if (s.equals(set[i]))
				return true;
		return false;
	}
	
	public final boolean addExtConst(String fn, Supplier<Double> fx) {
		if (extConstDefs.add(fn))
			return extConsts.add(fx);
		
		return false;
	}

	public final boolean isExtConst(String fn) {
		return extConstDefs.contains(fn);
	}
	
	public final String getRegexOpString() {
		return regexOps.substring(0);
	}
	
	public final Double evalExtConst(String fn) {
		return extConsts.get(extConstDefs.indexOf(fn)).get();
	}
	
	public final boolean addExtFn(String fn, Function<Double[], Double> fx) {
		if (extFnDefs.add(fn))
			return extFn.add(fx);

		return false;
	}

	public final boolean isExtFn(String fn) {
		return extFnDefs.contains(fn);
	}

	private String translateConstant(String constant) {
		switch (constant) {
			case "e" : return "" + Math.E;
			case "pi" : return "" + Math.PI;
			case "phi" : return "" + (1+Math.sqrt(5))/2;
		}
		
		for (String s : extConstDefs) {
			if (s.equals(constant)) 
				return extConsts.get(extConstDefs.indexOf(s)).get().toString();
		}
		return constant;
	}
	
	public final boolean addExtOp(String op, Function<Double[], Double> fn) {
		boolean isOp = op.length() == 1 && isOperator(op.charAt(0));
		boolean isExtOp = extOpDefs.contains(op);
		
		if (!isOp && !isExtOp) {
			char code = (char)(128 + extOpDefs.size());
			extOpDefs.add(op);
			extOpFn.add(fn);
			
			char[] newMap = new char[extOps.length+1];
			for (int i = 0; i < extOps.length; i++) 
				newMap[i] = extOps[i];
			newMap[extOps.length] = code;
			extOps = newMap;
			
			String regexOps2 = new String();
			for (int i = 0; i < operators.length; i++) {
				regexOps2 += "\\" + operators[i] + "|";
			}
			for (int i = 0; i < extOps.length; i++) {
				regexOps2 += "\\" + extOps[i] + "|";
			}
			regexOps = regexOps2.substring(0, regexOps2.length()-1);
			return true;
		}
		return false;
	}
	
	public final boolean isExtOp(char c) {
		return memberOfSet(c, extOps);
	}
	
	private Integer getExtOpIdx(char c) {
		for (int i = 0; i < extOps.length; i++) 
			if (extOps[i] == c)
				return i;
		return null;
	}
	
	private Double evalExtFn(String fn, Double... args) {
		return extFn.get(extFnDefs.indexOf(fn)).apply(args);
	}
	
	private Double evalFn(String fn, Double... args) {
		switch (fn) {
			case "sqrt" : return Math.sqrt(args[0]);
			case "cbrt" : return Math.cbrt(args[0]);
			case "cos" : return Math.cos(args[0]);
			case "sin" : return Math.sin(args[0]);
			case "tan" : return Math.tan(args[0]);
			case "cot" : return 1d/Math.tan(args[0]);
			case "sec" : return 1d/Math.cos(args[0]);	
			case "csc" : return 1d/Math.sin(args[0]);
			case "acos" : return Math.acos(args[0]);
			case "asin" : return Math.asin(args[0]);
			case "atan" : return Math.atan(args[0]);
			case "acot" : return 1d/Math.atan(args[0]);
			case "asec" : return 1d/Math.acos(args[0]);	
			case "acsc" : return 1d/Math.asin(args[0]);
			case "cosh" : return Math.cosh(args[0]);
			case "sinh" : return Math.sinh(args[0]);
			case "tanh" : return Math.tanh(args[0]);
			case "ln" : return Math.log(args[0]);
			case "log" : return Math.log10(args[0]);
			case "exp" : return Math.exp(args[0]);
			case "abs" : return Math.abs(args[0]);
			case "inv" : return 1d/args[0];
			case "hypo" : return Math.sqrt(Math.pow(args[0], 2) + Math.pow(args[1], 2));
			case "logx" : return Math.log(args[1])/Math.log(args[0]);
			case "mean" : return getMean(args);
			case "meanxsd" : {
				double xsd = args[0];
				return getMeanXSD(Arrays.asList(args).subList(1, args.length).toArray(new Double[] {}), xsd);
			}
			case "absmean" : return getAbsMean(args);
			case "sd" : return getStdDev(args);
			
			//MORE COMPLICATED FUNCTION DEFINITIONS
			
			//START LOGISTICS 
			case "logisfx" : {
				double k = 1;
				if (args.length == 2) 
					k = args[1];
				return 1d/(1 + Math.exp(-k*args[0]));
			}
		}
		
		if (isExtFn(fn)) return evalExtFn(fn, args);
		
		return null;
	}
	
	private Double getMean(Double[] list) {
		double sums = 0;
		for (double d : list) sums += d;
		return sums/list.length;
	}
	
	private Double getMeanXSD(Double[] list, double xsd) {
		if (list.length == 0) return null;
		if (list.length == 1) return list[0];
		
		Double mean = getMean(list);
		double sd = xsd*getStdDev(list)/2;
		
		List<Double> filtered = new ArrayList<>();
		for (int i = 0; i < list.length; i++) {
			Double val = list[i];
			if (val >= mean - sd && val <= mean + sd)
				filtered.add(val);
		}
		
		float sum = 0;
		for (Double item : filtered) sum += item;
		return (double) (sum/filtered.size());
	}
	
	private Double getAbsMean(Double[] list) {
		double sums = 0;
		for (double d : list) sums += Math.abs(d);
		return sums/list.length;
	}
	
	private Double getStdDev(Double[] list) {
		double mean = getMean(list);
		double dev = 0;
		for (int i = 0; i < list.length; i++)
			dev += Math.pow(mean - list[i], 2);
		return Math.sqrt(dev/list.length);
	}
	
	public static void main(String[] args) {
		Expressions s = new Expressions();
		Console.println("Simple expression evaluator. \n Enter q to quit.");
		
		while(true) {
	       try {
	    	   String input = Console.getInput(">");
	    	   
	    	   if (input.toLowerCase().equals("q"))
	    		   System.exit(0);
	    	   
	    	   Console.println("\t" + s.eval(input));
	       } catch (Exception e) {
	    	   Console.println("\n\t" + e.getMessage());
	       }
		}
	}
}