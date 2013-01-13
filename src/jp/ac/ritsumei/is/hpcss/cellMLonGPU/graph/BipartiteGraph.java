package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLVertex;


/**
 * Bipartite graph class
 * @author y-yamashita
 *
 * @param <V> Vertex class
 * @param <E> Edge class
 */
public class BipartiteGraph<V, E> extends DirectedGraph<V, E>{
	/** Set of source vertexes */
	private Set<V> srcVertexes;
	
	/** Set of dest vertexes */
	private Set<V> dstVertexes;

	/**
	 * Constructor
	 */
	public BipartiteGraph() {
		srcVertexes = new TreeSet<V>();
		dstVertexes = new TreeSet<V>();
	}
	
	/**
	 * Add vertex to graph and set of src vertexes
	 * @param v
	 * @throws GraphException
	 */
	public void addSourceVertex(V v) throws GraphException{
		super.addVertex(v);
		srcVertexes.add(v);
	}
	
	
	public void removeSourceVertex(V v)throws GraphException{
		super.removeVertex(v);
		srcVertexes.remove(v);
	}
	
	@Override
	public void removeVertex(V v){
		System.err.println(this.getClass().getName()+"."+
							Thread.currentThread().getStackTrace()[1].getMethodName()+":");
		System.err.println("removeSourceVertex(V v) or removeDestVertex(V v) " +
							"is recomended in BipartiteGraph class");
		super.removeVertex(v);
	}
	
	/**
	 * Add vertex to graph and set of dest vertexes
	 * @param v
	 * @throws GraphException
	 */
	public void addDestVertex(V v) throws GraphException{
		super.addVertex(v);
		dstVertexes.add(v);
	}

	public void removeDestVertex(V v)throws GraphException{
		super.removeVertex(v);
		dstVertexes.remove(v);
	}

	
	/**
	 * Get set of source vertexes
	 * @return Collection of source vertexes
	 */
	public Collection<V> getSourceVertexSet(){
		return srcVertexes;
	}
	
	/**
	 * Get set of dest vertexes 
	 * @return Collection of dest vertexes
	 */
	public Collection<V> getDestVertexeSet(){
		return dstVertexes;
	}
	
	
}
