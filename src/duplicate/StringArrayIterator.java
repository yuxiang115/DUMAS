package duplicate;

import java.util.Iterator;

import db.Record;

public class StringArrayIterator implements Iterator{
	private Iterator iter;

	public StringArrayIterator(Iterator iter){
		this.iter = iter;
	}

	public boolean hasNext(){
		return this.iter.hasNext();
	}

	public Object next(){
		Record record = (Record)this.iter.next();
		String[] array = record.getValues(null);
		return array;
	}

	public void remove(){
		throw new UnsupportedOperationException("Operation not supported.");
	}
}