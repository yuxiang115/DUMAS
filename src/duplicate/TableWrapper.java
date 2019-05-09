package duplicate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import db.Column;
import db.Record;
import db.RecordId;
import db.RecordIterator;
import db.Schema;
import db.Table;
import dumasException.DumasException;
import string.StringDistance;
import string.StringWrapper;

public class TableWrapper implements Table, Serializable{
	private transient Table table = null;
	private int[] residualColumns = null;
	private StringDistance recordCompare = null;
	private StringDistance fieldCompare = null;
	private boolean storeWrappers = false;
	private Hashtable recordWrappers = null;
	private transient Hashtable recordIds = new Hashtable();

	/**
	 * @param recordCompare
	 * 			String distance to compare record string
	 * @param fileCompare
	 * 			String distance to compare record field
	 * @param table
	 * 			table 
	 * */
	public TableWrapper(StringDistance recordCompare, StringDistance fieldCompare, Table table){
		this.table = table;
		this.recordCompare = recordCompare;
		this.fieldCompare = fieldCompare;
	}

	/**
	 *@return StringDistance
	 * */
	public StringDistance getRecordCompare(){
		return this.recordCompare;
	}
	/**
	 *@return StringDistance
	 * */
	public StringDistance getFieldCompare(){
		return this.fieldCompare;
	}
	/**
	 *@return boolean
	 * */
	public boolean getStoreWrappers(){
		return this.storeWrappers;
	}
	/**
	 * @param residual   int[]
	 * */
	public void setResidualComlumns(int[] residual){
		this.residualColumns = residual;
	}

	public int[] getResidualColumns(){
		return this.residualColumns;
	}

	public int prepareWrappers(Collection recordIds){
		this.storeWrappers = true;
		if (recordIds == null){
			RecordIterator recIter = this.table.recordIterator();
			return prepareWrappers(recIter);
		}
		int num = 0;
		this.recordWrappers = new Hashtable();
		Iterator recIdIter = recordIds.iterator();
		while (recIdIter.hasNext()){
			num++;
			RecordId recId = (RecordId)recIdIter.next();
			Record rec = this.table.getRecord(recId);
			if (rec == null) {
				throw new DumasException("No record with id " + recId + 
						" in table " + this.table.getQualifiedName() + ".");
			}
			RecordWrapper recWrapper = createRecordWrapper(rec);
			this.recordWrappers.put(recId, recWrapper);
		}	
		return num;
	}

	public int prepareWrappers(RecordIterator recIter){
		int num = 0;
		this.recordWrappers = new Hashtable();
		this.storeWrappers = true;
		while (recIter.hasNext()){
			num++;
			Record rec = (Record)recIter.next();
			RecordWrapper recWrapper = createRecordWrapper(rec);
			this.recordWrappers.put(rec.getRecordId(), recWrapper);
		}
		return num;
	}

	public RecordWrapper getRecordWrapper(RecordId id){
		if (this.storeWrappers) {
			return (RecordWrapper)this.recordWrappers.get(id);
		}

		Record rec = this.table.getRecord(id);
		RecordWrapper recWrapper = createRecordWrapper(rec);
		return recWrapper;
	}

	/**
	 * 
	 * @param rids list of RecordID
	 * @return RecordWrapper[]
	 */
	public RecordWrapper[] getRecordWrappers(RecordId[] rids){
		RecordWrapper[] result = new RecordWrapper[rids.length];
		if (this.storeWrappers){
			for (int i = 0; i < rids.length; i++) {
				result[i] = ((RecordWrapper)this.recordWrappers.get(rids[i]));
			}
		}

		else
		{
			Record[] records = this.table.getRecords(rids);
			for (int i = 0; i < rids.length; i++)
			{
				Record rec = records[i];
				result[i] = createRecordWrapper(rec);
			}
		}
		return result;
	}

	/***
	 * @return recordWrapperIterator
	 */
	public Iterator recordWrapperIterator1(){
		if (this.storeWrappers) {
			return this.recordWrappers.values().iterator();
		}
		return new RecordWrapperIterator(this.table.recordIterator());
	}

	/**
	 * @return Iterator
	 * 			recordWrapperIterator
	 * */
	public Iterator recordWrapperIterator(){
		if (this.storeWrappers) {
			return this.recordWrappers.values().iterator();
		}
		return new RecordWrapperIterator(this.table.recordIterator());
	}

