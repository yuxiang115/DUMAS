package datastructure;

import java.math.BigDecimal;

public class ScoreMatrix {
	private Double[][] scoreMatrix;
	private int sourceLength;
	private int targetLength;

	public ScoreMatrix(int sourceLength, int targetLength){
		this.sourceLength = sourceLength;
		this.targetLength = targetLength;
		this.scoreMatrix = new Double[sourceLength][targetLength];
		for (int i = 0; i < sourceLength; i++) {
			for (int j = 0; j < targetLength; j++) {
				this.scoreMatrix[i][j] = null;
			}
		}
	}

	public void setScore(int sourceColumn, int targetColumn, double score){
		this.scoreMatrix[(sourceColumn - 1)][(targetColumn - 1)] = new Double(score);

	}

	public void setScore(int sourceColumn, int targetColumn, Double score){
		this.scoreMatrix[(sourceColumn - 1)][(targetColumn - 1)] = score;
	}

	public double getScoreValue(int sourceColumn, int targetColumn){
		Double score = getScore(sourceColumn, targetColumn);
		if (score == null) {
			return 0.0D;
		}
		return score.doubleValue();
	}

	public Double getScore(int sourceColumn, int targetColumn){
		return this.scoreMatrix[(sourceColumn - 1)][(targetColumn - 1)];
	}

	public int getSourceLength(){
		return this.sourceLength;
	}

	public int getTargetLength(){
		return this.targetLength;
	}

	public String toString(){
		StringBuffer result = new StringBuffer(6 * this.scoreMatrix[0].length * 
				this.scoreMatrix.length);
		for (int sourcePos = 1; sourcePos <= this.sourceLength; sourcePos++)
		{
			for (int targetPos = 1; targetPos <= this.targetLength; targetPos++)
			{
				BigDecimal score = new BigDecimal(getScoreValue(sourcePos, targetPos));
				score = score.setScale(2, 4);
				result.append(score);
				result.append(" ");
			}
			result.append("\n");
		}
		return result.toString();
	}
}
