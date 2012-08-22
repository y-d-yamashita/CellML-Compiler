package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

/**
 * Java プログラム構文生成シングルトンクラス.
 */
public class JavaProgramGeneratorSingleton {

	/**
	 * Java プログラム構文生成シングルトンインスタンスを作成する.
	 */
	private JavaProgramGeneratorSingleton() {
	}

	private static JavaProgramGeneratorSingleton pInstance;
	private boolean mode;

	/**
	 * Java プログラム構文生成シングルトンインスタンスを取得する.
	 * @return Java プログラム構文生成シングルトンインスタンス
	 */
	public static JavaProgramGeneratorSingleton GetInstance() {
		if (pInstance == null) {
			pInstance = new JavaProgramGeneratorSingleton();
			pInstance.mode = false;
		}
		return pInstance;
	}

//	public static JavaProgramGeneratorSingleton GetInstanceCopy() {
//		if (pInstance != null)
//			return pInstance;
//		else
//			return null;
//	}

	/**
	 * モードをセットする.
	 * @param mode モード
	 */
	public void setMode(boolean mode) {
		this.mode = mode;
	}

	/**
	 * モードを取得する.
	 * @return モード
	 */
	public boolean getMode() {
		return this.mode;
	}

	/**
	 * インスタンスを削除する.
	 */
	public void DeleteInstance() {
		pInstance = null;
	}

}
