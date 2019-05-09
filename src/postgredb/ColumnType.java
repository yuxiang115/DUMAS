package postgredb;

import java.io.Serializable;

public class ColumnType implements Serializable{
	private int type = 0;

	public static final int VALUE = 1;
	public static final int RID = 2;
	public static final int RWO = 4;

	public static final ColumnType VALUE_TYPE = new ColumnType(1);
	public static final ColumnType RID_TYPE = new ColumnType(2);
	public static final ColumnType RWO_TYPE = new ColumnType(4);

	public ColumnType() {}

	public ColumnType(int type){
		this.type = type;
	}

	public int getType(){
		return type;
	}

	public boolean hasType(ColumnType type){
		return hasType(type.getType());
	}

	public boolean hasType(int type){
		return (this.type & type) == type;
	}

	public void addType(int type){
		this.type |= type;
	}

	public boolean equals(Object obj) {
		if ((obj instanceof ColumnType)) {
			ColumnType typeObj = (ColumnType)obj;
			return typeObj.getType() == type;
		}
		return false;
	}
}
