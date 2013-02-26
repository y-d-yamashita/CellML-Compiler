package jp.ac.ritsumei.is.hpcss.cellMLonGPU.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.ExpandException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.expansion.SimpleExpansion;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.expansion.SimpleRecMLWriter;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLVariableAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecML;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.XMLHandler;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *1ファイル1コンポーネントCellMLを展開し、SimpleRecMLを作成するMain 
 * @author m-ara
 *
 */

public class SimpleExpansionMain {
	
	//========================================================
	//DEFINE
	//========================================================
	/** Default parser name. */
	protected static final String DEFAULT_PARSER_NAME =
		"org.apache.xerces.parsers.SAXParser";

	
	public static void main(String[] args) throws MathException, IOException, ExpandException, CellMLException, RelMLException, TranslateException, TableException {
		//---------------------------------------------------
		//実行開始時処理
		//---------------------------------------------------
		/*引数チェック*/
		int n = 0;
		
		String xml ;
		String outputDir = null;
		String outputfilename = null;
		if (args.length <= n) {
			xml = "./model/sample/fhn/fhn_Euler.relml";
			outputDir = "";
//            System.err.println("error: Missing file name of RelML");
//			System.exit(1);
		}else{
			xml = args[0];
			outputfilename = "output.recml";
			n++;
		}
		
		if(args.length>n){
			outputDir = args[n];
		}
			n++;
		if(args.length>n){
			outputfilename = args[n];
			n++;
		}
		
		System.out.println("expand " +  xml + " at " + outputDir +" \"" + outputfilename + "\"");
		outputfilename = outputDir + File.separator + outputfilename;
		

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
			
		/*変数テーブルをCellML解析器に渡す*/
		pCellMLAnalyzer.setComponentTable(pCellMLVariableAnalyzer.getComponentTable());
			
		/*CellMLの解析*/
		if(!parseXMLFile(strCellMLFileName, parser, pCellMLAnalyzer)){
				System.exit(1);
		}
		
//		pCellMLAnalyzer.printContents();
		
		/*TecMLの解析*/
		if(!parseXMLFile(strTecMLFileName,parser,pTecMLAnalyzer)){
			System.exit(1);
		}
//		System.out.println("******* TecML parse end");
		
		//---------------------------------------------------
		//SimpleRecML生成
		//---------------------------------------------------
		/*SImpleRecML生成器インスタンス生成*/
		
		SimpleRecML pSimpleRecML = null;
		
		//数式展開
		SimpleExpansion pSimpleExpansion = new SimpleExpansion(pCellMLAnalyzer, pRelMLAnalyzer,pTecMLAnalyzer);
			
		pSimpleRecML = pSimpleExpansion.getSimpleRecML();
			
		
			System.out.println("\n");
			//SimpleRecML生成
			SimpleRecMLWriter pSimpleRecMLWriter = new SimpleRecMLWriter(pSimpleRecML);
			pSimpleRecMLWriter.createSimpleRecML();
//			pSimpleRecMLWriter.outPutSimpleRecML();
			
			//目的プログラムファイル出力
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputfilename)));
			/*プログラム出力*/
			for(String pStr : pSimpleRecMLWriter.getSimpleRecML() )
				out.println(pStr);
			out.close();
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
				se = (Exception)((SAXException)e).getException();
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
}
