package jp.ac.ritsumei.is.hpcss.cellMLonGPU.table;

/**
 * 変数参照クラス.
 */
public class VariableReference {

	/**変数名*/
	String strVariableName;
	/**親コンポーネントの変数テーブル*/
	VariableTable pParentTable;
	/**コネクション先参照構造体*/
	VariableReference pConnection;
	/**初期値文字列*/
	String strInitValue;

}
