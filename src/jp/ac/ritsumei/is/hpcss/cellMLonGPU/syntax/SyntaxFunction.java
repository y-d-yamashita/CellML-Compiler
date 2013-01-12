package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration.eDeclarationSpecifier;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * 関数構文クラス.
 */
public class SyntaxFunction extends Syntax {

	/**関数名*/
	protected String m_strFuncName;

	/**関数宣言時修飾子*/
	protected eDeclarationSpecifier m_decSpecifier;

	/**引数*/
	protected Vector<SyntaxDeclaration> m_vecFuncParam;

	/**戻り値型*/
	protected SyntaxDataType m_pFuncType;

	/**内部の構文*/
	protected Vector<SyntaxDeclaration> m_vecSynDeclaration;
	protected Vector<SyntaxStatement> m_vecSynStatement;
	protected String m_str; //追加

	/**
	 * 関数構文インスタンスを作成する.
	 * @param strFuncName 関数名
	 * @param pFuncType 戻り値型
	 */
	public SyntaxFunction(String strFuncName, SyntaxDataType pFuncType) {
		super(eSyntaxClassification.SYN_FUNCTION);
		m_strFuncName = strFuncName;
		m_decSpecifier = eDeclarationSpecifier.DS_NONE;
		m_pFuncType = pFuncType;
		m_vecFuncParam = new Vector<SyntaxDeclaration>();
		m_vecSynDeclaration = new Vector<SyntaxDeclaration>();
		m_vecSynStatement = new Vector<SyntaxStatement>();
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.Syntax#toLegalString()
	 */
	public String toLegalString()
	throws SyntaxException, MathException {
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		/*宣言指定子追加*/
		switch (m_decSpecifier) {

		case DS_STATIC:
			strPresentText.append(SyntaxDeclaration.DS_STR_STATIC + " ");
			break;

		case DS_CONST:
			strPresentText.append(SyntaxDeclaration.DS_STR_CONST + " ");
			break;

		case DS_CUDA_HOST:
			strPresentText.append(SyntaxDeclaration.DS_STR_HOST + " ");
			break;

		case DS_CUDA_DEVICE:
			strPresentText.append(SyntaxDeclaration.DS_STR_DEVICE + " ");
			break;

		case DS_CUDA_GLOBAL:
			strPresentText.append(SyntaxDeclaration.DS_STR_GLOBAL + " ");
			break;

		case DS_CUDA_CONSTANT:
			strPresentText.append(SyntaxDeclaration.DS_STR_CONSTANT + " ");
			break;
		}

		/*戻り値型文字列追加*/
		if (m_pFuncType != null) {
			strPresentText.append(m_pFuncType.toLegalString() +
					m_pFuncType.toStringSuffix() + " ");
		}
		else {
			throw new SyntaxException("SyntaxFunction","toLegalString",
						  "lack of data type");
		}

		/*関数名追加*/
		strPresentText.append(m_strFuncName + " ( ");

		//------------------------------------------
		//引数宣言追加
		//------------------------------------------

		{
			/*引数なし(void)*/
			if (m_vecFuncParam.size() == 0) {
				strPresentText.append("void");
			}
			/*引数あり*/
			else {
				/*順次追加*/
				boolean first = true;
				for (SyntaxDeclaration it: m_vecFuncParam) {

					/*はじめの引数以外の前に,を追加*/
					if (first) {
						first = false;
					} else {
						strPresentText.append(" , ");
					}

					strPresentText.append(it.toStringParam() + "");
				}
			}
		}

		/*関数内部構文開始*/
		strPresentText.append(" ) {" + StringUtil.lineSep + StringUtil.lineSep);

		/*インデントインクリメント*/
		incIndent();
		//------------------------------------------
		//宣言構文追加
		//------------------------------------------
		{
			/*順次追加*/
			for (SyntaxDeclaration it: m_vecSynDeclaration) {
				strPresentText.append(getIndentString() + it.toLegalString() + StringUtil.lineSep);
			}
		}

		/*改行*/
		strPresentText.append(StringUtil.lineSep);

		//------------------------------------------
		//statement構文追加
		//------------------------------------------
		{
			/*順次追加*/
			for (SyntaxStatement it: m_vecSynStatement) {
				strPresentText.append(getIndentString() + it.toLegalString() + StringUtil.lineSep);
			}
		}
		
		//テンプレート構文追加 @n-washio
		strPresentText.append(this.m_str);
		
		/*インデントデクリメント*/
		decIndent();

		/*関数内部構文終了*/
		strPresentText.append(getIndentString() + "}" + StringUtil.lineSep + StringUtil.lineSep);

		return strPresentText.toString();
	}

	/**
	 * 関数プロトタイプ用文字列に変換する.
	 * @return 関数プロトタイプ用文字列
	 * @throws MathException
	 * @throws SyntaxException
	 */
	public String toStringPrototype()
	throws MathException, SyntaxException {
		/*結果文字列*/
		StringBuilder strPresentText = new StringBuilder("");

		/*宣言指定子追加*/
		switch (m_decSpecifier) {

		case DS_STATIC:
			strPresentText.append(SyntaxDeclaration.DS_STR_STATIC + " ");
			break;

		case DS_CUDA_HOST:
			strPresentText.append(SyntaxDeclaration.DS_STR_HOST + " ");
			break;

		case DS_CUDA_DEVICE:
			strPresentText.append(SyntaxDeclaration.DS_STR_DEVICE + " ");
			break;

		case DS_CUDA_GLOBAL:
			strPresentText.append(SyntaxDeclaration.DS_STR_GLOBAL + " ");
			break;
		}

		/*戻り値型文字列追加*/
		if (m_pFuncType != null) {
			strPresentText.append(m_pFuncType.toLegalString()
					+ m_pFuncType.toStringSuffix() + " ");
		}
		else {
			throw new SyntaxException("SyntaxFunction","toLegalString",
						  "lack of data type");
		}

		/*関数名追加*/
		strPresentText.append(m_strFuncName + " ( ");

		//------------------------------------------
		//引数宣言追加
		//------------------------------------------
		{
			/*引数なし(void)*/
			if (m_vecFuncParam.size() == 0) {
				strPresentText.append("void");
			}
			/*引数あり*/
			else {
				/*順次追加*/
				boolean first = true;
				for (SyntaxDeclaration it: m_vecFuncParam) {

					/*はじめの引数以外の前に,を追加*/
					if (first) {
						first = false;
					} else {
						strPresentText.append(" , ");
					}

					strPresentText.append(it.toStringParam());
				}
			}
		}

		/*関数内部構文開始*/
		strPresentText.append(" ) ");

		return strPresentText.toString();
	}

	/**
	 * 関数引数を追加する.
	 * @param pDeclaration 追加する構文インスタンス
	 */
	public void addParam(SyntaxDeclaration pDeclaration) {
		//------------------------------------------
		//重複チェック
		//------------------------------------------
		/*ローカル変数宣言と照合*/
		for (SyntaxDeclaration it: m_vecSynDeclaration) {

			/*照合*/
			if (it.matches(pDeclaration)) {

				/*重複であれば追加しない*/
				return;
			}
		}

		/*引数宣言と照合*/
		for (SyntaxDeclaration it: m_vecFuncParam) {

			/*照合*/
			if (it.matches(pDeclaration)) {

				/*重複であれば追加しない*/
				return;
			}
		}

		//------------------------------------------
		//ベクタに追加
		//------------------------------------------
		/*引数宣言の追加*/
		m_vecFuncParam.add(pDeclaration);
	}

	/**
	 * 宣言構文を追加する.
	 * @param pDeclaration 追加する構文インスタンス
	 */
	public void addDeclaration(SyntaxDeclaration pDeclaration) {
		//------------------------------------------
		//重複チェック
		//------------------------------------------
		/*ローカル変数宣言と照合*/
		for (SyntaxDeclaration it: m_vecSynDeclaration) {

			/*照合*/
			if (it.matches(pDeclaration)) {

				/*重複であれば追加しない*/
				return;
			}
		}

		/*引数宣言と照合*/
		for (SyntaxDeclaration it: m_vecFuncParam) {

			/*照合*/
			if (it.matches(pDeclaration)) {

				/*重複であれば追加しない*/
				return;
			}
		}

		//------------------------------------------
		//ベクタに追加
		//------------------------------------------
		/*要素追加*/
		m_vecSynDeclaration.add(pDeclaration);
	}

	/**
	 * statement構文を追加する.
	 * @param pStatement 追加する構文インスタンス
	 */
	public void addStatement(SyntaxStatement pStatement) {
		/*要素追加*/
		m_vecSynStatement.add(pStatement);
	}
	
	/**
	 * string構文を追加する.
	 * @param pStatement 追加する構文インスタンス
	 */
	public void addString(String str) {
		/*要素追加*/
		m_str = str;
	}


	/**
	 * 宣言指定子を追加する.
	 * @param decSpecifier 宣言指定子列挙型
	 */
	public void addDeclarationSpecifier(eDeclarationSpecifier decSpecifier) {
		/*宣言指定子追加*/
		m_decSpecifier = decSpecifier;
	}

}
