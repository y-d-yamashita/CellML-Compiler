package jp.ac.ritsumei.is.hpcss.cellMLonGPU.graph.exception;

/**
 * Graph exception class
 * @author y-yamashita
 *
 */
public class GraphException extends Exception{

	/**
	 * Constructor
	 */
	public GraphException() {
	}
	/**
	 * Constructor
	 * @param message
	 */
	public GraphException(String message) {
		super(message);
	}

}
