package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;


public class ReplacePartOfVariableNameVisitor implements Visitor {

	private String regex;
	private String replacement;
	
	public ReplacePartOfVariableNameVisitor(String regex,String replacement){
		this.regex=regex;
		this.replacement=replacement;
	}

	
	public void visit(MathFactor factor) {
		if(factor instanceof Math_ci){
			Math_ci ci = (Math_ci) factor;
			ci.replaceStrPresentExt(regex, replacement);
			}
		
	}

}
