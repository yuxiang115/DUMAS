package duplicate;

import string.DocumentFrequency;
import string.SoftTFIDF;
import string.StringDistance;
import token.Tokenizer;

public class SoftTFIDFCompare extends AbstractStringCompare implements StringCompare{
	private SoftTFIDF sourceSoftTFIDF = null;
	private SoftTFIDF targetSoftTFIDF = null;

	public SoftTFIDFCompare(Tokenizer tokenizer, StringDistance tokenDistance, double tokenMatchThreshold){
		this.sourceSoftTFIDF = new SoftTFIDF(tokenizer, tokenDistance, tokenMatchThreshold);
		this.targetSoftTFIDF = new SoftTFIDF(tokenizer, tokenDistance, tokenMatchThreshold);
	}

	public void useStatisticsForSource(DocumentFrequency docFreq){
		this.sourceSoftTFIDF.setStatistics(docFreq);
	}

	public void useStatisticsForTarget(DocumentFrequency docFreq) {
		this.targetSoftTFIDF.setStatistics(docFreq);
	}

	public StringDistance getSourceDistance()
	{
		return this.sourceSoftTFIDF;
	}

	public StringDistance getTargetDistance(){
		return this.targetSoftTFIDF;
	}
}
