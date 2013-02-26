package jp.ac.ritsumei.is.hpcss.cellMLonGPU.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition.eRelMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition.eRelMLVarType;

/**
 * 変数テーブルクラス.
 * componentごとの変数テーブル.
 */
public class VariableTable extends Table<VariableReference> {

	//variableIDcounter
	static int id = 0;
	
	/**
	 * 変数テーブルインスタンスを作成する.
	 * @param strTableName テーブル名
	 */
	public VariableTable(String strTableName) {
		super(strTableName);
	}

	public Collection<VariableReference> values(){return super.m_mapElements.values();}
	
	/**
	 * 変数を挿入する.
	 * @param strVariableName 変数名
	 * @param strInitValue 初期値文字列
	 */
	public void insert(String strVariableName, String strInitValue){

		/*構造体を構成*/
		VariableReference pVarRef = new VariableReference();
		pVarRef.strVariableName = strVariableName;
		pVarRef.pParentTable = this;
		pVarRef.pConnection = null;
		pVarRef.strInitValue = strInitValue;
		pVarRef.setID(id);
		id++;
		
		/*値の挿入*/
		super.insert(strVariableName,pVarRef);
	}

	public VariableReference getVariableReference(int id) throws TableException{
		for(VariableReference v:this.m_mapElements.values()){
			if(v.getID()==id)
				return v;
		}
		throw new TableException(this.getClass().getName(), "getVariableReference","Not Found variable of id:"+id);
	}
	
	public Collection<VariableReference> getVariableReferences(){
		return this.m_mapElements.values();
	}
	
	
	/**
	 * コネクションの設定をする.
	 * @param pDstVariableTable コネクション先変数テーブル
	 * @param strSrcVarName コネクション元変数名
	 * @param strDstVarName コネクション先変数名
	 * @throws TableException
	 */
	public void setConnection(VariableTable pDstVariableTable,
			String strSrcVarName, String strDstVarName)
	throws TableException {

		/*コネクション元の変数参照構造体を取得*/
		VariableReference pVarSrcRef = find(strSrcVarName);

		/*コネクション先の変数参照構造体を取得*/
		VariableReference pVarDstRef = pDstVariableTable.find(strDstVarName);

		/*コネクション元の現在のコネクション先を更新する*/
		while (pVarSrcRef != null) {

			/*コネクションを設定*/
			VariableReference pConnectVarRef = pVarSrcRef.pConnection;
			pVarSrcRef.pConnection = pVarDstRef;

			/*初期値のコピー*/
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

			/*次のコネクションへ*/
			pVarSrcRef = pConnectVarRef;
		}

		/*現在の変数参照とコネクション先の変数参照を入れ替える*/
		//this->replace(strSrcVarName,varRef);
	}

	/**
	 * 完全名を取得する.
	 * @param strName 変数名
	 * @return 完全名
	 * @throws TableException
	 */
	public String getFullName(String strName)
	throws TableException {
		/*変数参照の取得*/
		VariableReference pVarRef = this.find(strName);

		/*コネクションを辿る*/
		while(pVarRef.pConnection!=null){
			pVarRef = pVarRef.pConnection;
		}

		/*完全名を返す*/
		return pVarRef.pParentTable.getName() + "." + pVarRef.strVariableName;
	}

	/**
	 * 初期値を取得する.
	 * @param strName 変数名
	 * @return 初期値
	 * @throws TableException
	 */
	public String getInitValue(String strName)
	throws TableException {
		/*変数参照の取得*/
		VariableReference pVarRef = this.find(strName);
		/*初期値チェック*/
		if(pVarRef.strInitValue == null
				|| pVarRef.strInitValue.length() == 0){
			throw new TableException("VariableTable","getInitValue",
			"no initialize value found");
		}

		/*初期値を返す*/
		return pVarRef.strInitValue;
	}
	
	public VariableReference find(Math_ci variable) throws TableException, MathException{
		VariableReference ref = null;
		/*名前の取得と分解*/
		String strVarName = variable.toLegalString();
		//コンポーネント名切り分け
		int nIndexPos = strVarName.indexOf(".");
		if(nIndexPos > 0){
			strVarName = strVarName.substring(nIndexPos+1);
		}
		
		ref = super.find(strVarName);
		return ref;		
		
	}
	
	public int getVariableCount(){
		return super.m_mapElements.size();
	}
	
	public List<VariableReference> getSortedVariableReferencesList(){
		List<VariableReference> m_mapList = new ArrayList<VariableReference>();
		m_mapList.addAll(m_mapElements.values());
		Collections.sort( m_mapList);
//		debug
//			for(int i=0;i<m_mapList.size();i++){
//				System.out.println(m_mapList.get(i));			
//			}
		return m_mapList;
	}
	
