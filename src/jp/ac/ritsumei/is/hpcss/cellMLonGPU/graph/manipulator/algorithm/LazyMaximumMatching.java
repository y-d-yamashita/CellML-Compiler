package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.manipulator.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.manipulator.algorithm.comparator.SortVertexDistanceComparator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.SimpleRecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.CCLogger;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.GlobalLogger;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.Pair;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.PairList;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.Triple;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.TripleList;

/**
 * Graph algorithm class for maximum matching
 * @author y-yamashita
 *
 */
public class LazyMaximumMatching {

	/**
	 * Constructor
	 */
	public LazyMaximumMatching() {}
	
	/**
	 * Maximum matching
	 * @param graph
	 * @return Pairs of vertexes
	 * @throws GraphException
	 * @see PairList
	 */
	public PairList<RecMLVertex,RecMLVertex> run(
			BipartiteGraph<RecMLVertex,RecMLEdge> graph, 
			SimpleRecMLAnalyzer 
			simpleRecMLAnalyzer,boolean useExpressionIdFlag) 
			throws GraphException{
		
		if(graph.getSourceVertexSet().size()!=graph.getDestVertexeSet().size()){
			throw new GraphException(
					this.getClass().getName(), 
					new Thread().currentThread().getStackTrace()[1].getMethodName(),
					"\n" +
					"Number of source side vertexes is differential from number of dest side vertexes.\n"+
					"Source:"+graph.getSourceVertexSet().size()+" Dest:"+graph.getDestVertexeSet().size());
		}

		
		PairList<RecMLVertex,RecMLVertex> resultPairList = new PairList<RecMLVertex,RecMLVertex>();
		PairList<RecMLVertex,RecMLVertex> bufferPairList = new PairList<RecMLVertex,RecMLVertex>();
		List<RecMLEdge> bufferPairEdgeList = new ArrayList<RecMLEdge>();
		TripleList<RecMLEdge, RecMLVertex, RecMLVertex> bufferOtherEdges = new TripleList<RecMLEdge, RecMLVertex, RecMLVertex>();
		Stack<RecMLVertex> pathStack = new Stack<RecMLVertex>();
		
		
		preRemoveDeterminedVertexPair(graph, bufferPairList, bufferPairEdgeList, bufferOtherEdges);
		
		
		RecMLVertex source = new RecMLVertex();
		RecMLVertex sink = new RecMLVertex();
	
		source.setSourceFlag(true);
		sink.setSinkFlag(true);

		
		
		//Add source & sink vertexes and edges
		addSourceAndSinkVertexes(graph,source,sink);
		
		//preReverceEdge(graph,source,sink,simpleRecMLAnalyzer,useExpressionIdFlag);
		//preRemoveVertexes(graph, source, sink, simpleRecMLAnalyzer, bufferPairList, bufferPairEdgeList,useExpressionIdFlag);
		
		resultPairList.addAll(bufferPairList);
		do{
			//Get a path from source vertex to sink vertex
			pathStack = getPath(source,sink,graph,pathStack);
			
			//Not find the path
			if(pathStack.isEmpty())
				break;
			
			//Reverse destinations of edges from source to sink
			for(int i=1;i<pathStack.size();i++){
				RecMLVertex src= pathStack.get(i-1);
				RecMLVertex dst = pathStack.get(i);
				graph.reverseEdge(src,dst);
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
				throw new GraphException(this.getClass().getName(),
						new Throwable().getStackTrace()[0].getMethodName(),
						"Dest vertex too many.");
					
			RecMLVertex src = graph.getDestVertex(outEdgeOfDst);
			
			/* src does not be variable vertex, throw exception*/
			if(!(src.hasVariable()||src.hasExpression()))
				throw new GraphException(this.getClass().getName(),
						new Throwable().getStackTrace()[0].getMethodName(),
						"Source vertex lacks variable or expression");

			/* dst does not be expression vertex, throw exception */
				else if(dst.hasVariable()&&dst.hasExpression())
					throw new GraphException(this.getClass().getName(),
							new Throwable().getStackTrace()[0].getMethodName(),
							"Dest vertex lacks variable or expression");
			
			//Set pair of matched vertexes
			Pair<RecMLVertex,RecMLVertex> pair = 
					new Pair<RecMLVertex, RecMLVertex>(src, dst);
			resultPairList.add(pair);
		}
		
		//Remove source & sink vertexes and edges
		removeSourceAndSink(graph,source,sink);
		
		//Restore graph
		for(Pair<RecMLVertex,RecMLVertex> pair : bufferPairList){
			graph.addSourceVertex(pair.getFirst());
			graph.addVertex(pair.getSecond());
			graph.addEdge(bufferPairEdgeList.get(bufferPairList.indexOf(pair)), pair.getSecond(),pair.getFirst());
		}
		for(Triple<RecMLEdge,RecMLVertex,RecMLVertex> triple : bufferOtherEdges){
			graph.addEdge(triple.getFirst(), triple.getSecond(), triple.getThird());
		}
	
		return resultPairList;
	}
	
	/**
	 * Remove determined vertex pairs.
	 * ex. exp0: x = 2
	 * 	   exp1: y = x +1
	 * 	   x and exp0 is obviously a pair. 
	 * 	   And y and exp1 is a pair when x & exp0 pair is determined.
	 * @param graph: bipartite graph
	 * @param pairList: matching result
	 * @param pairEdgeList: edges of removed pairs
	 * @param otherEdgeList: removed vertexes had edges
	 * @throws GraphException
	 * @author y-yamashita
	 */
	private void preRemoveDeterminedVertexPair(
			BipartiteGraph<RecMLVertex,RecMLEdge> graph,
			PairList<RecMLVertex,RecMLVertex> pairList,
			List<RecMLEdge> pairEdgeList,
			TripleList<RecMLEdge, RecMLVertex, RecMLVertex> otherEdgeList) throws GraphException{
		
		CCLogger.log(this.getClass(),"Start...");
		int count = 0;

		Queue<RecMLVertex> queue = new LinkedList<RecMLVertex>();
		
		//Stack vertexes which of degrees are 1
		for(RecMLVertex v : graph.getVertexes()){
			//Decrement to ignore source and sink edges
			int degree = graph.getEdges(v).size(); 

			if(degree < 2){		
				//Not allowed a vertex has no edge
				if(degree < 1){
					throw new GraphException(this.getClass().getName(),
							Thread.currentThread().getStackTrace()[1].getMethodName(),
							"Vertex"+v.toString()+" has no vertex.");
				}
				//Push a vertex has one edge
				queue.offer(v);

			}
		}
		
		
		//Stack is empty equals there is no determined pair
		while(!queue.isEmpty()){
			count++;
			
			//Pop a vertex only has one edge
			RecMLVertex v = queue.poll();
			
			//Either variable vertex or expression vertex
			if(v.hasVariable() && v.hasExpression()){
				throw new GraphException(this.getClass().getName(),
						Thread.currentThread().getStackTrace()[1].getMethodName(),
						"Vertex"+v.toString()+" has no vertex.");
			}
			
			//If v is variable vertex (source side vertex)
			if(v.hasVariable()){

				RecMLVertex expVertex = null;
				try{
					expVertex = graph.getOutOfVertex(v).iterator().next();
				}catch(NullPointerException e){
					CCLogger.log(this.getClass(),v.toString());
					throw new GraphException(this.getClass().getName(),
							Thread.currentThread().getStackTrace()[1].getMethodName(),
							"No sink side vertex");
				}

				//Remove pair vertexes from graph
				removeVertexPairFromGraph(v, expVertex, graph, pairList, pairEdgeList, otherEdgeList, queue);
			}
			
			
			//If v is expression vertex (sink side vertex)			
			if(v.hasExpression()){
				RecMLVertex varVertex = null;
				try{
					varVertex = graph.getInOfVertex(v).iterator().next();
				}catch(NullPointerException e){
					CCLogger.log(this.getClass(),v.toString());
					throw new GraphException(this.getClass().getName(),
							Thread.currentThread().getStackTrace()[1].getMethodName(),
							"No source side vertex");
				}
				
				//Remove pair vertexes from graph
				removeVertexPairFromGraph(varVertex,v, graph, pairList, pairEdgeList, otherEdgeList, queue);
			}
		}		
		CCLogger.log(this.getClass(),"Finish, "+count +" pairs removed.");
		CCLogger.log("Removed result >> Source:"+graph.getSourceVertexSet().size()+" Dest:"+graph.getDestVertexeSet().size());

	}
	
	
	/**
	 * Remove vertexes, when a variable vertex has one edge
	 * @param graph
	 * @param pairList
	 * @param pairEdgeList
	 * @param otherEdgeList
	 * @throws GraphException
	 * @author y-yamashita
	 * @see preRemoveDeterminedVertexPair()
	 */
	private void removeVertexPairFromGraph(
			RecMLVertex varVertex,
			RecMLVertex expVertex,
			BipartiteGraph<RecMLVertex,RecMLEdge> graph,
			PairList<RecMLVertex,RecMLVertex> pairList,
			List<RecMLEdge> pairEdgeList,
			TripleList<RecMLEdge, RecMLVertex, RecMLVertex> otherEdgeList,
			Queue<RecMLVertex> queue) throws GraphException{

		//Add a matching pair
		pairList.add(new Pair<RecMLVertex, RecMLVertex>(varVertex,expVertex));
		
		//Save and remove an edge of the pair
		RecMLEdge pairEdge = graph.getEdge(varVertex, expVertex); 
		pairEdgeList.add(pairEdge);
		graph.removeEdge(pairEdge);
		
		//Save and remove edges that vertex has, and check degree 
		for(RecMLEdge e  : graph.getOutEdges(varVertex)){
			RecMLVertex dst = graph.getDestVertex(e);
			
			//Save and remove an edge
			otherEdgeList.add(new Triple<RecMLEdge,RecMLVertex,RecMLVertex>(e,varVertex,dst));			

			
			//Check degree
			//Note: degree is decremented due to ConcurrentModificationException
			if(graph.getEdges(dst).size() == 2){
				queue.offer(dst);
			}
		}
		

		//Save and remove edges that vertex has, and check degree 
		for(RecMLEdge e  : graph.getInEdges(expVertex)){
			RecMLVertex src = graph.getSourceVertex(e);
			
			//Save and remove an edge
			otherEdgeList.add(new Triple<RecMLEdge,RecMLVertex,RecMLVertex>(e,src,expVertex));
			
			//Check degree
			if(graph.getEdges(src).size() == 2){
				queue.offer(src);
				}
		}
		
		
		//Remove pair vertexes from graph (Automatically remove related edges)
		if(queue.contains(varVertex)){
			queue.remove(varVertex);
		}
		if(queue.contains(expVertex)){
			queue.remove(expVertex);
		}
		graph.removeSourceVertex(varVertex);
		graph.removeDestVertex(expVertex);
		
		
		}
	

	
	
	/**
	 * Reverse path edges when left hand side has one variable in equations
	 * @param graph
	 * @param source
	 * @param sink
	 * @param simpleRecMLAnalyzer
	 * @param useExpressionIdFlag
	 * @throws GraphException 
	 */
	private void preReverceEdge(
			BipartiteGraph<RecMLVertex, RecMLEdge> graph, RecMLVertex source,
			RecMLVertex sink, SimpleRecMLAnalyzer simpleRecMLAnalyzer,
			boolean useExpressionIdFlag) throws GraphException {
			
			CCLogger.log(this.getClass(),"Start...");
			int preRvercedCount = 0;

		
			Vector<MathExpression> exprVector = simpleRecMLAnalyzer.getM_vecMathExpression();
			Map<Integer,String> originVariableMap = simpleRecMLAnalyzer.getRecMLVariableTable().getVariableList();
			Map<String,Integer> variableMap = new HashMap<String, Integer>();
			
			Map<Integer,RecMLVertex> variableVertexMap = new HashMap<Integer, RecMLVertex>();
			Map<Integer,RecMLVertex> equationVertexMap = new HashMap<Integer, RecMLVertex>();
			
			
			
			//swap key and value
			for(Integer key : originVariableMap.keySet()){
				variableMap.put(originVariableMap.get(key),key);
			}
			
			//create vertexMap
			for(RecMLVertex v : graph.getSourceVertexSet()){
				variableVertexMap.put(v.getVariableID(), v);
			}
			
			for(RecMLVertex v : graph.getDestVertexeSet()){
				equationVertexMap.put(v.getExpressionID(), v);
			}
			
			
			for(MathExpression expr: exprVector){
				if(expr.getLeftExpression().getRealVariableCount() == 1){
					String varInLHS = null;
					try {
						varInLHS = expr.getLeftExpression().getFirstVariable().toLegalString();
					} catch (MathException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Integer varID = variableMap.get(varInLHS);
					Integer exprID = -1;

					if(useExpressionIdFlag == true){
						exprID = (int) expr.getExID();
					}else{
						exprID = exprVector.indexOf(expr);
					}
					RecMLVertex varVertex = variableVertexMap.get(varID);
					RecMLVertex equVertex = equationVertexMap.get(exprID);

									
					//Reverse edges 
						graph.reverseEdge(source, varVertex);
						graph.reverseEdge(varVertex, equVertex);
						graph.reverseEdge(equVertex, sink);
						preRvercedCount++;
				}
			}
			CCLogger.log(this.getClass(),"Finish, "+preRvercedCount +" times revesed.");
	}

	/**
	 * Reverse path edges when left hand side has one variable in equations
	 * @param graph
	 * @param source
	 * @param sink
	 * @param simpleRecMLAnalyzer
	 * @param useExpressionIdFlag
	 * @throws GraphException 
	 */
	private void preRemoveVertexes(
			BipartiteGraph<RecMLVertex, RecMLEdge> graph, RecMLVertex source,
			RecMLVertex sink, SimpleRecMLAnalyzer simpleRecMLAnalyzer,
			PairList<RecMLVertex,RecMLVertex> bufferPairList,
			List<RecMLEdge> bufferRecMLEdgeList,
			boolean useExpressionIdFlag) throws GraphException {
			
			CCLogger.log(this.getClass(),"Start...");
			int removeCount = 0;

		
			Vector<MathExpression> exprVector = simpleRecMLAnalyzer.getM_vecMathExpression();
			Map<Integer,String> originVariableMap = simpleRecMLAnalyzer.getRecMLVariableTable().getVariableList();
			Map<String,Integer> variableMap = new HashMap<String, Integer>();
			
			Map<Integer,RecMLVertex> variableVertexMap = new HashMap<Integer, RecMLVertex>();
			Map<Integer,RecMLVertex> equationVertexMap = new HashMap<Integer, RecMLVertex>();
			
			
			
			//swap key and value
			for(Integer key : originVariableMap.keySet()){
				variableMap.put(originVariableMap.get(key),key);
			}
			
			//create vertexMap
			for(RecMLVertex v : graph.getSourceVertexSet()){
				variableVertexMap.put(v.getVariableID(), v);
			}
			
			for(RecMLVertex v : graph.getDestVertexeSet()){
				equationVertexMap.put(v.getExpressionID(), v);
			}
			
			
			for(MathExpression expr: exprVector){
				if(expr.getLeftExpression().getRealVariableCount() == 1){
					String varInLHS = null;
					try {
						varInLHS = expr.getLeftExpression().getFirstVariable().toLegalString();
					} catch (MathException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Integer varID = variableMap.get(varInLHS);
					Integer exprID = -1;

					if(useExpressionIdFlag == true){
						exprID = (int) expr.getExID();
					}else{
						exprID = exprVector.indexOf(expr);
					}
					RecMLVertex varVertex = variableVertexMap.get(varID);
					RecMLVertex equVertex = equationVertexMap.get(exprID);

						
					bufferPairList.add(new Pair<RecMLVertex, RecMLVertex>(varVertex,equVertex));
					bufferRecMLEdgeList.add(graph.getEdge(varVertex, equVertex));
					
					//Remove vertex and edges 
					graph.removeSourceVertex(varVertex);
					graph.removeDestVertex(equVertex);
					
					removeCount++;
				}
			}
			CCLogger.log(this.getClass(),"Finish, "+removeCount +" pairs removed.");
			CCLogger.log("Removed result >> Source:"+graph.getSourceVertexSet().size()+" Dest:"+graph.getDestVertexeSet().size());

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
					//depthFirstSearchPrioritizeGoal(next, goal, graph, stack);
			}
			if(!stack.peek().equals(goal))
				stack.pop();
			
	}
	/**
	 * Depth first search method
	 * @param cur current vertex
	 * @param goal goal vertex (sink vertex)
	 * @param graph
	 * @param stack
	 */
	private void depthFirstSearchPrioritizeGoal(RecMLVertex cur, RecMLVertex goal,
			BipartiteGraph<RecMLVertex, RecMLEdge> graph,
			Stack<RecMLVertex> stack) {
			stack.push(cur);
			cur.setVisited(true);

			if(!stack.peek().equals(goal)){
				List<RecMLVertex> outOfVertexList = new ArrayList<RecMLVertex>();
				//Check next vertexes whether goal vertex
				for(RecMLVertex next:graph.getOutOfVertex(cur)){
					if(next.equals(goal)){
						depthFirstSearchPrioritizeGoal(next, goal, graph, stack);
						outOfVertexList.clear();
						break;
					}
					outOfVertexList.add(next);
				}

				Collections.sort(outOfVertexList,new SortVertexDistanceComparator(graph, goal));

				for(RecMLVertex next:outOfVertexList){
					if(stack.peek().equals(goal)){
						break;
					}
					if(!next.isVisited()){
						depthFirstSearchPrioritizeGoal(next, goal, graph, stack);
					}
				}

				if(!stack.peek().equals(goal)){
					stack.pop();
				}
			}
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
