package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;

/**
 * Edge class is abstract class for graph.
 * When you create a new edge class,
 * you have to extends this class.
 * This class set id of vertices to control order of vertices.
 *  
 * @author y-yamashita
 *
 */
public abstract class Edge implements Comparable<Edge>{
	
	/** New ID is given to new edge */
	private static long newID=-1;
	
	/** ID of edge */
	private long id;
	
	/** Constructor */
	public Edge(){
		id=getNewID();
	}
	
	private long getNewID(){
		newID++;
		return newID;
	}
	
	/**
	 * This method is used to sort a set (or array) of edge.
	 * @return result of compared
	 */
	@Override
	public int compareTo(Edge n) {
		if(this.id < n.id)	return -1;
		else if(this.id > n.id) return 1;
		else return 0;
	}
	
	/**
	 * Change to string
	 * @return defined string in class
	 */
	public abstract String toString();
	
}
