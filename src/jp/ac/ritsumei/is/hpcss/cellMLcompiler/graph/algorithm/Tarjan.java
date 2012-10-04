package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.SetList;

/**
 * Topological sort class using Tarjan's algorithm
 * @author y-yamashita
 *
 */
public class Tarjan {
	private int index;
	
	/**
	 * Constructor
	 */
	public Tarjan(){}
	
	/**
	 * Topological sort using Tarjan's algorithm. Refer thesis about details.
	 * @param graph
	 * @return Sorted vertexes list. Strongly connected components is presented as Set of vertex. 
	 */
	public SetList<RecMLVertex> tarjan(DirectedGraph<RecMLVertex,RecMLEdge> graph){
		SetList<RecMLVertex> result = new SetList<RecMLVertex>();
		Stack<RecMLVertex> stack = new Stack<RecMLVertex>();
	
		index = -1;	
		for(RecMLVertex v:graph.getVertexes())
			if(v.number<0)
				travarse(v,stack,result,graph);
		
		return result;
	}
	private void travarse(RecMLVertex v, Stack<RecMLVertex> stack,
			SetList<RecMLVertex> result, DirectedGraph<RecMLVertex, RecMLEdge> graph) {
		index++;
		v.number=index;
		v.lowpt=index;
		
		stack.push(v);
		
		for(RecMLVertex w:graph.getOutOfVertex(v))
			if(w.number< 0){
				travarse(w, stack, result, graph);
				v.lowpt=Math.min(v.lowpt, w.lowpt);
			}else if(w.number < v.number){
				if(stack.search(w)>0)
					v.lowpt = Math.min(v.lowpt, w.number);
			}
		if(v.lowpt==v.number){
			Set<RecMLVertex> stronglyConnectedComponents =
					new TreeSet<RecMLVertex>();
			
			while(stack.isEmpty()!=true&&stack.peek().number>=v.number)
				stronglyConnectedComponents.add(stack.pop());
			
			result.add(stronglyConnectedComponents);
		}
	}
}
