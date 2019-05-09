package duplicate;

import java.util.Iterator;

import string.DocumentFrequency;
import string.StringDistance;
import string.StringWrapper;

public interface StringCompare {
	public abstract StringDistance getSourceDistance();

	public abstract StringDistance getTargetDistance();

	public abstract double score(StringWrapper paramStringWrapper1, StringWrapper paramStringWrapper2);

	public abstract void preprocessSource(Iterator paramIterator);

	public abstract void preprocessTarget(Iterator paramIterator);

	public abstract void useStatisticsForSource(DocumentFrequency paramDocumentFrequency);

	public abstract void useStatisticsForTarget(DocumentFrequency paramDocumentFrequency);

	public abstract DocumentFrequency getSourceStatistics();

	public abstract DocumentFrequency getTargetStatistics();
}
