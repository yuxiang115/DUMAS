package db;

public abstract interface Record {
	public abstract RecordId getRecordId();
	
	public abstract String getValue(int paramInt);
	
	public abstract boolean isNull(int paramInt);
	
	public abstract String getValues();
	
	public abstract String[] getValues(int[] paramArrayOfInt);
	
	public abstract String[] getRelevantValues();
	
	public abstract int numValues();
	
	public abstract Table getSchema();
}
