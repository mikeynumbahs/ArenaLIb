package org.arena.math;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Statistics extends MathEx {
	public final Comparator<? super Number> comp = (a,b) 
			-> Float.compare(a.floatValue(), b.floatValue());
	
	
	public final Comparator<? super Number[]> orderedComp = (a,b) 
			-> Float.compare(a[0].floatValue(), b[0].floatValue());
	
	public final Comparator<? super Number[]> orderedCompHighToLow = (a,b) 
			-> Float.compare(b[0].floatValue(), a[0].floatValue());
			
	public final Comparator<? super Double[]> arraySort = (a,b)
			-> Double.compare(a[0], b[0]);
			
	public final Predicate<? super Number[]> isInfinite = (Number[] e) 
			-> { 
				boolean isFinite = true;
				for (int i = 0; i < e.length; i++) {
					try {
						if (Double.isInfinite(e[i].doubleValue())) {
							isFinite = false;
						}
					} catch (Exception unknownEx) { isFinite = false; }
				}
				return !isFinite;
			};
			
	enum CENTRAL_TENDENCY {
		Mean, Median;
	};
	
	default Float getStdDev(List<? extends Number> list) {
		return getStdDev(list, CENTRAL_TENDENCY.Mean);
	}
	
	default Float getStdDev(List<? extends Number> list, CENTRAL_TENDENCY tendency) {
		if (list.isEmpty()) return null;
		Float ct = null; 
		switch (tendency) {
			case Median:
				ct = getMedian(list);
				break;
			default :
				ct = getMean(list);
		}
		
		float sum = 0;
		float N = list.size();
		for (Number n : list) {
			sum += Math.pow(n.floatValue() - ct, 2);
		}
		return (float) Math.sqrt(sum/N);
	}
	
	default Float getStdDev(List<? extends Number> list, Number central) {
		if (list.isEmpty()) return null;
	
		float sum = 0;
		float N = list.size();
		for (Number n : list) {
			sum += Math.pow(n.floatValue() - central.floatValue(), 2);
		}
		return (float) Math.sqrt(sum/N);
	}
	
	default Float getSampleStdDev(List<? extends Number> list, Number central) {
		if (list.isEmpty()) return null;
	
		float sum = 0;
		float N = list.size();
		for (Number n : list) {
			sum += Math.pow(n.floatValue() - central.floatValue(), 2);
		}
		return (float) Math.sqrt(sum/(N-1));
	}
	
	default float getAbsDev(List<? extends Number> list) {
		return getAbsDev(list, CENTRAL_TENDENCY.Mean);
	}
	
	default Float getAbsDev(List<? extends Number> list, CENTRAL_TENDENCY tendency) {
		float ct = 0;
		switch (tendency) {
			case Median :
				ct = getMedian(list);
				break;
			default :
				ct = getMean(list);
		}
		float N = list.size();
		float sum = 0;
		for (Number n : list) {
			sum += Math.abs(ct - n.floatValue());
		}
		return sum/N;
	}
	
	default Float getMedianAbsDev(List<? extends Number> list) {
		return getMedianAbsDev(list, CENTRAL_TENDENCY.Mean);
	}
	
	default Float getMedianAbsDev(List<? extends Number> list, CENTRAL_TENDENCY tendency) {
		float ct = 0;
		switch (tendency) {
			case Median :
				ct = getMedian(list);
				break;
			default :
				ct = getMean(list);
		}
		
		List<Float> dev = new ArrayList<Float>();
		for (Number n : list) {
			dev.add(Math.abs(ct - n.floatValue()));
		}
		return getMedian(dev);
	}
	
	default Float getMaxAbsDev(List<? extends Number> list) {
		return getMaxAbsDev(list, CENTRAL_TENDENCY.Mean);
	}
	
	default Float getMaxAbsDev(List<? extends Number> list, CENTRAL_TENDENCY tendency) {
		Float ct = null;
		switch (tendency) {
			case Median :
				ct = getMedian(list);
				break;
			default :
				ct = getMean(list);
		}
		
		List<Float> dev = new ArrayList<>();
		for (Number n : list) {
			dev.add(Math.abs(ct - n.floatValue()));
		}
		return dev.stream().max(comp).get();
	}
	
	default Float getMinAbsDev(List<? extends Number> list) {
		return getMinAbsDev(list, CENTRAL_TENDENCY.Mean);
	}
	
	default Float getMinAbsDev(List<? extends Number> list, CENTRAL_TENDENCY tendency) {
		Float ct = null;
		switch (tendency) {
			case Median :
				ct = getMedian(list);
				break;
			default :
				ct = getMean(list);
		}
		
		List<Float> dev = new ArrayList<>();
		for (Number n : list) {
			dev.add(Math.abs(ct - n.floatValue()));
		}
		return dev.stream().min(comp).get();
	}
	
	default Float getMean(List<? extends Number> list) {
		if (list.isEmpty()) return null;
		if (list.size() == 1) return list.get(0).floatValue();
		
		List<? extends Number> temp = list.stream()
										.filter(p -> p != null)
										.collect(Collectors.toList());
		float sum = 0;
		for (Number item : temp) sum += item.floatValue();
		return (float)sum/temp.size();
	}
	
	default Float getMean2SD(List<? extends Number> list) {
		return getMeanXSD(list, 2);
	}
	
	default Float getMeanXSD(List<? extends Number> list, double xsd) {
		if (list.isEmpty()) return null;
		if (list.size() == 1) return list.get(0).floatValue();
		
		Float mean = getMean(list);
		double sd = xsd*getStdDev(list)/2;
		System.out.println("M: " + mean + " SD: "+ sd);
		List<? extends Number> temp = list.stream()
										.filter(p -> p != null && p.doubleValue() <= mean+sd && p.doubleValue() >= mean-sd)
										.collect(Collectors.toList());
		float sum = 0;
		for (Number item : temp) sum += item.floatValue();
		return (float)sum/temp.size();
	}
	
	default Float getAbsMean(List<? extends Number> list) {
		if (list.isEmpty()) return null;
		if (list.size() == 1) return  Math.abs(list.get(0).floatValue());
		
		List<? extends Number> temp = list.stream()
										.filter(p -> p != null)
										.collect(Collectors.toList());
		float sum = 0;
		for (Number item : temp) sum += Math.abs(item.doubleValue());
		return (float)sum/temp.size();
	}
	
	default Float getMedian(List<? extends Number> list) {
		if (list.size() == 1) return list.get(0).floatValue();
	
		List<? extends Number> temp = list.stream()
			.filter(p -> p != null)
			.sorted((a, b) -> Float.valueOf(a.floatValue()).compareTo(b.floatValue()))
			.collect(Collectors.toList());
		
		int index = temp.size()/2;
		if (temp.size()%2 == 0) {
			return (temp.get(index).floatValue() + temp.get(--index).floatValue())/2.0f;
		} else return temp.get(index).floatValue();
	}
	
	default Float getAbsMedian(List<? extends Number> list) {
		if (list.size() == 1) return Math.abs(list.get(0).floatValue());
	
		List<? extends Number> temp = list.stream()
			.filter(p -> p != null)
			.sorted((a, b) -> Float.valueOf(Math.abs(a.floatValue())).compareTo(Math.abs(b.floatValue())))
			.collect(Collectors.toList());
		
		int index = temp.size()/2;
		if (temp.size()%2 == 0) {
			return (Math.abs(temp.get(index).floatValue()) + Math.abs(temp.get(--index).floatValue()))/2.0f;
		} else return temp.get(index).floatValue();
	}
	
	default List<Float> getMode(List<? extends Number> list) {
		List<? extends Number> temp = list.stream()
						.filter(p -> p != null)
						.sorted((a, b) -> Float.valueOf(a.floatValue()).compareTo(b.floatValue()))
						.collect(Collectors.toList());
		

		List<Float[]> collection = new ArrayList<Float[]>();
		
		List<Float> builder = new ArrayList<Float>();
		
		for (Number f : temp) {
			if (builder.isEmpty()) builder.add(f.floatValue());
			else {
				if (f.equals(builder.get(builder.size()-1))) builder.add(f.floatValue());
				else {
					collection.add(builder.toArray(new Float[]{}));
					builder.clear();
					builder.add(f.floatValue());
				}
			}
		}
		if (!builder.isEmpty()) collection.add(builder.toArray(new Float[]{}));
	
		collection.sort((a,b) -> Integer.valueOf(b.length).compareTo(a.length));
		int occ = collection.get(0).length;

		return collection.stream()
			.filter(e -> e.length == occ)
			.map(e -> e[0])
			.sorted((a,b) -> b.compareTo(a))
			.collect(Collectors.<Float>toList());
	}
	
	default Float getWeightedAverage(List<? extends Number> list, double init, double factor) {
		float val = 0;
		float N = 0;
		for (int i = list.size()-1; i > -1; i--) {
			val += list.get(i).doubleValue()*init;
			N += init;
			init *= factor;
		}
		return val/N;
	}
	
	default List<Double> getLSR(List<? extends Number> x, List<? extends Number> y) {
		double mX = getMean(x);
		double mY = getMean(y);
		
		double sX = getStdDev(x);
		double sY = getStdDev(y);
		
		double sums = 0;
		double sumX = 0;
		double sumY = 0;

		double asumY = 0;
		double asumX = 0;
		
		for (int i = 0; i < x.size(); i++) {
			sums += (x.get(i).doubleValue()-mX)*(y.get(i).doubleValue()-mY);
			sumX += Math.pow(x.get(i).doubleValue()-mX, 2);
			sumY += Math.pow(y.get(i).doubleValue()-mY, 2);
			asumY += y.get(i).doubleValue()-mY;
			asumX += x.get(i).doubleValue()-mX;
		}
		
		double r = (sums == 0 && (sumX == 0 || sumY == 0)) ? 
								sumX == 0 ? asumY/Math.sqrt(sumY) : asumX/Math.sqrt(sumX) 
							: sums/(Math.sqrt(sumX)*Math.sqrt(sumY)); //sXY/(sX*sY);
		double b = r*(sY/sX);
		double a = mY - (b*mX);
		
		List<Double> vals = new ArrayList<Double>();
		vals.add(a);
		vals.add(b);
		vals.add(r);
		
		List<Double> testStdDev = new ArrayList<Double>();
		List<Double> testAbsDev = new ArrayList<Double>();
		for (int i = 0; i < x.size(); i++) {
			double test = a + b*x.get(i).doubleValue();
			testStdDev.add(Math.pow(test-y.get(i).doubleValue(), 2));
			testAbsDev.add(Math.abs(test-y.get(i).doubleValue()));
		}
		vals.add(Math.sqrt(getSum(testStdDev)/testStdDev.size()));
		vals.add(getMean(testAbsDev).doubleValue());
		vals.add(Double.valueOf(x.size()));
		vals.add(getMean(x).doubleValue());
		return vals;
	}
	
	default List<Double> getLSR(List<? extends Number[]> orderedPair) {
		List<? extends Number[]> op = orderedPair;
		//Sort by x and remove if infinite
		op.removeIf(isInfinite);
		op.sort(orderedComp);
		
		//Generate serperate lists
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		for (Number[] e : op) {
			x.add(e[0].doubleValue());
			y.add(e[1].doubleValue());
		}
		
		return getLSR(x,y);
	}
	
	/**
	 * Calculates best fitting line for y values within 2 standard deviations
	 * @param orderedPair
	 * @return  List<Double> equation of line in format: (0)a + (1)slope;
	 */
	default List<Double> getLSR2SD(List<? extends Number[]> orderedPair) {
		List<Double[]> op = new ArrayList<>();
		//clone
		for (Number[] n : orderedPair) {
			op.add( new Double[] { n[0].doubleValue(), n[1].doubleValue() });
		}
		
		//Sort by x and remove if infinite
		op.removeIf(isInfinite);
		op.sort(orderedComp);
		
		//Generate separate lists
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		for (Number[] n : op) {
			x.add(n[0].doubleValue());
			y.add(n[1].doubleValue());
		}
		
		//Calculate standard deviation and mean
		double sdY = getStdDev(y);
		double mY = getMean(y);
		
		//Remove outliers
		op.removeIf((Number[] n) -> {
			if (n[1].doubleValue() > mY + sdY 
			|| n[1].doubleValue() < mY - sdY) {
				return true;
			} return false;
		});
	
		//System.out.println("Retained values between: " +(mY+sdY) + " - " + (mY - sdY));
		//Regenerate lists
		x.clear();
		y.clear();
		for (Number[] n : op) {
			x.add(n[0].doubleValue());
			y.add(n[1].doubleValue());
		}
		
		//System.out.println("Mean X: " + getMean(x));
		//System.out.println("Mean Y: " + getMean(y));
		//Pass to function
		return getLSR(x,y);
	}
	
	default List<Double> getLSR2SDInv(List<? extends Number[]> orderedPair) {
		List<Double[]> op = new ArrayList<>();
		//clone
		for (Number[] n : orderedPair) {
			op.add( new Double[] { n[0].doubleValue(), n[1].doubleValue() });
		}
				
		//Sort by x and remove if infinite
		op.removeIf(isInfinite);
		op.sort(orderedComp);
		
		//Generate separate lists
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		for (Number[] n : op) {
			x.add(n[0].doubleValue());
			y.add(n[1].doubleValue());
		}
		
		//Calculate 2 standard deviations and mean
		double sdY = getStdDev(y);
		double mY = getMean(y);
		
		//Remove outliers
		op.removeIf((Number[] n) -> {
			if (n[1].doubleValue() > mY + sdY 
			|| n[1].doubleValue() < mY - sdY) {
				return false;
			} return true;
		});
	
		//Regenerate lists
		x.clear();
		y.clear();
		for (Number[] n : op) {
			x.add(n[0].doubleValue());
			y.add(n[1].doubleValue());
		}
		
		//Pass to function
		return getLSR(x,y);
	}
	
	default List<Double> getMultiLR(List<? extends Number[]> indyAndDep) {
		List<Double> y = new ArrayList<>();
		List<Double[]> data = new ArrayList<>();
		
		for (Number[] row : indyAndDep) {
			List<Double> dataRow = new ArrayList<>();
			for (int i = 0; i < row.length-1; i++) {
				dataRow.add(row[i].doubleValue());
			}
	
			data.add(dataRow.toArray(new Double[] {}));
			Number tempY = row[row.length-1];
			y.add(tempY.doubleValue());
		}
		
		return getMultiLR(data, y);
	}
	
	default List<Double> getMultiLR(List<? extends Number[]> data, List<? extends Number> y) {
		Double[] covarVector = getCorrVector(data, y);
		Double[][] corrMatrix = getCovarMatrix(data);

		Double R2 = MathEx.getDotProduct(covarVector, MathEx.getMatrixColumnProduct(MathEx.invertMatrix(corrMatrix), covarVector));
		//R2 /= covarVector.length;
		Double R = Math.sqrt(R2);
		
		Double[][] allData = data.toArray(new Double[][] {});
		Double[] avgVals = getAvgVals(allData);
		Double[][] trans = MathEx.transpose(allData);
		Double[] allY = y.toArray(new Double[] {});
		Double[][] matrix = MathEx.getMatrixProduct(trans, allData);
		allY = MathEx.getMatrixColumnProduct(trans, allY);
		Double[] soln = MathEx.getMatrixColumnProduct(MathEx.invertMatrix(matrix), allY);
		
		List<Double> mlr = new ArrayList<>();
		for (Double d : soln) mlr.add(d);
		mlr.add(R);
		
		//test and add stdDev to mlr list
		List<Double> squares = new ArrayList<>();
		List<Double> abs = new ArrayList<>();
		for (int r = 0; r < data.size(); r++) {
			double testSoln = 0d;
			Number[] vars = data.get(r);
			for (int i = 0; i < soln.length; i++) {
				testSoln += soln[i]*vars[i].doubleValue();
			}
			squares.add(Math.pow(testSoln-y.get(r).doubleValue(), 2));
			abs.add(Math.abs(testSoln-y.get(r).doubleValue()));
		}
		
		Double sd = Math.sqrt(getSum(squares)/squares.size());
		mlr.add(sd);
		mlr.add(getMean(abs).doubleValue());
		mlr.add((double) data.size());
		for (Double avg : avgVals) mlr.add(avg);
		return mlr;
	}
	
	default List<Double> getMultiLR2SD(List<? extends Number[]> indyAndDep) {
		List<Double> y = new ArrayList<>();
		List<Double[]> data = new ArrayList<>();
		
		for (Number[] row : indyAndDep) {
			List<Double> dataRow = new ArrayList<>();
			for (int i = 0; i < row.length-1; i++) {
				dataRow.add(row[i].doubleValue());
			}
	
			data.add(dataRow.toArray(new Double[] {}));
			Number tempY = row[row.length-1];
			y.add(tempY.doubleValue());
		}
		
		return getMultiLR2SD(data, y);
	}
	

	default List<Double> getMultiLR2SD(List<? extends Number[]> data, List<? extends Number> y) {
		List<Double> means = new ArrayList<>();
		List<Double> sds = new ArrayList<>();
		
		for (int i = 0; i < data.get(0).length; i++) {
			List<Double> vals = new ArrayList<>();
			for (int j = 0; j < data.size(); j++) {
				Number[] temp = data.get(j);
				vals.add(temp[i].doubleValue());
			}
			means.add(getMean(vals).doubleValue());
			sds.add(getStdDev(vals).doubleValue());
		}
		
		Double meanY = getMean(y).doubleValue();
		Double sdY = getStdDev(y).doubleValue();
		
		for (int row = y.size()-1; row >= 0; row--) {
			Double tempY = y.get(row).doubleValue();
			if (sdY != 0) {
				if (tempY >= meanY+sdY || tempY <= meanY-sdY) {
					y.remove(row);
					data.remove(row);
					if (--row < 0) break;
				}
			}
			
			Number[] tempRow = data.get(row);
			for (int col = 0; col < tempRow.length; col++) {
				Double tempC = tempRow[col].doubleValue();
				if (sds.get(col) != 0) {
					if (tempC >= means.get(col)+sds.get(col) || tempC <= means.get(col)-sds.get(col)) {
						data.remove(row);
						y.remove(row);
						if (--row < 0) break;
						col = -1;
						tempRow = data.get(row);
					}
				}
			}
		}
		
		return getMultiLR(data, y);
	}
	
	default Double[] getCorrVector(List<? extends Number[]> data, List<? extends Number> ys) {
		Double[] c = new Double[data.get(0).length];
		// i = col selector, j = row
		for (int i = 0; i < data.get(0).length; i++) {
			List<Number[]> pairs = new ArrayList<>();
			for (int j = 0; j < data.size(); j++) {
				Number[] temp = data.get(j);
				pairs.add(new Number[] { temp[i], ys.get(j) });
			}
			c[i] = getLSR(pairs).get(2);
		}
		return c;
	}
	
	default Double[][] getCovarMatrix(List<? extends Number[]> data) {
		Double[][] covarMatrix = new Double[data.get(0).length][data.get(0).length];
		
		for (int col = 0; col < data.get(0).length; col++) {
			for (int col2 = 0; col2 < data.get(0).length; col2++ ) {
				if (col == col2) {
					covarMatrix[col][col] = 1d;
				} else {
					List<Number[]> pairs = new ArrayList<>();
					for (int row = 0; row < data.size(); row++) {
						Number[] tempRow = data.get(row);
						pairs.add(new Number[] { tempRow[col], tempRow[col2] });
					}
					covarMatrix[col][col2] = getLSR(pairs).get(2);
				}
			}
		}
		return covarMatrix;
	}
	
	default Double[] getAvgVals(Number[][] matrix) {
		List<Double> avg = new ArrayList<>();
		for (int i = 0; i < matrix[0].length; i++) {
			List<Double> temp = new ArrayList<>();
			for (int r = 0; r < matrix.length; r++) {
				temp.add(matrix[r][i].doubleValue());
			}
			avg.add(getMean(temp).doubleValue());
		}
		return avg.toArray(new Double[] {});
	}
	
	default Double getSum(List<? extends Number> list) {
		Double d = 0d;
		for (Number n : list) d += n.doubleValue();
		return d;
	}
	
	default Double getPythExpect(double FOR, double ALWD) {
		if (FOR == 0) return 0d;
		Double exp = Math.sqrt(FOR+ALWD - 1.73*Math.log(FOR+ALWD));
		Double denom = 1 + Math.pow(ALWD/FOR, exp);
		return 1/denom;
	}
	
	default Double logisFx(double arg) {
		return Math.pow(1 + Math.pow(Math.E, arg), -1);	
	}
	
	default Double logisFx(double arg, double k) {
		return Math.pow(1 + Math.pow(Math.E, k*arg), -1);	
	}
	
	default Double aLogisFx(double arg) {
		return Math.log(1d/arg - 1);
	}
	
	default List<Double[]> makeMap(double[] vector) {
		List<Double[]> list = new ArrayList<>();
		for (int i = 0; i < vector.length; i++) {
			list.add(new Double[] { vector[i], (double) i });
		}
		return list;
	}
	
	default List<Double[]> makeMap(List<Double[]> matrix, int index) {
		List<Double[]> list = new ArrayList<>();
		for (int i = 0; i < matrix.get(index).length; i++) {
			list.add(new Double[] { matrix.get(index)[i], (double) i });
		}
		return list;
	}
	
	default List<Double[]> makeAbsMap(double[] vector) {
		List<Double[]> list = new ArrayList<>();
		for (int i = 0; i < vector.length; i++) {
			list.add(new Double[] { Math.abs(vector[i]), (double) i });
		}
		return list;
	}
}
