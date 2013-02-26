package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.c;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * Statement のリスト（ブロックに相当）
 */
public class SyntaxStatementList extends SyntaxStatement {

	/*内部の構文*/
	protected Vector<SyntaxStatement> m_vecSynStatement;
	int i;

	/*-----コンストラクタ-----*/
	public SyntaxStatementList() {
		super(eSyntaxClassification.SYN_STATEMENTLIST);
		m_vecSynStatement = new Vector<SyntaxStatement>();
	}

	//===================================================
	//toLegalString
	//	文字列型変換メソッド
	//
	//@return
	//	文字列型表現	: string
	//
	//===================================================
	/*-----文字列変換メソッド-----*/
	public String toLegalString()
	throws SyntaxException, MathException {
		String strPresentText = "";
		//------------------------------------------
		//statement構文追加
		//------------------------------------------
		/*順次ステートメントを追加*/
		for (SyntaxStatement it: m_vecSynStatement) {
//			strPresentText += getIndentString() + it.toLegalString() + StringUtil.lineSep;
//			if(it.toLegalString() != ""){
//					strPresentText += getIndentString() + it.toLegalString() + StringUtil.lineSep;
//					strPresentText += it.toLegalString() + StringUtil.lineSep;
//					strPresentText += getIndentString();
//					strPresentText += it.toLegalString();
//					strPresentText += StringUtil.lineSep;
//					strPresentText += getIndentString();
//			}else{
//				strPresentText += it.toLegalString();
//				strPresentText += StringUtil.lineSep;
//				strPresentText += getIndentString();
//			}
//				strPresentText += it.toLegalString() + StringUtil.lineSep;
//				strPresentText += it.toLegalString();
//				strPresentText += StringUtil.lineSep;
//				strPresentText += getIndentString();
				strPresentText += it.toLegalString() + StringUtil.lineSep + getIndentString();
//				strPresentText += "    数式SyntaxStatementList" + it.toLegalString() + "enter" + StringUtil.lineSep + "  tab" + getIndentString();
		}
//		strPresentText += StringUtil.lineSep;

		return strPresentText;
	}

	//===================================================
	//addStatement
	//	statement構文追加メソッド
	//
	//@arg
	//	SyntaxStatement*		pStatement	: 追加する構文インスタンス
	//
	//===================================================
	/*-----構文追加メソッド-----*/
	public void addStatement(SyntaxStatement pStatement) {
		/*要素追加*/
		m_vecSynStatement.add(pStatement);
	}

}
