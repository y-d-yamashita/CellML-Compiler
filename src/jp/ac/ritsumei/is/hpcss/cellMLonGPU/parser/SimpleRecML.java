package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;

public class SimpleRecML{
	
	/*変数取得HashMap*/
	private HashMap<Math_ci, Integer> m_HashMapRecurList;
	public HashMap<Math_ci, Integer> getM_HashMapRecurVar() {
		return m_HashMapRecurList;
	}
	public void setM_HashMapRecurVar(Math_ci variable , int num){
		m_HashMapRecurList.put(variable, num);
	}
	
	private HashMap<Math_ci, Integer> m_HashMapArithList;
	public HashMap<Math_ci, Integer> getM_HashMapArithVar() {
		return m_HashMapArithList;
	}
	public void setM_HashMapArithVar(Math_ci variable , int num) {
		m_HashMapArithList.put(variable, num);
	}
	
	private HashMap<Math_ci, Integer> m_HashMapConstList;
	public HashMap<Math_ci, Integer> getM_HashMapConstVar() {
		return m_HashMapConstList;
	}
	public void setM_HashMapConstVar(Math_ci variable , int num) {
		m_HashMapConstList.put(variable, num);
	}
	
	private HashMap<Math_ci, Integer> m_HashMapStepVarList;
	public HashMap<Math_ci, Integer> getM_HashMapStepVar() {
		return m_HashMapStepVarList;
	}
	public void setM_HashMapStepVar(Math_ci variable , int num) {
		m_HashMapStepVarList.put(variable, num);
	}

	/*Loop構造情報*/
	public HashMap<Integer, String> indexHashMapList;
	public HashMap<Integer, String> getM_HashMapIndexList() {
		return  indexHashMapList;
	}
	public void setIndexHashMap(int LoopNumber, String varName) {
		indexHashMapList.put(LoopNumber, varName);
	}
	
	
	/*-----コンストラクタ-----*/
	public SimpleRecML() {
		m_HashMapRecurList = new HashMap<Math_ci, Integer>();
		m_HashMapArithList = new HashMap<Math_ci, Integer>();
		m_HashMapConstList = new HashMap<Math_ci, Integer>();
		m_HashMapStepVarList = new HashMap<Math_ci, Integer>();
		indexHashMapList = new HashMap<Integer, String>();
	}
	
	/**分類後非微分数式ベクタ*/
	Vector<MathExpression> m_vecNonDiffExpression = new Vector<MathExpression>();
	public Vector<MathExpression> getM_vecNonDiffExpression(){
		return m_vecNonDiffExpression;
	}
	
	/**分類後通常変数ベクタ*/
	Vector<Math_ci> m_vecArithVar = new Vector<Math_ci>();
	public Vector<Math_ci> getM_vecArithVar(){
		return m_vecArithVar;
	}
	
	/**数式ベクタ*/
	Vector<MathExpression> m_vecMathExpression = new Vector<MathExpression>();
	
	public void setMathExpression(Vector<MathExpression> pExpression){
		this.m_vecMathExpression = pExpression;
	}

	
	/**
	 * 数式を標準出力する.
	 * @throws MathException
	 */
	public void printExpressions() throws MathException {
		/*すべての数式を出力*/
		for (MathExpression it: m_vecMathExpression) {

			/*数式標準出力*/
			System.out.println(it.toLegalString());


		}
	}
	
	/**
	 * 数式をmathmlを取得する.
	 * @return 
	 * @throws MathException
	 */
	/*-----解析結果取得メソッド-----*/
	public ArrayList<String> getMathml() throws MathException {
		
		ArrayList<String> al = new ArrayList<String>();
		al.add("<!--    equation    -->");
		/*すべての数式を出力*/
		for (MathExpression it: m_vecMathExpression) {
			al.add(it.toMathMLString());
			al.add("\n");
		}
		return al;
		
	}
	
//	========================================================
	//printContents
	// mathml出力メソッド
	//
	//========================================================
	/*-----解析結果表示メソッド-----*/
	public void printMathml() throws MathException {
		/*開始線出力*/
		System.out.println("<!-------------------------------equation------------------------------->");

		/*すべての数式を出力*/
		for (MathExpression it: m_vecMathExpression) {

			/*数式標準出力*/
			System.out.println(it.toMathMLString());
			System.out.println();

		}
	}
	
}