	/**
	 * 最大マッチングのために変数タイプを取得する
	 * @param pTecMLAnalyzer
	 * @param pRelMLAnalyzer
	 * @throws MathException
	 * @throws TableException
	 * @throws RelMLException
	 * @author m-ara
	 */
	
	public void setRefVariableType(TecMLAnalyzer pTecMLAnalyzer, RelMLAnalyzer pRelMLAnalyzer) throws MathException, TableException, RelMLException{
		//TecML変数より検索をかける
		/*recurvarのTecML変数を取得*/
		Vector<Math_ci> m_curTypeVar = pTecMLAnalyzer.getM_vecRecurVar();
		for(Math_ci pTargetVar : m_curTypeVar){
			//RelMLから対応する変数ないし値を取得
			Vector<MathOperand> pCorrespondVar = pRelMLAnalyzer.getVecTecMLTransformation(pTargetVar);
			if(pCorrespondVar == null){
				throw new RelMLException("VariableTable", "setRefVariableTable",
						"can't find correspond TecML variables to "  + "\"" + pTargetVar.getName() + "\"");
			}
			for(MathOperand pVariable : pCorrespondVar){
				if(pVariable.matches(eMathOperand.MOPD_CI)){
					if((((Math_ci) pVariable).getCorrespondTag()) == eRelMLTag.RTAG_CORRCELLML){
						VariableReference m_curVariableReference = this.find((Math_ci)pVariable);
						if(m_curVariableReference != null){
							m_curVariableReference.setRelMLVarType(eRelMLVarType.RVAR_TYPE_RECURVAR);
						}
					}
				}
			}
		}
		
		/*arithvar*/
		m_curTypeVar = pTecMLAnalyzer.getM_vecArithVar();
		for(Math_ci pTargetVar : m_curTypeVar){
			//RelMLから対応する変数ないし値を取得
			Vector<MathOperand> pCorrespondVar = pRelMLAnalyzer.getVecTecMLTransformation(pTargetVar);
			if(pCorrespondVar == null){
				throw new RelMLException("VariableTable", "setRefVariableTable",
						"can't find correspond TecML variables to "  + "\"" + pTargetVar.getName() + "\"");
			}
			for(MathOperand pVariable : pCorrespondVar){
				if(pVariable.matches(eMathOperand.MOPD_CI)){
					if((((Math_ci) pVariable).getCorrespondTag()) == eRelMLTag.RTAG_CORRCELLML){
						VariableReference m_curVariableReference = this.find((Math_ci)pVariable);
						if(m_curVariableReference != null){
							m_curVariableReference.setRelMLVarType(eRelMLVarType.RVAR_TYPE_ARITHVAR);
						}
					}
				}
			}
		}
		
		/*constvar*/
		m_curTypeVar = pTecMLAnalyzer.getM_vecConstVar();
		for(Math_ci pTargetVar : m_curTypeVar){
			//RelMLから対応する変数ないし値を取得
			Vector<MathOperand> pCorrespondVar = pRelMLAnalyzer.getVecTecMLTransformation(pTargetVar);
			if(pCorrespondVar == null){
				throw new RelMLException("VariableTable", "setRefVariableTable",
						"can't find correspond TecML variables to "  + "\"" + pTargetVar.getName() + "\"");
			}
			for(MathOperand pVariable : pCorrespondVar){
				if(pVariable.matches(eMathOperand.MOPD_CI)){
					if((((Math_ci) pVariable).getCorrespondTag()) == eRelMLTag.RTAG_CORRCELLML){
						VariableReference m_curVariableReference = this.find((Math_ci)pVariable);
						if(m_curVariableReference != null){
							m_curVariableReference.setRelMLVarType(eRelMLVarType.RVAR_TYPE_CONSTVAR);
						}
					}
				}
			}
		}
		
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("<<"+super.m_strName+">>-----------------------\n");
		List<VariableReference> m_mapList = new ArrayList<VariableReference>();
		m_mapList.addAll(m_mapElements.values());
		Collections.sort( m_mapList);

		for(VariableReference t:m_mapList){
			sb.append("Variable[").append(t.getID()).append("]: ").append(t.toString());
		}
		sb.setLength(sb.length()-2);
		sb.append("\n------------------------------------------\n\n");
		return sb.toString();
	}
	
	public HashMap<Integer, String> getVariableList() {
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		
		List<VariableReference> m_mapList = new ArrayList<VariableReference>();
		m_mapList.addAll(m_mapElements.values());
		Collections.sort( m_mapList);

		for(VariableReference t:m_mapList){
			int id = t.getID();
			String var = t.strVariableName;
			hm.put(id, var);
		}

		return hm;
	}

}
