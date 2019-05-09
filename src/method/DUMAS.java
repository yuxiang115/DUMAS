package method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import complexmatching.ComplexTableMatch;
import datastructure.Alignment;
import datastructure.MatchScore;
import datastructure.TableMatchResult;
import db.Record;
import db.RecordId;
import db.Table;
import dumasException.DumasException;
import duplicate.TFIDFMatch;
import postgredb.PostgreDatabase;
import postgredb.PostgreRecord;
import postgredb.PostgreTable;
import tableModel.QueryTableModel;
import tablemaching.SchemaMatch;
import util.StandardLoggingHandler;

public class DUMAS {
	private PostgreDatabase db1;
	private PostgreDatabase db2;
	private String duplicatesRes;
	private String schemaRes;
	private int numDuplicates = 0;
	private static Logger _logger = null;
	private static final Level LOGLEVEL = Level.SEVERE;
	
	private int numWantedDuplicates = 10;
	private double tokenThreshold = 0.5;
	private double dupTreshold = 1.0;
	
	
	
	public DUMAS(){
		db1 = new PostgreDatabase();
		db2 = new PostgreDatabase();
	}

	public DUMAS(QueryTableModel tableModel1, QueryTableModel tableModel2){
		db1 = new PostgreDatabase(tableModel1.getName());
		db2 = new PostgreDatabase(tableModel2.getName());
		this.setTables(tableModel1, tableModel2);
		
	}

	public void setTables(QueryTableModel tableModel1, QueryTableModel tableModel2){
		this.setFirstTable(tableModel1);
		this.setSecondTable(tableModel2);

	}

	public void setFirstTable(QueryTableModel tableModel1){
		try
		{
			System.out.println("First database: " + tableModel1.getName());
			db1.load(tableModel1, "KeyCol", "RWOId");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DumasException("Database cannot be loaded from TABLE " + 
					tableModel1.getName() + ".");
		}


	}

	public void setSecondTable(QueryTableModel tableModel2){
		try
		{
			System.out.println("Second database: " + tableModel2.getName());
			db2.load(tableModel2, "KeyCol", "RWOId");
		} catch (Exception ex) {
			throw new DumasException("Database cannot be loaded from file " + 
					tableModel2.getName() + ".");
		}
	}


	public Table getFirstTable(){
		return this.db1.getTable();
	}

	public Table getSecondTable(){
		return this.db2.getTable();
	}
	
	public void setNumWantedDuplicates(int num){
		this.numWantedDuplicates = num;
	}
	
	public void setTokenTreshold(double var){
		this.tokenThreshold = var;
	}
	
