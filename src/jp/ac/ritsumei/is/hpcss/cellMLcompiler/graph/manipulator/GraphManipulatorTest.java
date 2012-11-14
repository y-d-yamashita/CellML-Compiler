package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.manipulator;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.FieldGraph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.FieldVertexGroupList;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.Graph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.MathExpressionLoop;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.SimpleRecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.RecMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.SimpleRecMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.List2D;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.Pair;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.PairList;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.Triple;

import org.junit.Test;
import org.junit.rules.TestName;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Test for GrapphManipulator Class
 * @author y-yamashita
 *
 */
public class GraphManipulatorTest {

/* Shared */
static GraphManipulator graphManipulator= new GraphManipulator();
static RecMLAnalyzer recmlAnalyzer = null;

static SimpleRecMLAnalyzer simpleRecmlAnalyzer = null;

/* Result of each test */
static BipartiteGraph<RecMLVertex,RecMLEdge> resultTestCreateBipartiteGraph=null;
static DirectedGraph<RecMLVertex,RecMLEdge> resultTestCreateDependencyGraph=null;
static PairList<RecMLVertex,RecMLVertex> resultTestMaximumMatching=null;
static List2D<RecMLVertex> resultTestTrajan=null;
static FieldGraph resultTestCreateFieldGraph=null;
static FieldVertexGroupList resutlTestSeparateLoop=null;
static MathExpressionLoop resultTestCreateLoop=null;

/* Select recml file*/
String xml=
//"./model/recml/RecMLSample/FHN_FTCS_simple_2x3x3.recml"
//"./model/recml/RecMLSample/ArbitraryModel_1D_simple.recml"
//"./model/recml/RecMLSample/ArbitraryModel_1D_simple_v2_yamashita.recml"
"./model/recml/SimpleKawabataTestSample/SimpleRecMLSample001.recml"
;


@Test
public void testCreateBipartieGraph() {
	/*Parse RecML*/
	recmlAnalyzer = parse(xml);
	simpleRecmlAnalyzer = parse_simple(xml);


	/*
	 * Create a variable container.
	 * The container is necessary to create 
	 * a bipartite graph between variables and equations.
	 */
	RecMLEquationAndVariableContainer container = 
			new RecMLEquationAndVariableContainer(recmlAnalyzer,recmlAnalyzer.getRecMLVariableTable());
	
	SimpleRecMLEquationAndVariableContainer container2 = 
			new SimpleRecMLEquationAndVariableContainer(simpleRecmlAnalyzer,simpleRecmlAnalyzer.getRecMLVariableTable());

	/* Create a bipartite graph */
	try {		
//		resultTestCreateBipartiteGraph = graphManipulator.createBipartiteGraph(container);
		resultTestCreateBipartiteGraph = graphManipulator.createBipartiteGraph_Simple(container2);
	} catch (GraphException e) {
		e.printStackTrace();
	}
	
	/* Print result*/
	System.out.println("<<testCreateBipartieGraph>> -----------------------------");
	System.out.println(graphManipulator.toRecMLXMLString(resultTestCreateBipartiteGraph,null));

	assertNotNull(resultTestCreateBipartiteGraph);

}



@Test
public void testMaximumMatching() {
	/*Maximum matching*/
	try {
		resultTestMaximumMatching = 
				graphManipulator.maximumMatching(resultTestCreateBipartiteGraph);
	} catch (GraphException e) {
		e.printStackTrace();
	}
	assertNotNull(resultTestMaximumMatching);
	
	/* Print result*/
	System.out.println("<<testMaximumMatchin>> -----------------------------");
	for(Pair<RecMLVertex, RecMLVertex> p:resultTestMaximumMatching){
		System.out.println(p.getFirst()+","+p.getSecond());
	}
}




@Test
	public void testCretateDependencyGraph() {
		/* Create a dependency graph from a maximum matching 
		 * result and a bipartite graph */
		try {
			resultTestCreateDependencyGraph =
					graphManipulator.cretateDependencyGraph(resultTestMaximumMatching
												           ,resultTestCreateBipartiteGraph);
		} catch (GraphException e) {
			e.printStackTrace();
		}
		
		assertNotNull(resultTestCreateDependencyGraph);
		
		/*Print result*/
		System.out.println("<<testCretateDependencyGraph>>------------");
		System.out.println(resultTestCreateDependencyGraph);
	}


