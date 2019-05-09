package datastructure;

import java.math.BigDecimal;
import java.util.ArrayList;

import dumasException.DumasException;

public class ComplexScoreMatrix extends ScoreMatrix{
	private ColSet[] srcColSets = null;
	private ColSet[] tgtColSets = null;

	public ComplexScoreMatrix(ColSet[] srcColSets, ColSet[] tgtColSets){
		super(srcColSets.length, tgtColSets.length);
		this.srcColSets = srcColSets;
		this.tgtColSets = tgtColSets;
	}
	/**
	 * get all source columnSet[] (records)
	 *
	 * @return source 
	 * 				columnSet[]
	 * */
	public ColSet[] getSrcColSets(){
		return this.srcColSets;
	}
	/**
	 * get source columnSet (record)
	 * @param postion 
	 * 				int
	 * @return source 
	 * 				columnSet
	 * */
	public ColSet getSrcColumns(int src){
		return this.srcColSets[(src - 1)];
	}
	/**
	 * get all target columnSet[] (records)
	 *
	 * @return target 
	 * 				columnSet[]
	 * */
	public ColSet[] getTgtColSets(){
		return this.tgtColSets;
	}
	/**
	 * get target columnSet (record)
	 * @param postion 
	 * 				int
	 * @return target 
	 * 				columnSet
	 * */
	public ColSet getTgtColumns(int tgt){
		return this.tgtColSets[(tgt - 1)];
	}
	/**
	 * check if has source columnSet (record)
	 * @param source columnsSet (Record)
	 * @return position int
	 * **/
	public int getSrcIndex(ColSet cols){
		for (int i = 0; i < this.srcColSets.length; i++) {
			if (this.srcColSets[i].equals(cols)) {
				return i + 1;
			}
		}
		return -1;
	}
	/**
	 * check if has source columnSet (record)
	 * @param source columnsSet (Record)
	 * @return boolean
	 * **/
	public boolean hasSrcColumns(ColSet cols){
		return getSrcIndex(cols) > 0;
	}

	/**
	 * check if has target columnSet (record)
	 * @param target columnsSet (Record)
	 * @return position int
	 * **/
	public int getTgtIndex(ColSet cols){
		for (int i = 0; i < this.tgtColSets.length; i++) {
			if (this.tgtColSets[i].equals(cols)) {
				return i + 1;
			}
		}
		return -1;
	}

	/**
	 * check if has target columnSet (record)
	 * @param target columnsSet (Record)
	 * @return boolean
	 * **/
	public boolean hasTgtColumns(ColSet cols){
		return getTgtIndex(cols) > 0;
	}
	/**
	 * Set two records Similar score
	 * @param source columnsSet (Record)
	 * @param target ColumnsSet (Record)
	 * **/
	public void setScore(ColSet srcCols, ColSet tgtCols, double score){
		int srcIndex = getSrcIndex(srcCols);
		if (srcIndex == -1) {
			throw new DumasException("No attribute group for source columns: " + 
					srcCols);
		}
		int tgtIndex = getTgtIndex(tgtCols);
		if (tgtIndex == -1) {
			throw new DumasException("No attribute group for target columns: " + 
					tgtCols);
		}
		super.setScore(srcIndex, tgtIndex, score);
	}

	/**
	 * Set two records Similar score
	 * @param source columnsSet (Record)
	 * @param target ColumnsSet (Record)
	 * **/
	public Double getScore(ColSet srcCols, ColSet tgtCols){
		int srcIndex = getSrcIndex(srcCols);
		if (srcIndex == -1) {
			throw new DumasException("No attribute group for source columns: " + 
					srcCols);
		}
		int tgtIndex = getTgtIndex(tgtCols);
		if (tgtIndex == -1) {
			throw new DumasException("No attribute group for target columns: " + 
					tgtCols);
		}
		return super.getScore(srcIndex, tgtIndex);
	}

	public ComplexScoreMatrix mergeSourceColumns(ColSet src1, ColSet src2){
		return mergeColumns(src1, src2, false);
	}

	public ComplexScoreMatrix mergeTargetColumns(ColSet tgt1, ColSet tgt2){
		return mergeColumns(tgt1, tgt2, true);
	}

