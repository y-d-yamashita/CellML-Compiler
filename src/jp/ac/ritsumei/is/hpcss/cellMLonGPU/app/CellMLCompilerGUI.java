package jp.ac.ritsumei.is.hpcss.cellMLonGPU.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

/**
 * 数式展開システム GUI版メインクラス.
 */
@SuppressWarnings("serial")
public class CellMLCompilerGUI extends JFrame {

	private static final String STATUS_LABEL_RUN = "実行開始";
	private static final String STATUS_LABEL_END = "実行終了";
	private static final String STATUS_LABEL_FAIL = "実行に失敗しました";
	private static final String DEFAULT_INITIALIZE_FILE = "initialize.txt";
	private static final String DEFAULT_RELATION_FILE = "relation.txt";
	private static final int dialogWidth = 650;
	private static final int dialogHeight = 550;
	private static final int labelWidth = 130;
	private static final int textFieldColumns = 40;
	private JLabel relmlLabel;
	private JTextField relmlTextField;
	private JButton relmlButton;
	private JLabel outDirLabel;
	private JTextField outDirTextField;
	private JButton outDirButton;
	private JRadioButton cRadioButton;
	private JRadioButton cudaRadioButton;
	private JRadioButton simpleRadioButton;
	private JRadioButton javaRadioButton;
	private JRadioButton javaBigDecimalRadioButton;
	private JLabel programLabel;
	private JTextField programTextField;
	private JLabel initializeLabel;
	private JTextField initializeTextField;
	private JLabel relationLabel;
	private JTextField relationTextField;
	private JTextArea execLogTextArea;
	private JButton execButton;
	private JButton endButton;
	private ExecSubProcess execSubProcess;

	/**
	 * 数式展開システムGUI版エントリポイント関数.
	 * @param args コマンドライン文字列
	 */
	public static void main(String[] args) {
		new CellMLCompilerGUI().setVisible(true);
	}

