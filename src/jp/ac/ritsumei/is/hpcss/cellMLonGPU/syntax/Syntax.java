package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;

/**
 * 構文基底クラス.
 */
public abstract class Syntax {

	//========================================================
	//ENUM
	//========================================================

	/**
	 * 構文分類列挙
	 */
	public enum eSyntaxClassification {
		SYN_DECLARATION,
		SYN_EXPRESSION,
		SYN_PREPROCESSOR,
		SYN_CONTROL,
		SYN_FUNCTION,
		SYN_PROGRAM,
		SYN_CONDITION,
		SYN_CALLFUNCTION,
		SYN_STATEMENTLIST,
	};

	/**構文分類*/
	protected eSyntaxClassification m_classification;

	/**現在のインデント*/
	protected static int m_ushIndent = 0;

	/**
	 * 構文インスタンスを作成する.
	 * @param classification 構文分類
	 */
	public Syntax(eSyntaxClassification classification) {
		m_classification = classification;
	}

	/**
	 * 文字列に変換する.
	 * @return 変換した文字列
	 * @throws MathException
	 * @throws SyntaxException
	 */
	abstract public String toLegalString()
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
