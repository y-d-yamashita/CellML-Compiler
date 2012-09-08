package jp.ac.ritsumei.is.hpcss.cellMLcompiler.manipulator.graph;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.Graph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.manipulator.graph.algorithm.MaximumMatching;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.manipulator.graph.algorithm.Tarjan;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.Pair;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.PairList;
	
/**
 * Graph Manipulator
 * @author y-yamashita
 *
 */
public class GraphManipulator {

	/** Graph creator */
	private GraphCreator graphCreator;
	
	/** Maximum matching */
	private MaximumMatching maximumMatching;
	
	/** Tarjan */
	private Tarjan	tarjan;
	
	
	/**
	 * Constructor
	 */
	public GraphManipulator(){
		//Initialize
		graphCreator = new GraphCreator();
		maximumMatching = new MaximumMatching();
		tarjan = new Tarjan();
	}
	
	/**
	 * Crate dependency graph
	 * @param pairList
	 * @param graph
	 * @return Dependency graph
	 * @throws GraphException
	 */
	public DirectedGraph<RecMLVertex, RecMLEdge> cretateDependencyGraph(PairList<RecMLVertex,RecMLVertex> pairList,
			DirectedGraph<RecMLVertex,RecMLEdge> graph) throws GraphException{
			return graphCreator.cretateDependencyGraph(pairList, graph);
	}
	
	/**
	 * Create bipartite graph
	 * @param recmlAnalyzer
	 * @return Bipartite graph
	 * @throws GraphException
	 */
	public Graph<RecMLVertex,RecMLEdge> createBipartiteGraph(
			RecMLAnalyzer recmlAnalyzer) throws GraphException{
		return graphCreator.createBipartiteGraph(recmlAnalyzer);
	}
	
	/**
	 * Maximum matching
	 * @param graph
	 * @return List of pairs<RecMLVertex,RecMLVertex> 
	 * @throws GraphException
	 * @see Pair
	 * @see PairList
	 */
	public PairList<RecMLVertex,RecMLVertex> maximumMatching(BipartiteGraph<RecMLVertex,RecMLEdge> graph) 
			throws GraphException{
		return maximumMatching.maximumMatching(graph);
	}
	
	/**
	 * Topological sort using Tarjan's algorithm
	 * @param graph
	 * @return Reverse calculation order including strongly connected components
	 */
	public List<Set<RecMLVertex>> tarjan(DirectedGraph<RecMLVertex,RecMLEdge> graph){
		return tarjan.tarjan(graph);
	}
}
