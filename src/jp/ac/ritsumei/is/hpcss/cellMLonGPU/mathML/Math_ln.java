package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子lnクラス
 */
public class Math_ln extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_ln(String[] strAttr) {
		super("ln", eMathOperator.MOP_LN, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LN, strAttr);
	}
	
	public Math_ln() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LN) {
			throw new MathException("Math_ln","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.log(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LN) {
			throw new MathException("Math_ln","toLegalString","lack of operand");
		}

		return "log( " + m_vecFactor.get(0).toLegalString() + " )";
	}
	
	/*-----Java文字列変換メソッド-----*/
	public String toLegalJavaString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LN) {
			throw new MathException("Math_ln","toLegalJavaString","lack of operand");
		}

		return "Math.log( " + m_vecFactor.get(0).toLegalJavaString() + " )";
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LN) {
			throw new MathException("Math_ln","toMathMLString","lack of operand");
		}

		return 	"<ln/>" + "\n" +
				"\t" + m_vecFactor.get(0).toMathMLString() + "\n";
	}

}
