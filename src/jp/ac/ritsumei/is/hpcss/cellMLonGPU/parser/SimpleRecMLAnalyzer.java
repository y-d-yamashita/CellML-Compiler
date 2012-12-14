package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

//import static org.junit.Assert.assertNotNull;

import java.beans.Expression;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.EqnDepTree.EqnDepTree;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.BipartiteGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.DirectedGraph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.Graph;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.manipulator.GraphManipulator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLEdge;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure.RelationPattern;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.CreateSimpleRecMLVariableTableVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.ReplacePartOfVariableNameVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.SetLeftSideRightSideVariableVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.SimpleRecML_SetLeftSideRightSideVariableVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.SimpleRecMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.SimpleRecMLDefinition.eRecMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.SimpleRecMLDefinition.eRecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.SimpleRecMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.SimpleRecMLVariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.List2D;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.PairList;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure.Labelattr;


/**
 * CellML解析クラス
 */
public class SimpleRecMLAnalyzer extends MathMLAnalyzer {

	/*数式解析中判定*/
	private boolean m_bMathParsing;
	private boolean m_bAttrParsing;
	
	private SimpleRecMLVariableTable simpleRecMLVariableTable;
	private RecMLGraphAnalyzer graphAnlyzer;
	private DirectedGraph<RecMLVertex,RecMLEdge> graph;
	
	// loop index variable name list (initialized in constructor)
	// just for test
	// should be implemented as hashmap or new class
//	private String[] indexStringList;
	
	// equation dependent tree root node
	EqnDepTree root, now;
	int type; // 0=pre, 1=init, 2=inner, 3=loopcond, 4=final, 5=post
	
	//HashMap
	public HashMap<Integer, String> indexHashMapList;
	public HashMap<Integer, String> getM_HashMapIndexList() {
		return  indexHashMapList;
	}
	
	public HashMap<Math_ci, Integer> m_HashMapRecurList;
	public HashMap<Math_ci, Integer> getM_HashMapRecurVar() {
		return m_HashMapRecurList;
	}
	public HashMap<Math_ci, Integer> m_HashMapArithList;
	public HashMap<Math_ci, Integer> getM_HashMapArithVar() {
		return m_HashMapArithList;
	}
	public HashMap<Math_ci, Integer> m_HashMapConstList;
	public HashMap<Math_ci, Integer> getM_HashMapConstVar() {
		return m_HashMapConstList;
	}
	public HashMap<Math_ci, Integer> m_HashMapOutputList;
	public HashMap<Math_ci, Integer> getM_HashMapOutputVar() {
		return m_HashMapOutputList;
	}	
	public HashMap<Math_ci, Integer> m_HashMapStepVarList;
	public HashMap<Math_ci, Integer> getM_HashMapStepVar() {
		return m_HashMapStepVarList;
	}	
	
	public ArrayList<Math_ci> m_ArrayListRecurList;
	public ArrayList<Math_ci>  getM_ArrayListRecurVar() {
		return m_ArrayListRecurList;
	}
	public ArrayList<Math_ci> m_ArrayListArithList;
	public ArrayList<Math_ci>  getM_ArrayListArithVar() {
		return m_ArrayListArithList;
	}
	public ArrayList<Math_ci> m_ArrayListConstList;
	public ArrayList<Math_ci>  getM_ArrayListConstVar() {
		return m_ArrayListConstList;
	}
	public ArrayList<Math_ci> m_ArrayListOutputList;
	public ArrayList<Math_ci>  getM_ArrayListOutputVar() {
		return m_ArrayListOutputList;
	}
	public ArrayList<Math_ci> m_ArrayListStepVarList;
	public ArrayList<Math_ci>  getM_ArrayListStepVar() {
		return m_ArrayListStepVarList;
	}
	
	/* equation vector */
	Vector<MathExpression> m_vecExpression;
	public Vector<MathExpression> getM_vecExpression() {
		return m_vecExpression;
	}

	/*式中の変数*/
	Vector<Math_ci> m_vecRecurVar;
	public Vector<Math_ci> getM_vecRecurVar() {
		return m_vecRecurVar;
	}
	Vector<Math_ci> m_vecArithVar;
	public Vector<Math_ci> getM_vecArithVar() {
		return m_vecArithVar;
	}
	Vector<Math_ci> m_vecConstVar;
	public Vector<Math_ci> getM_vecConstVar() {
		return m_vecConstVar;
	}
	Vector<Math_ci> m_vecCondition;
	public Vector<Math_ci> getM_vecCondition() {
		return m_vecCondition;
	}
	Vector<Math_ci> m_vecOutput;
	public Vector<Math_ci> getM_vecOutput() {
		return m_vecOutput;
	}
	
	
	HashMap<Integer, HashMap<String, Integer>> m_HashMapNodeList;
	public HashMap<Integer, HashMap<String, Integer>> getM_HashMapNodeList() {
		return m_HashMapNodeList;
	}
	public void setM_HashMapNodeList(HashMap<Integer, HashMap<String, Integer>> hm) {
		m_HashMapNodeList = hm;
	}
	HashMap<Integer, HashMap<String, Integer>> m_HashMapEdgeList;
	public HashMap<Integer, HashMap<String, Integer>> getM_HashMapEdgeList() {
		return m_HashMapEdgeList;
	}
	public void setM_HashMapEdgeList(HashMap<Integer, HashMap<String, Integer>> hm) {
		m_HashMapEdgeList = hm;
	}
	Vector<Integer> EquOrder; 
	public Vector<Integer> getEquOder() {
		return EquOrder;
	}
	public void setEquOder(Vector<Integer> vec) {
		EquOrder = vec;
	}
	
	ArrayList<RelationPattern> m_LoopStructure;
	public ArrayList<RelationPattern> getM_LoopStrucuture(){
		return m_LoopStructure;
	}
	public void setLoopStrucuture(ArrayList<RelationPattern> rp){
		m_LoopStructure = rp;
	}
	
