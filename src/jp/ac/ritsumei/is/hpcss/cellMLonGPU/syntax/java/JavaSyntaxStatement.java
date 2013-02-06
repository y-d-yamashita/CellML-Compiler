package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.java;

/**
 * Java　statement構文クラス
 * 
 * @author n-washio
 */
public abstract class JavaSyntaxStatement extends JavaSyntax {

	/**
	 * statement構文インスタンスを作成する.
	 * @param classification 構文分類
	 */
	public JavaSyntaxStatement(eSyntaxClassification classification) {
		super(classification);
	}

}
