package org.arena.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.arena.io.Console;

public abstract interface MathEx {
	static final double phi = (1 + Math.sqrt(5))/2;
	
	static Float getSum(List<? extends Number> list) {
		list.removeIf(p -> p == null);
		Float sum = 0f;
		for (Number item : list) sum += item.floatValue();
		return sum;
	}
	
	
	static Integer sumate(int x) {
		int sum = x * (x + 1);
		return sum/2;
	}
	
	
	static Float sumate(float x) {
		float sum = x * (x + 1);
		return sum/2;
	}
	
	
	static Long factorial(int x) {
		long fact = 1;
		for (int i = 1; i <= x; i++) fact*=i;
		return fact;
	}
	
	
	static Long factorial(float x) {
		long f = 1;
		for (int i = 1; i <= x; i++) f *= i;
		return f;
	}
	
	
	static Float pow(int x, int exp) {
		return (float) Math.pow(x, exp);
	}
	
	
	static Float pow(int x, float exp) {
		return (float) Math.pow(x, exp);
	}
	
	
	static Float pow(float x, int exp) {
		return (float) Math.pow(x, exp);
	}
	
	
	static Float pow(float x, float exp) {
		return (float) Math.pow(x, exp);
	}
	
	
	static Float sqrt(float x) {
		return (float) (sign(x) * Math.sqrt(Math.abs(x)));
	}
	
	
	static Float cbrt(float x) {
		return (float) (sign(x) * Math.cbrt(Math.abs(x)));
	}
	
	
	static Float e(float x) {
		return (float) Math.pow(Math.E, x);
	}
	
	
	static Float sige(float x) {
		return (float) (sign(x) * Math.pow(Math.E, Math.abs(x)));
	}
	
	
	static int sign(float x) {
		return (int) (Math.abs(x)/x);
	}
	
	
	static int sign(double x) {
		return (int) (Math.abs(x)/x);
	}
	
	
	static int sign(int x) {
		return Math.abs(x)/x;
	}
	
	
	static int sign(long x) {
		return (int) (Math.abs(x)/x);
	}
	
	
	static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	
	static Integer charToInt(char c) {
		try {
			Integer aInt = Integer.valueOf(""+c);
			return aInt > -1 && aInt < 10 ? aInt : null;
		} catch (Exception e) {	return null; }
	}
	
	
	static Integer extractInt(String s, int offset) {
		String val = "";
		for (int j = offset; j < s.length(); j++) {
			Integer i = charToInt(s.charAt(j));
			if (i == null) break;
			else val += s.charAt(j);
		}
		return Integer.valueOf(val);
	}
	
	
	static Integer extractInt(String s) {
		return extractInt(s, 0);
	}
	
	
	static Integer extractInt(String s, String offset) {
		return extractInt(s, s.indexOf(offset)+offset.length());
	}
	
	
	static List<Integer> extractIntList(String s, int offset) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		String val = "";
		
