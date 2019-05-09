package postgredb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import db.Column;
import db.Record;
import db.RecordId;
import db.RecordIterator;
import db.Schema;
import db.Table;
import dumasException.DumasException;

public class PostgreTable implements Table, Serializable{
	private ArrayList columns = new ArrayList();

	private String name = null;

	private AbstractDatabase db = null;
	private int[] relevantColumns = null;

	public PostgreTable(String name, AbstractDatabase db){
		this.name = name;
		this.db = db;
	}

	public void setDatabase(AbstractDatabase db){
		this.db = db;
	}

	public void addPostgreColumn(PostgreColumn column){
		boolean rid = column.getColumnType().hasType(2);
		boolean rwo = column.getColumnType().hasType(4);

		if (numRecords() > 0){
			throw new DumasException("Cannot add column " + column.getName() + 
					". Table " + getName() + " already contains records.");
		}

		if ((rid) || (rwo)) {
			Iterator iter = columns.iterator();
			while (iter.hasNext()) {
				PostgreColumn col = (PostgreColumn)iter.next();
				if ((rwo) && (col.getColumnType().hasType(4))) {
					throw new DumasException(
							"Schema contains more than one RWO column.");
				}
			}
		}
		doAddPostgreColumn(column);
	}

	protected void doAddPostgreColumn(PostgreColumn column){
		Table oldTable = column.getTable();
		if (oldTable != null) {
			throw new DumasException("Column " + column.getName() + 
					" is already part of another table.");
		}
		if (hasPostgreColumn(column.getName())) {
			throw new DumasException("Column with name " + column.getName() + 
					" already exists.");
		}

		columns.add(column);
		column.setColumnSet(this);
	}

	public PostgreColumn getPostgreColumn(int pos){
		return (PostgreColumn)columns.get(pos - 1);
	}

	public PostgreColumn getPostgreColumn(String name) {
		Iterator colIter = columns.iterator();
		while (colIter.hasNext()){
			PostgreColumn col = (PostgreColumn)colIter.next();
			if (col.getName().equalsIgnoreCase(name)) {
				return col;
			}
		}
		return null;
	}

	public boolean hasPostgreColumn(String name){
		return getPostgreColumn(name) != null;
	}

	public boolean hasPostgreColumnObject(PostgreColumn column){
		Iterator iter = columns.iterator();
		while (iter.hasNext()){
			if(column == iter.next()){
				return true;
			}
		}
		return false;
	}

	public PostgreColumn getColumn(ColumnType columnType, int pos){
		if(pos < 1)
			return null;

		int found = 0;
		Iterator iter = this.columns.iterator();
		while (iter.hasNext()) {
			PostgreColumn col = (PostgreColumn)iter.next();
			if (col.getColumnType().getType() == columnType.getType()){
				found++;
			}
			if(found == pos){
				return col;
			}
		}
		return null;
	}

	public Column getColumn(int pos){
		return getColumn(ColumnType.VALUE_TYPE, pos);
	}

	public List getColumns(){
		List result = new ArrayList(columns.size());
		Iterator iter = columns.iterator();
		while (iter.hasNext()) {
			PostgreColumn col = (PostgreColumn)iter.next();
			ColumnType type = col.getColumnType();
			if (type.hasType(1)) {
				result.add(col);
			}
		}
		return result;
	}

	public int length(){
		return columns.size();
	}

	public int length(ColumnType columnType){
		int found = 0;
		Iterator iter = columns.iterator();
		while (iter.hasNext()) {
			PostgreColumn col = (PostgreColumn)iter.next();
			if (col.getColumnType().hasType(columnType.getType())) {
				found++;
			}
		}
		return found;
	}

	public String getColumnName(int pos){
		PostgreColumn col = getColumn(ColumnType.VALUE_TYPE, pos);
		return col.getName();
	}

	public int numColumns(){
		return length(ColumnType.VALUE_TYPE);
	}

	public String toString(){
		StringBuffer result = new StringBuffer();
		result.append("Schema: ");
		String rid = null;
		String cid = null;
		StringBuffer cols = null;
		Iterator colIter = columns.iterator();
		while (colIter.hasNext()) {
			PostgreColumn col = (PostgreColumn)colIter.next();
			if (col.getColumnType().hasType(ColumnType.RID_TYPE)){
				if (rid == null){
					rid = "[" + col.getName();
				}
				else{
					rid = rid + ", " + col.getName();
				}
			}
			if (col.getColumnType().hasType(ColumnType.RWO_TYPE)){
				cid = col.getName();
			}
			if (col.getColumnType().hasType(ColumnType.VALUE_TYPE)){
				if (cols == null){
					cols = new StringBuffer();
					cols.append(col.getName());
				}
				else{
					cols.append(", " + col.getName());
				}
			}
		}
		if (rid != null) {
			result.append("RID: " + rid + "]; ");
		}

		if (cid != null) {
			result.append("RWO: " + cid + "]; ");
		}
		result.append(cols);
		return result.toString();
	}

	public String getName(){
		return name;
	}

	public Schema getSchema(){
		return null;
	}

	public String getQualifiedName(){
		String schemaName = null;
		if (db != null) {
			schemaName = db.getName();
		}
		if (schemaName == null) {
			return getName();
		}

		return schemaName + "." + getName();
	}

	public Column getColumn(String name){
		Iterator colIter = columns.iterator();
		while (colIter.hasNext()) {
			PostgreColumn col = (PostgreColumn)colIter.next();
			if (col.getName().equalsIgnoreCase(name)){
				return col;
			}
		}
		return null;
	}

	public int getColumnPosition(Column column){
		int size = numColumns();
		for (int i = 1; i <= size; i++){
			Column curCol = getColumn(i);
			if (curCol.equals(column)) {
				return i;
			}
		}
		return -1;
	}

	public int getColumnPosition(String name){
		throw new UnsupportedOperationException("Method not needed.");
	}

	public Record getRecord(RecordId id){
		return db.getRecord(name, id);
	}
	
	public Record[] getRecords(RecordId[] ids){
		Record[] result = new PostgreRecord[ids.length];
		for (int i = 0; i < ids.length; i++) {
			result[i] = getRecord(ids[i]);
		}
		return result;
	}
	
	public int numRecords(){
		if (db == null) {
			return 0;
		}
		return db.numRecords();
	}
	
	public RecordIterator recordIterator(){
		return db.recordIterator(name);
	}
	
	public AbstractDatabase getDatabase() {
		return db;
	}
	
	public int[] getRelevantColumns() {
		if (relevantColumns == null) {
			relevantColumns = new int[numColumns()];
			for (int i = 1; i <= relevantColumns.length; i++) {
				relevantColumns[(i - 1)] = i;
			}
		}
		return relevantColumns;
	}
	
	public void setRelevantColumns(int[] rel) {
		relevantColumns = rel;
	}
}
