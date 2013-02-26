package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition.eRelMLTag;


/**
 * RelML解析クラス.
 */
public class RelMLAnalyzer extends MathMLAnalyzer {

	/**数式解析中判定*/
	private boolean m_bMathParsing;

	/*変数名*/
	Vector<Math_ci> m_vecRelMLVariable;

	/**読み込みファイル名*/
	String m_strFileNameCellML;
	/*簡易名*/
	String m_strSimpleNameCellML;
	
	/**読み込みファイル名*/
	String m_strFileNameTecML;

	String m_strCurComponent;
	
	/**tecml変数対応リスト*/
	HashMap<Math_ci, Vector<MathOperand>> m_mapTecMLVariableTransformation;
	/**現在tecml変数ベクター*/
	Vector<MathOperand> m_veccurTecMLVariable;
	
	/**condition数式情報*/
	Vector<String[]> m_vecConditionInformation;

	//applyCellMLVariableType
	Vector<Math_ci> m_vecRecurVar;
	Vector<Math_ci> m_vecArithVar;
	Vector<Math_ci> m_vecConstVar;
	
	/**
	 * RelML解析インスタンスを作成する.
	 */
	public RelMLAnalyzer() {
		m_bMathParsing = false;
		m_vecRelMLVariable = new Vector<Math_ci>();
		m_mapTecMLVariableTransformation = new HashMap<Math_ci, Vector<MathOperand>>();
		m_vecConditionInformation = new Vector<String[]>();
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
			
			/*get RelML condition information*/
			String strCondName = pXMLAttr.getValue(RelMLDefinition.RELML_ATTR_CONDNAME);
			String strLoopIndex = pXMLAttr.getValue(RelMLDefinition.RELML_ATTR_LOOPINDEX);
			if(strCondName != null && strLoopIndex != null){
				int pExID = (int)super.m_pCurMathExpression.getExID();
				String[] pCondInfo = {strCondName, strLoopIndex, String.valueOf(pExID)};
				m_vecConditionInformation.add(pCondInfo);
			}
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
					
					/*簡易名取得*/
					String strName = pXMLAttr.getValue("name");
					m_strSimpleNameCellML = strName;
					
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

					/*変数名から変数インスタンス生成*/
					//nullの場合Math_ciを作らずにnullとして登録
					Math_ci pVariable;
					if(strName.endsWith("null")){
						pVariable = null;						
					}else{
						pVariable =
								(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,strName);
					}

					/*ベクタに追加*/
					m_vecRelMLVariable.add(pVariable);
					
					break;
				}
				
				//----------------------------------tecml変数対応記述におけるtecml変数
			case RTAG_TVAR:
				{
					/*変数名取得*/
					String strName = pXMLAttr.getValue("name");
					
					/*変数名から変数インスタンス生成*/
					Math_ci pVariable =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,strName);
					
					/*TecML変数対応リスト作成*/
					m_veccurTecMLVariable = new Vector<MathOperand>();
					m_mapTecMLVariableTransformation.put(pVariable, m_veccurTecMLVariable);
						
