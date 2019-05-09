package duplicate;

import java.io.Serializable;

import db.Record;
import db.RecordId;
import dumasException.DumasException;
import string.StringWrapper;

public class RecordWrapper implements Serializable{
	private RecordId recordId = null;
	private int numColumns = 0;
	private StringWrapper wrapper = null;
	private StringWrapper[] fieldWrapper = null;

	public RecordWrapper(Record record, RecordId recId){
		this.recordId = recId;
		this.numColumns = record.numValues();
	}

	public RecordId getRecordId(){
		return this.recordId;
	}

	public int numValues(){
		return this.fieldWrapper.length;
	}

	public boolean isNull(int pos){
		return this.fieldWrapper[(pos - 1)] == null;
	}

	public void setStringWrapper(StringWrapper wrapper){
		this.wrapper = wrapper;
	}

	public StringWrapper getStringWrapper(){
		return this.wrapper;
	}

	public void setFieldWrapper(StringWrapper[] wrapper)
	{
		if (wrapper.length == this.numColumns) {
			this.fieldWrapper = wrapper;
		} else {
			throw new DumasException("Incompatible length. Array has " + 
					wrapper.length + " values, record has " + this.numColumns + 
					" columns.");
		}
	}

	public StringWrapper getFieldWrapper(int pos){
		return this.fieldWrapper[(pos - 1)];
	}

	public boolean hasFieldWrapper(){
		return this.fieldWrapper != null;
	}

	public boolean equals(Object obj){
		if ((obj instanceof RecordWrapper))
		{
			RecordWrapper wrapper = (RecordWrapper)obj;
			return getRecordId().equals(wrapper.getRecordId());
		}
		return false;
	}

	public String toString(){
		return this.wrapper.toString();
	}
}
