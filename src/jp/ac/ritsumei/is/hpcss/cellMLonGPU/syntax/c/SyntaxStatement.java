package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.c;

/**
 * statement構文クラス.
 */
public abstract class SyntaxStatement extends Syntax {

	/**
	 * statement構文インスタンスを作成する.
	 * @param classification 構文分類
	 */
	public SyntaxStatement(eSyntaxClassification classification) {
		super(classification);
	}

}
