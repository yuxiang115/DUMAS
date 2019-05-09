package datastructure;

import dumasException.DumasException;

public class GroupAlignment extends Alignment{
	private ColSet[] sourceColumns = null;
	private ColSet[] targetColumns = null;

	public GroupAlignment(ColSet[] srcCols, ColSet[] tgtCols){
		super(srcCols.length, tgtCols.length);
		this.sourceColumns = srcCols;
		this.targetColumns = tgtCols;
	}

	public String toString(){
		StringBuffer result = new StringBuffer(100);
		for (int i = 1; i <= getSourceSize(); i++) {
			if (hasSourceAlignment(i))
			{
				result.append(this.sourceColumns[(i - 1)]);
				result.append("->");
				int tgt = getSourceAlignment(i).intValue();
				result.append(this.targetColumns[(tgt - 1)]);
				result.append(" ");
			}
		}
		result.append(";\n Unmatched source:");
		for (int i = 1; i <= getSourceSize(); i++) {
			if (!hasSourceAlignment(i))
			{
				result.append(" ");
				result.append(this.sourceColumns[(i - 1)]);
			}
		}
		result.append(";\n Unmatched target:");
		for (int i = 1; i <= getTargetSize(); i++) {
			if (!hasTargetAlignment(i))
			{
				result.append(" ");
				result.append(this.targetColumns[(i - 1)]);
			}
		}
		return result.toString();
	}

	public boolean isComplex(){
		return true;
	}

	public void useAlignment(Alignment align){
		if (align.getSourceSize() != getSourceSize()) {
			throw new DumasException("Incompatible source size.");
		}
		if (align.getTargetSize() != getTargetSize()) {
			throw new DumasException("Incompatible target size.");
		}

		for (int i = 1; i <= getSourceSize(); i++) {
			if (align.hasSourceAlignment(i))
			{
				Integer target = align.getSourceAlignment(i);
				double score = align.getScore(i);
				addAlignment(i, target.intValue(), score);
			}
			else
			{
				removeSourceAlignment(i);
			}
		}
	}
}
