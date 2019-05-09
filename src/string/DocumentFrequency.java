package string;

import java.io.Serializable;

import token.Token;

public abstract interface DocumentFrequency extends Serializable{
	public abstract int getDocumentFrequency(Token paramToken);
	
	public abstract void incrementCachedDocumentFrequency(Token paramToken);
	
	public abstract void finalize();
	
}
