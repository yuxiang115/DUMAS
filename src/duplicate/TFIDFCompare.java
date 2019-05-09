package duplicate;

import string.DocumentFrequency;
import string.StringDistance;
import string.TFIDF;

public class TFIDFCompare extends AbstractStringCompare
implements StringCompare {
	private TFIDF sourceTFIDF = null;
	private TFIDF targetTFIDF = null;


	public TFIDFCompare(){
		this.sourceTFIDF = new TFIDF();
		this.targetTFIDF = new TFIDF();
	}

	public void useStatisticsForSource(DocumentFrequency docFreq){
		((TFIDF)getSourceDistance()).setStatistics(docFreq);
	}

	public void useStatisticsForTarget(DocumentFrequency docFreq)
	{
		((TFIDF)getTargetDistance()).setStatistics(docFreq);
	}

	public StringDistance getSourceDistance(){
		return this.sourceTFIDF;
	}

	public StringDistance getTargetDistance(){
		return this.targetTFIDF;
	}
}
