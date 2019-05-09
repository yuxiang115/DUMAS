package string;

import java.util.Iterator;

import token.Token;
import token.Tokenizer;

public class SoftTFIDF extends TFIDF{
	private StringDistance tokenDistance;
	private double tokenMatchThreshold;
	
	public SoftTFIDF(Tokenizer tokenizer, StringDistance tokenDistance, double tokenMatchThreshold){
		super(tokenizer);
		this.tokenDistance = tokenDistance;
		this.tokenMatchThreshold = tokenMatchThreshold;
	}
	
	public void setTokenMatchThreshold(double d) {
		tokenMatchThreshold = d;
	}
	
	public void setTokenMatchThreshold(Double d) {
		tokenMatchThreshold = d.doubleValue();
	}
	
	public double getTokenMatchThreshold() {
		return tokenMatchThreshold;
	}
	
	public double score(StringWrapper s, StringWrapper t) {
		BagOfTokens sBag = (BagOfTokens)s;
		BagOfTokens tBag = (BagOfTokens)t;
		double sim = 0.0D;
		
		for (Iterator i = sBag.tokenIterator(); i.hasNext();) {
			Token tok = (Token)i.next();
			if (tBag.contains(tok)) {
				sim += sBag.getWeight(tok) * tBag.getWeight(tok);
			}
			else{
				double matchScore = tokenMatchThreshold;
				Token matchTok = null;
				for (Iterator j = tBag.tokenIterator(); j.hasNext();){
					Token tokJ = (Token)j.next();
					double distItoJ = tokenDistance.score(tok.getValue(), tokJ.getValue());
					if (distItoJ >= matchScore) {
						matchTok = tokJ;
						matchScore = distItoJ;
					}
				}
				if (matchTok != null){
					sim = sim + sBag.getWeight(tok) * tBag.getWeight(matchTok) * matchScore;
				}
			}
		}
		return sim;
	}
	
	public String explainScore(StringWrapper s, StringWrapper t){
		BagOfTokens sBag = (BagOfTokens)s;
		BagOfTokens tBag = (BagOfTokens)t;
		StringBuffer buf = new StringBuffer("");
		PrintfFormat fmt = new PrintfFormat("%.3f");
		buf.append("Common tokens: ");
		for (Iterator i = sBag.tokenIterator(); i.hasNext();) {
			Token tok = (Token)i.next();
			if (tBag.contains(tok)) {
				buf.append(" " + tok.getValue() + ": ");
				buf.append(fmt.sprintf(sBag.getWeight(tok)));
				buf.append("*");
				buf.append(fmt.sprintf(tBag.getWeight(tok)));
			}
			else{
				double matchScore = tokenMatchThreshold;
				Token matchTok = null;
				for (Iterator j = tBag.tokenIterator(); j.hasNext();) {
					Token tokJ = (Token)j.next();
					double distItoJ = tokenDistance.score(tok.getValue(), tokJ.getValue());
					if (distItoJ >= matchScore) {
						matchTok = tokJ;
						matchScore = distItoJ;
					}
				}
				if (matchTok != null) {
			          buf.append(" '" + tok.getValue() + "'~='" + 
			            matchTok.getValue() + "': ");
			          buf.append(fmt.sprintf(sBag.getWeight(tok)));
			          buf.append("*");
			          buf.append(fmt.sprintf(tBag.getWeight(matchTok)));
			          buf.append("*");
			          buf.append(fmt.sprintf(matchScore)); 
				}
			}
		}
		buf.append("\nscore = " + score(s, t));
		return buf.toString();
	}
	
	public String toString() {
		    return "[SoftTFIDF thresh=" + tokenMatchThreshold + ";" + tokenDistance + "]";
	}
}
