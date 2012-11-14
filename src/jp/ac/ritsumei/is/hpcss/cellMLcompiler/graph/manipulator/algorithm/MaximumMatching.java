package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.manipulator.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.GlobalLogger;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.Pair;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.PairList;

/**
 * Graph algorithm class for maximum matching
 * @author y-yamashita
 *
 */
public class MaximumMatching {

	/**
	 * Constructor
	 */
	public MaximumMatching() {}
	
	/**
	 * Maximum matching
	 * @param graph
	 * @return Pairs of vertexes
	 * @throws GraphException
	 * @see PairList
	 */
	public PairList<RecMLVertex,RecMLVertex> maximumMatching(BipartiteGraph<RecMLVertex,RecMLEdge> graph) 
			throws GraphException{
		
		PairList<RecMLVertex,RecMLVertex> resultPairList = new PairList<RecMLVertex,RecMLVertex>();
		Stack<RecMLVertex> pathStack = new Stack<RecMLVertex>();
	
		RecMLVertex source = new RecMLVertex();
		RecMLVertex sink = new RecMLVertex();
	
		source.setSourceFlag(true);
		sink.setSinkFlag(true);

		//Add source & sink vertexes and edges
		addSourceAndSinkVertexes(graph,source,sink);
		
		do{
			//Get a path from source vertex to sink vertex
			pathStack = getPath(source,sink,graph,pathStack);
			GlobalLogger.log(pathStack.toString());

			//Not find the path
			if(pathStack.isEmpty())
				break;
			
			//Reverse destinations of edges from source to sink
			for(int i=1;i<pathStack.size();i++){
				RecMLVertex src= pathStack.get(i-1);
				RecMLVertex dst = pathStack.get(i);
				graph.reverseEdge(src,dst);
				GlobalLogger.log(graph.toString());

			}
			
			//Reset visited flag of all vertexes
			for(RecMLVertex v:graph.getVertexes())
				v.setVisited(false);
			
			//Clean stack
			pathStack.clear();
		}while(true);
		
		
		//Set result of maximum matching
		RecMLEdge outEdgeOfDst=null;
		for(RecMLVertex dst:graph.getDestVertexeSet()){
			//If maximum matching success, one dest vertex has one outEdge to one src vertex 
			if(graph.getOutEdges(dst).size()==1)
				outEdgeOfDst = (RecMLEdge) graph.getOutEdges(dst).toArray()[0];
			else
				throw new GraphException();
					
			RecMLVertex src = graph.getDestVertex(outEdgeOfDst);
			
			/* src does not be variable vertex, throw exception*/
			if(!(src.hasVariable()||src.hasExpression()))
				throw new GraphException();
			/* dst does not be expression vertex, throw exception */
				else if(dst.hasVariable()&&dst.hasExpression())
				throw new GraphException();
			
			//Set pair of matched vertexes
			Pair<RecMLVertex,RecMLVertex> pair = 
					new Pair<RecMLVertex, RecMLVertex>(src, dst);
			resultPairList.add(pair);
		}
		
		//Remove source & sink vertexes and edges
		removeSourceAndSink(graph,source,sink);
		
		return resultPairList;
	}
	
	
	/**
	 * Remove source & sink vertex and edges
	 * @param graph
	 * @param source
	 * @param sink
	 */
	private void removeSourceAndSink(BipartiteGraph<RecMLVertex, RecMLEdge> graph,
			RecMLVertex source,
			RecMLVertex sink) {
		List<RecMLEdge> removeEdges = new ArrayList<RecMLEdge>();
		
		//Get edges to source vertex
		for(RecMLEdge e:graph.getInEdges(source)){
			removeEdges.add(e);
		}
		//Get edges from source vertex
		for(RecMLEdge e:graph.getOutEdges(source))
			removeEdges.add(e);

		
		//Get edges to sink vertex
		for(RecMLEdge e:graph.getInEdges(sink))
			removeEdges.add(e);
		//Get edges from soruce vertex
		for(RecMLEdge e:graph.getOutEdges(sink))
			removeEdges.add(e);
		
		for(RecMLEdge e:removeEdges)
			graph.removeEdge(e);
		
		graph.removeVertex(source);
		graph.removeVertex(sink);

	}
	
	/**
	 * Get a path from source to sink
	 * @param source
	 * @param sink
	 * @param graph
	 * @param stack
	 * @return Path of vertexes
	 */
	private Stack<RecMLVertex> getPath(
			RecMLVertex source, RecMLVertex sink, BipartiteGraph<RecMLVertex, RecMLEdge> graph,
			Stack<RecMLVertex> stack){
	
		//Get path using DFS
		depthFirstSearch(source,sink,graph,stack);

	return stack;
}
	
	/**
	 * Depth first search method
	 * @param cur current vertex
	 * @param goal goal vertex (sink vertex)
	 * @param graph
	 * @param stack
	 */
	private void depthFirstSearch(RecMLVertex cur, RecMLVertex goal,
			BipartiteGraph<RecMLVertex, RecMLEdge> graph,
			Stack<RecMLVertex> stack) {
			stack.push(cur);
			cur.setVisited(true);
			
			for(RecMLVertex next:graph.getOutOfVertex(cur)){
				if(stack.peek().equals(goal))
					break;
				
				if(!next.isVisited())
					depthFirstSearch(next, goal, graph, stack);
			}
			if(!stack.peek().equals(goal))
				stack.pop();
			
	}

	/**
	 * Add source & sink vertex and edges to graph
	 * @param graph
	 * @param source
	 * @param sink
	 * @throws GraphException
	 */
	private void addSourceAndSinkVertexes(
			BipartiteGraph<RecMLVertex, RecMLEdge> graph,
			RecMLVertex source,
			RecMLVertex sink) throws GraphException {
			graph.addVertex(source);
			graph.addVertex(sink);
			
			for(RecMLVertex src: graph.getSourceVertexSet())
				graph.addEdge(new RecMLEdge(), source, src);
			
			for(RecMLVertex dst: graph.getDestVertexeSet())
				graph.addEdge(new RecMLEdge(), dst, sink);
		}
		
	
}
