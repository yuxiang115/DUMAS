package complexmatching;

import java.util.ArrayList;

import datastructure.Alignment;
import datastructure.ColSet;
import datastructure.ComplexScoreMatrix;
import dumasException.DumasException;



public class MatrixWrapper
implements Comparable {
    private static int _nextId = 0;
    private ComplexScoreMatrix _matrix = null;
    private double _gmScore = -1.0;
    private int _id;
    private Alignment _align = null;
    private ComplexTableMatch _complexMatch = null;
    private MatrixWrapper _parentWrapper = null;
    private double _corSum = -1.0;
    private double _corAvg = -1.0;
    private double _weightedCorSum = -1.0;
    private String _info = null;
    private MergedColumns _mergeInfo = null;
    private boolean _wasImprovement = true;

    public MatrixWrapper(ComplexScoreMatrix matrix, ComplexTableMatch matcher, MatrixWrapper parent) {
        this.setId();
        this._matrix = matrix;
        this._complexMatch = matcher;
        this._align = matcher.createAlignment(matrix);
        this.setParent(parent);
    }

    private void setId() {
        this._id = ++_nextId;
    }

    public int getId() {
        return this._id;
    }

    public void setParent(MatrixWrapper parent) {
        this._parentWrapper = parent;
    }

    public MatrixWrapper getParent() {
        return this._parentWrapper;
    }

    public int getParentId() {
        MatrixWrapper parent = this.getParent();
        if (parent == null) {
            return 0;
        }
        return parent.getId();
    }

    public ComplexScoreMatrix getMatrix() {
        return this._matrix;
    }

    public Alignment getAlignment() {
        return this._align;
    }

    public double getMatchScore() {
        return this.getGMAvg();
    }

    public double getGMAvg() {
        if (this._gmScore < 0.0) {
            this._gmScore = this._complexMatch.gmAvg(this.getMatrix(), this.getAlignment());
        }
        return this._gmScore;
    }

    public double getCorSum() {
        if (this._corSum < 0.0) {
            this._corSum = this._complexMatch.corSum(this.getMatrix(), this.getAlignment());
        }
        return this._corSum;
    }

    public double getCorAvg() {
        if (this._corAvg < 0.0) {
            this._corAvg = this._complexMatch.corAvg(this.getMatrix(), this.getAlignment());
        }
        return this._corAvg;
    }

    public double getWeightedCorSum() {
        if (this._weightedCorSum < 0.0) {
            this._weightedCorSum = this._complexMatch.weightedCorSum(this.getMatrix(), this.getAlignment());
        }
        return this._weightedCorSum;
    }

    public int compareTo(Object obj) {
        MatrixWrapper wrapper = (MatrixWrapper)obj;
        double myMatchScore = this.getWeightedCorSum();
        double otherMatchScore = wrapper.getWeightedCorSum();
        if (otherMatchScore > myMatchScore) {
            return 1;
        }
        if (otherMatchScore < myMatchScore) {
            return -1;
        }
        return 0;
    }

    public void setInfo(String info) {
        this._info = info;
    }

    public void addToInfo(String newInfo) {
        this._info = String.valueOf(this._info) + newInfo;
    }

    public String getInfo() {
        return this._info;
    }

    public String toString() {
        return "MW:" + this.getCorSum();
    }

    public String debugInfo() {
        String line = "Matrix " + this.getId() + "(" + this.getParentId() + ")\n";
        line = String.valueOf(line) + this.getMatrix().toString(this.getAlignment()) + "\n";
        line = String.valueOf(line) + "GMAvg: " + this.getGMAvg() + "\n";
        line = String.valueOf(line) + "CorSum: " + this.getCorSum() + "\n";
        line = String.valueOf(line) + "CorAvg: " + this.getCorAvg() + "\n";
        line = String.valueOf(line) + "WeightCorSum: " + this.getWeightedCorSum() + "\n";
        line = String.valueOf(line) + "Info: " + this.getInfo() + "\n";
        line = String.valueOf(line) + "MergeInfo: " + this.getMergeInfo() + "\n";
        line = String.valueOf(line) + "Improvement: " + (this.wasImprovement() ? "YES" : "NO");
        return line;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MatrixWrapper) {
            MatrixWrapper wrapper = (MatrixWrapper)obj;
            return this.getMatrix().equals(wrapper.getMatrix());
        }
        return false;
    }

    public void setMergeInfo(MergedColumns merged) {
        if (this._mergeInfo != null) {
            throw new DumasException("Merge info cannot be reset.");
        }
        this._mergeInfo = merged;
    }

    public MergedColumns getMergeInfo() {
        if (this._mergeInfo == null) {
            if (this.getParent() == null) {
                return null;
            }
            this._mergeInfo = this.computeMergedColumns(this.getMatrix(), this.getParent().getMatrix());
        }
        return this._mergeInfo;
    }

    private MergedColumns computeMergedColumns(ComplexScoreMatrix matrix, ComplexScoreMatrix parent) {
        System.out.println(">>>>>>>>>>>>> WARNING: MergedColumns not set!!! <<<<<<<<<<<<<");
        ArrayList<ColSet> notFound = new ArrayList<ColSet>();
        int srcLength = parent.getSourceLength();
        for (int i = 1; i <= srcLength; ++i) {
            ColSet set = parent.getSrcColumns(i);
            if (matrix.hasSrcColumns(set)) continue;
            notFound.add(set);
        }
        if (notFound.size() == 2) {
            ColSet col2;
            boolean isSource = true;
            ColSet col1 = (ColSet)notFound.get(0);
            ColSet merged = ColSet.merge(col1, col2 = (ColSet)notFound.get(1));
            int index = matrix.getSrcIndex(merged);
            if (index > 0) {
                MergedColumns mergeInfo = new MergedColumns(col1, col2, matrix.getSrcColumns(index), isSource);
                return mergeInfo;
            }
            throw new DumasException("Merged columns not found.");
        }
        if (notFound.size() != 0) {
            throw new DumasException("Number of source columns is " + notFound.size() + ".");
        }
        int tgtLength = parent.getTargetLength();
        for (int i = 1; i <= tgtLength; ++i) {
            ColSet set = parent.getTgtColumns(i);
            if (matrix.hasTgtColumns(set)) continue;
            notFound.add(set);
        }
        if (notFound.size() == 2) {
            ColSet col2;
            boolean isSource = false;
            ColSet col1 = (ColSet)notFound.get(0);
            ColSet merged = ColSet.merge(col1, col2 = (ColSet)notFound.get(1));
            int index = matrix.getTgtIndex(merged);
            if (index > 0) {
                MergedColumns mergeInfo = new MergedColumns(col1, col2, matrix.getTgtColumns(index), isSource);
                return mergeInfo;
            }
            throw new DumasException("Merged columns not found.");
        }
        if (notFound.size() == 0) {
            throw new DumasException("No columns seem to be merged.");
        }
        throw new DumasException("Number of target columns is " + notFound.size() + ".");
    }

    public boolean wasImprovement() {
        return this._wasImprovement;
    }

    public void setImprovement(boolean imp) {
        this._wasImprovement = imp;
    }
}

