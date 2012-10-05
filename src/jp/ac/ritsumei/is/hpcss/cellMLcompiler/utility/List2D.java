package jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;



/**
 * List for result of topological sort
 * @author y-yamashita
 *
 */
public class List2D<V> extends ArrayList<List<V>>{

	/**
	 * Constructor
	 */
	public List2D() {
	}
	
	/**
	 * Add a vertex as a set
	 * @param v
	 */
	public void add(V v){
		List<V> s = new ArrayList<V>();
		s.add(v);
		super.add(s);
	}
	/**
	 * Add a set of vertexes
	 * @return 
	 */
	public boolean add(List<V> s){
		return super.add(s);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(List<V> list:this){
			sb.append("[");
			for(V v:list){
				sb.append(v.toString()).append(", ");
			}
			sb.delete(sb.length()-2, sb.length()-1);
			sb.append("]\n");
		}
		return sb.toString();
	}
	
	

}
