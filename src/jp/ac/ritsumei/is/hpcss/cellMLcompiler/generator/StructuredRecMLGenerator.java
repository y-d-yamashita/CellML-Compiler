package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.Exception;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.CommonProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.CudaProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.ProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.SimpleProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.loopStructure.RelationPath;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_fn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLVariableAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.SimpleRecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.*;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxControl.eControlKind;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.tecML.TecMLDefinition.eTecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility.StringUtil;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
public class StructuredRecMLGenerator{

	//========================================================
	//DEFINE
	//========================================================

	/*共通変数*/

	protected SimpleRecMLAnalyzer pSimpleRecMLAnalyzer;
	
	/*-----コンストラクタ-----*/
	public StructuredRecMLGenerator(SimpleRecMLAnalyzer simpleRecMLAnalyzer) throws MathException{
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
				"./model/recml/SimpleRecMLSample/SimpleKawabataTestSample/SimpleRecMLSample001.recml"
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

		simpleRecMLAnalyzer.testOutPutStrRec(simpleRecMLAnalyzer);
		StructuredRecMLGenerator sr = new StructuredRecMLGenerator(simpleRecMLAnalyzer);
		sr.outPutStrRecml();
		
	}
	
	
	private void outPutStrRecml() throws MathException {
		ArrayList<String> out = new ArrayList<String>();
		
		System.out.println("<!--    StructuredRecML    -->");

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
		
		for(int i=0;i<out.size();i++){
			System.out.println(out.get(i));	
		}

		System.out.println("<math>");
		this.outPutMatthML();
		System.out.println("</math>");
		System.out.println("</recml>");

	}


	private ArrayList<String> outPutLoopIndex() {
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

	
	private ArrayList<String> outPutLoopStructure() {
		ArrayList<String> out = new ArrayList<String>();
		String str = "";
		ArrayList<RelationPath> rp = pSimpleRecMLAnalyzer.getM_LoopStrucuture();
		
		//loopが1つの場合
		if(rp.size() == 1){
			if(rp.get(0).Child_name == -1 && rp.get(0).Attribute_name.length() == 0){
				str = str.concat("<loopstruct name="+ "\""+rp.get(0).Parent_name+ "\""+">\n");
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
			str = str.concat("<loopstruct name="+ "\""+rp.get(root_num.get(0)).Parent_name+"\""+">\n");
			for(int i=0;i<root_num.size();i++){
				str = str.concat("\t"+"<position name="+ "\""+rp.get(root_num.get(i)).Attribute_name+ "\""+">\n");
				int p_flag=0;
				for(int j=0;j<rp.size();j++){
					if(rp.get(i).Child_name.equals(rp.get(j).Parent_name)){
						p_flag=1;//親になっているフラグ
					}
				}
				if(p_flag==1)str = str.concat("\t\t"+"<loopstruct name="+ "\""+rp.get(root_num.get(i)).Child_name+ "\""+">\n");
				else str = str.concat("\t\t"+"<loopstruct name="+ "\""+rp.get(root_num.get(i)).Child_name+ "\""+"/>\n");
				str = str.concat(call_child(rp, rp.get(root_num.get(i)).Child_name, tab_count));
			}
			str = str.concat("</loopstruct name>\n");
		}
		
		out.add(str);
		return out;
	}

	private String call_child(ArrayList<RelationPath> LoopStructure,Integer My_name, int tab_count){
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
				if(p_flag==1)out = out.concat("<loopstruct name="+ "\""+LoopStructure.get(i).Child_name+ "\""+">\n");
				else out = out.concat("<loopstruct name="+ "\""+LoopStructure.get(i).Child_name+ "\""+"/>\n");
				call_child(LoopStructure,LoopStructure.get(i).Child_name, tab_count);
				tab_count--;
				flag=1;//子要素ありのフラグ
			}
		}
		if(flag==1){
			//子要素を持つ場合の処理（tab++が必要）
			tab_count--;
			for(int j=0;j<tab_count;j++) out = out.concat("\t");
			out = out.concat("</loopstruct name>\n");
		
		
			tab_count--;
			for(int j=0;j<tab_count;j++) out = out.concat("\t");
			out = out.concat("</position name>\n");
			
		}
		else{
			//子要素を持たない場合
			tab_count--;
			for(int j=0;j<tab_count;j++) out = out.concat("\t");
			out = out.concat("</position name>\n");
			
		}
		return out;
	}
	

	private ArrayList<String> outPutVariable() throws MathException {
		ArrayList<String> out = new ArrayList<String>();
		HashMap<Math_ci, Integer> rv = pSimpleRecMLAnalyzer.getM_HashMapRecurVar();
		for(Math_ci key:rv.keySet()){
			String str;			
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"" + rv.get(key) + "\"/>";
			out.add(str);
		}
		
		HashMap<Math_ci, Integer> av = pSimpleRecMLAnalyzer.getM_HashMapArithVar();
		for(Math_ci key:av.keySet()){
			String str;			
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"" + av.get(key) + "\"/>";
			out.add(str);
		}

		HashMap<Math_ci, Integer> cv = pSimpleRecMLAnalyzer.getM_HashMapConstVar();
		for(Math_ci key:cv.keySet()){
			String str;			
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"" + cv.get(key) + "\"/>";
			out.add(str);
		}

		HashMap<Math_ci, Integer> ov = pSimpleRecMLAnalyzer.getM_HashMapOutputVar();
		for(Math_ci key:ov.keySet()){
			String str;			
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"" + ov.get(key) + "\"/>";
			out.add(str);
		}
		
		HashMap<Math_ci, Integer> sv = pSimpleRecMLAnalyzer.getM_HashMapStepVar();
		for(Math_ci key:sv.keySet()){
			String str;			
			str = "<variable name=\"" + key.toLegalString() + "\" type=\"" + sv.get(key) + "\"/>";
			out.add(str);
		}
		return out;

	}

	
	private ArrayList<String> outPutDepGraph() {
		ArrayList<String> out = new ArrayList<String>();
		HashMap<Integer, HashMap<String, Integer>> gr = pSimpleRecMLAnalyzer.getM_HashMapNodeList();
		String str = "";
		for(Integer key1:gr.keySet()){
			HashMap<String, Integer> hm = gr.get(key1);
			str = str.concat("<node id=\"" + Integer.toString(key1) + "\" ");
			for(String key2:hm.keySet()){
				str = str.concat(key2 + "=\"" + hm.get(key2) +  "\" ");
			}
			str = str.concat(">\n");
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
			str = str.concat(">\n");
		}
		out.add(str);
		return out;
	}
	
	
	private String outPutMatthML() throws MathException {
		pSimpleRecMLAnalyzer.printMathml();
		return null;
	}
	
}
