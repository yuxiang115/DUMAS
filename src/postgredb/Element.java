package postgredb;

import java.io.Serializable;

import dumasException.DumasException;

public class Element implements Serializable{
	private PostgreColumn column;
	private String value;

	public Element(PostgreColumn column){
		this.column = column;
	}

	public PostgreColumn getColumn(){
		return column;
	}

	public String getValue(){
		return value;
	}
	
	public void setValue(String value){
		this.value = value;
	}

	public int compareTo(Object obj) {
		Element el = (Element)obj;
		if (!column.equals(el.getColumn())) {
			throw new DumasException("Incompatible columns.");
		}
		return value.compareTo(el.getValue());
	}

	public boolean equals(Object obj) {
		Element el = (Element)obj;
		if (!column.equals(el.getColumn())) {
			return false;
		}
		return value.equals(el.getValue());
	}

	public String toString() {
		return value;
	}
}
