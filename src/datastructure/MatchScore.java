package datastructure;

import db.RecordId;
import dumasException.DumasException;
import duplicate.RecordWrapper;

public class MatchScore {
	private RecordId sourceId;
	private RecordId targetId;
	private RecordWrapper sourceRec;
	private RecordWrapper targetRec;
	private double score = 0.0D;
	private ScoreMatrix scoreMatrix = null;


	public MatchScore(RecordId sourceRecordId, RecordId targetRecordId){
		if (sourceRecordId == null) {
			throw new NullPointerException("Source ID must be set.");
		}
		if (targetRecordId == null) {
			throw new NullPointerException("Target ID must be set.");
		}
		this.sourceId = sourceRecordId;
		this.targetId = targetRecordId;
	}

	public void setScore(double score){
		this.score = score;
	}

	public double getScore(){
		return this.score;
	}

	public void setMatrix(ScoreMatrix matrix){
		this.scoreMatrix = matrix;
	}


	public ScoreMatrix getMatrix(){
		return this.scoreMatrix;
	}

	public RecordId getTargetId(){
		return this.targetId;
	}

	public RecordId getSourceId(){
		return this.sourceId;
	}

	public void setSourceWrapper(RecordWrapper src){
		if ((src != null) && (!src.getRecordId().equals(this.sourceId))) {
			throw new DumasException(
					"Provided soucce wrapper has false record id.\n_sourceId = " + 
							this.sourceId + ", id of src = " + src.getRecordId());
		}
		this.sourceRec = src;
	}

	public RecordWrapper getSourceWrapper(){
		return this.sourceRec;
	}

	public void setTargetWrapper(RecordWrapper tgt){
		if ((tgt != null) && (!tgt.getRecordId().equals(this.targetId))) {
			throw new DumasException(
					"Provided target wrapper has false record id.\n_tgtId = " + 
							this.targetId + ", id of tgt = " + tgt.getRecordId());
		}
		this.targetRec = tgt;
	}

	public RecordWrapper getTargetWrapper(){
		return this.targetRec;
	}


}
