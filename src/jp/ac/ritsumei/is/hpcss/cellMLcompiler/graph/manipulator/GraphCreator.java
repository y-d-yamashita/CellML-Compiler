package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.manipulator;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.FieldGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.Graph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.RecMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.SimpleRecMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableTable;
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
			RecMLVertex v = new RecMLVertex();
			v.setVariable(p.getFirst().getVariableID());
			v.setExpression(p.getSecond().getExpressionID());
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
	 * Create a dependency graph
	 * @param  Graph of variables and equations
	 * @return Field Dependency graph
	 * @throws GraphException
	 * @throws TableException 
	 * @throws MathException 
	 */
	public FieldGraph cretateFieldDependencyGraph(
			DirectedGraph<RecMLVertex,RecMLEdge> oldGraph,
			RecMLVariableTable table,
			RecMLAnalyzer recmlAnalyer
			)throws GraphException, TableException, MathException{
		
		FieldGraph fieldDependencyGraph = new FieldGraph();
	
		Map<String,FieldVertex> vertexMap = new HashMap<String,FieldVertex>();
		
		//Add vertex
		List<String> indexStringList= new ArrayList<String>();
		StringBuilder vertexMapKeyBuilder = new StringBuilder();
		MathExpression expression = null;
		FieldVertex v=null;
		for(RecMLVertex rv:oldGraph.getVertexes()){
			expression = recmlAnalyer.getExpression(rv.getExpressionID());
			
			for(MathFactor f:table.getVariableReference(rv.getVariableID()).getMathCI().getM_vecIndexListFactor()){
				indexStringList.add(f.toLegalString());
				vertexMapKeyBuilder.append(f.toLegalString());
			}
			if(vertexMap.containsKey(vertexMapKeyBuilder.toString())){
				v= vertexMap.get(vertexMapKeyBuilder.toString());
			}else{
				v = new FieldVertex();
				for(int i=0;i<indexStringList.size();i++){
					v.setAxisIndex(indexStringList.get(i), i);
				}
				fieldDependencyGraph.addVertex(v);
				vertexMap.put(vertexMapKeyBuilder.toString(),v);
			}
			v.addExpression(expression);
			indexStringList.clear();
			vertexMapKeyBuilder.setLength(0);
		}
		
		//Add Edge
		RecMLVertex srv; // Source RecML Vertex
		RecMLVertex drv; // Dest RecML Vertex
		StringBuilder srvIndexStringBuilder =  new StringBuilder();
		StringBuilder drvIndexStringBuilder =  new StringBuilder();
		FieldVertex sfv = null;
		FieldVertex dfv = null;
		Set<String> addedEdge = new TreeSet<String>();
		
		for(RecMLEdge re:oldGraph.getEdges()){
			srv = oldGraph.getSourceVertex(re);
			drv = oldGraph.getDestVertex(re);
			
			for(MathFactor f:table.getVariableReference(srv.getVariableID()).getMathCI().getM_vecIndexListFactor()){
				srvIndexStringBuilder.append(f.toLegalString());
			}
			if(vertexMap.containsKey(srvIndexStringBuilder.toString())){
				sfv=vertexMap.get(srvIndexStringBuilder.toString());
			}
			
			
			for(MathFactor f:table.getVariableReference(drv.getVariableID()).getMathCI().getM_vecIndexListFactor()){
				drvIndexStringBuilder.append(f.toLegalString());
			}
			if(vertexMap.containsKey(drvIndexStringBuilder.toString())){
				dfv=vertexMap.get(drvIndexStringBuilder.toString());
			}
			
			if(!addedEdge.contains(sfv.toString()+dfv.toString())){
				fieldDependencyGraph.addEdge(new FieldEdge(), sfv, dfv);
				addedEdge.add(sfv.toString()+dfv.toString());
			}
			srvIndexStringBuilder.setLength(0);
			drvIndexStringBuilder.setLength(0);
		}
		
		
		
		return fieldDependencyGraph;
	}

	
	/**
	 * Create a bipartite graph
	 * @param recmlAnalyzer
	 * @return Bipartite graph
	 * @throws GraphException
	 * @attention No at all test
	 */
	public BipartiteGraph<RecMLVertex,RecMLEdge> createBipartiteGraph(RecMLEquationAndVariableContainer contener) throws GraphException{
		BipartiteGraph<RecMLVertex,RecMLEdge> graph = new BipartiteGraph<RecMLVertex, RecMLEdge>();
		
		//Add src side vertexes
		addRecMLVariableVertex(graph,contener);
		
		//Add dst side vertexes
		addRecMLExpressionVertex(graph,contener);
		
		//Add edges
		addRecMLEdges(graph,contener);
		
		//Remove not connected vertexes
		removeNotConnectedVertexes(graph);
		
		return graph;
	}
	
	
	public BipartiteGraph<RecMLVertex,RecMLEdge> createBipartiteGraph_Simple(SimpleRecMLEquationAndVariableContainer contener) throws GraphException{
		BipartiteGraph<RecMLVertex,RecMLEdge> graph = new BipartiteGraph<RecMLVertex, RecMLEdge>();
		
		//Add src side vertexes
		addSimpleRecMLVariableVertex(graph,contener);
		
		//Add dst side vertexes
		addSimpleRecMLExpressionVertex(graph,contener);
		
		//Add edges
		addSimpleRecMLEdges(graph,contener);
		
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
		List<RecMLVertex> removeVertexList = new ArrayList<RecMLVertex>();
		for(RecMLVertex v:graph.getVertexes()){
			if(graph.getEdges(v).isEmpty()){
				removeVertexList.add(v);
			}
				
		}
			for(RecMLVertex rv:removeVertexList){
				graph.removeVertex(rv);
			}
	}


	/**
	 * Add RecML edges from recMLAalyzer
	 * @param graph
	 * @param recmlAnalyzer
	 * @throws GraphException
	 */
	private void addRecMLEdges(
			Graph<RecMLVertex, RecMLEdge> graph,
			RecMLEquationAndVariableContainer contener
			) throws GraphException {
		for(Integer varID:contener.getVariableIDs())
			for(Integer exprID:contener.getEqautionIDsOfVariable(varID)){
				RecMLEdge edge = new RecMLEdge();
				RecMLVertex src = findVarRecMLVertexes(varID, graph);
				RecMLVertex dst = findExpRecMLVertexes(exprID, graph);
				graph.addEdge(edge, src, dst);
				
			}
	}
	private void addSimpleRecMLEdges(
			Graph<RecMLVertex, RecMLEdge> graph,
			SimpleRecMLEquationAndVariableContainer contener
			) throws GraphException {
		for(Integer varID:contener.getVariableIDs())
			for(Integer exprID:contener.getEqautionIDsOfVariable(varID)){
				RecMLEdge edge = new RecMLEdge();
				RecMLVertex src = findVarRecMLVertexes(varID, graph);
				RecMLVertex dst = findExpRecMLVertexes(exprID, graph);
				graph.addEdge(edge, src, dst);
				
			}
	}
	/**
	 * Find RecML vertexes from Math_ci
	 * @param ci
	 * @param graph
	 * @throws GraphException 
	 */
	private RecMLVertex findVarRecMLVertexes(Integer varID,Graph<RecMLVertex,RecMLEdge> graph) throws GraphException{
		for(RecMLVertex v: graph.getVertexes())
			if(v.equalsVarID(varID)) return v;
		
				throw new GraphException();
	}

	/**
	 * Find RecML vertexes from MathExpression
	 * @param ci
	 * @param graph
	 * @throws GraphException 
	 */
	private  RecMLVertex findExpRecMLVertexes(Integer exprID,Graph<RecMLVertex,RecMLEdge> graph) throws GraphException{
		for(RecMLVertex v: graph.getVertexes())
			if(v.equalsExpID(exprID)) return v;
		
		throw new GraphException();
	}

	
	/**
	 * Add a vertex of RecML expression
	 * @param graph
	 * @param recmlAnalyzer
	 * @throws GraphException
	 */
	private void addRecMLExpressionVertex(
			BipartiteGraph<RecMLVertex, RecMLEdge> graph,
			RecMLEquationAndVariableContainer contener
			)throws GraphException {
		for(Integer exprID: contener.getEquationIDs()){
			RecMLVertex v = new RecMLVertex();
			v.setExpression(exprID);
			graph.addDestVertex(v);
		}
		
	}
	private void addSimpleRecMLExpressionVertex(
			BipartiteGraph<RecMLVertex, RecMLEdge> graph,
			SimpleRecMLEquationAndVariableContainer contener
			)throws GraphException {
		for(Integer exprID: contener.getEquationIDs()){
			RecMLVertex v = new RecMLVertex();
			v.setExpression(exprID);
			graph.addDestVertex(v);
		}
		
	}
	/**
	 * Add a vertex of RecML variable
	 * @param graph
	 * @param recmlAnalyzer
	 * @throws GraphException
	 */
	private void addRecMLVariableVertex(
			BipartiteGraph<RecMLVertex, RecMLEdge> graph, 
			RecMLEquationAndVariableContainer contener
			) throws GraphException {
		/* Add new vertexes of recvar*/
		for(Integer varID: contener.getVariableIDs()){
			RecMLVertex v = new RecMLVertex();
			v.setVariable(varID);
			graph.addSourceVertex(v);
		}
	}
	
	private void addSimpleRecMLVariableVertex(
			BipartiteGraph<RecMLVertex, RecMLEdge> graph, 
			SimpleRecMLEquationAndVariableContainer contener
			) throws GraphException {
		/* Add new vertexes of recvar*/
		for(Integer varID: contener.getVariableIDs()){
			RecMLVertex v = new RecMLVertex();
			v.setVariable(varID);
			graph.addSourceVertex(v);
		}
	}
	
}
