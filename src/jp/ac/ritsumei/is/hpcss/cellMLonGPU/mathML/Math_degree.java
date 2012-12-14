package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子diffクラス
 */
public class Math_degree extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_degree(String[] strAttr) {
		super("diff", eMathOperator.MOP_DEGREE, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DEGREE, strAttr);
	}
	
	public Math_degree() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		throw new MathException("Math_degree","calculate","can't calculate");
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*単項演算子*/
		if(m_vecFactor.size() == 1){
			return "^" + m_vecFactor.get(0).toLegalString();
		}
		/*例外*/
		else{
			throw new MathException("Math_degree","toLegalString","lack of operand");
		}
	}
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() == 1) {
			return 	"<degree>" + m_vecFactor.get(0).toMathMLString() + "</degree>";
		}
		/*例外*/
		else{
			throw new MathException("Math_degree","toMathMLString","lack of operand");
		}
	}

}
