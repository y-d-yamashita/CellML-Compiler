package jp.ac.ritsumei.is.hpcss.cellMLcompiler.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.*;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLVariableAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxProgram;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sun.corba.se.impl.orbutil.graph.Graph;

public class CellMLCompilerCudaRecurrenceRelationGeneratorMain {

	//========================================================
	//DEFINE
	//========================================================
	private static final String MAIN_VAR_RELATION_FILENAME = "relation.txt";
	private static final String MAIN_VAR_INITIALIZE_FILENAME = "initialize.txt";

	/** Default parser name. */
	protected static final String DEFAULT_PARSER_NAME =
		"org.apache.xerces.parsers.SAXParser";

	private static final String GENERATOR_CUDA = "cuda";
	private static final String GENERATOR_COMMON = "common";
	private static final String GENERATOR_SIMPLE = "simple";
	private static final String DEFALUT_GENERATOR = GENERATOR_CUDA;

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
	public static void main(String[] args) {

		RecMLAnalyzer pRecMLAnalyzer = new RecMLAnalyzer();
		
		String xml = "";
		xml = "./model/relfile/relation.recml";

		//---------------------------------------------------
		//XMLパーサ初期化
		//---------------------------------------------------
		// create parser
		XMLReader parser = null;
		try {
			parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			parser.setProperty("http://apache.org/xml/properties/input-buffer-size",
					new Integer(16 * 0x1000));
		} catch (Exception e) {
			System.err.println("error: Unable to instantiate parser ("
					+ "org.apache.xerces.parsers.SAXParser" + ")");
			System.exit(1);
		}

		XMLHandler handler = new XMLHandler(pRecMLAnalyzer);
		parser.setContentHandler(handler);
		try {
			parser.parse(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
//		/*selector内cnのInteger*/
		pRecMLAnalyzer.changeAllSelectorInteger();
		
		/*selector削除*/
		pRecMLAnalyzer.removeAllSelector();
		
		
		pRecMLAnalyzer.createVariableTable();
		pRecMLAnalyzer.setLeftsideRightsideVariable();
		/** 内容確認 ***/
		try {
			pRecMLAnalyzer.printContents();
		} catch (MathException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
//		/*variable取得*/
//		Vector<Math_ci> pRecMLVariables = new Vector<Math_ci>();
//		try {
//			pRecMLAnalyzer.getAllVariable(pRecMLVariables);
//		} catch (MathException e2) {
//			e2.printStackTrace();
//		}
//		int i=0;
//		for(Math_ci it:pRecMLVariables){
//			try {
//				System.out.println("Variable ["+i+"]:"+it.toLegalString());
//				i++;
//			} catch (MathException e) {
//				// TODO 自動生成された catch ブロック
//				e.printStackTrace();
//			}
//		}
		
		
		
		//---------------------------------------------------
		//目的プログラム生成
		//---------------------------------------------------
		/*プログラム生成器インスタンス生成*/
		ProgramGenerator pProgramGenerator = null;
		SyntaxProgram pSynProgram = null;
//		String relfile = "";
//		relfile = "./model/relfile/relation.recml";
//		try {
//			if (generatorName.equals(GENERATOR_CUDA)) {
//				pProgramGenerator =
//					new CudaProgramGenerator(pCellMLAnalyzer,pRelMLAnalyzer,pTecMLAnalyzer);
//			} else if ((generatorName.equals(GENERATOR_COMMON))) {
//				pProgramGenerator =
//					new CommonProgramGenerator(pCellMLAnalyzer,pRelMLAnalyzer,pTecMLAnalyzer);
//			} else if ((generatorName.equals(GENERATOR_SIMPLE))) {
//				pProgramGenerator =
//					new SimpleProgramGenerator(pCellMLAnalyzer,pRelMLAnalyzer,pTecMLAnalyzer);
//			} else {
//                System.err.println("error: invalid Generator name ("+generatorName+").");
//				printUsage();
//				System.exit(1);
//			}
//
//			/*パラメータをハードコードで設定*/
//			pProgramGenerator.setElementNum(1);
//			pProgramGenerator.setTimeParam(0.0,400.0,0.01);
//
//			/*プログラム構文出力*/
//			pSynProgram = pProgramGenerator.getSyntaxProgram();
//
//		} catch (Exception e) {
//			/*エラー出力*/
//			System.err.println(e.getMessage());
//			e.printStackTrace(System.err);
//			System.err.println("failed to translate program");
//			System.exit(1);
//		}

		/*------ ↓　先生が作成したもの ------*/
		
//		String[] strAttr = new String[] {"init", "null", "null" };
//		int cnt = 0;
//		MathExpression mathExp;
//		System.out.println("loop1="+strAttr[0]+"\n");
//		while ((mathExp = pRecMLAnalyzer.getExpressionWithAttr(strAttr)) != null) {
//			try {
//				System.out.println("じっこうしたお!!");
//				System.out.println(mathExp.toLegalString());
//			} catch (MathException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			cnt++;
//		}
//
//		try {
//			pRecMLAnalyzer.printContents();
//		} catch (MathException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		/*------ ↑　先生が作成したもの ------*/
		
		
		
		
		
		try {
			pProgramGenerator =
			//new RecurrenceRelationGenerator(pRecMLAnalyzer);
			//new RecurrenceRelationGeneratorStatementList(pRecMLAnalyzer);
			//new RecurrenceRelationGeneratorStatementList(pRecMLAnalyzer);
			new CudaRecurrenceProgramGenerator(pRecMLAnalyzer);
			} catch (MathException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		
		/*パラメータをハードコードで設定*/
		pProgramGenerator.setElementNum(1);
		pProgramGenerator.setTimeParam(0.0,400.0,0.01);

		/*プログラム構文出力*/
		try {
			pSynProgram = pProgramGenerator.getSyntaxProgram();
			System.out.println("[output]------------------------------------");

			/*プログラム出力*/
			System.out.println(pSynProgram.toLegalString());
		} catch (MathException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (CellMLException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (RelMLException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (TranslateException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (SyntaxException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		
		//---------------------------------------------------
		//出力
		//---------------------------------------------------
		try {
			/*RelML内容出力*/
//			pRelMLAnalyzer.printContents();

			/*CellML内容出力*/
//			pCellMLAnalyzer.printContents();

			/*TecML内容出力*/
//			pTecMLAnalyzer.printContents();

			/*目的プログラム出力*/
			if (pSynProgram != null) {
				/*出力開始線*/
//				System.out.println("[output]------------------------------------");
//
//				/*プログラム出力*/
//				System.out.println(pSynProgram.toLegalString());
			}
//			System.exit(1);		// *ML内容出力確認時に有効にする
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}

//		try {
//			PrintWriter out = null;
//			/*変数関係の出力*/
//			out = new PrintWriter(
//					new BufferedWriter(new FileWriter(MAIN_VAR_RELATION_FILENAME)));
//			pProgramGenerator.outputVarRelationList(out);
//			out.close();
//
//			/*初期化式の出力*/
//			out = new PrintWriter(
//					new BufferedWriter(new FileWriter(MAIN_VAR_INITIALIZE_FILENAME)));
//			pProgramGenerator.outputInitializeList(out,
//				pCellMLVariableAnalyzer.getComponentTable());
//			out.close();
//		} catch (Exception e) {
//			System.err.println(e.getMessage());
//			e.printStackTrace(System.err);
//			System.exit(1);
//		}
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
	
//	private static Graph<RecMLVertex,ReMLEdge> createBipatieGraph()
	
}
