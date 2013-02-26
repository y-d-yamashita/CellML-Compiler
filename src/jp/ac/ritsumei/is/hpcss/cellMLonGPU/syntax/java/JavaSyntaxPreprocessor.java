package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;

/**
 * Java プリプロセッサ構文クラス
 * 
 * @author n-washio
 */
public class JavaSyntaxPreprocessor extends JavaSyntax {

	//========================================================
	//ENUM
	//========================================================

	/**
	 * プリプロセッサ分類列挙
	 */
	public enum ePreprocessorKind {
		/**include<> 絶対パス*/
		PP_INCLUDE_ABS,
		/**include"" 相対パス*/
		PP_INCLUDE_REL,
		PP_DEFINE,
		JAVA_IMPORT,
		JAVA_CLASS,
		JAVA_CLASS_END,
	};

	/**プリプロセッサ種類*/
	protected ePreprocessorKind m_preprocessorKind;

	/**内容文字列*/
	protected String m_strContent;

	/**
	 * プリプロセッサ構文インスタンスを作成する.
	 * @param strContent 内容文字列
	 */
	public JavaSyntaxPreprocessor(String strContent) {
		super(eSyntaxClassification.SYN_JAVA_PREPROCESSOR);
		m_preprocessorKind = ePreprocessorKind.JAVA_IMPORT;
		m_strContent = strContent;
	}

	/**
	 * プリプロセッサ構文インスタンスを作成する.
	 * @param preprocessorKind プリプロセッサ種類
	 * @param strContent 内容文字列
	 */
	public JavaSyntaxPreprocessor(ePreprocessorKind preprocessorKind, String strContent) {
		super(eSyntaxClassification.SYN_JAVA_PREPROCESSOR);
		m_preprocessorKind = preprocessorKind;
		m_strContent = strContent;
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.Syntax#toLegalJavaString()
	 */
	public String toLegalJavaString()
	throws SyntaxException {
		/*プリプロセッサの種類ごとの処理*/
		switch (m_preprocessorKind) {

			/*include<絶対パス>*/
		case PP_INCLUDE_ABS:
			return "#include<" + m_strContent + ">";

			/*include"相対パス"*/
		case PP_INCLUDE_REL:
			return "#include\"" + m_strContent + "\"";

			/*define*/
		case PP_DEFINE:
			return "#define " + m_strContent;
			
			/*import*/
		case JAVA_IMPORT:
			return "import " + m_strContent;
			
			/*class*/
		case JAVA_CLASS:
			return "public class " + m_strContent + "{";

			/*予期しない種類*/
		default:
			throw new SyntaxException("SyntaxPreprocessor","toLegalJavaString",
						  "not declared preprocessor kind used");
		}
	}

	/**
	 * Javaプログラムの文字列に変換する.
	 * import文の追加
	 * @return 変換した文字列
	 */
	String toJavaString() {
		/*import文の追加*/
		return "import " + m_strContent + ";";
	}

	/**
	 * インスタンスを照合する.
	 * @param pPreprocessor 比較対象インスタンス
	 * @return 一致判定
	 */
	public boolean matches(JavaSyntaxPreprocessor pPreprocessor) {
		/*プリプロセッサの種類と内容の一致を確認*/
		if (m_preprocessorKind == pPreprocessor.m_preprocessorKind &&
		    m_strContent == pPreprocessor.m_strContent ) {
			return true;
		}

		return false;
	}

}
