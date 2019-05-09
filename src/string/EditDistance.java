package string;

import java.util.Iterator;

public class EditDistance implements StringDistance{
	public static final int BELOW_THRESHOLD = -1;
	private double minSim = 0.0D;
	
	public EditDistance(){}
	
	public double score(String sourceString, String targetString, double minSimilarity){
        int minMatch;
        int maxDist = Math.max(sourceString.length(), targetString.length());
        int distThreshold = maxDist - (minMatch = new Double(Math.ceil(minSimilarity * (double)maxDist)).intValue());
        int editDistance = this.score(sourceString, targetString, distThreshold);
        if (editDistance == -1) {
            return 0.0;
        }
        return 1.0 - (double)editDistance / (double)maxDist;
	}
	
	private int score(String sourceString, String targetString, int distThreshold){
		int lengthDiff = Math.abs(sourceString.length() - targetString.length());
		if(lengthDiff > distThreshold) return -1;
		
		char[] src = sourceString.toCharArray();
		char[] tgt = targetString.toCharArray();
		
		int[][] distanceMatrix = new int[src.length + 1][tgt.length + 1];
		boolean aboveThreshold = false;
		
		for(int i = 0; i <= src.length; i++){
			distanceMatrix[i][0] = i;
		}
		
		for(int j = 1; (!aboveThreshold) && (j <= tgt.length); j++){
			distanceMatrix[0][j] = j;
			int minDistance = distThreshold + 1;
			
			for(int i = 1; i <= src.length; i++){
				char srcChar = Character.toLowerCase(src[(i - 1)]);
				char tgtChar = Character.toLowerCase(tgt[(j - 1)]);
				int match = srcChar == tgtChar ? 0 : 1;
				int score = min3(distanceMatrix[(i - 1)][j] + 1,
						distanceMatrix[i][(j - 1)] + 1,
						distanceMatrix[(i - 1)][(j - 1)] + match);
				distanceMatrix[i][j] = score;
				if(score < minDistance){
					minDistance = score;
				}
			}
			aboveThreshold = minDistance > distThreshold;
		}
		
		int finalScore = distanceMatrix[src.length][tgt.length];
		if((aboveThreshold) || (finalScore > distThreshold)) {
			return -1;
		}
		return finalScore;	
	}
	
	private int min3(int x, int y, int z){
		return Math.min(x, Math.min(y, z));
	}

	@Override
	public double score(StringWrapper src, StringWrapper tgt) {
		return score(src.unwrap(), tgt.unwrap());
	}

	@Override
	public double score(String src, String tgt) {
		return score(src, tgt, minSim);
	}

	@Override
	public StringWrapper prepare(String str) {
		return new StringWrapper(str);
	}

	@Override
	public StringWrapper prepare(String[] s) {
		return prepare(StringOperations.concatenate(s));
	}

	@Override
	public String explainScore(StringWrapper str1, StringWrapper str2) {
		return Double.toString(score(str1, str2));
	}

	@Override
	public void accumulateStringArrayStatistics(Iterator paramIterator) {}

	@Override
	public DocumentFrequency getStatistics() {
		return null;
	}
	
}

