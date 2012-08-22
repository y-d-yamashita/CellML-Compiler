package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子minusクラス
 */
public class Math_minus extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_minus(String[] strAttr) {
		super("-", eMathOperator.MOP_MINUS, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_MINUS, strAttr);
	}
	
	public Math_minus() {
		this(null);
	}

	/*-----数式ツリー修復メソッド-----*/
	public void restoreFactor(int unRemovedFactorNum)
	{
		/*左辺値が削除されたとき*/
		if(unRemovedFactorNum == 0) {
			/*そのまま単項演算子として利用*/
		}
		/*右辺値が削除されたとき*/
		else {
			/**/
		}
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*単項演算子*/
		if (m_vecFactor.size() == 1) {
			return -m_vecFactor.get(0).getValue();
		}
		/*2項演算子*/
		else if(m_vecFactor.size() == 2) {
			return m_vecFactor.get(0).getValue() - m_vecFactor.get(1).getValue();
		}
		/*例外*/
		else{
			throw new MathException("Math_minus","calculate","lack of operand");
		}
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*単項演算子*/
		if (m_vecFactor.size() == 1) {
			return " ( - " + m_vecFactor.get(0).toLegalString() + " ) ";
		}
		/*2項演算子*/
		else if (m_vecFactor.size() == 2) {
			return " ( " + m_vecFactor.get(0).toLegalString() + " - " +
				m_vecFactor.get(1).toLegalString() + " ) ";
		}
		/*例外*/
		else{
			throw new MathException("Math_minus","toLegalString","lack of operand");
		}
	}

}
