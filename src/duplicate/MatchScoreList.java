package duplicate;

import java.util.ArrayList;
import java.util.Iterator;

import datastructure.MatchScore;
import db.RecordId;

public class MatchScoreList {
	private ArrayList matchScores = null;

	public MatchScoreList(int initialSize){
		this.matchScores = new ArrayList(initialSize);
	}

	/**
	 * Add MatchScore into list
	 * @param score
	 * 			MatchScore
	 * **/
	public void add(MatchScore score){
		this.matchScores.add(score);
	}

	/**
	 * @return ArrayList<MatchScore>
	 * */
	public ArrayList getArrayList(){
		return this.matchScores;
	}


	public boolean hasSourceRecord(RecordId id){
		Iterator iter = this.matchScores.iterator();
		while (iter.hasNext()){
			MatchScore score = (MatchScore)iter.next();
			if ( ( score).getSourceId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasTargetRecord(RecordId id) {
		Iterator iter = this.matchScores.iterator();
		while (iter.hasNext())
		{
			MatchScore score = (MatchScore)iter.next();
			if (score.getTargetId().equals(id)) {
				return true;
			}
		}
		return false;
	}
}
