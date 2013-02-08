package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import java.util.HashMap;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子applyクラス
 */
public class Math_apply extends MathOperator {

	/*構造情報要素*/
	HashMap<String, String> m_hashExpInfo;
	public void setExpInfo(HashMap<String, String> attrList){
		/*オペランドをベクタに追加*/
		m_hashExpInfo = attrList;
	}
	public int getExpNum(){
		/*オペランドをベクタに追加*/
		int num = Integer.parseInt(m_hashExpInfo.get("num"));
		return num;
	}
	
	/*構造情報要素*/
	HashMap<Integer, String> m_hashAttr;
	public void setAttrList(HashMap<Integer, String> attrList){
		/*オペランドをベクタに追加*/
		m_hashAttr = attrList;
	}

	/*-----コンストラクタ-----*/
	public Math_apply(String[] strAttr) {
		super("", eMathOperator.MOP_APPLY, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY, strAttr);
		m_hashAttr = new HashMap<Integer, String>();
		m_hashExpInfo = new HashMap<String, String>();
	}

	public Math_apply() {
		this(null);
		m_hashAttr = new HashMap<Integer, String>();
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY){
			throw new MathException("Math_apply","calculate","lack of operand");
		}

		/*演算結果を返す*/
		return m_vecFactor.get(0).getValue();
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY){
			throw new MathException("Math_apply","toLegalString","lack of operand");
		}

		return m_vecFactor.get(0).toLegalString();
	}

	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY){
			throw new MathException("Math_apply","toMathMLString","lack of operand");
		}
		
		 return 	m_vecFactor.get(0).toMathMLString();
	}

}
