package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子logクラス
 */
public class Math_log extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_log(String[] strAttr) {
		super("log", eMathOperator.MOP_LOG, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LOG, strAttr);
	}
	
	public Math_log() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LOG) {
			throw new MathException("Math_log","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.log(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LOG) {
			throw new MathException("Math_log","toLegalString","lack of operand");
		}

		return "log( " + m_vecFactor.get(0).toLegalString() + " )";
	}

}
