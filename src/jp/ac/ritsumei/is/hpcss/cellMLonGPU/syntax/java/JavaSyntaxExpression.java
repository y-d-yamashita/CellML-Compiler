package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;

/**
 * Java　式構文クラス
 * 
 * @author n-washio
 */
public class JavaSyntaxExpression extends JavaSyntaxStatement {

	/**数式*/
	protected MathExpression m_pMathExpression;

	/**
	 * 式構文インスタンスを作成する.
	 * @param pMathExp 数式
	 */
	public JavaSyntaxExpression(MathExpression pMathExp) {
		super(eSyntaxClassification.SYN_JAVA_EXPRESSION);
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
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.Syntax#toLegalJavaString()
	 */
	public String toLegalJavaString() throws MathException {
		return m_pMathExpression.toLegalJavaString() + ";";
	}

}
