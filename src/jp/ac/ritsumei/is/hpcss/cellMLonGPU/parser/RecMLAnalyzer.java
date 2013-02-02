package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

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

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.RecMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.RecMLDefinition.eRecMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.RecMLDefinition.eRecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.RecMLVariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.SimpleRecMLVariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.PairList;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.EqnDepTree.EqnDepTree;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.recml.RecMLVertex;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.CreateRecMLVariableTableVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.CreateSimpleRecMLVariableTableVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.SimpleRecML_SetLeftSideRightSideVariableVisitor;


/**
 * CellML解析クラス
 */
public class RecMLAnalyzer extends MathMLAnalyzer {

	/*数式解析中判定*/
	private boolean m_bMathParsing;
	private boolean m_bAttrParsing;
	
	private RecMLVariableTable recMLVariableTable;
	public HashMap<Integer,Vector<Integer>> simulEquationIDList;
	public ArrayList<Vector<MathExpression>> simulEquationList;
	public ArrayList<Vector<Integer>> resultMaximumMatching;
	public HashMap<Integer,String> variableNameMap;
	
	// loop index variable name list (initialized in constructor)
	// just for test
	// should be implemented as hashmap or new class
//	private String[] indexStringList;
	
	// equation dependent tree root node
	private EqnDepTree root;
	EqnDepTree now;
	int type; // 0=pre, 1=init, 2=inner, 3=loopcond, 4=final, 5=post
	

	//HashMap
	private HashMap<Integer, String> indexHashMapList;
	public HashMap<Integer, String> getM_HashMapIndexList() {
		return  indexHashMapList;
	}		
	
	private HashMap<Math_ci, Integer> m_HashMapRecurList;
	public HashMap<Math_ci, Integer> getM_HashMapRecurVar() {
		return m_HashMapRecurList;
	}
	private HashMap<Math_ci, Integer> m_HashMapArithList;
	public HashMap<Math_ci, Integer> getM_HashMapArithVar() {
		return m_HashMapArithList;
	}
	private HashMap<Math_ci, Integer> m_HashMapConstList;
	public HashMap<Math_ci, Integer> getM_HashMapConstVar() {
		return m_HashMapConstList;
	}
	private HashMap<Math_ci, Integer> m_HashMapOutputList;
	public HashMap<Math_ci, Integer> getM_HashMapOutputVar() {
		return m_HashMapOutputList;
	}	
	private HashMap<Math_ci, Integer> m_HashMapStepList;
	public HashMap<Math_ci, Integer> getM_HashMapStepVar() {
		return m_HashMapStepList;
	}
	
	private ArrayList<Math_ci> m_ArrayListRecurList;
	public ArrayList<Math_ci>  getM_ArrayListRecurVar() {
		return m_ArrayListRecurList;
	}
	private ArrayList<Math_ci> m_ArrayListArithList;
	public ArrayList<Math_ci>  getM_ArrayListArithVar() {
		return m_ArrayListArithList;
	}
	private ArrayList<Math_ci> m_ArrayListConstList;
	public ArrayList<Math_ci>  getM_ArrayListConstVar() {
		return m_ArrayListConstList;
	}
	private ArrayList<Math_ci> m_ArrayListOutputList;
	public ArrayList<Math_ci>  getM_ArrayListOutputVar() {
		return m_ArrayListOutputList;
	}
	private ArrayList<Math_ci> m_ArrayListStepList;
	public ArrayList<Math_ci>  getM_ArrayListStepVar() {
		return m_ArrayListStepList;
	}
	
	/* equation vector */
	Vector<MathExpression> m_vecExpression;
	public Vector<MathExpression> getM_vecExpression() {
		return m_vecExpression;
	}

	
	//連立方程式セットの追加
	public void setSimulEquationList(ArrayList<Vector<MathExpression>> list){
		this.simulEquationList=list;
	}
	
