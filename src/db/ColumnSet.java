package db;

import java.util.List;

public abstract interface ColumnSet {
	public abstract Column getColumn(int paramInt);
	
	 public abstract Column getColumn(String paramString);
	 
	 public abstract int getColumnPosition(Column paramColumn);
	 
	 public abstract int getColumnPosition(String paramString);
	 
	 public abstract List getColumns();
	 
	 public abstract int numColumns();
}
