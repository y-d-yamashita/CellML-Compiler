package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子selectorクラス
 */
public class Math_condition extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_condition(String[] strAttr) {
		super("", eMathOperator.MOP_CONDITION, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CONDITION, strAttr);
	}
	
	public Math_condition() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CONDITION){
			throw new MathException("Math_condition","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return m_vecFactor.get(0).getValue();
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_CONDITION){
			throw new MathException("Math_condition","toLegalString","lack of operand");
		}
		/* Works the same as apply */
		return m_vecFactor.get(0).toLegalString();
		
	}

}
