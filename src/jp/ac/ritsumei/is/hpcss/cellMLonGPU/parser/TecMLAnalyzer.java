package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLVarType;

/**
 * TecML解析クラス.
 */
public class TecMLAnalyzer extends MathMLAnalyzer {

	/**数式解析中判定*/
	private boolean m_bMathParsing;

	/**式中のrecur変数*/
	Vector<Math_ci> m_vecRecurVar;
	/**
	 * 式中のrecur変数を取得する.
	 * @return 式中のrecur変数
	 */
	public Vector<Math_ci> getM_vecRecurVar() {
		return m_vecRecurVar;
	}

	/**式中の中間変数*/
	Vector<Math_ci> m_vecArithVar;
	/**
	 * 式中の中間変数を取得する.
	 * @return 式中の中間変数
	 */
	public Vector<Math_ci> getM_vecArithVar() {
		return m_vecArithVar;
	}

	/**式中の定数変数*/
	Vector<Math_ci> m_vecConstVar;
	/**
	 * 式中の定数変数を取得する.
	 * @return 式中の定数変数
	 */
	public Vector<Math_ci> getM_vecConstVar() {
		return m_vecConstVar;
	}
	
	/**式中のcondition*/
	Vector<Math_ci> m_vecCondition;
	/**
	 * 式中のconditionを取得する.
	 * @return 式中のcondition
	 */
	public Vector<Math_ci> getM_vecCondition() {
		return m_vecCondition;
	}
	
	/**式中の出力変数*/
	Vector<Math_ci> m_vecOutputVar;
	/**
	 * 式中の出力変数を取得する.
	 * @return 式中の出力変数
	 */
	public Vector<Math_ci> getM_vecOutputVar() {
		return m_vecOutputVar;
	}
	
	/**式中のステップ変数*/
	Vector<Math_ci> m_vecStepVar;
	/**
	 * 式中のステップ変数を取得する.
	 * @return 式中のステップ変数
	 */
	public Vector<Math_ci> getM_vecStepVar() {
		return m_vecStepVar;
	}
	
	/**関数定義解析中判定*/
	boolean m_bFunctionAnalyzing;
	Math_ci m_AnalyzingFuncVariable;

	/**次元対応HashMap*/
	HashMap<Math_ci, Math_ci> m_mapDimensionalDependency;
	
	/**
	 * TecML解析インスタンスを作成する.
	 */
	public TecMLAnalyzer() {
		m_bMathParsing = false;
		m_bFunctionAnalyzing = false;
		m_mapDimensionalDependency = new HashMap<Math_ci, Math_ci>();

		m_vecRecurVar = new Vector<Math_ci>();
		m_vecArithVar = new Vector<Math_ci>();
		m_vecConstVar = new Vector<Math_ci>();
		m_vecCondition = new Vector<Math_ci>();
		m_vecOutputVar = new Vector<Math_ci>();
		m_vecStepVar = new Vector<Math_ci>();
	}

	/*========================================================
	 * findTagStart
	 * 開始タグ解析メソッド
	 * 
	 * @arg
	 * string			strTag		: 開始タグ名
	 * XMLAttribute*	pXMLAttr	: 属性クラスインスタンス
	 * 
	 * (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findTagStart(java.lang.String, jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAttribute)
	 * ======================================================== */
	public void findTagStart(String strTag, XMLAttribute pXMLAttr)
	throws MathException, XMLException, RelMLException, CellMLException, TecMLException, RecMLException {
		/*数式の解析*/
		if (m_bMathParsing) {
			super.findTagStart(strTag,pXMLAttr);
			/*数式Type取得*/
			String strType = pXMLAttr.getValue("type");
			if(strType != null){
				HashMap<String, String> pHashMap = new HashMap<String, String>();
				pHashMap.put("type", strType);
				//loopindex取得
				String strLoopindex = pXMLAttr.getValue("loopindex");
				pHashMap.put("loopindex", strLoopindex);
				m_pCurMathExpression.assignExpInfoToApply(pHashMap);
			}
		}

		/*TecML解析*/
		else {

			/*タグ特定*/
			eTecMLTag tagId = TecMLDefinition.getTecMLTagId(strTag);

			/*タグ種別ごとの処理*/
			switch (tagId) {

				//--------------------------------------変数宣言
			case TTAG_VARIABLE:
				{
					/*変数名とタイプ取得*/
					String strName = pXMLAttr.getValue("name");
					String strType = pXMLAttr.getValue("type");
					eTecMLVarType varType = TecMLDefinition.getTecMLVarType(strType);

					/*変数名から変数インスタンス生成*/
					Math_ci pVariable =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strName);

					/*タイプごとに変数追加*/
					switch (varType) {

						//----------------------------------Recur型
					case TVAR_TYPE_RECURVAR:
						m_vecRecurVar.add(pVariable);
						break;

						//----------------------------------中間変数型
					case TVAR_TYPE_ARITHVAR:
						m_vecArithVar.add(pVariable);
						break;

						//----------------------------------定数型
					case TVAR_TYPE_CONSTVAR:
						m_vecConstVar.add(pVariable);
						break;
					
						//----------------------------------condition
					case TVAR_TYPE_CONDITION:
						m_vecCondition.add(pVariable);
						break;
						
						//----------------------------------ステップを表す変数型
					case TVAR_TYPE_STEPVAR:
						m_vecStepVar.add(pVariable);
						break;

					}

					break;
				}

				//--------------------------------------関数プロトタイプ宣言
			case TTAG_FUNCTION:
				{
//					m_bFunctionAnalyzing = true;
//					
//					/*変数名取得*/
//					String strName = pXMLAttr.getValue("name");
//					
//					/*変数名から変数インスタンス生成*/
//					m_AnalyzingFuncVariable =
//						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strName);
					
					break;
				}

				//---------------------------------------数式記述による次元の制約
			case TTAG_DIMENSION:
				{
					/*左辺変数取得*/
					String strLeft = pXMLAttr.getValue("left");
					/*変数名から変数インスタンス生成*/
					Math_ci pLVariable =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strLeft);
					/*右辺変数取得*/
					String strRight = pXMLAttr.getValue("right");
					/*変数名から変数インスタンス生成*/
					Math_ci pRVariable =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strRight);
					
					m_mapDimensionalDependency.put(pLVariable, pRVariable);
					
					break;
				}

