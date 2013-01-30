package jp.ac.ritsumei.is.hpcss.cellMLonGPU.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class CellMLcompilerGUI  extends JFrame {
	
	private static final String STATUS_LABEL_RUN = "Start";
	private static final String STATUS_LABEL_END = "Finish";
	private static final String STATUS_LABEL_FAIL = "failure";
	private static final String DEFAULT_INITIALIZE_FILE = "initialize.txt";
	private static final String DEFAULT_RELATION_FILE = "relation.txt";
	private static final int dialogWidth = 650;
	private static final int dialogHeight = 550;
	private static final int labelWidth = 130;
	private static final int textFieldColumns = 40;
		private JLabel recmlLabel;
		private JTextField recmlTextField;
		private JButton recmlButton;
	private JLabel outDirLabel;
	private JTextField outDirTextField;
	private JButton outDirButton;
//	private JRadioButton cRadioButton;
//	private JRadioButton cudaRadioButton;
//	private JRadioButton simpleRadioButton;
//	private JRadioButton javaRadioButton;
//	private JRadioButton javaBigDecimalRadioButton;

	/*生成系1*/
	private JRadioButton gRustyButton;
	private JRadioButton gKawabataButton;

	/*生成系2*/
	private JRadioButton rSimpleButton;
	private JRadioButton rStructuredButton;
	private JRadioButton rRelmlButton;
	
	private JLabel programLabel;
	private JTextField programTextField;
	private JLabel initializeLabel;
	private JTextField initializeTextField;
	private JLabel relationLabel;
	private JTextField relationTextField;
	private JTextArea execLogTextArea;
	private JButton execButton;
	private JButton endButton;
	private ExecSubProcess_recml execSubProcess;

	/**
	 * 数式展開システムGUI版エントリポイント関数.
	 * @param args コマンドライン文字列
	 */
	public static void main(String[] args) {
		new CellMLcompilerGUI().setVisible(true);
	}
	
	/**
	 * インスタンスを作成し数式展開システムGUI版を実行する.
	 */
	public CellMLcompilerGUI() {
		super("数式展開プログラム");
		setSize(dialogWidth, dialogHeight);
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// コンポーネント作成

		// RelML ラベル
		recmlLabel = new JLabel("Input fail");
		Dimension dim = recmlLabel.getPreferredSize();
		recmlLabel.setPreferredSize(new Dimension(labelWidth, dim.height));
		// (SimpleRecML or StructuredRecML)テキストフィールド
		recmlTextField = new JTextField(textFieldColumns);
		dim = recmlTextField.getPreferredSize();
		recmlTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height));
		recmlTextField.setEditable(true);
		// (SimpleRecML or StructuredRecML)ボタン
		recmlButton = new JButton("...");
		recmlButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			selectRecMLPath();
    		}
    	});
		
		// 生成器1
		gRustyButton = new JRadioButton("PDE solver (-R)     ");
		gKawabataButton = new JRadioButton("ODE solver (-K)      ");
		ButtonGroup bg0 = new ButtonGroup();
		bg0.add(gRustyButton);
		bg0.add(gKawabataButton);
		
		// 生成器2
		rSimpleButton = new JRadioButton("SimpleRecML");
		rStructuredButton = new JRadioButton("StructuredRecML");
		rRelmlButton = new JRadioButton("RelML");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rSimpleButton);
		bg.add(rStructuredButton);
		bg.add(rRelmlButton);

		// 出力ディレクトリ ラベル
		outDirLabel = new JLabel("Output folder");
		dim = outDirLabel.getPreferredSize();
		outDirLabel.setPreferredSize(new Dimension(labelWidth, dim.height));
		// 出力ディレクトリ テキストフィールド
		outDirTextField = new JTextField(textFieldColumns);
		dim = outDirTextField.getPreferredSize();
		outDirTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height));
		outDirTextField.setEditable(true);
		// 出力ディレクトリ ボタン
		outDirButton = new JButton("...");
		outDirButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			selectOutDirPath();
    		}
    	});
		
		// プログラムファイル ラベル
		programLabel = new JLabel("Program file");
		dim = programLabel.getPreferredSize();
		programLabel.setPreferredSize(new Dimension(labelWidth, dim.height));
		// プログラムファイル テキストフィールド
		programTextField = new JTextField(textFieldColumns);
		dim = programTextField.getPreferredSize();
		programTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height));
		programTextField.setEditable(true);
		
		// 実行ログ
		execLogTextArea = new JTextArea("");
		execLogTextArea.setEditable(false);

		// 実行ボタン
		execButton = new JButton("EXEC");
		execButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exec();
			}
    	});
		// 閉じるボタン
    	endButton = new JButton("CLOSE");
    	endButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			// 終了
    			dispose();
    		}
    	});

    	// パネルレイアウト

    	JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		JPanel recmlPanel = new JPanel();
		dim = recmlButton.getPreferredSize();
		recmlPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		recmlPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		recmlPanel.setLayout(new BoxLayout(recmlPanel, BoxLayout.X_AXIS));
		recmlPanel.add(Box.createHorizontalStrut(10));
		recmlPanel.add(recmlLabel);
		recmlPanel.add(Box.createHorizontalStrut(10));
		recmlPanel.add(recmlTextField);
		recmlPanel.add(Box.createHorizontalStrut(10));
		recmlPanel.add(recmlButton);
		recmlPanel.add(Box.createHorizontalGlue());
		recmlPanel.add(Box.createHorizontalStrut(5));
		recmlPanel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		centerPanel.add(recmlPanel);

		JPanel selectPanel0 = new JPanel();
		selectPanel0.setLayout(new BoxLayout(selectPanel0, BoxLayout.Y_AXIS));
		selectPanel0.setBorder(new TitledBorder(new EtchedBorder(), "Numerical solution"));
		selectPanel0.add(gRustyButton);
		selectPanel0.add(gKawabataButton);
		
		dim = selectPanel0.getPreferredSize();
		JPanel radioPanel0 = new JPanel();
		radioPanel0.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height));
		radioPanel0.setLayout(new BoxLayout(radioPanel0, BoxLayout.X_AXIS));
		radioPanel0.add(Box.createHorizontalStrut(10));
		radioPanel0.add(selectPanel0);
		radioPanel0.add(Box.createHorizontalGlue());
		radioPanel0.add(Box.createHorizontalStrut(10));
		radioPanel0.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		centerPanel.add(radioPanel0);
		
		JPanel selectPanel = new JPanel();
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
		selectPanel.setBorder(new TitledBorder(new EtchedBorder(), "Input file"));
		selectPanel.add(rSimpleButton);
		selectPanel.add(rStructuredButton);
		selectPanel.add(rRelmlButton);

		dim = selectPanel.getPreferredSize();
		JPanel radioPanel = new JPanel();
		radioPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height));
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
		radioPanel.add(Box.createHorizontalStrut(10));
		radioPanel.add(selectPanel);
		radioPanel.add(Box.createHorizontalGlue());
		radioPanel.add(Box.createHorizontalStrut(10));
		radioPanel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		centerPanel.add(radioPanel);

		JPanel outdirPanel = new JPanel();
		dim = outDirButton.getPreferredSize();
		outdirPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		outdirPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		outdirPanel.setLayout(new BoxLayout(outdirPanel, BoxLayout.X_AXIS));
		outdirPanel.add(Box.createHorizontalStrut(10));
		outdirPanel.add(outDirLabel);
		outdirPanel.add(Box.createHorizontalStrut(10));
		outdirPanel.add(outDirTextField);
		outdirPanel.add(Box.createHorizontalStrut(10));
		outdirPanel.add(outDirButton);
		outdirPanel.add(Box.createHorizontalGlue());
		outdirPanel.add(Box.createHorizontalStrut(5));
		outdirPanel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		centerPanel.add(outdirPanel);

		JPanel programPanel = new JPanel();
		dim = outDirButton.getPreferredSize();
		programPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		programPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		programPanel.setLayout(new BoxLayout(programPanel, BoxLayout.X_AXIS));
		programPanel.add(Box.createHorizontalStrut(10));
		programPanel.add(programLabel);
		programPanel.add(Box.createHorizontalStrut(10));
		programPanel.add(programTextField);
		programPanel.add(Box.createHorizontalStrut(10));
		programPanel.add(Box.createHorizontalStrut(dim.width));
		programPanel.add(Box.createHorizontalGlue());
		programPanel.add(Box.createHorizontalStrut(5));
		programPanel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		centerPanel.add(programPanel);

		JScrollPane sp = new JScrollPane(execLogTextArea);
		sp.setBorder(new TitledBorder(new EtchedBorder(), "Execution log"));
		dim = sp.getPreferredSize();
		sp.setPreferredSize(new Dimension(dialogWidth - 100, dim.height));
		JPanel logPanel = new JPanel();
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.X_AXIS));
		logPanel.add(Box.createHorizontalStrut(10));
		logPanel.add(sp);
		logPanel.add(Box.createHorizontalGlue());
		logPanel.add(Box.createHorizontalStrut(10));
		logPanel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		centerPanel.add(logPanel);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    	buttonPanel.add(execButton);
    	buttonPanel.add(endButton);

		add(centerPanel);
    	add(buttonPanel, BorderLayout.SOUTH);
	}
	
    /**
     * RECMLファイル選択ダイアログを表示する.
     */
    private void selectRecMLPath() {
    	String dir = getRecML();
    	if (dir.length() == 0 || !new File(dir).exists()) {
    		dir = null;
    	}
		JFileChooser fileChooser = new JFileChooser(dir);
		FileExtentionFilter filter = new FileExtentionFilter();
		filter.addExtension("recml");
		filter.setDescription("RECML");
		fileChooser.setFileFilter(filter);
		FileExtentionFilter fl = new FileExtentionFilter();
		fl.addExtension("relml");
		fl.setDescription("RELML");
		fileChooser.setFileFilter(fl);
		fileChooser.setAcceptAllFileFilterUsed(false);

		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			setRelML(fileChooser.getSelectedFile().getPath());
		}
    }
    
	/**
	 * RECMLファイルパスを取得する.
	 * @return RELMLファイルパス
	 */
	private String getRecML() {
		return recmlTextField.getText();
    }
	
	/**
	 * RECMLファイルパスを設定する.
	 * @param path RELMLファイルパス
	 */
	private void setRelML(String path) {
		recmlTextField.setText(path);
    }
	
    /**
     * 出力ディレクトリ選択ダイアログを表示する.
     */
    private void selectOutDirPath() {
    	String dir = getOutDir();
    	if (dir.length() == 0 || !new File(dir).exists()) {
    		dir = null;
    	}
		JFileChooser fileChooser = new JFileChooser(dir);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			setOutDir(fileChooser.getSelectedFile().getPath());
		}
    }
    
	/**
	 * 出力ディレクトリパスを取得する.
	 * @return 出力ディレクトリパス
	 */
	private String getOutDir() {
		return outDirTextField.getText();
    }

	/**
	 * 出力ディレクトリパスを設定する.
	 * @param path 出力ディレクトリパス
	 */
	private void setOutDir(String path) {
		outDirTextField.setText(path);
    }
	
    /**
     * 別スレッドを起動し、数式展開を行う.
     */
    private void exec() {
    	if (getOutDir().length() == 0) {
    		JFileChooser fileChooser = new JFileChooser();
    		setOutDir(fileChooser.getFileSystemView().getDefaultDirectory().getPath());
    	} else {
    		if (!new File(getOutDir()).isDirectory()) {
        		JOptionPane.showMessageDialog(this,
        				"出力ディレクトリが存在しません: " + getOutDir(),
        				"エラー", JOptionPane.ERROR_MESSAGE);
        		return;
        	}
    	}

//		if (getRelationFile().length() == 0) {
//			setRelationFile(DEFAULT_RELATION_FILE);
//		}
//
//		if (getInitializeFile().length() == 0) {
//			setInitializeFile(DEFAULT_INITIALIZE_FILE);
//		}

		File recml = new File(getRecML());
    	if (!recml.exists()) {
    		JOptionPane.showMessageDialog(this,
    				"RECMLファイルが存在しません: " + getRecML(),
    				"エラー", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
	
    	
    	/*生成系1*/
    	String genCode0 = null;
    	if (gRustyButton.isSelected()) {
    		genCode0 = ODECommonProgramGeneratorMain.GENERATOR_RUSTY;
    	}else if(gKawabataButton.isSelected()){
    		genCode0 = ODECommonProgramGeneratorMain.GENERATOR_KAWABATA;
    	}

    	/*生成系2*/
    	String genCode1 = null;
    	if (rSimpleButton.isSelected()) {
    		genCode1 = ODECommonProgramGeneratorMain.FILETYPE_SIMPLE;
    	}else if(rStructuredButton.isSelected()){
    		genCode1 = ODECommonProgramGeneratorMain.FILETYPE_STRUCTURED;    		
    	}else if(rRelmlButton.isSelected()){
    		genCode1 = ODECommonProgramGeneratorMain.FILETYPE_RELML;
    	}
    	
    	if (genCode0 != null && genCode1 != null) {
    		execButton.setEnabled(false);
    		execLogTextArea.setText(STATUS_LABEL_RUN + "\n");
    		try {
    			execSubProcess =
    				new ExecSubProcess_recml(recml,
    						genCode0,
    						genCode1,
    						getOutDir(),
    						getProgramFile(),
  						execLogTextArea);
        		ThreadRunner threadRunner = new ThreadRunner();
        		threadRunner.start();
    		} catch (Exception e) {
    			e.printStackTrace();
    			setFailStatus();
    		}
    	}
	}
    
	/**
	 * 変数関係ファイルパスを取得する.
	 * @return 変数関係ファイルパス
	 */
	private String getRelationFile() {
		return relationTextField.getText();
    }

	/**
	 * 変数関係ファイルパスを設定する.
	 * @param path 変数関係ファイルパス
	 */
	private void setRelationFile(String path) {
		relationTextField.setText(path);
    }

	/**
	 * 初期化式ファイルパスを取得する.
	 * @return 初期化式ファイルパス
	 */
	private String getInitializeFile() {
		return initializeTextField.getText();
    }

	/**
	 * 初期化式ファイルパスを設定する.
	 * @param path 初期化式ファイルパス
	 */
	private void setInitializeFile(String path) {
		initializeTextField.setText(path);
    }

	
	/**
	 * プログラムファイルパスを取得する.
	 * @return プログラムファイルパス
	 */
	private String getProgramFile() {
		return programTextField.getText();
    }
	
    /**
     * スレッド管理クラス
     */
    public class ThreadRunner extends Thread {

    	/* (非 Javadoc)
    	 * スレッドの実行を開始する.
    	 * @see java.lang.Thread#run()
    	 */
    	public void run() {
    		try {
//    			setCursor(new Cursor(Cursor.WAIT_CURSOR));
    			runThread();
//				setOKStatus();
    		} catch (Exception e) {
    			e.printStackTrace();
    			setFailStatus();
    		} finally {
//    			setCursor(null);
			}
    	}
    }
    

    /**
     * 実行成功表示を行う.
     */
    private void setOKStatus() {
		execLogTextArea.append(STATUS_LABEL_END + "\n");
		execButton.setEnabled(true);
    }

    /**
     * 実行失敗表示を行う.
     */
    private void setFailStatus() {
		execLogTextArea.append(STATUS_LABEL_FAIL + "\n");
		execButton.setEnabled(true);
    }

    /**
     * スレッドの実行を開始する.
     * @throws Exception
     */
    private void runThread() throws Exception {
    	if (execSubProcess.run() == 0) {
			setOKStatus();
    	} else {
			setFailStatus();
    	}
    }


    
    
    
    


}
