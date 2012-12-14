package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;

/**
 * Vertex class is abstract class for graph.
 * When you create a new vertex class,
 * you have to extends this class.
 * This class set id of vertices to control order of vertices.
 *  
 * @author y-yamashita
 *
 */
public abstract class Vertex implements Comparable<Vertex>{
	
	/** New ID is given to new Vertex */
	private static long newID=-1;
	
	/** ID of vertex */
	private long id;
	
	/** Constructor */
	public Vertex(){
		id=getNewID();
	}
	
	private long getNewID(){
		newID++;
		return newID;
	}
	
	/**
	 * This method is used to sort a set (or array) of vertexes.
	 * @return result of compared
	 */
	@Override
	public int compareTo(Vertex n) {
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
