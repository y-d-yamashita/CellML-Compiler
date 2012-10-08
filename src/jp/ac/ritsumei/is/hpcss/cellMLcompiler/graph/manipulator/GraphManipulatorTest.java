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
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.Graph;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.field.FieldVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.RecMLEquationAndVariableContener;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.Pair;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.PairList;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.List2D;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.Triple;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class GraphManipulatorTest {

GraphManipulator gm= new GraphManipulator();
RecMLAnalyzer ra = new RecMLAnalyzer();
/****

	@Test
	public void testCretateDependencyGraph() {
		BipartiteGraph<RecMLVertex, RecMLEdge> graph = null;
		PairList<RecMLVertex,RecMLVertex> pairList=null;
		RecMLAnalyzer recmlAnalyzer = parse();
		try {
			RecMLEquationAndVariableContener contener = new RecMLEquationAndVariableContener(recmlAnalyzer,recmlAnalyzer.getRecMLVariableTable());
			graph = gm.createBipartiteGraph(contener);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(graph);
		try {
			pairList = gm.maximumMatching(graph);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Graph<RecMLVertex, RecMLEdge> dg = null;
		try {
		dg=gm.cretateDependencyGraph(pairList, graph);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Dependency Graph------------");
		System.out.println(dg);
	}

	@Test
	public void testCreateBipartieGraph() {
		fail("Not yet implemented");
	}

	@Test
	public void testMaximumMatching() {
		BipartiteGraph<RecMLVertex, RecMLEdge> graph = null;
		PairList<RecMLVertex,RecMLVertex> pairList=null;
		RecMLAnalyzer recmlAnalyzer = parse();
		try {
			RecMLEquationAndVariableContener contener = new RecMLEquationAndVariableContener(recmlAnalyzer,recmlAnalyzer.getRecMLVariableTable());
			graph = gm.createBipartiteGraph(contener);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(graph);
		try {
			pairList = gm.maximumMatching(graph);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Matching------------------");
		for(Pair<RecMLVertex, RecMLVertex> p:pairList){
			System.out.println(p.getFirst()+","+p.getSecond());
		}
}
****/
	@Test
	public void testTarjan() {
		BipartiteGraph<RecMLVertex, RecMLEdge> graph = null;
		PairList<RecMLVertex,RecMLVertex> pairList=null;
		RecMLAnalyzer recmlAnalyzer = parse();
		try {
			RecMLEquationAndVariableContener contener = new RecMLEquationAndVariableContener(recmlAnalyzer,recmlAnalyzer.getRecMLVariableTable());
			graph = gm.createBipartiteGraph(contener);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(graph);
		try {
			pairList = gm.maximumMatching(graph);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DirectedGraph<RecMLVertex, RecMLEdge> dg = null;
		try {
		dg=gm.cretateDependencyGraph(pairList, graph);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List2D<RecMLVertex> t = gm.tarjan(dg);
		System.out.println("Tarjan-----------------------");
		System.out.println(t.toString());

RecMLVariableTable table = recmlAnalyzer.getRecMLVariableTable();		
for(RecMLVariableReference v:table.getVariableReferences()){
	System.out.print(v.getMathCI().getM_strPresentText()+":");
	for(MathFactor f:v.getMathCI().getM_vecIndexListFactor()){
		try {
			System.out.print(f.toLegalString()+",");
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	System.out.println();
}


System.out.println(gm.toRecMLXMLString(dg, t));

/*
List<Triple<String,String,String>> indexesList = new ArrayList<Triple<String,String,String>>();
	List<RecMLVertex> vl = new ArrayList<RecMLVertex>(dg.getVertexes());
	for(RecMLVertex v:vl){
		try {
			Triple<String,String,String> indexes = new Triple<String,String,String>();
			Vector<MathFactor> indexesListInVarRef = table.getVariableReference(vl.indexOf(v)).getMathCI().getM_vecIndexListFactor();	
			if(indexesListInVarRef.size()>0){	
				indexes.setFirst(indexesListInVarRef.get(0).toLegalString());
			}
			if(indexesListInVarRef.size()>1){	
				indexes.setSecond(indexesListInVarRef.get(1).toLegalString());
			}

			if(indexesListInVarRef.size()>2){	
				indexes.setThird(indexesListInVarRef.get(2).toLegalString());
			}

			indexesList.add(indexes);

		} catch (TableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MathException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		
		try {
			System.out.print("V:"+v.toString()+"  ");
			for(MathFactor f:table.getVariableReference(vl.indexOf(v)).getMathCI().getM_vecIndexListFactor()){
				try {
					System.out.print(f.toLegalString()+" ");
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		} catch (TableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		sb.append(v.toXMLString(vl.indexOf(v), indent));
System.out.println();
	}
	
	for(Triple<String,String,String> indexes:indexesList)
		System.out.println(indexes);
		*/
DirectedGraph<FieldVertex, FieldEdge> fg=null;
	try {
		 fg=gm.cretateFieldDependencyGraph(dg, recmlAnalyzer.getRecMLVariableTable());
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
	
	System.out.println("<<Field Graph>>------------------------------");
	System.out.println(gm.toRecMLXMLString(fg));
}

private static RecMLAnalyzer parse(){
	RecMLAnalyzer pRecMLAnalyzer = new RecMLAnalyzer();
	
	String xml = "";
	xml = "./model/recml/RecMLSample/FHN_FTCS_simple_2x3x3.recml";

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
	
	

//	/*selector内cnのInteger*/
	pRecMLAnalyzer.changeAllSelectorInteger();
	
	/*selector削除*/
	pRecMLAnalyzer.removeAllSelector();
	
	
	pRecMLAnalyzer.createVariableTable();
	pRecMLAnalyzer.setAssignRefRecVariableType();
	/** 内容確認 ***/

	try {
		pRecMLAnalyzer.printContents();
	} catch (MathException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	
	return pRecMLAnalyzer;
}
	BipartiteGraph<RecMLVertex, FieldEdge>createBipartieGraph() throws GraphException{
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
