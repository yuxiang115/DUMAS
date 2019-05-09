package db;

public abstract interface Schema {
	public abstract String getName();
	
	public abstract Record getRecord(String paramString, RecordId paramRecordId);
	
	public abstract RecordIterator recordIterator(String paramString);
	
	public abstract Table getTable(String paramString);
}
