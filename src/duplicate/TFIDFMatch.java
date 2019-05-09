package duplicate;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import db.RecordId;
import db.RecordIterator;
import db.Table;
import method.DUMAS;
import string.BagOfTokens;
import string.EditDistance;
import string.StringDistance;
import string.StringWrapper;
import token.SimpleTokenizer;
import token.Token;
import datastructure.Alignment;
import datastructure.MatchScore;
import datastructure.ScoreMatrix;
import dumasException.DumasException;
import index.InvertedIndex;

public class TFIDFMatch extends AbstractRecordMatch{
	protected StringCompare fieldCompare = null;
	protected FieldMatch fieldMatch = null;
	private PriorityQueue queue = null;
	private boolean collectStatistics = true;
	private int wantedDuplicates = -1;

	private static final int MINNUMDUPLICATES = 3;
	private static final int NUMDUPLICATES = 10;
	private static final double DUPTHRESHOLD = 1.0;
	
	private int numduplicates = 10;
	private double dupthreshold = 1.0;
	private double tokenThres = .05D;
	
	public static final StringCompare DEFAULT_COMPARE = new TFIDFCompare();
	public static final StringCompare DEFAULT_FIELDCOMPARE = new SoftTFIDFCompare(
			SimpleTokenizer.DEFAULT_TOKENIZER, new EditDistance(), .05D);

	public TFIDFMatch(boolean cacheRecords){
		super(DEFAULT_COMPARE);
		this.fieldCompare = DEFAULT_FIELDCOMPARE;
		this.fieldMatch = new FieldMatch(this.fieldCompare);
	}

	public void setNumduplicates(int num){
		this.numduplicates = num;
	}
	
	public void setDupthreshold(double threshold){
		this.dupthreshold = threshold;
	}
	
	public void setTokenThres(double var){
		this.tokenThres = var;
		this.fieldCompare = new SoftTFIDFCompare(SimpleTokenizer.DEFAULT_TOKENIZER, new EditDistance(), this.tokenThres);
		this.fieldMatch = new FieldMatch(this.fieldCompare);
	}
	
	/**
	 * loading source table and target table in duplicate dection
	 *@param source Table
	 *@param target Table
	 * */
	public void load(Table source, Table target){
		load(source, source.recordIterator(), target, target.recordIterator());
	}
	
	public void load(Table source, RecordIterator sourceSample, Table target, RecordIterator targetSample){
		Logger logger = DUMAS.getLogger();
		String className = getClass().getName();
		String methodName = "load";

		long start = 0L;
		long end = 0L;

		logger.logp(Level.FINE, className, methodName, 
				"Start accumulating statistics.");
		start = System.currentTimeMillis();

		loadSource(source, sourceSample);
		logger.logp(Level.FINE, className, methodName, 
				"Statistics for first database collected.");

		loadTarget(target, targetSample);
		logger.logp(Level.FINE, className, methodName, 
				"Statistics for second databases collected.");

		end = System.currentTimeMillis();
		logger.logp(Level.FINE, className, methodName, 
				"Finished accumulating statistics in " + (end - start) + 
				" ms.");
	}

	/**Loading source table
	 * @param source Table to be loaded
	 * @param RecordIterator
	 * */
	public void loadSource(Table table, RecordIterator sample){
		if (this.collectStatistics) {
			this.compare.preprocessSource(sample);
		}
		this.fieldCompare.useStatisticsForSource(this.compare.getSourceStatistics());
		setSourceWrapper(new TableWrapper(this.compare.getSourceDistance(), 
				this.fieldCompare.getSourceDistance(), table));
	}

	/**Loading target table
	 * @param taget Table to be loaded
	 * @param RecordIterator
	 * */
	public void loadTarget(Table table, RecordIterator sample){
		if (this.collectStatistics) {
			this.compare.preprocessTarget(sample);
		}
		this.fieldCompare.useStatisticsForTarget(this.compare.getTargetStatistics());
		setTargetWrapper(new TableWrapper(this.compare.getTargetDistance(), 
				this.fieldCompare.getTargetDistance(), table));
	}

