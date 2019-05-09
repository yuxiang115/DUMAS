package tableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class QueryTableModel extends AbstractTableModel {
	Vector cache; // will hold String[] objects . . .

	String tableName;

	int colCount;

	String[] headers;

	Connection db;

	Statement  statement;

	public QueryTableModel() {
		cache = new Vector(); 
	}

	public QueryTableModel(String tableName) {
		cache = new Vector();
		this.tableName = tableName;
	}
	
	public void setName(String name){
		this.tableName = name;
	}
	

	
	
	public Vector getRows(){
		return cache;
	}
	
	public String getName(){
		return this.tableName;
	}

	public String getColumnName(int i) {
		return headers[i];
	}

	public String[] getHeadersName(){
		return headers;
	}

	public int getColumnCount() {
		return colCount;
	}

	public int getRowCount() {
		return cache.size();
	}

	public Object getValueAt(int row, int col) {
		return ((String[]) cache.elementAt(row))[col];
	}

	public Object getValueAtRow(int row) {
		return ((String[]) cache.elementAt(row));
	}

	public void setConnection(Connection conn) throws SQLException{
		db = conn;
		statement = db.createStatement();
	}


	// All the real work happens here; in a real application,
	// we'd probably perform the query in a separate thread.
	public void setQuery(String q) {
		cache = new Vector();
		try {
			// Execute the query and store the result set and its metadata
			ResultSet rs = statement.executeQuery(q);
			ResultSetMetaData meta = rs.getMetaData();
			colCount = meta.getColumnCount();

			// Now we must rebuild the headers array with the new column names
			headers = new String[colCount];
			for (int h = 1; h <= colCount; h++) {
				headers[h - 1] = meta.getColumnName(h);
			}

			// and file the cache with the records from our query. This would
			// not be
			// practical if we were expecting a few million records in response
			// to our
			// query, but we aren't, so we can do this.
			while (rs.next()) {
				String[] record = new String[colCount];
				for (int i = 0; i < colCount; i++) {
					record[i] = rs.getString(i + 1);
				}
				cache.addElement(record);
			}
			fireTableChanged(null); // notify everyone that we have a new table.
		} catch (Exception e) {
			cache = new Vector(); // blank it out and keep going.
			e.printStackTrace();
		}
	}
	
	public void setData(String[] headers, Vector rows){
		this.headers = headers.clone();
		this.cache = (Vector) rows.clone();
		this.colCount = headers.length;
		fireTableChanged(null);
	}


}