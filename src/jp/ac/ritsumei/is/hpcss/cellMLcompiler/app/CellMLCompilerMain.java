package jp.ac.ritsumei.is.hpcss.cellMLcompiler.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.CommonProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.CudaProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.JavaBigDecimalProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.JavaProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.ProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator.SimpleProgramGenerator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.CellMLVariableAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.XMLHandler;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.syntax.SyntaxProgram;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 数式展開システム コマンド版メインクラス
 */
public class CellMLCompilerMain {

	//========================================================
	//DEFINE
	//========================================================
	protected static final String MAIN_VAR_RELATION_FILENAME = "relation.txt";
	protected static final String MAIN_VAR_INITIALIZE_FILENAME = "initialize.txt";

	/** Default parser name. */
	protected static final String DEFAULT_PARSER_NAME =
		"org.apache.xerces.parsers.SAXParser";

	protected static final String GENERATOR_CUDA = "cuda";
	protected static final String GENERATOR_COMMON = "common";
	protected static final String GENERATOR_SIMPLE = "simple";
	protected static final String GENERATOR_JAVA = "java";
	protected static final String GENERATOR_JAVA_BIGDECIMAL = "java_bigdecimal";
//	protected static final String DEFAULT_GENERATOR = GENERATOR_CUDA;
	protected static final String DEFAULT_GENERATOR = GENERATOR_JAVA_BIGDECIMAL;

	protected static String generatorName = GENERATOR_JAVA;
	protected static String outputDir = null;
	protected static String programFilename = null; //"fhn_euler_unisys2.java";
	protected static String relationFilename = MAIN_VAR_RELATION_FILENAME;
	protected static String initializeFilename = MAIN_VAR_INITIALIZE_FILENAME;

