package datastructure;

import dumasException.DumasException;

public class Alignment {
	private int sourceColumns = 0;
	private int targetColumns = 0;
	private Integer[] alignment = null;
	private double[] score;
	
	public Alignment(int srcColumns, int tgtColumns){
		if ((srcColumns <= 0) || (tgtColumns <= 0)) {
			throw new DumasException(
			        "Number of columns must be greater than 0.");
		}
		this.sourceColumns = srcColumns;
	    this.targetColumns = tgtColumns;
	    this.alignment = new Integer[srcColumns];
	    for (int i = 0; i < srcColumns; i++) {
	    	this.alignment[i] = null;
	    }
	    this.score = new double[srcColumns];
	}
	
	public void addAlignment(int source, int target, double score){
		 if (source <= 0) {
		      throw new DumasException("Source column must be greater than 0.");
		    }
		 if (target <= 0) {
		      throw new DumasException("Target column must be greater than 0.");
		    }
		 if (source > this.sourceColumns) {
		      throw new DumasException("Maximum source column: " + this.sourceColumns);
		    }
		 if (target > this.targetColumns) {
		      throw new DumasException("Maximum target column: " + this.targetColumns);
		    }
		 if ((hasSourceAlignment(source)) && 
			      (getSourceAlignment(source).intValue() != target)) {
			      throw new DumasException("A different alignment for source column " + 
			        source + " exists.");
		 }
		 if ((hasTargetAlignment(target)) && 
			      (getTargetAlignment(target).intValue() != source)) {
			      throw new DumasException("A different alignment for target column " + 
			        target + " exists.");
			    }
		 this.alignment[(source - 1)] = new Integer(target);
		 setScore(source, score);
	}
	
	public void removeSourceAlignment(int sourceColumn){
		this.alignment[(sourceColumn - 1)] = null;
	}
	
	 public boolean hasSourceAlignment(int sourceColumn){
	    return getSourceAlignment(sourceColumn) != null;
	  }
	 
	 public Integer getSourceAlignment(int sourceColumn){
		 return this.alignment[(sourceColumn - 1)];
	 }
	 
	 public boolean hasTargetAlignment(int targetColumn){
		 return getTargetAlignment(targetColumn) != null;
	 }
	 
	 public Integer getTargetAlignment(int targetColumn){
		 for (int i = 0; i < this.sourceColumns; i++){
			 Integer tgt = this.alignment[i];
			 if ((tgt != null) && (tgt.intValue() == targetColumn)) {
				 return new Integer(i + 1);
			 }
		 }
		 return null;
	 }
	 
	 public int getSourceSize(){
		 return this.sourceColumns;
	 }
	 
	 public int getTargetSize(){
		 return this.targetColumns;
	 }
	 
	 public void setScore(int srcColumn, double score){
		 this.score[(srcColumn - 1)] = score;
	 }
	 
	 public double getScore(int srcColumn){
		 return this.score[(srcColumn - 1)];
	 }
	 
	 public String toString(){
		 StringBuffer result = new StringBuffer(100);
		    for (int i = 1; i <= this.sourceColumns; i++) {
		      if (hasSourceAlignment(i))
		      {
		        result.append(i);
		        result.append("->");
		        result.append(getSourceAlignment(i));
		        result.append(" ");
		      }
		    }
		    result.append(";\n Unmatched source:");
		    for (int i = 1; i <= this.sourceColumns; i++) {
		      if (!hasSourceAlignment(i))
		      {
		        result.append(" ");
		        result.append(i);
		      }
		    }
		    result.append(";\n Unmatched target:");
		    for (int i = 1; i <= this.targetColumns; i++) {
		      if (!hasTargetAlignment(i))
		      {
		        result.append(" ");
		        result.append(i);
		      }
		    }
		    return result.toString();
	 }
	 
	  public boolean equals(Object obj){
	    if (!(obj instanceof Alignment)) {
	      throw new DumasException("Other object is not an alignment.");
	    }
	    Alignment al = (Alignment)obj;
	    if (equalSize(al))
	    {
	      int i = 1;
	      if (i <= this.sourceColumns)
	      {
	        if (hasSourceAlignment(i))
	        {
	          if ((!al.hasSourceAlignment(i)) || 
	            (!getSourceAlignment(i).equals(
	            al.getSourceAlignment(i)))) {
	            return false;
	          }
	        }
	        else if (al.hasSourceAlignment(i)) {
	          return false;
	        }
	        return true;
	      }
	    }
	    return false;
	  }
	  
	  public boolean equalSize(Alignment align){
		  return (this.sourceColumns == align.getSourceSize()) && 
				  (this.targetColumns == align.getTargetSize());
	  }
	  
	  public boolean isComplex(){
		  return false;
	  }
}
