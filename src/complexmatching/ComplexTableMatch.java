/*
 * Decompiled with CFR 0.139.
 */
package complexmatching;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import datastructure.Alignment;
import datastructure.ColSet;
import datastructure.ComplexScoreMatrix;
import datastructure.GroupAlignment;
import datastructure.MatchScore;
import datastructure.TableMatchResult;
import db.Column;
import db.Table;
import dumasException.DumasException;
import duplicate.RecordWrapper;
import string.StringDistance;
import string.StringWrapper;
import tablemaching.AbstractSchemaMatch;

public class ComplexTableMatch
extends AbstractSchemaMatch {
    private static final boolean _debug = false;
    private Table _srcTable = null;
    private Table _tgtTable = null;
    private StringDistance _fieldCompare = null;
    private ArrayList _srcRecords = new ArrayList();
    private ArrayList _tgtRecords = new ArrayList();
    private int _numPartners = 2;
    private double _threshold = 0.7;
    private static final int STANDARDNUMPARTNERS = 2;
    private static final double STANDARDTHRESHOLD = 0.7;
    private static final double SIGNIFICANCE = 0.1;
    private static final double PARTNERTHRESHOLD = 0.2;

    public ComplexTableMatch(Table srcTable, Table tgtTable, StringDistance fieldcompare) {
        this._srcTable = srcTable;
        this._tgtTable = tgtTable;
        this._fieldCompare = fieldcompare;
    }

    public void addDuplicate(MatchScore matchscore) {
        RecordWrapper srcRec = matchscore.getSourceWrapper();
        if (srcRec == null) {
            throw new DumasException("ComplexTableMatch: Source record is null.");
        }
        RecordWrapper tgtRec = matchscore.getTargetWrapper();
        if (tgtRec == null) {
            throw new DumasException("ComplexTableMatch: Target record is null.");
        }
        this._srcRecords.add(srcRec);
        this._tgtRecords.add(tgtRec);
    }

    public TableMatchResult match() {
        ComplexScoreMatrix matrix = this.computeBestMatrix();
        Alignment align = super.align(matrix, this._threshold);
        GroupAlignment groupAlign = new GroupAlignment(matrix.getSrcColSets(), matrix.getTgtColSets());
        groupAlign.useAlignment(align);
        TableMatchResult tmr = new TableMatchResult();
        tmr.setAlignment(groupAlign);
        return tmr;
    }

    private ComplexScoreMatrix computeBestMatrix() {
        ComplexScoreMatrix startMatrix = this.createStartMatrix();
        MatrixWrapper matrixWrapper = new MatrixWrapper(startMatrix, this, null);
        return this.computeBestMatrix(matrixWrapper);
    }

    private ComplexScoreMatrix computeBestMatrix(MatrixWrapper startMatrixWrapper) {
        MatrixList newMatrices = new MatrixList(this);
        newMatrices.addMatrixWrapper(startMatrixWrapper);
        MatrixWrapper bestMatrix = startMatrixWrapper;
        while (!newMatrices.isEmpty()) {
            MatrixWrapper firstWrapper = newMatrices.bestMatrixWrapper();
            if (firstWrapper != null && bestMatrix.compareTo(firstWrapper) > 0) {
                bestMatrix = firstWrapper;
            }
            ArrayList<MatrixWrapper> matrixWrappers = newMatrices.getMatrixWrappers();
            newMatrices.clear();
            for (MatrixWrapper wrapper : matrixWrappers) {
                ArrayList childMatrices = this.childMatrices(wrapper);
                newMatrices.addMatrixWrappers(childMatrices);
            }
        }
        return bestMatrix.getMatrix();
    }

    private ComplexScoreMatrix createStartMatrix() {
        ColSet[] srcSets = new ColSet[this._srcTable.numColumns()];
        for (int i = 0; i < srcSets.length; ++i) {
            ColSet set;
            srcSets[i] = set = new ColSet(this._srcTable.getColumn(i + 1));
        }
        ColSet[] tgtSets = new ColSet[this._tgtTable.numColumns()];
        for (int i = 0; i < tgtSets.length; ++i) {
            ColSet set;
            tgtSets[i] = set = new ColSet(this._tgtTable.getColumn(i + 1));
        }
        ComplexScoreMatrix matrix = this.createStartMatrix(srcSets, tgtSets);
        return matrix;
    }

    private ComplexScoreMatrix createStartMatrix(ColSet[] srcSets, ColSet[] tgtSets) {
        ComplexScoreMatrix matrix = new ComplexScoreMatrix(srcSets, tgtSets);
        for (int i = 0; i < matrix.getSourceLength(); ++i) {
            for (int j = 0; j < matrix.getTargetLength(); ++j) {
                ColSet srcSet = srcSets[i];
                ColSet tgtSet = tgtSets[j];
                matrix.setScore(srcSet, tgtSet, this.avgFieldSim(srcSet, tgtSet));
            }
        }
        return matrix;
    }

    private ArrayList childMatrices(MatrixWrapper matrixWrapper) {
        ArrayList allChildren = null;
        if (matrixWrapper.wasImprovement()) {
            allChildren = this.childrenBySource(matrixWrapper);
            ArrayList<MatrixWrapper> tgtChildren = this.childrenByTarget(matrixWrapper);
            for (MatrixWrapper wrapper : tgtChildren) {
                if (allChildren.contains(wrapper)) continue;
                allChildren.add(wrapper);
            }
        } else {
            MergedColumns mergeInfo = matrixWrapper.getMergeInfo();
            allChildren = mergeInfo.isSource() ? this.childrenBySource(matrixWrapper) : this.childrenByTarget(matrixWrapper);
        }
        return allChildren;
    }

    private ArrayList childrenBySource(MatrixWrapper matrixWrapper) {
        ComplexScoreMatrix matrix = matrixWrapper.getMatrix();
        int srcLength = matrix.getSourceLength();
        int tgtLength = matrix.getTargetLength();
        ArrayList<MatrixWrapper> srcChildren = new ArrayList<MatrixWrapper>();
        ArrayList newTargetGroups = new ArrayList();
        for (int src = 1; src <= srcLength; ++src) {
            ColSet srcColSet = matrix.getSrcColumns(src);
            TreeSet<PartnerColSet> partnerList = this.createPartners(matrix, src, true);
            for (PartnerColSet partner : partnerList) {
                for (int i = 1; i <= tgtLength; ++i) {
                    boolean found;
                    double newSim;
                    ArrayList<MatrixWrapper> srcChildrenCopy;
                    MatrixWrapper curWrapper;
                    Iterator mIter;
                    if (partner.getPosition() == i) continue;
                    ColSet cs = matrix.getTgtColumns(i);
                    ColSet newColSet = ColSet.merge(partner.getColSet(), cs);
                    if (newTargetGroups.contains(newColSet) || !((newSim = this.avgFieldSim(srcColSet, newColSet)) > partner.getScore())) continue;
                    ComplexScoreMatrix newScoreMatrix = matrix.mergeTargetColumns(partner.getColSet(), cs);
                    for (int srcInx = 1; srcInx <= srcLength; ++srcInx) {
                        ColSet curSrcSet = newScoreMatrix.getSrcColumns(srcInx);
                        newScoreMatrix.setScore(curSrcSet, newColSet, this.avgFieldSim(curSrcSet, newColSet));
                    }
                    MatrixWrapper newMatrixWrapper = new MatrixWrapper(newScoreMatrix, this, matrixWrapper);
                    MergedColumns mergeInfo = new MergedColumns(partner.getColSet(), cs, newColSet, false);
                    newMatrixWrapper.setMergeInfo(mergeInfo);
                    newMatrixWrapper.setInfo(this.makeInfo(srcColSet, partner.getColSet(), cs));
                    double oldMatchContrib = this.matchContribution(matrix, matrixWrapper.getAlignment(), new ColSet[]{partner.getColSet(), cs}, false);
                    double newMatchContrib = this.matchContribution(newScoreMatrix, newMatrixWrapper.getAlignment(), new ColSet[]{newColSet}, false);
                    newMatrixWrapper.addToInfo(" Old:" + oldMatchContrib + ", New:" + newMatchContrib);
                    if (this.isImprovement(newMatrixWrapper)) {
                        if (!srcChildren.contains(newMatrixWrapper)) {
                            srcChildren.add(newMatrixWrapper);
                            continue;
                        }
                        srcChildrenCopy = srcChildren;
                        srcChildren = new ArrayList(srcChildrenCopy.size());
                        mIter = srcChildrenCopy.iterator();
                        found = false;
                        while (mIter.hasNext()) {
                            curWrapper = (MatrixWrapper)mIter.next();
                            if (curWrapper.equals(newMatrixWrapper)) {
                                if (!curWrapper.wasImprovement()) continue;
                                found = true;
                                srcChildren.add(curWrapper);
                                continue;
                            }
                            srcChildren.add(curWrapper);
                        }
                        if (found) continue;
                        srcChildren.add(newMatrixWrapper);
                        continue;
                    }
                    if (!matrixWrapper.wasImprovement()) continue;
                    newMatrixWrapper.setImprovement(false);
                    srcChildrenCopy = srcChildren;
                    srcChildren = new ArrayList(srcChildrenCopy.size());
                    mIter = srcChildrenCopy.iterator();
                    found = false;
                    while (mIter.hasNext()) {
                        curWrapper = (MatrixWrapper)mIter.next();
                        if (curWrapper.equals(newMatrixWrapper)) {
                            if (curWrapper.wasImprovement()) {
                                found = true;
                                srcChildren.add(curWrapper);
                                continue;
                            }
                            srcChildren.add(curWrapper);
                            if (!curWrapper.getMergeInfo().equals(newMatrixWrapper.getMergeInfo())) continue;
                            found = true;
                            continue;
                        }
                        srcChildren.add(curWrapper);
                    }
                    if (found) continue;
                    srcChildren.add(newMatrixWrapper);
                }
            }
        }
        return srcChildren;
    }

    private ArrayList childrenByTarget(MatrixWrapper matrixWrapper) {
        ComplexScoreMatrix matrix = matrixWrapper.getMatrix();
        int srcLength = matrix.getSourceLength();
        int tgtLength = matrix.getTargetLength();
        ArrayList<MatrixWrapper> tgtChildren = new ArrayList<MatrixWrapper>();
        ArrayList newSourceGroups = new ArrayList();
        for (int tgt = 1; tgt <= tgtLength; ++tgt) {
            ColSet tgtColSet = matrix.getTgtColumns(tgt);
            TreeSet<PartnerColSet> partnerList = this.createPartners(matrix, tgt, false);
            for (PartnerColSet partner : partnerList) {
                for (int i = 1; i <= srcLength; ++i) {
                    boolean found;
                    double newSim;
                    ArrayList<MatrixWrapper> tgtChildrenCopy;
                    MatrixWrapper curWrapper;
                    Iterator mIter;
                    if (partner.getPosition() == i) continue;
                    ColSet cs = matrix.getSrcColumns(i);
                    ColSet newColSet = ColSet.merge(partner.getColSet(), cs);
                    if (newSourceGroups.contains(newColSet) || !((newSim = this.avgFieldSim(newColSet, tgtColSet)) > partner.getScore()) || !(newSim > matrix.getScoreValue(i, tgt))) continue;
                    ComplexScoreMatrix newScoreMatrix = matrix.mergeSourceColumns(partner.getColSet(), cs);
                    for (int tgtInx = 1; tgtInx <= tgtLength; ++tgtInx) {
                        ColSet curTgtSet = newScoreMatrix.getTgtColumns(tgtInx);
                        newScoreMatrix.setScore(newColSet, curTgtSet, this.avgFieldSim(newColSet, curTgtSet));
                    }
                    MatrixWrapper newMatrixWrapper = new MatrixWrapper(newScoreMatrix, this, matrixWrapper);
                    MergedColumns mergeInfo = new MergedColumns(partner.getColSet(), cs, newColSet, true);
                    newMatrixWrapper.setMergeInfo(mergeInfo);
                    newMatrixWrapper.setInfo(this.makeInfo(tgtColSet, partner.getColSet(), cs));
                    double oldMatchContrib = this.matchContribution(matrix, matrixWrapper.getAlignment(), new ColSet[]{partner.getColSet(), cs}, true);
                    double newMatchContrib = this.matchContribution(newScoreMatrix, newMatrixWrapper.getAlignment(), new ColSet[]{newColSet}, true);
                    newMatrixWrapper.addToInfo(" Old:" + oldMatchContrib + ", New:" + newMatchContrib);
                    if (this.isImprovement(newMatrixWrapper)) {
                        if (!tgtChildren.contains(newMatrixWrapper)) {
                            tgtChildren.add(newMatrixWrapper);
                            continue;
                        }
                        tgtChildrenCopy = tgtChildren;
                        tgtChildren = new ArrayList(tgtChildrenCopy.size());
                        mIter = tgtChildrenCopy.iterator();
                        found = false;
                        while (mIter.hasNext()) {
                            curWrapper = (MatrixWrapper)mIter.next();
                            if (curWrapper.equals(newMatrixWrapper)) {
                                if (!curWrapper.wasImprovement()) continue;
                                found = true;
                                tgtChildren.add(curWrapper);
                                continue;
                            }
                            tgtChildren.add(curWrapper);
                        }
                        if (found) continue;
                        tgtChildren.add(newMatrixWrapper);
                        continue;
                    }
                    if (!matrixWrapper.wasImprovement()) continue;
                    newMatrixWrapper.setImprovement(false);
                    tgtChildrenCopy = tgtChildren;
                    tgtChildren = new ArrayList(tgtChildrenCopy.size());
                    mIter = tgtChildrenCopy.iterator();
                    found = false;
                    while (mIter.hasNext()) {
                        curWrapper = (MatrixWrapper)mIter.next();
                        if (curWrapper.equals(newMatrixWrapper)) {
                            if (curWrapper.wasImprovement()) {
                                found = true;
                                tgtChildren.add(curWrapper);
                                continue;
                            }
                            tgtChildren.add(curWrapper);
                            if (!curWrapper.getMergeInfo().equals(newMatrixWrapper.getMergeInfo())) continue;
                            found = true;
                            continue;
                        }
                        tgtChildren.add(curWrapper);
                    }
                    if (found) continue;
                    tgtChildren.add(newMatrixWrapper);
                }
            }
        }
        return tgtChildren;
    }

    private TreeSet createPartners(ComplexScoreMatrix matrix, int index, boolean isSource) {
        ColSet colSet = null;
        int otherSize = -1;
        if (isSource) {
            colSet = matrix.getSrcColumns(index);
            otherSize = matrix.getTargetLength();
        } else {
            colSet = matrix.getTgtColumns(index);
            otherSize = matrix.getSourceLength();
        }
        TreeSet<PartnerColSet> partnerList = new TreeSet<PartnerColSet>();
        for (int pIndex = 1; pIndex <= otherSize; ++pIndex) {
            ColSet pSet = null;
            PartnerColSet partner = null;
            double score = -1.0;
            if (isSource) {
                pSet = matrix.getTgtColumns(pIndex);
                score = matrix.getScoreValue(index, pIndex);
                partner = new PartnerColSet(pIndex, pSet, score);
            } else {
                pSet = matrix.getSrcColumns(pIndex);
                score = matrix.getScoreValue(pIndex, index);
                partner = new PartnerColSet(pIndex, pSet, score);
            }
            if (!(score > 0.2)) continue;
            if (partnerList.size() < this._numPartners) {
                partnerList.add(partner);
                continue;
            }
            PartnerColSet listPartner = (PartnerColSet)partnerList.last();
            if (!(listPartner.getScore() < score)) continue;
            partnerList.remove(listPartner);
            partnerList.add(partner);
        }
        return partnerList;
    }

    private boolean isImprovement(MatrixWrapper matrixWrapper) {
        MatrixWrapper parentWrapper = matrixWrapper.getParent();
        if (parentWrapper.wasImprovement()) {
            return this.improvesParent(matrixWrapper);
        }
        return this.improvesGrandParent(matrixWrapper);
    }

    private boolean improvesParent(MatrixWrapper wrapper) {
        MatrixWrapper parentWrapper = wrapper.getParent();
        double parentScore = parentWrapper.getMatchScore();
        double score = wrapper.getMatchScore();
        return !(score <= parentScore);
    }

    private boolean improvesGrandParent(MatrixWrapper wrapper) {
        MatrixWrapper parentWrapper = wrapper.getParent();
        ComplexScoreMatrix matrix = wrapper.getMatrix();
        MergedColumns mergeInfo = wrapper.getMergeInfo();
        MatrixWrapper grandParentWrapper = parentWrapper.getParent();
        if (grandParentWrapper == null) {
            return false;
        }
        MergedColumns parentMergeInfo = parentWrapper.getMergeInfo();
        ComplexScoreMatrix gpMatrix = grandParentWrapper.getMatrix();
        MergedColumns srcMerge = null;
        MergedColumns tgtMerge = null;
        if (mergeInfo.isSource() && !parentMergeInfo.isSource()) {
            srcMerge = mergeInfo;
            tgtMerge = parentMergeInfo;
        } else if (!mergeInfo.isSource() && parentMergeInfo.isSource()) {
            srcMerge = parentMergeInfo;
            tgtMerge = mergeInfo;
        } else {
            return false;
        }
        ColSet srcCol1 = srcMerge.getFirst();
        ColSet srcCol2 = srcMerge.getSecond();
        ColSet srcMergedCols = srcMerge.getMerged();
        ColSet tgtCol1 = tgtMerge.getFirst();
        ColSet tgtCol2 = tgtMerge.getSecond();
        ColSet tgtMergedCols = tgtMerge.getMerged();
        double val1 = gpMatrix.getScore(srcCol1, tgtCol1) + 0.1;
        double val2 = gpMatrix.getScore(srcCol1, tgtCol2) + 0.1;
        double val3 = gpMatrix.getScore(srcCol2, tgtCol1) + 0.1;
        double val4 = gpMatrix.getScore(srcCol2, tgtCol2) + 0.1;
        double combVal = matrix.getScore(srcMergedCols, tgtMergedCols);
        return !(combVal <= val1 || combVal <= val2 || combVal <= val3) && !(combVal <= val4);
    }

    private String makeInfo(ColSet x, ColSet p, ColSet y) {
        return "X: " + x.toString() + "; P: " + p.toString() + "; Y: " + y.toString();
    }

    private double avgFieldSim(ColSet srcCols, ColSet tgtCols) {
        double score = 0.0;
        int size = this._srcRecords.size();
        for (int i = 0; i < size; ++i) {
            RecordWrapper tgtWrapper;
            RecordWrapper srcWrapper = (RecordWrapper)this._srcRecords.get(i);
            Double intScore = this.fieldSim(srcWrapper, srcCols, tgtWrapper = (RecordWrapper)this._tgtRecords.get(i), tgtCols);
            if (intScore == null) continue;
            score += intScore.doubleValue();
        }
        return score /= (double)size;
    }

    private Double fieldSim(RecordWrapper srcWrapper, ColSet srcCols, RecordWrapper tgtWrapper, ColSet tgtCols) {
        String string1 = this.makeString(srcWrapper, srcCols, this._srcTable);
        String string2 = this.makeString(tgtWrapper, tgtCols, this._tgtTable);
        if (string1 == null || string2 == null) {
            return null;
        }
        double fieldSim = this._fieldCompare.score(string1, string2);
        return new Double(fieldSim);
    }

    private String makeString(RecordWrapper wrapper, ColSet colSet, Table tab) {
        StringBuffer stringBuf = new StringBuffer();
        boolean notNull = false;
        for (int i = 1; i <= colSet.numColumns(); ++i) {
            Column col = colSet.getColumn(i);
            int colPos = tab.getColumnPosition(col);
            StringWrapper stringWrap = wrapper.getFieldWrapper(colPos);
            if (stringWrap == null) continue;
            notNull = true;
            if (stringBuf.length() > 0) {
                stringBuf.append(" ");
            }
            String val = stringWrap.getString();
            stringBuf.append(val);
        }
        if (notNull) {
            return stringBuf.toString();
        }
        return null;
    }

    private double gmAvg(ComplexScoreMatrix matrix) {
        Alignment align = this.createAlignment(matrix);
        return this.gmAvg(matrix, align);
    }

    public double gmAvg(ComplexScoreMatrix matrix, Alignment align) {
        double matchScore = 0.0;
        int count = 0;
        for (int i = 1; i <= align.getSourceSize(); ++i) {
            matchScore += align.getScore(i);
            ++count;
        }
        return matchScore /= (double)count;
    }

    public double gmSum(ComplexScoreMatrix matrix, Alignment align) {
        double sum = 0.0;
        for (int i = 1; i <= align.getSourceSize(); ++i) {
            double score = align.getScore(i);
            sum += score;
        }
        return sum;
    }

    public double corSum(ComplexScoreMatrix matrix, Alignment align) {
        double corSum = 0.0;
        for (int i = 1; i <= align.getSourceSize(); ++i) {
            double score = align.getScore(i);
            if (!(score > this.getThreshold())) continue;
            corSum += align.getScore(i);
        }
        return corSum;
    }

    public double corAvg(ComplexScoreMatrix matrix, Alignment align) {
        double corSum = 0.0;
        int count = 0;
        for (int i = 1; i <= align.getSourceSize(); ++i) {
            double score = align.getScore(i);
            if (!(score > this.getThreshold())) continue;
            ++count;
            corSum += align.getScore(i);
        }
        double corAvg = count == 0 ? 0.0 : corSum / (double)count;
        return corAvg;
    }

    public double weightedCorSum(ComplexScoreMatrix matrix, Alignment align) {
        double corSum = 0.0;
        for (int i = 1; i <= align.getSourceSize(); ++i) {
            double score = align.getScore(i);
            if (!(score > this.getThreshold())) continue;
            ColSet srcColSet = matrix.getSrcColumns(i);
            int tgtIndex = align.getSourceAlignment(i);
            ColSet tgtColSet = matrix.getTgtColumns(tgtIndex);
            int weight = srcColSet.numColumns() + tgtColSet.numColumns();
            double wScore = (double)weight * score;
            corSum += wScore;
        }
        return corSum;
    }

    private double matchContribution(ComplexScoreMatrix matrix, Alignment align, ColSet[] colSets, boolean isSource) {
        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < colSets.length; ++i) {
            ColSet colSet = colSets[i];
            double score = 0.0;
            int srcIndex = -1;
            int tgtIndex = -1;
            if (isSource) {
                srcIndex = matrix.getSrcIndex(colSet);
                if (align.hasSourceAlignment(srcIndex)) {
                    tgtIndex = align.getSourceAlignment(srcIndex);
                }
            } else {
                tgtIndex = matrix.getTgtIndex(colSet);
                if (align.hasTargetAlignment(tgtIndex)) {
                    srcIndex = align.getTargetAlignment(tgtIndex);
                }
            }
            if (srcIndex == -1 || tgtIndex == -1) continue;
            score = matrix.getScore(srcIndex, tgtIndex);
            sum += score;
            ++count;
        }
        double matchScore = this.gmSum(matrix, align);
        double contribution = sum / matchScore;
        return contribution;
    }

    public Alignment createAlignment(ComplexScoreMatrix matrix) {
        return this.getGraphMatcher().match(matrix);
    }

    public double getThreshold() {
        return this._threshold;
    }

    public static class PartnerColSet
    implements Comparable {
        private int _pos = 0;
        private ColSet _colSet = null;
        private double _score = 0.0;

        public PartnerColSet(int pos, ColSet colSet, double score) {
            this._pos = pos;
            this._colSet = colSet;
            this._score = score;
        }

        public int getPosition() {
            return this._pos;
        }

        public ColSet getColSet() {
            return this._colSet;
        }

        public double getScore() {
            return this._score;
        }

        public int compareTo(Object obj) {
            PartnerColSet other = (PartnerColSet)obj;
            double otherScore = other.getScore();
            if (otherScore > this.getScore()) {
                return 1;
            }
            if (otherScore < this.getScore()) {
                return -1;
            }
            return 0;
        }

        public String toString() {
            return "[" + this.getColSet().toString() + "," + this.getScore() + "]";
        }
    }

}

