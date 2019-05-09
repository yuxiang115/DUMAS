package string;

import java.io.Serializable;
import java.util.Iterator;

public interface StringDistance
extends Serializable {
    public double score(StringWrapper var1, StringWrapper var2);

    public double score(String var1, String var2);

    public StringWrapper prepare(String var1);

    public StringWrapper prepare(String[] var1);

    public String explainScore(StringWrapper var1, StringWrapper var2);

    public void accumulateStringArrayStatistics(Iterator var1);

    public DocumentFrequency getStatistics();
}

