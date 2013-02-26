package jp.ac.ritsumei.is.hpcss.cellMLonGPU.app;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.CommonProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.JavaProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator.ProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.exception.GraphException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver.LeftHandSideTransposition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.c.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java.JavaSyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.writer.StructuredRecMLWiter;


import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * ODE Program Generator main class 
 * 
 * input file:  
 * 	SimpleRecML or StructuredRecML

 * output file:
 *  C program code or Java program code
 * 
 * 
 * @author n-washio
 * 
 */

public class ProgramGeneratorMain {

	//========================================================
	//DEFINE
	//========================================================
	
	/** Default parser name. */
	protected static final String DEFAULT_PARSER_NAME =
		"org.apache.xerces.parsers.SAXParser";

	private static final String GENERATOR_CUDA = "cuda";
	private static final String DEFALUT_GENERATOR = GENERATOR_CUDA;

	protected static final String GENERATOR_JAVA = "-J";
	protected static final String GENERATOR_C = "-C";

	protected static final String FILETYPE_SIMPLE = "SimpleRecML";
	protected static final String FILETYPE_STRUCTURED = "StructuredRecML";
	protected static final String FILETYPE_RELML = "RelML";

	protected static String pre_programFilename = null;
	protected static String programFilename = null;
	protected static String Classname = null;
	protected static String outputDir = null;
	protected static String generatorName = DEFALUT_GENERATOR;

