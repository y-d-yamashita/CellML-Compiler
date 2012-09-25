package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.visitor.Visitor;

/**
 * MathML被演算子クラス.
 */
public abstract class MathOperand extends MathFactor {

	/**要素の値変数*/
	protected double m_dValue;

	/**初期化判定*/
	protected boolean m_bInitFlag;

	/**被演算子種類*/
	protected eMathOperand m_operandKind;

	/**
	 * MathML被演算子インスタンスを作成する.
	 * @param strPresentText 表示用文字列
	 * @param dValue 要素の値変数
	 * @param operandKind 被演算子種類
	 */
	public MathOperand(String strPresentText,double dValue, eMathOperand operandKind) {
		super(strPresentText, eMathMLClassification.MML_OPERAND);
		m_dValue = dValue;
		m_bInitFlag = true;
		m_operandKind = operandKind;
	}

	/**
	 * MathML被演算子インスタンスを作成する.
	 * @param strPresentText 表示用文字列
	 * @param operandKind 被演算子種類
	 */
	public MathOperand(String strPresentText, eMathOperand operandKind) {
		super(strPresentText, eMathMLClassification.MML_OPERAND);
		m_dValue = 0.0;
		m_bInitFlag = false;
		m_operandKind = operandKind;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#getValue()
	 */
	public double getValue() throws MathException {
		/*未初期化の場合は例外処理*/
		if(!m_bInitFlag){
			throw new MathException("MathOperand","getValue",
					"uninitialized operand referenced");
		}

		return m_dValue;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#createCopy()
	 */
	public MathFactor createCopy() throws MathException{
		return MathFactory.createOperand(m_operandKind, m_strPresentText, m_dValue);
	}

	/**
	 * オブジェクトを比較する.
	 * @param pOperand 比較するオブジェクト
	 * @return 同一判定
	 */
	public boolean matches(MathOperand pOperand){
		return m_strPresentText.equals(pOperand.m_strPresentText);
	}

	/**
	 * オブジェクトを比較する.
	 * @param operandKind 被演算子種類
	 * @return 同一判定
	 */
	public boolean matches(eMathOperand operandKind){
		return m_operandKind == operandKind;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor#matchesExpression(jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor)
	 */
	public boolean matchesExpression(MathFactor pFactor){

		/*オペランドの場合*/
		if(pFactor.matches(eMathMLClassification.MML_OPERAND)){
			return matches((MathOperand)pFactor);
		}
		/*その他の要素とは比較しない*/
		else{
			return false;
		}
	}
	/**
	 * ツリーの横断
	 * @param v
	 */
	public void traverse(Visitor v){
		v.visit(this);
	}

}
