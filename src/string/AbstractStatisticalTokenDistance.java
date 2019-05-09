package string;

import java.util.HashSet;
import java.util.Iterator;

import token.SimpleTokenizer;
import token.Token;
import token.Tokenizer;

public abstract class AbstractStatisticalTokenDistance extends AbstractStringDistance{
	protected transient Tokenizer tokenizer;
	protected DocumentFrequency documentFrequency = new MemoryDocumentFrequency();
	
	public static final Integer ONE = new Integer(1);
	public final static Integer TWO = new Integer(2);
	public static final Integer THREE = new Integer(3);
	
	protected int collectionSize = 0;
	protected int totalTokenCount = 0;
	
	private transient HashSet seenTokens = new HashSet();
	
	public AbstractStatisticalTokenDistance(Tokenizer tokenizer){
		this.tokenizer = tokenizer;
	}
	
	public AbstractStatisticalTokenDistance(){
		this(SimpleTokenizer.DEFAULT_TOKENIZER);
	}
	
	public void accumulateStringArrayStatistics(Iterator stringArrayIter){
		while(stringArrayIter.hasNext()){
			String[] array = (String[])stringArrayIter.next();
			Token[] toks = tokenizer.tokenizeTemp(array);
			addToStatistics(toks);
		}
		documentFrequency.finalize();
	}

	private void addToStatistics(Token[] toks) {
		seenTokens.clear();
		for(int j = 0; j <toks.length; j++){
			totalTokenCount += 1;
			if(!seenTokens.contains(toks[j])){
				seenTokens.add(toks[j]);
				documentFrequency.incrementCachedDocumentFrequency(toks[j]);
			}
		}
		collectionSize += 1;
	}
	
	public int getDocumentFrequency(Token tok){
		return this.documentFrequency.getDocumentFrequency(tok);
	}
	
	public void setStatistics(DocumentFrequency df){
		this.documentFrequency = df;
	}
	
	public DocumentFrequency getStatistics(){
		return this.documentFrequency;
	}
	
	 public void setTokenizer(Tokenizer tokenizer){
		 this.tokenizer = tokenizer;
	 }
	 
}
