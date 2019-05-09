package dumasException;

public class HungarianMethodException extends DumasException{
	private int[][] intMatrix = null;
	private double[][] doubleMatrix = null;
	private int[] mate = null;

	public HungarianMethodException(String message){
		super(message);
	}

	public void setDoubleMatrix(double[][] matrix){
		this.doubleMatrix = matrix;
	}

	public double[][] getDoubleMatrix(){
		return this.doubleMatrix;
	}

	public void setIntMatrix(int[][] matrix){
		this.intMatrix = matrix;
	}

	public int[][] getIntMatrix(){
		return this.intMatrix;
	}

	public void setMate(int[] mate){
		this.mate = mate;
	}

	public int[] getMate(){
		return this.mate;
	}
}
