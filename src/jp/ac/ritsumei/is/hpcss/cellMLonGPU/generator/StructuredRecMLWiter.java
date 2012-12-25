package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.Exception;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure.RelationPattern;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_assign;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_eq;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_fn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl.eControlKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCondition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.CommonProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.CudaProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.ProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.SimpleProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLVariableAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 逐次プログラム構文生成クラス
 */
public class StructuredRecMLWiter{

	//========================================================
	//DEFINE
	//========================================================

	/*共通変数*/

	protected SimpleRecMLAnalyzer pSimpleRecMLAnalyzer;
	protected ArrayList<String> alStruRecml;

	
	/*-----コンストラクタ-----*/
	public StructuredRecMLWiter(SimpleRecMLAnalyzer simpleRecMLAnalyzer) throws MathException{
		pSimpleRecMLAnalyzer = simpleRecMLAnalyzer;
	}

	
	public static void main(String[] args) throws MathException {
//	public static void main() throws MathException {
		
		SimpleRecMLAnalyzer simpleRecMLAnalyzer = new SimpleRecMLAnalyzer();
		
		String xml=
				//"./model/recml/RecMLSample/FHN_FTCS_simple_2x3x3.recml"
				//"./model/recml/RecMLSample/ArbitraryModel_1D_simple.recml"
				//"./model/recml/RecMLSample/ArbitraryModel_1D_simple_v2_yamashita.recml"
//				"./model/recml/SimpleKawabataTestSample/SimpleRecMLSample001.recml"
//				"./model/recml/SimpleKawabataTestSample/SimpleRecMLSample002.recml"
//				"./model/recml/SimpleRecMLSample/SimpleKawabataTestSample/SimpleRecMLSample001.recml"
				"./model/recml/SimpleRecMLSample/SimpleKawabataTestSample/SimpleRecMLSample002.recml"

		;
		
		XMLReader parser = null;
		
		try {
			parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		} catch (SAXException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			parser.setProperty("http://apache.org/xml/properties/input-buffer-size",
			new Integer(16 * 0x1000));
		} catch (SAXNotRecognizedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (SAXNotSupportedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		XMLHandler handler = new XMLHandler(simpleRecMLAnalyzer);
		parser.setContentHandler(handler);
		try {
//			parser.parse(args[0]);
			parser.parse(xml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
		//---------------------------------------------------
		//目的プログラム生成
		//---------------------------------------------------

		simpleRecMLAnalyzer.analysisForOutPutStrRec(simpleRecMLAnalyzer);
		simpleRecMLAnalyzer.printContents();
		StructuredRecMLWiter sr = new StructuredRecMLWiter(simpleRecMLAnalyzer);
		sr.createStrRecml();
		sr.outPutStrRecml();
	}

	public void setStrRecml(ArrayList<String> al) throws MathException {
		alStruRecml = al;
	}
	
	public ArrayList<String> getStrRecml() throws MathException {
		return alStruRecml;
	}
	
	public void createStrRecml() throws MathException {
		ArrayList<String> out = new ArrayList<String>();

		out.add("<recml>");
		out.add("\n");
		
		ArrayList<String> li = this.outPutLoopIndex();
		out.addAll(li);
		out.add("\n");
		
		ArrayList<String> ls = this.outPutLoopStructure();
		out.addAll(ls);
		out.add("\n");
		
		ArrayList<String> var = this.outPutVariable();
		out.addAll(var);
		out.add("\n");
		
		ArrayList<String> dgr = this.outPutDepGraph();
		out.addAll(dgr);
		out.add("\n");
		
		
		ArrayList<String> se = this.outPutSimulEqu();
		out.addAll(se);
		out.add("\n");
		
		ArrayList<String> rvl = this.outPutRefVariableList();
		out.addAll(rvl);
		out.add("\n");
		
		out.add("<math>");
		out.addAll(this.getMathML());
		out.add("</math>");
		out.add("</recml>");

		this.setStrRecml(out);
	}
	
	public void outPutStrRecml() throws MathException {
		for(String str:alStruRecml){
			System.out.println(str);
		}
	}


	public ArrayList<String> outPutLoopIndex() {
		ArrayList<String> out = new ArrayList<String>();
		HashMap<Integer, String> il = pSimpleRecMLAnalyzer.getM_HashMapIndexList();
		String str = "";
		for(Integer key:il.keySet()){
			str = str.concat("<loopindex num=\"" + key  + "\" name=\"" + il.get(key) +"\" />");
			str = str.concat("\n");
		}
		out.add(str);
		return out;
	}

	
	public ArrayList<String> outPutLoopStructure() {
		ArrayList<String> out = new ArrayList<String>();
		String str = "";
		ArrayList<RelationPattern> rp = pSimpleRecMLAnalyzer.getM_LoopStrucuture();
		
		//loopが1つの場合
		if(rp.size() == 1){
			if(rp.get(0).Child_name == -1 && rp.get(0).Attribute_name.length() == 0){
				str = str.concat("<loopstruct num="+ "\""+rp.get(0).Parent_name+ "\""+">\n");
			}
		}else{
			
			//rootの探索
			ArrayList<Integer> root_num = new ArrayList<Integer>();
			for(int i=0;i<rp.size();i++){
				//各親要素の名前について子要素の中を探索し、見つからなければrootである
				int flag=0;
				for(int j=0;j<rp.size();j++){
					if(rp.get(i).Parent_name.equals(rp.get(j).Child_name)){
						flag=1;
					}
				}
				if(flag==0)	root_num.add(i);//rootとなる関係情報を記録していく
			}
			
			int tab_count=2;//まず2回tabが入った状態で子要素は呼ばれる。
			str = str.concat("<loopstruct num="+ "\""+rp.get(root_num.get(0)).Parent_name+"\""+">\n");
			for(int i=0;i<root_num.size();i++){
				str = str.concat("\t"+"<position name="+ "\""+rp.get(root_num.get(i)).Attribute_name+ "\""+">\n");
				int p_flag=0;
				for(int j=0;j<rp.size();j++){
					if(rp.get(i).Child_name.equals(rp.get(j).Parent_name)){
						p_flag=1;//親になっているフラグ
					}
				}
				if(p_flag==1)str = str.concat("\t\t"+"<loopstruct num="+ "\""+rp.get(root_num.get(i)).Child_name+ "\""+">\n");
				else str = str.concat("\t\t"+"<loopstruct num="+ "\""+rp.get(root_num.get(i)).Child_name+ "\""+"/>\n");
				str = str.concat(call_child(rp, rp.get(root_num.get(i)).Child_name, tab_count));
			}
			str = str.concat("</loopstruct>\n");
		}
		
		out.add(str);
		return out;
	}

	public String call_child(ArrayList<RelationPattern> LoopStructure,Integer My_name, int tab_count){
		//子要素の名前を受け取り、親になっていれば子要素を表示する動作を再帰的に行う。
		String out = "";
		int flag=0;
		
		for(int i=0;i<LoopStructure.size();i++){
			if(My_name.equals(LoopStructure.get(i).Parent_name)){
				if(flag==0)tab_count++;
				for(int j=0;j<tab_count;j++) out = out.concat("\t");
				out = out.concat("<position name="+ "\""+LoopStructure.get(i).Attribute_name+ "\""+">\n");
				
				tab_count++;
				for(int j=0;j<tab_count;j++) out = out.concat("\t");
				int p_flag=0;
				for(int j=0;j<LoopStructure.size();j++){
					if(LoopStructure.get(i).Child_name.equals(LoopStructure.get(j).Parent_name)){
						p_flag=1;//親になっているフラグ
					}
				}
				if(p_flag==1)out = out.concat("<loopstruct num="+ "\""+LoopStructure.get(i).Child_name+ "\""+">\n");
				else out = out.concat("<loopstruct num="+ "\""+LoopStructure.get(i).Child_name+ "\""+"/>\n");
				out = out.concat(call_child(LoopStructure,LoopStructure.get(i).Child_name, tab_count));
				tab_count--;
				flag=1;//子要素ありのフラグ
			}
		}
		if(flag==1){
			//子要素を持つ場合の処理（tab++が必要）
			tab_count--;
			for(int j=0;j<tab_count;j++) out = out.concat("\t");
			out = out.concat("</loopstruct>\n");
		
		
			tab_count--;
			for(int j=0;j<tab_count;j++) out = out.concat("\t");
			out = out.concat("</position>\n");
			
		}
		else{
			//子要素を持たない場合
			tab_count--;
			for(int j=0;j<tab_count;j++) out = out.concat("\t");
			out = out.concat("</position>\n");
			
		}
		return out;
	}
	

	public ArrayList<String> outPutVariable() throws MathException {
		ArrayList<String> out = new ArrayList<String>();
		HashMap<Math_ci,HashMap<Integer,Integer>> loopCompo = pSimpleRecMLAnalyzer.getVarLoopcomponent(); 
		HashMap<Math_ci, Integer> rv = pSimpleRecMLAnalyzer.getM_HashMapRecurVar();
		for(Math_ci key:rv.keySet()){
			String str;			
//			str = "<variable name=\"" + key.toLegalString() + "\" type=\"recurvar\"/>";
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"recurvar\" ";
			if(loopCompo.containsKey(key)){
				HashMap<Integer,Integer> hm = loopCompo.get(key);
				for(Integer i:hm.keySet()){
					if(hm.get(i) == 1){
						str += "loopcomponent" + (i+1) +" = \""+ 1 +"\" ";						
					}
				}
			}
			str += "/>";
			out.add(str);
		}
		
		HashMap<Math_ci, Integer> av = pSimpleRecMLAnalyzer.getM_HashMapArithVar();
		for(Math_ci key:av.keySet()){
			String str;			
//			str = "<variable name=\"" + key.toLegalString() + "\" type=\"arithvar\"/>";
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"arithvar\" ";
			if(loopCompo.containsKey(key)){
				HashMap<Integer,Integer> hm = loopCompo.get(key);
				for(Integer i:hm.keySet()){
					if(hm.get(i) == 1){
						str += "loopcomponent" + (i+1) +" = \""+ 1 +"\" ";						
					}
				}
			}
			str += "/>";
			out.add(str);
		}

		HashMap<Math_ci, Integer> cv = pSimpleRecMLAnalyzer.getM_HashMapConstVar();
		for(Math_ci key:cv.keySet()){
			String str;			
//			str = "<variable name=\"" + key.toLegalString() + "\" type=\"constvar\"/>";
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"constvar\" ";
			if(loopCompo.containsKey(key)){
				HashMap<Integer,Integer> hm = loopCompo.get(key);
				for(Integer i:hm.keySet()){
					if(hm.get(i) == 1){
						str += "loopcomponent" + (i+1) +" = \""+ 1 +"\" ";						
					}
				}
			}
			str += "/>";
			out.add(str);
		}

		HashMap<Math_ci, Integer> ov = pSimpleRecMLAnalyzer.getM_HashMapOutputVar();
		for(Math_ci key:ov.keySet()){
			String str;			
//			str = "<variable name=\"" + key.toLegalString() + "\" type=\"output\"/>";
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"output\" ";
			if(loopCompo.containsKey(key)){
				HashMap<Integer,Integer> hm = loopCompo.get(key);
				for(Integer i:hm.keySet()){
					if(hm.get(i) == 1){
						str += "loopcomponent" + (i+1) +" = \""+ 1 +"\" ";						
					}
				}
			}
			str += "/>";
			out.add(str);
		}
		
		HashMap<Math_ci, Integer> sv = pSimpleRecMLAnalyzer.getM_HashMapStepVar();
		for(Math_ci key:sv.keySet()){
			String str;			
//			str = "<variable name=\"" + key.toLegalString() + "\" type=\"stepvar\"/>";
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"stepvar\" ";
			if(loopCompo.containsKey(key)){
				HashMap<Integer,Integer> hm = loopCompo.get(key);
				for(Integer i:hm.keySet()){
					if(hm.get(i) == 1){
						str += "loopcomponent" + (i+1) +" = \""+ 1 +"\" ";						
					}
				}
			}
			str += "/>";
			out.add(str);
		}
		return out;

	}

	
	public ArrayList<String> outPutDepGraph() {
		ArrayList<String> out = new ArrayList<String>();
		HashMap<Integer, HashMap<String, Integer>> gr = pSimpleRecMLAnalyzer.getM_HashMapNodeList();
		String str = "";
		for(Integer key1:gr.keySet()){
			HashMap<String, Integer> hm = gr.get(key1);
			str = str.concat("<node id=\"" + Integer.toString(key1) + "\" ");
			for(String key2:hm.keySet()){
				str = str.concat(key2 + "=\"" + hm.get(key2) +  "\" ");
			}
			str = str.concat("/>\n");
		}	
		
		str = str.concat("\n");
		
		HashMap<Integer, HashMap<String, Integer>> eg = pSimpleRecMLAnalyzer.getM_HashMapEdgeList();
		for(Integer key1:eg.keySet()){
			HashMap<String, Integer> hm = eg.get(key1);
			str = str.concat("<edge ");
			for(String key2:hm.keySet()){
				String str2 = key2 + "=\"" + hm.get(key2) +  "\" ";
				str = str.concat(str2);
			}
			str = str.concat("/>\n");
		}
		out.add(str);
		return out;
	}
	
	public ArrayList<String> outPutSimulEqu() {
		ArrayList<String> out = new ArrayList<String>();
		ArrayList<ArrayList<Integer>> se = pSimpleRecMLAnalyzer.getSimulEquList();
		int id=0;
		for(ArrayList<Integer> al:se){
			String str = "";
			for(Integer in:al){
				str = "<simulltaneous id = \"" + id + "\" equation = \""+ in +"\"/>";
			}
			id++;
			out.add(str);
		}
		return out;
	}
	
	public ArrayList<String> outPutRefVariableList() {
		ArrayList<String> out = new ArrayList<String>();
		HashMap<Integer, String> rv = pSimpleRecMLAnalyzer.getRefVariableList();
		for(Integer key:rv.keySet()){
			String str;			
			str = "<refvariable id=\"" + key + "\" variableName=\"" + rv.get(key) + "\"/>";
			out.add(str);
		}
		
		return out;
	}
	
	private String outPutMathML() throws MathException {
		pSimpleRecMLAnalyzer.printMathml();
		return null;
	}
	private ArrayList<String> getMathML() throws MathException {
		return pSimpleRecMLAnalyzer.getMathml();
	}
	
}
