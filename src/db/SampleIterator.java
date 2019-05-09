package db;

import java.util.Random;

public class SampleIterator implements RecordIterator{
	private RecordIterator baseIter;
	private Random rand;
	private float bernoulliProb;
	private Record nextRecord;

	@Override
	public boolean hasNext() {
		return nextRecord != null;
	}

	@Override
	public Object next() {
		return nextRecord();
	}

	@Override
	public Record nextRecord() {
		Record rec = nextRecord;
		makeNext();
		float k = rand.nextFloat();
		return rec;
	}

	private void makeNext() {
		nextRecord = null;
		while (baseIter.hasNext()) {
			Record rec = baseIter.nextRecord();
			float rand1 = rand.nextFloat();
			if (rand1 <= bernoulliProb) {
				nextRecord = rec;
				return;
			}
		}
	}



}
