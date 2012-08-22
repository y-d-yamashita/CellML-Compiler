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
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.SimpleRecMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.SimpleRecMLDefinition.eRecMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.SimpleRecMLDefinition.eRecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.EqnDepTree.EqnDepTree;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;


/**
 * CellML解析クラス
 */
public class SimpleRecMLAnalyzer extends MathMLAnalyzer {

	/*数式解析中判定*/
	private boolean m_bMathParsing;
	private boolean m_bAttrParsing;
	
	/*変数型に対する変数の個数*/
	int RecurNum = 0;
	int ArithNum = 0;
	int OutputNum = 0;
	
	/*変数取得HashMap*/
	private HashMap<Integer, Math_ci> m_HashMapSimpleRecurList;
	public HashMap<Integer, Math_ci> getM_HashMapRecurVar() {
		return m_HashMapSimpleRecurList;
	}
	
	private HashMap<Integer, Math_ci> m_HashMapSimpleArithList;
	public HashMap<Integer, Math_ci> getM_HashMapArithVar() {
		return m_HashMapSimpleArithList;
	}
	
	private HashMap<Integer, Math_ci> m_HashMapSimpleOutputList;
	public HashMap<Integer, Math_ci> getM_HashMapOutputVar() {
		return m_HashMapSimpleOutputList;
	}
	
	/*-----コンストラクタ-----*/
	public SimpleRecMLAnalyzer() {
		m_bMathParsing = false;
		m_bAttrParsing = false;
		
		HashMap<Integer, Math_ci> SimpleRecurVarHM = new HashMap<Integer, Math_ci>();
		m_HashMapSimpleRecurList = SimpleRecurVarHM;

		HashMap<Integer, Math_ci> SimpleArithVarHM = new HashMap<Integer, Math_ci>();
		m_HashMapSimpleArithList = SimpleArithVarHM;
		
		HashMap<Integer, Math_ci> SimpleOutputVarHM = new HashMap<Integer, Math_ci>();
		m_HashMapSimpleOutputList = SimpleOutputVarHM;
		
	}
	
	/**分類後非微分数式ベクタ*/
	Vector<MathExpression> m_vecNonDiffExpression;
	
	/**分類後通常変数ベクタ*/
	Vector<Math_ci> m_vecArithVar;

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
				tagId = SimpleRecMLDefinition.getRecMLTagId(strTag);
			}
			catch (RecMLException e) {
				/*特定のタグ以外は無視する*/
				return;
			}


			
			/*タグ種別ごとの処理*/
			switch (tagId) {


			// mathml process
			case CTAG_MATH:
				{
					m_bMathParsing = true;
					m_NextOperandKind = null;
					break;
				}
				
			case CTAG_VARIABLE:
			{
				/*変数名とタイプ取得*/
				String strName = pXMLAttr.getValue("name");
				String strType = pXMLAttr.getValue("type");
				eRecMLVarType varType = SimpleRecMLDefinition.getRecMLVarType(strType);

				/*変数名から変数インスタンス生成*/
				Math_ci pVariable =
					(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strName);

//				/*input変数の登録*/
//				if (tagId == eTecMLTag.TTAG_INPUTVAR) {
//					m_pInputVar = pVariable;
//				}
//				/*output変数の登録*/
//				else if (tagId == eTecMLTag.TTAG_OUTPUTVAR) {
//					m_pOutputVar = pVariable;
//				}

//				/*タイプごとに変数追加*/
//				switch (varType) {
//
//					//----------------------------------RECURVAR変数型
//				case CVAR_TYPE_RECURVAR:
//					m_vecRecurVar.add(pVariable);
//					break;
//
//					//----------------------------------ARITHVAR変数型
//				case CVAR_TYPE_ARITHVAR:
//					m_vecArithVar.add(pVariable);
//					break;
//
//					//----------------------------------CONSTVAR変数型
//				case CVAR_TYPE_CONSTVAR:
//					m_vecConstVar.add(pVariable);
//					break;
//
//					//----------------------------------CONDITION変数型
//				case CVAR_TYPE_STR_CONDITION:
//					m_vecCondition.add(pVariable);
//					break;
//
//					//----------------------------------OUTPUT変数型
//				case CVAR_TYPE_OUTPUT:
//					m_vecOutput.add(pVariable);
//					break;
//
//				}
				
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
//					m_ArrayListRecurList.add(pVariable);
					m_HashMapSimpleRecurList.put(RecurNum,pVariable);
//					m_vecRecurVar.add(pVariable);
					RecurNum++;
					break;

//					//----------------------------------ARITHVAR変数型
				case CVAR_TYPE_ARITHVAR:
//					m_ArrayListArithList.add(pVariable);
					m_HashMapSimpleArithList.put(ArithNum,pVariable);
//					m_vecArithVar.add(pVariable);
					ArithNum++;
					break;
//
//					//----------------------------------CONSTVAR変数型
//				case CVAR_TYPE_CONSTVAR:
//					m_ArrayListConstList.add(pVariable);
//					m_HashMapConstList.put(pVariable, squarebracketsnum);
////					m_vecConstVar.add(pVariable);
//					break;
//					
//					//----------------------------------OUTPUT変数型
				case CVAR_TYPE_OUTPUT:
//					m_ArrayListOutputList.add(pVariable);
					m_HashMapSimpleOutputList.put(OutputNum,pVariable);
//					m_vecOutput.add(pVariable);
					OutputNum++;
					break;

				}
				break;
			}
				
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

		//-----------------------------------------------------
		//RecML analysis
		//-----------------------------------------------------
