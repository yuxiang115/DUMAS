package token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SimpleTokenizer implements Tokenizer{
	public static final SimpleTokenizer DEFAULT_TOKENIZER = new SimpleTokenizer(true, true);
	
	protected boolean ignorePunctuation = true;
	protected boolean ignoreCase = true;
	
	protected Token nullToken = null;
	private int nextId = 0;
	private Map toMap = new TreeMap();
	
	public SimpleTokenizer(boolean ignorePunctuation, boolean ignoreCase){
		this.ignoreCase = ignoreCase;
		this.ignorePunctuation = ignorePunctuation;
		nullToken = new Token(++nextId, null);
	}
	
	public void setIgnorePunctuation(boolean ignore){
		this.ignorePunctuation = ignore;
	}
	
	public void setIgnoreCase(boolean ignore){
		this.ignoreCase = ignore;
	}
	
	public String toString(){
		return "[SimpleTokenizer " + this.ignorePunctuation + ";" + this.ignoreCase + "]";
		
	}
	
	@Override
	public Token intern(String s){
		if(s == null){
			return null;
		}
		
		Token tok = (Token) toMap.get(s);
		if(tok == null){
			tok = new Token(++nextId, s);
			toMap.put(s, tok);
		}
		return tok;
	}
	
	
	private Token internSomething(String s){
		if(s == null) return intern(s);
		return intern(this.ignoreCase ? s.toLowerCase() : s);
	}
	
	@Override
	public Token[] tokenize(String input) {
		if(input == null){
			return new Token[0];
		}
		
		List<Token> tokens = new ArrayList<Token>();
		int cursor = 0;
		while(cursor < input.length()){
			char ch = input.charAt(cursor);
			if(Character.isWhitespace(ch)) cursor++;
			else if(Character.isLetter(ch)){
				StringBuffer buf = new StringBuffer("");
				while((cursor < input.length()) && Character.isLetter(input.charAt(cursor))){
					buf.append(input.charAt(cursor));
					cursor++;
				}
				tokens.add(internSomething(buf.toString()));
			}
			else if(Character.isDigit(ch)){
				StringBuffer buf = new StringBuffer("");
				while((cursor < input.length()) && Character.isDigit(input.charAt(cursor))){
					buf.append(input.charAt(cursor));
					cursor++;
				}
				tokens.add(internSomething(buf.toString()));
			}
			else{
				if(!this.ignorePunctuation){
					StringBuffer buf = new StringBuffer("");
					buf.append(ch);
					String str = buf.toString();
					tokens.add(internSomething(str));
				}
				cursor++;
			}
		}
		Token[] list = new Token[tokens.size()];
		for(int i = 0; i < list.length; i++){
			list[i] = tokens.get(i);
		}
		
		return  tokens.toArray(new Token [tokens.size()]);
	}

	@Override
	public Token[] tokenize(String[] input) {
		ArrayList<Token> tokens = new ArrayList<Token>();
		for(int i = 0; i < input.length; i++){
			Token[] part = tokenize(input[i]);
			for (int p = 0; p < part.length; p++) {
				tokens.add(part[p]);
			}
		}
		return tokens.toArray(new Token [tokens.size()]);
	}

	@Override
	public Token[] tokenizeTemp(String[] input) {
		return tokenize(input);
	}
	
}
