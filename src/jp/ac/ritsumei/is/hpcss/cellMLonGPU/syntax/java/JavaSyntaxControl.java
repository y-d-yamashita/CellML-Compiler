package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * Java　制御構文クラス
 * 
 * @author n-washio
 */
public class JavaSyntaxControl extends JavaSyntaxStatement {

	//========================================================
	//ENUM
	//========================================================

	/**
	 * 制御文種類列挙
	 */
	public enum eControlKind {
		CTRL_BLANK,
		CTRL_IF,
		CTRL_FOR,
		CTRL_WHILE,
		CTRL_DOWHILE
	};

	/**制御文種類*/
	protected eControlKind m_controlKind;

	/**内部の構文*/
	protected Vector<JavaSyntaxStatement> m_vecSynStatement;

	/**条件式*/
	protected JavaSyntaxCondition m_pSynCondition;

	/**
	 * インスタンスを作成する.
	 * @param controlKind 制御文種類
	 * @param pSynCondition 条件式
	 */
	public JavaSyntaxControl(eControlKind controlKind, JavaSyntaxCondition pSynCondition) {
		super(eSyntaxClassification.SYN_JAVA_CONTROL);
		m_controlKind = controlKind;
		m_pSynCondition = pSynCondition;
		m_vecSynStatement = new Vector<JavaSyntaxStatement>();
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.Syntax#toLegalJavaString()
	 */
	public String toLegalJavaString()
	throws SyntaxException, MathException {
		/*結果文字列*/
		String strPresentText = "";
		
		int dowhile_flag = 0;

		/*制御文の種類ごとの処理*/
		switch (m_controlKind) {

			/*if there are no control arguments*/
		case CTRL_BLANK:
			strPresentText += "";
			dowhile_flag = -1;
			break;
			
			/*if文*/
		case CTRL_IF:
			strPresentText += "if(" + m_pSynCondition.toLegalJavaString() + ")";
			dowhile_flag = -1;
			break;

			/*for文*/
		case CTRL_FOR:
			strPresentText += "for(" + m_pSynCondition.toStringFor() + ")";
			dowhile_flag = -1;
			break;

			/*while文*/
		case CTRL_WHILE:
			strPresentText += "while(" + m_pSynCondition.toLegalJavaString() + ")";
			dowhile_flag = -1;
			break;
			
		case CTRL_DOWHILE:
			strPresentText += "do";
			dowhile_flag = 1;
			break;

			/*予期しない種類*/
		default:
			throw new SyntaxException("SyntaxControl","toLegalJavaString",
						  "not declared control kind used");
		}

		/*-- do-while 以外の処理 --*/
		if(dowhile_flag == -1){
			/*ブロック開始括弧追加*/
			strPresentText += "{" + StringUtil.lineSep + StringUtil.lineSep;
	
			/*インデントインクリメント*/
			incIndent();
	
			//------------------------------------------
			//statement構文追加
			//------------------------------------------
			/*順次ステートメントを追加*/
			for (JavaSyntaxStatement it: m_vecSynStatement) {
				strPresentText += getIndentString() + it.toLegalJavaString() + StringUtil.lineSep;
			}
	
			/*インデントデクリメント*/
			decIndent();
	
			/*ブロック終了括弧追加*/
			strPresentText += StringUtil.lineSep + getIndentString() + "}" + StringUtil.lineSep;
		}
		
		/*-- do-while の処理 --*/
		if(dowhile_flag == 1){
			/*ブロック開始括弧追加*/
			strPresentText += "{" + StringUtil.lineSep + StringUtil.lineSep;
	
			/*インデントインクリメント*/
			incIndent();
	
			//------------------------------------------
			//statement構文追加
			//------------------------------------------
			/*順次ステートメントを追加*/
			for (JavaSyntaxStatement it: m_vecSynStatement) {
				strPresentText += getIndentString() + it.toLegalJavaString() + StringUtil.lineSep;
			}
	
			/*インデントデクリメント*/
			decIndent();
			
			/*ブロック終了括弧追加*/
			strPresentText += StringUtil.lineSep + getIndentString() + "}" ;
			
			/*終了条件の追加*/
			strPresentText += "while(" + m_pSynCondition.toLegalJavaString() + ");"+ StringUtil.lineSep;
		}
		return strPresentText;
	}

	/**
	 * statement構文を追加する.
	 * @param pStatement 追加する構文インスタンス
	 */
	public void addStatement(JavaSyntaxStatement pStatement) {
		/*要素追加*/
		m_vecSynStatement.add(pStatement);
	}

}
