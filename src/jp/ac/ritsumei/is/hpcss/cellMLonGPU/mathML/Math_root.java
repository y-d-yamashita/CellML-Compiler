package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子rootクラス
 */
public class Math_root extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_root(String[] strAttr) {
		super("root", eMathOperator.MOP_ROOT, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_ROOT, strAttr);
	}
	
	public Math_root() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_ROOT){
			throw new MathException("Math_root","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return Math.sqrt(m_vecFactor.get(0).getValue());
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_ROOT){
			throw new MathException("Math_root","toLegalString","lack of operand");
		}

		return "sqrt( " + m_vecFactor.get(0).toLegalString() + " )";
	}

}
