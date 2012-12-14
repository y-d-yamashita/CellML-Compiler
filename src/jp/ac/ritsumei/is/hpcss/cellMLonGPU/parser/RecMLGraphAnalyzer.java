package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SpringLayout;


import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.Graph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.RecMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.RecMLGraphDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.RecMLGraphDefinition.eRecMLGraphTag;

/**
 * MathML解析クラス.
 */
public class RecMLGraphAnalyzer extends XMLAnalyzer {

	DirectedGraph<RecMLVertex, RecMLEdge> graph;
	Map<Integer,RecMLVertex> vertexMap;
	Map<Integer, RecMLEdge> edgeMap;
	RecMLVertex	curVertex;
	RecMLEdge	curEdge;
	
	Integer varID;
	Integer equID;
	Integer srcID;
	Integer dstID;

	boolean varFlag;
	boolean equFlag;
	boolean srcFlag;
	boolean dstFlag;
	
	private boolean parseMode;
/**
	 * MathML解析インスタンスを作成する.
	 */
	public RecMLGraphAnalyzer() {
		setParseMode(false);
		}

		/* ========================================================
	 * findTagStart
	 * 開始タグ解析メソッド
	 * 
	 * @arg
	 * string			strTag		: 開始タグ名
	 * XMLAttribute*	pXMLAttr	: 属性クラスインスタンス
	 * 
	 * @throws
	 * 		MathException
	 * (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAnalyzer#findTagStart(java.lang.String, jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAttribute)
	 * ======================================================== */
	public void findTagStart(String strTag, XMLAttribute pXMLAttr)
	throws MathException, XMLException, RelMLException, CellMLException, TecMLException, RecMLException {
		eRecMLGraphTag tagKind;
		/*タグの種類を特定*/
		try {
			tagKind = RecMLGraphDefinition.getRecMLGraphTagId(strTag);
		}
		catch (RecMLException e) {
			System.err.println(e.getMessage());
			throw new RecMLException("Analyzer","findTagStart",
					"can't specify MathML tag [" + strTag + "]");
		}

		/*種類ごとの処理*/
		switch(tagKind){

			//-----------------------------------演算子の解析
		case CTAG_RECML:
			break;
		case CTAG_GRAPH:
		//	graph = new DirectedSparseGraph<MyNode, MyEdge>();
			graph = new DirectedGraph<RecMLVertex, RecMLEdge>();
				break;
		case CTAG_NODES:
			vertexMap=new HashMap<Integer,RecMLVertex>();
			break;
		case CTAG_NODE:
			curVertex=new RecMLVertex();
			vertexMap.put(new Integer(pXMLAttr.getValue("id")),curVertex);
			break;
		case CTAG_VARIABLE:
			varFlag=true;
			break;
		case CTAG_EQUATION:
			equFlag=true;
			break;
		case CTAG_EDGES:
			edgeMap=new HashMap<Integer,RecMLEdge>();
			break;
		case CTAG_EDGE:
			curEdge=new RecMLEdge();
			edgeMap.put(new Integer(pXMLAttr.getValue("id")),curEdge);
			break;
		case CTAG_SOURCE:
			srcFlag=true;
			break;
		case CTAG_DEST:
			dstFlag=true;
			break;
			
		//-----------------------------------未定義の種別
		default:
			/*例外処理*/
			throw new MathException("MathMLAnalyzer","findTagStart",
					"unknown MathML tag found [" + strTag + "]");
		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAnalyzer#findTagEnd(java.lang.String)
	 */
	public void findTagEnd(String strTag)
	throws MathException, RelMLException, CellMLException, RecMLException {
		eRecMLGraphTag tagKind;

		/*タグの種類を特定*/
		try{
			tagKind = RecMLGraphDefinition.getRecMLGraphTagId(strTag);
		}catch (RecMLException e) {
			System.err.println(e.getMessage());
		throw new RecMLException("Analyzer","findTagStart",
				"can't specify MathML tag [" + strTag + "]");
		}

		/*種類ごとの処理*/
		switch(tagKind){

		case CTAG_RECML:
			break;
		case CTAG_GRAPH:
			break;
		case CTAG_NODES:
			break;
		case CTAG_NODE:
			try {
				graph.addVertex(curVertex);
			} catch (GraphException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			curVertex=null;
			break;
		case CTAG_VARIABLE:
			varFlag=false;
			break;
		case CTAG_EQUATION:
			equFlag=false;
			break;
		case CTAG_EDGES:
			break;
		case CTAG_EDGE:
			try {
				graph.addEdge(curEdge,
						vertexMap.get(srcID),
						vertexMap.get(dstID));
			} catch (GraphException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			curEdge=null;
			break;
		case CTAG_SOURCE:
			srcFlag=false;
			break;
		case CTAG_DEST:
			srcFlag=false;
			break;
				default:
			/*例外処理*/
			throw new RecMLException("MathMLAnalyzer","findTagEnd",
					"unknown MathML tag found [" + strTag + "]");
		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAnalyzer#findText(java.lang.String)
	 */
	public void findText(String strText)
	throws MathException, CellMLException, TableException {
	if(varFlag==true){
		varID=Integer.valueOf(strText);
		curVertex.setVariable(new Integer(varID));
	}else if(equFlag==true){
		equID=Integer.valueOf(strText);
		curVertex.setExpression(new Integer(equID));
	}else if(srcFlag==true){
		srcID=Integer.valueOf(strText);
	}else if(dstFlag==true){
		dstID=Integer.valueOf(strText);
	}
	}
	
	public DirectedGraph<RecMLVertex,RecMLEdge> getGraph(){
		return graph;
	}

	public boolean isParseMode() {
		return parseMode;
	}

	public void setParseMode(boolean parseMode) {
		if(parseMode){
			graph = new DirectedGraph<RecMLVertex, RecMLEdge>();
		}
		this.parseMode = parseMode;
	}
}
