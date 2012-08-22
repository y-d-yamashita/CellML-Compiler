package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子otherwiseクラス
 */
public class Math_otherwise extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_otherwise(String[] strAttr) {
		super("", eMathOperator.MOP_OTHERWISE, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_OTHERWISE, strAttr);
	}
	
	public Math_otherwise() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		throw new MathException("Math_otherwise","calculate","can't calculate");
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if (m_vecFactor.size() != 1){
			throw new MathException("Math_otherwise","toLegalString","lack of operand");
		}

		return m_vecFactor.get(0).toLegalString();
	}

}
