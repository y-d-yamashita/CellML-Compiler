package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.ArrayList;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition.eRelMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition.eRelMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.ComponentTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.VariableTable;

/**
 * RelML解析クラス.
 */
public class RelMLAnalyzer extends MathMLAnalyzer {

	/**数式解析中判定*/
	private boolean m_bMathParsing;

	/*種類ごとの対応変数名*/
	Vector<Math_ci> m_vecTimeVar;
	Vector<Math_ci> m_vecDiffVar;
	Vector<Math_ci> m_vecArithVar;
	Vector<Math_ci> m_vecConstVar;
	Vector<Math_ci> m_vecPartialDiffVar;

	/**読み込みファイル名*/
	String m_strFileNameCellML;
	/**読み込みファイル名*/
	String m_strFileNameTecML;

	/*微分方程式の左辺式記述の解析用変数*/
	boolean m_bDiffEquListParsing;
	String m_strCurComponent;

	/**
	 * RelML解析インスタンスを作成する.
	 */
	public RelMLAnalyzer() {
		m_bMathParsing = false;
		m_bDiffEquListParsing = false;
		m_vecTimeVar = new Vector<Math_ci>();
		m_vecDiffVar = new Vector<Math_ci>();
		m_vecArithVar = new Vector<Math_ci>();
		m_vecConstVar = new Vector<Math_ci>();
		m_vecPartialDiffVar = new Vector<Math_ci>();
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findTagStart(java.lang.String, jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAttribute)
	 */
	public void findTagStart(String strTag, XMLAttribute pXMLAttr)
	throws MathException, XMLException, RelMLException, CellMLException, TecMLException, RecMLException {
		//-----------------------------------------------------
		//数式部の解析
		//-----------------------------------------------------
		if(m_bMathParsing){

			/*MathML解析器に投げる*/
			super.findTagStart(strTag,pXMLAttr);
		}

		//-----------------------------------------------------
		//RelML解析
		//-----------------------------------------------------
		else{

			/*タグidの取得*/
			eRelMLTag tagId = RelMLDefinition.getRelMLTagId(strTag);

			/*タグ種別ごとの処理*/
			switch(tagId){

				//-----------------------------------TecMLファイルの指定
			case RTAG_TECML:
				{
					/*ファイル名取得*/
					String strFileName = pXMLAttr.getValue("filename");

					/*ファイル名の指定*/
					m_strFileNameTecML = strFileName;

					break;
				}

				//-----------------------------------RelMLファイルの指定
			case RTAG_CELLML:
				{
					/*ファイル名取得*/
					String strFileName = pXMLAttr.getValue("filename");

					/*ファイル名の指定*/
					m_strFileNameCellML = strFileName;

					break;
				}

				//-----------------------------------変数宣言
			case RTAG_VARIABLE:
				{
					/*変数名とタイプ取得*/
					String strName = pXMLAttr.getValue("name");
					String strComponent = pXMLAttr.getValue("component");
					String strType = pXMLAttr.getValue("type");
					eRelMLVarType varType;

					try{
						varType = RelMLDefinition.getRelMLVarType(strType);
					}
					catch(RelMLException e){
						System.err.println(e.getMessage());
						throw new RelMLException("RelMLAnalyzer","findTagStart",
								"Unknown type used in variable daclaration");
					}

					/*コンポーネント名をつなげる*/
					strName = strComponent + "." + strName;

					/*変数名から変数インスタンス生成*/
					Math_ci pVariable =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,strName);

					/*タイプごとにベクタに追加*/
					switch(varType){

						//-------------------------------時間変数型
					case RVAR_TYPE_TIMEVAR:
						m_vecTimeVar.add(pVariable);
						break;

						//-------------------------------微分変数型
					case RVAR_TYPE_DIFFVAR:
						m_vecDiffVar.add(pVariable);
						break;

						//-------------------------------通常変数型
					case RVAR_TYPE_ARITHVAR:
						m_vecArithVar.add(pVariable);
						break;

						//-------------------------------定数型
					case RVAR_TYPE_CONSTVAR:
						m_vecConstVar.add(pVariable);
						break;
						
						//-------------------------------if partial differential variable
					case RVAR_TYPE_PARTIALDIFFVAR:
						m_vecPartialDiffVar.add(pVariable);
						break;

						//---------------------------------その他の型
					default:
						throw new RelMLException("RelMLAnalyzer","findTagStart",
								"Unknown type used in variable daclaration");
					}

					break;
				}

				//-----------------------------------微分方程式左辺式記述の解析開始
			case RTAG_DIFFEQU:
				{
					/*解析開始フラグON*/
					m_bDiffEquListParsing = true;
					break;
				}

				//-----------------------------------コンポーネントの解析開始
			case RTAG_COMPONENT:
				{
					/*diffequの中でない場合は例外*/
					if(!m_bDiffEquListParsing){
						throw new RelMLException("RelMLAnalyzer","findTagStart",
								"component found without diffequ tag");
					}

					/*コンポーネント名取得*/
					m_strCurComponent = pXMLAttr.getValue("name");
					break;
				}

				//-----------------------------------数式部分の解析開始
			case RTAG_MATH:
				{
					/*解析開始フラグON*/
					m_bMathParsing = true;
					m_NextOperandKind = null;
					break;
				}
			}

		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findTagEnd(java.lang.String)
	 */
	public void findTagEnd(String strTag)
	throws MathException, RelMLException, CellMLException, RecMLException {
		//-----------------------------------------------------
		//数式部の解析
		//-----------------------------------------------------
		if (m_bMathParsing) {

			/*数式解析終了*/
			if(strTag.equals(RelMLDefinition.RELML_TAG_STR_MATH)){
				m_bMathParsing = false;
				return;
			}

			/*MathML解析器に投げる*/
			super.findTagEnd(strTag);
		}

		//-----------------------------------------------------
		//RelML解析
		//-----------------------------------------------------
		else {
			/*タグidの取得*/
			eRelMLTag tagId = RelMLDefinition.getRelMLTagId(strTag);

			/*タグ種別ごとの処理*/
			switch (tagId) {

				//-----------------------------------微分方程式左辺式記述の解析開始
			case RTAG_DIFFEQU:
				{
					/*解析フラグoff*/
					m_bDiffEquListParsing = false;
					break;
				}

				//-----------------------------------コンポーネントの解析終了
			case RTAG_COMPONENT:
				{
					/*コンポーネント名初期化*/
					m_strCurComponent = "";
					break;
				}
			}
		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findText(java.lang.String)
	 */
	public void findText(String strText)
	throws MathException, CellMLException, TableException {
		//-----------------------------------------------------
		//数式部の解析
		//-----------------------------------------------------
		if (m_bMathParsing && m_NextOperandKind != null) {
			/*コンポーネント名をつなげる*/
			if(m_NextOperandKind==eMathOperand.MOPD_CI){
				strText = m_strCurComponent + "." + strText;
			}

			/*MathML解析器に投げる*/
			super.findText(strText);
		}

		//-----------------------------------------------------
		//RelML解析
		//-----------------------------------------------------
		else {
		}
	}

	/**
	 * 微分変数か判定する.
	 * @param pVariable 判定する数式
	 * @return 一致判定
	 */
	public boolean isDiffVar(MathOperand pVariable) {
		/*すべての要素を比較*/
		for (Math_ci it: m_vecDiffVar) {
			/*一致判定*/
			if (it.matches(pVariable)) {
				return true;
			}
		}

		/*不一致*/
		return false;
	}

	/**
	 * 通常変数か判定する.
	 * @param pVariable 判定する数式
	 * @return 一致判定
	 */
	public boolean isArithVar(MathOperand pVariable) {
		/*すべての要素を比較*/
		for (Math_ci it: m_vecArithVar) {
			/*一致判定*/
			if (it.matches(pVariable)) {
				return true;
			}
		}

		/*不一致*/
		return false;
	}

	/**
	 * 定数変数か判定する.
	 * @param pVariable 判定する数式
	 * @return 一致判定
	 */
	public boolean isConstVar(MathOperand pVariable) {
		/*すべての要素を比較*/
		for (Math_ci it: m_vecConstVar) {
			/*一致判定*/
			if (it.matches(pVariable)) {
				return true;
			}
		}

		/*不一致*/
		return false;
	}

	//========================================================
	//isPartialDiffVar
	// Check if the variable is a partial differential / propagating variable 
	//
	//@arg
	// MathOperand*	pVariable	: 判定する数式
	//
	//@return
	// 一致判定	: bool
	//
	//========================================================
	public boolean isPartialDiffVar(MathOperand pVariable) {
		/*すべての要素を比較*/
		for (Math_ci it: m_vecPartialDiffVar) {
			/*一致判定*/
			if (it.matches(pVariable)) {
				return true;
			}
		}

		/*不一致*/
		return false;
	}

	/**
	 * 数式を照合する
	 * @param pExpression 照合する数式
	 * @return 照合結果
	 */
	public boolean matchesExpression(MathExpression pExpression) {
		/*数式数を取得*/
		int nExpressionNum = super.getExpressionCount();

		for (int i = 0; i < nExpressionNum; i++) {
			/*数式取得*/
			MathExpression pMathExp = super.getExpression(i);

			/*数式比較*/
			if (pMathExp.matches(pExpression)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 読み込みCellMLファイル名を習得する.
	 * @return 読み込むCellMLのファイル名
	 */
	public String getFileNameCellML() {
		return m_strFileNameCellML;
	}

	/**
	 * 読み込みTecMLファイル名を習得する.
	 * @return 読み込むTecMLのファイル名
	 */
	public String getFileNameTecML() {
		return m_strFileNameTecML;
	}

	/**
	 * 変数テーブルを適用する.
	 * @param pComponentTable コンポーネントテーブルインスタンス
	 * @throws RelMLException
	 * @throws MathException
	 */
	public void applyComponentTable(ComponentTable pComponentTable)
	throws RelMLException, MathException {
		Vector<String> vecVarNameL = new Vector<String>();
//		printContents();	// debug
		m_vecDiffVar = applyComponentTable(pComponentTable, m_vecDiffVar, vecVarNameL);
		m_vecArithVar = applyComponentTable(pComponentTable, m_vecArithVar, vecVarNameL);
		m_vecConstVar = applyComponentTable(pComponentTable, m_vecConstVar, vecVarNameL);
//		printContents();	// debug
	}

	/**
	 * 変数テーブルを適用する.
	 * @param pComponentTable コンポーネントテーブルインスタンス
	 * @param orgV 適用前変数テーブル
	 * @param vecVarNameL 変数リスト
	 * @return 適用後変数テーブル
	 * @throws RelMLException
	 * @throws MathException
	 */
	private Vector<Math_ci> applyComponentTable(ComponentTable pComponentTable,
			Vector<Math_ci> orgV, Vector<String> vecVarNameL)
			throws RelMLException, MathException {

		//-------------------------------------------------
		//すべての変数に変数テーブルから名前を取得させる
		//-------------------------------------------------
		Vector<Math_ci> newV = new Vector<Math_ci>();

		for (Math_ci pVariable: orgV) {
			/*名前の取得と分解*/
			String strVarName = pVariable.toLegalString();
			int nDotPos = strVarName.indexOf(".");
			String strCompName = strVarName;
			if (nDotPos >= 0) {
				strCompName = strVarName.substring(0, nDotPos);
			}
			String strLocalName = strVarName.substring(nDotPos + 1);
			String strFullName;

			/*テーブルの探索*/
			try {
				VariableTable pVariableTable = pComponentTable.searchTable(strCompName);
				strFullName = pVariableTable.getFullName(strLocalName);
			}
			catch (TableException e) {
				System.err.println(e.getMessage());
				throw new RelMLException("RelMLAnalyzer","applyComponentTable",
							 "can't apply variable table to relml variables");
			}

			/*名前の重複チェック*/
			boolean found = false;
			for (String it2: vecVarNameL) {
				/*重複削除*/
				if (it2.equals(strFullName)) {
					found = true;
					break;
				}
			}

			/*見つけた場合は次の変数へ*/
			if (found) {
				continue;
			}

			/*変数リストに登録*/
			vecVarNameL.add(strFullName);

			/*新しいベクタに追加*/
			Math_ci pNewVariable =
				(Math_ci) MathFactory.createOperand(eMathOperand.MOPD_CI, strFullName);
			newV.add(pNewVariable);
		}

		/*新しいベクタの適用*/
		return newV;
	}

	/**
	 * 解析内容を標準出力する.
	 * @throws MathException
	 */
	public void printContents() throws MathException {
		//-------------------------------------------------
		//出力用一時変数初期化
		//-------------------------------------------------
		/*ベクタ配列*/
		ArrayList<Vector<Math_ci>> vectorArray = new ArrayList<Vector<Math_ci>>();
		vectorArray.add(m_vecDiffVar);
		vectorArray.add(m_vecArithVar);
		vectorArray.add(m_vecConstVar);
		vectorArray.add(m_vecPartialDiffVar);

		/*表示タグ配列初期化*/
		String[] strTag = {
			"diffvar\t",
			"arithvar\t",
			"constvar\t",
			"partialdiffvar\t",
		};

		//-------------------------------------------------
		//出力
		//-------------------------------------------------
		/*開始線出力*/
		System.out.println("[RelML]------------------------------------");

		/*読み込みファイル名出力*/
		System.out.println("CellML\t: " +  m_strFileNameCellML);
		System.out.println("TecML\t: " + m_strFileNameTecML);

		/*変数リスト出力*/
		for (int i = 0; i < vectorArray.size(); i++) {
			System.out.print(strTag[i] + "= { ");

			boolean first = true;
			for(Math_ci it: vectorArray.get(i)) {
				if (first) {
					first = false;
				} else {
					System.out.print(" , ");
				}
				System.out.print(it.toLegalString());
			}

			System.out.println("}");
		}

		/*数式出力*/
		//super.printExpressions();

		/*改行*/
		System.out.println();
	}
	
	//========================================================
	// getPartialDiffVarCount
	// 	return the number of propagating variables in the simulation
	//
	//========================================================
	/* TODO: get the number of propagating variables */
	public int getPartialDiffVarCount() throws MathException{
		//int PartialDiffVarNum = m_vecPartialDiffVar.size();
		int PartialDiffVarNum = 1;
		
		return PartialDiffVarNum;
	}

}
