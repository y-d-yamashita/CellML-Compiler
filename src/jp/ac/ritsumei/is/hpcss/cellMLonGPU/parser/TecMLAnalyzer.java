package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.HashMap;
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
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLFuncType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLVarType;

/**
 * TecML解析クラス.
 */
public class TecMLAnalyzer extends MathMLAnalyzer {

	/**数式解析中判定*/
	private boolean m_bMathParsing;

	/**式中の入力変数*/
	Math_ci m_pInputVar;
	/**
	 * 式中の入力変数を取得する.
	 * @return 式中の入力変数
	 */
	public Math_ci getM_pInputVar() {
		return m_pInputVar;
	}

	/**式中の出力変数*/
	Math_ci m_pOutputVar;
	/**
	 * 式中の出力変数を取得する.
	 * @return 式中の出力変数
	 */
	public Math_ci getM_pOutputVar() {
		return m_pOutputVar;
	}

	/**式中のdelta変数*/
	Math_ci m_pDeltaVar;
	/**
	 * 式中のdelta変数を取得する.
	 * @return 式中のdelta変数
	 */
	public Math_ci getM_pDeltaVar() {
		return m_pDeltaVar;
	}

	/**式中の時間変数*/
	Math_ci m_pTimeVar;
	/**
	 * 式中の時間変数を取得する.
	 * @return 式中の時間変数
	 */
	public Math_ci getM_pTimeVar() {
		return m_pTimeVar;
	}

	/**式中の微係数変数*/
	Vector<Math_ci> m_vecDerivativeVar;
	/**
	 * 式中の微係数変数を取得する.
	 * @return 式中の微係数変数
	 */
	public Vector<Math_ci> getM_vecDerivativeVar() {
		return m_vecDerivativeVar;
	}

	/**式中の微分変数*/
	Vector<Math_ci> m_vecDiffVar;
	/**
	 * 式中の微分変数を取得する.
	 * @return 式中の微分変数
	 */
	public Vector<Math_ci> getM_vecDiffVar() {
		return m_vecDiffVar;
	}

	/**式中の通常変数*/
	Vector<Math_ci> m_vecArithVar;
	/**
	 * 式中の通常変数を取得する.
	 * @return 式中の通常変数
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

	/**微分方程式関数変数*/
	Math_ci m_pDiffFuncVar;
	/**
	 * 微分方程式関数変数を取得する.
	 * @return 微分方程式関数変数
	 */
	public Math_ci getM_pDiffFuncVar() {
		return m_pDiffFuncVar;
	}

	/**非微分方程式関数変数*/
	Math_ci m_pNonDiffFuncVar;
	/**
	 * 非微分方程式関数変数を取得する.
	 * @return 非微分方程式関数変数
	 */
	public Math_ci getM_pNonDiffFuncVar() {
		return m_pNonDiffFuncVar;
	}

	/**関数プロトタイプ引数ベクタ*/
	Vector<eTecMLVarType> m_vecDiffFuncArgType;
	Vector<eTecMLVarType> m_vecNonDiffFuncArgType;

	/**関数定義解析中判定*/
	boolean m_bFunctionAnalyzing;
	eTecMLFuncType m_AnalyzingFuncType;

	/**
	 * TecML解析インスタンスを作成する.
	 */
	public TecMLAnalyzer() {
		m_bMathParsing = false;
		m_pInputVar = null;
		m_pOutputVar = null;
		m_pDeltaVar = null;
		m_pTimeVar = null;
		m_pDiffFuncVar = null;
		m_pNonDiffFuncVar = null;
		m_bFunctionAnalyzing = false;

		m_vecDerivativeVar = new Vector<Math_ci>();
		m_vecDiffVar = new Vector<Math_ci>();
		m_vecArithVar = new Vector<Math_ci>();
		m_vecConstVar = new Vector<Math_ci>();
		m_vecDiffFuncArgType = new Vector<eTecMLVarType>();
		m_vecNonDiffFuncArgType = new Vector<eTecMLVarType>();
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
		}

		/*TecML解析*/
		else {

			/*タグ特定*/
			eTecMLTag tagId = TecMLDefinition.getTecMLTagId(strTag);

			/*タグ種別ごとの処理*/
			switch (tagId) {

				//--------------------------------------入力変数宣言
			case TTAG_INPUTVAR:

				//--------------------------------------出力変数宣言
			case TTAG_OUTPUTVAR:

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

					/*input変数の登録*/
					if (tagId == eTecMLTag.TTAG_INPUTVAR) {
						m_pInputVar = pVariable;
					}
					/*output変数の登録*/
					else if (tagId == eTecMLTag.TTAG_OUTPUTVAR) {
						m_pOutputVar = pVariable;
					}

					/*タイプごとに変数追加*/
					switch (varType) {

						//----------------------------------delta変数型
					case TVAR_TYPE_DELTATIMEVAR:
						m_pDeltaVar = pVariable;
						break;

						//----------------------------------時間変数型
					case TVAR_TYPE_TIMEVAR:
						m_pTimeVar = pVariable;
						break;

						//----------------------------------微分変数型
					case TVAR_TYPE_DIFFVAR:
						m_vecDiffVar.add(pVariable);
						break;

						//----------------------------------微係数変数型
					case TVAR_TYPE_DERIVATIVEVAR:
						m_vecDerivativeVar.add(pVariable);
						break;

						//----------------------------------通常変数型
					case TVAR_TYPE_ARITHVAR:
						m_vecArithVar.add(pVariable);
						break;

						//----------------------------------定数型
					case TVAR_TYPE_CONSTVAR:
						m_vecConstVar.add(pVariable);
						break;
					}

					break;
				}

				//--------------------------------------関数プロトタイプ宣言
			case TTAG_FUNCTION:
				{
					m_bFunctionAnalyzing = true;

					/*変数名とタイプ取得*/
					String strName = pXMLAttr.getValue("name");
					String strType = pXMLAttr.getValue("type");
					eTecMLFuncType varType = TecMLDefinition.getTecMLFuncType(strType);

					/*変数名から変数インスタンス生成*/
					Math_ci pVariable =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strName);

					/*関数変数の追加*/
					switch (varType) {

						//---------------------------------非微分方程式関数
					case TFUNC_TYPE_NONDIFF:
						m_pNonDiffFuncVar = pVariable;
						m_AnalyzingFuncType = eTecMLFuncType.TFUNC_TYPE_NONDIFF;
						break;

						//---------------------------------微分方程式関数
					case TFUNC_TYPE_DIFF:
						m_pDiffFuncVar = pVariable;
						m_AnalyzingFuncType = eTecMLFuncType.TFUNC_TYPE_DIFF;
						break;
					}

					break;
				}

