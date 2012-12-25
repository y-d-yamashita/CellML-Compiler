package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.HashMap;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;

/**
 * XML属性格納クラス.
 */
public class XMLAttribute {

	/**属性名と値文字列
	 * !! The old method for the XMLAttribute uses Vector<String> instead of hashmap */
	private HashMap<String, String> m_AttributeValue;

	/**
	 * XML属性格納インスタンスを作成する.
	 */
	public XMLAttribute(){
		m_AttributeValue = new HashMap<String, String>();
	}

	/**
	 * 属性を格納する.
	 * @param strAttribute 格納する属性
	 * @param strValue 格納する値
	 */
	public void addAttribute(String strAttribute, String strValue) {
		m_AttributeValue.put(strAttribute, strValue);
	}

	/**
	 * 属性を取得する.
	 * @param strAttribute 取得したい属性
	 * @return 引数に指定した属性の値
	 * @throws XMLException
	 */
	public String getValue(String strAttribute) throws XMLException {
		/*目的の属性を捜索*/
		if (m_AttributeValue.containsKey(strAttribute)) {
			return m_AttributeValue.get(strAttribute);
		}
		
		return null;
		//throw new XMLException("XMLAttribute","getValue","attribute not found");
	}

}