	@Test
	public void testTarjan() {
		resultTestTrajan = graphManipulator.tarjan(resultTestCreateDependencyGraph);
		System.out.println("<<testCretateDependencyGraph>>------------");
		
		System.out.println(graphManipulator.toRecMLXMLString(resultTestCreateDependencyGraph, resultTestTrajan));
	}
	
	
	@Test
	public void testCreateFieldDependencyGraph() {
	try {
		 resultTestCreateFieldGraph = 
				 graphManipulator.cretateFieldDependencyGraph(
						 resultTestCreateDependencyGraph,
						 recmlAnalyzer);
	} catch (TableException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (MathException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (GraphException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	assertNotNull(resultTestCreateFieldGraph);
	
	System.out.println("<<Field Graph>>------------------------------");
	System.out.println(graphManipulator.toRecMLXMLString(resultTestCreateFieldGraph));
	
	MathExpression expr0=null;
	for(FieldVertex v:resultTestCreateFieldGraph.getVertexes()){
		System.out.println("Field vertex["+v.getId()+"]:");
		if(expr0==null){
			expr0=v.getExpressionList().get(0);
		}
		for(MathExpression expr:v.getExpressionList()){
			try {
				System.out.println("	"+expr.toLegalString());
				System.out.println("	"+expr0.toLegalString());
				System.out.println("compare expr[0]:"+expr0.compareFocusOnVariableIndex(expr, 1)+"\n");
			} catch (MathException e) {
				e.printStackTrace();
			}
		}
	}
}
	

	@Test
	public void testSeparateLoop(){
		resutlTestSeparateLoop =	graphManipulator.separateLoopGroup(resultTestCreateFieldGraph);
		assertNotNull(resutlTestSeparateLoop);
		
		System.out.println("<<testSeparateLoop>>------------------------------");
		System.out.println(resutlTestSeparateLoop.toString());

	}
	
	
	@Test
	public void testCreateLoop(){
		try {
			resultTestCreateLoop = graphManipulator.createLoop(resutlTestSeparateLoop);
		} catch (MathException e) {
			e.printStackTrace();
		}
		
		System.out.println("<<testCreateLoop>>------------------------------");
		System.out.println(resultTestCreateLoop.toString());

	}

	
	/* RecML parse method (Not test)*/
	private static RecMLAnalyzer parse(String xml){
		RecMLAnalyzer pRecMLAnalyzer = new RecMLAnalyzer();
		
		//---------------------------------------------------
		//XMLパーサ初期化
		//---------------------------------------------------
		// create parser
		XMLReader parser = null;
		try {
			parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			parser.setProperty("http://apache.org/xml/properties/input-buffer-size",
					new Integer(16 * 0x1000));
		} catch (Exception e) {
			System.err.println("error: Unable to instantiate parser ("
					+ "org.apache.xerces.parsers.SAXParser" + ")");
			System.exit(1);
		}

		XMLHandler handler = new XMLHandler(pRecMLAnalyzer);
		parser.setContentHandler(handler);
		try {
			parser.parse(xml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*selector内cnのInteger*/
		pRecMLAnalyzer.changeAllSelectorInteger();
		
		/*selector削除*/
		pRecMLAnalyzer.removeAllSelector();
		
		/*Create variable table from variable information in RecmlAnalyzer*/
		pRecMLAnalyzer.createVariableTable();
		
		/*Attach information about assignment and reference equations*/
		pRecMLAnalyzer.setLeftsideRightsideVariable();

		/*Set variable type (ex. recvar, constvara)*/
		pRecMLAnalyzer.setRefVariableType();

		/** 内容確認 ***/
		try {
			pRecMLAnalyzer.printContents();
		} catch (MathException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return pRecMLAnalyzer;
	}

	/* RecML parse method (Not test)*/
	private static SimpleRecMLAnalyzer parse_simple(String xml){
		SimpleRecMLAnalyzer pSimpleRecMLAnalyzer = new SimpleRecMLAnalyzer();
		
		//---------------------------------------------------
		//XMLパーサ初期化
		//---------------------------------------------------
		// create parser
		XMLReader parser = null;
		try {
			parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			parser.setProperty("http://apache.org/xml/properties/input-buffer-size",
					new Integer(16 * 0x1000));
		} catch (Exception e) {
			System.err.println("error: Unable to instantiate parser ("
					+ "org.apache.xerces.parsers.SAXParser" + ")");
			System.exit(1);
		}

		XMLHandler handler = new XMLHandler(pSimpleRecMLAnalyzer);
		parser.setContentHandler(handler);
		try {
			parser.parse(xml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*selector内cnのInteger*/
		pSimpleRecMLAnalyzer.changeAllSelectorInteger();
		
		/*selector削除*/
		pSimpleRecMLAnalyzer.removeAllSelector();
		
		/*Create variable table from variable information in RecmlAnalyzer*/
		pSimpleRecMLAnalyzer.createVariableTable();
		
		/*Attach information about assignment and reference equations*/
		pSimpleRecMLAnalyzer.setLeftsideRightsideVariable();

		/*Set variable type (ex. recvar, constvara)*/
		pSimpleRecMLAnalyzer.setRefVariableType();

		/** 内容確認 ***/
		try {
			pSimpleRecMLAnalyzer.printContents();
		} catch (MathException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return pSimpleRecMLAnalyzer;
	}
	
	/* Stub method to create bibiatie graph (Not test)*/
	private static BipartiteGraph<RecMLVertex, FieldEdge>stubCreateBipartieGraph() throws GraphException{
		
		//Create sample bipartite graph
		BipartiteGraph<RecMLVertex, FieldEdge> graph = new BipartiteGraph<RecMLVertex, FieldEdge>();
		
		RecMLVertex var1 = new RecMLVertex();
		var1.setVariable(1);
		RecMLVertex var2 = new RecMLVertex();
		var2.setVariable(2);
		RecMLVertex var3 = new RecMLVertex();
		var3.setVariable(3);
		graph.addSourceVertex(var1);
		graph.addSourceVertex(var2);
		graph.addSourceVertex(var3);
			

		RecMLVertex expr1 = new RecMLVertex();
		expr1.setExpression(1);
		RecMLVertex expr2 = new RecMLVertex();
		expr2.setExpression(2);
		RecMLVertex expr3 = new RecMLVertex();
		expr3.setExpression(3);
		graph.addDestVertex(expr1);
		graph.addDestVertex(expr2);
		graph.addDestVertex(expr3);


		graph.addEdge(new FieldEdge(), var1, expr1);
		graph.addEdge(new FieldEdge(), var2, expr2);
		graph.addEdge(new FieldEdge(), var3, expr3);
		graph.addEdge(new FieldEdge(), var1, expr3);
		graph.addEdge(new FieldEdge(), var3, expr1);
		return graph;
	}
	
	
}
