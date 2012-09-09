package jp.ac.ritsumei.is.hpcss.cellMLcompiler.manipulator.graph;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.SetList;
	
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
	public SetList<RecMLVertex> tarjan(DirectedGraph<RecMLVertex,RecMLEdge> graph){
		return tarjan.tarjan(graph);
	}
	
	 /**
     * toXMLString method
     * @param indent
     * @return XML string
     */
    public String toXMLString(DirectedGraph<RecMLVertex, RecMLEdge> graph,SetList sl){
    	/***** Dependency graph XML  *****/
    	String indent="		";
    	StringBuilder sb= new StringBuilder().
    			append("<!----- Dependency graph ---->\n").
    			append("<graph type=\"dependency\">\n").
    			append("	<nodes>\n");
    	List<RecMLVertex> vl = new ArrayList<RecMLVertex>(graph.getVertexes());
    	for(RecMLVertex v:vl)
    		sb.append(v.toXMLString(vl.indexOf(v), indent));
    	sb.append("	</nodes>\n").
    	append("	<edges>\n");
    	List<RecMLEdge> el = new ArrayList<RecMLEdge>(graph.getEdges());
    	for(RecMLEdge e:el)
    		sb.append(e.toXMLString(
    				vl.indexOf(graph.getSourceVertex(e)),
    				vl.indexOf(graph.getDestVertex(e)),
    				el.indexOf(e), indent));
    	sb.append("	</edges>\n").
    	append("</graph>\n");
    	
    	
    	/*** Simultaneous equation XML ***/
    	sb.append("<!----- Simultaneous equations ---->\n")
    	.append("<simulequs>\n");
    	for(Object obj:sl.toArray()){
    	if(obj instanceof Set){
    		Set<RecMLVertex> s = (Set<RecMLVertex>)obj;
    		sb.append("	<gourp id=").append(sl.indexOf(obj)).append(">\n");
    		for(RecMLVertex v:s)
    			sb.append("		<node>").append(vl.indexOf(v)).append("</node>\n");
    		sb.append("	</group>\n");
    	}
    	}
    	sb.append("</simulequs>\n");
    	return sb.toString();
    	
    	
    	
    }
}
