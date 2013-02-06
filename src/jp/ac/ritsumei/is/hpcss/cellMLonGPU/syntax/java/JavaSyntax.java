package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;

/**
 * Java　構文基底クラス
 * 
 * @author n-washio
 */
public abstract class JavaSyntax {

	//========================================================
	//ENUM
	//========================================================

	/**
	 * 構文分類列挙
	 */
	public enum eSyntaxClassification {
		SYN_JAVA_DECLARATION,
		SYN_JAVA_EXPRESSION,
		SYN_JAVA_PREPROCESSOR,
		SYN_JAVA_CONTROL,
		SYN_JAVA_FUNCTION,
		SYN_JAVA_PROGRAM,
		SYN_JAVA_CONDITION,
		SYN_JAVA_CALLFUNCTION,
		SYN_JAVA_STATEMENTLIST,
	};

	/**構文分類*/
	protected eSyntaxClassification m_classification;

	/**現在のインデント*/
	protected static int m_ushIndent = 0;

	/**
	 * 構文インスタンスを作成する.
	 * @param classification 構文分類
	 */
	public JavaSyntax(eSyntaxClassification classification) {
		m_classification = classification;
	}

	/**
	 * 文字列に変換する.
	 * @return 変換した文字列
	 * @throws MathException
	 * @throws SyntaxException
	 */
	abstract public String toLegalJavaString()
	throws MathException, SyntaxException;

	/**
	 * インデントを初期化する.
	 */
	protected void initIndent() {
		m_ushIndent = 0;
	}

	/**
	 * インデントをインクリメントする.
	 */
	protected void incIndent() {
		m_ushIndent++;
	}

	/**
	 * インデントをデクリメントする.
	 */
	protected void decIndent() {
		if (m_ushIndent > 0) {
			m_ushIndent--;
		}
	}

	/**
	 * インデント文字列を取得する.
	 * @return インデント文字列
	 */
	protected String getIndentString() {
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		/*インデント追加*/
		for (int i = 0; i < m_ushIndent; i++) {
			strPresentText.append("\t");
		}

		return strPresentText.toString();
	}

}
