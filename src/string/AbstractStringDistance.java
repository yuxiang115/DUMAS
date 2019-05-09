package string;


public abstract class AbstractStringDistance
implements StringDistance {
    public abstract double score(StringWrapper var1, StringWrapper var2);

    public abstract String explainScore(StringWrapper var1, StringWrapper var2);

    public final double score(String s, String t) {
        return this.score(this.prepare(s), this.prepare(t));
    }

    public StringWrapper prepare(String s) {
        return new StringWrapper(s);
    }
}

