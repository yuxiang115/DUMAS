package tablemaching;

import datastructure.MatchScore;
import datastructure.TableMatchResult;

public abstract interface SchemaMatch{
  public abstract void addDuplicate(MatchScore paramMatchScore);
  
  public abstract TableMatchResult match();
}

