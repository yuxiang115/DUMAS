package tablemaching;

import java.math.BigDecimal;

import datastructure.Alignment;
import datastructure.ScoreMatrix;
import dumasException.DumasException;

public class HungarianMethod implements GraphMatching{
	private double[][] arrayCopy;
	private boolean debug = false;

	private static final double INF = Double.MAX_VALUE;
	private static final double ZERO = 1.0E-7D;
	private static final boolean verbose = false;
	private static final int INTINF = Integer.MAX_VALUE;

	public HungarianMethod() {}



	public Alignment match(ScoreMatrix matrix){
		int j;
		int i;
		int srcLength = matrix.getSourceLength();
		int tgtLength = matrix.getTargetLength();
		
		double maxValue = 0.0;
		int size = Math.max(srcLength, tgtLength);
		double[][] array = new double[size][size];
		for (i = 0; i < srcLength; ++i) {
			for (j = 0; j < tgtLength; ++j) {
				double score = matrix.getScoreValue(i + 1, j + 1);
				if ((score > maxValue))
					maxValue = score;
			}
		}
		for (i = 0; i < srcLength; ++i) {
			for (j = 0; j < tgtLength; ++j) {
				array[i][j] = maxValue - matrix.getScoreValue(i + 1, j + 1);
			}
		}
		if (srcLength > tgtLength) {
			for (i = 0; i < srcLength; ++i) {
				for (j = tgtLength; j < srcLength; ++j) {
					array[i][j] = maxValue + 1.0;
				}
			}
		} else if (tgtLength > srcLength) {
			for (i = srcLength; i < tgtLength; ++i) {
				for (j = 0; j < tgtLength; ++j) {
					array[i][j] = maxValue + 1.0;
				}
			}
		}

		this.copyArray(array);
		int[] mate = (new BipartiteMatching(array)).execute();
		if (this.debug) {
			this.printArray(array);
			for(int dd = 0; dd < mate.length; dd++){
				System.out.print(mate[dd] + "; ");
			}
			System.out.println("srcLength: " + srcLength);
			System.out.println("tgtLength: " + tgtLength);
		}
		
		
		Alignment alignment = new Alignment(matrix.getSourceLength(), matrix.getTargetLength());
		for (int src = 1; src <= srcLength; ++src) {
			int tgt = mate[src - 1] + 1;
			if (tgt > tgtLength) continue;
			alignment.addAlignment(src, tgt, matrix.getScoreValue(src, tgt));
		}
		return alignment;
	}

	private void copyArray(double[][] array){
		int srcLength = array.length;
		int tgtLength = array[0].length;

		this.arrayCopy = new double[srcLength][tgtLength];
		for (int i = 0; i < srcLength; i++) {
			for (int j = 0; j < tgtLength; j++) {
				this.arrayCopy[i][j] = array[i][j];
			}
		}
	}
	private void printArray(double[][] array) {
		System.out.println(HungarianMethod.arrayToString(array));
	}

	private void printArray(int[][] array) {
		System.out.println(HungarianMethod.arrayToString(array));
	}

	private static String arrayToString(int[][] array) {
		int srcLength = array.length;
		int tgtLength = array[0].length;
		StringBuffer line = new StringBuffer(300);
		for (int i = 0; i < srcLength; ++i) {
			for (int j = 0; j < tgtLength; ++j) {
				if (j > 0) {
					line.append(" ");
				}
				line.append(array[i][j]);
			}
			if (i >= srcLength - 1) continue;
			line.append("\n");
		}
		return line.toString();
	}
	
    private static String arrayToString(double[][] array) {
        int afterdecimal = 3;
        int srcLength = array.length;
        int tgtLength = array[0].length;
        StringBuffer line = new StringBuffer(300);
        for (int i = 0; i < srcLength; ++i) {
            for (int j = 0; j < tgtLength; ++j) {
                if (j > 0) {
                    line.append(" ");
                }
                BigDecimal score = new BigDecimal(array[i][j]);
                score = score.setScale(3, 4);
                line.append(score);
            }
            if (i >= srcLength - 1) continue;
            line.append("\n");
        }
        return line.toString();
    }

}