package jp.ac.ritsumei.is.hpcss.cellMLcompiler.generator;

/**
 * Java Bigdecimalプログラム構文生成シングルトンクラス.
 */
public class JavaBigDecimalProgramGeneratorSingleton {

	/**
	 * Java Bigdecimalプログラム構文生成シングルトンインスタンスを作成する.
	 */
	private JavaBigDecimalProgramGeneratorSingleton() {
	}

	private static JavaBigDecimalProgramGeneratorSingleton pInstance;
	private boolean mode;
	private int force;

	/**
	 * Java Bigdecimalプログラム構文生成シングルトンインスタンスを取得する.
	 * @return Java Bigdecimalプログラム構文生成シングルトンインスタンス
	 */
	public static JavaBigDecimalProgramGeneratorSingleton GetInstance() {
		if (pInstance == null) {
			pInstance = new JavaBigDecimalProgramGeneratorSingleton();
			pInstance.mode = false;
			pInstance.force = -1;
		}
		return pInstance;
	}

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
		return mode;
	}

	/**
	 * モードを反転する.
	 */
	public void revMode() {
		mode = !mode;
	}

	/**
	 * forceをあげる.
	 */
	public void addForce() {
		force++;
	}

	/**
	 * forceを取得する.
	 * @return force
	 */
	public int getForce() {
		return force;
	}

	/**
	 * インスタンスを削除する.
	 */
	public void DeleteInstance() {
		pInstance = null;
	}

}
