package jp.ac.ritsumei.is.hpcss.cellMLcompiler.recML;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.RecMLException;

/**
 * RecML Graph definitions
 */
public class RecMLGraphDefinition {
	/* RecML tag names */
	public static final String RECML_TAG_STR_RECML = RecMLDefinition.RECML_TAG_STR_RECML;
	
	/* Graph */
	public static final String RECML_TAG_STR_GRAPH = "graph";

	public static final String RECML_TAG_STR_NODES = "nodes";
	public static final String RECML_TAG_STR_NODE = "node";
	public static final String RECML_TAG_STR_VARIABLE = RecMLDefinition.RECML_TAG_STR_VARIABLE;
	public static final String RECML_TAG_STR_EQUATIN = "equation";

	public static final String RECML_TAG_STR_EDGES = "edges";
	public static final String RECML_TAG_STR_EDGE = "edge";
	public static final String RECML_TAG_STR_SOURCE= "source";
	public static final String RECML_TAG_STR_DEST 	= "dest";

/**
	 * 
	 * @author y-yamashita
	 *
	 */
	public enum eRecMLGraphTag{
		CTAG_RECML		(RECML_TAG_STR_RECML),
		CTAG_GRAPH		(RECML_TAG_STR_GRAPH),
		CTAG_NODES		(RECML_TAG_STR_NODES),
		CTAG_NODE		(RECML_TAG_STR_NODE),
		CTAG_VARIABLE	(RECML_TAG_STR_VARIABLE),
		CTAG_EQUATION	(RECML_TAG_STR_EQUATIN),
		CTAG_EDGES		(RECML_TAG_STR_EDGES),
		CTAG_EDGE		(RECML_TAG_STR_EDGE),
		CTAG_SOURCE		(RECML_TAG_STR_SOURCE),
		CTAG_DEST		(RECML_TAG_STR_DEST)
			;
		private final String operatorStr;
		private eRecMLGraphTag(String operatorstr) {
			operatorStr = operatorstr;
		}
		private String getOperatorStr() {
			return operatorStr;
		}

	};
		/**
		 * 
		 * @param strTag
		 * @return
		 * @throws RecMLException
		 * @author y-yamashita
		 */
	public static eRecMLGraphTag getRecMLGraphTagId(String strTag)
		throws RecMLException {
			/*演算子と比較*/
			for (eRecMLGraphTag t: eRecMLGraphTag.values()) {
				if (t.getOperatorStr().equals(strTag)) {
					return t;
				}
			}

		/*見つからなければ例外*/
		throw new RecMLException("","getRecMLTagId",
					  "invalid RecML tag : " + strTag);
	}

}