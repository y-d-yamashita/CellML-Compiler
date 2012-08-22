package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子powerクラス
 */
public class Math_power extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_power(String[] strAttr) {
		super("pow", eMathOperator.MOP_POWER, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_POWER, strAttr);
	}
	
	public Math_power() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_POWER){
			throw new MathException("Math_power","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.pow(m_vecFactor.get(0).getValue(), m_vecFactor.get(1).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_POWER){
			throw new MathException("Math_power","toLegalString","lack of operand");
		}

		return "pow( " + m_vecFactor.get(0).toLegalString()
			+ " , " + m_vecFactor.get(1).toLegalString() + " )";
	}

}