	HashMap<Integer, String> refVariableList;
	public HashMap<Integer, String> getRefVariableList(){
		return refVariableList;
	}
	public void setRefVariableList(HashMap<Integer, String> hm){
		refVariableList = hm;
	}
	
	ArrayList<ArrayList<Integer>> simulEquList;
	public ArrayList<ArrayList<Integer>> getSimulEquList(){
		return simulEquList;
	}
	public void setSimulEquList(ArrayList<ArrayList<Integer>> al){
		simulEquList = al;
	}
	
	HashMap<Math_ci,HashMap<Integer,Integer>> varLoopcomponent;
	public HashMap<Math_ci,HashMap<Integer,Integer>> getVarLoopcomponent(){
		return varLoopcomponent;
	}
	public void setVarLoopcomponent(HashMap<Math_ci,HashMap<Integer,Integer>> vlc){
		varLoopcomponent = vlc;
	}
	
	/*SimpleRecML*/
	/*数式情報リスト*/	
	HashMap<Integer, Integer> m_hashfinalAttrLists;	
	public HashMap<Integer, Integer> getM_HashMapFinalAttrLists() {
		return m_hashfinalAttrLists;
	}
	HashMap<Integer, Integer> m_hashinitendAttrLists;
	public HashMap<Integer, Integer> getM_HashMapInitendAttrLists() {
		return m_hashinitendAttrLists;
	}
	HashMap<Integer, Integer> m_hashcondrefAttrLists;
	public HashMap<Integer, Integer> getM_HashMapCondrefAttrLists() {
		return m_hashcondrefAttrLists;
	}
	
//	static GraphManipulator graphManipulator;
//	static BipartiteGraph<RecMLVertex,RecMLEdge> resultTestCreateBipartiteGraph;
	
	/*-----コンストラクタ-----*/
	public SimpleRecMLAnalyzer() {
		m_bMathParsing = false;
		m_bAttrParsing = false;
		m_vecExpression = new Vector<MathExpression>();
		// just for test
		// should be implemented as hashmap?
//		String[] indexList = {"tn", "tm", "to"};
//		indexStringList = indexList;
		
				
		// equation dependent tree init
		root = null;
		now = null;
		
		//HashMap
		HashMap<Integer, String> indexListHM = new HashMap<Integer, String>();
		indexHashMapList = indexListHM;
		
		
		HashMap<Math_ci, Integer> RecurVarHM = new HashMap<Math_ci, Integer>();
		m_HashMapRecurList = RecurVarHM;
		HashMap<Math_ci, Integer> ArithVarHM = new HashMap<Math_ci, Integer>();
		m_HashMapArithList = ArithVarHM;
		HashMap<Math_ci, Integer> ConstVarHM = new HashMap<Math_ci, Integer>();
		m_HashMapConstList = ConstVarHM;
		HashMap<Math_ci, Integer> OutputVarHM = new HashMap<Math_ci, Integer>();
		m_HashMapOutputList = OutputVarHM;
		HashMap<Math_ci, Integer> StepVarHM = new HashMap<Math_ci, Integer>();
		m_HashMapStepVarList = StepVarHM;
		
		ArrayList<Math_ci> RecurVarAL = new ArrayList<Math_ci>();
		m_ArrayListRecurList = RecurVarAL;
		ArrayList<Math_ci> ArithVarAL = new ArrayList<Math_ci>();
		m_ArrayListArithList = ArithVarAL;
		ArrayList<Math_ci> ConstVarAL = new ArrayList<Math_ci>();
		m_ArrayListConstList = ConstVarAL;
		ArrayList<Math_ci> OutputVarAL = new ArrayList<Math_ci>();
		m_ArrayListOutputList = OutputVarAL;
		ArrayList<Math_ci> StepVarAL = new ArrayList<Math_ci>();
		m_ArrayListStepVarList = StepVarAL;
		
		
		m_vecRecurVar = new Vector<Math_ci>();
		m_vecArithVar = new Vector<Math_ci>();
		m_vecConstVar = new Vector<Math_ci>();
		m_vecCondition = new Vector<Math_ci>();
		m_vecOutput = new Vector<Math_ci>();

		graphAnlyzer=new RecMLGraphAnalyzer();
		graph=null;
		
		
//		graphManipulator= new GraphManipulator();
//		resultTestCreateBipartiteGraph=null;

		HashMap<Integer, HashMap<String, Integer>> NodeHashMap = new HashMap<Integer, HashMap<String, Integer>>();
		m_HashMapNodeList = NodeHashMap;
		HashMap<Integer, HashMap<String, Integer>> EdgeHashMap = new HashMap<Integer, HashMap<String, Integer>>();
		m_HashMapEdgeList = EdgeHashMap;
		Vector<Integer> v = new Vector<Integer>();
		EquOrder = v;
		
		simulEquList = new ArrayList<ArrayList<Integer>>();
		varLoopcomponent = new HashMap<Math_ci,HashMap<Integer,Integer>>();
		
		/*SimpleRecML*/	
		/*数式情報リスト*/	
		m_hashfinalAttrLists = new HashMap<Integer, Integer>();	
		m_hashinitendAttrLists = new HashMap<Integer, Integer>();	
		m_hashcondrefAttrLists = new HashMap<Integer, Integer>();
		
	}

	/*-----解析メソッド-----*/