	public Collection match(){
		return match(null, null, null);
	}
	/**
	 * start to match two tables
	 * @param source
	 * 			RecordId's Collection
	 * @param target
	 * 			 RecordId's Collection
	 * @param Alignment
	 * */
	public Collection match(Collection source, Collection target, Alignment alignment){
		Logger logger = DUMAS.getLogger();
		String className = this.getClass().getName();
		String methodName = "match";
		TableWrapper sourceDB = this.getSourceWrapper();
		TableWrapper targetDB = this.getTargetWrapper();
		int numSourceRecords = sourceDB.numRecords();
		if (numSourceRecords == 0) {
			throw new DumasException("Source database does not contain any tuples.");
		}
		int numTargetRecords = targetDB.numRecords();
		if (numTargetRecords == 0) {
			throw new DumasException("Target database does not contain any tuples.");
		}
		logger.logp(Level.FINE, className, methodName, "Preparing wrappers.");
		sourceDB.prepareWrappers(source);
		targetDB.prepareWrappers(target);
		if (this.getWantedDuplicates() <= 0) {
			int minRecords = Math.min(numSourceRecords, numTargetRecords);
			int wantedDuplicates = 1 + Math.min(minRecords / numduplicates, numduplicates - 1);
			wantedDuplicates = Math.max(wantedDuplicates, 3);
			this.setWantedDuplicates(wantedDuplicates);
		}
		if (alignment != null) {
			throw new DumasException("TFIDFMatch does not support known alignment.");
		}
		logger.logp(Level.FINE, className, methodName, "Start the matching process.");
		return this.matchPrepared(sourceDB, targetDB);
	}


	public Collection matchPrepared(TableWrapper source, TableWrapper target){
		Logger logger = DUMAS.getLogger();
		String className = getClass().getName();
		String methodName = "matchPrepared";

		this.queue = new PriorityQueue(3 * source.size());

		logger.logp(Level.FINE, className, methodName, 
				"Creating index for second database.");

		InvertedIndex invertedIndex = createInvertedIndex(target);

		Collection results = whirlSearch(source, target, invertedIndex, this.queue, 
				this.compare);

		return results;
	}

	/**
	 * Adding all token from all records into invertedIndex
	 * @param TableWrapper
	 * @return InvertedIndex
	 * **/
	private InvertedIndex createInvertedIndex(TableWrapper table){
		InvertedIndex invertedIndex = new InvertedIndex();
		Iterator targetIter = table.recordWrapperIterator();

		while(targetIter.hasNext()){
			RecordWrapper recWrapper = (RecordWrapper)targetIter.next();
			BagOfTokens bot = (BagOfTokens)recWrapper.getStringWrapper();
			Iterator tokIter = bot.tokenIterator();
			while(tokIter.hasNext()){
				Token tok = (Token)tokIter.next();
				double weight = bot.getWeight(tok);
				invertedIndex.addPosting(tok, recWrapper.getRecordId(), weight);
			}
		}
		return invertedIndex;
	}
	/**
	 * Records comparisons between source and target
	 * @param source
	 * 			TableWrapper
	 * @param target
	 * 			TableWrapper
	 * @param InvertedIndex
	 * 
	 * @param PriorityQueue
	 * 
	 * @param comparitor
	 * 			StringCompare
	 * **/
	private Collection whirlSearch(TableWrapper source, TableWrapper target, InvertedIndex invertedIndex, PriorityQueue queue, StringCompare compare){
		MatchScoreList result = new MatchScoreList(this.getWantedDuplicates());
		Logger logger = DUMAS.getLogger();
		String className = this.getClass().getName();
		String methodName = "whirlSearch";
		long start = 0L;
		long end = 0L;
		logger.logp(Level.FINE, className, methodName, "Start comparing records.");
		start = System.currentTimeMillis();
		long numComparisons = 0L;
		Iterator iter1 = source.recordWrapperIterator();
		while (iter1.hasNext()) {
			double value = 0.0;
			RecordWrapper sourceRecord = (RecordWrapper)iter1.next();
			BagOfTokens bot = (BagOfTokens)sourceRecord.getStringWrapper();
			Iterator tokIter = bot.tokenIterator();
			while (tokIter.hasNext()) {
				Token tok = (Token)tokIter.next();
				double srcWeight = bot.getWeight(tok);
				double maxWeight = invertedIndex.getMaxWeight(tok);
				value += srcWeight * maxWeight;
			}
			SearchState state = new SearchState(sourceRecord.getRecordId(), value, bot.sortedTokenList());
			queue.insert(state);
		}
		GoalStateList goalStates = new GoalStateList(this.getWantedDuplicates());
		int numGoalStates = 0;
		while (numGoalStates < this.getWantedDuplicates() && !queue.isEmpty()) {
			SearchState state = (SearchState)queue.extractMax();
			RecordId sourceId = state.getSource();
			RecordWrapper sourceRecord = source.getRecordWrapper(sourceId);
			BagOfTokens sourceBot = (BagOfTokens)sourceRecord.getStringWrapper();
			if (state.getTarget() != null) {
				RecordWrapper targetRecord = target.getRecordWrapper(state.getTarget());
				if (result.hasSourceRecord(state.getSource()) || result.hasTargetRecord(state.getTarget())) continue;
				MatchScore matchScore = new MatchScore(state.getSource(), state.getTarget());
				matchScore.setScore(state.getValue());
				matchScore.setSourceWrapper(sourceRecord);
				matchScore.setTargetWrapper(targetRecord);
				result.add(matchScore);
				++numGoalStates;
				continue;
			}
			Token tok = state.removeFirstToken();
			if (tok == null) continue;
			Token[] exclusions = state.exclusions();
			List postings = invertedIndex.getPostings(tok);
			RecordId[] targetIds = new RecordId[postings.size()];
			for (int i = 0; i < postings.size(); ++i) {
				targetIds[i] = (RecordId)postings.get(i);
			}
			RecordWrapper[] targetWrappers = target.getRecordWrappers(targetIds);
			for (int i = 0; i < targetWrappers.length; ++i) {
				RecordWrapper targetRecord = targetWrappers[i];
				BagOfTokens targetBot = (BagOfTokens)targetRecord.getStringWrapper();
				boolean containsExclusion = false;
				for (int x = 0; x < exclusions.length; ++x) {
					if (!targetBot.contains(exclusions[x])) continue;
					containsExclusion = true;
				}
				if (containsExclusion) continue;
				double score = compare.score(sourceRecord.getStringWrapper(), targetRecord.getStringWrapper());
				++numComparisons;
				if (!goalStates.couldInsert(score) || numGoalStates >= this.getWantedDuplicates()) continue;
				SearchState newState = new SearchState(sourceId, score, null);
				newState.setTarget(targetRecord.getRecordId());
				goalStates.insert(newState);
				if (score > this.dupthreshold) {
					if (result.hasSourceRecord(sourceId) || result.hasTargetRecord(targetRecord.getRecordId())) continue;
					MatchScore matchScore = new MatchScore(sourceId, targetRecord.getRecordId());
					matchScore.setScore(score);
					matchScore.setSourceWrapper(sourceRecord);
					matchScore.setTargetWrapper(targetRecord);
					result.add(matchScore);
					++numGoalStates;
					continue;
				}
				queue.insert(newState);
			}
			state.addExclusion(tok);
			Token[] otherTokens = state.getTokens();
			double newValue = 0.0;
			for (int i = 0; i < otherTokens.length; ++i) {
				Token curTok = otherTokens[i];
				newValue += sourceBot.getWeight(curTok) * invertedIndex.getMaxWeight(curTok);
			}
			if (!goalStates.couldInsert(newValue)) continue;
			state.setValue(newValue);
			queue.insert(state);
		}
		end = System.currentTimeMillis();
		logger.logp(Level.FINE, className, methodName, "> > > Finished comparing records in " + (end - start) + " ms.");
		logger.logp(Level.FINE, className, methodName, "Possible pairs: " + source.size() * target.size());
		logger.logp(Level.FINE, className, methodName, "Number of comparisons: " + numComparisons);
		return result.getArrayList();
	}

