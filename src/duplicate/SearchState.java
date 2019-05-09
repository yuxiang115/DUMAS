package duplicate;

import java.util.ArrayList;

import db.RecordId;
import token.Token;

public class SearchState implements Comparable{
	private Double value = null;
	private RecordId source = null;
	private RecordId target = null;
	private ArrayList exclusions = new ArrayList();
	private ArrayList<Token> tokens;

	public SearchState(RecordId src, double value, ArrayList<Token> sortedTokens){
		setSource(src);
		setValue(value);
		this.tokens = sortedTokens;
	}

	private void setSource(RecordId src){
		this.source = src;
	}

	public RecordId getSource(){
		return this.source;
	}

	public RecordId getTarget(){
		return this.target;
	}

	public void setTarget(RecordId target){
		this.target = target;
	}

	public Double getValue(){
		return this.value;
	}

	public void setValue(double value){
		this.value = new Double(value);
	}

	public Token removeFirstToken(){
		if (this.tokens.size() == 0) {
			return null;
		}
		return (Token)this.tokens.remove(0);
	}

	public Token[] getTokens(){
		return (Token[])this.tokens.toArray(new Token[tokens.size()]);
	}

	public Token[] exclusions(){
		return (Token[])this.exclusions.toArray(new Token[exclusions.size()]);
	}

	public void addExclusion(Token tok){
		this.exclusions.add(tok);
	}

	public int compareTo(Object obj){
		SearchState state = (SearchState)obj;
		return this.value.compareTo(state.getValue());
	}

	public String toString(){
		return this.value.toString();
	}
}
