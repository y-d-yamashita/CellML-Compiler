package jp.ac.ritsumei.is.hpcss.cellMLcompiler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.SimpleRecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML.SimpleRecMLDefinition.eRecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.GlobalLogger;

/**
 * 変数テーブルクラス.
 * componentごとの変数テーブル.
 */
public class SimpleRecMLVariableTable extends Table<SimpleRecMLVariableReference> {

	
	/**
	 * 変数テーブルインスタンスを作成する.
	 * @param strTableName テーブル名
	 */
	public SimpleRecMLVariableTable(String strTableName) {
		super(strTableName);
	}

	public Collection<SimpleRecMLVariableReference> values(){return super.m_mapElements.values();}
	/**
	 * 変数を挿入する.
	 * @param strVariableName 変数名
	 * @param strInitValue 初期値文字列
	 */
	public void insert(Math_ci variable,int id){

		/*構造体を構成*/
		SimpleRecMLVariableReference pVarRef = new SimpleRecMLVariableReference();
		pVarRef.setID(id);
		pVarRef.setMathCI(variable);
		try {
			pVarRef.strVariableName = variable.toLegalString();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//pVarRef.pParentTable = this;
		//pVarRef.pConnection = null;
		//pVarRef.strInitValue = strInitValue;

		/*値の挿入*/
		super.insert(pVarRef.strVariableName,pVarRef);
	}

	public SimpleRecMLVariableReference getVariableReference(int id) throws TableException{
		for(SimpleRecMLVariableReference v:this.m_mapElements.values()){
			if(v.getID()==id)
				return v;
		}
			
		throw new TableException(this.getClass().getName(), "getVariableReference","Not Found variable of id:"+id);
	}
	
	public Collection<SimpleRecMLVariableReference> getVariableReferences(){
		return this.m_mapElements.values();
	}

	/**
	 * コネクションの設定をする.
	 * @param pDstVariableTable コネクション先変数テーブル
	 * @param strSrcVarName コネクション元変数名
	 * @param strDstVarName コネクション先変数名
	 * @throws TableException
	 */
	/*
	public void setConnection(RecMLVariableTable pDstVariableTable,
			String strSrcVarName, String strDstVarName)
	throws TableException {

		//コネクション元の変数参照構造体を取得
		RecMLVariableReference pVarSrcRef = find(strSrcVarName);

		//コネクション先の変数参照構造体を取得
		RecMLVariableReference pVarDstRef = pDstVariableTable.find(strDstVarName);

		//コネクション元の現在のコネクション先を更新する
		while (pVarSrcRef != null) {

			//コネクションを設定
			VariableReference pConnectVarRef = pVarSrcRef.pConnection;
			pVarSrcRef.pConnection = pVarDstRef;

			//初期値のコピー
			if (pVarSrcRef.strInitValue != null
					&& pVarSrcRef.strInitValue.length() != 0
					&& (pVarDstRef.strInitValue == null
							|| pVarDstRef.strInitValue.length() == 0)) {
				pVarDstRef.strInitValue = pVarSrcRef.strInitValue;
			}
			else if ((pVarSrcRef.strInitValue == null
					|| pVarSrcRef.strInitValue.length() == 0)
					&& pVarDstRef.strInitValue != null
					&& pVarDstRef.strInitValue.length() != 0) {
				pVarSrcRef.strInitValue = pVarDstRef.strInitValue;
			}

			//次のコネクションへ
			pVarSrcRef = pConnectVarRef;
		}

		//現在の変数参照とコネクション先の変数参照を入れ替える
		//this->replace(strSrcVarName,varRef);
	}
	*/
	/**
	 * 完全名を取得する.
	 * @param strName 変数名
	 * @return 完全名
	 * @throws TableException
	 */
	/*
	public String getFullName(String strName)
	throws TableException {
		/変数参照の取得
		VariableReference pVarRef = this.find(strName);

		//コネクションを辿る
		while(pVarRef.pConnection!=null){
			pVarRef = pVarRef.pConnection;
		}

		//完全名を返す
		return pVarRef.pParentTable.getName() + "." + pVarRef.strVariableName;
	}

*/
	/**
	 * 初期値を取得する.
	 * @param strName 変数名
	 * @return 初期値
	 * @throws TableException
	 */
	public String getInitValue(String strName)
	throws TableException {
		/*変数参照の取得*/
		SimpleRecMLVariableReference pVarRef = this.find(strName);
		/*初期値チェック*/

		/*if(pVarRef.strInitValue == null
				|| pVarRef.strInitValue.length() == 0){
			throw new TableException("VariableTable","getInitValue",
			"no initialize value found");
		}
*/
		/*初期値を返す*/
		//return pVarRef.strInitValue;
		return null;
	}

	public SimpleRecMLVariableReference find(Math_ci variable) throws TableException, MathException{
		SimpleRecMLVariableReference ref = null;
		ref = super.find(variable.toLegalString());
		return ref;		
		
	}
	
	public int getVariableCount(){
		return super.m_mapElements.size();
	}
	
	public List<SimpleRecMLVariableReference> getSortedRecMLVariableReferencesList(){
		List<SimpleRecMLVariableReference> m_mapList = new ArrayList<SimpleRecMLVariableReference>();
		m_mapList.addAll(m_mapElements.values());
		Collections.sort( m_mapList);
		return m_mapList;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("<<"+super.m_strName+">>-----------------------\n");
		List<SimpleRecMLVariableReference> m_mapList = new ArrayList<SimpleRecMLVariableReference>();
		m_mapList.addAll(m_mapElements.values());
		Collections.sort( m_mapList);

		for(SimpleRecMLVariableReference t:m_mapList){
			sb.append("Variable[").append(t.getID()).append("]: ").append(t.toString());
		}
		sb.setLength(sb.length()-2);
		sb.append("\n------------------------------------------\n\n");
		return sb.toString();
	}

	public void setRefVariableType(SimpleRecMLAnalyzer recMLAnalyzer) {
		boolean continueFlag;
		for(SimpleRecMLVariableReference ref:this.m_mapElements.values()){
			continueFlag=false;
			String refVarName=ref.strVariableName;
			String simpleVarName;
		if(refVarName.indexOf('[')>0){
			simpleVarName =refVarName.substring(0, refVarName.indexOf('['));
		}else{
			simpleVarName=refVarName;
		}
			for(Math_ci ci:recMLAnalyzer.getM_HashMapRecurVar().keySet()){
				if(ci.getM_strPresentText().equals(simpleVarName)){
					ref.setRecMLVarType(eRecMLVarType.CVAR_TYPE_RECURVAR);
					continueFlag=true;
					break;
				}
			}
			if(continueFlag)continue;

			
			for(Math_ci ci:recMLAnalyzer.getM_HashMapConstVar().keySet()){
				if(ci.getM_strPresentText().equals(simpleVarName)){
					ref.setRecMLVarType(eRecMLVarType.CVAR_TYPE_CONSTVAR);
					continueFlag=true;
					break;
				}
			}
			if(continueFlag)continue;

			
			for(Math_ci ci:recMLAnalyzer.getM_HashMapArithVar().keySet()){
				if(ci.getM_strPresentText().equals(simpleVarName)){
					ref.setRecMLVarType(eRecMLVarType.CVAR_TYPE_ARITHVAR);
					continueFlag=true;
					break;
				}
			}
			if(continueFlag)continue;


			for(Math_ci ci:recMLAnalyzer.getM_HashMapOutputVar().keySet()){
				if(ci.getM_strPresentText().equals(simpleVarName)){
					ref.setRecMLVarType(eRecMLVarType.CVAR_TYPE_OUTPUT);
					continueFlag=true;
					break;
				}
			}
			if(continueFlag)continue;

			
		}
	}

}
