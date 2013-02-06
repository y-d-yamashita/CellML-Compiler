package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * Java　StatementList
 * 
 * @author n-washio
 */
public class JavaSyntaxStatementList extends JavaSyntaxStatement {

	/*内部の構文*/
	protected Vector<JavaSyntaxStatement> m_vecSynStatement;
	int i;

	/*-----コンストラクタ-----*/
	public JavaSyntaxStatementList() {
		super(eSyntaxClassification.SYN_JAVA_STATEMENTLIST);
		m_vecSynStatement = new Vector<JavaSyntaxStatement>();
	}

	//===================================================
	//toLegalJavaString
	//	文字列型変換メソッド
	//
	//@return
	//	文字列型表現	: string
	//
	//===================================================
	/*-----文字列変換メソッド-----*/
	public String toLegalJavaString()
	throws SyntaxException, MathException {
		String strPresentText = "";
		//------------------------------------------
		//statement構文追加
		//------------------------------------------
		/*順次ステートメントを追加*/
		for (JavaSyntaxStatement it: m_vecSynStatement) {
//			strPresentText += getIndentString() + it.toLegalJavaString() + StringUtil.lineSep;
//			if(it.toLegalJavaString() != ""){
//					strPresentText += getIndentString() + it.toLegalJavaString() + StringUtil.lineSep;
//					strPresentText += it.toLegalJavaString() + StringUtil.lineSep;
//					strPresentText += getIndentString();
//					strPresentText += it.toLegalJavaString();
//					strPresentText += StringUtil.lineSep;
//					strPresentText += getIndentString();
//			}else{
//				strPresentText += it.toLegalJavaString();
//				strPresentText += StringUtil.lineSep;
//				strPresentText += getIndentString();
//			}
//				strPresentText += it.toLegalJavaString() + StringUtil.lineSep;
//				strPresentText += it.toLegalJavaString();
//				strPresentText += StringUtil.lineSep;
//				strPresentText += getIndentString();
				strPresentText += it.toLegalJavaString() + StringUtil.lineSep + getIndentString();
//				strPresentText += "    数式SyntaxStatementList" + it.toLegalJavaString() + "enter" + StringUtil.lineSep + "  tab" + getIndentString();
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
	public void addStatement(JavaSyntaxStatement pStatement) {
		/*要素追加*/
		m_vecSynStatement.add(pStatement);
	}

}
