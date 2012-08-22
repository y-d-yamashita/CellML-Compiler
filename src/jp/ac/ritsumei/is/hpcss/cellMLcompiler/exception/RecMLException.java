package jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception;

/**
 * RelML例外クラス
 */
@SuppressWarnings("serial")
public class RecMLException extends Exception {

	/*-----コンストラクタ-----*/
	public RecMLException(String strClassName, String strFunctionName, String strMessage) {
		super(strClassName, strFunctionName, strMessage);
	}

	/*-----メッセージ取得-----*/
	public String getMessage() {
		return "RecMLException In [" + m_strClassName + "." + m_strFunctionName + "] " + m_strMessage;
	}

}
