package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.manipulator;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.FieldGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.FieldVertexGroupList;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.MathExpressionLoop;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.manipulator.algorithm.LoopCreator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.manipulator.algorithm.LoopSeparator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.manipulator.algorithm.MaximumMatching;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.manipulator.algorithm.Tarjan;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.RecMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.SimpleRecMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.List2D;
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
	
	/** Loop separator of Field graph*/
	private LoopSeparator loopSeparator;
	
	/** Loop creator */
	private LoopCreator loopCreator;
	
	/**
	 * Constructor
	 */
	public GraphManipulator(){
		//Algorithm executors initialize
		graphCreator = new GraphCreator();
		maximumMatching = new MaximumMatching();
		tarjan = new Tarjan();
		loopSeparator=new LoopSeparator();
		loopCreator = new LoopCreator();
		
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
	 * Crate dependency graph
	 * @param dependency graph of variables and equations
	 * @return Field Dependency graph
	 * @throws GraphException
	 * @throws MathException 
	 * @throws TableException 
	 */
	//public FieldGraph cretateFieldDependencyGraph(
	//		DirectedGraph<RecMLVertex, RecMLEdge> graph,
	//		RecMLAnalyzer recmlAnalyzer
	//		) throws GraphException, TableException, MathException{
	//		return graphCreator.cretateFieldDependencyGraph(graph,recmlAnalyzer.getRecMLVariableTable(),recmlAnalyzer);
	//}
	
	/**
	 * Create bipartite graph
	 * @param recmlAnalyzer
	 * @return Bipartite graph
	 * @throws GraphException
	 */
	public BipartiteGraph<RecMLVertex,RecMLEdge> createBipartiteGraph(
			RecMLEquationAndVariableContainer contener) throws GraphException{
		return graphCreator.createBipartiteGraph(contener);
	}
	
	
	public BipartiteGraph<RecMLVertex,RecMLEdge> createBipartiteGraph_Simple(
			SimpleRecMLEquationAndVariableContainer contener) throws GraphException{
		return graphCreator.createBipartiteGraph_Simple(contener);
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
	public List2D<RecMLVertex> tarjan(DirectedGraph<RecMLVertex,RecMLEdge> graph){
		return tarjan.tarjan(graph);
	}
	
	 /**
     * toXMLString method
     * @param indent
     * @return XML string
     */
    public String toRecMLXMLString(DirectedGraph<RecMLVertex, RecMLEdge> graph,List2D<RecMLVertex> listlist){

    	/***** Dependency graph XML  *****/
    	String indent="		";
    	StringBuilder sb= new StringBuilder();
    	
    	
    			sb.append("<!----- Dependency graph ---->\n");
    	if(graph!=null){
    			sb.append("<graph type=\"dependency\">\n");
    
    	sb.append("	<nodes>\n");
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
    	sb.append("	</edges>\n");
    	sb.append("</graph>\n");
    	
    	/*** Simultaneous equation XML ***/
    	sb.append("<!----- Simultaneous equations ---->\n")
    	.append("<simulequs>\n");
    	if(listlist!=null){
    		for(List<RecMLVertex> list:listlist){
    			sb.append("	<gourp id=").append(listlist.indexOf(list)).append(">\n");
    			for(RecMLVertex v:list)
    				sb.append("		<node>").append(vl.indexOf(v)).append("</node>\n");
    			sb.append("	</group>\n");
    		}
    	}
    }else{ // graph is null
    	sb.append("<!---- Graph is NULL  ---\n>");
    }
    	sb.append("</simulequs>\n");
    	return sb.toString();
    	
    	
    	
    }


    public HashMap<Integer, HashMap<String, Integer>> getRecMLNode(DirectedGraph<RecMLVertex, RecMLEdge> graph){
    	HashMap<Integer, HashMap<String, Integer>> hm = new HashMap<Integer, HashMap<String, Integer>>();

    	List<RecMLVertex> vl = new ArrayList<RecMLVertex>(graph.getVertexes());
    	for(RecMLVertex v:vl){
    		HashMap<String, Integer> h = new HashMap<String, Integer>();
    		int id = vl.indexOf(v);
    		int var = v.getVarId();
    		int equ = v.getEquId();
    		h.put("variable", var);
    		h.put("equation", equ);
    		hm.put(id, h);
    	} 	
    	
    	return hm;
    }
    public HashMap<Integer, HashMap<String, Integer>> getRecMLEdge(DirectedGraph<RecMLVertex, RecMLEdge> graph){
    	HashMap<Integer, HashMap<String, Integer>> hm = new HashMap<Integer, HashMap<String, Integer>>();
    	
    	List<RecMLVertex> vl = new ArrayList<RecMLVertex>(graph.getVertexes());
    	List<RecMLEdge> el = new ArrayList<RecMLEdge>(graph.getEdges());
    	for(RecMLEdge e:el){
    		HashMap<String, Integer> h = new HashMap<String, Integer>();
    		int id = el.indexOf(e);
    		int sour = vl.indexOf(graph.getSourceVertex(e));
    		int dest = vl.indexOf(graph.getDestVertex(e));
    		h.put("source", sour);
    		h.put("dest", dest);
    		hm.put(id, h);
    	}
  	
    	return hm;
    }
    public Vector<Integer> getEquOder(DirectedGraph<RecMLVertex, RecMLEdge> graph, List2D<RecMLVertex> listlist){
    	Vector<Integer> vec1 = new Vector<Integer>();
    	List<RecMLVertex> vl = new ArrayList<RecMLVertex>(graph.getVertexes());
    	if(listlist!=null){
    		for(List<RecMLVertex> list:listlist){
    			for(RecMLVertex v:list){
    				int nodeid = vl.indexOf(v);
    				vec1.add(nodeid);
    			}
    		}
    	}
    	  	
    	HashMap<Integer, Integer> h = new HashMap<Integer, Integer>();
    	for(RecMLVertex v:vl){
    		int id = vl.indexOf(v);
    		int equ = v.getEquId();
    		h.put(id, equ);
    	} 	
    	
    	Vector<Integer> vec2 = new Vector<Integer>();
    	for(int i = vec1.size()-1;0<=i;i--){
    		int nid = vec1.get(i);
    		vec2.add(h.get(nid));
    	}

    	return vec2;
    }
    /**
     * Generate loop group based on Field graph
     * @param fieldGraph
     * @return loop group list
     */
	public FieldVertexGroupList separateLoopGroup(
			FieldGraph fieldGraph) {
			FieldVertexGroupList loopGroupList = loopSeparator.separate(fieldGraph);
		return loopGroupList;
	}

	public MathExpressionLoop createLoop(FieldVertexGroupList loopGroupList) throws MathException{
		MathExpressionLoop mathExpressionLoop = loopCreator.create(loopGroupList);
		return mathExpressionLoop;
	}
	
	
	
    /**
     * toXMLString method
     * @param indent
     * @return XML string
     */
    public String toRecMLXMLString(DirectedGraph<FieldVertex, FieldEdge> graph){

    	/***** Dependency graph XML  *****/
    	String indent="		";
    	StringBuilder sb= new StringBuilder().
    			append("<!----- Field graph ---->\n").
    			append("<graph type=\"dependency\">\n").
    			append("	<nodes>\n");
    	List<FieldVertex> vl = new ArrayList<FieldVertex>(graph.getVertexes());
    	for(FieldVertex v:vl){
    		v.setId(vl.indexOf(v));
    		sb.append(v.toXMLString( indent));
    	}
    	sb.append("	</nodes>\n").
    	append("	<edges>\n");
    	List<FieldEdge> el = new ArrayList<FieldEdge>(graph.getEdges());
    	for(FieldEdge e:el)
    		sb.append(e.toXMLString(
    				vl.indexOf(graph.getSourceVertex(e)),
    				vl.indexOf(graph.getDestVertex(e)),
    				el.indexOf(e), indent));
    	sb.append("	</edges>\n").
    	append("</graph>\n");
    	
    	
    	return sb.toString();
    	
    }

}
