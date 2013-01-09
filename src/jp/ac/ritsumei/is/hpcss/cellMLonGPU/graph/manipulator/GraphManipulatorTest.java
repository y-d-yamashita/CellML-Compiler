package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.manipulator;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.FieldGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.FieldVertexGroupList;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.Graph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.MathExpressionLoop;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.field.FieldEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.field.FieldVertex;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.util.MathCollections;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.RecMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.SimpleRecMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver.LeftHandSideTransposition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.RecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.RecMLVariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.CCLogger;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.Pair;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.PairList;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.List2D;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.Triple;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.sort.ExpressionComparatorByIndex;

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

//////////////////////////////////////////////////////////////////
//**************** Using SimpleRecMLAnalyzer *******************//
static SimpleRecMLAnalyzer recmlAnalyzer = null;                //
static SimpleRecMLEquationAndVariableContainer container =null; //
//----------------------------------------------------------------
//****************    Using RecMLAnalyzer    *******************//
//static RecMLAnalyzer recmlAnalyzer = null;                    //
//static RecMLEquationAndVariableContainer container =null;     //
//////////////////////////////////////////////////////////////////

/* Result of each test */
static BipartiteGraph<RecMLVertex,RecMLEdge> resultTestCreateBipartiteGraph=null;
static DirectedGraph<RecMLVertex,RecMLEdge> resultTestCreateDependencyGraph=null;
static PairList<RecMLVertex,RecMLVertex> resultTestMaximumMatching=null;
static List2D<RecMLVertex> resultTestTrajan=null;
static FieldGraph resultTestCreateFieldGraph=null;
static FieldVertexGroupList resutlTestSeparateLoop=null;
static MathExpressionLoop resultTestCreateLoop=null;
static List2D<RecMLVertex> resultTestCompressDependencyList=null;


/* Select recml file*/
String xml=
//"./model/recml/RecMLSample/FHN_FTCS_simple_2x3x3_v2_yamashita.recml"
//"./model/recml/RecMLSample/ArbitraryModel_1D_simple.recml"
//"./model/recml/RecMLSample/ArbitraryModel_1D_simple_v2_yamashita.recml"
//"./model/recml/RecMLSample/ArbitraryModel_1D_simple_v3_yamashita_strict.recml" //TEST CLEAR!
//"./model/recml/RecMLSample/ArbitraryModel_1D_simple_v2_notime_yamashita.recml"
//"./model/recml/RecMLSample/FHN_FTCS_2D_simple_v1.recml"
//"./model/recml/RecMLSample/FHN_FTCS_2D_simple_v1_yamashita.recml"
//"./model/recml/SimpleKawabataTestSample/SimpleRecMLSample001.recml"
//"./model/recml/RecMLSample/LR1_FTCS_2D_struct_v3.recml"
//"./model/recml/SimpleRecMLSample/SimpleRecMLSampleRustyYamashita/LR1_FTCS_2D_simple_generated.recml"
//"./model/recml/RecMLSample/LR1_FTCS_2D_struct_v3.recml"
//"./model/recml/RecMLSample/LR1_FTCS_2D_simple_v4_yamashita.recml"
//"./model/recml/RecMLSample/LR1_FTCS_2D_simple_v5_yamashia.recml"
//"./model/recml/RecMLSample/LR1_FTCS_2D_simple_v6_50x50_yamashita.recml"
//"./model/recml/RecMLSample/LR1_FTCS_2D_simple_v6_30x30_yamashita.recml"
"./model/recml/RecMLSample/LR1_FTCS_2D_simple_v6_10x10_yamashita_3.recml"
//"./model/recml/RecMLSample/LR1_FTCS_2D_simple_v6_3x3_yamashita.recml"
//"./model/recml/RecMLSample/LR1_FTCS_2D_simple_v6_5x5_yamashita.recml"
;


