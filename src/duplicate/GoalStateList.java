package duplicate;

import java.util.ArrayList;

import dumasException.DumasException;

public class GoalStateList {
	private ArrayList goalStates;
	private int length;

	public GoalStateList(int length){
		this.length = length;
		this.goalStates = new ArrayList(length);
	}
	/**
	 * check if the current score larger than exiting score
	 * if true, score can be added otherwise no
	 * @param SearchState 
	 * @return boolean
	 * */
	public boolean couldInsert(SearchState st){
		double score = st.getValue().doubleValue();
		return couldInsert(score);
	}

	/**
	 * check if the current score larger than exiting score
	 * if true, score can be added otherwise no
	 * @param score double 
	 * @return boolean
	 * */
	public boolean couldInsert(double score){
		if (this.goalStates.size() < this.length) {
			return true;
		}
		SearchState last = (SearchState)this.goalStates.get(this.length - 1);
		if (score > last.getValue().doubleValue()) {
			return true;
		}
		return false;
	}
	/**
	 * SearchState sorted by score from high to low
	 * if inserting searchState's score is large enough to be inserted
	 * and the list is full, remove searchSate with smallest score
	 * @param Searchsate
	 * @return boolean if sucess
	 * */
	public boolean insert(SearchState state){
		if (state.getTarget() == null) {
			throw new DumasException("No goal state.");
		}
		if (couldInsert(state)){
			doInsert(state);
			return true;
		}
		return false;
	}
	
	/**
	 * SearchState sorted by score from high to low
	 * if inserting searchState's score is large enough to be inserted
	 * and the list is full, remove searchSate with smallest score
	 * @param Searchsate
	 * */
	private void doInsert(SearchState st){
		int i = 0;
		double score = st.getValue().doubleValue();
		while (i < this.goalStates.size()){
			SearchState curState = (SearchState)this.goalStates.get(i);
			if (score > curState.getValue().doubleValue()){
				this.goalStates.add(i, st);
				if (this.goalStates.size() > this.length) {
					this.goalStates.remove(this.length);
				}
				return;
			}
			i++;
		}
		if (i < this.length){
			this.goalStates.add(st);
			return;
		}
		throw new DumasException("Should not get here.");
	}

}