				//--------------------------------------数式部分
			case TTAG_MATH:
				/*MathMLパース開始*/
				m_bMathParsing = true;
				break;

			}
		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findTagEnd(java.lang.String)
	 */
	public void findTagEnd(String strTag)
	throws MathException, RelMLException, CellMLException, RecMLException {
		/*数式の解析*/
		if (m_bMathParsing) {

			/*数式解析終了*/
			if (strTag == TecMLDefinition.TECML_TAG_STR_MATH) {
				m_bMathParsing = false;
				return;
			}

			super.findTagEnd(strTag);
			
			
			
		}
//		else if (m_bFunctionAnalyzing) {
//
//			/*関数解析終了*/
//			if (strTag == TecMLDefinition.TECML_TAG_STR_FUNCTION) {
//				m_bFunctionAnalyzing = false;
//			}
//			
//		}

		/*TecML解析*/
		else {
		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findText(java.lang.String)
	 */
	public void findText(String strText)
	throws MathException, CellMLException, TableException {
		/*数式の解析*/
		if (m_bMathParsing) {
			super.findText(strText);
		}

		/*TecML解析*/
		else {
		}
	}

	/**
	 * 解析内容を標準出力する.
	 * @throws MathException
	 */
	public void printContents() throws MathException {
		//-------------------------------------------------
		//出力
		//-------------------------------------------------
		/*開始線出力*/
		System.out.println("[TecML]------------------------------------");

		printContents("recurvar", m_vecRecurVar);
		printContents("arithvar", m_vecArithVar);
		printContents("constvar", m_vecConstVar);
		printContents("stepvar", m_vecStepVar);

		/*数式出力*/
		super.printExpressions();
		
		//キーを取得する
        Set<Math_ci> keys = m_mapDimensionalDependency.keySet();

        // mapの中身をすべて表示する
        for(Math_ci key : keys) {
                System.out.println(key + 
                          " : " + m_mapDimensionalDependency.get(key).toLegalString());
        }
		/*改行*/
		System.out.println();
	}

	/**
	 * 解析内容を標準出力する.
	 * @param strTag 変数リスト名
	 * @param vecM 変数リスト
	 * @throws MathException
	 */
	private void printContents(String strTag, Vector<Math_ci> vecM)
	throws MathException {
		/*変数リスト出力*/
		System.out.print(strTag + "\t= { ");

		boolean first = true;
		for (Math_ci it: vecM) {
			if (first) {
				first = false;
			} else {
				System.out.print(" , ");
			}
			System.out.print(it.toLegalString());
		}

		System.out.println("}");
	}

	/**
	 * recur変数か判定する.
	 * @param pVariable 判定する数式
	 * @return 一致判定
	 */
	public boolean isRecurVar(MathOperand pVariable) {
		/*すべての要素を比較*/
		for (Math_ci it: m_vecRecurVar) {

			/*一致判定*/
			if (it.matches(pVariable)) {
				return true;
			}
		}

		/*不一致*/
		return false;
	}

	/**
	 * 中間変数か判定する.
	 * @param pVariable 判定する数式
	 * @return 一致判定
	 */
	public boolean isArithVar(MathOperand pVariable) {
		/*すべての要素を比較*/
		for (Math_ci it: m_vecArithVar) {

			/*一致判定*/
			if (it.matches(pVariable)) {
				return true;
			}
		}

		/*不一致*/
		return false;
	}

	/**
	 * 定数変数か判定する.
	 * @param pVariable 判定する数式
	 * @return 一致判定
	 */
	public boolean isConstVar(MathOperand pVariable) {
		/*すべての要素を比較*/
		for (Math_ci it: m_vecConstVar) {

			/*一致判定*/
			if (it.matches(pVariable)) {
				return true;
			}
		}

		/*不一致*/
		return false;
	}
	
	/**
	 * ステップ変数か判定する
	 */
	public boolean isStepVar(MathOperand pVariable){
		/*全ての要素を比較*/
		for( Math_ci it: m_vecStepVar){
			
			/*一致判定*/
			if(it.matches(pVariable)){
				return true;
			}
		}
		
		/*不一致*/
		return false;
	}

}
