package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子timesクラス
 */
public class Math_times extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_times(String[] strAttr) {
		super("*", eMathOperator.MOP_TIMES, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TIMES, strAttr);
	}

	public Math_times() {
		this(null);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TIMES){
			throw new MathException("Math_times","calculate","lack of operand");
		}

		/*積算*/
		double dValueSum = 1.0;

		for (MathFactor it: m_vecFactor) {
			dValueSum *= it.getValue();
		}

		/*演算結果を返す*/
		return dValueSum;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*非演算子がない場合は例外*/
		if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TIMES) {
			throw new MathException("Math_times","toLegalString","lack of operand");
		}

		/*文字列を追加していく*/
		String strExpression = " ( ";
		
		for (int i=0; i < m_vecFactor.size(); i++) {

			/* *演算子を追加 */
//			if(it != m_vecFactor.firstElement()){
//				strExpression += " * ";
//			}
			
			strExpression += (m_vecFactor.get(i)).toLegalString();
			
			if(i < ( m_vecFactor.size() - 1)){
				strExpression += " * ";
			}

			/*項を追加*/
			
		}

		/*閉じ括弧を追加*/
		strExpression += " ) ";

		return strExpression;
	}
	
	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TIMES){
			throw new MathException("Math_times","toMathMLString","lack of operand");
		}
		
		/*文字列を追加していく*/
		String strExpression = "";
		
		for(int i=0; i < m_vecFactor.size(); i++) {
			
			if(i != 0){
				strExpression += "\n\t";
			}
			
			strExpression += (m_vecFactor.get(i)).toMathMLString();	
		}
			
		return 	"\t" + "<times/>" + "\n" +
					strExpression;
	}

}