	//========================================================
	//findTagStart
	// 開始タグ解析メソッド
	//
	//@arg
	// string			strTag		: 開始タグ名
	// XMLAttribute*	pXMLAttr	: 属性クラスインスタンス
	//
	//========================================================
	public void findTagStart(String strTag, XMLAttribute pXMLAttr)
	throws CellMLException, TecMLException, MathException, XMLException, RelMLException, RecMLException {

		//-----------------------------------------------------
		//数式部の解析
		//-----------------------------------------------------
		if (m_bMathParsing) {

			/*MathML解析器に投げる*/
			super.findTagStart(strTag,pXMLAttr);
			
			/*SimpleRecML*/	
			/*属性情報リストを取得*/	
			String strAttrNum;	
			String strAttrType = "";	
			String strAttrCondref = "";	
			int intAttrLoopnum;	
			strAttrNum =	
			pXMLAttr.getValue(SimpleRecMLDefinition.SIMPLERECML_ATTR_NUM);	
			strAttrType =	
			pXMLAttr.getValue(SimpleRecMLDefinition.SIMPLERECML_ATTR_TYPE);	
			String strLoopnum =	
			pXMLAttr.getValue(SimpleRecMLDefinition.SIMPLERECML_ATTR_LOOPNUM);	
			strAttrCondref =	
			pXMLAttr.getValue(SimpleRecMLDefinition.SIMPLERECML_ATTR_CONDREF);
			
			/*SimpleRecML*/	
			/*属性情報リストを取得*/	
			int intAttrNum;	
			int intAttrCondref;	
			if("final".equals(strAttrType)){	
				intAttrNum = Integer.parseInt(strAttrNum);	
				intAttrLoopnum = Integer.parseInt(strLoopnum);	
				m_hashfinalAttrLists.put(intAttrNum, intAttrLoopnum);
			}	
			if("initend".equals(strAttrType)){	
				intAttrNum = Integer.parseInt(strAttrNum);	
				intAttrLoopnum = Integer.parseInt(strLoopnum);	
				m_hashinitendAttrLists.put(intAttrNum, intAttrLoopnum);	
			}	
			if(strAttrCondref!=null){	
				intAttrNum = Integer.parseInt(strAttrNum);	
				intAttrCondref = Integer.parseInt(strAttrCondref);	
//				m_hashcondrefAttrLists.put(intAttrNum, intAttrCondref);	
				m_hashcondrefAttrLists.put(intAttrCondref,intAttrNum);	
			}
			
		}
		else if(graphAnlyzer.isParseMode()){
			graphAnlyzer.findTagStart(strTag, pXMLAttr);
		}

		//-----------------------------------------------------
		//RecMLの解析
		//-----------------------------------------------------
		else {
			/*タグidの取得*/
			eRecMLTag tagId;

			try {
				tagId = SimpleRecMLDefinition.getRecMLTagId(strTag);
			}
			catch (RecMLException e) {
				/*特定のタグ以外は無視する*/
				return;
			}


			
			/*タグ種別ごとの処理*/
			switch (tagId) {
			
			// loop structure tree
			case CTAG_LOOPSTRUCT:
				{
					// loop structure construction
					int loopNum = Integer.parseInt(pXMLAttr.getValue("num"));
					if (root == null) {
						root = new EqnDepTree(loopNum);
						now = root;
					} else {
						root.push(now);
						EqnDepTree child = new EqnDepTree(loopNum);
						switch (type) {
						case 0: now.n_pre = child; break;
						case 1: now.n_init = child; break;
						case 2: now.n_inner = child; break;
						case 3: now.n_loopcond = child; break;
						case 4: now.n_final = child; break;
						case 5: now.n_post = child; break;
						}
						now = child;
					}
					break;
				}
				// loop structure tree
//			case CTAG_POSITION:
//				{
//					// loop structure construction
//					String positionname = pXMLAttr.getValue("name");
//					if (positionname.equals(SimpleRecMLDefinition.SIMPLERECML_ATTR_PRE)) {
//						type = 0;
//					} else if (positionname.equals(SimpleRecMLDefinition.RECML_ATTR_INIT)) {
//						type = 1;
//					} else if (positionname.equals(SimpleRecMLDefinition.RECML_ATTR_INNER)) {
//						type = 2;
//					} else if (positionname.equals(SimpleRecMLDefinition.RECML_ATTR_LOOPCOND)) {
//						type = 3;
//					} else if (positionname.equals(SimpleRecMLDefinition.RECML_ATTR_FINAL)) {
//						type = 4;
//					} else if (positionname.equals(SimpleRecMLDefinition.RECML_ATTR_POST)) {
//						type = 5;
//					}
//					break;
//				}
				// loop index string
			case CTAG_LOOPINDEX:
				{
					// loop structure construction
					String name = pXMLAttr.getValue("name");
					int num = Integer.parseInt(pXMLAttr.getValue("num"));
//					setIndexString(num, name);
					
					//HashMap
					setIndexHashMap(num, name);
					break;
				}

			// mathml process
			case CTAG_MATH:
				{
					m_bMathParsing = true;
					m_NextOperandKind = null;
					break;
				}
			case CTAG_GRAPH:
			{
				graphAnlyzer.setParseMode(true);
				break;
			}
				//--------------------------------------変数宣言
			case CTAG_VARIABLE:
				{
					/*変数名とタイプ取得*/
					String strName = pXMLAttr.getValue("name");
					String strType = pXMLAttr.getValue("type");
					eRecMLVarType varType = SimpleRecMLDefinition.getRecMLVarType(strType);
	
					/*変数名から変数インスタンス生成*/
					Math_ci pVariable =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strName);
	
//					/*input変数の登録*/
//					if (tagId == eTecMLTag.TTAG_INPUTVAR) {
//						m_pInputVar = pVariable;
//					}
//					/*output変数の登録*/
//					else if (tagId == eTecMLTag.TTAG_OUTPUTVAR) {
//						m_pOutputVar = pVariable;
//					}
	
//					/*タイプごとに変数追加*/
//					switch (varType) {
//	
//						//----------------------------------RECURVAR変数型
//					case CVAR_TYPE_RECURVAR:
//						m_vecRecurVar.add(pVariable);
//						break;
//	
//						//----------------------------------ARITHVAR変数型
//					case CVAR_TYPE_ARITHVAR:
//						m_vecArithVar.add(pVariable);
//						break;
//	
//						//----------------------------------CONSTVAR変数型
//					case CVAR_TYPE_CONSTVAR:
//						m_vecConstVar.add(pVariable);
//						break;
//	
//						//----------------------------------CONDITION変数型
//					case CVAR_TYPE_STR_CONDITION:
//						m_vecCondition.add(pVariable);
//						break;
//	
//						//----------------------------------OUTPUT変数型
//					case CVAR_TYPE_OUTPUT:
//						m_vecOutput.add(pVariable);
//						break;
//
//					}
					
					String[] LoopComponent = new String[5];
					LoopComponent[0] = pXMLAttr.getValue("loopcomponent1");
					LoopComponent[1] = pXMLAttr.getValue("loopcomponent2");
					LoopComponent[2] = pXMLAttr.getValue("loopcomponent3");
					LoopComponent[3] = pXMLAttr.getValue("loopcomponent4");
					LoopComponent[4] = pXMLAttr.getValue("loopcomponent5");
					
					
					int squarebracketsnum = 0;
					for(int i = 0; i < LoopComponent.length; ++i){
						if(LoopComponent[i] != null){
							squarebracketsnum++;
						}
					}
						
					/*タイプごとに変数追加*/
					switch (varType) {
	
						//----------------------------------RECURVAR変数型
					case CVAR_TYPE_RECURVAR:
						m_ArrayListRecurList.add(pVariable);
						m_HashMapRecurList.put(pVariable, squarebracketsnum);
//						m_vecRecurVar.add(pVariable);
						break;
	
						//----------------------------------ARITHVAR変数型
					case CVAR_TYPE_ARITHVAR:
						m_ArrayListArithList.add(pVariable);
						m_HashMapArithList.put(pVariable, squarebracketsnum);
//						m_vecArithVar.add(pVariable);
						break;
	
						//----------------------------------CONSTVAR変数型
					case CVAR_TYPE_CONSTVAR:
						m_ArrayListConstList.add(pVariable);
						m_HashMapConstList.put(pVariable, squarebracketsnum);
//						m_vecConstVar.add(pVariable);
						break;
						
						//----------------------------------OUTPUT変数型
					case CVAR_TYPE_OUTPUT:
						m_ArrayListOutputList.add(pVariable);
						m_HashMapOutputList.put(pVariable, squarebracketsnum);
//						m_vecOutput.add(pVariable);
						break;
						//----------------------------------OUTPUT変数型
					case CVAR_TYPE_STEPVER:
						m_ArrayListStepVarList.add(pVariable);
						m_HashMapStepVarList.put(pVariable, squarebracketsnum);
//						m_vecOutput.add(pVariable);
						break;
					}
					break;
				}
				//--------------------------------------変数宣言
			}
		}
	}

	//========================================================
	//findTagEnd
	// 終了タグ解析メソッド
	//
	//@arg
	// string	strTag	: 終了タグ名
	//
	//========================================================
	public void findTagEnd(String strTag)
	throws CellMLException, MathException, RelMLException, RecMLException {

		//-----------------------------------------------------
		//数式部の解析
		//-----------------------------------------------------
		if (m_bMathParsing) {

			/*数式解析終了*/
			if (strTag == SimpleRecMLDefinition.SIMPLERECML_TAG_STR_MATH) {
				m_bMathParsing = false;
				return;
			}
			/*MathML解析器に投げる*/
			super.findTagEnd(strTag);
		}
		
		else if(graphAnlyzer.isParseMode()){
			if(strTag==SimpleRecMLDefinition.SIMPLERECML_TAG_STR_GRAPH){
				graphAnlyzer.setParseMode(false);
				graph = graphAnlyzer.getGraph();
				return;
			}
			graphAnlyzer.findTagEnd(strTag);
		}
		

		//-----------------------------------------------------
		//RecML analysis
		//-----------------------------------------------------
		else {

			/*タグidの取得*/
			eRecMLTag tagId = SimpleRecMLDefinition.getRecMLTagId(strTag);

			/*タグ種別ごとの処理*/
			switch (tagId) {
			case CTAG_LOOPSTRUCT:
				// end of loop tag
				if (!root.emptyStack()) {
					now = root.pop();
				} else {
					;
				}
				// for debug
				//root.printString();
				break;
					
			default:
			}
		}
	}

	//========================================================
	//findText
	// 文字列解析メソッド
	//
	//@arg
	// string	strText	: 切り出された文字列
	//
	//========================================================
	public void findText(String strText)
	throws CellMLException, MathException, TableException {
		//-----------------------------------------------------
		//数式部の解析
		//-----------------------------------------------------
		if (m_bMathParsing && m_NextOperandKind != null) {
			/*MathML解析器に投げる*/
			super.findText(strText);
		}

		else if(graphAnlyzer.isParseMode()){
			graphAnlyzer.findText(strText);
		}
		//-----------------------------------------------------
		//CellMLの解析
		//-----------------------------------------------------
		else {
		}
	}

	//	========================================================
	//getExpWithAttr
	// get indices for Expressions whose attribute matches strAttr
	//
	//@return
	// ArrayList containing indices for m_vecMathExpression
	//
	//========================================================
	// 
	public ArrayList getExpressionWithAttr(String[] strAttr) {
		ArrayList indexList = new ArrayList();
		for (int i=0; i<m_vecAttrList.size(); i++){
			if(Arrays.equals(strAttr, m_vecAttrList.get(i))){
				indexList.add(i);
			}
		}
		return indexList;
	}
	
	//========================================================
	//printContents
	// 解析内容標準出力メソッド
	//
	//========================================================
	/*-----解析結果表示メソッド-----*/
	public void printContents() throws MathException {
		/*開始線出力*/
		System.out.println("[RecML]------------------------------------");
		
		//private HashMap<Integer, String> indexHashMapList;
		System.out.println("<indexHashMapList>--------------------------");
		for(Integer key:indexHashMapList.keySet())
			System.out.print("("+key+","+indexHashMapList.get(key)+"),");
		System.out.println("\n -----------------------------------------");
		
		
		//private HashMap<Math_ci, Integer> m_HashMapRecurList;
		System.out.println("<m_HashMapRecurList>--------------------------");
		if(m_HashMapRecurList.isEmpty())
			System.out.print("None");
		for(Math_ci key:m_HashMapRecurList.keySet())
			System.out.print("("+key.toLegalString()+","+m_HashMapRecurList.get(key)+"),");
		System.out.println("\n -----------------------------------------");

		
		//private HashMap<Math_ci, Integer> m_HashMapArithList;
		System.out.println("<m_HashMapArithList>--------------------------");
		if(m_HashMapArithList.isEmpty())
			System.out.print("None");
		for(Math_ci key:m_HashMapArithList.keySet())
			System.out.print("("+key.toLegalString()+","+m_HashMapArithList.get(key)+"),");
		System.out.println("\n -----------------------------------------");

		
		//private HashMap<Math_ci, Integer> m_HashMapConstList;
		System.out.println("<m_HashMapConstList>--------------------------");
		if(m_HashMapConstList.isEmpty())
			System.out.print("None");
		for(Math_ci key:m_HashMapConstList.keySet())
			System.out.print("("+key.toLegalString()+","+m_HashMapConstList.get(key)+"),");
		System.out.println("\n -----------------------------------------");
		
		//private HashMap<Math_ci, Integer> m_HashMapOutputList;
		System.out.println("<m_HashMapOutputList>--------------------------");
		if(m_HashMapOutputList.isEmpty())
			System.out.print("None");
		for(Math_ci key:m_HashMapOutputList.keySet())
			System.out.print("("+key.toLegalString()+","+m_HashMapOutputList.get(key)+"),");
		System.out.println("\n -----------------------------------------");

		
		//private ArrayList<Math_ci> m_ArrayListRecurList;
		System.out.println("<m_ArrayListRecurList>--------------------------");
		if(m_ArrayListRecurList.isEmpty())
			System.out.print("None");
		for(Math_ci value:m_ArrayListRecurList)
			System.out.print("("+value.toLegalString()+"),");
		System.out.println("\n -----------------------------------------");
		
		//private ArrayList<Math_ci> m_ArrayListArithList;
		System.out.println("<m_ArrayListArithList>--------------------------");
		if(m_ArrayListArithList.isEmpty())
			System.out.print("None");
		for(Math_ci value:m_ArrayListArithList)
			System.out.print("("+value.toLegalString()+"),");
		System.out.println("\n -----------------------------------------");
		
		//private ArrayList<Math_ci> m_ArrayListConstList;
		System.out.println("<m_ArrayListConstList>--------------------------");
		if(m_ArrayListConstList.isEmpty())
			System.out.print("None");
		for(Math_ci value:m_ArrayListConstList)
			System.out.print("("+value.toLegalString()+"),");
		System.out.println("\n -----------------------------------------");
		
		//private ArrayList<Math_ci> m_ArrayListOutputList;
		System.out.println("<m_ArrayListOutputList>--------------------------");
		if(m_ArrayListOutputList.isEmpty())
			System.out.print("None");
		for(Math_ci value:m_ArrayListOutputList)
			System.out.print("("+value.toLegalString()+"),");
		System.out.println("\n -----------------------------------------");
		
		
		
		/*式中の変数*/
		//Vector<Math_ci> m_vecRecurVar;
		System.out.println("<m_vecRecurVar>--------------------------");
		if(m_vecRecurVar.isEmpty())
			System.out.print("None");
		for(Math_ci value:m_vecRecurVar)
			System.out.print("("+value.toLegalString()+"),");
		System.out.println("\n -----------------------------------------");
		
		//Vector<Math_ci> m_vecArithVar;
		System.out.println("<m_vecArithVar>--------------------------");
		if(m_vecArithVar.isEmpty())
			System.out.print("None");
		for(Math_ci value:m_vecArithVar)
			System.out.print("("+value.toLegalString()+"),");
		System.out.println("\n -----------------------------------------");

		
		//Vector<Math_ci> m_vecConstVar;
		System.out.println("<m_vecConstVar>--------------------------");
		if(m_vecConstVar.isEmpty())
			System.out.print("None");
		for(Math_ci value:m_vecConstVar)
			System.out.print("("+value.toLegalString()+"),");
		System.out.println("\n -----------------------------------------");
		
		//Vector<Math_ci> m_vecCondition;
		System.out.println("<m_vecCondition>--------------------------");
		if(m_vecCondition.isEmpty())
			System.out.print("None");
		for(Math_ci value:m_vecCondition)
			System.out.print("("+value.toLegalString()+"),");
		System.out.println("\n -----------------------------------------");
		
		//Vector<Math_ci> m_vecOutput;
		System.out.println("<m_vecOutput>--------------------------");
		if(m_vecOutput.isEmpty())
			System.out.print("None");
		for(Math_ci value:m_vecOutput)
			System.out.print("("+value.toLegalString()+"),");
		System.out.println("\n -----------------------------------------");
		
		
		System.out.println(simpleRecMLVariableTable.toString());
		
		//System.out.println(new SimpleRecMLEquationAndVariableContainer(this,simpleRecMLVariableTable).toString());
		//root.printString(" ");
		//for(String[] strArray:getAttribute())			
		//System.out.println(strArray[0]+strArray[1]+strArray[2]+strArray[3]+strArray[4]);
		
		System.out.println();
		/*数式出力*/
		System.out.println("<Print expressions>-------------------------");
		if(root!=null){
			System.out.println("(Using PrintExpression of RecMLAnalyzerClass)");
			root.printString("   ");
			printExpressions();
		}else{
			System.out.println("(Using PrintExpression of Super Class)");
			super.printExpressions();
		}
		
		/*改行*/
		System.out.println();
		System.out.println("----------------------------------------------");
	}
	
	//========================================================
	//getExpression
	// get the mathExpression from m_vecExpression
	//
	//========================================================
	/*-----解析結果表示メソッド-----*/
	public MathExpression getExpression(int index){
		/*数式出力*/
		return super.getExpression(index);
	}
	public MathExpression getRecMLExpression(int index){
		//MathExpression vector is empty when using RecMLAnalyzer
		return this.m_vecExpression.get(index);
	}
	
	
	public static void main(String[] args) throws MathException {
		SimpleRecMLAnalyzer simpleRecMLAnalyzer = new SimpleRecMLAnalyzer();
		
		String xml=
				//"./model/recml/RecMLSample/FHN_FTCS_simple_2x3x3.recml"
				//"./model/recml/RecMLSample/ArbitraryModel_1D_simple.recml"
				//"./model/recml/RecMLSample/ArbitraryModel_1D_simple_v2_yamashita.recml"
//				"./model/recml/SimpleKawabataTestSample/SimpleRecMLSample001.recml"
//				"./model/recml/SimpleKawabataTestSample/SimpleRecMLSample002.recml"
				"./model/recml/SimpleRecMLSample/SimpleKawabataTestSample/SimpleRecMLSample002.recml"
		;
		
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

		XMLHandler handler = new XMLHandler(simpleRecMLAnalyzer);
		parser.setContentHandler(handler);
		try {
//			parser.parse(args[0]);
			parser.parse(xml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*output for StructuredRecML*/
		simpleRecMLAnalyzer.testOutPutStrRec(simpleRecMLAnalyzer);
		
		
	}

	public void analysisForOutPutStrRec(SimpleRecMLAnalyzer sa) throws MathException{
		/*selector内cnのInteger*/
		sa.changeAllSelectorInteger();
		
		/*selector削除*/
		sa.removeAllSelector();
		
		/*conditionの数式を一時的に除く*/
		ArrayList<MathExpression> me
		= sa.kawabataRemoveCondition();
		
//		sa.printMathmlExpressions();
				
		/*Create variable table from variable information in RecmlAnalyzer*/
		sa.createVariableTable();
		
		/*Attach information about assignment and reference equations*/
		//sa.setLeftsideRightsideVariable();

		/*Set variable type (ex. recvar, constvara)*/
		sa.setRefVariableType();
		
		GraphManipulator graphManipulator= new GraphManipulator();
		BipartiteGraph<RecMLVertex,RecMLEdge> resultTestCreateBipartiteGraph=null;
		PairList<RecMLVertex,RecMLVertex> resultTestMaximumMatching=null;
		DirectedGraph<RecMLVertex,RecMLEdge> resultTestCreateDependencyGraph=null;
		List2D<RecMLVertex> resultTestTrajan=null;
		
		
		/*Comment out
		SimpleRecMLEquationAndVariableContainer container2 = 
				new SimpleRecMLEquationAndVariableContainer(sa,sa.getRecMLVariableTable());
		
		*/
		/* Create a bipartite graph */
		/*
		try {		
//			resultTestCreateBipartiteGraph = graphManipulator.createBipartiteGraph(container);
			resultTestCreateBipartiteGraph = graphManipulator.createBipartiteGraph_Simple(container2);
		} catch (GraphException e) {
			e.printStackTrace();
		}
		*/
		/*Maximum matching*/
		try {
			resultTestMaximumMatching = 
					graphManipulator.maximumMatching(resultTestCreateBipartiteGraph);
		} catch (GraphException e) {
			e.printStackTrace();
		}
		
		/* Create a dependency graph from a maximum matching 
		 * result and a bipartite graph */
		try {
			resultTestCreateDependencyGraph =
					graphManipulator.cretateDependencyGraph(resultTestMaximumMatching
												           ,resultTestCreateBipartiteGraph);
		} catch (GraphException e) {
			e.printStackTrace();
		}
		
		resultTestTrajan = graphManipulator.tarjan(resultTestCreateDependencyGraph);
		
		sa.setM_HashMapNodeList(graphManipulator.getRecMLNode(resultTestCreateDependencyGraph));
		sa.setM_HashMapEdgeList(graphManipulator.getRecMLEdge(resultTestCreateDependencyGraph));
		sa.setEquOder(graphManipulator.getEquOder(resultTestCreateDependencyGraph, resultTestTrajan));	
		sa.setRefVariableList(sa.simpleRecMLVariableTable.getVariableList());
		
		sa.setSimulEquList(graphManipulator.getSimulEquList(resultTestCreateDependencyGraph, resultTestTrajan));
		
		/*condition数式を加える*/
		sa.m_vecMathExpression.addAll(me);
		
		sa.structureAnalysis();
		
//		sa.m_vecMathExpression.addAll(sa.replaceEquOrder(sa));
		
		/*loopcomponent解析*/
		HashMap<Math_ci,HashMap<Integer,Integer>> loopCompo = new HashMap<Math_ci,HashMap<Integer,Integer>>(); 
		for(Math_ci ci:sa.m_HashMapRecurList.keySet()){
			HashMap<Integer,Integer> hm = sa.serchIndexVaruable(sa.indexHashMapList,ci);
			loopCompo.put(ci, hm);
		}
		for(Math_ci ci:sa.m_HashMapArithList.keySet()){
			HashMap<Integer,Integer> hm = sa.serchIndexVaruable(sa.indexHashMapList,ci);
			loopCompo.put(ci, hm);
		}
		for(Math_ci ci:sa.m_HashMapOutputList.keySet()){
			HashMap<Integer,Integer> hm = sa.serchIndexVaruable(sa.indexHashMapList,ci);
			loopCompo.put(ci, hm);
		}
		sa.setVarLoopcomponent(loopCompo);

		
		System.out.println("SimpleRecMLAnalyzer Finish.");

	}
	
	public void testOutPutStrRec(SimpleRecMLAnalyzer sa) throws MathException{
		/*selector内cnのInteger*/
		sa.changeAllSelectorInteger();
		
		/*selector削除*/
		sa.removeAllSelector();
		
		/*conditionの数式を一時的に除く*/
		ArrayList<MathExpression> me
		= sa.kawabataRemoveCondition();
		
		
		/*Create variable table from variable information in RecmlAnalyzer*/
		sa.createVariableTable();
		
		/*Attach information about assignment and reference equations*/
		//sa.setLeftsideRightsideVariable();

		/*Set variable type (ex. recvar, constvara)*/
		sa.setRefVariableType();
		
		GraphManipulator graphManipulator= new GraphManipulator();
		BipartiteGraph<RecMLVertex,RecMLEdge> resultTestCreateBipartiteGraph=null;
		PairList<RecMLVertex,RecMLVertex> resultTestMaximumMatching=null;
		DirectedGraph<RecMLVertex,RecMLEdge> resultTestCreateDependencyGraph=null;
		List2D<RecMLVertex> resultTestTrajan=null;
		
		
		/*
		SimpleRecMLEquationAndVariableContainer container2 = 
				new SimpleRecMLEquationAndVariableContainer(sa,sa.getRecMLVariableTable());
		*/
		
		/* Create a bipartite graph */
		/*
		try {		
//			resultTestCreateBipartiteGraph = graphManipulator.createBipartiteGraph(container);
			resultTestCreateBipartiteGraph = graphManipulator.createBipartiteGraph_Simple(container2);
		} catch (GraphException e) {
			e.printStackTrace();
		}
		*/
		/*Maximum matching*/
		try {
			resultTestMaximumMatching = 
					graphManipulator.maximumMatching(resultTestCreateBipartiteGraph);
		} catch (GraphException e) {
			e.printStackTrace();
		}
		
		/* Create a dependency graph from a maximum matching 
		 * result and a bipartite graph */
		try {
			resultTestCreateDependencyGraph =
					graphManipulator.cretateDependencyGraph(resultTestMaximumMatching
												           ,resultTestCreateBipartiteGraph);
		} catch (GraphException e) {
			e.printStackTrace();
		}
		
		resultTestTrajan = graphManipulator.tarjan(resultTestCreateDependencyGraph);
		
		sa.setM_HashMapNodeList(graphManipulator.getRecMLNode(resultTestCreateDependencyGraph));
		sa.setM_HashMapEdgeList(graphManipulator.getRecMLEdge(resultTestCreateDependencyGraph));
		sa.setEquOder(graphManipulator.getEquOder(resultTestCreateDependencyGraph, resultTestTrajan));	
		sa.setRefVariableList(sa.simpleRecMLVariableTable.getVariableList());
		
		sa.setSimulEquList(graphManipulator.getSimulEquList(resultTestCreateDependencyGraph, resultTestTrajan));
		
		/*condition数式を加える*/
		sa.m_vecMathExpression.addAll(me);
		
		sa.structureAnalysis();
		
		sa.m_vecMathExpression.addAll(sa.replaceEquOrder(sa));
		
		System.out.println("Finish.");

	}

	// <loopindex num="0" name="tn"/> tag registration
	// should be implemented as hashmap or new class
//	protected void setIndexString(int LoopNumber, String varName) {
//		indexStringList[LoopNumber] = varName;
//	}
	//HashMap
	protected void setIndexHashMap(int LoopNumber, String varName) {
		indexHashMapList.put(LoopNumber, varName);
	}
	
	// get loop index variable name
	// should be implemented as hashmap or new class
//	public String getIndexString(int LoopNumber) {
//		return indexStringList[LoopNumber];
//	}
	//HashMap
	public String getIndexHashMap(int LoopNumber) {
		return indexHashMapList.get(LoopNumber);
	}
	
	// return true if LoopNumber has inner element
	public boolean hasInner(int LoopNumber) {
		boolean flag = false;
		for (int i=0; i<m_vecAttrList.size(); i++){
			String[] strAttr = m_vecAttrList.get(i);
			if (strAttr[LoopNumber]!=null) {
				if (strAttr[LoopNumber].equals("inner")) {
					flag = true;
				}
			}
		}
		return flag;
	}

	// return true if strAttr has child
	// just for debug
	// should be implemented to refer recml tree structure
	public boolean hasChild(String[] strAttr) {
		if (nextChildLoopNumber(strAttr) != -1) {
			return true;
		} else {
			return false;
		}
	}
	
	// LoopNumber for the child of strAttr
	public int nextChildLoopNumber(String[] strAttr) {
		return nextChildLoopNumber(strAttr, root);
	}
	
	private int nextChildLoopNumber(String[] strAttr, EqnDepTree node) {
		int childLoopNum = -1;
		int loopNum = node.loopNumber;
		String curAttr = strAttr[loopNum];
		if ((curAttr == null) && (node != null)) {
			return loopNum;
		}

		if (curAttr.equals("pre") && (node.n_pre != null)) {
			childLoopNum = nextChildLoopNumber(strAttr, node.n_pre);
		} else if (curAttr.equals("init") && (node.n_init != null)) {
			childLoopNum = nextChildLoopNumber(strAttr, node.n_init);
		} else if (curAttr.equals("inner") && (node.n_inner != null)) {
			childLoopNum = nextChildLoopNumber(strAttr, node.n_inner);
		} else if (curAttr.equals("loopcond") && (node.n_loopcond != null)) {
			childLoopNum = nextChildLoopNumber(strAttr, node.n_loopcond);
		} else if (curAttr.equals("final") && (node.n_final != null)) {
			childLoopNum = nextChildLoopNumber(strAttr, node.n_final);
		} else if (curAttr.equals("post") && (node.n_post != null)) {
			childLoopNum = nextChildLoopNumber(strAttr, node.n_post);
		} 
		return childLoopNum;
	}

	public void createVariableTable() {
		CreateSimpleRecMLVariableTableVisitor visitor = new CreateSimpleRecMLVariableTableVisitor();
		
		 for(MathExpression expr :m_vecMathExpression)
			 expr.getRootFactor().traverse(visitor);
		 simpleRecMLVariableTable=visitor.getTable();
	}
	/*
	public void setLeftsideRightsideVariable(){
		SimpleRecML_SetLeftSideRightSideVariableVisitor visitor = new SimpleRecML_SetLeftSideRightSideVariableVisitor(simpleRecMLVariableTable);
		 for(MathExpression expr :m_vecMathExpression){
			 visitor.reset(expr.getExpressionNumfromApply());
			 expr.getRootFactor().traverse(visitor);
//			 visitor.reset();
		 }
		 simpleRecMLVariableTable=visitor.getTable();
	}
	*/
	public void setRefVariableType(){
		simpleRecMLVariableTable.setRefVariableType(this);
	}
	
	
	public SimpleRecMLVariableTable getRecMLVariableTable() {
		return simpleRecMLVariableTable;
	}

	/**
	 * 数式を標準出力する.
	 * @throws MathException
	 */
	public void printExpressions() throws MathException {
		/*すべての数式を出力*/
		for (MathExpression it: m_vecMathExpression) {

			System.out.print("Expression["+m_vecMathExpression.indexOf(it)+"]: ");
			/*数式標準出力*/
	//		System.out.println(it.toLegalString());
			String[] strArray=getAttribute(m_vecMathExpression.indexOf(it));
				for(int i=0;strArray[i]!=null;i++)
					System.out.print("Loop["+i+"]<"+strArray[i]+">:");
			
				System.out.println(it.toLegalString());
			
			//変数一覧表示（デバッグ用）
			//int nVariableCount = it.getVariableCount();
		//	System.out.println("variablecount:"+nVariableCount);
		//	for (int j = 0; j < nVariableCount; j++) {
		//		System.out.println(it.getVariable(j).toLegalString());
		//	}

		}
	}
	
	
	public void replaceAllVariable(String regex,String replacement){
		ReplacePartOfVariableNameVisitor visitor = new ReplacePartOfVariableNameVisitor(regex, replacement);
		
		for(Math_ci var: m_ArrayListRecurList){
			var.replaceStrPresentExt(regex,replacement);
		}
		for(Math_ci var: m_ArrayListArithList){
			var.replaceStrPresentExt(regex,replacement);
			
		}
		for(Math_ci var: m_ArrayListConstList){
			var.replaceStrPresentExt(regex,replacement);
			
		}
		for(Math_ci var: m_ArrayListOutputList){
			var.replaceStrPresentExt(regex,replacement);
		}
		for(Math_ci var: m_ArrayListStepVarList){
			var.replaceStrPresentExt(regex,replacement);
		}
		for(MathExpression expr:this.m_vecMathExpression){
			expr.traverse(visitor);
		}
		
	}
	
	public DirectedGraph<RecMLVertex,RecMLEdge>getGraph(){
		return graph;
	}
	
	public void structureAnalysis() throws MathException {
				
		HashMap<Integer, Integer> finalAttrLists = getM_HashMapFinalAttrLists();
		HashMap<Integer, Integer> initendAttrLists = getM_HashMapInitendAttrLists();
		HashMap<Integer, Integer> condrefAttrLists = getM_HashMapCondrefAttrLists();
		
		
		Labelattr lb = new Labelattr(
//				LabelattrV2 lb = new LabelattrV2(
		getM_HashMapNodeList(),
		getM_HashMapEdgeList(),
		getEquOder(),
		finalAttrLists,
		initendAttrLists,
		condrefAttrLists,
		getM_HashMapIndexList());
		
		lb.getLoopStructure();
		
		setLoopStrucuture(lb.getM_LoopStrucuture());
		
		HashMap<Integer, HashMap<Integer, String>> AttrLists = lb.getM_AttrLists();
		assignStruAttrToApply(AttrLists);

//		printMathml();
		
//		try {
//			this.printContents();
//		} catch (MathException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
	}
	
	
	public int getLoopSize() {
		int LoopSize = indexHashMapList.size();
		return LoopSize;
	}
	

	
	//========================================================
	//assignStruAttrToAppry
	// 構造情報をapplyへ割り当てる
	//
	//========================================================
	public void assignStruAttrToApply(HashMap<Integer, HashMap<Integer, String>> AttrLists){
		super.assignStruAttrToApply(AttrLists);
	}
	
	//========================================================
	//printContents
	// mathml出力メソッド
	//
	//========================================================
	/*-----解析結果表示メソッド-----*/
	public void printMathml() throws MathException {
		/*開始線出力*/
		System.out.println("<!-------------------------------equation------------------------------->");

		/*数式出力*/
		super.printMathmlExpressions();

		/*改行*/
		System.out.println();
	}

	/*-----解析結果取得メソッド-----*/
	public ArrayList<String> getMathml() throws MathException {
		/*数式出力*/
		return super.getMathmlExpressions();
	}
	
	public ArrayList<MathExpression> kawabataRemoveCondition(){
		HashMap<Integer, Integer> condrefAttrLists = getM_HashMapCondrefAttrLists();
		/*数式を保管しておけるベクタが必要?*/
		ArrayList<MathExpression> ar = new ArrayList<MathExpression>();
		Vector<MathExpression> v_ex = (Vector<MathExpression>) m_vecMathExpression.clone();
		m_vecMathExpression.clear();
		for(int i=0;i<v_ex.size();i++){
			if(condrefAttrLists.containsKey(i)){
				MathExpression me = v_ex.get(i);
				ar.add(me);
			}else{
				m_vecMathExpression.add(v_ex.get(i));
			}
		}
		
		return ar;
		
	}
	
	private Vector<MathExpression> replaceEquOrder(SimpleRecMLAnalyzer sa) {
		Vector<Integer> eo = sa.getEquOder();
		Vector<MathExpression> clone = (Vector<MathExpression>) sa.m_vecMathExpression.clone();
		sa.m_vecMathExpression.removeAllElements();
		Vector<MathExpression> ve = new Vector<MathExpression>();
		int i=0;
		for(; i<eo.size();i++){
			int id = eo.get(i);
			sa.m_vecMathExpression.add(clone.get(id));
		}
		
		for(; i<clone.size();i++){
			sa.m_vecMathExpression.add(clone.get(i));
		}
		
		return ve;
	}
	
	
	public HashMap<Integer,Integer> serchIndexVaruable(HashMap<Integer, String> indexList,Math_ci mci) throws MathException{
		return super.serchIndexVaruable(indexList,mci);
	}
	
}
