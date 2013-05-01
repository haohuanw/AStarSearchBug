import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AStar {
	private int[][] map;// map 1 can walk through 0 cannot
	private List<Node> openList;
	private List<Node> closeList;
	private final static int COST_STRAIGHT = 10;
	private final static int COST_DIAGONAL = 14;
	private int row;// row
	private int column;// col
	private NodeFComparator mNodeFComparator;// for sorting
	private Object mObjectLock;// object lock
	private boolean isCancel = false;// cancel operation
	private boolean isReleased = true;// release resource
	
	private static ArrayList<Node> nodeHeap;//a node heap to avoid new node
	private static int nodeHeapSize = 0;//length of the nodeheap
	private static int nodeUsed = 0;
	private static final int MALLOC_NODE_SIZE = 100;

	public AStar(int[][] map) {
		this.map = map;
		if (map != null && map.length > 0 && map[0].length > 0) {
			this.row = map.length;
			this.column = map[0].length;
		} else {
			row = 0;
			column = 0;
		}
		mNodeFComparator = new NodeFComparator();
		openList = new ArrayList<Node>();
		closeList = new ArrayList<Node>();
		
		mObjectLock = new Object();
		
		isCancel = false;
		isReleased = false;
	}

	/**
	 * search
	 * @param row1
	 * @param col1
	 * @param row2
	 * @param col2
	 * @return
	 */
	public ArrayList<Node> search(int row1, int col1, int row2, int col2) {
		if(isReleased) return null;
		
		ArrayList<Node> result = null;
		synchronized (mObjectLock) {
			if (row1 < 0 || row1 >= row || row2 < 0 || row2 >= row || col1 < 0
					|| col1 >= column || col2 < 0 || col2 >= column) {
				result = null;
			} else if (map[row1][col1] == 0 || map[row2][col2] == 0) {
				result = null;
			} else {
				openList.clear();
				closeList.clear();

				Node sNode = mallocNode(row1, col1, null);
				Node eNode = mallocNode(row2, col2, null);
				openList.add(sNode);
				ArrayList<Node> roadList = search(sNode, eNode);
				if(roadList.size() == 0){
					roadList = null;
					result = null;
				} else {
					result = roadList;
				}
				nodeUsed = 0;
			}
		}
		return result;
	}

	/**
	 * search method
	 * 
	 * @param sNode
	 *           start node
	 * @param eNode
	 *           end node
	 */
	private ArrayList<Node> search(Node sNode, Node eNode) {
		boolean isFind = false;
		Node node = null;
		ArrayList<Node> roadList = new ArrayList<Node>();
		while (!isCancel && openList.size() > 0) {
			// get the min(F)
			node = openList.get(0);
			// check if find
			if (node.getRow() == eNode.getRow() && node.getCol() == eNode.getCol()) {
				isFind = true;
				break;
			}
			// up
			if ((node.getCol() - 1) >= 0) {
				checkPath(node.getRow(), node.getCol() - 1, node, eNode,
						COST_STRAIGHT);
			}
			// down
			if ((node.getCol() + 1) < column) {
				checkPath(node.getRow(), node.getCol() + 1, node, eNode,
						COST_STRAIGHT);
			}
			// left
			if ((node.getRow() - 1) >= 0) {
				checkPath(node.getRow() - 1, node.getCol(), node, eNode,
						COST_STRAIGHT);
			}
			// right
			if ((node.getRow() + 1) < row) {
				checkPath(node.getRow() + 1, node.getCol(), node, eNode,
						COST_STRAIGHT);
			}
			// left up
			if ((node.getRow() - 1) >= 0 && (node.getCol() - 1) >= 0) {
				checkPath(node.getRow() - 1, node.getCol() - 1, node, eNode,
						COST_DIAGONAL);
			}
			// left down
			if ((node.getRow() - 1) >= 0 && (node.getCol() + 1) < column) {
				checkPath(node.getRow() - 1, node.getCol() + 1, node, eNode,
						COST_DIAGONAL);
			}
			// right up
			if ((node.getRow() + 1) < row && (node.getCol() - 1) >= 0) {
				checkPath(node.getRow() + 1, node.getCol() - 1, node, eNode,
						COST_DIAGONAL);
			}
			// right down
			if ((node.getRow() + 1) < row && (node.getCol() + 1) < column) {
				checkPath(node.getRow() + 1, node.getCol() + 1, node, eNode,
						COST_DIAGONAL);
			}
			// delete from open list
			// add to closed list
			closeList.add(openList.remove(0));
			//sort
			Collections.sort(openList, mNodeFComparator);
		}
		if (isFind) {
			getPath(roadList, node);
		}
		return roadList;
	}

	// check
	private boolean checkPath(int row, int col, Node parentNode, Node eNode,
			int cost) {
		Node node = mallocNode(row, col, parentNode);
		//check in the map
		if (map[row][col] == 0) {
			closeList.add(node);
			return false;
		}
		// check close list
		if (isListContains(closeList, row, col) != -1) {
			return false;
		}
		// check open list
		int index = -1;
		if ((index = isListContains(openList, row, col)) != -1) {
			// check G and F
			if ((parentNode.getG() + cost) < openList.get(index).getG()) {
				node.setParentNode(parentNode);
				countG(node, eNode, cost);
				countF(node);
				openList.set(index, node);
			}
		} else {
			// add to open list
			node.setParentNode(parentNode);
			count(node, eNode, cost);
			openList.add(node);
		}
		return true;
	}

	// check if contain
	private int isListContains(List<Node> list, int row, int col) {
		int size = list.size();
		Node node;
		for (int i = 0; i < size; i++) {
			node = list.get(i);
			if (node.getRow() == row && node.getCol() == col) {
				return i;
			}
		}
		return -1;
	}

	// end->start
	private void getPath(ArrayList<Node>roadList, Node node) {
		if (node.getParentNode() != null) {
			getPath(roadList, node.getParentNode());
		}
		roadList.add(node);
	}

	// count GHF
	private void count(Node node, Node eNode, int cost) {
		countG(node, eNode, cost);
		countH(node, eNode);
		countF(eNode);
	}

	private void countG(Node node, Node eNode, int cost) {
		if (node.getParentNode() == null) {
			node.setG(cost);
		} else {
			node.setG(node.getParentNode().getG() + cost);
		}
	}

	
	private void countH(Node node, Node eNode) {
		node.setH(Math.abs(node.getRow() - eNode.getRow())
				+ Math.abs(node.getCol() - eNode.getCol()));
	}


	private void countF(Node node) {
		node.setF(node.getG() + node.getH());
	}


	public void cancel() {
		isCancel = true;
	}

	public void reset() {
		if(isReleased) return;
		
		isCancel = true;
		synchronized (mObjectLock) {
			openList.clear();
			closeList.clear();
			nodeUsed = 0;
		}
		isCancel = false;
	}

	
	public void setMap(int[][] map){
		synchronized(mObjectLock){
			this.map = null;
			this.map = map;
			if (map != null && map.length > 0 && map[0].length > 0) {
				this.row = map.length;
				this.column = map[0].length;
			} else {
				row = 0;
				column = 0;
			}
		}
	}
	
	
	public void onDestroy() {
		if (isReleased)
			return;

		isReleased = true;
		isCancel = true;
		synchronized (mObjectLock) {

			openList.clear();
			closeList.clear();

			openList = null;
			closeList = null;
		}
		mObjectLock = null;
		map = null;
	}
	
	//get node from heap
	private static Node mallocNode(int row, int col, Node parent){
		if(nodeUsed + 1 >= nodeHeapSize){
			if(nodeHeap == null) nodeHeap = new ArrayList<Node>();
			for(int i = MALLOC_NODE_SIZE; i > 0; i--) nodeHeap.add(new Node(0,0,null));
			nodeHeapSize = nodeHeap.size();
			//System.out.println("node heap increated to " + nodeHeapSize);
		} 
		
		Node result = nodeHeap.get(nodeUsed);
		result.setRow(row);
		result.setCol(col);
		result.setParentNode(parent);
		nodeUsed++;//next
		return  result;
	}
	
	/**
	 * return all node
	 */
	public static void rebackAllUseNode(){
		nodeUsed = 0;
	}
	
	/**
	 * clear
	 */
	public static  void clearNodeHeap(){
		if(nodeHeap != null){
			synchronized(nodeHeap){
				nodeHeapSize = 0;
				nodeHeap.clear();
				nodeUsed = 0;
			}
			nodeHeap = null;
		}
		
	}

	// comparator
	class NodeFComparator implements Comparator<Node> {
		@Override
		public int compare(Node o1, Node o2) {
			return o1.getF() - o2.getF();
		}
	}
}