	//連立方程式セットの取得
	public ArrayList<Vector<MathExpression>> getSimulEquationList(){
		return this.simulEquationList;
	}
	
	
	//idから数式取得メソッド
	public MathExpression getExpressionFromID(long id) {
		MathExpression exp = new MathExpression();
		for(int i=0;i<m_vecMathExpression.size();i++){
			if(m_vecMathExpression.get(i).getExID() == id){
				exp=m_vecMathExpression.get(i);
				break;
			}
		}
		return exp;
	}
	
	//idから数式位置取得メソッド
	public int getLocationFromID(long id) {
		int here = -1;
		for(int i=0;i<m_vecMathExpression.size();i++){
			if(m_vecMathExpression.get(i).getExID() == id){
				here=i;
			}
		}
		return here;
	}
	
	//数式の置換メソッド
	public void setM_vecMathExpression(int i,MathExpression exp) {
		m_vecMathExpression.set(i, exp);
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
	
	/*-----コンストラクタ-----*/
	public RecMLAnalyzer() {
		m_bMathParsing = false;
		m_bAttrParsing = false;
		m_vecExpression = new Vector<MathExpression>();
		this.simulEquationIDList = new HashMap<Integer,Vector<Integer>>();
		this.simulEquationList = new ArrayList<Vector<MathExpression>>();
		this.resultMaximumMatching = new ArrayList<Vector<Integer>>();
		this.variableNameMap = new HashMap<Integer,String>();
		setRoot(null);
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
		m_HashMapStepList = StepVarHM;
		
		ArrayList<Math_ci> RecurVarAL = new ArrayList<Math_ci>();
		m_ArrayListRecurList = RecurVarAL;
		ArrayList<Math_ci> ArithVarAL = new ArrayList<Math_ci>();
		m_ArrayListArithList = ArithVarAL;
		ArrayList<Math_ci> ConstVarAL = new ArrayList<Math_ci>();
		m_ArrayListConstList = ConstVarAL;
		ArrayList<Math_ci> OutputVarAL = new ArrayList<Math_ci>();
		m_ArrayListOutputList = OutputVarAL;
		ArrayList<Math_ci> StepVarAL = new ArrayList<Math_ci>();
		m_ArrayListStepList = StepVarAL;
		
		m_vecRecurVar = new Vector<Math_ci>();
		m_vecArithVar = new Vector<Math_ci>();
		m_vecConstVar = new Vector<Math_ci>();
		m_vecCondition = new Vector<Math_ci>();
		m_vecOutput = new Vector<Math_ci>();
		
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
		}

		//-----------------------------------------------------
		//RecMLの解析
		//-----------------------------------------------------
		else {
			/*タグidの取得*/
			eRecMLTag tagId;

			try {
				tagId = RecMLDefinition.getRecMLTagId(strTag);
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
					if (getRoot() == null) {
						setRoot(new EqnDepTree(loopNum));
						now = getRoot();
					} else {
						getRoot().push(now);
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
			case CTAG_POSITION:
				{
					// loop structure construction
					String positionname = pXMLAttr.getValue("name");
					if (positionname.equals(RecMLDefinition.RECML_ATTR_PRE)) {
						type = 0;
					} else if (positionname.equals(RecMLDefinition.RECML_ATTR_INIT)) {
						type = 1;
					} else if (positionname.equals(RecMLDefinition.RECML_ATTR_INNER)) {
						type = 2;
					} else if (positionname.equals(RecMLDefinition.RECML_ATTR_LOOPCOND)) {
						type = 3;
					} else if (positionname.equals(RecMLDefinition.RECML_ATTR_FINAL)) {
						type = 4;
					} else if (positionname.equals(RecMLDefinition.RECML_ATTR_POST)) {
						type = 5;
					}
					break;
				}
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
				//--------------------------------------変数宣言
			case CTAG_VARIABLE:
				{
					/*変数名とタイプ取得*/
					String strName = pXMLAttr.getValue("name");
					String strType = pXMLAttr.getValue("type");
					eRecMLVarType varType = RecMLDefinition.getRecMLVarType(strType);
	
					/*変数名から変数インスタンス生成*/
					Math_ci pVariable =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strName);
					
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

					case CVAR_TYPE_STEPVAR:
						m_ArrayListStepList.add(pVariable);
						m_HashMapStepList.put(pVariable, squarebracketsnum);
//						m_vecOutput.add(pVariable);
						break;
					}
					break;
				}
				//--------------------------------------変数宣言
				
				
				case CTAG_NODE:
				{
					String equation = pXMLAttr.getValue("equation");
					String variable = pXMLAttr.getValue("variable");
					
					Vector<Integer> pairID = new Vector<Integer>();
					pairID.add(Integer.parseInt(equation));
					pairID.add(Integer.parseInt(variable));
					this.resultMaximumMatching.add(pairID);
					break;
				}
		
				
				
				case CTAG_SIMULLTANEOUS:
				{
					/*連立IDと数式IDを取得*/
					String strID = pXMLAttr.getValue("id");
					String strEqID = pXMLAttr.getValue("equation");
					
					if(this.simulEquationIDList.containsKey(Integer.parseInt(strID))){
						Vector<Integer> list =  this.simulEquationIDList.get(Integer.parseInt(strID));
						list.add(Integer.parseInt(strEqID));
						this.simulEquationIDList.put(Integer.parseInt(strID),list);
						break;
					}else{
						Vector<Integer> list = new Vector<Integer>();
						list.add(Integer.parseInt(strEqID));
						this.simulEquationIDList.put(Integer.parseInt(strID),list);
						break;
					}
				}
				
				
				case CTAG_REFVARIABLE:
				{
					/*変数IDと変数文字列を取得*/
					String strID = pXMLAttr.getValue("id");
					String strVarName = pXMLAttr.getValue("variableName");
					
					variableNameMap.put(Integer.parseInt(strID), strVarName);
					break;
				}

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
			if (strTag == RecMLDefinition.RECML_TAG_STR_MATH) {
				m_bMathParsing = false;
				return;
			}
			/*MathML解析器に投げる*/
			super.findTagEnd(strTag);
		}

		//-----------------------------------------------------
		//RecML analysis
		//-----------------------------------------------------
		else {

			/*タグidの取得*/
			eRecMLTag tagId = RecMLDefinition.getRecMLTagId(strTag);

			/*タグ種別ごとの処理*/
			switch (tagId) {
			case CTAG_LOOPSTRUCT:
				// end of loop tag
				if (!getRoot().emptyStack()) {
					now = getRoot().pop();
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

		/*数式出力*/
		super.printExpressions();

		/*改行*/
		System.out.println();
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
	
	public static void main(String[] args) {
		RecMLAnalyzer recMLAnalyzer = new RecMLAnalyzer();
		
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

		XMLHandler handler = new XMLHandler(recMLAnalyzer);
		parser.setContentHandler(handler);
		try {
			parser.parse(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] strAttr = new String[] {"init", null, null};
		System.out.println("loop1 = " + strAttr[0] + "\n");
		ArrayList expIndex = new ArrayList();
		expIndex = recMLAnalyzer.getExpressionWithAttr(strAttr);
		
		for (int i=0; i < expIndex.size(); i++){
			int index = Integer.parseInt(expIndex.get(i).toString());
			try {
				System.out.println(recMLAnalyzer.getExpression(index).toLegalString());
			} catch (MathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	//HashMap
	protected void setIndexHashMap(int LoopNumber, String varName) {
		indexHashMapList.put(LoopNumber, varName);
	}
	
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

		return nextChildLoopNumber(strAttr, getRoot());
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
		CreateRecMLVariableTableVisitor visitor = new CreateRecMLVariableTableVisitor();
		
		 for(MathExpression expr :m_vecMathExpression)
			 expr.getRootFactor().traverse(visitor);
		 recMLVariableTable=visitor.getTable();
	}
	
	public RecMLVariableTable getRecMLVariableTable() {
		return recMLVariableTable;
	}


	public EqnDepTree getRoot() {
		return root;
	}


	public void setRoot(EqnDepTree root) {
		this.root = root;
	}
}
