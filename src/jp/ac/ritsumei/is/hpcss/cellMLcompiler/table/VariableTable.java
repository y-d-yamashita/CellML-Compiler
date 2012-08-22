package jp.ac.ritsumei.is.hpcss.cellMLcompiler.table;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TableException;

/**
 * 変数テーブルクラス.
 * componentごとの変数テーブル.
 */
public class VariableTable extends Table<VariableReference> {

	/**
	 * 変数テーブルインスタンスを作成する.
	 * @param strTableName テーブル名
	 */
	public VariableTable(String strTableName) {
		super(strTableName);
	}

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

		/*値の挿入*/
		super.insert(strVariableName,pVarRef);
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

}
