package postgredb;

import java.io.Serializable;
import java.util.List;

public class IdentifiableRecord extends PostgreRecord implements Serializable{
	private Integer rwoId;

	public IdentifiableRecord(PostgreTable schema){
		super(schema);
	}

	public Integer getRwoId() {
		return rwoId;
	}

	public void addElement(Element e){
		super.addElement(e);

		ColumnType colType = e.getColumn().getColumnType();
		if (colType.hasType(4)) {
			String rwoString = e.getValue();
			rwoId = Integer.valueOf(rwoString);
		}
	}


	public String toString(){
		StringBuffer buf = new StringBuffer(200);
		buf.append(rwoId);
		buf.append(":");
		buf.append(getRecordId());
		buf.append(":");
		List elements = getElements();
		for (int i = 0; i < elements.size(); i++) {
			Element el = (Element)elements.get(i);
			if (el.getColumn().getColumnType().hasType(1)) {
				buf.append(el.getValue());

				buf.append(":");
			}
		}
		return buf.toString();
	}
}
