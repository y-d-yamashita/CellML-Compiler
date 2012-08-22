package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子bvarクラス
 */
public class Math_bvar extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_bvar(String[] strAttr) {
		super("bvar", eMathOperator.MOP_BVAR, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_BVAR, strAttr);
	}

	public Math_bvar() {
		this(null);
	}
	
	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		throw new MathException("Math_bvar","calculate","can't calculate");
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_BVAR){
			throw new MathException("Math_bvar","toLegalString","lack of operand");
		}

		return "( d" + m_vecFactor.get(0).toLegalString() + " )";
	}

}
