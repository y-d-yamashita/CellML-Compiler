package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;

/**
 * 式構文クラス.
 */
public class SyntaxExpression extends SyntaxStatement {

	/**数式*/
	protected MathExpression m_pMathExpression;

	/**
	 * 式構文インスタンスを作成する.
	 * @param pMathExp 数式
	 */
	public SyntaxExpression(MathExpression pMathExp) {
		super(eSyntaxClassification.SYN_EXPRESSION);
		m_pMathExpression = null;
		setExpression(pMathExp);
	}

	/**
	 * 数式を設定する.
	 * @param pMathExp 数式
	 */
	public void setExpression(MathExpression pMathExp) {
		/*数式を与える*/
		m_pMathExpression = pMathExp;
	}

	/**
	 * 数式を取得する.
	 * @return 数式
	 */
	public MathExpression getExpression() {
		/*内部の数式を返す*/
		return m_pMathExpression;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.Syntax#toLegalString()
	 */
	public String toLegalString() throws MathException {
		return m_pMathExpression.toLegalString() + ";";
	}

}
