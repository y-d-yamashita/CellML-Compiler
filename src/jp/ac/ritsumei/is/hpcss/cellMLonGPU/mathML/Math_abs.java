package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子ceilクラス
 */
public class Math_abs extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_abs(String[] strAttr) {
		super("abs", eMathOperator.MOP_ABS, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_ABS, strAttr);
	}

	public Math_abs() {
		this(null);
	}
	
	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_ABS){
			throw new MathException("Math_abs","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.abs(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_ABS){
			throw new MathException("Math_abs","toLegalString","lack of operand");
		}

		return "abs( " + m_vecFactor.get(0).toLegalString() + " )";
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_ABS) {
			throw new MathException("Math_abs","toMathMLString","lack of operand");
		}

		return 	"<abs/>" + "\n" +
				"\t" + m_vecFactor.get(0).toMathMLString() + "\n";
	}

}
