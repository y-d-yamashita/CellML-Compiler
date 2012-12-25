package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;

public class ReplacePartOfVariableNameVisitor implements Visitor {

	private String regex;
	private String replacement;
	
	public ReplacePartOfVariableNameVisitor(String regex,String replacement){
		this.regex=regex;
		this.replacement=replacement;
	}

	
	@Override
	public void visit(MathFactor factor) {
		if(factor instanceof Math_ci){
			Math_ci ci = (Math_ci) factor;
			ci.replaceStrPresentExt(regex, replacement);
			}
		
	}

}