	public void setDupTreshold(double var){
		this.dupTreshold = var;
	}
	
	
	
	
	public Vector<String[]> compareTables(Table sourceTable, Table targetTable){
		long startTime = System.nanoTime();

		
		
		TFIDFMatch recordmatch = new TFIDFMatch(true);
		recordmatch.setTokenThres(tokenThreshold);
		recordmatch.setNumduplicates(numWantedDuplicates);
		recordmatch.setDupthreshold(dupTreshold);
		
		Alignment alignment = null;
		
		recordmatch.load((PostgreTable)sourceTable, (PostgreTable)targetTable);
		ArrayList result = (ArrayList)recordmatch.match();

		SchemaMatch sm = new ComplexTableMatch(sourceTable, targetTable, recordmatch.getFieldDistance());
		StringBuilder duplicatesSB = new StringBuilder("");
		this.numDuplicates = 0;
		ArrayList<Record[]> duplicatesPairs = new ArrayList<Record[]>();

		for (int i = 0; (i < this.numWantedDuplicates) && (i < result.size()); i++){
			MatchScore matchscore = (MatchScore)result.get(i);
			duplicatesSB.append("Pair ");
			duplicatesSB.append(String.valueOf(i + 1));
			duplicatesSB.append(": ");
			duplicatesSB.append(matchscore.getScore());
			duplicatesSB.append("\n");
			RecordId srcId = matchscore.getSourceId();
			Record src = sourceTable.getRecord(srcId);
			RecordId tgtId = matchscore.getTargetId();
			Record tgt = targetTable.getRecord(tgtId);
			duplicatesSB.append(src.toString());
			duplicatesSB.append("\n");
			duplicatesSB.append(tgt.toString());
			duplicatesSB.append("\n");
			numDuplicates++;
			Record[] pair = new Record[2];
			pair[0] = src;
			pair[1] = tgt;
			duplicatesPairs.add(pair);
			sm.addDuplicate(matchscore);
		}
		this.duplicatesRes = duplicatesSB.toString();

		TableMatchResult smRes = sm.match();
		Alignment newAlign = smRes.getAlignment();
		this.schemaRes = "";
		if (newAlign.isComplex()) {
			this.schemaRes = newAlign.toString();
		}
		else{
			for (int i = 1; i <= sourceTable.numColumns(); i++){
				StringBuffer line = new StringBuffer(20);
				if (newAlign.hasSourceAlignment(i)){
					Integer tgtCol = newAlign.getSourceAlignment(i);
					line.append(i + "->" + tgtCol + "(");
					line.append(sourceTable.getColumn(i).getName());
					line.append("->");
					line.append(
							targetTable.getColumn(tgtCol.intValue()).getName());
					line.append(") ");
					line.append(newAlign.getScore(i));
				}
				else{
					line.append("Unaligned: " + i + "(");
					line.append(sourceTable.getColumn(i).getName());
					line.append(")");
				}
				this.schemaRes = line.toString();
			}
		}
		int sourceColumnSize = newAlign.getSourceSize();
		int targetColumnSize = newAlign.getTargetSize();
		ArrayList<int[]> matchedIndexs = new ArrayList<int[]>();
		ArrayList<Integer> unmatchedSourceIndex = new ArrayList<Integer>();
		ArrayList<Integer> unmatchedtargetIndex = new ArrayList<Integer>();

		for(int i = 1; i <= sourceColumnSize; i++){
			if(newAlign.hasSourceAlignment(i)){
				int[] matchIndex = new int[2];
				matchIndex[0] = i;
				matchIndex[1] = newAlign.getSourceAlignment(i);
				matchedIndexs.add(matchIndex);
			}
			else{
				unmatchedSourceIndex.add(i);
			}
		}
		for(int i = 1; i <= targetColumnSize; i++){
			if(!newAlign.hasTargetAlignment(i)){
				unmatchedtargetIndex.add(i);
			}
		}
		
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		String heading1 = "Token Threshold";
		String heading2 = "Duplicate Threshold";
		String heading3 = "Running Time";
		System.out.printf( "%-22s %22s %22s %n", heading1, heading2,heading3);
		System.out.println(this.tokenThreshold + "\t\t\t\t" + this.dupTreshold + "\t\t\t" + totalTime/1000000);

		//System.out.println("Token Threshold: " + this.tokenThreshold + "\t\t" + "Duplicates Threshold: " + this.dupTreshold);
		//System.out.println("DUMAS Integration Time: " + totalTime);
		
		String[] headers = new String[matchedIndexs.size() + unmatchedSourceIndex.size() + unmatchedtargetIndex.size()];
		int i = 0;
		PostgreTable schema1 = (PostgreTable) db1.getTable(null);
		PostgreTable schema2 = (PostgreTable) db2.getTable(null);
		HashMap<Integer, Integer> mapSource = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> mapTarget = new HashMap<Integer, Integer>();

		for(int k = 0; k < matchedIndexs.size(); k++){
			headers[i] = schema1.getColumnName(matchedIndexs.get(k)[0]);
			mapSource.put(matchedIndexs.get(k)[0], i);
			mapTarget.put(matchedIndexs.get(k)[1], i);
			i++;
		}

		for(int k = 0; k < unmatchedSourceIndex.size(); k++){
			headers[i] = schema1.getColumnName(unmatchedSourceIndex.get(k));
			mapSource.put(unmatchedSourceIndex.get(k), i);
			i++;
		}

		for(int k = 0; k < unmatchedtargetIndex.size(); k++){
			headers[i] = schema2.getColumnName(unmatchedtargetIndex.get(k));
			mapTarget.put(unmatchedtargetIndex.get(k), i);
			i++;
		}


		Vector rows = new Vector();
		rows.add(headers);
		Iterator recordIter = db1.recordIterator(null);
		//adding source records
		for(Record[] pair : duplicatesPairs){
			PostgreRecord source = (PostgreRecord) pair[0];
			PostgreRecord target = (PostgreRecord) pair[1];
			String[] row = new String[headers.length];
			for(int j = 0; j < matchedIndexs.size(); j++){
				int fieldIndex = matchedIndexs.get(j)[0];
				row[mapSource.get(fieldIndex)] = source.getValue(fieldIndex);
			}

			for(int j = 0; j < unmatchedSourceIndex.size(); j++){
				int fieldIndex = unmatchedSourceIndex.get(j);
				row[mapSource.get(fieldIndex)] = source.getValue(fieldIndex);
			}
			
			for(int j = 0; j < unmatchedtargetIndex.size(); j++){
				int fieldIndex = unmatchedtargetIndex.get(j);
				row[mapTarget.get(fieldIndex)] = target.getValue(fieldIndex);
			}
			rows.addElement(row);
		}
		


		while(recordIter.hasNext()){
			PostgreRecord record = (PostgreRecord) recordIter.next();			
			if(this.isContainsSource(duplicatesPairs, record))
				continue;
			String[] row = new String[headers.length];
			for(int j = 0; j < matchedIndexs.size(); j++){
				int fieldIndex = matchedIndexs.get(j)[0];
				row[mapSource.get(fieldIndex)] = record.getValue(fieldIndex);
			}

			for(int j = 0; j < unmatchedSourceIndex.size(); j++){
				int fieldIndex = unmatchedSourceIndex.get(j);
				row[mapSource.get(fieldIndex)] = record.getValue(fieldIndex);
			}

			rows.addElement(row);
		}

		recordIter = db2.recordIterator(null);
		while(recordIter.hasNext()){
			PostgreRecord record = (PostgreRecord) recordIter.next();			
			if(this.isContainsTarget(duplicatesPairs, record))
				continue;
			String[] row = new String[headers.length];
			for(int j = 0; j < matchedIndexs.size(); j++){
				int fieldIndex = matchedIndexs.get(j)[1];
				row[mapTarget.get(fieldIndex)] = record.getValue(fieldIndex);
			}

			for(int j = 0; j < unmatchedtargetIndex.size(); j++){
				int fieldIndex = unmatchedtargetIndex.get(j);
				row[mapTarget.get(fieldIndex)] = record.getValue(fieldIndex);
			}

			rows.addElement(row);
		}
		return rows;

	}

