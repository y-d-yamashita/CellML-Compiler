package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML.CellMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML.CellMLDefinition.eCellMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.CellML_SetLeftSideRightSideVariableVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML.CellMLEquationAndVariableContainer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.ComponentTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.VariableTable;

/**
 * CellML解析クラス.
 */
public class CellMLAnalyzer extends MathMLAnalyzer {

	// 未使用なのでコメントにした
//	public static final String CELLML_VAR_PREFIX_STR = "$";

	/**数式解析中判定*/
	private boolean m_bMathParsing;

	/**分類後Recur変数ベクタ*/
	Vector<Math_ci> m_vecRecurVar;
	/**
	 * 分類後Recur変数ベクタを取得する.
	 * @return 分類後Recur変数ベクタ
	 */
	public Vector<Math_ci> getM_vecRecurVar() {
		return m_vecRecurVar;
	}

	/**分類後中間変数ベクタ*/
	Vector<Math_ci> m_vecArithVar;
	/**
	 * 分類後中間変数ベクタを取得する.
	 * @return 分類後中間変数ベクタ
	 */
	public Vector<Math_ci> getM_vecArithVar() {
		return m_vecArithVar;
	}

	/**分類後定数変数ベクタ*/
	Vector<Math_ci> m_vecConstVar;
	/**
	 * 分類後定数変数ベクタを取得する.
	 * @return 分類後定数変数ベクタ
	 */
	public Vector<Math_ci> getM_vecConstVar() {
		return m_vecConstVar;
	}

	/**変数テーブル*/
	ComponentTable m_pComponentTable;
	/**コンポーネントに対応する変数テーブル*/
	VariableTable m_pCurVariableTable;

	/**分類後微分数式ベクタ*/
	Vector<MathExpression> m_vecDiffExpression;
	/**
	 * 分類後微分数式ベクタを取得する.
	 * @return 分類後微分数式ベクタ
	 */
	public Vector<MathExpression> getM_vecDiffExpression() {
		return m_vecDiffExpression;
	}

	/**分類後非微分数式ベクタ*/
	Vector<MathExpression> m_vecNonDiffExpression;
	/**
	 * 分類後非微分数式ベクタを取得する.
	 * @return 分類後非微分数式ベクタ
	 */
	public Vector<MathExpression> getM_vecNonDiffExpression() {
		return m_vecNonDiffExpression;
	}

	/**
	 * CellML解析インスタンスを作成する.
	 */
	public CellMLAnalyzer() {
		m_bMathParsing = false;
		m_pComponentTable = null;
		m_pCurVariableTable = null;

		m_vecRecurVar = new Vector<Math_ci>();
		m_vecArithVar = new Vector<Math_ci>();
		m_vecConstVar = new Vector<Math_ci>();
		m_vecDiffExpression = new Vector<MathExpression>();
		m_vecNonDiffExpression = new Vector<MathExpression>();
	}
	/**
	 * CellML解析インスタンスを作成する.
	 * @param pComponentTable 変数テーブル
	 */
	public CellMLAnalyzer(ComponentTable pComponentTable) {
		this();
		m_pComponentTable = pComponentTable;
	}

