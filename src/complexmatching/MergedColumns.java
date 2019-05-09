package complexmatching;

import datastructure.ColSet;

public class MergedColumns {
    private ColSet _col1 = null;
    private ColSet _col2 = null;
    private ColSet _merged = null;
    private boolean _isSource = false;

    public MergedColumns(ColSet col1, ColSet col2, ColSet merged, boolean isSource) {
        this._col1 = col1;
        this._col2 = col2;
        this._merged = merged;
        this._isSource = isSource;
    }

    public ColSet getFirst() {
        return this._col1;
    }

    public ColSet getSecond() {
        return this._col2;
    }

    public ColSet getMerged() {
        return this._merged;
    }

    public boolean isSource() {
        return this._isSource;
    }

    public String toString() {
        StringBuffer line = new StringBuffer();
        if (this.isSource()) {
            line.append("Source: [");
        } else {
            line.append("Target: [");
        }
        line.append(this.getFirst().toString());
        line.append(",");
        line.append(this.getSecond().toString());
        line.append("]->");
        line.append(this.getMerged().toString());
        return line.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof MergedColumns) {
            MergedColumns merge = (MergedColumns)obj;
            return this.getFirst().equals(merge.getFirst()) && this.getSecond().equals(merge.getSecond()) && this.getMerged().equals(merge.getMerged()) && this.isSource() == merge.isSource();
        }
        return false;
    }
}

