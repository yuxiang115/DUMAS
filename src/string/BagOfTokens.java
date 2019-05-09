package string;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import token.Token;

public class BagOfTokens extends StringWrapper implements Serializable{
	private Map weightMap = new TreeMap();
	private TokenWeightList twList = new TokenWeightList();
	private double totalWeight = 0.0D;
	
	public BagOfTokens(String s, Token[] toks){
		super(s);
		for(int i = 0; i < toks.length; i++){
			weightMap.put(toks[i], new Double(getWeight(toks[i]) + 1.0D));
		}
		totalWeight = toks.length;
		Iterator tokenMapIter = weightMap.entrySet().iterator();
		while(tokenMapIter.hasNext()){
			Map.Entry entry = (Map.Entry) tokenMapIter.next();
			Token curTok = (Token)entry.getKey();
			Double curWeight = (Double)entry.getValue();
			twList.setWeight(curTok, curWeight);
		}
	}
	
	public Iterator tokenIterator(){
		return weightMap.keySet().iterator();
	}
	
	public boolean contains(Token tok){
		return weightMap.get(tok) != null;
	}
	
	public double getWeight(Token tok){
		Double f = (Double) weightMap.get(tok);
		return f == null ? 0.0D : f.doubleValue();
	}
	
	public void setWeight(Token tok, double d){
		Double newWeight = new Double(d);
		Double oldWeight = (Double)weightMap.get(tok);
		totalWeight += (oldWeight == null ? d : d - oldWeight.doubleValue());
		weightMap.put(tok, newWeight);
		twList.setWeight(tok, new Double(d));
	}
	
	public double getTotalWeight(){
		return totalWeight;
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer(200);
		result.append("[");
		Iterator entryIter = weightMap.entrySet().iterator();
		while (entryIter.hasNext()){
			Map.Entry entry = (Map.Entry)entryIter.next();
			result.append("<");
			result.append(entry.getKey());
			result.append(",");
			result.append(entry.getValue());
			result.append(">");
		}
		result.append("]");
		return result.toString();
	}
	
	public ArrayList sortedTokenList(){
		return twList.sortedTokenList();
	}
	
	public class TokenWeight implements Comparable, Serializable{
		private Token token;
		private Double weight;
		
		private TokenWeight(Token token, Double weight){
			this.token = token;
			this.weight = weight;
		}
		
		private Token getToken(){
			return token;
		}
		
		private Double getWeight(){
			return weight;
		}
		
		private void setWeigth(Double weight){
			this.weight = weight;
		}
		
		public int compareTo(Object obj){
			TokenWeight tw = (TokenWeight) obj;
			int comp = weight.compareTo(tw.getWeight());
			if(comp == 0) return token.compareTo(tw.getToken());
			return -1 * weight.compareTo(tw.getWeight());
		}
		
		public boolean equals(Object obj){
			if(obj instanceof TokenWeight){
				TokenWeight tw = (TokenWeight)obj;
				return (token.equals(tw.getToken())) && (weight.equals(tw.getWeight()));
			}
			return false;
		}
		
		public String toString(){
			return "<TW: " + token + "," + weight + ">";
		}
	}
	
	
	public class TokenWeightList implements Serializable{
		private TreeSet tokenWeights = new TreeSet();
		
		public TokenWeightList(){}
		
		private void setWeight(Token tok, Double weight){
			BagOfTokens.TokenWeight tw = null;
			Iterator iter = tokenWeights.iterator();
			while((tw == null) && iter.hasNext()){
				BagOfTokens.TokenWeight curTw = (BagOfTokens.TokenWeight)iter.next();
				if(curTw.getToken().equals(tok)){
					tw = curTw;
				}
			}
			
			if(tw == null){
				tw = new BagOfTokens.TokenWeight(tok, weight);
				tokenWeights.add(tw);
			}
			else {
				tokenWeights.remove(tw);
				tw.setWeigth(weight);
				tokenWeights.add(tw);
			}
		}
		
		public ArrayList sortedTokenList(){
			ArrayList result = new ArrayList(tokenWeights.size());
			Iterator iter = tokenWeights.iterator();
			while(iter.hasNext()){
				BagOfTokens.TokenWeight tw = (BagOfTokens.TokenWeight)iter.next();
				result.add(tw.getToken());
			}
			return result;
		}
		
		public String toString(){
			int maxElements = 5;
			StringBuffer result = new StringBuffer(100);
			result.append("<TWL:");
			int size = tokenWeights.size();
			int num = Math.min(size, maxElements);
			int i = 0;
			Iterator iter = tokenWeights.iterator();
			while(i < num){
				result.append(iter.next().toString());
				if (i < size - 1) {
					result.append("; ");
				}
				i++;
			}
			if(i < size){
				result.append("...");
			}
			result.append(">");
			return result.toString();
		}
	}
	
}
