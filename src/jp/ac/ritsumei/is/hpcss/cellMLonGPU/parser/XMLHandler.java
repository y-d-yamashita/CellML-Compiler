package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XMLパーサクラス.
 */
public class XMLHandler extends DefaultHandler {

	/**XML解析クラスインスタンス*/
	XMLAnalyzer m_pAnalyzer;

	/**ソース位置*/
	private Locator locator;

	/**
	 * XMLパーサインスタンスを作成する.
	 * @param pAnalyzer
	 */
	public XMLHandler(XMLAnalyzer pAnalyzer) {
		m_pAnalyzer = pAnalyzer;
	}

	/* (非 Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator) {
		// ソース位置を覚えておく
		// エラー発生時に使用する
		this.locator = locator;
	}

	/* (非 Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri,String localName, String qName, Attributes rAttr)
	throws SAXException {
		/*属性サイズ取得*/
		int nAttrSize = rAttr.getLength();
//		System.out.println("startElement(" +localName+ ", "+qName+", attrs:"+nAttrSize+ ")");

		/*XMLAttributeインスタンスに格納*/
		XMLAttribute pXMLAttr = new XMLAttribute();

		for (int i = 0; i < nAttrSize; i++) {
			/*名前と値を取得*/
			String pAttrName = rAttr.getLocalName(i);
			String pAttrValue = rAttr.getValue(pAttrName);

			/*不適切な属性*/
			if (pAttrName == null || pAttrValue == null) {
				continue;
			}

			/*XMLAttributeインスタンスに追加*/
			pXMLAttr.addAttribute(pAttrName, pAttrValue);
//			System.out.println("\t" +pAttrName+ "\t"+pAttrValue);
		}

		/*解析クラスに投げる*/
		try {
			m_pAnalyzer.findTagStart(localName, pXMLAttr);
		} catch (Exception e) {
			throw new SAXException(getLocatorMsg(), e);
		}

	}

	/* (非 Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
	throws SAXException {
//		System.out.println("endElement(" +localName+ ", "+qName+ ")");
		/*終了タグ名取得*/
		String pszElementName = localName;

		/*解析クラスに投げる*/
		try {
			m_pAnalyzer.findTagEnd(pszElementName);
		} catch (Exception e) {
			throw new SAXException(getLocatorMsg(), e);
		}
	}

	/* (非 Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
	throws SAXException {
		/*バッファより文字列取得*/
		String pszContent = new String(ch, start, length).trim();

		/*解析クラスに投げる*/
		if (pszContent.length() > 0) {
			try {
//				System.out.println("characters(" +pszContent+ ")");
				m_pAnalyzer.findText(pszContent);
			} catch (Exception e) {
//				System.err.println("Exception characters\t" + start + "\t" + length);
//				System.err.println("characters(" +new String(ch, start, length)+ ")");
				throw new SAXException(getLocatorMsg(), e);
			}
		}
	}

	/**
	 * ソース位置を表示可能文字列にして返す.
	 * @return ソース位置
	 */
	private String getLocatorMsg() {
		String msg = "";
        String systemId = locator.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1) {
                msg = systemId.substring(index + 1);
            } else {
            	msg = systemId;
            }
        }
        msg += ":" + locator.getLineNumber() + ":" + locator.getColumnNumber();

		return msg;
	}
}
