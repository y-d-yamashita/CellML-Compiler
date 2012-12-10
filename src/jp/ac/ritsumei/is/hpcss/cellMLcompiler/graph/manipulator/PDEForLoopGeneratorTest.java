package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.manipulator;

import org.junit.Test;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.Graph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.Pair;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.PairList;

public class PDEForLoopGeneratorTest {

	GraphManipulator gManipulator= new GraphManipulator();
	RecMLAnalyzer recAnalyzer = new RecMLAnalyzer();
	
	@Test
	public void testCretateDependencyGraph() {
		BipartiteGraph<RecMLVertex, RecMLEdge> graph = null;
		PairList<RecMLVertex,RecMLVertex> pairList=null;
		try {
			graph = createBipartieGraph();
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(graph);
		try {
			pairList = gManipulator.maximumMatching(graph);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Graph<RecMLVertex, RecMLEdge> dg = null;
		try {
		dg=gManipulator.cretateDependencyGraph(pairList, graph);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Dependency Graph------------");
		System.out.println(dg);
	}
	
	@Test
	public void testMaximumMatching() {
		BipartiteGraph<RecMLVertex, RecMLEdge> graph = null;
		PairList<RecMLVertex,RecMLVertex> pairList=null;
		try {
			graph = createBipartieGraph();
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(graph);
		try {
			pairList = gManipulator.maximumMatching(graph);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Matching------------------");
		for(Pair<RecMLVertex, RecMLVertex> p:pairList){
			System.out.println(p.getFirst()+","+p.getSecond());
		}
	}
	
	BipartiteGraph<RecMLVertex, RecMLEdge>createBipartieGraph() throws GraphException{
		BipartiteGraph<RecMLVertex, RecMLEdge> graph = new BipartiteGraph<RecMLVertex, RecMLEdge>();
		
		RecMLVertex var1 = new RecMLVertex(new Math_ci("1"));
		RecMLVertex var2 = new RecMLVertex(new Math_ci("2"));
		RecMLVertex var3 = new RecMLVertex(new Math_ci("3"));
		graph.addSourceVertex(var1);
		graph.addSourceVertex(var2);
		graph.addSourceVertex(var3);
			

		RecMLVertex expr1 = new RecMLVertex(new MathExpression(var1.getVariable()));
		RecMLVertex expr2 = new RecMLVertex(new MathExpression(var2.getVariable()));
		RecMLVertex expr3 = new RecMLVertex(new MathExpression(var3.getVariable()));
		graph.addDestVertex(expr1);
		graph.addDestVertex(expr2);
		graph.addDestVertex(expr3);


		graph.addEdge(new RecMLEdge(), var1, expr1);
		graph.addEdge(new RecMLEdge(), var2, expr2);
		graph.addEdge(new RecMLEdge(), var3, expr3);
		graph.addEdge(new RecMLEdge(), var1, expr3);
		graph.addEdge(new RecMLEdge(), var3, expr1);
		return graph;
	}
}