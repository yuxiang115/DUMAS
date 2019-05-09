package token;

import java.io.Serializable;

public class Token implements Comparable, Serializable{
	private final int index;
	private final String value;
	
	public Token(int index, String value){
		this.index = index;
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public int getIndex(){
		return index;
	}
	
	public int compareTo(Object obj){
		Token t = (Token) obj;
		return index - t.getIndex();
	}
	
	public boolean equals(Object obj){
		if((obj instanceof Token)){
			Token t = (Token)obj;
			return this.getIndex() == t.getIndex();
		}
		return false;
	}
	
	public int hashCode(){
		if(value == null){
			return 0;
		}
		return value.hashCode();
	}
	
	public String toString(){
		return "[tok " + getIndex() + ":" + getValue() + "] ";
	}
}
