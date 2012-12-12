package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML;

import java.util.HashMap;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition;

/**
 * MathML演算子eqクラス
 */
public class Math_eq extends MathOperator {

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
	public Math_eq(String[] strAttr) {
		super("=", eMathOperator.MOP_EQ, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_EQ, strAttr);
		m_hashAttr = new HashMap<Integer, String>();
		m_hashExpInfo = new HashMap<String, String>();
	}
	
	public Math_eq() {
		this(null);
		m_hashAttr = new HashMap<Integer, String>();
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != 2) {
			throw new MathException("Math_eq","calculate","lack of operand");
		}

		/*左辺値・右辺値取得*/
		double dLeftValue = m_vecFactor.get(0).getValue();
		double dRightValue = m_vecFactor.get(1).getValue();

		/*左辺値の値を返す*/
		return (dLeftValue == dRightValue) ? 1 : 0;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != 2) {
			throw new MathException("Math_eq","toLegalString","lack of operand");
		}

		return m_vecFactor.get(0).toLegalString()
			+ " == " + m_vecFactor.get(1).toLegalString();
	}
	
	/**
	 * Change to 0 == F(x) format
	 * @return
	 * @throws MathException 
	 */
//	@Override
//	public MathFactor toZeroEqualFormat() throws MathException{
//		/*被演算子の個数チェック*/
//		if(m_vecFactor.size() != 2) {
//			throw new MathException("Math_eq","toLegalString","lack of operand");
//		}
//		if(m_vecFactor.get(0).matches(eMathMLClassification.MML_OPERAND)&&
//				((MathOperand)m_vecFactor.get(0)).matches(eMathOperand.MOPD_CN)&&
//				((Math_cn)m_vecFactor.get(0)).getValue()==0)
//				return this;
//		
//		MathOperand zero = new Math_cn("0.0");
//		MathOperator apply = new Math_apply();
//		MathOperator minus = new Math_minus();
//		apply.addFactor(minus);
//		minus.addFactor(m_vecFactor.get(1));
//		minus.addFactor(m_vecFactor.get(0));
//		this.m_vecFactor.set(0, zero);
//		this.m_vecFactor.set(1,apply);
//		
//		return this;
//
//	}

	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() != 2) {
			throw new MathException("Math_eq","toMathMLString","lack of operand");
		}

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY){
			throw new MathException("Math_apply","toMathMLString","lack of operand");
		}
		
		String expinfo = "";
		if(m_hashExpInfo.size()>0){
			for(String key:m_hashExpInfo.keySet()){
				expinfo += " " + key + "=" + "\"" + m_hashExpInfo.get(key) + "\"";
			}
		}
		
		String Attrinfo = "";
		int loopnum = 0;
		if(m_hashAttr.size()>0){
			int flag = m_hashAttr.size();
			while(flag>0){
				if(m_hashAttr.containsKey(loopnum)){
					Attrinfo += " loop" + (loopnum+1) + " = \"" + m_hashAttr.get(loopnum) + "\"";
					flag--;
					loopnum++;
				}
			}
		}
		
		return 	"<apply "  + expinfo + Attrinfo + "><eq/>" + "\n" +
				"\t" + m_vecFactor.get(0).toMathMLString() + "\n" +
				"\t" + m_vecFactor.get(1).toMathMLString() + "\n" +
			     "</apply>";
	}
}
