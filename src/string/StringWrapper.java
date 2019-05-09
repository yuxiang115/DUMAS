package string;

import java.io.Serializable;

public class StringWrapper implements Serializable{
	private String str;
	
	public StringWrapper(String str){
		this.str = str;
	}
	
	public String getString(){
		return unwrap();
	}
	
	public String unwrap(){
		return str;
	}
	
	public int hashCode(){
		return str.hashCode();
	}
}
