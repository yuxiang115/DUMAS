package duplicate;

import java.util.ArrayList;

public class PriorityQueue {
	private ArrayList list;

	public PriorityQueue(int initialCapacity){
		this.list = new ArrayList(initialCapacity);
	}

	public Comparable extractMax(){
		int length = size();
		if (length < 1) {
			throw new NullPointerException("Priority queue is empty.");
		}
		Comparable result = getElement(1);
		Comparable last = (Comparable)this.list.remove(length - 1);
		if (size() > 0){
			this.list.set(0, last);
			heapify(1);
		}
		return result;
	}

	public void insert(Comparable obj){
		int pos = size() + 1;
		while ((pos > 1) && (getElement(parent(pos)).compareTo(obj) < 0)){
			setElement(getElement(parent(pos)), pos);
			pos = parent(pos);
		}
		setElement(obj, pos);
	}

	private void heapify(int pos){
		Comparable posObj = getElement(pos);
		int left = left(pos);
		int right = right(pos);
		int largest;
		if ((left <= size()) && (posObj.compareTo(getElement(left)) < 0)) {
			largest = left;    	
		}
		else{
			largest = pos;
		}
		if ((right <= size()) && (getElement(largest).compareTo(getElement(right)) < 0)) {
			largest = right;
		}
		if (largest != pos){
			setElement(getElement(largest), pos);
			setElement(posObj, largest);
			heapify(largest);
		}
	}

	public int size(){
		return this.list.size();
	}

	public boolean isEmpty(){
		return this.list.isEmpty();
	}

	private Comparable getElement(int pos){
		return (Comparable)this.list.get(pos - 1);
	}

	private void addElement(Object obj, int pos){
		this.list.add(pos - 1, obj);
	}

	private void setElement(Object obj, int pos){
		if (pos <= size()) {
			this.list.set(pos - 1, obj);
		}
		else{
			addElement(obj, pos);
		}
	}

	private int parent(int pos){
		return pos / 2;
	}

	private int left(int pos){
		return 2 * pos;
	}

	private int right(int pos){
		return 2 * pos + 1;
	}

	public String toString(){
		int maxElements = 5;
		StringBuffer result = new StringBuffer(100);
		result.append("<PQ: ");
		int size = this.list.size();
		int num = Math.min(size, maxElements);
		for (int i = 0; i < num; i++)
		{
			result.append(this.list.get(i));
			if (i < size - 1) {
				result.append("; ");
			}
		}
		if (num < size) {
			result.append("...");
		}
		result.append(">");
		return result.toString();
	}
}