	private ComplexScoreMatrix mergeColumns(ColSet col1, ColSet col2, boolean isTarget){
		boolean found1 = false;
		boolean found2 = false;
		ArrayList newColSets = new ArrayList();

		ColSet[] colSets = (ColSet[])null;
		if (isTarget) {
			colSets = this.tgtColSets;
		} else {
			colSets = this.srcColSets;
		}
		for (int i = 0; i < colSets.length; i++){
			ColSet curSet = colSets[i];
			if (curSet.equals(col1)){
				if (found1) {
					throw new DumasException("First column set found twice.");
				}
				found1 = true;
			}
			else if (curSet.equals(col2)){
				if (found2) {
					throw new DumasException("Second column set found twice.");
				}
				found2 = true;
			}
			else{
				newColSets.add(curSet);
			}
		}
		if (!found1) {
			throw new DumasException("First column set not found.");
		}
		if (!found2) {
			throw new DumasException("Second column set not found");
		}
		ColSet newSet = ColSet.merge(col1, col2);
		newColSets.add(newSet);

		ColSet[] newColsArray = (ColSet[])newColSets.toArray(new ColSet[newColSets.size()]);
		ComplexScoreMatrix newMatrix = null;
		if (isTarget) {
			newMatrix = new ComplexScoreMatrix(this.srcColSets, newColsArray);
		} else {
			newMatrix = new ComplexScoreMatrix(newColsArray, this.tgtColSets);
		}
		int newSrcSize = newMatrix.getSourceLength();
		int newTgtSize = newMatrix.getTargetLength();
		for (int i = 1; i <= newSrcSize; i++)
		{
			ColSet curSrcSet = newMatrix.getSrcColumns(i);
			if ((isTarget) || (!curSrcSet.equals(newSet))) {
				for (int j = 1; j <= newTgtSize; j++)
				{
					ColSet curTgtSet = newMatrix.getTgtColumns(j);
					if ((!isTarget) || (!curTgtSet.equals(newSet))) {
						newMatrix.setScore(i, j, getScore(curSrcSet, curTgtSet));
					}
				}
			}
		}
		return newMatrix;
	}

	public String toString(){
		StringBuffer line = new StringBuffer(200 * getSourceLength());
		int srcSize = getSourceLength();
		int tgtSize = getTargetLength();
		for (int tgtInd = 0; tgtInd < tgtSize; tgtInd++)
		{
			line.append("  ");
			line.append(this.tgtColSets[tgtInd].toString());
		}
		line.append("\n");
		for (int i = 1; i <= srcSize; i++)
		{
			line.append(getSrcColumns(i).toString());
			line.append(" ");
			for (int j = 1; j <= getTargetLength(); j++)
			{
				BigDecimal score = new BigDecimal(getScoreValue(i, j));
				score = score.setScale(2, 4);
				line.append(score);
				line.append(" ");
			}
			line.append("\n");
		}
		return line.toString();
	}

	public String toString(Alignment align){
		StringBuffer line = new StringBuffer(200 * getSourceLength());
		int srcSize = getSourceLength();
		int tgtSize = getTargetLength();
		for (int tgtInd = 0; tgtInd < tgtSize; tgtInd++)
		{
			line.append("  ");
			line.append(this.tgtColSets[tgtInd].toString());
		}
		line.append("\n");
		for (int i = 1; i <= srcSize; i++){
			int corPos = 0;
			if ((align != null) && (align.hasSourceAlignment(i))) {
				corPos = align.getSourceAlignment(i).intValue();
			}
			line.append(getSrcColumns(i).toString());
			line.append(" ");
			for (int j = 1; j <= getTargetLength(); j++)
			{
				if (corPos == j) {
					line.append("(");
				}
				BigDecimal score = new BigDecimal(getScoreValue(i, j));
				score = score.setScale(2, 4);
				line.append(score);
				if (corPos == j) {
					line.append(")");
				}
				line.append(" ");
			}
			line.append("\n");
		}
		return line.toString();
	}

	public boolean equals(Object obj){
		if ((obj instanceof ComplexScoreMatrix)){
			ComplexScoreMatrix matrix = (ComplexScoreMatrix)obj;
			return (sameDimension(getSrcColSets(), matrix.getSrcColSets())) && (sameDimension(getTgtColSets(), matrix.getTgtColSets()));
		}
		return false;
	}

	private boolean sameDimension(ColSet[] set1, ColSet[] set2){
		int length = set1.length;
		if (set2.length != length) {
			return false;
		}
		for (int i = 0; i < length; i++)
		{
			ColSet colSet1 = set1[i];
			boolean found = false;
			for (int j = 0; (j < length) && (!found); j++) {
				if (colSet1.equals(set2[j])) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}
}