@Test
public void testCreateBipartieGraph() {
	CCLogger.log(this.getClass(), "START TEST");

	/*Parse RecML*/
	
	//recmlAnalyzer = parse(xml);
	recmlAnalyzer = simpleRecMLParse(xml);

	
	/*
	 * Create a variable container.
	 * The container is necessary to create 
	 * a bipartite graph between variables and equations.
	 */
	container = 
			new SimpleRecMLEquationAndVariableContainer(recmlAnalyzer,recmlAnalyzer.getRecMLVariableTable());

	//container = 
	//		new RecMLEquationAndVariableContainer(recmlAnalyzer,recmlAnalyzer.getRecMLVariableTable());

	//System.out.println(container);
	/* Create a bipartite graph */
	try {		
		//resultTestCreateBipartiteGraph = graphManipulator.createBipartiteGraph(container);
		resultTestCreateBipartiteGraph = graphManipulator.createBipartiteGraph_Simple(container);
	} catch (GraphException e) {
		e.printStackTrace();
	}
	
	/* Print result*/
	System.out.println("<<testCreateBipartieGraph>> -----------------------------");
	//System.out.println(graphManipulator.toRecMLXMLString(resultTestCreateBipartiteGraph,null));

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
public void testLeftHandSideTransport() throws MathException, TableException{
	System.out.println(new Thread().currentThread().getStackTrace()[1].getMethodName()+">>> -----------------");
	for(Pair<RecMLVertex, RecMLVertex> p:resultTestMaximumMatching){
		System.out.print(p.getFirst()+"("+recmlAnalyzer.getRecMLVariableTable().getVariableReference(p.getFirst().getVarId()).getMathCI().toLegalString()+")"+", ");
		System.out.print(p.getSecond()+"("+recmlAnalyzer.getExpression(p.getSecond().getEquId()).toLegalString()+")");
	//	MathExpression lhs = recmlAnalyzer.getExpression(p.getSecond().getEquId()).getLeftExpression();
	//	MathOperator apply = (MathOperator) lhs.getRootFactor();
	//	System.out.println(" leftRoot:"+apply.getChildFactor(1).toLegalString());
	}
	
	
	System.out.println("Transport>>>");
	for(Pair<RecMLVertex, RecMLVertex> p:resultTestMaximumMatching){
		Math_ci target =  recmlAnalyzer.getRecMLVariableTable().getVariableReference(p.getFirst().getVarId()).getMathCI();
		MathExpression expr = recmlAnalyzer.getExpression(p.getSecond().getEquId());
		
	//	if(!((MathOperator) expr.getLeftExpression().getRootFactor()).getChildFactor(1).toLegalString().equals(target.toLegalString())){
	//		System.out.print(p.getFirst()+"("+recmlAnalyzer.getRecMLVariableTable().getVariableReference(p.getFirst().getVarId()).getMathCI().toLegalString()+")"+",");
	//		System.out.println(p.getSecond()+"("+recmlAnalyzer.getExpression(p.getSecond().getEquId()).toLegalString()+")");
			LeftHandSideTransposition lst = new LeftHandSideTransposition();
	//		System.out.println((lst.transporseExpression(expr, target)).toLegalString());
			CCLogger.log(expr.toLegalString()+target.toLegalString());
			if(!lst.check_EqDirectLeft(expr, target)){
				recmlAnalyzer.setExpression(p.getSecond().getEquId(),lst.transporseExpression(expr, target));
			}
				//	}
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
	public void testCompressDependencyList(){
		/*
		 * index position:   0   1   2 ...
		 *                x [n] [l] [k]...
		 */
		int indexPosition =1;
		//Sort to calculation order
		   Collections.reverse(resultTestTrajan);
		   
			int order=0;
			for(List<RecMLVertex> list: resultTestTrajan){
				for(RecMLVertex v:list){
					v.setOrder(order);
					order++;
				}
			}
		
		   
		resultTestCompressDependencyList = graphManipulator.compressDependencyList(resultTestTrajan, resultTestCreateDependencyGraph);
		System.out.println("<<testCompressDependencyList>>------------");
		System.out.println(resultTestTrajan.toString());
		System.out.println(resultTestCompressDependencyList.toString());

		List<MathExpression> exprList = new ArrayList<MathExpression>();
		
		MathExpressionLoop root = new MathExpressionLoop();

		for(List<RecMLVertex> list:resultTestCompressDependencyList){
			//Better result is gained than not reversing
			Collections.reverse(list);
			
			for(RecMLVertex v:list){
					exprList.add(container.getEquation(v.getEquId()));
		}

			//Sort exprlist based on index number and expression shape
			Collections.sort(exprList,new ExpressionComparatorByIndex(indexPosition,exprList.size()));
		
			System.out.println("[");
			for(MathExpression expr:exprList){
				try {
					System.out.println(expr.toLegalString());
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("]");

			List<MathExpression> mergedExprList = new ArrayList<MathExpression>();
			MathExpression prevExpr=null;
			MathExpression pushExpr=null;
			boolean replacedFlag=false;
			
			prevExpr = exprList.get(0);
			pushExpr = exprList.get(0);
			
			MathExpressionLoop loop = new MathExpressionLoop();
			loop.addMathExpression(pushExpr);
			root.addLoop(loop);
			
			//Specify loop index
			loop.setIndexFactor(new Math_ci("i"));
			
			//Set index ex. x[0][1]-> startIndex:1, endIndex:1

			if(pushExpr.getLeftExpression().getFirstVariable().getIndexList().size() > indexPosition){
			
			Math_cn index =  MathCollections.calculate(pushExpr.getLeftExpression().getFirstVariable().getIndexList().get(indexPosition));
			loop.setSatrtLoopIndex(index.decode());
			loop.setEndLoopIndex(index.decode());
			}
			mergedExprList.add(pushExpr);
			
			for(int i=1;i<exprList.size();i++){
				Integer compareResult = null;
				try {
					//Confirm that expression can be merge
					compareResult = prevExpr.compareFocusOnVariableIndex(exprList.get(i), indexPosition);
					
					
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				System.out.println("result:"+compareResult);
				try {
					System.out.println(prevExpr.toLegalString());
					System.out.println(exprList.get(i).toLegalString());
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				if(compareResult!=null&&compareResult==1){//It's increment
							if(!replacedFlag){
							pushExpr.replaceIndex(new Math_ci("i"), indexPosition);
							replacedFlag=true;
						}
						loop.setEndLoopIndex(exprList.get(i).getLeftExpression().getFirstVariable().getIndexList().get(indexPosition).decode());
						
				}else{
						
					pushExpr=exprList.get(i);
					mergedExprList.add(pushExpr);
					
					//Create new loop group
					loop = new MathExpressionLoop();
					loop.addMathExpression(pushExpr);
					loop.setIndexFactor(new Math_ci("i"));
					List<MathFactor> indexList = pushExpr.getLeftExpression().getFirstVariable().getIndexList();
					if(indexList.size() > indexPosition){
						Math_cn index_cn = MathCollections.calculate(indexList.get(indexPosition));
						loop.setSatrtLoopIndex(index_cn.decode());
						loop.setEndLoopIndex(index_cn.decode());
					}
					root.addLoop(loop);
					
					replacedFlag=false;
				}
					
				prevExpr=exprList.get(i);
			}
			
			System.out.println("[");
			for(MathExpression expr:mergedExprList){
				try {
					System.out.println(expr.toLegalString());
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("]");
			
			//Reset flags and lists
			replacedFlag=false;
			mergedExprList.clear();
			exprList.clear();
		}
		List<MathExpressionLoop> newLoopList = new ArrayList<MathExpressionLoop>();
		for(MathExpressionLoop loop: root.getMathExpressionLoopList()){
			if(newLoopList.size()==0){
				newLoopList.add(loop);
			}else{
				Integer prevStartLoopIndex = newLoopList.get(newLoopList.size()-1).getSatrtLoopIndex();
				Integer nextStartLoopIndex = loop.getSatrtLoopIndex();

				Integer prevEndLoopIndex = newLoopList.get(newLoopList.size()-1).getEndLoopIndex();
				Integer nextEndLoopIndex = loop.getEndLoopIndex();
				
				String prevIncrement = null;
				String nextIncrement = null;
				try {
					prevIncrement = newLoopList.get(newLoopList.size()-1).getIndexFactor().toLegalString();
					nextIncrement = loop.getIndexFactor().toLegalString();
				} catch (MathException e) {
					e.printStackTrace();
				}
			
				boolean isPrevEnable = (prevStartLoopIndex != null) && (prevEndLoopIndex!=null) && (prevIncrement != null);
				boolean isNextEnable = (nextStartLoopIndex != null) && (nextEndLoopIndex!=null) && (nextIncrement != null);
				if(isPrevEnable && isNextEnable 
						&& (prevStartLoopIndex == nextStartLoopIndex) 
						&& (prevEndLoopIndex == nextEndLoopIndex)
						&& (prevIncrement.equals(nextIncrement))){
						newLoopList.get(newLoopList.size()-1).addMathExpressionList(loop.getMathExpressionList());
				}else{
					newLoopList.add(loop);
				}
			}
			root.setMathExpressionLoopList(newLoopList);
		}
		System.out.println(root.toString());
		
	}
	
	//@Test
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
	
	//@Test
	public void testSeparateLoop(){
		resutlTestSeparateLoop =	graphManipulator.separateLoopGroup(resultTestCreateFieldGraph);
		assertNotNull(resutlTestSeparateLoop);
		
		System.out.println("<<testSeparateLoop>>------------------------------");
		System.out.println(resutlTestSeparateLoop.toString());

	}
	
	
	//@Test
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
	private static SimpleRecMLAnalyzer simpleRecMLParse(String xml){

		SimpleRecMLAnalyzer pRecMLAnalyzer = new SimpleRecMLAnalyzer();
		
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
		
		/*Precalculate index ex.x[2+1] -> x[3] ** Added by y-yamashita (2013/01/07)*/
		pRecMLAnalyzer.calculateVariableIndex();
	
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
