package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子sinクラス
 */
public class Math_sin extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_sin(String[] strAttr) {
		super("sin", eMathOperator.MOP_SIN, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_SIN, strAttr);
	}

	public Math_sin() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_SIN){
			throw new MathException("Math_sin","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.sin(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_SIN){
			throw new MathException("Math_sin","toLegalString","lack of operand");
		}

		return "sin( " + m_vecFactor.get(0).toLegalString() + " )";
	}

}
