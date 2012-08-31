package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph;


import java.util.Collection;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;

public interface Graph<V,E> {
	
	public boolean addVertex(V v) throws GraphException;
	public boolean addEdge(E e,V v1, V v2) throws GraphException;
	public String toString();

	public Collection<V> getVertices();
	public Collection<V> getVerteces(E e);
	public V getSrcVertece(E e);
	public V getDstVertece(E e);

	public Collection<E> getEdges();
	public Collection<E> getEdges(V v);
	public Collection<E> getOutEdges(V src);
	public Collection<E> getInEdges(V dst);

	
}
