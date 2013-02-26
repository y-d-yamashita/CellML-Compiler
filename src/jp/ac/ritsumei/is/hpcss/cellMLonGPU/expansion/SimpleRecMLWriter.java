package jp.ac.ritsumei.is.hpcss.cellMLonGPU.expansion;

import java.util.ArrayList;
import java.util.HashMap;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecML;


public class SimpleRecMLWriter {


	/**
	 * 逐次プログラム構文生成クラス
	 * @author m-ara
	 */

	
	/*共通変数*/
	
	protected SimpleRecML pSimpleRecML;
	protected static ArrayList<String> alStruRecml;

	
	/*-----コンストラクタ-----*/
	public SimpleRecMLWriter(SimpleRecML simpleRecML) {
		pSimpleRecML = simpleRecML;
	}
	
	

	public void outPutSimpleRecML() throws MathException {
		for(String str:alStruRecml){
			System.out.println(str);
		}
	}
	
	public ArrayList<String> outPutLoopIndex() {
		ArrayList<String> out = new ArrayList<String>();
		HashMap<Integer, String> il = pSimpleRecML.getM_HashMapIndexList();
		String str = "";
		for(Integer key:il.keySet()){
			str = str.concat("<loopindex num=\"" + key  + "\" name=\"" + il.get(key) +"\" />");
			str = str.concat("\n");
		}
		out.add(str);
		return out;
	}
	
	public ArrayList<String> outPutVariable() throws MathException {
		ArrayList<String> out = new ArrayList<String>();
		
		HashMap<Math_ci, Integer> rv = pSimpleRecML.getM_HashMapRecurVar();		
		for(Math_ci key:rv.keySet()){
			String str = "<variable name=\"" + key.getName() + "\" type=\"recurvar\" " ;
			if(key.isInitialized()){
				str += "initial_value=\"" + key.getValue() + "\" ";
			}
			str += "/>";
			out.add(str);
		}
		
		HashMap<Math_ci, Integer> av = pSimpleRecML.getM_HashMapArithVar();
		for(Math_ci key:av.keySet()){
			String str = "<variable name=\"" + key.getName() + "\" type=\"arithvar\" " ;
			if(key.isInitialized()){
				str += "initial_value=\"" + key.getValue() + "\" ";
			}
			str += "/>";
			out.add(str);
		}

		HashMap<Math_ci, Integer> cv = pSimpleRecML.getM_HashMapConstVar();
		for(Math_ci key:cv.keySet()){
			String str = "<variable name=\"" + key.getName() + "\" type=\"constvar\" " ;
			if(key.isInitialized()){
				str += "initial_value=\"" + key.getValue() + "\" ";
			}
			str += "/>";
			out.add(str);
		}
		
		HashMap<Math_ci, Integer> sv = pSimpleRecML.getM_HashMapStepVar();
		for(Math_ci key:sv.keySet()){
			String str = "<variable name=\"" + key.getName() + "\" type=\"stepvar\" " ;
			if(key.isInitialized()){
				str += "initial_value=\"" + key.getValue() + "\" ";
			}
			str += "/>";
			out.add(str);
		}
		
		
		return out;

	}
	
	public void createSimpleRecML() throws MathException{
		ArrayList<String> out = new ArrayList<String>();
		
		out.add("<recml>");
		out.add("\n");
		
		ArrayList<String> li = this.outPutLoopIndex();
		out.addAll(li);
		out.add("\n");
		
		//variable宣言
		ArrayList<String> var = this.outPutVariable();
		out.addAll(var);
		out.add("\n");
		
		out.add("<math >");
		out.addAll(this.getMathML());
		out.add("</math>");
		out.add("</recml>");

		this.setSimpleRecml(out);
	}
	
	public void setSimpleRecml(ArrayList<String> al) throws MathException {
		alStruRecml = al;
	}
	
	private ArrayList<String> getMathML() throws MathException {
		return pSimpleRecML.getMathml();
	}
	
	@SuppressWarnings("static-access")
	public ArrayList<String> getSimpleRecML(){
		return this.alStruRecml;
	}


}