	/**
	 * 数式展開システムコマンド版エントリポイント関数.
	 * @param args コマンドライン文字列
	 */
	public static void main(String[] args) {

		//---------------------------------------------------
		//実行開始時処理
		//---------------------------------------------------
		/*引数チェック*/
		int n = 0;
		if (args.length > n && args[n].startsWith("-")) {
			if (args[n].equals("-g")) {
				n++;
				if (args.length > n) {
					generatorName = args[n++];
				} else {
                    System.err.println("error: Missing argument to -g option.");
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
            System.err.println("error: Missing file name of RelML");
			printUsage();
			System.exit(1);
		}
		String xml = args[n++];
//		String xml = "";
		xml = "./model/fhn2/fhn_Euler.relml";
//		xml = "./model/fhn2/fhn_6thOrderRungeKutta.relml";
//		xml = "./model/fhn2/fhn_ModifiedEuler.relml";
//		xml = "./model/optest/fhn_Euler.relml";
//		xml = "./model/relml/luo_rudy_1991/luo_rudy_1991_Euler.relml";
//		xml = "./model/relml/luo_rudy_1991/luo_rudy_1991_MEuler.relml";
//		xml = "./model/relml/luo_rudy_1991/luo_rudy_1991_RungeKutta.relml";
//		xml = "./model/relml/iyer_mazhari_winslow_2004/iyer_mazhari_winslow_2004_Euler.relml";

		if (args.length > n) {
			outputDir = args[n++];
			if (!new File(outputDir).isDirectory()) {
				System.err.println("error: \"" + outputDir + "\" is not a directory");
				printUsage();
				System.exit(1);
			}
		}
		if (args.length > n) {
			relationFilename = args[n++];
		}
		if (args.length > n) {
			initializeFilename = args[n++];
		}
		if (args.length > n) {
			programFilename = args[n++];
		}
		if (outputDir != null) {
			if (programFilename != null) {
				programFilename = outputDir + File.separator + programFilename;
			}
			relationFilename = outputDir + File.separator + relationFilename;
			initializeFilename = outputDir + File.separator + initializeFilename;
		}

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

		//---------------------------------------------------
		//解析処理
		//---------------------------------------------------
		/*解析器インスタンス生成*/
		CellMLVariableAnalyzer pCellMLVariableAnalyzer = new CellMLVariableAnalyzer();
		CellMLAnalyzer pCellMLAnalyzer = new CellMLAnalyzer();
		RelMLAnalyzer pRelMLAnalyzer = new RelMLAnalyzer();
		TecMLAnalyzer pTecMLAnalyzer = new TecMLAnalyzer();

		/*各ファイル名初期化*/
		String strRelMLFileName = xml;
		String strTecMLFileName;
		String strCellMLFileName;

		/*RelMLの解析*/
		if(!parseXMLFile(strRelMLFileName, parser, pRelMLAnalyzer)){
			System.exit(1);
		}
//		System.out.println("******* RelML parse end ");

		/*読み込みファイル名取得*/
		strTecMLFileName = pRelMLAnalyzer.getFileNameTecML();
		strCellMLFileName = pRelMLAnalyzer.getFileNameCellML();

		/*CellML変数部分の解析*/
		if(!parseXMLFile(strCellMLFileName, parser, pCellMLVariableAnalyzer)){
			System.exit(1);
		}
//		System.out.println("******* CellML変数 parse end");

		/*変数テーブルをCellML解析器に渡す*/
		pCellMLAnalyzer.setComponentTable(pCellMLVariableAnalyzer.getComponentTable());

		/*CellMLの解析*/
		if(!parseXMLFile(strCellMLFileName, parser, pCellMLAnalyzer)){
			System.exit(1);
		}
//		System.out.println("******* CellML parse end");

		/*TecMLの解析*/
		if(!parseXMLFile(strTecMLFileName,parser,pTecMLAnalyzer)){
			System.exit(1);
		}
//		System.out.println("******* TecML parse end");

		//---------------------------------------------------
		//目的プログラム生成
		//---------------------------------------------------
		/*プログラム生成器インスタンス生成*/
		ProgramGenerator pProgramGenerator = null;
		SyntaxProgram pSynProgram = null;

		try {
			if (generatorName.equals(GENERATOR_CUDA)) {
				pProgramGenerator =
					new CudaProgramGenerator(pCellMLAnalyzer,pRelMLAnalyzer,pTecMLAnalyzer);
			} else if ((generatorName.equals(GENERATOR_COMMON))) {
				pProgramGenerator =
					new CommonProgramGenerator(pCellMLAnalyzer,pRelMLAnalyzer,pTecMLAnalyzer);
			} else if ((generatorName.equals(GENERATOR_SIMPLE))) {
				pProgramGenerator =
					new SimpleProgramGenerator(pCellMLAnalyzer,pRelMLAnalyzer,pTecMLAnalyzer);
			} else if ((generatorName.equals(GENERATOR_JAVA))) {
				pProgramGenerator =
					new JavaProgramGenerator(pCellMLAnalyzer,pRelMLAnalyzer,pTecMLAnalyzer);
			} else if ((generatorName.equals(GENERATOR_JAVA_BIGDECIMAL))) {
				pProgramGenerator =
					new JavaBigDecimalProgramGenerator(pCellMLAnalyzer,pRelMLAnalyzer,pTecMLAnalyzer);
			} else {
                System.err.println("error: invalid Generator name ("+generatorName+").");
				printUsage();
				System.exit(1);
			}

			/*パラメータをハードコードで設定*/
//			pProgramGenerator.setElementNum(1024);
			pProgramGenerator.setElementNum(5);
			pProgramGenerator.setTimeParam(0.0,400.0,0.01);
			if (pProgramGenerator instanceof JavaBigDecimalProgramGenerator) {
				((JavaBigDecimalProgramGenerator) pProgramGenerator).setScale(80);
			}

			/*プログラム構文出力*/
			pSynProgram = pProgramGenerator.getSyntaxProgram();

		} catch (Exception e) {
			/*エラー出力*/
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			System.err.println("failed to translate program");
			System.exit(1);
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
				PrintWriter out = null;
				if (programFilename == null) {
					out = new PrintWriter(System.out);
				} else {
					out = new PrintWriter(
							new BufferedWriter(new FileWriter(programFilename)));
				}

				/*出力開始線*/
				//out.println("[output]------------------------------------");

				/*プログラム出力*/
				if (pProgramGenerator instanceof JavaProgramGenerator) {
					out.println(pSynProgram.toJavaString());
				} else if (pProgramGenerator instanceof JavaBigDecimalProgramGenerator) {
					out.println(pSynProgram.toJavaBigDecimalString());
				} else {
					out.println(pSynProgram.toLegalString());
				}
				if (programFilename != null) {
					out.close();
				} else {
					out.flush();
				}
			}
//			System.exit(1);		// *ML内容出力確認時に有効にする
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}

		try {
			PrintWriter out = null;
			/*変数関係の出力*/
			out = new PrintWriter(
					new BufferedWriter(new FileWriter(relationFilename)));
			pProgramGenerator.outputVarRelationList(out);
			out.close();

			/*初期化式の出力*/
			out = new PrintWriter(
					new BufferedWriter(new FileWriter(initializeFilename)));
			pProgramGenerator.outputInitializeList(out,
				pCellMLVariableAnalyzer.getComponentTable());
			out.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/**
	 * XMLファイル解析関数.
	 * @param strXMLFileName 読み込みファイル名
	 * @param pParser XMLパーサインスタンス
	 * @param pXMLAnalyzer RelML解析器インスタンス
	 * @return 成否判定
	 */
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

	/**
	 * システム使用方法の出力.
	 */
	private static void printUsage() {
		System.err.println("usage: ./parser [option] \"filename of RelML\" [output-option]");
		System.err.println("option:");
        System.err.println("  -g name     Select Generator by name.");
        System.err.println("              name: {"
        		+ GENERATOR_CUDA + "|"
        		+ GENERATOR_COMMON + "|"
        		+ GENERATOR_SIMPLE + "|"
        		+ GENERATOR_JAVA + "|"
        		+ GENERATOR_JAVA_BIGDECIMAL + "}");
        System.err.println("              default Generator: " + DEFAULT_GENERATOR);
		System.err.println("output-option:");
        System.err.println("  output-dir [Relation-file [Initialize-file [Program-file]]]");
	}

}
