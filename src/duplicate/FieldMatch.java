package duplicate;

import datastructure.Alignment;
import datastructure.ScoreMatrix;
import dumasException.DumasException;
import string.StringWrapper;

public class FieldMatch {
	private StringCompare compare = null;
	
	public FieldMatch(StringCompare compare){
		this.compare = compare;
	}
	/**
	 * String edit distance to generate similar score
	 * @param source
	 * 			RecordWrapper
	 * @param target
	 * 			RecordWrapper
	 * @return ScoreMatrix
	 * */
	public ScoreMatrix compareFields(RecordWrapper source, RecordWrapper target, Alignment alignment){
		int sourceColumns = source.numValues();
		int targetColumns = target.numValues();
		
		ScoreMatrix result = new ScoreMatrix(sourceColumns, targetColumns);
		
		for (int i = 1; i <= sourceColumns; i++){
			int align = 0;
			if ((alignment != null) && (alignment.hasSourceAlignment(i))) {
				align = alignment.getSourceAlignment(i).intValue();
			}
			if (!source.isNull(i)){
				StringWrapper sourceValue = getFieldWrapper(source, i);
				for (int j = 1; j <= targetColumns; j++) {
					if (!target.isNull(j)){
						if ((align != 0) && (align != j)){
							result.setScore(i, j, new Double(0.0D));
						}
						else{
							StringWrapper targetValue = getFieldWrapper(target, j);
							double score = this.compare.score(sourceValue, targetValue);
							result.setScore(i, j, new Double(score));
						}
					}
					else{
						result.setScore(i, j, null);
					}
				}
			}
			else{
				for (int j = 1; j <= targetColumns; j++) {
					result.setScore(i, j, null);
				}
			}
		}
		return result;
	}
	
	private StringWrapper getFieldWrapper(RecordWrapper rec, int pos){
		if (!rec.hasFieldWrapper()) {
			throw new DumasException(
			        "Field wrapper not set. Please check source code.");
			    
		}
		return rec.getFieldWrapper(pos);
	}
}
