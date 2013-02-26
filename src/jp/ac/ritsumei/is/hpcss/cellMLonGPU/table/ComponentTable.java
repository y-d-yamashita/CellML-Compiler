package jp.ac.ritsumei.is.hpcss.cellMLonGPU.table;

import java.util.HashMap;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;

/**
 * ComponentTableクラス.
 * modelごとのcomponentのテーブル.
 */
public class ComponentTable extends Table<VariableTable> {

	/**
	 * ComponentTableインスタンスを作成する.
	 * @param strTableName テーブル名
	 */
	public ComponentTable(String strTableName) {
		super(strTableName);
	}

	/**
	 * 変数テーブルを挿入する.
	 * @param newElement 変数テーブル
	 */
	public void insert(VariableTable newElement) {
		/*値の挿入*/
		super.insert(newElement.getName(),newElement);
	}

	/**
	 * コネクションの設定をする.
	 * @param strSrcCompName コネクション元テーブル名
	 * @param strDstCompName コネクション先テーブル名
	 * @param strSrcVarName コネクション元変数名
	 * @param strDstVarName コネクション先変数名
	 * @throws TableException
	 */
	public void setConnection(String strSrcCompName,String strDstCompName,
			String strSrcVarName,String strDstVarName)
	throws TableException {
		/*変数テーブルの取得*/
		VariableTable pSrcVariableTable =
			searchTable(strSrcCompName);	//コネクションを設定するコンポーネントの変数テーブル
		VariableTable pDstVariableTable =
			searchTable(strDstCompName);	//コネクション先のコンポーネントの変数テーブル

		/*コネクション設定*/
		pSrcVariableTable.setConnection(pDstVariableTable,strSrcVarName,strDstVarName);
	}

	/**
	 * 変数テーブルを取得する.
	 * @param strTableName テーブル名
	 * @return 変数テーブル
	 * @throws TableException
	 */
	public VariableTable searchTable(String strTableName) throws TableException {
		return find(strTableName);
	}
	
	/**
	 * map要素を取得
	 */
	public HashMap<String, VariableTable> getMapElements(){
		return m_mapElements;
	}

}
