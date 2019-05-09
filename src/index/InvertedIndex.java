package index;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import db.RecordId;
import token.Token;

import java.util.ArrayList;


import java.io.Serializable;
public class InvertedIndex implements Serializable{
	private HashMap index;

	public InvertedIndex(){
		this.index = new HashMap();
	}

	/**
	 * Adding token and linker (RecordID) into hashMap
	 * @param term
	 * 			Token
	 * @param link
	 * 			RecordId
	 * @param weight
	 * 			double
	 * */
	public void addPosting(Token term, RecordId link, double weight){
		if (term == null) {
			throw new NullPointerException("Term is null.");
		}
		if (link == null) {
			throw new NullPointerException("Link for term " + term + 
					" is null.");
		}

		PostingsList list = (PostingsList)this.index.get(term);
		if (list == null){
			list = new PostingsList(link, weight);
			this.index.put(term, list);
		}
		else{
			list.addPosting(link, weight);
		}
	}

	public List getPostings(Token term){
		PostingsList list = (PostingsList)this.index.get(term);
		if (list == null) {
			return new ArrayList();
		}
		return list.getPostings();
	}

	public double getMaxWeight(Token term){
		PostingsList list = (PostingsList)this.index.get(term);
		if (list == null) {
			return 0.0D;
		}
		return list.getMaxWeight();
	}
	
	/**
	 * @return token set
	 * */
	public Set getIndexTerms(){
		return this.index.keySet();
	}

	private class PostingsList implements Serializable{
		private ArrayList list;
		private double maxWeight;

		private PostingsList(RecordId link, double weight){
			this.list = new ArrayList();
			this.list.add(link);
			this.maxWeight = weight;
		}

		private void addPosting(RecordId link, double weight){
			this.list.add(link);
			if (weight > this.maxWeight) {
				this.maxWeight = weight;
			}
		}

		private List getPostings(){
			return this.list;
		}

		private double getMaxWeight(){
			return this.maxWeight;
		}
	}
}
