package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph;

import java.util.ArrayList;
import java.util.List;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.field.FieldVertex;


/**
 * Each FieldLoopGroup elements are having possibility of loop optimization
 * @author y-yamashita
 *
 */
public class FieldVertexGroup {
	
	/** Element of loop optimization */
	private List<FieldVertex> group;
	
	/**
	 * Constructor
	 */
	public FieldVertexGroup(){
		group=new ArrayList<FieldVertex>();
	}
	
	/**
	 * Add vertex to group
	 * @param v
	 */
	public void addVertex(FieldVertex v){
		group.add(v);
	}
	
	/**
	 * Add all vertexes to group
	 * @param list
	 */
	public void addVertexList(List<FieldVertex> list){
		if(list!=null){
			group.addAll(list);
		}
	}
	
	
	/**
	 * Get loop group
	 * @return loop group
	 */
	public List<FieldVertex> getVertexGroup(){
		return group;
	}
	
	public FieldVertex getVertex(int i){
		return group.get(i);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(FieldVertex v:group){
			sb.append(v.toString()+", ");
		}
		return sb.toString();
	}
}
