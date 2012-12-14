package jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility;

import java.util.HashSet;
import java.util.Iterator;


public class VariableListPrinter {

	public VariableListPrinter() {
		
	}
	
	/* print the variable declaration list for RelML */
	public void printRelMLVarList(HashSet<String> pRelVarList) {
		Iterator iter = pRelVarList.iterator();
		/* Extract elements from iterator, may not follow the order in which they are added to HashSet. */
		while(iter.hasNext()) {
			String varDec = "<variable name=\"" + iter.next() + "\" type=\"vartype\" />";
			System.out.println(varDec);
			iter.remove();
		}
		
	}
	
}