	//===================================================
	//main
	//	エントリポイント関数
	//
	//@arg
	// int	nArgc		: コマンドライン文字列数
	// char	**pszArgv	: コマンドライン文字列
	//
	//@return
	//	終了コード		: int
	//===================================================
	public static void main(String[] args) throws MathException, CellMLException, RelMLException, TranslateException, SyntaxException, TableException, GraphException {

		//---------------------------------------------------
		//実行開始時処理
		//---------------------------------------------------
		/*引数チェック*/
		
		for(int i= 0;i<args.length;i++){
			System.out.println(i + "------" + args[i]);
		}
		
		/*引数チェック*/
		int n = 0;
		if (args.length > n && args[n].startsWith("-")) {
			if (args[n].equals("-C") || args[n].equals("-J")) {
				n++;
				if (args.length > n) {
					generatorName = args[n++];
				} else {
                    System.err.println("error: Missing argument to -(C or J) option.");
					printUsage();
					System.exit(1);
				}
			} else {
                System.err.println("error: unknown option ("+args[n]+").");
				printUsage();
				System.exit(1);
			}
		}
		if (args.length <= n) {
            System.err.println("error: Missing file name of RecML");
			printUsage();
			System.exit(1);
		}
		
		String xml = args[n++];
		
		if (args.length > n) {
			outputDir = args[n++];
			if (!new File(outputDir).isDirectory()) {
				System.err.println("error: \"" + outputDir + "\" is not a directory");
				printUsage();
				System.exit(1);
			}
		}

		if (args.length > n) {
			programFilename = args[n++];
		}
		if (outputDir != null) {
			if (programFilename != null) {
				pre_programFilename = outputDir + File.separator + "STRUCTUREDRECML_" + programFilename +".recml";
				Classname = programFilename;
				programFilename = outputDir + File.separator + programFilename;
			}
		}

		//System.out.println("------------------------------------------------------------------pf---"+programFilename);
		//---------------------------------------------------
		//XMLパーサ初期化
		//---------------------------------------------------
		// create parser
		XMLReader parser = null;
		try {
			parser = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
			// バッファサイズが小さいと ハンドラの characters() が
			// 文字列を途中で切った値を返す。バッファサイズを大きくする
			// デフォルトは2k
			parser.setProperty("http://apache.org/xml/properties/input-buffer-size",
					new Integer(16 * 0x1000));
		} catch (Exception e) {
			System.err.println("error: Unable to instantiate parser ("
					+ DEFAULT_PARSER_NAME+")");
			System.exit(1);
		}
		
		ArrayList<String> outputRecml = new ArrayList<String>();
		
		/*Javaプログラムコード生成*/
		if(args[0].equals(GENERATOR_JAVA)){
			
			if(args[1].equals(FILETYPE_SIMPLE)){
				
				/*SimpleRecML*/
				SimpleRecMLAnalyzer simpleRecMLAnalyzer = new SimpleRecMLAnalyzer();
				XMLHandler handler = new XMLHandler(simpleRecMLAnalyzer);
				parser.setContentHandler(handler);
				try {
					parser.parse(xml);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*SimpleRecMLの情報を解析*/
				simpleRecMLAnalyzer.analysisForOutPutStrRec(simpleRecMLAnalyzer);

				/*StructuredRecMLの表示形式に変更する*/
				StructuredRecMLWiter sr = new StructuredRecMLWiter(simpleRecMLAnalyzer);
				sr.createStrRecml();
				outputRecml = sr.getStrRecml();
			
				/*StructuredRecMLの出力*/
				try {
					/*目的プログラム出力*/
					if (outputRecml != null) {
						PrintWriter out = null;
						if (pre_programFilename == null) {
							out = new PrintWriter(System.out);
						} else {
							out = new PrintWriter(
									new BufferedWriter(new FileWriter(pre_programFilename)));
						}

						/*プログラム出力*/
						for(String str:outputRecml){
							out.println(str);
						}
						
						if (pre_programFilename != null) {
							out.close();
						} else {
							out.flush();
						}
					}

				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace(System.err);
					System.exit(1);
				}
				
				/*StructuredRecML*/
				RecMLAnalyzer recMLAnalyzer = new RecMLAnalyzer();
				handler = new XMLHandler(recMLAnalyzer);
				parser.setContentHandler(handler);
				try {
					parser.parse(pre_programFilename);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			

				
				/*selector内cnのInteger*/
				recMLAnalyzer.changeAllSelectorInteger();
				/*selector削除*/
				recMLAnalyzer.removeAllSelector();
				
				
				
				
				
				//数式に対する導出変数を取得し,移項処理を行う.
				LeftHandSideTransposition lst = new LeftHandSideTransposition();
				
				recMLAnalyzer.createVariableTable();
				//RecMLVariableTable vartable2 = recMLAnalyzer.getRecMLVariableTable();
				ArrayList<Vector<Integer>> pairList2 = recMLAnalyzer.resultMaximumMatching;
				
				for(int i=0;i<pairList2.size();i++){
					
					int expID = pairList2.get(i).get(0);
					int varID = pairList2.get(i).get(1);
					
					
					//数式を取得
					MathExpression exp = recMLAnalyzer.getExpressionFromID(expID);
					 
					//RecMLVariableReference derivedTable = vartable2.getVariableReference(varID);
					
					//導出変数を取得
					String derivedVarName =  recMLAnalyzer.variableNameMap.get(varID);
					Math_ci derivedVar=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, derivedVarName);
					
					if(exp.getExID()!=-1){
						MathExpression exp_new = lst.transporseExpression(exp, derivedVar);
						exp_new.setDerivedVariable(derivedVar);
						//数式を置換(移項処理の有無にかかわらず全て移項処理を通過させたものに置換する)
						//非線形数式であれば非線形フラグがMathExpressionに付与される.導出変数は数式が保持する.
						recMLAnalyzer.setM_vecMathExpression(recMLAnalyzer.getLocationFromID(expID), exp_new);
					}
				}

				
				//連立方程式をIDでソートする.(処理上は不要な処理であるが,数式のid順に出力するようにしている)
				HashMap<Integer,Vector<Integer>> simulEquationIDList = recMLAnalyzer.simulEquationIDList;
				
				for(int i=0;i<simulEquationIDList.size();i++){
					
					for(int j=0;j<simulEquationIDList.get(i).size();j++){
						
						Integer temp;
						for(int x=0;x<simulEquationIDList.get(i).size()-1;x++){
							
							for(int y=simulEquationIDList.get(i).size()-1;y>x;y--){
								if(simulEquationIDList.get(i).get(y)<simulEquationIDList.get(i).get(y-1)){
									temp=simulEquationIDList.get(i).get(y);
									simulEquationIDList.get(i).set(y, simulEquationIDList.get(i).get(y-1));
									simulEquationIDList.get(i).set(y-1, temp);
								}
							}
							
						}

					}
					
				}
				
				//連立式をベクターに格納
				ArrayList<Vector<MathExpression>> simulEquationListRec = new ArrayList<Vector<MathExpression>>();
				Vector<MathExpression> simulEquSetRec;

				for(int i=0;i<simulEquationIDList.size();i++){
					Vector<Integer> setID =simulEquationIDList.get(i);
					simulEquSetRec = new Vector<MathExpression>();
					for(int j=0;j<setID.size();j++){
						
						recMLAnalyzer.getExpressionFromID((long)setID.get(j)).setSimulID(i);
						simulEquSetRec.add(recMLAnalyzer.getExpressionFromID((long)setID.get(j)));
					}
					simulEquationListRec.add(simulEquSetRec);
					
				}
				
				recMLAnalyzer.setSimulEquationList(simulEquationListRec);
				
				

				
				//---------------------------------------------------
				//目的プログラム生成
				//---------------------------------------------------
				/*プログラム生成器インスタンス生成*/
				ProgramGenerator pProgramGenerator = null;
				JavaSyntaxProgram pSynProgram = null;
				
				
				try {
					pProgramGenerator =
					new JavaProgramGenerator(recMLAnalyzer,Classname);
				} catch (MathException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
				
				/*パラメータをハードコードで設定*/
				pProgramGenerator.setElementNum(1);
				pProgramGenerator.setTimeParam(0.0,400.0,0.01);
				
				pSynProgram = ((JavaProgramGenerator) pProgramGenerator).getJavaSyntaxProgram();
				/*プログラム構文出力*/
				try {
					/*目的プログラム出力*/
					if (pSynProgram != null) {
						PrintWriter outKST1 = null;
						if (programFilename == null) {
							outKST1 = new PrintWriter(System.out);
						}else{
							outKST1 = new PrintWriter(new BufferedWriter(new FileWriter(programFilename)));							
						}
						/*プログラム出力*/
						
						
						
						outKST1.println(pSynProgram.toLegalJavaString());
						
						if (programFilename != null) {
							outKST1.close();
						} else {
							outKST1.flush();
						}
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace(System.err);
					System.exit(1);
				}
				
			}
			
			if(args[1].equals(FILETYPE_STRUCTURED)){
				
				/*StructuredRecML*/
				RecMLAnalyzer pRecMLAnalyzer = new RecMLAnalyzer();
				XMLHandler handler = new XMLHandler(pRecMLAnalyzer);
				parser.setContentHandler(handler);
				try {
					parser.parse(xml);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*selector内cnのInteger*/
				pRecMLAnalyzer.changeAllSelectorInteger();
				
				/*selector削除*/
				pRecMLAnalyzer.removeAllSelector();


				
				//数式に対する導出変数を取得し,移項処理を行う.
				LeftHandSideTransposition lst2 = new LeftHandSideTransposition();
				
				pRecMLAnalyzer.createVariableTable();
				ArrayList<Vector<Integer>> pairList2 = pRecMLAnalyzer.resultMaximumMatching;
				
				for(int i=0;i<pairList2.size();i++){
					
					int expID = pairList2.get(i).get(0);
					int varID = pairList2.get(i).get(1);
					
					
					//数式を取得
					MathExpression exp = pRecMLAnalyzer.getExpressionFromID(expID);
					 
					//RecMLVariableReference derivedTable = vartable2.getVariableReference(varID);
					
					//導出変数を取得
					String derivedVarName =  pRecMLAnalyzer.variableNameMap.get(varID);
					Math_ci derivedVar=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, derivedVarName);

					if(exp.getExID()!=-1){
						MathExpression exp_new = lst2.transporseExpression(exp, derivedVar);
						exp_new.setDerivedVariable(derivedVar);
						//数式を置換(移項処理の有無にかかわらず全て移項処理を通過させたものに置換する)
						//非線形数式であれば非線形フラグがMathExpressionに付与される.導出変数は数式が保持する.
						pRecMLAnalyzer.setM_vecMathExpression(i, exp_new);
					}
				}

				//連立方程式をIDでソートする.(処理上は不要な処理であるが,数式のid順に出力するようにしている)
				HashMap<Integer,Vector<Integer>> simulEquationIDList = pRecMLAnalyzer.simulEquationIDList;
				
				for(int i=0;i<simulEquationIDList.size();i++){
					
					for(int j=0;j<simulEquationIDList.get(i).size();j++){
						
						Integer temp;
						for(int x=0;x<simulEquationIDList.get(i).size()-1;x++){
							
							for(int y=simulEquationIDList.get(i).size()-1;y>x;y--){
								if(simulEquationIDList.get(i).get(y)<simulEquationIDList.get(i).get(y-1)){
									temp=simulEquationIDList.get(i).get(y);
									simulEquationIDList.get(i).set(y, simulEquationIDList.get(i).get(y-1));
									simulEquationIDList.get(i).set(y-1, temp);
								}
							}
							
						}

					}
					
				}
				
				//連立式をベクターに格納
				ArrayList<Vector<MathExpression>> simulEquationListRec = new ArrayList<Vector<MathExpression>>();
				Vector<MathExpression> simulEquSetRec;
				
				for(int i=0;i<simulEquationIDList.size();i++){
					Vector<Integer> setID =simulEquationIDList.get(i);
					simulEquSetRec = new Vector<MathExpression>();
					for(int j=0;j<setID.size();j++){
						
						pRecMLAnalyzer.getExpressionFromID((long)setID.get(j)).setSimulID(i);
						simulEquSetRec.add(pRecMLAnalyzer.getExpressionFromID((long)setID.get(j)));
					}
					simulEquationListRec.add(simulEquSetRec);
					
				}
				
				pRecMLAnalyzer.setSimulEquationList(simulEquationListRec);
				
				
				//---------------------------------------------------
				//目的プログラム生成
				//---------------------------------------------------
				/*プログラム生成器インスタンス生成*/
				ProgramGenerator pProgramGenerator = null;
				JavaSyntaxProgram pSynProgram = null;
				
				
				try {
					pProgramGenerator =
					new JavaProgramGenerator(pRecMLAnalyzer,Classname);
					} catch (MathException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
				
				/*パラメータをハードコードで設定*/
				pProgramGenerator.setElementNum(1);
				pProgramGenerator.setTimeParam(0.0,400.0,0.01);
				
				pSynProgram = ((JavaProgramGenerator) pProgramGenerator).getJavaSyntaxProgram();
				/*プログラム構文出力*/
				try {
					/*目的プログラム出力*/
					if (pSynProgram != null) {
						PrintWriter outKST1 = null;
						outKST1 = new PrintWriter(new BufferedWriter(new FileWriter(programFilename)));
						if (programFilename == null) {
							outKST1 = new PrintWriter(System.out);
						}
						/*プログラム出力*/
						outKST1.println(pSynProgram.toLegalJavaString());
						
						if (programFilename != null) {
							outKST1.close();
						} else {
							outKST1.flush();
						}
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace(System.err);
					System.exit(1);
				}
				
			}

		}
		
		/*Cプログラムコード生成*/
		if(args[0].equals(GENERATOR_C)){
			if(args[1].equals(FILETYPE_SIMPLE)){
				
				/*SimpleRecML*/
				SimpleRecMLAnalyzer simpleRecMLAnalyzer = new SimpleRecMLAnalyzer();
				XMLHandler handler = new XMLHandler(simpleRecMLAnalyzer);
				parser.setContentHandler(handler);
				try {
					parser.parse(xml);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*SimpleRecMLの情報を解析*/
				simpleRecMLAnalyzer.analysisForOutPutStrRec(simpleRecMLAnalyzer);

				/*StructuredRecMLの表示形式に変更する*/
				StructuredRecMLWiter sr = new StructuredRecMLWiter(simpleRecMLAnalyzer);
				sr.createStrRecml();
				outputRecml = sr.getStrRecml();
			
				/*StructuredRecMLの出力*/
				try {
					/*目的プログラム出力*/
					if (outputRecml != null) {
						PrintWriter out = null;
						if (pre_programFilename == null) {
							out = new PrintWriter(System.out);
						} else {
							out = new PrintWriter(
									new BufferedWriter(new FileWriter(pre_programFilename)));
						}

						/*プログラム出力*/
						for(String str:outputRecml){
							out.println(str);
						}
						
						if (pre_programFilename != null) {
							out.close();
						} else {
							out.flush();
						}
					}

				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace(System.err);
					System.exit(1);
				}
				
				/*StructuredRecML*/
				RecMLAnalyzer recMLAnalyzer = new RecMLAnalyzer();
				handler = new XMLHandler(recMLAnalyzer);
				parser.setContentHandler(handler);
				try {
					parser.parse(pre_programFilename);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			

				
				/*selector内cnのInteger*/
				recMLAnalyzer.changeAllSelectorInteger();
				/*selector削除*/
				recMLAnalyzer.removeAllSelector();
				
				
				
				
				
				//数式に対する導出変数を取得し,移項処理を行う.
				LeftHandSideTransposition lst = new LeftHandSideTransposition();
				
				recMLAnalyzer.createVariableTable();
				//RecMLVariableTable vartable2 = recMLAnalyzer.getRecMLVariableTable();
				ArrayList<Vector<Integer>> pairList2 = recMLAnalyzer.resultMaximumMatching;
				
				for(int i=0;i<pairList2.size();i++){
					
					int expID = pairList2.get(i).get(0);
					int varID = pairList2.get(i).get(1);
					
					
					//数式を取得
					MathExpression exp = recMLAnalyzer.getExpressionFromID(expID);
					 
					//RecMLVariableReference derivedTable = vartable2.getVariableReference(varID);
					
					//導出変数を取得
					String derivedVarName =  recMLAnalyzer.variableNameMap.get(varID);
					Math_ci derivedVar=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, derivedVarName);
					
					if(exp.getExID()!=-1){
						MathExpression exp_new = lst.transporseExpression(exp, derivedVar);
						exp_new.setDerivedVariable(derivedVar);
						//数式を置換(移項処理の有無にかかわらず全て移項処理を通過させたものに置換する)
						//非線形数式であれば非線形フラグがMathExpressionに付与される.導出変数は数式が保持する.
						recMLAnalyzer.setM_vecMathExpression(recMLAnalyzer.getLocationFromID(expID), exp_new);
					}
				}

				
				//連立方程式をIDでソートする.(処理上は不要な処理であるが,数式のid順に出力するようにしている)
				HashMap<Integer,Vector<Integer>> simulEquationIDList = recMLAnalyzer.simulEquationIDList;
				
				for(int i=0;i<simulEquationIDList.size();i++){
					
					for(int j=0;j<simulEquationIDList.get(i).size();j++){
						
						Integer temp;
						for(int x=0;x<simulEquationIDList.get(i).size()-1;x++){
							
							for(int y=simulEquationIDList.get(i).size()-1;y>x;y--){
								if(simulEquationIDList.get(i).get(y)<simulEquationIDList.get(i).get(y-1)){
									temp=simulEquationIDList.get(i).get(y);
									simulEquationIDList.get(i).set(y, simulEquationIDList.get(i).get(y-1));
									simulEquationIDList.get(i).set(y-1, temp);
								}
							}
							
						}

					}
					
				}
				
				//連立式をベクターに格納
				ArrayList<Vector<MathExpression>> simulEquationListRec = new ArrayList<Vector<MathExpression>>();
				Vector<MathExpression> simulEquSetRec;

				for(int i=0;i<simulEquationIDList.size();i++){
					Vector<Integer> setID =simulEquationIDList.get(i);
					simulEquSetRec = new Vector<MathExpression>();
					for(int j=0;j<setID.size();j++){
						
						recMLAnalyzer.getExpressionFromID((long)setID.get(j)).setSimulID(i);
						simulEquSetRec.add(recMLAnalyzer.getExpressionFromID((long)setID.get(j)));
					}
					simulEquationListRec.add(simulEquSetRec);
					
				}
				
				recMLAnalyzer.setSimulEquationList(simulEquationListRec);
				
				

				
				//---------------------------------------------------
				//目的プログラム生成
				//---------------------------------------------------
				/*プログラム生成器インスタンス生成*/
				ProgramGenerator pProgramGenerator = null;
				SyntaxProgram pSynProgram = null;
				
				
				try {
					pProgramGenerator =
					new CommonProgramGenerator(recMLAnalyzer);
				} catch (MathException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
				
				/*パラメータをハードコードで設定*/
				pProgramGenerator.setElementNum(1);
				pProgramGenerator.setTimeParam(0.0,400.0,0.01);
				
				pSynProgram = pProgramGenerator.getSyntaxProgram();
				
				/*プログラム構文出力*/
				try {
					/*目的プログラム出力*/
					if (pSynProgram != null) {
						PrintWriter outKST1 = null;
						if (programFilename == null) {
							outKST1 = new PrintWriter(System.out);
						}else{
							outKST1 = new PrintWriter(new BufferedWriter(new FileWriter(programFilename)));							
						}
						/*プログラム出力*/
						
						
						
						outKST1.println(pSynProgram.toLegalString());
						
						if (programFilename != null) {
							outKST1.close();
						} else {
							outKST1.flush();
						}
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace(System.err);
					System.exit(1);
				}
				
			}
			
			if(args[1].equals(FILETYPE_STRUCTURED)){
				
				/*StructuredRecML*/
				RecMLAnalyzer pRecMLAnalyzer = new RecMLAnalyzer();
				XMLHandler handler = new XMLHandler(pRecMLAnalyzer);
				parser.setContentHandler(handler);
				try {
					parser.parse(xml);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*selector内cnのInteger*/
				pRecMLAnalyzer.changeAllSelectorInteger();
				
				/*selector削除*/
				pRecMLAnalyzer.removeAllSelector();


				
				//数式に対する導出変数を取得し,移項処理を行う.
				LeftHandSideTransposition lst2 = new LeftHandSideTransposition();
				
				pRecMLAnalyzer.createVariableTable();
				ArrayList<Vector<Integer>> pairList2 = pRecMLAnalyzer.resultMaximumMatching;
				
				for(int i=0;i<pairList2.size();i++){
					
					int expID = pairList2.get(i).get(0);
					int varID = pairList2.get(i).get(1);
					
					
					//数式を取得
					MathExpression exp = pRecMLAnalyzer.getExpressionFromID(expID);
					 
					//RecMLVariableReference derivedTable = vartable2.getVariableReference(varID);
					
					//導出変数を取得
					String derivedVarName =  pRecMLAnalyzer.variableNameMap.get(varID);
					Math_ci derivedVar=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, derivedVarName);

					if(exp.getExID()!=-1){
						MathExpression exp_new = lst2.transporseExpression(exp, derivedVar);
						exp_new.setDerivedVariable(derivedVar);
						//数式を置換(移項処理の有無にかかわらず全て移項処理を通過させたものに置換する)
						//非線形数式であれば非線形フラグがMathExpressionに付与される.導出変数は数式が保持する.
						pRecMLAnalyzer.setM_vecMathExpression(i, exp_new);
					}
				}

				//連立方程式をIDでソートする.(処理上は不要な処理であるが,数式のid順に出力するようにしている)
				HashMap<Integer,Vector<Integer>> simulEquationIDList = pRecMLAnalyzer.simulEquationIDList;
				
				for(int i=0;i<simulEquationIDList.size();i++){
					
					for(int j=0;j<simulEquationIDList.get(i).size();j++){
						
						Integer temp;
						for(int x=0;x<simulEquationIDList.get(i).size()-1;x++){
							
							for(int y=simulEquationIDList.get(i).size()-1;y>x;y--){
								if(simulEquationIDList.get(i).get(y)<simulEquationIDList.get(i).get(y-1)){
									temp=simulEquationIDList.get(i).get(y);
									simulEquationIDList.get(i).set(y, simulEquationIDList.get(i).get(y-1));
									simulEquationIDList.get(i).set(y-1, temp);
								}
							}
							
						}

					}
					
				}
				
				//連立式をベクターに格納
				ArrayList<Vector<MathExpression>> simulEquationListRec = new ArrayList<Vector<MathExpression>>();
				Vector<MathExpression> simulEquSetRec;
				
				for(int i=0;i<simulEquationIDList.size();i++){
					Vector<Integer> setID =simulEquationIDList.get(i);
					simulEquSetRec = new Vector<MathExpression>();
					for(int j=0;j<setID.size();j++){
						
						pRecMLAnalyzer.getExpressionFromID((long)setID.get(j)).setSimulID(i);
						simulEquSetRec.add(pRecMLAnalyzer.getExpressionFromID((long)setID.get(j)));
					}
					simulEquationListRec.add(simulEquSetRec);
					
				}
				
				pRecMLAnalyzer.setSimulEquationList(simulEquationListRec);
				
				
				//---------------------------------------------------
				//目的プログラム生成
				//---------------------------------------------------
				/*プログラム生成器インスタンス生成*/
				ProgramGenerator pProgramGenerator = null;
				SyntaxProgram pSynProgram = null;
				
				
				try {
					pProgramGenerator =
					new CommonProgramGenerator(pRecMLAnalyzer);
					} catch (MathException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
				
				/*パラメータをハードコードで設定*/
				pProgramGenerator.setElementNum(1);
				pProgramGenerator.setTimeParam(0.0,400.0,0.01);
				
				pSynProgram = pProgramGenerator.getSyntaxProgram();
				/*プログラム構文出力*/
				try {
					/*目的プログラム出力*/
					if (pSynProgram != null) {
						PrintWriter outKST1 = null;
						outKST1 = new PrintWriter(new BufferedWriter(new FileWriter(programFilename)));
						if (programFilename == null) {
							outKST1 = new PrintWriter(System.out);
						}
						/*プログラム出力*/
						outKST1.println(pSynProgram.toLegalString());
						
						if (programFilename != null) {
							outKST1.close();
						} else {
							outKST1.flush();
						}
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace(System.err);
					System.exit(1);
				}
				
			}
				
			
		}

	}

	//=============================================================
	//parseXMLFile
	// XMLファイル解析関数
	//
	//@arg
	// string			strXMLFileName	: 読み込みファイル名
	// SAX2XMLReader*	pParser			: XMLパーサインスタンス
	// XMLAnalyzer*		pXMLAnalyzer	: RelML解析器インスタンス
	//
	//@return
	// 成否判定	: bool
	//
	//=============================================================
	static boolean parseXMLFile(String strXMLFileName, XMLReader pParser,
			XMLAnalyzer pXMLAnalyzer) {

		try {

			/*ファイルの存在を確認*/
			if(!new File(strXMLFileName).canRead()){
				System.err.println("file can't open : " + strXMLFileName);
				return false;
			}

			/*パース処理*/
			XMLHandler handler = new XMLHandler(pXMLAnalyzer);
			pParser.setContentHandler(handler);
			pParser.parse(strXMLFileName);

		} catch (SAXParseException e) {
			/*エラー出力*/
			System.err.println("failed to parse file : " + strXMLFileName);
			return false;
		} catch (Exception e) {

			/*例外メッセージ出力*/
			System.err.println("error: Parse error occurred - "+e.getMessage());
			Exception se = e;
			if (e instanceof SAXException) {
				se = ((SAXException)e).getException();
			}
			if (se != null) {
				se.printStackTrace(System.err);
			} else {
				e.printStackTrace(System.err);
			}

			/*エラー出力*/
			System.err.println("failed to parse file : " + strXMLFileName);
			return false;
		}

		return true;
	}

	private static void printUsage() {
		System.err.println("usage: ./parser [option] \"filename of RelML\"");
		System.err.println("option:");
        System.err.println("  -g name     Select Generator by name. {cuda|common|simple}");
        System.err.println("default:");
        System.err.println("  Generator:  "+DEFALUT_GENERATOR);
	}
	
}