	public boolean isContainsSource(ArrayList<Record[]> duplicatesPairs,Record rec){
		for(Record[] target : duplicatesPairs){
			if(target[0].equals(rec))
				return true;
		}

		return false;
	}

	public boolean isContainsTarget(ArrayList<Record[]> duplicatesPairs,Record rec){
		for(Record[] target : duplicatesPairs){
			if(target[1].equals(rec))
				return true;
		}
		return false;
	}
	public String getDuplicatesRes(){
		return this.duplicatesRes;
	}

	public int getNumDuplicates(){
		return this.numDuplicates;
	}

	public String getSchemaRes(){
		return this.schemaRes;
	}

	public Vector getIntegratedData(){
		Iterator recordIter = db1.recordIterator(null);

		PostgreTable schema = (PostgreTable) db1.getTable(null);
		String headers[] = new String[schema.numColumns()];
		for(int i = 0; i < schema.numColumns(); i++){
			headers[i] = schema.getColumnName(i + 1);
		}
		Vector rows = new Vector();
		rows.add(headers);
		while(recordIter.hasNext()){
			PostgreRecord record = (PostgreRecord) recordIter.next();			
			String[] row = new String[schema.numColumns()];
			for(int i = 0; i < schema.numColumns(); i++){
				row[i] = record.getValue(i + 1);
			}
			rows.addElement(row);
		}
		return rows;
	}



	public static Logger getLogger(){
		if (_logger == null) {
			createDefaultLogger();
		}
		return _logger;
	}

	public static void setLogger(Logger logger){
		_logger = logger;
	}

	private static void createDefaultLogger()
	{
		Logger logger = Logger.getLogger("dumas");
		Handler handler = new StandardLoggingHandler();
		handler.setLevel(LOGLEVEL);
		logger.addHandler(handler);
		logger.setLevel(LOGLEVEL);
		logger.setUseParentHandlers(false);
		setLogger(logger);
	}
}