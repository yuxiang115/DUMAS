package datastructure;

public class PartnerColSet implements Comparable{
	private int pos = 0;
    private ColSet colSet = null;
    private double score = 0.0;
    
    public PartnerColSet(int pos, ColSet colSet, double score){
    	this.pos = pos;
        this.colSet = colSet;
        this.score = score;
    }
    
    public int getPosition(){
      return this.pos;
    }
    
    public ColSet getColSet(){
      return this.colSet;
    }
    
    public double getScore(){
      return this.score;
    }
    
    public int compareTo(Object obj){
      PartnerColSet other = (PartnerColSet)obj;
      double otherScore = other.getScore();
      if (otherScore > getScore()) {
        return 1;
      }
      if (otherScore < getScore()) {
        return -1;
      }
      return 0;
    }
    
    public String toString(){
      return "[" + getColSet().toString() + "," + getScore() + "]";
    }
}
