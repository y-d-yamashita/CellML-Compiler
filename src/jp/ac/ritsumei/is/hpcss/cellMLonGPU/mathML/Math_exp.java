package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子expクラス
 */
public class Math_exp extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_exp(String[] strAttr) {
		super("exp", eMathOperator.MOP_EXP, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_EXP, strAttr);
	}
	
	public Math_exp() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_EXP) {
			throw new MathException("Math_exp","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.exp(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_EXP) {
			throw new MathException("Math_exp","toLegalString","lack of operand");
		}

		return "exp( " + m_vecFactor.get(0).toLegalString() + " )";
	}

}
