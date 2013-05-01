
public class Node {
	private int row;
	private int col;
	private Node parentNode;
	private int g;// move cost
	private int h;// r1-r2|+|c1-c2|
	private int f;// f=g+h

	public Node(int row, int col, Node parentNode) {
		this.row = row;
		this.col = col;
		this.parentNode = parentNode;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}
}
