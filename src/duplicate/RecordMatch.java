package duplicate;

import java.util.Collection;

import datastructure.Alignment;
import db.Table;

public abstract interface RecordMatch {
	public abstract void load(Table paramTable1, Table paramTable2);
	public abstract Collection match(Collection paramCollection1, Collection paramCollection2, Alignment paramAlignment);
}