	/**
	 * インスタンスを作成し数式展開システムGUI版を実行する.
	 */
	public CellMLCompilerGUI() {
		super("数式展開プログラム");
		setSize(dialogWidth, dialogHeight);
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// コンポーネント作成

		// RelML ラベル
		relmlLabel = new JLabel("RelMLファイル");
		Dimension dim = relmlLabel.getPreferredSize();
		relmlLabel.setPreferredSize(new Dimension(labelWidth, dim.height));
		// RelML テキストフィールド
		relmlTextField = new JTextField(textFieldColumns);
		dim = relmlTextField.getPreferredSize();
		relmlTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height));
		relmlTextField.setEditable(true);
		// RelML ボタン
		relmlButton = new JButton("...");
		relmlButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			selectRelMLPath();
    		}
    	});

		// 生成器
		cRadioButton = new JRadioButton("C");
		cudaRadioButton = new JRadioButton("CUDA 用 C", true);
		simpleRadioButton = new JRadioButton("Simple コード");
		javaRadioButton = new JRadioButton("Java");
		javaBigDecimalRadioButton = new JRadioButton("Java BigDecimal");
		ButtonGroup bg = new ButtonGroup();
		bg.add(cRadioButton);
		bg.add(cudaRadioButton);
		bg.add(simpleRadioButton);
		bg.add(javaRadioButton);
		bg.add(javaBigDecimalRadioButton);

		// 出力ディレクトリ ラベル
		outDirLabel = new JLabel("出力フォルダ");
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
		programLabel = new JLabel("プログラムファイル");
		dim = programLabel.getPreferredSize();
		programLabel.setPreferredSize(new Dimension(labelWidth, dim.height));
		// プログラムファイル テキストフィールド
		programTextField = new JTextField(textFieldColumns);
		dim = programTextField.getPreferredSize();
		programTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height));
		programTextField.setEditable(true);

		// 変数関係ファイル ラベル
		relationLabel = new JLabel("変数関係ファイル");
		dim = relationLabel.getPreferredSize();
		relationLabel.setPreferredSize(new Dimension(labelWidth, dim.height));
		// 変数関係ファイル テキストフィールド
		relationTextField = new JTextField(DEFAULT_RELATION_FILE, textFieldColumns);
		dim = relationTextField.getPreferredSize();
		relationTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height));
		relationTextField.setEditable(true);

		// 初期化式ファイル ラベル
		initializeLabel = new JLabel("初期化式ファイル");
		dim = initializeLabel.getPreferredSize();
		initializeLabel.setPreferredSize(new Dimension(labelWidth, dim.height));
		// 初期化式ファイル テキストフィールド
		initializeTextField = new JTextField(DEFAULT_INITIALIZE_FILE, textFieldColumns);
		dim = initializeTextField.getPreferredSize();
		initializeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height));
		initializeTextField.setEditable(true);

		// 実行ログ
		execLogTextArea = new JTextArea("");
		execLogTextArea.setEditable(false);

		// 実行ボタン
		execButton = new JButton("実行");
		execButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exec();
			}
    	});
		// 閉じるボタン
    	endButton = new JButton("閉じる");
    	endButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			// 終了
    			dispose();
    		}
    	});

    	// パネルレイアウト

    	JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		JPanel relmlPanel = new JPanel();
		dim = relmlButton.getPreferredSize();
		relmlPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		relmlPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		relmlPanel.setLayout(new BoxLayout(relmlPanel, BoxLayout.X_AXIS));
		relmlPanel.add(Box.createHorizontalStrut(10));
		relmlPanel.add(relmlLabel);
		relmlPanel.add(Box.createHorizontalStrut(10));
		relmlPanel.add(relmlTextField);
		relmlPanel.add(Box.createHorizontalStrut(10));
		relmlPanel.add(relmlButton);
		relmlPanel.add(Box.createHorizontalGlue());
		relmlPanel.add(Box.createHorizontalStrut(5));
		relmlPanel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		centerPanel.add(relmlPanel);

		JPanel selectPanel = new JPanel();
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
		selectPanel.setBorder(new TitledBorder(new EtchedBorder(), "生成器"));
		selectPanel.add(cRadioButton);
		selectPanel.add(cudaRadioButton);
		selectPanel.add(simpleRadioButton);
		selectPanel.add(javaRadioButton);
		selectPanel.add(javaBigDecimalRadioButton);
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

		JPanel relationPanel = new JPanel();
		dim = outDirButton.getPreferredSize();
		relationPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		relationPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		relationPanel.setLayout(new BoxLayout(relationPanel, BoxLayout.X_AXIS));
		relationPanel.add(Box.createHorizontalStrut(10));
		relationPanel.add(relationLabel);
		relationPanel.add(Box.createHorizontalStrut(10));
		relationPanel.add(relationTextField);
		relationPanel.add(Box.createHorizontalStrut(10));
		relationPanel.add(Box.createHorizontalStrut(dim.width));
		relationPanel.add(Box.createHorizontalGlue());
		relationPanel.add(Box.createHorizontalStrut(5));
		relationPanel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		centerPanel.add(relationPanel);

		JPanel initializePanel = new JPanel();
		dim = outDirButton.getPreferredSize();
		initializePanel.setPreferredSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		initializePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, dim.height + 10));
		initializePanel.setLayout(new BoxLayout(initializePanel, BoxLayout.X_AXIS));
		initializePanel.add(Box.createHorizontalStrut(10));
		initializePanel.add(initializeLabel);
		initializePanel.add(Box.createHorizontalStrut(10));
		initializePanel.add(initializeTextField);
		initializePanel.add(Box.createHorizontalStrut(10));
		initializePanel.add(Box.createHorizontalStrut(dim.width));
		initializePanel.add(Box.createHorizontalGlue());
		initializePanel.add(Box.createHorizontalStrut(5));
		initializePanel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		centerPanel.add(initializePanel);

		JScrollPane sp = new JScrollPane(execLogTextArea);
		sp.setBorder(new TitledBorder(new EtchedBorder(), "実行ログ"));
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
     * RELMLファイル選択ダイアログを表示する.
     */
    private void selectRelMLPath() {
    	String dir = getRelML();
    	if (dir.length() == 0 || !new File(dir).exists()) {
    		dir = null;
    	}
		JFileChooser fileChooser = new JFileChooser(dir);
		FileExtentionFilter filter = new FileExtentionFilter();
		filter.addExtension("relml");
		filter.setDescription("RELML");
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);

		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			setRelML(fileChooser.getSelectedFile().getPath());
		}
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

		if (getRelationFile().length() == 0) {
			setRelationFile(DEFAULT_RELATION_FILE);
		}

		if (getInitializeFile().length() == 0) {
			setInitializeFile(DEFAULT_INITIALIZE_FILE);
		}

		File relml = new File(getRelML());
    	if (!relml.exists()) {
    		JOptionPane.showMessageDialog(this,
    				"RELMLファイルが存在しません: " + getRelML(),
    				"エラー", JOptionPane.ERROR_MESSAGE);
    		return;
    	}

    	String genCode = null;
    	if (cRadioButton.isSelected()) {
    		genCode = CellMLonGPUMain.GENERATOR_COMMON;
    	} else if (cudaRadioButton.isSelected()) {
    		genCode = CellMLonGPUMain.GENERATOR_CUDA;
    	} else if (simpleRadioButton.isSelected()) {
    		genCode = CellMLonGPUMain.GENERATOR_SIMPLE;
    	} else if (javaRadioButton.isSelected()) {
    		genCode = CellMLonGPUMain.GENERATOR_JAVA;
    	} else if (javaBigDecimalRadioButton.isSelected()) {
    		genCode = CellMLonGPUMain.GENERATOR_JAVA_BIGDECIMAL;
    	}

    	if (genCode != null) {
    		execButton.setEnabled(false);
    		execLogTextArea.setText(STATUS_LABEL_RUN + "\n");
    		try {
    			execSubProcess =
    				new ExecSubProcess(relml,
    						genCode,
    						getOutDir(),
    						getProgramFile(),
    						getRelationFile(),
    						getInitializeFile(),
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

    //--------------- getter & setter

	/**
	 * RELMLファイルパスを取得する.
	 * @return RELMLファイルパス
	 */
	private String getRelML() {
		return relmlTextField.getText();
    }

	/**
	 * RELMLファイルパスを設定する.
	 * @param path RELMLファイルパス
	 */
	private void setRelML(String path) {
		relmlTextField.setText(path);
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
	 * プログラムファイルパスを取得する.
	 * @return プログラムファイルパス
	 */
	private String getProgramFile() {
		return programTextField.getText();
    }

//	/**
//	 * プログラムファイルパスを設定する.
//	 * @param path プログラムファイルパス
//	 */
//	private void setProgramFile(String path) {
//		programTextField.setText(path);
//    }

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

}
