package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.GlobalLogger;

/**
 * Directed graph class
 * @author y-yamashita
 *
 * @param <V>
 * @param <E>
 */
public class DirectedGraph<V,E> implements Graph<V, E>{

	/**
	 * Node class has object of vertex class.
	 * The class has two type edges.
	 * Set "outEdges" is out from a vertex.
	 * Set "inEdges" is in to a vertex.  
	 * @author y-yamashita
	 *
	 */
	private class Node{
		private V v; Set<E> outEdges; Set<E> inEdges;
		/**
		 * Constructor 
		 * @param v
		 */
		private Node(V v){
			this.v=v;
			this.outEdges = new TreeSet<E>();
			this.inEdges = new TreeSet<E>();
		}
		
		/**
		 * toString method
		 */
		public String toString(){return v.toString();}
		
		/**
		 * Add outEdge "e"
		 * @param e
		 * @return
		 */
		private boolean addOutEdge(E e){
			if(outEdges.contains(e))
				return false;
			return outEdges.add(e);
			}

		/**
		 * Add inEdge "e"
		 * @param e
		 * @return
		 */
		private boolean addInEdge(E e){
			if(inEdges.contains(e))
				return false;
			return inEdges.add(e);
			}
		
		/**
		 * equal method
		 * @param n
		 * @return if eqaul return true, othercase return false
		 */
		private boolean equals(Node n){return this.equals(n);}
	
		
	}
	
	/**
	 * Connection class has object of edge class.
	 * The class has two vertex.
	 * V "src" is root vertex of the edge.
	 * V "dst" is destination vertex of the edge.  
	 * @author y-yamashita
	 *
	 */	
	 private  class Connection{
		E e;  V src; V dst; 

		/**
		 * Constructor
		 * @param e Edge
		 * @param src Source vertex
		 * @param dst Destination Vertex
		 */
      private Connection(E e,V src,V dst) {
          this.src =src; this.dst =dst; this.e =e;
     }
      /**
       * toString method
       */
      public String toString(){
            return e .toString()+"["+ src.toString()+ ","+dst .toString()+"]";
      	}

     }

	 
	 /* *************************** */
	 /*          Field              */
	 /* *************************** */
	 /** Map of vertexes */
	 private Map<V,Node> nGroup;
	 /** Map of edges */
	 private Map<E,Connection> cGroup;
	

	 /**
	  * Constructor
	  */
	 public DirectedGraph(){
		nGroup = new TreeMap<V,Node>();
		cGroup = new TreeMap<E,Connection>();
	 }
	 
	 /**
	  * Add new vertex
	  * @param v
	  * @throws GraphException
	  */
	@Override
	public void addVertex(V v) throws GraphException{
		//if v is not an instance of Vertex class
		if(!(v instanceof Vertex))
			throw new GraphException(this.getClass().getName(),
					new Throwable().getStackTrace()[0].getMethodName(),
					"Error:"+v+", Vertex:"+v+" is not extended Vertex class");
			
		// nGroup has already vertex "v"
		if(nGroup.containsKey(v)==true)
			throw new GraphException(this.getClass().getName(),
					new Throwable().getStackTrace()[0].getMethodName(),
					"Error:"+v+	", Vertex:"+v+" was already added");
		
		//Register vertex "v"
		nGroup.put(v,new Node(v));
		
		}

	/**
	 * Add new edge
	 * @param e new edge
	 * @param src source vertex of the edge
	 * @param dst dest vertex of the edge
	 * @throws GraphException
	 */
	@Override
	public void addEdge(E e, V src, V dst) throws GraphException {		
		//If e is not an instance of Edge class
		if(!(e instanceof Edge))
			throw new GraphException(this.getClass().getName(),
					new Throwable().getStackTrace()[0].getMethodName(),
					"Error:"+e+", Edge:"+e+" is not extended graph.Edge class");
		
		//Get  node from V
		Node s = getNode(src);
		Node d = getNode(dst);
		
		//If new edge "e" was already added, throw exception 
		if(cGroup.containsKey(e)==true) 
			throw new GraphException(this.getClass().getName(),
					new Throwable().getStackTrace()[0].getMethodName(),
					"Error:"+e+"["+src+","+dst+"]"+
					", Edge:"+e+" was already added");
		
		
		//If vertex s and d were not added, throw exception
		if(s==null || d == null) 
			throw new GraphException(this.getClass().getName(),
					new Throwable().getStackTrace()[0].getMethodName(),
					"Error:"+e+"["+src+","+dst+"]"+
					", the either vertex is null");

		//If adding failed, throw exception
		if(s.addOutEdge(e)==false || d.addInEdge(e)==false)
			throw new GraphException(this.getClass().getName(),
					new Throwable().getStackTrace()[0].getMethodName(),
					"Error:"+e+"["+src+","+dst+"]"+
					", Edge adding was falled");
		
		//Register new edge
		cGroup.put(e,new Connection(e, src, dst));
		

	}
	
	
	/**
	 * toSgring method
	 * @return stirng
	 */
	@Override
	 public String toString(){
         String str = "";
         str+= "Vertices:";
          for(Node n:nGroup.values())
               str+= n.toString()+ ", ";
         str+= "\n";
         str+= "Edges:";
                for(Connection c:cGroup.values() )
                     str+= c.toString()+ " ";         
          return str;
   }
	 /**
	  * Get Node from vertex 
	  * @param v
	  * @return Node of vertex "v"
	  */
		private Node getNode(V v){
			return nGroup.get(v);
		}

