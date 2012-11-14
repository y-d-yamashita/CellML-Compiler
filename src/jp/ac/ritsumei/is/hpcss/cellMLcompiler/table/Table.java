package jp.ac.ritsumei.is.hpcss.cellMLcompiler.table;

import java.util.HashMap;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TableException;

/**
 * table基底クラス.
 */
public class Table<T> {

	/**テーブル名*/
	protected String m_strName;

	/**テーブル要素*/
	protected HashMap<String,T> m_mapElements;

	/*親へのポインタ*/
	// 使用していないので削除
//	private Table m_pParentTable;

	/**
	 * tableインスタンスを作成する.
	 * @param strTableName テーブル名
	 */
	public Table(String strTableName) {
		m_strName = strTableName;
//		m_pParentTable = null;
		m_mapElements = new HashMap<String,T>();
	}

	/**
	 * 名前を取得する.
	 * @return 名前
	 */
	public String getName(){
		/*名前を返す*/
		return m_strName;
	}

	/**
	 * テーブルへ挿入する.
	 * @param strTableName テーブル名
	 * @param newElement 挿入する値
	 */
	protected void insert(String strTableName,T newElement){
		/*値の挿入*/
		m_mapElements.put(strTableName, newElement);
	}

	/**
	 * 名前から値を取得する.
	 * @param strTableName 検索する名前
	 * @return 見つかった要素の値
	 * @throws TableException
	 */
	protected T find(String strTableName) throws TableException {
		/*名前から検索する*/
		T v = m_mapElements.get(strTableName);

		/*見つからなかった場合*/
		if (v == null) {
			throw new TableException("Table","find","can't find table: " +strTableName);
		}

		/*見つかった要素を返す*/
		return v;
	}

	public boolean isContain(String strTableName){
		/*名前から検索する*/
		/*見つからなかった場合*/
		if (m_mapElements.get(strTableName) == null) 
			return false;
		/*見つかった要素を返す*/
		else return true;
	}

	/**
	 * 値を置換する.
	 * @param strTableName 検索する名前
	 * @param repElement 新たな値
	 */
	protected void replace(String strTableName,T repElement){
		/*以前の値の削除と新たな値の挿入*/
		m_mapElements.put(strTableName,repElement);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("<<"+this.m_strName+">>-----------------------\n");
		for(T t:m_mapElements.values()){
			sb.append(t.toString());
			sb.append(", ");
		}
		sb.setLength(sb.length()-2);
		return sb.toString();
	}

}
