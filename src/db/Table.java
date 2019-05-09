package db;

public abstract interface Table extends ColumnSet{
	public abstract String getName();
	
	public abstract String getQualifiedName();
	
	public abstract Schema getSchema();
	
	public abstract String getColumnName(int paramInt);
	
	public abstract int[] getRelevantColumns();
	
	public abstract Record getRecord(RecordId paramRecordId);
	
	public abstract Record[] getRecords(RecordId[] paramArrayOfRecordId);
	
	public abstract RecordIterator recordIterator();
	
	public abstract int numRecords();
}
