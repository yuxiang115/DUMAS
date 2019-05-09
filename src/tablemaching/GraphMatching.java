package tablemaching;

import datastructure.Alignment;
import datastructure.ScoreMatrix;

public abstract interface GraphMatching {
	public abstract Alignment match(ScoreMatrix paramScoreMatrix);
}