				//--------------------------------------プロトタイプ引数宣言
			case TTAG_ARGUMENT:
				{
					/*引数タイプ取得*/
					String strType = pXMLAttr.getValue("type");
					eTecMLVarType varType = TecMLDefinition.getTecMLVarType(strType);

					switch (m_AnalyzingFuncType) {

						//---------------------------------非微分方程式関数
					case TFUNC_TYPE_NONDIFF:
						m_vecNonDiffFuncArgType.add(varType);
						break;

						//---------------------------------微分方程式関数
					case TFUNC_TYPE_DIFF:
						m_vecDiffFuncArgType.add(varType);
						break;
					}
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
		else if (m_bFunctionAnalyzing) {

			/*関数解析終了*/
			if (strTag == TecMLDefinition.TECML_TAG_STR_FUNCTION) {
				m_bFunctionAnalyzing = false;
			}
		}

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

		printContents("diffvar", m_vecDerivativeVar);
		printContents("timevar", m_vecDiffVar);
		printContents("var", m_vecArithVar);
		printContents("constvar", m_vecConstVar);

		/*数式出力*/
		super.printExpressions();

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
	 * 微係数変数か判定する.
	 * @param pVariable 判定する数式
	 * @return 一致判定
	 */
	public boolean isDerivativeVar(MathOperand pVariable) {
		/*すべての要素を比較*/
		for (Math_ci it: m_vecDerivativeVar) {

			/*一致判定*/
			if (it.matches(pVariable)) {
				return true;
			}
		}

		/*不一致*/
		return false;
	}

	/**
	 * 微分変数か判定する.
	 * @param pVariable 判定する数式
	 * @return 一致判定
	 */
	public boolean isDiffVar(MathOperand pVariable) {
		/*すべての要素を比較*/
		for (Math_ci it: m_vecDiffVar) {

			/*一致判定*/
			if (it.matches(pVariable)) {
				return true;
			}
		}

		/*不一致*/
		return false;
	}

	/**
	 * 通常変数か判定する.
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
	 * 関数引数タイプインデックスを取得する.
	 * 各タイプの引数がプロトタイプ宣言で何番目に宣言されているかを調べる
	 * @return 関数引数型ごとのidとインデックス
	 */
	public HashMap<eTecMLVarType, Integer> getDiffFuncArgTypeIdx() {
		HashMap<eTecMLVarType, Integer> ati = new HashMap<eTecMLVarType, Integer>();
		for (int id = 0; id < m_vecDiffFuncArgType.size(); id++) {
			switch (m_vecDiffFuncArgType.get(id)) {
				//----------------------------------時間変数型
			case TVAR_TYPE_TIMEVAR:

				//----------------------------------微分変化変数型
			case TVAR_TYPE_DIFFVAR:

				//----------------------------------通常変数型
			case TVAR_TYPE_ARITHVAR:

				//----------------------------------定数型
			case TVAR_TYPE_CONSTVAR:

				ati.put(m_vecDiffFuncArgType.get(id), id);
				break;
			}
		}
		return ati;
	}

	/**
	 * 関数引数タイプインデックスを取得する.
	 * 各タイプの引数がプロトタイプ宣言で何番目に宣言されているかを調べる
	 * @return 関数引数型ごとのidとインデックス
	 */
	public HashMap<eTecMLVarType, Integer> getNonDiffFuncArgTypeIdx() {
		HashMap<eTecMLVarType, Integer> ati = new HashMap<eTecMLVarType, Integer>();
		for (int id = 0; id < m_vecNonDiffFuncArgType.size(); id++) {
			switch (m_vecNonDiffFuncArgType.get(id)) {
//			switch (m_vecDiffFuncArgType.get(id)) {

				//----------------------------------delta変数型
			case TVAR_TYPE_TIMEVAR:

				//----------------------------------微分変数型
			case TVAR_TYPE_DIFFVAR:

				//----------------------------------通常変数型
			case TVAR_TYPE_ARITHVAR:

				//----------------------------------定数型
			case TVAR_TYPE_CONSTVAR:

				ati.put(m_vecNonDiffFuncArgType.get(id), id);
				break;
			}
		}
		return ati;
	}

}