//		else {
//
//			/*タグidの取得*/
//			eRecMLTag tagId = SimpleRecMLDefinition.getRecMLTagId(strTag);
//
//			/*タグ種別ごとの処理*/
//			switch (tagId) {
//			case CTAG_LOOPSTRUCT:
//				// end of loop tag
//				if (!root.emptyStack()) {
//					now = root.pop();
//				} else {
//					;
//				}
//				// for debug
//				//root.printString();
//				break;
//					
//			default:
//			}
//		}
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
//	public ArrayList getExpressionWithAttr(String[] strAttr) {
//		ArrayList indexList = new ArrayList();
//		for (int i=0; i<m_vecAttrList.size(); i++){
//			if(Arrays.equals(strAttr, m_vecAttrList.get(i))){
//				indexList.add(i);
//			}
//		}
//		return indexList;
//	}
	public void removeSelector(){
		super.removeAllSelector();
	}
	//========================================================
	//printContents
	// 解析内容標準出力メソッド
	//
	//========================================================
	/*-----解析結果表示メソッド-----*/
	public void printContents() throws MathException {
		/*開始線出力*/
		System.out.println("[SimpleRecML]------------------------------------");

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
//	public MathExpression getExpression(int index){
//		/*数式出力*/
//		return super.getExpression(index);
//	}
	
	public static void main(String[] args) throws MathException {
		SimpleRecMLAnalyzer simpleRecMLAnalyzer = new SimpleRecMLAnalyzer();
		
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
			parser.parse(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		simpleRecMLAnalyzer.removeSelector();
		
//		String[] strAttr = new String[] {"init", null, null};
//		System.out.println("loop1 = " + strAttr[0] + "\n");
//		ArrayList expIndex = new ArrayList();
//		expIndex = simpleRecMLAnalyzer.getExpressionWithAttr(strAttr);
		
//		for (int i=0; i < expIndex.size(); i++){
//			int index = Integer.parseInt(expIndex.get(i).toString());
//			try {
//				System.out.println(simpleRecMLAnalyzer.getExpression(index).toLegalString());
//			} catch (MathException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		int i = 0;
		System.out.println(i);
		System.out.println(simpleRecMLAnalyzer.m_HashMapSimpleRecurList.size());
		for(; i<simpleRecMLAnalyzer.m_HashMapSimpleRecurList.size(); i++){
			System.out.println(i);
			System.out.println(simpleRecMLAnalyzer.m_HashMapSimpleRecurList.get(i).toLegalString());
		}
		
		/*数式の数*/
		System.out.println("simpleRecMLAnalyzer.getExpressionCount()");
		System.out.println(simpleRecMLAnalyzer.getExpressionCount());
		System.out.println();
		
		/*ある数式の変数の個数を取得*/
		System.out.println("simpleRecMLAnalyzer.getExpression().getVariable()");
		System.out.println(simpleRecMLAnalyzer.getExpression(0).getVariableCount());
		System.out.println();
		
		/*数式取得*/
		System.out.println("simpleRecMLAnalyzer.getExpression().getVariable()");
		System.out.println(simpleRecMLAnalyzer.getExpression(0).getVariable(1).toLegalString());
		System.out.println();
		
		System.out.println("simpleRecMLAnalyzer.getExpression().toLegalString()");
		System.out.println(simpleRecMLAnalyzer.getExpression(0).toLegalString());
		System.out.println();

		try {
			simpleRecMLAnalyzer.printContents();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finish.");
	}
	
	
	// return true if LoopNumber has inner element
//	public boolean hasInner(int LoopNumber) {
//		boolean flag = false;
//		for (int i=0; i<m_vecAttrList.size(); i++){
//			String[] strAttr = m_vecAttrList.get(i);
//			if (strAttr[LoopNumber]!=null) {
//				if (strAttr[LoopNumber].equals("inner")) {
//					flag = true;
//				}
//			}
//		}
//		return flag;
//	}

//	private int nextChildLoopNumber(String[] strAttr, EqnDepTree node) {
//		int childLoopNum = -1;
//		int loopNum = node.loopNumber;
//		String curAttr = strAttr[loopNum];
//		if ((curAttr == null) && (node != null)) {
//			return loopNum;
//		}
//
//		if (curAttr.equals("pre") && (node.n_pre != null)) {
//			childLoopNum = nextChildLoopNumber(strAttr, node.n_pre);
//		} else if (curAttr.equals("init") && (node.n_init != null)) {
//			childLoopNum = nextChildLoopNumber(strAttr, node.n_init);
//		} else if (curAttr.equals("inner") && (node.n_inner != null)) {
//			childLoopNum = nextChildLoopNumber(strAttr, node.n_inner);
//		} else if (curAttr.equals("loopcond") && (node.n_loopcond != null)) {
//			childLoopNum = nextChildLoopNumber(strAttr, node.n_loopcond);
//		} else if (curAttr.equals("final") && (node.n_final != null)) {
//			childLoopNum = nextChildLoopNumber(strAttr, node.n_final);
//		} else if (curAttr.equals("post") && (node.n_post != null)) {
//			childLoopNum = nextChildLoopNumber(strAttr, node.n_post);
//		} 
//		return childLoopNum;
//	}
	
	
	
	
	/**
	 * 変数テーブルをRelMLに適用する.
	 * @param pRelMLAnalyzer RelML解析器インスタンス
	 * @throws CellMLException
	 * @throws RelMLException
	 * @throws MathException
	 */
	public void applyRelML(RelMLAnalyzer pRelMLAnalyzer)
	throws CellMLException, RelMLException, MathException {
//		/*変数テーブルをRelMLに適用*/
//		pRelMLAnalyzer.applyComponentTable(m_pComponentTable);
//
//		/*変数ベクタをコピー*/
//		m_vecTimeVar = pRelMLAnalyzer.m_vecTimeVar;
//		m_vecConstVar = pRelMLAnalyzer.m_vecConstVar;

		//-------------------------------------------------
		//数式の解析
		//-------------------------------------------------
		/*数式数を取得*/
		int nExpressionNum = getExpressionCount();

		for (int i = 0; i < nExpressionNum; i++) {
			/*数式取得*/
			MathExpression pMathExp = getExpression(i);

			/*左辺式取得*/
			MathExpression pLeftExp = pMathExp.getLeftExpression();

			if (pLeftExp == null) {
				throw new CellMLException("CellMLAnalyzer","applyRelML",
							  "failed to parse expression");
			}

			/*左辺変数取得*/
			Math_ci pLeftVar = (Math_ci)pLeftExp.getFirstVariable();


			/*通常変数ベクタに追加*/
			m_vecArithVar.add(pLeftVar);

			/*非微分式として登録*/
			m_vecNonDiffExpression.add(pMathExp);

		}

		/*式の並べ替えを行う*/
		this.sortExpressions();
//		System.out.println("sort m_vecNonDiffExpression");
//		for (int i = 0; i < m_vecNonDiffExpression.size(); i++) {
//			MathExpression it = m_vecNonDiffExpression.get(i);
//			System.out.println(i + "\t" + it.toLegalString());
//		}
//		System.out.println("sort m_vecArithVar");
//		for (int i = 0; i < m_vecArithVar.size(); i++) {
//			Math_ci it = m_vecArithVar.get(i);
//			System.out.println(i + "\t" + it.toLegalString());
//		}
//		printContents();	// debug
	}
	
	/**
	 * 計算式を並べ替える.
	 * 次の計算式を並び替える
	 * m_vecNonDiffExpression 並び替える数式ベクタ
	 * m_vecArithVar 未初期化変数リスト
	 * @throws MathException
	 */
	private void sortExpressions() throws MathException {
		Vector<MathExpression> pvecExpressions = m_vecNonDiffExpression;
		/*並び替え後のベクタ*/
		Vector<MathExpression> vecReorderedExpression = new Vector<MathExpression>();
		Vector<Math_ci> vecReorderedVariables = new Vector<Math_ci>();

		/*未初期化変数ベクタ*/
		Vector<Math_ci> vecUnInitializedVar = m_vecArithVar;

		/*式を順番に調べていく*/
		/*新しいベクタにすべての式が入るまで繰り返し*/
		while (pvecExpressions.size() > 0) {
//			System.out.println("pvecExpressions->size(): " + pvecExpressions.size());

			Vector<MathExpression> newPvecExpressions = new Vector<MathExpression>();
			/**
			 * VC++版と同じ並び順の出力を生成するためのフラグ.
			 * このフラグが行う処理はなくてもいい.
			 */
			boolean removeFlagForSameAction_CPlusPlusVersion = false;
			for (MathExpression it: pvecExpressions) {
				if (removeFlagForSameAction_CPlusPlusVersion) {
					removeFlagForSameAction_CPlusPlusVersion = false;
					newPvecExpressions.add(it);
					continue;
				}

				/*式の取得*/
				MathExpression pExp = it;
				MathExpression pLeftExp = pExp.getLeftExpression();
				Math_ci pLeftVar = pLeftExp.getFirstVariable();

				/*未初期化変数のチェック*/
				boolean bUnInitialized = false;
				int nVariableNum = pExp.getVariableCount();
//				System.out.println(nVariableNum + "\t" + it.toLegalString());

				for (int i = 0; i < nVariableNum; i++) {

					/*変数取得*/
					MathOperand pVariable = pExp.getVariable(i);

					/*左辺値と同じものは初期化済み扱い*/
					if (pVariable.toLegalString().equals(pLeftVar.toLegalString())) {
						continue;
					}

					/*未初期化変数ベクタとの比較*/
					for (Math_ci it2: vecUnInitializedVar) {

						/*変数名が一致すれば未初期化*/
						if (it2.toLegalString().equals(pVariable.toLegalString())) {
							bUnInitialized = true;
							break;
						}
					}

					if (bUnInitialized) {
						break;
					}
				}

				/*未初期化の式は後回しにする*/
				if (bUnInitialized) {
					newPvecExpressions.add(it);
//					System.out.println("UnInitialized");
					continue;
				}
				/*初期化済みの右辺式を持つ式*/
				else {
					/*式を新しいベクタに加える*/
					vecReorderedExpression.add(pExp);
					vecReorderedVariables.add(pLeftVar);
//					System.out.println("Initialized");
					removeFlagForSameAction_CPlusPlusVersion = true;

					/*未初期化変数リストから左辺変数を削除*/
					for (Math_ci it2: vecUnInitializedVar) {

						/*一致する変数を削除*/
						if (it2.toLegalString().equals(pLeftVar.toLegalString())) {
							vecUnInitializedVar.remove(it2);
//							System.out.println("removed " + it2.toLegalString());
							break;
						}
					}

					/*元の式をベクタから削除*/
					//pvecExpressions.remove(it);

				}
			}
			pvecExpressions = newPvecExpressions;
		}

		/*新しいベクタを適用する*/
		m_vecNonDiffExpression = vecReorderedExpression;
		m_vecArithVar = vecReorderedVariables;
	}
	
	
}