		for (int j = offset; j < s.length(); j++) {
			Integer i = charToInt(s.charAt(j));
			if (i == null) {
				try {
					list.add(Integer.valueOf(val));
				} catch (Exception e) {}
				val = "";
			} else val += s.charAt(j);
		}
		if (!val.isEmpty()) list.add(Integer.valueOf(val));
		return list;
	}
	
	
	static List<Integer> extractIntList(String s) {
		return extractIntList(s, 0);
	}
	
	
	static List<Integer> extractIntList(String s, String offset) {
		return extractIntList(s, s.indexOf(offset)+offset.length());
	}
	
	
	static Float extractFloat(String s, int offset) {
		String val = "";
		for (int j = offset; j < s.length(); j++) {
			Integer i = charToInt(s.charAt(j));
			if (i == null && s.charAt(j) != '.') break;
			else val += s.charAt(j);
		}
		return Float.valueOf(val);
	}
	
	
	static Float extractFloat(String s) {
		return extractFloat(s, 0);
	}
	
	
	static Float extractFloat(String s, String offset) {
		return extractFloat(s, s.indexOf(offset)+offset.length());
	}
	
	
	static List<Float> extractFloatList(String s, int offset) {
		ArrayList<Float> list = new ArrayList<Float>();
		String val = "";
		
		for (int j = offset; j < s.length(); j++) {
			Integer i = charToInt(s.charAt(j));
			if (i == null && s.charAt(j) != '.') {
				try {
					list.add(Float.valueOf(val));
				} catch (Exception e) {}
				val = "";
			} else val += s.charAt(j);
		}
		if (!val.isEmpty()) list.add(Float.valueOf(val));
		return list;
	}
	
	
	static List<Float> extractFloatList(String s) {
		return extractFloatList(s, 0);
	}
	
	
	static List<Float> extractFloatList(String s, String offset) {
		return extractFloatList(s, s.indexOf(offset)+offset.length());
	}

	
	static Float[] linInterpLinear(Float[]... rows) {
		List<Float[]> matrix = new ArrayList<>();
		for (Float[] row : rows) {
			matrix.add(row);
		}
		
		int col = 0;
		for (int i = 0; i < matrix.size(); i++) {
			Float[] workRow = matrix.get(i);
			for (int k = 0; k < matrix.size(); k++) {
				if (i!=k) {
					Float leading = matrix.get(k)[col];
					Float co = leading/workRow[col];
					for (int m = col; m < matrix.get(i).length; m++) {
						matrix.get(k)[m] = matrix.get(k)[m] - co*workRow[m];
					}
				}
			}
			col++;
		}
		
		col = 1;
		for (int i = 1; i < matrix.size(); i++) {
			float op = matrix.get(i)[col];
			for (int k = col; k < matrix.get(i).length; k++) {
				matrix.get(i)[k] = matrix.get(i)[k]/op;
			}
			col++;
		}
		
		Float[] val =  new Float[matrix.size()];
		for (int i = 0; i < matrix.size(); i++) {
			val[i] = matrix.get(i)[matrix.get(i).length-1];
		}
		return val;
	}
	
	
	static Float[] linInterpPoly(Number[]... orderedPairs) {
		List<Float[]> pairs = new ArrayList<>();
		for (Number[] n : orderedPairs) {
			pairs.add(new Float[] { n[0].floatValue(), n[1].floatValue() });
		}
		pairs.sort(Statistics.orderedComp);
		
		List<Float[]> matrix = new ArrayList<>();
		for (Float[] pair : pairs) {
			Float[] row = new Float[pairs.size()+1];
			row[0] = 1.0f;
			for (int i = 1; i < row.length-1; i++) {
				row[i] = (float) Math.pow(pair[0], (double)i);
			}
			row[row.length-1] = pair[1];
			matrix.add(row);
		}
		
		int col = 0;
		for (int i = 0; i < matrix.size(); i++) {
			Float[] workRow = matrix.get(i);
			for (int k = 0; k < matrix.size(); k++) {
				if (i!=k) {
					Float leading = matrix.get(k)[col];
					Float co = leading/workRow[col];
					for (int m = col; m < matrix.get(i).length; m++) {
						matrix.get(k)[m] = matrix.get(k)[m] - co*workRow[m];
					}
				}
			}
			col++;
		}
		
		col = 1;
		for (int i = 1; i < matrix.size(); i++) {
			float op = matrix.get(i)[col];
			for (int k = col; k < matrix.get(i).length; k++) {
				matrix.get(i)[k] = matrix.get(i)[k]/op;
			}
			col++;
		}
		
		Float[] val =  new Float[matrix.size()];
		for (int i = 0; i < matrix.size(); i++) {
			val[i] = matrix.get(i)[matrix.get(i).length-1];
		}
		return val;
	}
	
	
	static Double[][] mkIdentMatrix(int size) {
		Double[][] I = new Double[size][size];
		
		for (int i = 0; i < I.length; i++) {
			for (int j = 0; j < I[i].length; j++) {
				if (i == j) I[i][j] = 1d;
				else I[i][j] = 0d;
			}
		}
		
		return I;
	}
	
	
	static Double[] initMatrix(Double[] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = Double.valueOf(0);
		}
		return matrix;
	}
	
	
	static Double[][] initMatrix(Double[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = 0d;
			}
		}
		return matrix;
	}
	
	
	static Double[][][] initMatrix(Double[][][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			initMatrix(matrix[i]);
		}
		return matrix;
	}
	
	
	static Double[][] invertMatrix(Double[][] invert) {
		if (invert.length != invert[0].length) return null;
		//create an identity matrix
		Double[][] I = mkIdentMatrix(invert.length);
		
		//create a copy
		Double[][] matrix = cpMatrix(invert);
		
		for (int i = 0; i < matrix.length; i++) {
			//scan for nonzero's in i-th column
			Double leading = null;
			Integer leadingRow = null;
			for (int r = i; r < matrix.length; r++) {
				if (matrix[r][i] != 0) {
					leading = matrix[r][i];
					leadingRow = r;
					break;
				}
			}
			
			if (leading != null) {
				//divide row by leading value and do same to corresponding Identity Matrix
				for (int c = 0; c < matrix.length; c++) {
					matrix[leadingRow.intValue()][c] = matrix[leadingRow.intValue()][c]/leading; 
					I[leadingRow.intValue()][c] = I[leadingRow.intValue()][c]/leading;
				}
				
				//there can be only ONE!
				for (int r = 0; r < matrix.length; r++) {
					if (r != leadingRow.intValue() && matrix[r][i] != 0) {
						Double subject = Double.valueOf(matrix[r][i]);
						for (int c = 0; c < matrix[r].length; c++) {
							matrix[r][c] = matrix[r][c] - matrix[leadingRow.intValue()][c]*subject;  
							I[r][c] = I[r][c] - I[leadingRow.intValue()][c]*subject;  
						}
					}
				}
			}
		}
		return I;
	}
	
	
	static Double[][] getMatrixProduct(Double[][] left, Double[][] right) {
		if (left[0].length != right.length) {
			System.out.println("Inappropriate Matrices sizes: Left[0].length: " 
						+ left[0].length + " Right.length: " + right.length);
			return null;
		}
		
		Double[][] product = new Double[left.length][right[0].length];
		initMatrix(product);
		
		for (int pi = 0; pi < product.length; pi++) {
			for (int pj = 0; pj < product[pi].length; pj++) {
				product[pi][pj] = getDotProduct(left, pi, right, pj);
			}
		}

		return product;
	}
	
	
	static Double getDotProduct(double[] a, double[] b) {
		if (a.length != b.length) {
			return null;
		}
		double dp = 0;
		for (int i = 0; i < a.length; i++) {
			dp += a[i]*b[i];
		}
		return dp;
	}
	
	
	static Double getDotProduct(Double[] a, Double[] b) {
		if (a.length != b.length) {
			return null;
		}
		double dp = 0;
		for (int i = 0; i < a.length; i++) {
			dp += a[i]*b[i];
		}
		return dp;
	}
	
	
	static Double getDotProduct(Double[][] left, int ix, Double[][] right, int jx) {
		Double dotp = 0d;
		
		for (int i = 0; i < left[ix].length; i++) {
			dotp += left[ix][i]*right[i][jx];
		}
		
		return dotp;
	}
	
	
	static Double getLength(Double[] vector) {
		double v = 0d;
		for (Double d : vector) v += d*d;
		return Math.sqrt(v);
	}
	
	
	static Double getLength(double[] vector) {
		double v = 0d;
		for (Double d : vector) v += d*d;
		return Math.sqrt(v);
	}
	
	
	static Double[][] cpMatrix(Double[][] copy) {
		Double[][] matrix = new Double[copy.length][copy.length];
		
		for (int i = 0; i < copy.length; i++) {
			for (int j = 0; j < copy[i].length; j++) {
				matrix[i][j] = Double.valueOf(copy[i][j]);
			}
		}
		return matrix;
	}
	
	
	static Double[] getMatrixColumnProduct(Double[][] b, Double[] column) {
		if (b[0].length != column.length) return null;
		
		Double[] product = new Double[b.length];
		initMatrix(product);
		for (int r = 0; r < b.length; r++) {
			for (int c = 0; c < b[r].length; c++) {	
				product[r] += b[r][c]*column[c];
			}	
		}
		
		//polish
		for (int i = 0; i < product.length; i++) {
			if (Math.abs(product[i]) < .0000001) product[i] = 0d;
		}
		return product;
	}
	
	
	static void printMatrix(Number[][] matrix) {
		//calculate column widths
		List<Integer> widths = new ArrayList<>();
		
		for (int c = 0; c < matrix[0].length; c++) {
			int width = 0;
			for (int r = 0; r < matrix.length; r++) {
				int w = (""+matrix[r][c]).length() + 2;
				if (w > width) width = w;
			}
			widths.add(width);
		}
		
		String format = "";
		for (Integer width : widths) 
			format += "%-" + width + "s ";
		format += "%n";

		for (int i = 0; i < matrix.length; i++) {
			Console.printf(format, matrix[i]);
		}
	}
	
	
	static void printMatrix(Number[][] matrix, int places) {
		//calculate column widths
		List<Integer> widths = new ArrayList<>();
		
		Double[][] newMatrix = new Double[matrix.length][matrix[0].length];
		
		for (int c = 0; c < matrix[0].length; c++) {
			int width = 0;
			for (int r = 0; r < matrix.length; r++) {
				Double rounded = round(matrix[r][c].doubleValue(), places);
				newMatrix[r][c] = rounded;
				int w = (""+rounded).length() + 2 + places;
				if (w > width) width = w;
			}
			widths.add(width);
		}
		
		String format = "";
		for (Integer width : widths) 
			format += "%-" + width + "." + places + "f ";
		format += "%n";
		
		for (int i = 0; i < newMatrix.length; i++) {
			Console.printf(format, newMatrix[i]);
		}
	}
	
	
	static <N extends Number>  boolean listContainsValue(List<N> list, N val) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == val) return true;
		}
		return false;
	}

	
	static Double[][] addMatrices(Double[][] m2, Double[][] matrix) {
		Double[][] sum = new Double[m2.length][matrix[1].length];
		
		for (int i = 0; i < m2.length; i++) {
			for (int j = 0; j < m2[i].length; j++) {
				sum[i][j] = Double.valueOf(m2[i][j]+matrix[i][j]);
			}
		}
		return sum;
	}
	
	
	static Double[][] transpose(Double[][] matrix) {
		Double[][] t = new Double[matrix[0].length][matrix.length];
		initMatrix(t);
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				t[j][i] = matrix[i][j];
			}
		}
		return t;
	}
}
