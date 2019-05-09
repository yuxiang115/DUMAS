package postgredb;

import java.io.Serializable;

import db.Column;
import db.Table;
import dumasException.DumasException;

public class PostgreColumn implements Column, Serializable{
	private ColumnType columnType = null;

	private String name = null;

	private PostgreTable table = null;

	public PostgreColumn(ColumnType type, String name){
		columnType = type;
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public int getDataType(){
		return 1;
	}

	public void setName(String name){
		this.name = name;
	}

	public ColumnType getColumnType(){
		return columnType;
	}

	public boolean equals(Object obj){
		if((obj instanceof PostgreColumn)){
			PostgreColumn other = (PostgreColumn)obj;
			if ((getName().equalsIgnoreCase(other.getName())) && 
					(getColumnType().equals(other.getColumnType()))) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public Table getTable(){
		return table;
	}
	
	public void setColumnSet(PostgreTable tab){
		if (!tab.hasPostgreColumnObject(this)) {
			throw new DumasException("Table " + tab.getQualifiedName() + 
			        " does not contain this column.");
		}
		this.table = tab;
	}
}
