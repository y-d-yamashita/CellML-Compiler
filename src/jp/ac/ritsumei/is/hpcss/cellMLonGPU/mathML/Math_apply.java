package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import java.util.HashMap;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子applyクラス
 */
public class Math_apply extends MathOperator {

	/*構造情報要素*/
	HashMap<String, String> m_hashExpInfo;
	
	
	public HashMap<String, String> get_hashExpInfo(){
		return this.m_hashExpInfo;
	}
	public void set_hashExpInfo(HashMap<String, String> info){
		this.m_hashExpInfo = info;
	}
	
	public void setExpInfo(HashMap<String, String> attrList){
		/*オペランドをベクタに追加*/
		m_hashExpInfo = attrList;
	}
	public int getExpNum(){
		/*オペランドをベクタに追加*/
		int num = Integer.parseInt(m_hashExpInfo.get("num"));
		return num;
	}
	public void addExpInfo(String name, String value){
		/*数式情報をHashMapに追加*/
		m_hashExpInfo.put(name, value);
	}
	public String getExpInfo(String name){
		return m_hashExpInfo.get(name);
	}
	
	
	/*構造情報要素*/
	HashMap<Integer, String> m_hashAttr;
	public void setAttrList(HashMap<Integer, String> attrList){
		/*オペランドをベクタに追加*/
		m_hashAttr = attrList;
	}
	public HashMap<Integer, String> get_hashAttr(){
		return this.m_hashAttr;
	}
	public void set_hashAttr(HashMap<Integer, String> attr){
		this.m_hashAttr = attr;
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
	
	/*-----Java文字列変換メソッド-----*/
	public String toLegalJavaString() throws MathException {

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY){
			throw new MathException("Math_apply","toLegalJavaString","lack of operand");
		}

		return m_vecFactor.get(0).toLegalJavaString();
	}

	/*-----Method for converting Expression to MathML-----*/
	public String toMathMLString() throws MathException {
//		System.out.println("make StructuredRecML MathML Output Start MathML_Apply-1.");
		String strExpression = "";

		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY){
			throw new MathException("Math_apply","toMathMLString","lack of operand");
		}
		
		String expinfo = "";
		if(m_hashExpInfo.size()>0){
			for(String key:m_hashExpInfo.keySet()){
//				System.out.println("make StructuredRecML MathML Output Start MathML_Apply-2.");
				expinfo += " " + key + " = " + "\"" + m_hashExpInfo.get(key) + "\"";
			}
		}
		
		String Attrinfo = "";
//		int loopnum = 0;
		if(m_hashAttr.size()>0){
			for(Integer lnum:m_hashAttr.keySet()){
				Attrinfo += " loop" + (lnum+1) + " = \"" + m_hashAttr.get(lnum) + "\"";
			}
		}
		
		strExpression = "<apply " + expinfo + Attrinfo + ">" + "\n" +	
 						"\t" + m_vecFactor.get(0).toMathMLString()+ "\n" +
 						"</apply>";
		return strExpression;
	}


}
