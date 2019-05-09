package postgredb;

import java.util.Iterator;

import db.Record;
import db.RecordIterator;

public class PostgreRecordIterator implements RecordIterator{
	private Iterator baseIter;
	
	public PostgreRecordIterator(Iterator iter){
		this.baseIter = iter;
	}
	
	public Record nextRecord() {
		return (Record)baseIter.next();
	}
	
	public boolean hasNext() {
		return baseIter.hasNext();
	}
	
	public Object next() {
		return nextRecord();
	}
	
	public void remove() {
		throw new UnsupportedOperationException("Remove not supported.");
	}
}
