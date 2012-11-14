package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子floorクラス
 */
public class Math_floor extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_floor(String[] strAttr) {
		super("floor", eMathOperator.MOP_FLOOR, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_FLOOR, strAttr);
	}
	
	public Math_floor() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_FLOOR) {
			throw new MathException("Math_floor","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.floor(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_FLOOR) {
			throw new MathException("Math_floor","toLegalString","lack of operand");
		}

		return "floor( " + m_vecFactor.get(0).toLegalString() + " )";
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_FLOOR) {
			throw new MathException("Math_floor","toMathMLString","lack of operand");
		}

		return 	"<floor/>" + "\n" +
				"\t" + m_vecFactor.get(0).toMathMLString() + "\n";
	}

}
