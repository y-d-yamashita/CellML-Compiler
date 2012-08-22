package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子cosクラス
 */
public class Math_cos extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_cos(String[] strAttr) {
		super("cos", eMathOperator.MOP_COS, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_COS, strAttr);
	}
	
	public Math_cos() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_COS){
			throw new MathException("Math_cos","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.cos(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_COS){
			throw new MathException("Math_cos","toLegalString","lack of operand");
		}

		return "cos( " + m_vecFactor.get(0).toLegalString() + " )";
	}

}
