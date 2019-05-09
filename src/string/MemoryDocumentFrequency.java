package string;

import java.util.HashMap;

import token.Token;

public class MemoryDocumentFrequency implements DocumentFrequency{
	private HashMap dfMap = new HashMap();
	
	public MemoryDocumentFrequency(){}
	@Override
	public int getDocumentFrequency(Token token){
		Integer df = (Integer) dfMap.get(token);
		
		return df == null ? 0 : df.intValue();
	}
	
	public void incrementDocumentFrequency(Token token){
		Integer df = (Integer) dfMap.get(token);
		
		if(df == null){
			dfMap.put(token, AbstractStatisticalTokenDistance.ONE);
		}
		else if(df == AbstractStatisticalTokenDistance.ONE){
			dfMap.put(token, AbstractStatisticalTokenDistance.TWO);
		}
		else if(df == AbstractStatisticalTokenDistance.TWO){
			dfMap.put(token, AbstractStatisticalTokenDistance.THREE);
		}
		else
			dfMap.put(token, new Integer(df.intValue() + 1));
	}
	
	@Override
	public void incrementCachedDocumentFrequency(Token tok){
		incrementDocumentFrequency(tok);
	}
	@Override
	public void finalize(){}
}
