package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子divideクラス
 */
public class Math_divide extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_divide(String[] strAttr) {
		super("/", eMathOperator.MOP_DIVIDE, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DIVIDE, strAttr);
	}
	
	public Math_divide() {
		this(null);
	}

	/*-----数式ツリー修復メソッド-----*/
	public void restoreFactor(int unRemovedFactorNum)
	{
		/*左辺値が削除されたとき*/
		if(unRemovedFactorNum == 0) {
		}
		/*右辺値が削除されたとき*/
		else {
		}
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DIVIDE){
			throw new MathException("Math_divide","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return m_vecFactor.get(0).getValue() / m_vecFactor.get(1).getValue();
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DIVIDE){
			throw new MathException("Math_divide","toLegalString","lack of operand");
		}

		return " ( " + m_vecFactor.get(0).toLegalString()
			+ " / " + m_vecFactor.get(1).toLegalString() + " ) ";
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DIVIDE) {
			throw new MathException("Math_divide","toMathMLString","lack of operand");
		}

		return 	"<divide/>" + "\n" +
				"\t" + m_vecFactor.get(0).toMathMLString() + "\n" +
				"\t" + m_vecFactor.get(1).toMathMLString() + "\n";
	}

}
