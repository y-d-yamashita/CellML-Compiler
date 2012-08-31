package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.edge.Edge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.vertex.Vertex;

public class DirectedGraph<V,E> implements Graph<V, E>{
	
	private class Node{
			private V v; Set<E> outEdges; Set<E> inEdges;
		private Node(V v){
			this.v=v;
			this.outEdges = new TreeSet<E>();
			this.inEdges = new TreeSet<E>();
		}
		public String toString(){return v.toString();}
		
		private boolean addRootEdge(E e){
			if(outEdges.contains(e))
				return false;
			return outEdges.add(e);
			}

		private boolean addArrowEdge(E e){
			if(inEdges.contains(e))
				return false;
			return inEdges.add(e);
			}
		private boolean match(V v){return this.v.equals(v);}
		private boolean equals(Node n){return this.equals(n);}
	
		
	}
	
	
	 private  class Connection{
		E e;  V src; V dst; 

      private Connection(E e,V src,V dst) {
          this.src =src; this.dst =dst; this.e =e;
     }
      public String toString(){
            return e .toString()+"["+ src.toString()+ ","+dst .toString()+"]";
      	}

     }
   
	 private Map<V,Node> nGroup;
	 private Map<E,Connection> cGroup;

	 public DirectedGraph(){
		nGroup = new TreeMap<V,Node>();
		cGroup = new TreeMap<E,Connection>();
	 }
	 
	@Override
	public boolean addVertex(V v) throws GraphException{
		if(!(v instanceof Vertex))
			throw new GraphException("Error:"+v+
					", Vertex:"+v+" is not extended Vertex class");
			
		if(nGroup.containsKey(v)==true)
			throw new GraphException("Error:"+v+
					", Vertex:"+v+" was already added");
		
		nGroup.put(v,new Node(v));
		return true;
	}

	@Override
	public boolean addEdge(E e, V src, V dst) throws GraphException {		
		if(!(e instanceof Edge))
			throw new GraphException("Error:"+e+
					", Edge:"+e+" is not extended graph.Edge class");
		
		Node s = getNode(src);
		Node d = getNode(dst);
		
		if(cGroup.containsKey(e)==true) 
			throw new GraphException("Error:"+e+"["+src+","+dst+"]"+
					", Edge:"+e+" was already added");
		if(s==null || d == null) 
			throw new GraphException("Error:"+e+"["+src+","+dst+"]"+
					", the either vertex is null");
		if(s.addRootEdge(e)==false || d.addArrowEdge(e)==false)
			throw new GraphException("Error:"+e+"["+src+","+dst+"]"+
					", Edge adding was falled");
		cGroup.put(e,new Connection(e, src, dst));
		return true;				
	}
	
	
	@Override
	 public String toString(){
         String str = "";
         str+= "Vertices:";
          for(Node n:nGroup.values())
               str+= n.toString()+ ",";
         str+= "\n";
         str+= "Edges:";
                for(Connection c:cGroup.values() )
                     str+= c.toString()+ " ";         
          return str;
   }
	 
		private Node getNode(V v){
			return nGroup.get(v);
		}
		private Connection getConnection(E e){
			return cGroup.get(e);
		}

		@Override
		public Collection<V> getVertices() {
			return nGroup.keySet();
		}


		@Override
		public Collection<V> getVerteces(E e) {
			List<V> list = new ArrayList<V>();
			Connection c = getConnection(e);
			list.add(c.src);
			list.add(c.dst);
			return list;
		}

		@Override
		public V getSrcVertece(E e) {
			return getConnection(e).src;
		}

		@Override
		public V getDstVertece(E e) {
			return getConnection(e).dst;
		}
		
		@Override
		public Collection<E> getEdges() {
			return cGroup.keySet();
		}
		
		@Override
		public Collection<E> getEdges(V v) {
			List<E> list = new ArrayList<E>();
			Node n = nGroup.get(v);
			list.addAll(n.inEdges);
			list.addAll(n.outEdges);
			return list;
		}

		@Override
		public Collection<E> getOutEdges(V v) {
			return nGroup.get(v).outEdges;
		}

		@Override
		public Collection<E> getInEdges(V v) {
			return nGroup.get(v).inEdges;
		}

}
