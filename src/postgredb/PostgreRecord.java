package postgredb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import db.Record;
import db.RecordId;
import db.Table;
import dumasException.DumasException;

public class PostgreRecord implements Record, Serializable{
	private PostgreRecordId recordId = null;
	private ArrayList elements = new ArrayList();
	private PostgreTable table;

	public PostgreRecord(PostgreTable table){
		this.table = table;
	}

	public void addElement(Element e){
		addElement(e, true);
	}

	public void addElement(Element e, boolean setRecordId){
		PostgreColumn col = e.getColumn();
		ColumnType colType = col.getColumnType();

		int pos = elements.size() + 1;
		if (pos > table.length()) {
			throw new DumasException("Trying to add element at position " + pos + 
					". Table has length " + table.length() + ".");
		}
		if (e.getColumn() != table.getPostgreColumn(pos)) {
			throw new DumasException(
					"Column of the element is not the same as the column at the given position of the schema");
		}
		elements.add(e);

		if ((colType.hasType(2)) && (setRecordId)) {
			setRecordId();
		}
	}

	public List getElements(){
		return elements;
	}

	public String valueString(){
		StringBuffer valueString = new StringBuffer(200);
		boolean space = false;
		for (int i = 1; i <= numValues(); i++) {
			if(!isNull(i)) {
				String val = getValue(i);
				if (space) {
					valueString.append(" ");
				}
				else{
					space = true;
				}
				valueString.append(val);
			}
		}
		return valueString.toString();
	}

	public String getValue(int pos){
		if (pos <= 0) {
			throw new DumasException("Positions start at 1.");
		}
		int found = 0;
		for (int i = 0; i < elements.size(); i++) {
			Element el = (Element)elements.get(i);
			if (el.getColumn().getColumnType().hasType(1)){
				found++;
			}
			if (found == pos){
				return el.getValue();
			}
		}
		throw new DumasException("Position " + pos + " is out of bounds.");
	}

	public String getValues() {
		return valueString();
	}

	public String[] getValues(int[] pos){
		String[] result = (String[])null;
		if (pos == null) {
			int numValues = numValues();
			result = new String[numValues];
			for (int i = 1; i <= numValues; i++) {
				result[(i - 1)] = (isNull(i) ? null : getValue(i));
			}
		} else {
			result = new String[pos.length];
			for (int i = 0; i < pos.length; i++) {
				result[i] = (isNull(pos[i]) ? null : getValue(pos[i]));
			}
		}
		return result;
	}

	public int numValues(){
		int num = 0;
		for (Iterator i = elements.iterator(); i.hasNext();) {
			Element el = (Element)i.next();
			if (el.getColumn().getColumnType().hasType(1)){
				num++;
			}
		}
		return num;
	}

	public boolean isNull(int pos){
		String value = getValue(pos);
		if ((value == null) || (value.equals("")) || (value.equals(" "))) {
			return true;
		}
		return false;
	}

	public Table getSchema(){
		return table;
	}

	private void setRecordId(){
		recordId = new PostgreRecordId();	    
		Iterator elIter = getElements().iterator();
		while (elIter.hasNext()) {
			Element el = (Element)elIter.next();
			if (el.getColumn().getColumnType().hasType(2)) {
				recordId.addElement(el);
			}
		}
	}

	public RecordId getRecordId() {
		return recordId;
	}

	public String toString(){
		StringBuffer line = new StringBuffer(200);
		for (int i = 1; i <= numValues(); i++) {
			if (i == 1) {
				line.append("[");
			}
			else{
				line.append(", ");
			}
			line.append(getSchema().getColumn(i).getName());
			line.append(":");
			line.append(getValue(i));
		}
		line.append("]");
		return line.toString();
	}
	
	public String[] getRelevantValues() {
		int[] relevantColumns = table.getRelevantColumns();
		return getValues(relevantColumns);
	}
}