		/**
		 * Get connection from edge
		 * @param e
		 * @return Connection of edge "e"
		 */
		private Connection getConnection(E e){
			return cGroup.get(e);
		}

		/**
		 * Get all vertexes
		 * @return Collection of vertexes
		 */
		@Override
		public Collection<V> getVertexes() {
			return nGroup.keySet();
		}


		/**
		 * Get source and dest vertex of edge
		 * @param e
		 * @return Source and dest vertex of edge "e"
		 */
		@Override
		public Collection<V> getVertexes(E e) {
			List<V> list = new ArrayList<V>();
			Connection c = getConnection(e);
			list.add(c.src);
			list.add(c.dst);
			return list;
		}

		/**
		 * Get source vertex of edge
		 * @param e
		 * @return Source vertex of edge "e"
		 */
		@Override
		public V getSourceVertex(E e) {
			return getConnection(e).src;
		}

		/**
		 * Get dest vertex of edge
		 * @param e
		 * @return Dest vertex of edge "e"
		 */
		@Override
		public V getDestVertex(E e) {
			return getConnection(e).dst;
		}
		
		/**
		 * Get all edges in graph
		 * @return collection of all edges
		 */
		@Override
		public Collection<E> getEdges() {
			return cGroup.keySet();
		}
		
		/**
		 * Get edges which connect to vertex
		 * @param v
		 * @return Edges of vertex "v"
		 */
		@Override
		public Collection<E> getEdges(V v) {
			List<E> list = new ArrayList<E>();
			Node n = nGroup.get(v);
			list.addAll(n.inEdges);
			list.addAll(n.outEdges);
			return list;
		}

		/**
		 * Get outEdges of a vertex
		 * @param v
		 * @return OutEdges of vertex "v"
		 */
		@Override
		public Collection<E> getOutEdges(V v) {
			return nGroup.get(v).outEdges;
		}

		/**
		 * Get inEdges of a vertex
		 * @param v
		 * @return InEdges of vertex "v"
		 */
		@Override
		public Collection<E> getInEdges(V v) {
			return nGroup.get(v).inEdges;
		}

		/**
		 * Remove a vertex from graph
		 * @param v
		 */
		@Override
		public void removeVertex(V v) {
			nGroup.remove(v);
		}

		/**
		 * Remove an edge from graph
		 * @param e
		 */
		@Override
		public void removeEdge(E e) {
			Connection c = cGroup.get(e);
			nGroup.get(c.src).outEdges.remove(e);
			nGroup.get(c.dst).inEdges.remove(e);
			cGroup.remove(e);

		}

		/**
		 * Reverse a direction of edge.
		 * ex. src---->dst   >>[reverse]>>  src <---- dst
		 * @param src
		 * @param dst
		 * @throws GraphException
		 */
		public void reverseEdge(V src,V dst) throws GraphException {
			GlobalLogger.log(getOutOfVertex(src).toString());
			Connection c=null;
			E edge=null;
			
			//Find an edge from src to dst
			for(E e:nGroup.get(src).outEdges){
				c = cGroup.get(e);
				GlobalLogger.log(c.toString());
				if(c.dst.equals(dst)){
					edge = e;
					break;
				}
			}
			
			if(edge!=null){//If fina an edge from src to dst...
				//Reverse an edge direction.
				//Change dst and src in Connection
					c.dst=src;
					c.src=dst;
					
					//Change outEdges and inEdges in Node
					Node srcNode = nGroup.get(src);
					if(!srcNode.outEdges.contains(edge))
						throw new GraphException(this.getClass().getName(),
								new Throwable().getStackTrace()[0].getMethodName(),
								"Not contains");					
					srcNode.outEdges.remove(edge);
					srcNode.inEdges.add(edge);
					
					Node dstNode = nGroup.get(dst);
					if(!dstNode.inEdges.contains(edge))
						throw new GraphException(this.getClass().getName(),
								new Throwable().getStackTrace()[0].getMethodName(),
								"Not caontains.");
					dstNode.inEdges.remove(edge);
					dstNode.outEdges.add(edge);
					
				
			}
				
		}
		
		/**
		 * Get dest vertexes of src vertex
		 * @param src
		 * @return Collection of dest vertexes
		 */
		public Collection<V> getOutOfVertex(V src){
			List<V> outVs = new ArrayList<V>();
			
			for(E e:nGroup.get(src).outEdges)
				outVs.add(cGroup.get(e).dst);
				
				return outVs;
		}
		
		/**
		 * Get src vertexes of dest vertex
		 * @param src
		 * @return Collection of src vertesxes
		 */
		public Collection<V> getInOfVertex(V src){
			List<V> inVs = new ArrayList<V>();
			
			for(E e:nGroup.get(src).inEdges)
				inVs.add(cGroup.get(e).src);
				
				return inVs;
		}

		public void changeSrcVertex(E e, V xloop) {
			//Connection c = cGroup.get(e);
			///c.src=xloop;
			
		}
	   

}
