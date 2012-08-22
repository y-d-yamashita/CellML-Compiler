package jp.ac.ritsumei.is.hpcss.cellMLcompiler.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JTextArea;

/**
 * サブプロセスで数式展開を実行するクラス.
 */
public class ExecSubProcess {

	private File relml;
	private String genCode;
	private String outputDir;
	private String programFilename;
	private String relationFilename;
	private String initializeFilename;
	private JTextArea log;

	/**
	 * サブプロセスで数式展開を実行するためのインスタンスを作成する.
	 * @param relml RelMLファイル
	 * @param genCode 生成器
	 * @param outputDir 出力フォルダ
	 * @param programFilename プログラムファイル
	 * @param relationFilename 変数関係ファイル
	 * @param initializeFilename 初期化式ファイル
	 * @param log ログ出力先
	 */
	public ExecSubProcess(File relml, String genCode, String outputDir,
			String programFilename, String relationFilename, String initializeFilename,
			JTextArea log) {
		this.relml = relml;
		this.genCode = genCode;
		this.outputDir = outputDir;
		this.programFilename = programFilename;
		this.relationFilename = relationFilename;
		this.initializeFilename = initializeFilename;
		this.log = log;
	}

	/**
	 * サブプロセスで数式展開を実行する.
	 * @throws Exception
	 */
	public int run() throws Exception {
		// 実行開始
		ArrayList<String> commandList = createCommandList();
		return startProcess(commandList);
	}

   /**
	 * 数式展開の実行用コマンドリストを作成する.
	 * @return コマンドリスト
	 * @throws IOException
	 */
	private ArrayList<String> createCommandList() throws IOException {
		// スクリプトファイルを介して、数式展開の実行を行う。
		ArrayList<String> commandList = new ArrayList<String>();
		commandList.add("cmd.exe");
		commandList.add("/C");
		commandList.add("parser.bat");
		commandList.add("-g");
		commandList.add(genCode);
		commandList.add(relml.getPath());
		if (outputDir.length() > 0) {
			commandList.add(outputDir);
			if (relationFilename.length() > 0) {
				commandList.add(relationFilename);
				if (initializeFilename.length() > 0) {
					commandList.add(initializeFilename);
					if (programFilename.length() > 0) {
						commandList.add(programFilename);
					}
				}
			}
		}

		return commandList;
	}

	/**
	 * プロセスを実行する.
	 * 修了するまで待つ.
	 * @param commandList コマンドリスト
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private int startProcess(ArrayList<String> commandList)
    throws IOException, InterruptedException {
    	ProcessBuilder pb = new ProcessBuilder();
    	pb.command(commandList);
    	pb.redirectErrorStream(true);
    	Process p = pb.start();
    	BufferedReader r = new java.io.BufferedReader(
    			new InputStreamReader(p.getInputStream()));
    	String line;
    	while ((line=r.readLine()) != null) {
    		// 入力しないとプロセスがブロックされる
//    		System.out.println(line);
    		log.append(line + "\n");
    	}
    	int ret = p.waitFor();
    	r.close();
    	return ret;
    }

}
