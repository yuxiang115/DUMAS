package token;

public abstract interface Tokenizer {
	public abstract Token[] tokenize(String input);
	
	public abstract Token[] tokenize(String[] input);
	
	public abstract Token[] tokenizeTemp(String[] input);
	
	public abstract Token intern(String str);
}
