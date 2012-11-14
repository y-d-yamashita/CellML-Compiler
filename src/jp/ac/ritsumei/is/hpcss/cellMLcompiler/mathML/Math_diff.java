package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子diffクラス
 */
public class Math_diff extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_diff(String[] strAttr) {
		super("diff", eMathOperator.MOP_DIFF, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DIFF, strAttr);
	}
	
	public Math_diff() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		throw new MathException("Math_diff","calculate","can't calculate");
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*単項演算子*/
		if(m_vecFactor.size() == 1){
			return " ( " + m_vecFactor.get(0).toLegalString() + " ' ) ";
		}
		/*2項演算子*/
		else if(m_vecFactor.size() == 2){
			return " ( d" + m_vecFactor.get(1).toLegalString() + " / " +
				m_vecFactor.get(0).toLegalString() + " ) ";
		}
		/*例外*/
		else{
			throw new MathException("Math_diff","toLegalString","lack of operand");
		}
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_DIFF){
			throw new MathException("Math_diff","toMathMLString","lack of operand");
		}

		/*文字列を追加していく*/
		String strExpression = "   ";

		for(MathFactor it: m_vecFactor) {

			/* &&演算子を追加 */
			if(it != m_vecFactor.firstElement()){
				strExpression += "\n\t";
			}

			/*項を追加*/
			strExpression += it.toMathMLString();
		}

		return 	"<diff/>" + "\n" +
					strExpression + "\n";
	}

}
