package duplicate;

import java.util.Iterator;

import string.DocumentFrequency;
import string.StringDistance;
import string.StringWrapper;

public abstract class AbstractStringCompare implements StringCompare{
	public abstract StringDistance getSourceDistance();

	public abstract StringDistance getTargetDistance();

	public double score(StringWrapper source, StringWrapper target){
		return getSourceDistance().score(source, target);
	}

	public void preprocessSource(Iterator records){
		preprocess(records, getSourceDistance());
	}

	public void preprocessTarget(Iterator records){
		preprocess(records, getTargetDistance());
	}

	private void preprocess(Iterator records, StringDistance dist){
		StringArrayIterator iter = new StringArrayIterator(records);
		dist.accumulateStringArrayStatistics(iter);
	}

	public abstract void useStatisticsForSource(DocumentFrequency paramDocumentFrequency);

	public abstract void useStatisticsForTarget(DocumentFrequency paramDocumentFrequency);

	public DocumentFrequency getSourceStatistics(){
		return getSourceDistance().getStatistics();
	}

	public DocumentFrequency getTargetStatistics(){
		return getTargetDistance().getStatistics();
	}
}
