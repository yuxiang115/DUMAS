package postgredb;

import java.io.Serializable;

import db.Table;
import dumasException.DumasException;
import tableModel.QueryTableModel;

public class PostgreDatabase extends AbstractDatabase implements Serializable{
	private transient Character boundary = null;
	
	private static final int OUT = 0;
	private static final int INQUOTE = 1;
	private static final int IMPLICITQUOTE = 2;
	
	
	public PostgreDatabase(){
		this(null);
	}
	
	public PostgreDatabase(String name) {
		super(name);
	}
	
	
	public void load(QueryTableModel tableModel, String ridColumn, String rwoColumn){
		
		PostgreTable schema = setHeader(tableModel.getHeadersName(), tableModel.getName(), rwoColumn);
		setTable(schema);
		
		int numRows = tableModel.getRowCount();
		for(int i = 0; i < numRows; i++){
			String[] row = (String[])tableModel.getValueAtRow(i);
			PostgreRecord record = parseRecord(row,String.valueOf(i) ,(PostgreTable)getTable(null));
			addRecord((PostgreRecordId)record.getRecordId(), record);
		}
		
	}
	
	private PostgreTable setHeader(String[] headers,String tableName, String rwoColumn){
		PostgreTable table = new PostgreTable(tableName, this);
		ColumnType colTypekey = new ColumnType();
		colTypekey.addType(2);
		PostgreColumn colKey = new PostgreColumn(colTypekey, "KeyCol");
		table.addPostgreColumn(colKey);
		
		for(int i = 0; i < headers.length; i++){
			ColumnType colType = new ColumnType();
			String el = headers[i];

			if(el.equals(rwoColumn)){
				colType.addType(4);
			}
			else{
				colType.addType(1);
			}
			PostgreColumn col = new PostgreColumn(colType, el);
			table.doAddPostgreColumn(col);
		}
		return table;
	}
	
	private PostgreRecord parseRecord(String[] row,String rowID, PostgreTable table){
		if(row.length != table.length() - 1){
			throw new DumasException(
			        row.toString() + 
			        " does not fit the given schema. Incompatible number of fields.");
		}
		IdentifiableRecord record = new IdentifiableRecord(table);
		Element e = new Element(table.getPostgreColumn(1));
		e.setValue(rowID);
		record.addElement(e);
		
		for (int i = 1; i < row.length + 1; i++) {
		      Element el = new Element(table.getPostgreColumn(i + 1));
		      el.setValue(row[i - 1]);
		      record.addElement(el);
		}
		return record;
	}



}
