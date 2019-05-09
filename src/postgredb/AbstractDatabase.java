package postgredb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import db.Record;
import db.RecordId;
import db.RecordIterator;
import db.Schema;
import db.Table;
import dumasException.DumasException;

public abstract class AbstractDatabase implements Schema, Serializable{
	private PostgreTable table;
	private Hashtable records = new Hashtable();

	private String name = null;

	protected AbstractDatabase(String name){
		this.name = name;
	}

	protected void setTable(PostgreTable table) {
		this.table = table;
	}

	public Table getTable(String name){
		return table;
	}
	
	public Table getTable(){
		return table;
	}

	public Table[] getTables() {
		return new Table[] {table};
	}

	public void addRecord(PostgreRecordId key, PostgreRecord record){
		if (key == null) {
			throw new DumasException(
					"Cannot add record to database. Record ID is null.");
		}
		if (records.get(key) != null) {
			throw new DumasException("Record with ID " + key + 
					" already exists.");
		}
		records.put(key, record);
	}

	public Collection getRecords() {
		return records.values();
	}

	public Set getRecordIds() {
		return records.keySet();
	}

	public ArrayList getRecordIdsCopy(){
		Set ids = getRecordIds();
		Iterator idIter = ids.iterator();
		ArrayList copy = new ArrayList(ids.size());
		while (idIter.hasNext()) {
			copy.add(idIter.next());
		}
		return copy;
	}
	
	public Record getRecord(String table, RecordId id){
		PostgreRecordId rid = null;
		if ((id instanceof PostgreRecordId)) {
			rid = (PostgreRecordId)id;
		}
		else{
			throw new DumasException("Incompatible record id class.");
		}
		return getRecord(rid);
	}
	
	public PostgreRecord getRecord(PostgreRecordId id) {
	    return (PostgreRecord)records.get(id);
	  }
	
	public int numRecords() {
		return records.size();
	}
	
	public String getName(){
		return null;
	}
	
	public RecordIterator recordIterator(String table){
		return new PostgreRecordIterator(getRecords().iterator());
	}
}
