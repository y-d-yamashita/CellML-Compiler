package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.manipulator.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.List2D;

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
	public List2D<RecMLVertex> tarjan(DirectedGraph<RecMLVertex,RecMLEdge> graph){
		List2D<RecMLVertex> result = new List2D<RecMLVertex>();
		Stack<RecMLVertex> stack = new Stack<RecMLVertex>();
	
		index = -1;	
		for(RecMLVertex v:graph.getVertexes())
			if(v.number<0)
				travarse(v,stack,result,graph);
		
		return result;
	}
	private void travarse(RecMLVertex v, Stack<RecMLVertex> stack,
			List2D<RecMLVertex> result, DirectedGraph<RecMLVertex, RecMLEdge> graph) {
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
			List<RecMLVertex> stronglyConnectedComponents =
					new ArrayList<RecMLVertex>();
			
			while(stack.isEmpty()!=true&&stack.peek().number>=v.number)
				stronglyConnectedComponents.add(stack.pop());
			
			result.add(stronglyConnectedComponents);
		}
	}
}