	/**
	 * 変数テーブルを設定する.
	 * @param pComponentTable 変数テーブル
	 */
	public void setComponentTable(ComponentTable pComponentTable) {
		m_pComponentTable = pComponentTable;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findTagStart(java.lang.String, jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAttribute)
	 */
	public void findTagStart(String strTag, XMLAttribute pXMLAttr)
	throws CellMLException, MathException, XMLException, RelMLException, TecMLException, RecMLException {
		/*変数テーブルを持たなければ例外*/
		if (m_pComponentTable == null) {
			throw new CellMLException("CellMLAnalyzer","findTagStart",
						  "component table not found");
		}

		//-----------------------------------------------------
		//数式部の解析
		//-----------------------------------------------------
		if (m_bMathParsing) {

			/*MathML解析器に投げる*/
			super.findTagStart(strTag,pXMLAttr);
		}

		//-----------------------------------------------------
		//CellMLの解析
		//-----------------------------------------------------
		else {

			/*タグidの取得*/
			eCellMLTag tagId;

			try {
				tagId = CellMLDefinition.getCellMLTagId(strTag);
			}
			catch (CellMLException e) {
				/*特定のタグ以外は無視する*/
				return;
			}

			/*タグ種別ごとの処理*/
			switch (tagId) {

				//-----------------------------------数式解析開始
			case CTAG_MATH:
				{
					m_bMathParsing = true;
					m_NextOperandKind = null;
					break;
				}
				//-----------------------------------コンポーネント解析開始
			case CTAG_COMPONENT:
				{
					String strComponentName;

					try {
						/*コンポーネント名を取得*/
						strComponentName =
							pXMLAttr.getValue(CellMLDefinition.CELLML_ATTR_STR_NAME);
					}
					catch (Exception e) {
						throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
									  "component name not found");
					}

					try {
						/*コンポーネントに対応する変数テーブルを取得*/
						m_pCurVariableTable =
							m_pComponentTable.searchTable(strComponentName);
					}
					catch (TableException e) {
						System.err.println(e.getMessage());
						throw new CellMLException("CellMLAnalyzer","findTagStart",
									  "can't find variable table correspond to component");
					}

					break;
				}
			}
		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findTagEnd(java.lang.String)
	 */
	public void findTagEnd(String strTag)
	throws CellMLException, MathException, RelMLException, RecMLException {
		/*変数テーブルを持たなければ例外*/
		if (m_pComponentTable == null) {
			throw new CellMLException("CellMLAnalyzer","findTagEnd",
						  "component table not found");
		}

		//-----------------------------------------------------
		//数式部の解析
		//-----------------------------------------------------
		if (m_bMathParsing) {

			/*数式解析終了*/
			if (strTag == CellMLDefinition.CELLML_TAG_STR_MATH) {
				m_bMathParsing = false;
				return;
			}

			/*MathML解析器に投げる*/
			super.findTagEnd(strTag);
		}

		//-----------------------------------------------------
		//CellMLの解析
		//-----------------------------------------------------
		else {

			/*タグidの取得*/
			eCellMLTag tagId;

			try {
				tagId = CellMLDefinition.getCellMLTagId(strTag);
			}
			catch (CellMLException e) {
				/*特定のタグ以外は無視する*/
				return;
			}

			/*タグ種別ごとの処理*/
			switch (tagId) {

				//-----------------------------------componentの解析終了
			case CTAG_COMPONENT:
				{
					/*現在の変数テーブルをNULLに*/
					m_pCurVariableTable = null;
					break;
				}

			}
		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findText(java.lang.String)
	 */
	public void findText(String strText)
	throws CellMLException, MathException, TableException {
		/*変数テーブルを持たなければ例外*/
		if (m_pComponentTable == null) {
			throw new CellMLException("CellMLAnalyzer","findText",
						  "component table not found");
		}

		//-----------------------------------------------------
		//数式部の解析
		//-----------------------------------------------------
		if (m_bMathParsing && m_NextOperandKind != null) {
			/*変数の場合,テーブルより完全名を取得*/
			if (m_NextOperandKind == eMathOperand.MOPD_CI) {
				strText = m_pCurVariableTable.getFullName(strText);
			}

			/*MathML解析器に投げる*/
			super.findText(strText);
		}

		//-----------------------------------------------------
		//CellMLの解析
		//-----------------------------------------------------
		else {
		}
	}

	/**
	 * 解析内容を標準出力する.
	 * @throws MathException
	 */
	public void printContents() throws MathException {
		/*開始線出力*/
		System.out.println("[CellML]------------------------------------");

		/*数式出力*/
		super.printExpressions();
		HashMap<String, VariableTable> m_mapComponent = m_pComponentTable.getMapElements();
		for(String key : m_mapComponent.keySet()){
			if(key.equals("Main")){
				m_pCurVariableTable = m_mapComponent.get(key);
			}
		}
		System.out.println(m_pCurVariableTable.toString());
		System.out.println(new CellMLEquationAndVariableContainer(this).toString());

		/*改行*/
		System.out.println();
	}

	/**
	 * 変数が数式のどの位置にあるか調べる
	 * @author m-ara
	 */
	public void setLeftsideRightsideVariable(){
		HashMap<String, VariableTable> m_mapComponent = m_pComponentTable.getMapElements();
		for(String key : m_mapComponent.keySet()){
			m_pCurVariableTable = m_mapComponent.get(key);
			CellML_SetLeftSideRightSideVariableVisitor visitor = new CellML_SetLeftSideRightSideVariableVisitor(m_pCurVariableTable);
			 for(MathExpression expr :m_vecMathExpression){
				 visitor.reset((int)expr.getExID());
				 expr.getRootFactor().traverse(visitor);
				 //visitor.reset();
			 }
		}
	}
	
	/**
	 *変数Typeを取得する
	 * @param pTecMLAnalyzer
	 * @param pRelMLAnalyzer
	 * @throws MathException
	 * @throws TableException
	 * @throws RelMLException
	 * @author m-ara
	 */
	public void setRefVariableType(TecMLAnalyzer pTecMLAnalyzer, RelMLAnalyzer pRelMLAnalyzer) throws MathException, TableException, RelMLException{
		HashMap<String, VariableTable> m_mapComponent = m_pComponentTable.getMapElements();
		for(String key : m_mapComponent.keySet()){
			m_pCurVariableTable = m_mapComponent.get(key);
			m_pCurVariableTable.setRefVariableType(pTecMLAnalyzer, pRelMLAnalyzer);
		}
	}

	//CellMLEquationAndVariableContainer用にVariableTableを渡す
	//今は１コンポーネントしか対応していないので、mainのVariableTableを返す
	//@author m-ara
	public VariableTable getVariableTable() throws TableException{
		m_pCurVariableTable = null;
		HashMap<String, VariableTable> m_mapComponent = m_pComponentTable.getMapElements();
		for(String key : m_mapComponent.keySet()){
			if(key.equals("Main")){
				m_pCurVariableTable = m_mapComponent.get(key);
			}
		}
		return m_pCurVariableTable;
	}
	
	/**
	 * 指定された変数の初期値を取得する
	 * @throws MathException 
	 * @throws TableException 
	 * @author m-ara
	 */
	public String getInitialValue(Math_ci pVariable) throws MathException, TableException{
		m_pCurVariableTable = null;
		
		/*名前の取得と分解*/
		String strVarName = pVariable.toLegalString();
		String strComponentName = null;
		
		//コンポーネント名切り分け
		int nIndexPos = strVarName.indexOf(".");
		if(nIndexPos > 0){
			strComponentName = strVarName.substring(0, nIndexPos);
			strVarName = strVarName.substring(nIndexPos+1);
		}
		
		HashMap<String, VariableTable> m_mapComponent = m_pComponentTable.getMapElements();
		for(String key : m_mapComponent.keySet()){
			if(key.equals(strComponentName)){
				m_pCurVariableTable = m_mapComponent.get(key);
			}
		}
		
		//error handling
		if(m_pCurVariableTable == null){
			throw new MathException("CellMLAnalyzer", "getInitialValue",
					"can't find VariableTable name \"" + strComponentName + "\"");
		}
		
		String initialValue =m_pCurVariableTable.getInitValue(strVarName);
		
		return initialValue;
	}
	
//	/**
//	 * dx/dt等の構造をひとつの変数に変える
//	 * @author m-ara
//	 */
//	public void changeDifferentialParameterToOneVariable(){
//		for(MathExpression pExp : m_vecMathExpression){
//			MathOperator pRootFactor = (MathOperator) pExp.getRootFactor();
//			pRootFactor.changeDifferentialParameterToOneVariable(pRootFactor);
//		}
//	}
}
