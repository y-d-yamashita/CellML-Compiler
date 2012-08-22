package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子pieceクラス
 */
public class Math_piece extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_piece(String[] strAttr) {
		super("", eMathOperator.MOP_PIECE, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_PIECE, strAttr);
	}
	
	public Math_piece() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		throw new MathException("Math_piece","calculate","can't calculate");
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if (m_vecFactor.size() != 2){
			throw new MathException("Math_piece","toLegalString","lack of operand");
		}

		return m_vecFactor.get(1).toLegalString()
			+ " ? " + m_vecFactor.get(0).toLegalString();
	}

}
