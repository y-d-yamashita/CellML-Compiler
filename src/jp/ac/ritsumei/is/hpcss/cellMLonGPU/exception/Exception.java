package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * 例外基底クラス.
 */
@SuppressWarnings("serial")
public class Exception extends java.lang.Exception {

	/*例外情報文字列*/
	protected String m_strClassName;
	protected String m_strFunctionName;
	protected String m_strMessage;

	/**
	 * インスタンスを作成する.
	 * @param strClassName クラス名
	 * @param strFunctionName 関数名
	 * @param strMessage 例外メッセージ
	 */
	public Exception(String strClassName,
			String strFunctionName, String strMessage) {
		m_strClassName = strClassName;
		m_strFunctionName = strFunctionName;
		m_strMessage = strMessage;
	}

	/*-----メッセージ取得-----*/
	/* (非 Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return "Exception In [" + m_strClassName + "." +
		m_strFunctionName + "] " + m_strMessage;
	}

}
