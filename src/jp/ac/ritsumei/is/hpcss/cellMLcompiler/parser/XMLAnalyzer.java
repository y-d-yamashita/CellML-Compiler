package jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.XMLException;

/**
 * XML解析基底クラス.
 */
public abstract class XMLAnalyzer {

	/**
	 * XML解析インスタンスを作成する.
	 */
	public XMLAnalyzer() {
	}

	/**
	 * 開始タグを解析する.
	 * @param strTag 開始タグ名
	 * @param pXMLAttr 属性クラスインスタンス
	 * @throws MathException
	 * @throws XMLException
	 * @throws RelMLException
	 * @throws CellMLException
	 * @throws TecMLException
	 * @throws RecMLException 
	 */
	abstract public void findTagStart(String strTag, XMLAttribute pXMLAttr)
	throws MathException, XMLException, RelMLException, CellMLException, TecMLException, RecMLException;

	/**
	 * 終了タグを解析する.
	 * @param strTag 終了タグ名
	 * @throws MathException
	 * @throws RelMLException
	 * @throws CellMLException
	 */
	abstract public void findTagEnd(String strTag)
	throws MathException, RelMLException, CellMLException, RecMLException;

	/**
	 * 文字列を解析する.
	 * @param strText 切り出された文字列
	 * @throws MathException
	 * @throws CellMLException
	 * @throws TableException
	 */
	abstract public void findText(String strText)
	throws MathException, CellMLException, TableException;
}
