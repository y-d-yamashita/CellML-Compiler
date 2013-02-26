package jp.ac.ritsumei.is.hpcss.cellMLonGPU.app;

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

	private File recml;

	private String genCode0;
	private String genCode1;
	private String outputDir;
	private String programFilename;
	private JTextArea log;

	public ExecSubProcess(File xml, String str0, String str1, String outputDir, String programFilename, JTextArea log) {
		this.recml = xml;
		this.genCode0 = str0;
		this.genCode1 = str1;
		this.outputDir = outputDir;
		this.programFilename = programFilename;
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
		if (genCode0.length() > 0) {
			commandList.add(genCode0);
			if (genCode1.length() > 0) {
				commandList.add(genCode1);
				commandList.add(recml.getPath());
				if (outputDir.length() > 0) {
					commandList.add(outputDir);
					if (programFilename.length() > 0) {
						commandList.add(programFilename);
					}
				}
			}
		}

		return commandList;
	}


	/**
	 * @param commandList
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
    		log.append(line + "\n");
    	}
    	int ret = p.waitFor();
    	r.close();
    	return ret;
    }

}
