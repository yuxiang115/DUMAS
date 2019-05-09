package complexmatching;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import datastructure.ComplexScoreMatrix;

public class MatrixList {
    private TreeSet<MatrixWrapper> _matrices = new TreeSet<MatrixWrapper>();
    private ComplexTableMatch _matcher = null;

    public MatrixList(ComplexTableMatch matcher) {
        this._matcher = matcher;
    }

    public void addMatrixWrapper(MatrixWrapper wrapper) {
        this._matrices.add(wrapper);
    }

    public void addMatrixWrappers(ArrayList wrappers) {
        for (int i = 0; i < wrappers.size(); ++i) {
            this.addMatrixWrapper((MatrixWrapper)wrappers.get(i));
        }
    }

    public ArrayList getMatrices() {
        ArrayList<ComplexScoreMatrix> result = new ArrayList<ComplexScoreMatrix>(this._matrices.size());
        for (MatrixWrapper wrapper : this._matrices) {
            result.add(wrapper.getMatrix());
        }
        return result;
    }

    public ArrayList getMatrixWrappers() {
        ArrayList<MatrixWrapper> result = new ArrayList<MatrixWrapper>(this._matrices.size());
        for (MatrixWrapper wrapper : this._matrices) {
            result.add(wrapper);
        }
        return result;
    }

    public void clear() {
        this._matrices.clear();
    }

    public boolean isEmpty() {
        return this._matrices.size() == 0;
    }

    public MatrixWrapper bestMatrixWrapper() {
        for (MatrixWrapper wrapper : this._matrices) {
            if (!wrapper.wasImprovement()) continue;
            return wrapper;
        }
        return null;
    }

    public String toString() {
        StringBuffer line = new StringBuffer(200);
        line.append("[");
        Iterator iter = this._matrices.iterator();
        int count = 0;
        while (iter.hasNext()) {
            MatrixWrapper wr = (MatrixWrapper)iter.next();
            if (count == 0) {
                ++count;
            } else {
                line.append(",");
            }
            line.append(wr.getCorSum());
        }
        line.append("]");
        return line.toString();
    }
}

