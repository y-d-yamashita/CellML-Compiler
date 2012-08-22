package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子diffクラス
 */
public class Math_laplacian extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_laplacian(String[] strAttr) {
		super("diff", eMathOperator.MOP_LAPLACIAN, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LAPLACIAN, strAttr);
	}
	
	public Math_laplacian() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		throw new MathException("Math_laplacian","calculate","can't calculate");
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*単項演算子*/
		if(m_vecFactor.size() == 1){
			return " Del2 " + m_vecFactor.get(0).toLegalString() + " ";
		}
		/*例外*/
		else{
			throw new MathException("Math_laplacian","toLegalString","lack of operand");
		}
	}

}
