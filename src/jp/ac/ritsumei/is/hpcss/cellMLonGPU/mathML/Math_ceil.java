package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子ceilクラス
 */
public class Math_ceil extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_ceil(String[] strAttr) {
		super("ceil", eMathOperator.MOP_CEIL, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CEIL, strAttr);
	}

	public Math_ceil() {
		this(null);
	}
	
	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CEIL){
			throw new MathException("Math_ceil","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.ceil(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CEIL){
			throw new MathException("Math_ceil","toLegalString","lack of operand");
		}

		return "ceil( " + m_vecFactor.get(0).toLegalString() + " )";
	}

	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CEIL) {
			throw new MathException("Math_ceil","toMathMLString","lack of operand");
		}

		return 	"<ceiling/>" + "\n" +
				"\t" + m_vecFactor.get(0).toMathMLString() + "\n";
	}
}
