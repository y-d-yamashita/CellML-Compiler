package jp.ac.ritsumei.is.hpcss.cellMLcompiler.manipulator.graph;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.Graph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.Pair;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.PairList;

/**
 * Graph creator class
 * @author y-yamashita
 *
 */
public class GraphCreator {

	/**
	 * Create a dependency graph
	 * @param pairList
	 * @param oldGraph
	 * @return Dependency graph
	 * @throws GraphException
	 */
	public DirectedGraph<RecMLVertex, RecMLEdge> cretateDependencyGraph(
			PairList<RecMLVertex,RecMLVertex> pairList,
			DirectedGraph<RecMLVertex,RecMLEdge> oldGraph) throws GraphException{
		
		DirectedGraph<RecMLVertex, RecMLEdge> dependencyGraph = new DirectedGraph<RecMLVertex, RecMLEdge>();
	
		Map<RecMLVertex,RecMLVertex> oldSrcMap = new HashMap<RecMLVertex, RecMLVertex>();
		Map<RecMLVertex,RecMLVertex> oldDstMap = new HashMap<RecMLVertex, RecMLVertex>();
		
		//Add new vertex of variable and expression
		for(Pair<RecMLVertex,RecMLVertex> p:pairList){
			RecMLVertex v = new RecMLVertex(p.getFirst().getVariable(),p.getSecond().getExpression());
			dependencyGraph.addVertex(v);
			oldSrcMap.put(p.getFirst(), v);
			oldDstMap.put(p.getSecond(), v);
		}
		
		//Add new edges in dependency graph
		for(RecMLVertex oldSrc:oldSrcMap.keySet())
			for(RecMLVertex oldDst:oldGraph.getOutOfVertex(oldSrc))
				dependencyGraph.addEdge(new RecMLEdge(), oldSrcMap.get(oldSrc),oldDstMap.get(oldDst));
		
		return dependencyGraph;
	}
	
	
	
	/**
	 * Create a bipartite graph
	 * @param recmlAnalyzer
	 * @return Bipartite graph
	 * @throws GraphException
	 * @attention No at all test
	 */
	public Graph<RecMLVertex,RecMLEdge> createBipartiteGraph(RecMLAnalyzer recmlAnalyzer) throws GraphException{
		BipartiteGraph<RecMLVertex,RecMLEdge> graph = new BipartiteGraph<RecMLVertex, RecMLEdge>();
		
		//Add src side vertexes
		addRecMLVariableVertex(graph,recmlAnalyzer);
		
		//Add dst side vertexes
		addRecMLExpressionVertex(graph,recmlAnalyzer);
		
		//Add edges
		addRecMLEdges(graph,recmlAnalyzer);
		
		//Remove not connected vertexes
		removeNotConnectedVertexes(graph);
		
		return graph;
	}
	
	

	/**
	 * Remove not connected vertexes
	 * @param graph
	 */
	private void removeNotConnectedVertexes(
			Graph<RecMLVertex, RecMLEdge> graph) {
		for(RecMLVertex v:graph.getVertexes())
			if(graph.getEdges(v).isEmpty())
				graph.removeVertex(v);
	}


	/**
	 * Add RecML edges from recMLAalyzer
	 * @param graph
	 * @param recmlAnalyzer
	 * @throws GraphException
	 */
	private void addRecMLEdges(Graph<RecMLVertex, RecMLEdge> graph,
			RecMLAnalyzer recmlAnalyzer) throws GraphException {
		for(MathExpression expr: recmlAnalyzer.getM_vecExpression())
			for(MathOperand var:expr.getVariables()){
				RecMLEdge edge = new RecMLEdge();
				RecMLVertex src = findRecMLVertexes((Math_ci) var, graph);
				RecMLVertex dst = findRecMLVertexes(expr, graph);
				graph.addEdge(edge, src, dst);
				
			}
	}

	/**
	 * Find RecML vertexes from Math_ci
	 * @param ci
	 * @param graph
	 * @throws GraphException 
	 */
	private RecMLVertex findRecMLVertexes(Math_ci ci,Graph<RecMLVertex,RecMLEdge> graph) throws GraphException{
		for(RecMLVertex v: graph.getVertexes())
			if(v.equals(ci)) return v;
		
		throw new GraphException();
	}

	/**
	 * Find RecML vertexes from MathExpression
	 * @param ci
	 * @param graph
	 * @throws GraphException 
	 */
	private  RecMLVertex findRecMLVertexes(MathExpression expr,Graph<RecMLVertex,RecMLEdge> graph) throws GraphException{
		for(RecMLVertex v: graph.getVertexes())
			if(v.equals(expr)) return v;
		
		throw new GraphException();
	}

	
	/**
	 * Add a vertex of RecML expression
	 * @param graph
	 * @param recmlAnalyzer
	 * @throws GraphException
	 */
	private void addRecMLExpressionVertex(
			BipartiteGraph<RecMLVertex, RecMLEdge> graph, RecMLAnalyzer recmlAnalyzer) throws GraphException {
		for(MathExpression expr: recmlAnalyzer.getM_vecExpression())
			graph.addDestVertex(new RecMLVertex(expr));
		
	}

	/**
	 * Add a vertex of RecML variable
	 * @param graph
	 * @param recmlAnalyzer
	 * @throws GraphException
	 */
	private void addRecMLVariableVertex(
			BipartiteGraph<RecMLVertex, RecMLEdge> graph, RecMLAnalyzer recmlAnalyzer) throws GraphException {
		/* Add new vertexes of recvar*/
		for(Math_ci var: recmlAnalyzer.getM_vecRecurVar())
			graph.addSourceVertex(new RecMLVertex(var));
		
		/* Add new vertexes of arithvar*/
		for(Math_ci var: recmlAnalyzer.getM_vecArithVar())
			graph.addSourceVertex(new RecMLVertex(var));
	}
	
	
}
