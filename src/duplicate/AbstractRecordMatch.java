package duplicate;

import datastructure.Alignment;
import datastructure.MatchScore;
import db.RecordId;
import dumasException.DumasException;

public abstract class AbstractRecordMatch implements RecordMatch{
	 protected static final boolean debug = true;
	 protected StringCompare compare;
	 private TableWrapper db1 = null;
	 private TableWrapper db2 = null;
	 
	 protected AbstractRecordMatch(StringCompare compare){
	    this.compare = compare;
	  }
	 
	 public TableWrapper getSourceWrapper(){
	    return this.db1;
	  }
	  
	  public TableWrapper getTargetWrapper(){
	    return this.db2;
	  }
	  
	  public void setSourceWrapper(TableWrapper source){
	    this.db1 = source;
	  }
	  
	  public void setTargetWrapper(TableWrapper target){
	    this.db2 = target;
	  }
	  
	  public MatchScore score(RecordId sourceId, RecordId targetId, Alignment alignment){
	    RecordWrapper sourceWrapper = this.db1.getRecordWrapper(sourceId);
	    if (sourceWrapper == null) {
	      throw new DumasException(
	        "Source database does not contain a record with ID" + 
	        sourceId + ".");
	    }
	    RecordWrapper targetWrapper = this.db2.getRecordWrapper(targetId);
	    if (targetWrapper == null) {
	      throw new DumasException(
	        "Target database does not contain a record with ID" + 
	        targetId + ".");
	    }
	    return score(sourceWrapper, targetWrapper, alignment, true);
	  }
	  
	  protected abstract MatchScore score(RecordWrapper paramRecordWrapper1, RecordWrapper paramRecordWrapper2, Alignment paramAlignment, boolean paramBoolean);

}
