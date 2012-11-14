package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph;

import java.util.Collection;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;


/**
 * Graph interface
 * 
 * @author y-yamashita
 * @param <V> Vertex class type
 * @param <E> Edge class type
 */
public interface Graph<V,E> {
	
	/**
	 * A vertex is added in graph.
	 * @param v
	 * @throws GraphException
	 */
	public void addVertex(V v) throws GraphException;
	
	/**
	 * An edge is added in graph.
	 * @param e 
	 * @param v1
	 * @param v2
	 * @throws GraphException
	 */
	public void addEdge(E e,V v1, V v2) throws GraphException;
	
	/**
	 * toSring
	 * @return string
	 */
	public String toString();

	/**
	 * Get all vertexes of graph
	 * @return all vertexes
	 */
	public Collection<V> getVertexes();
	
	/**
	 * Get source and destination vertexes of a edge. 
	 * @param e
	 * @return source and destination vertex
	 */
	public Collection<V> getVertexes(E e);
	
	/**
	 * Get source vertex of a edge
	 * @param e
	 * @return source vertex of edge "e"
	 */
	public V getSourceVertex(E e);
	
	/**
	 * Get dest vertex of a edge
	 * @param e
	 * @return dest vertex of edge "e"
	 */
	public V getDestVertex(E e);

	/**
	 * Get all edges in a graph
	 * @return collection of edges
	 */
	public Collection<E> getEdges();
	
	/**
	 * Get edges of vertex "v" in a graph
	 * @param v
	 * @return collection of edges of vertex "v"
	 */
	public Collection<E> getEdges(V v);
	
	/**
	 * Get edges from vertex "src"
	 * @param src
	 * @return collection of edges from vertex "src"
	 */
	public Collection<E> getOutEdges(V src);
	
	/**
	 * Get edges to vertex "dst"
	 * @param dst
	 * @return collection of edges to vertex "dst"
	 */
	public Collection<E> getInEdges(V dst);

	/**
	 * Remove a vertex from graph
	 * @param v
	 */
	public void removeVertex(V v);
	
	/**
	 * Remove an edge from graph
	 * @param e
	 */
	public void removeEdge(E e);
}
