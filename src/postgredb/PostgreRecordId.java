package postgredb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import db.RecordId;
import dumasException.DumasException;

public class PostgreRecordId implements RecordId, Serializable{
	private ArrayList values = null;

	public PostgreRecordId(){
		values = new ArrayList();
	}

	public void addElement(Element e){
		values.add(e);
	}

	public Element[] getValues() {
		return (Element[])values.toArray(new Element[values.size()]);
	}

	public int compareTo(Object id){
		PostgreRecordId rid = (PostgreRecordId)id;
		Element[] values = rid.getValues();
		if (this.values.size() != values.length) {
			throw new DumasException("Incompatible id length.");
		}
		for (int i = 0; i < this.values.size(); i++) {
			if (!this.values.get(i).equals(values[i])) {
				return ((Element)this.values.get(i)).compareTo(values[i]);
			}
		}
		return 0;
	}

	public int hashCode() {
		int hashCode = 0;
		Iterator iter = values.iterator();
		while (iter.hasNext()) {
			Element el = (Element)iter.next();
			hashCode ^= el.getValue().hashCode();
		}
		return hashCode;
	}

	public boolean equals(Object obj){
		if ((obj instanceof PostgreRecordId)) {
			PostgreRecordId other = (PostgreRecordId)obj;
			if (size() != other.size()) {
				return false;
			}
			Element[] val1 = getValues();
			Element[] val2 = other.getValues();
			for (int i = 0; i < size(); i++) {
				if (!val1[i].equals(val2[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public int size() {
		return values.size();
	}
	
	public String toString() {
		String result = "[";
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) {
				result = result + ", ";
			}
			result = result + values.get(i);
		}
		result = result + "]";
		return result;
	}
}
