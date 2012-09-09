package jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;



/**
 * List for result of topological sort
 * @author y-yamashita
 *
 */
public class SetList<V> extends ArrayList<Set<V>> {

	/**
	 * Constructor
	 */
	public SetList() {
	}
	
	/**
	 * Add a vertex as a set
	 * @param v
	 */
	public void add(V v){
		Set<V> s = new TreeSet<V>();
		s.add(v);
		super.add(s);
	}
	/**
	 * Add a set of vertexes
	 * @return 
	 */
	public boolean add(Set<V> s){
		return super.add(s);
	}
	
	
	

}
