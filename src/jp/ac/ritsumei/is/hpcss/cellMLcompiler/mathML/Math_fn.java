package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子fnクラス
 */
public class Math_fn extends MathOperator {

	/*関数オペランド*/
	MathOperand m_pFuncOperand;	//こちらが関数名を持つオペランド
					//m_vecFactorが引数リストとなるので，注意！

	/*-----コンストラクタ-----*/
	public Math_fn(String[] strAttr) {
		super("", eMathOperator.MOP_FN, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_FN, strAttr);
		m_pFuncOperand = null;
	}
	
	public Math_fn() {
		this(null);
	}

	/*-----関数オペランド設定メソッド-----*/
	public void setFuncOperand(MathOperand pFuncOperand) {
		m_pFuncOperand = pFuncOperand;
	}

	/*-----引数リストベクタメソッド-----*/
	public Vector<MathFactor> getArgumentsVector() {
		return m_vecFactor;
	}

	/*-----数式複製メソッド-----*/
	public MathFactor createCopy() throws MathException {
		/*演算子の複製*/
		Math_fn newOperator = (Math_fn)MathFactory.createOperator(eMathOperator.MOP_FN);

		/*すべての子要素を複製*/
		for (MathFactor it: m_vecFactor) {
			newOperator.addFactor(it.createCopy());
		}

		/*関数オペランドの設定*/
		newOperator.setFuncOperand((MathOperand)m_pFuncOperand.createCopy());

		return newOperator;
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		throw new MathException("Math_fn","calculate","can't calculate function");
	}

	/*-----照合メソッド-----*/
	public boolean matches(MathOperand pOperand) {
		return m_pFuncOperand.matches(pOperand);
	}

	/*-----照合メソッド-----*/
	public boolean matches(Math_fn pFunction) {

		/*関数名の照合*/
		if(!m_pFuncOperand.matches(pFunction.m_pFuncOperand)) {
			return false;
		}

		/*すべての子要素との一致を調べる*/
		if (m_vecFactor.size() != pFunction.m_vecFactor.size()) {
			return false;
		}
		for (int i = 0; i < m_vecFactor.size(); i++) {
			if (!m_vecFactor.get(i).matches(pFunction.m_vecFactor.get(i))) {
				return false;
			}
		}

		return true;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*例外処理*/
		if(m_pFuncOperand == null){
			throw new MathException("Math_fn","toLegalString","have no function operand");
		}

		/*関数名を文字列に追加*/
		String strExpression = m_pFuncOperand.toLegalString();

		/*引数を追加していく*/
		strExpression += " ( ";

		for (MathFactor it: m_vecFactor) {

			/*コンマを追加*/
			if (it != m_vecFactor.firstElement()) {
				strExpression += " , ";
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

		/*例外処理*/
		if(m_pFuncOperand == null){
			throw new MathException("Math_fn","toMathMLString","have no function operand");
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

		return 	"<fn>" + "\n" +
					strExpression + "\n" +
			    "</fn>";
	}
}
