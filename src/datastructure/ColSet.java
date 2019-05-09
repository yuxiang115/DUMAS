package datastructure;

import java.util.ArrayList;
import java.util.Iterator;

import db.Column;

public class ColSet {
	private ArrayList columns = new ArrayList();

	public ColSet(Column col){
		addColumn(col);
	}

	public ColSet(ColSet columns){
		addColumns(columns);
	}

	public void addColumn(Column col){
		if (col == null) {
			throw new NullPointerException("No database column provided.");
		}
		if (!contains(col)) {
			this.columns.add(col);
		}
	}

	public void addColumns(ColSet columns){
		int size = columns.numColumns();
		for (int i = 1; i <= size; i++) {
			addColumn(columns.getColumn(i));
		}
	}

	public Column[] getColumns(){
		return (Column[])this.columns.toArray(new Column[columns.size()]);
	}

	public Column getColumn(int i){
		return (Column)this.columns.get(i - 1);
	}

	public boolean contains(Column col){
		Iterator colIter = this.columns.iterator();
		while (colIter.hasNext()){
			Column myCol = (Column)colIter.next();
			if (myCol.equals(col)) {
				return true;
			}
		}
		return false;
	}

	public boolean equals(Object obj){
		if ((obj instanceof ColSet)){
			ColSet other = (ColSet)obj;
			Column[] columns = other.getColumns();
			if (columns.length != getColumns().length) {
				return false;
			}
			for (int i = 0; i < columns.length; i++) {
				if (!contains(columns[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public int numColumns(){
		return this.columns.size();
	}
	
	public String toString(){
		int size = numColumns();
	    StringBuffer line = new StringBuffer(200);
	    line.append("{");
	    for (int i = 1; i <= size; i++)
	    {
	      line.append(getColumn(i).getName());
	      if (i < size) {
	        line.append(",");
	      }
	    }
	    line.append("}");
	    return line.toString();
	}
	
	public static ColSet merge(ColSet set1, ColSet set2){
		 ColSet newSet = new ColSet(set1);
		 newSet.addColumns(set2);
		 return newSet;
	}
}
