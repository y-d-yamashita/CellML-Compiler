package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.JavaBigDecimalProgramGeneratorSingleton;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;

/**
 * Java　条件文構文クラス
 * 
 * @author n-washio
 */
public class JavaSyntaxCondition extends JavaSyntax {

	/*forループ用初期化式 for( m_pInitExpression ; m_pCondExpression ; m_pReInitExpression) */
	/**条件式*/
	protected MathExpression m_pCondExpression;
	/**初期化式*/
	protected MathExpression m_pInitExpression;
	/**再初期化式*/
	protected MathExpression m_pReInitExpression;

	/**
	 * SyntaxCondition for creating a blank loop.
	 * @param null
	 */
	public JavaSyntaxCondition() {
		super(eSyntaxClassification.SYN_JAVA_CONDITION);
		m_pCondExpression = null;
		m_pInitExpression = null;
		m_pReInitExpression = null;
	}
	
	/**
	 * 条件文構文インスタンスを作成する.
	 * @param pCondExpression 条件式
	 */
	public JavaSyntaxCondition(MathExpression pCondExpression) {
		super(eSyntaxClassification.SYN_JAVA_CONDITION);
		m_pCondExpression = pCondExpression;
		m_pInitExpression = null;
		m_pReInitExpression = null;
	}

	/**
	 * forループ用初期化式を設定する.
	 * @param pInitExpression 初期化式
	 * @param pReInitExpression 再初期化式
	 */
	public void setInitExpression(MathExpression pInitExpression,
			MathExpression pReInitExpression) {
		/*式を設定*/
		m_pInitExpression = pInitExpression;
		m_pReInitExpression = pReInitExpression;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.Syntax#toLegalJavaString()
	 */
	public String toLegalJavaString() throws MathException {
		/*条件文のみ返却*/
		return m_pCondExpression.toLegalJavaString();
	}

	/**
	 * for文用文字列型に変換する.
	 * @return for文用文字列
	 * @throws MathException
	 */
	public String toStringFor() throws MathException {
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");
		JavaBigDecimalProgramGeneratorSingleton pJavaBDInstance =
			JavaBigDecimalProgramGeneratorSingleton.GetInstance();

		if (pJavaBDInstance.getMode()) {
			pJavaBDInstance.addForce();
		}

		/*初期化式追加*/
		if (m_pInitExpression != null) {
			strPresentText.append(m_pInitExpression.toLegalJavaString());
		}
		if (pJavaBDInstance.getMode()) {
			pJavaBDInstance.addForce();
		}

		strPresentText.append(";");

		/*条件式追加*/
		if (m_pCondExpression != null) {
			strPresentText.append(m_pCondExpression.toLegalJavaString());
		}

		strPresentText.append(";");

		/*再初期化式追加*/
		if (m_pReInitExpression != null) {
			strPresentText.append(m_pReInitExpression.toLegalJavaString());
		}
		if (pJavaBDInstance.getMode()) {
			pJavaBDInstance.addForce();
		}

		if (pJavaBDInstance.getForce() == 2) {
			pJavaBDInstance.revMode();
		}

		return strPresentText.toString();
	}

}
