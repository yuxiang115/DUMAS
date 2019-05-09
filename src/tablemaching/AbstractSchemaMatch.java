package tablemaching;

import datastructure.Alignment;
import datastructure.ScoreMatrix;

public abstract class AbstractSchemaMatch implements SchemaMatch{
	private boolean checkCertainty = false;
	public static final GraphMatching STANDARD_MATCHING = new HungarianMethod();
	private GraphMatching matcher;

	protected AbstractSchemaMatch(){
		this(STANDARD_MATCHING);
	}

	protected AbstractSchemaMatch(GraphMatching matcher){
		this.matcher = matcher;
	}

	/**
	 * Get Alignment of two tables that satify threshold
	 * @param matrix
	 * 		ScoreMatrix
	 * @param threshold
	 * 		double
	 * @return Alignment
	 * **/
	protected Alignment align(ScoreMatrix matrix, double threshold){
		Alignment alignment = align(matrix);
		for (int src = 1; src <= alignment.getSourceSize(); src++) {
			if (alignment.hasSourceAlignment(src)){
				int tgt = alignment.getSourceAlignment(src).intValue();
				double value = matrix.getScoreValue(src, tgt);
				if (value < threshold) {
					alignment.removeSourceAlignment(src);
				}
			}
		}
		return alignment;
	}

	/**
	 * get Alignment of matching records
	 * @param matrix
	 * 		ScoreMatrix
	 * @return Alignment
	 * */
	protected Alignment align(ScoreMatrix matrix){
		return this.matcher.match(matrix);
	}

	protected boolean getCheckCertainty(){
		return this.checkCertainty;
	}

	protected void setCheckCertainty(boolean check){
		this.checkCertainty = check;
	}

	protected GraphMatching getGraphMatcher(){
		return this.matcher;
	}
}
