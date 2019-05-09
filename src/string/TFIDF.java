package string;

import java.util.Iterator;

import token.Token;
import token.Tokenizer;

public class TFIDF extends AbstractStatisticalTokenDistance{
	public TFIDF(){}
	
	public TFIDF(Tokenizer tokenizer){
		super(tokenizer);
	}
	
	public double score(StringWrapper s, StringWrapper t){
		BagOfTokens sBag = (BagOfTokens)s;
		BagOfTokens tBag = (BagOfTokens)t;
		
		double sim = 0.0D;
		for (Iterator i = sBag.tokenIterator(); i.hasNext();) {
			Token tok = (Token)i.next();
			if (tBag.contains(tok)) {
				sim += sBag.getWeight(tok) * tBag.getWeight(tok);
			}
		}
		return sim;
	}
	
	public StringWrapper prepare(String s){
		BagOfTokens bag = new BagOfTokens(s, tokenizer.tokenize(s));
		return prepareBag(bag);
	}
	
	public StringWrapper prepare(String[] s){
		BagOfTokens bag = new BagOfTokens(StringOperations.concatenate(s), tokenizer.tokenize(s));
		return prepareBag(bag);
	}
	
	private BagOfTokens prepareBag(BagOfTokens bag){
		double normalizer = 0.0D;
		for (Iterator i = bag.tokenIterator(); i.hasNext();) {
			Token tok = (Token)i.next();
			if (collectionSize > 0) {
				int dfInteger = getDocumentFrequency(tok);
				
				double df = dfInteger == 0 ? 1.0D : dfInteger;
				
				if (df == collectionSize) {
					df -= 1.0D;
				}
				
				double w = Math.log(bag.getWeight(tok) + 1.0D) * Math.log(collectionSize / df);
				bag.setWeight(tok, w);
				normalizer += w * w;
			}
			else{
				bag.setWeight(tok, 1.0D);
				normalizer += 1.0D;
			}
		}
		
		normalizer = Math.sqrt(normalizer);
		for (Iterator i = bag.tokenIterator(); i.hasNext();) {
			Token tok = (Token)i.next();
			bag.setWeight(tok, bag.getWeight(tok) / normalizer);
		}
		return bag;
	}
	
	public String explainScore(StringWrapper s, StringWrapper t){
		BagOfTokens sBag = (BagOfTokens)s;
		BagOfTokens tBag = (BagOfTokens)t;
		StringBuffer buf = new StringBuffer("");
		PrintfFormat fmt = new PrintfFormat("%.3f");
		buf.append("Common tokens: ");
		
		 for (Iterator i = sBag.tokenIterator(); i.hasNext();) {
			 Token tok = (Token)i.next();
			 if (tBag.contains(tok)){
				 buf.append(" " + tok.getValue() + ": ");
				 buf.append(fmt.sprintf(sBag.getWeight(tok)));
				 buf.append("*");
				 buf.append(fmt.sprintf(tBag.getWeight(tok)));
			 }
		 }
		 buf.append("\nscore = " + score(s, t));
		 return buf.toString();
	}
	
	public String toString() {
		return "[TFIDF]";
	}
}
