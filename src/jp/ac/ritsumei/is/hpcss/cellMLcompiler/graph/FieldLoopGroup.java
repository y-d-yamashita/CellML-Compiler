package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldVertex;

public class FieldLoopGroup {
	private List<FieldVertex> group;
	public FieldLoopGroup(){
		group=new ArrayList<FieldVertex>();
	}
	
	public void addVertex(FieldVertex v){
		group.add(v);
	}
	public void addVertexList(List<FieldVertex> list){
		if(list!=null){
			group.addAll(list);
		}
	}
	
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