	/**
	 * Compare two records to get score of similar
	 * @param source record
	 * 			RecordWrapper
	 * @param target record
	 * 			RecordWrapper
	 * @param Alignment
	 * 
	 * @param if createMatrix
	 * 			boolean
	 * @return MatchScore
	 * **/
	public MatchScore score(RecordWrapper record1, RecordWrapper record2, Alignment alignment, boolean createMatrix){
		if (alignment != null) {
			throw new DumasException("TFIDFMatch does not support known alignment.");
		}
		StringWrapper wrapper1 = record1.getStringWrapper();
		StringWrapper wrapper2 = record2.getStringWrapper();
		MatchScore matchscore = new MatchScore(record1.getRecordId(), record2.getRecordId());
		matchscore.setSourceWrapper(record1);
		matchscore.setTargetWrapper(record2);
		if (alignment == null) {
			matchscore.setScore(this.compare.score(wrapper1, wrapper2));
		} else {
			int normalizer = 0;
			double score = 0.0;
			for (int i = 1; i <= record1.numValues(); ++i) {
				if (!alignment.hasSourceAlignment(i)) continue;
				++normalizer;
				int targetPos = alignment.getSourceAlignment(i);
				StringWrapper srcField = record1.getFieldWrapper(i);
				StringWrapper tgtField = record2.getFieldWrapper(targetPos);
				score += this.fieldCompare.score(srcField, tgtField);
			}
			matchscore.setScore((score += this.compare.score(wrapper1, wrapper2)) / (double)(++normalizer));
		}
		if (createMatrix) {
			ScoreMatrix matrix = this.fieldMatch.compareFields(record1, record2, alignment);
			matchscore.setMatrix(matrix);
		}
		return matchscore;
	}

	/**
	 * set Duplicates needed
	 * @param num of Duplicates
	 * 			int
	 * */
	public void setWantedDuplicates(int numDuplicates){
		this.wantedDuplicates = numDuplicates;
	}

	public int getWantedDuplicates(){
		return this.wantedDuplicates;
	}

	public StringDistance getFieldDistance(){
		return this.fieldCompare.getSourceDistance();
	}
}