					break;
					
				}
				
				//-----------------------------------tecml変数対応記述におけるcellml変数
			case RTAG_CORRCELLML:
				{
					/*ファイル名、コンポーネント名、変数名取得*/
					String strFileName = pXMLAttr.getValue("file");
					String strComponent = pXMLAttr.getValue("component");
					String strName = pXMLAttr.getValue("name");
					
					/*ファイル名、コンポーネント名をつなげる*/
					strName =  strComponent + "." + strName;
					
					/*変数名から変数インスタンス生成*/
					Math_ci pVariable =
						(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,strName);
					
					//CellMLファイル名登録
					if(m_strSimpleNameCellML.contentEquals(strFileName)){
						pVariable.setCellMLFileName(m_strFileNameCellML);
					}
					
					//CellML変数であることを登録
					pVariable.setCorrespondTag(eRelMLTag.RTAG_CORRCELLML);
					
					
					/*TecML変数対応リストに登録*/
					m_veccurTecMLVariable.add(pVariable);
					
					break;
				}
				
				//-----------------------------------tecml変数対応記述におけるcellml変数
			case RTAG_CORRRELML:
				{
					/*変数名または値を取得*/
					String strName = pXMLAttr.getValue("name");
					String strValue = pXMLAttr.getValue("value");
					
					MathOperand pVariable = null;
					
					/*TecML変数対応リストに登録*/
					if(strName != null){
						/*変数名から変数インスタンス生成*/
						if(strValue != null){
							pVariable =
									(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, strName, Double.valueOf(strValue));
						}else{
							pVariable =
									(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,strName);
						}
						//CellML変数であることを登録
						((Math_ci)pVariable).setCorrespondTag(eRelMLTag.RTAG_CORRRELML);
					}else if(strValue != null ){
						/*値から定数インスタンス生成*/
						pVariable =
							(Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,strValue);
						
					}
					
					/*TecML変数対応リストに登録*/
					m_veccurTecMLVariable.add(pVariable);
					
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

				//-----------------------------------コンポーネントの解析終了
			case RTAG_COMPONENT:
				{
					/*コンポーネント名初期化*/
					m_strCurComponent = "";
					break;
				}
				
				//-----------------------------------tecml変数対応記述内でtecml変数部分が終了
			case RTAG_TVAR:
				{
					/*現在tecml変数をクリア*/
					m_veccurTecMLVariable = null;
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
			if(m_strCurComponent != null){
				if(m_NextOperandKind==eMathOperand.MOPD_CI){
					strText = m_strCurComponent + "." + strText;
				}
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
	 * Condition情報を取得する
	 * @return Condition情報String
	 * @author m-ara
	 * ([0]:condition名、[1]:ループインデックス名、[2]:数式ID)
	 */
	public Vector<String[]> getConditionInformation(){
		return m_vecConditionInformation;
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
	 * 該当するTecML変数の対応リストを取得する
	 * @throws MathException 
	 * @author m-ara
	 */
	public Vector<MathOperand> getVecTecMLTransformation(Math_ci pVariable) throws MathException{
		
		/*名前の取得*/
		String strVarName = pVariable.getName();
		
		
		for(Math_ci it : m_mapTecMLVariableTransformation.keySet()){
			if(it.toLegalString().equals(strVarName)){
				return m_mapTecMLVariableTransformation.get(it);
			}
		}
		return null;
		
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
//		vectorArray.add(m_vecRecurVar);
//		vectorArray.add(m_vecArithVar);
//		vectorArray.add(m_vecConstVar);
//		vectorArray.add(m_vecPartialDiffVar);

		/*表示タグ配列初期化*/
		String[] strTag = {
			"Recurvar\t",
			"Arithvar\t",
			"constvar\t",
			"partialdiffvar\t",
		};

		//-------------------------------------------------
		//出力
		//-------------------------------------------------
		/*開始線出力*/
		System.out.println("[RelML]------------------------------------");

		/*読み込みファイル名出力*/
		System.out.println("CellML\t: " + m_strFileNameCellML);
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

		/*tecml対応リスト表示*/
		//キーを取得する
        Set<Math_ci> keys = m_mapTecMLVariableTransformation.keySet();

        // mapの中身をすべて表示する
        for(Math_ci key : keys) {
        	System.out.print(key.toLegalString() + " :"); 
        	for(MathFactor it : m_mapTecMLVariableTransformation.get(key)){
               System.out.print(" "+it.toLegalString());
        	}
        	System.out.println();
        }
		
		/*数式出力*/
		super.printExpressions();
		for(String[] str: m_vecConditionInformation){
			System.out.print("CondName:"+str[0]);
			System.out.print(" , LoopIndex:"+str[1]);
			System.out.println(" , Ex-ID:"+str[2]);
		}
		
		/*改行*/
		System.out.println();
	}
	
}
