package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML.CellMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML.CellMLDefinition.eCellMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.ComponentTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.VariableTable;

/**
 * CellML解析クラス.
 */
public class CellMLVariableAnalyzer extends MathMLAnalyzer {

	private static final String CELLML_MODEL_NAME = "model";
	//componentテーブルの名前に利用する．何でも良い

	/**変数テーブル*/
	ComponentTable m_pComponentTable;
	VariableTable m_pCurVariableTable;

	/**コネクション設定用変数*/
	boolean m_bSettingConnection;
	String m_strMapComponent1;
	String m_strMapComponent2;

	/**
	 * CellML解析インスタンスを作成する.
	 */
	public CellMLVariableAnalyzer() {
		m_pComponentTable = null;
		m_pCurVariableTable = null;
		m_bSettingConnection = false;
		initialize();
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findTagStart(java.lang.String, jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAttribute)
	 */
	public void findTagStart(String strTag, XMLAttribute pXMLAttr)
	throws CellMLException {
		/*タグidの取得*/
		eCellMLTag tagId;

		try {
			tagId = CellMLDefinition.getCellMLTagId(strTag);
		}
		catch (CellMLException e) {
			/*特定のタグ以外は無視する*/
			return;
		}
		/*タグ種別ごとの処理*/
		switch (tagId) {

			//-----------------------------------コンポーネント解析開始
		case CTAG_COMPONENT:
			{
				String strComponentName;

				try {
					/*コンポーネント名を取得*/
					strComponentName =
						pXMLAttr.getValue(CellMLDefinition.CELLML_ATTR_STR_NAME);
				}
				catch (Exception e) {
					throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
								  "component name not found");
				}

				/*新たな変数テーブルを作る*/
				VariableTable pNewVariableTable = new VariableTable(strComponentName);

				/*変数テーブルをコンポーネントテーブルに追加する*/
				m_pComponentTable.insert(pNewVariableTable);

				/*現在のテーブルに指定する*/
				m_pCurVariableTable = pNewVariableTable;

				break;
			}

			//-----------------------------------変数解析開始
		case CTAG_VARIABLE:
			{
				/*コンポーネント中でない場合は例外*/
				if(m_pCurVariableTable==null){
					throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
							"variable declared without component declaration");
				}

				String strVariableName;
				String strInitValue = null;

				try {
					/*コンポーネント名を取得*/
					strVariableName =
						pXMLAttr.getValue(CellMLDefinition.CELLML_ATTR_STR_NAME);
				}
				catch (Exception e) {
					throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
								  "variable name not found");
				}

				try {
					/*初期値を取得*/
					strInitValue =
						pXMLAttr.getValue(CellMLDefinition.CELLML_ATTR_STR_INITIAL_VALUE);
				}
				catch (Exception e) {
					/*初期値がなくても問題ない*/
				}

				/*変数テーブルに変数登録*/
				m_pCurVariableTable.insert(strVariableName,strInitValue);

				break;
			}

			//-----------------------------------コネクション解析開始
		case CTAG_CONNECTION:
			{
				/*コネクション解析フラグ*/
				m_bSettingConnection = true;
			}

			break;

			//-----------------------------------コネクション解析-コンポーネント関連記述
		case CTAG_MAP_COMPONENTS:
			{
				/*connection中でない場合は例外*/
				if(!m_bSettingConnection){
					throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
								  "map_component found without connection tag");
				}

				try {
					/*接続するコンポーネント名を取得*/
					m_strMapComponent1 =
						pXMLAttr.getValue(CellMLDefinition.CELLML_ATTR_STR_COMPONENT_1);
					m_strMapComponent2 =
						pXMLAttr.getValue(CellMLDefinition.CELLML_ATTR_STR_COMPONENT_2);
				}
				catch (Exception e) {
					throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
								  "mapped component missing");
				}
				break;
			}

			//-----------------------------------コネクション解析-変数関連記述
		case CTAG_MAP_VARIABLES:
			{
				/*connection中でない場合は例外*/
				if(!m_bSettingConnection){
					throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
								  "map_variable found without connection tag");
				}

				/*接続するコンポーネントが指定されていない場合は例外*/
				if(m_strMapComponent1.length() == 0 || m_strMapComponent2.length() == 0){
					throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
								  "map_variable found mapped component declared");
				}

				String strMapVariable1;
				String strMapVariable2;

				try {
					/*接続する変数名を取得*/
					strMapVariable1 =
						pXMLAttr.getValue(CellMLDefinition.CELLML_ATTR_STR_VARIABLE1);
					strMapVariable2 =
						pXMLAttr.getValue(CellMLDefinition.CELLML_ATTR_STR_VARIABLE2);
				}
				catch (Exception e) {
					throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
								  "mapped variable missing");
				}

				try {
					/*コネクションの設定*/
					m_pComponentTable.setConnection(m_strMapComponent2,
							m_strMapComponent1,strMapVariable2,strMapVariable1);
				}
				catch (TableException e) {
					System.err.println(e.getMessage());
					throw new CellMLException("CellMLVariableAnalyzer","findTagStart",
								  "can't connect variables");
				}

				break;
			}
		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findTagEnd(java.lang.String)
	 */
	public void findTagEnd(String strTag) {
		/*タグidの取得*/
		eCellMLTag tagId;

		try {
			tagId = CellMLDefinition.getCellMLTagId(strTag);
		}
		catch (CellMLException e) {
			/*特定のタグ以外は無視する*/
			return;
		}

		/*タグ種別ごとの処理*/
		switch (tagId) {

			//-----------------------------------コンポーネント解析終了
		case CTAG_COMPONENT:
			{
				/*現在の変数テーブルをNULLに*/
				m_pCurVariableTable = null;
				break;
			}


			//-----------------------------------コネクション解析終了
		case CTAG_CONNECTION:
			{
				/*コネクション解析フラグOFF*/
				m_bSettingConnection = false;

				/*接続コンポーネント名初期化*/
				m_strMapComponent1 = "";
				m_strMapComponent2 = "";

				break;
			}
		}
	}

	/* (非 Javadoc)
	 * @see jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer#findText(java.lang.String)
	 */
	public void findText(String strText) {
	}

	/**
	 * 解析内容を標準出力する.
	 */
	public void printContents() {
	}

	/**
	 * コンポーネントテーブルを取得する.
	 * @return コンポーネントテーブル
	 */
	public ComponentTable getComponentTable() {
		return m_pComponentTable;
	}

	/**
	 * 初期化する.
	 */
	void initialize() {
		/*変数テーブルインスタンス生成*/
		m_pComponentTable = new ComponentTable(CELLML_MODEL_NAME);
	}

}

