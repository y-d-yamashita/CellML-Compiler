package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.Visitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;

/**
 * MathML演算要素クラス.
 */
public abstract class MathFactor {

	/**表示用文字列*/
	protected String m_strPresentText;

	/**要素分類*/
	protected eMathMLClassification m_classification;

	/**
	 * MathML演算要素インスタンスを作成する.
	 * @param strPresentText 表示用文字列
	 * @param classification 要素分類
	 */
	public MathFactor(String strPresentText,eMathMLClassification classification) {
		m_strPresentText = strPresentText;
		m_classification = classification;
	}

	/**
	 * 演算結果を取得する.
	 * @return 演算結果
	 * @throws MathException
	 */
	public abstract double getValue() throws MathException;

	/**
	 * 演算結果を格納する.
	 * @param dValue 演算結果
	 * @throws MathException
	 */
	public abstract void setValue(double dValue) throws MathException;

	/**
	 * 数式を複製する.
	 * @return 複製した数式
	 * @throws MathException
	 */
	public abstract MathFactor createCopy() throws MathException;

	/**
	 * 文字列に変換する.
	 * @return 変換した文字列
	 * @throws MathException
	 */
	public abstract String toLegalString() throws MathException;

	/**
	 * 文字列に変換する.
	 * @return 変換した文字列
	 * @throws MathException
	 */
	public String toStringInCondition() throws MathException {
		return toLegalString();
	}
	/**
	 * Convert MathFactor to MathML string.
	 * @return String MathML
	 * @throws MathException
	 */
	public abstract String toMathMLString() throws MathException;
	/**
	 * オブジェクトを比較する.
	 * @param pFactor 比較オブジェクト
	 * @return 同一判定
	 */
	public boolean matches(MathFactor pFactor) {
		return m_classification == pFactor.m_classification;
	}
	/**
	 * オブジェクトを比較する.
	 * @param classification 要素分類
	 * @return 同一判定
	 */
	public boolean matches(eMathMLClassification classification) {
		return m_classification == classification;
	}

	/**
	 * 数式を比較する.
	 * @param pFactor 比較対照ファクタ
	 * @return 同一判定
	 */
	public abstract boolean matchesExpression(MathFactor pFactor);
	/**
	 * ツリーの横断
	 * @param v
	 */
	public abstract void traverse(Visitor v);

	public String getM_strPresentText() {
		return m_strPresentText;
	}

	public void setM_strPresentText(String m_strPresentText) {
		this.m_strPresentText = m_strPresentText;
	}
	public void replaceStrPresentExt(String regex,String replacement){
		m_strPresentText=m_strPresentText.replaceAll(regex, replacement);
	}
}