	/**
	 * @param rec 
	 * 			Record
	 * @return RecordWrapper
	 * */
	private RecordWrapper createRecordWrapper(Record rec)
	{
		RecordId newRecId = rec.getRecordId();
		if (!this.storeWrappers)
		{
			RecordId oldRecId = (RecordId)this.recordIds.get(newRecId);
			if (oldRecId == null) {
				this.recordIds.put(newRecId, newRecId);
			} else {
				newRecId = oldRecId;
			}
		}
		RecordWrapper recWrapper = new RecordWrapper(rec, newRecId);
		setStringWrapper(rec, recWrapper, this.residualColumns);
		setFieldWrapper(rec, recWrapper);
		return recWrapper;
	}

	/**
	 * @param rec
	 * 			Record
	 * @param recWrapper
	 * 			RecordWrapper
	 * @param residualColumns
	 * 			int[]
	 * */
	private void setStringWrapper(Record rec, RecordWrapper recWrapper, int[] residualColumns){
		StringWrapper stringWrapper = null;
		if ((residualColumns == null) || (residualColumns.length == 0)){
			String[] values = rec.getRelevantValues();
			stringWrapper = this.recordCompare.prepare(values);
		}
		else{
			String[] values = new String[residualColumns.length];
			for (int i = 0; i < residualColumns.length; i++) {
				values[i] = rec.getValue(residualColumns[i]);
			}
			stringWrapper = this.recordCompare.prepare(values);
		}
		recWrapper.setStringWrapper(stringWrapper);
	}

	private void setFieldWrapper(Record rec, RecordWrapper recWrapper){
		StringWrapper[] fieldwrapper = new StringWrapper[rec.numValues()];
		for (int i = 1; i <= rec.numValues(); i++) {
			if (rec.isNull(i)){
				fieldwrapper[(i - 1)] = null;
			}
			else{
				String value = rec.getValue(i);
				fieldwrapper[(i - 1)] = this.fieldCompare.prepare(value);
			}
		}
		recWrapper.setFieldWrapper(fieldwrapper);
	}

	/**
	 * @return recordWrappers's size
	 * 			Hashtable<RecordID, RecordWrapper>
	 * */
	public int size(){
		if (this.recordWrappers == null) {
			return 0;
		}
		return this.recordWrappers.size();
	}


	public Table getTable(){
		return this.table;
	}

	/**
	 * @return Table's name String
	 * */
	public String getName(){
		return this.table.getName();
	}

	public String getQualifiedName(){
		return this.table.getQualifiedName();
	}

	public Schema getSchema(){
		return this.table.getSchema();
	}

	/**
	 * @param Position of Column
	 * @return	name of that column String
	 * */
	public String getColumnName(int pos){
		return this.table.getColumnName(pos);
	}

	/**
	 * @param list of RecordID[]
	 * @return Record[]
	 * */
	public Record[] getRecords(RecordId[] ids){
		return this.table.getRecords(ids);
	}

	public RecordIterator recordIterator(){
		return this.table.recordIterator();
	}

	public Column getColumn(int pos){
		return this.table.getColumn(pos);
	}

	public Column getColumn(String name){
		return this.table.getColumn(name);
	}

	public int getColumnPosition(String name){
		return this.table.getColumnPosition(name);
	}

	public List getColumns(){
		return this.table.getColumns();
	}

	public int numColumns(){
		return this.table.numColumns();
	}

	public int[] getRelevantColumns(){
		return this.table.getRelevantColumns();
	}

	public int numRecords(){
		return this.table.numRecords();
	}

	public int getColumnPosition(Column column){
		return this.table.getColumnPosition(column);
	}
	
	public Record getRecord(RecordId id){
		return this.table.getRecord(id);
	}

	public class RecordWrapperIterator implements Iterator{
		Iterator recordIter = null;

		public RecordWrapperIterator(Iterator recordIter){
			this.recordIter = recordIter;
		}

		public void remove(){
			throw new UnsupportedOperationException(
					"Method remove not supported in RecordWrapperIterator.");
		}

		public boolean hasNext(){
			return this.recordIter.hasNext();
		}

		public Object next(){
			Record rec = (Record)this.recordIter.next();
			RecordWrapper recWrapper = TableWrapper.this.createRecordWrapper(rec);
			return recWrapper;
		}
	}
}
