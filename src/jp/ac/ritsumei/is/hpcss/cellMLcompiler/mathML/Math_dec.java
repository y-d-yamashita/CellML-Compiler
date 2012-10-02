package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * デクリメント演算子クラス
 */
public class Math_dec extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_dec(String[] strAttr) {
		super("--", eMathOperator.MOP_DEC, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DEC, strAttr);
	}
	
	public Math_dec() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DEC){
			throw new MathException("Math_dec","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return m_vecFactor.get(0).getValue() - 1.0;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DEC){
			throw new MathException("Math_dec","toLegalString","lack of operand");
		}

		return m_vecFactor.get(0).toLegalString() + "--";
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DEC) {
			throw new MathException("Math_dec","toMathMLString","lack of operand");
		}

		return 	"--deprecated" + "\n";
	}

}
