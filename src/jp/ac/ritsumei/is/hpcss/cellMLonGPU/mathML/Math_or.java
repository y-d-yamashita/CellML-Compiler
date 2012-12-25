package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子orクラス
 */
public class Math_or extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_or(String[] strAttr) {
		super("||", eMathOperator.MOP_OR, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_OR, strAttr);
	}
	
	public Math_or() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_OR){
			throw new MathException("Math_or","calculate","lack of operand");
		}

		/*左辺値・右辺値取得*/
		double dLeftValue = m_vecFactor.get(0).getValue();
		double dRightValue = m_vecFactor.get(1).getValue();

		/*左辺値の値を返す*/
		return (dLeftValue != 0 || dRightValue != 0) ? 1 : 0;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_OR){
			throw new MathException("Math_or","toLegalString","lack of operand");
		}

		/*文字列を追加していく*/
		String strExpression = " ( ";

		for(MathFactor it: m_vecFactor) {

			/* ||演算子を追加 */
			if(it != m_vecFactor.firstElement()){
				strExpression += " || ";
			}

			/*項を追加*/
			strExpression += it.toLegalString();
		}

		/*閉じ括弧を追加*/
		strExpression += " ) ";

		return strExpression;
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_OR){
			throw new MathException("Math_or","toMathMLString","lack of operand");
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

		return 	"<or/>" + "\n" +
					strExpression + "\n" ;
	}

}
