package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * インクリメント演算子クラス
 */
public class Math_inc extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_inc(String[] strAttr) {
		super("++", eMathOperator.MOP_INC, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_INC, strAttr);
	}
	
	public Math_inc() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_INC){
			throw new MathException("Math_inc","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return m_vecFactor.get(0).getValue() + 1.0;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_INC){
			throw new MathException("Math_inc","toLegalString","lack of operand");
		}

		return m_vecFactor.get(0).toLegalString() + "++";
	}

}
