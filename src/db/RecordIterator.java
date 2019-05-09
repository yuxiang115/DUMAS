package db;

import java.util.Iterator;

public abstract interface RecordIterator extends Iterator{
	public abstract Record nextRecord();
}
