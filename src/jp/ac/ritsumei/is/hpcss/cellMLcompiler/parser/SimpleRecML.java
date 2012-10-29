package jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser;

import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;

public class SimpleRecML {
	
	/*変数取得HashMap*/
	private HashMap<Integer, Math_ci> m_HashMapSimpleRecurList;
	public HashMap<Integer, Math_ci> getM_HashMapRecurVar() {
		return m_HashMapSimpleRecurList;
	}
	public void setM_HashMapRecurVar(int num, Math_ci variable){
		m_HashMapSimpleRecurList.put(num, variable);
	}
	
	private HashMap<Integer, Math_ci> m_HashMapSimpleArithList;
	public HashMap<Integer, Math_ci> getM_HashMapArithVar() {
		return m_HashMapSimpleArithList;
	}
	public void setM_HashMapArithVar(int num, Math_ci variable) {
		m_HashMapSimpleArithList.put(num, variable);
	}
	
	private HashMap<Integer, Math_ci> m_HashMapSimpleOutputList;
	public HashMap<Integer, Math_ci> getM_HashMapOutputVar() {
		return m_HashMapSimpleOutputList;
	}
	public void setM_HashMapOutputVar(int num, Math_ci variable) {
		m_HashMapSimpleOutputList.put(num, variable);
	}
	
	/*-----コンストラクタ-----*/
	public SimpleRecML() {
		HashMap<Integer, Math_ci> SimpleRecurVarHM = new HashMap<Integer, Math_ci>();
		m_HashMapSimpleRecurList = SimpleRecurVarHM;

		HashMap<Integer, Math_ci> SimpleArithVarHM = new HashMap<Integer, Math_ci>();
		m_HashMapSimpleArithList = SimpleArithVarHM;
		
		HashMap<Integer, Math_ci> SimpleOutputVarHM = new HashMap<Integer, Math_ci>();
		m_HashMapSimpleOutputList = SimpleOutputVarHM;
		
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

	public void printHashMap(){
		for(int i=0; i<m_HashMapSimpleRecurList.size(); i++){
			try {
				System.out.println("Recurvar[" +i +"]:" + m_HashMapSimpleRecurList.get(i).toLegalString());
			} catch (MathException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		for(int i=0; i<m_HashMapSimpleArithList.size(); i++){
			try {
				System.out.println("Arithvar[" +i +"]:" + m_HashMapSimpleArithList.get(i).toLegalString());
			} catch (MathException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		for(int i=0; i<m_HashMapSimpleOutputList.size(); i++){
			try {
				System.out.println("Outputvar[" +i +"]:" + m_HashMapSimpleOutputList.get(i).toLegalString());
			} catch (MathException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
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
}
