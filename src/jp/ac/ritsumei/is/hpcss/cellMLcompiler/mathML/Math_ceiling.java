package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子ceilクラス
 */
public class Math_ceiling extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_ceiling(String[] strAttr) {
		super("ceiling", eMathOperator.MOP_CEILING, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CEILING, strAttr);
	}

	public Math_ceiling() {
		this(null);
	}
	
	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CEILING){
			throw new MathException("Math_ceiling","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.ceil(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CEILING){
			throw new MathException("Math_ceiling","toLegalString","lack of operand");
		}

		return "ceiling( " + m_vecFactor.get(0).toLegalString() + " )";
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CEILING) {
			throw new MathException("Math_ceiling","toMathMLString","lack of operand");
		}

		return 	"<apply><ceiling/>" + "\n" +
				"\t" + m_vecFactor.get(0).toMathMLString() + "\n" +
			    "</apply>";
	}

}
