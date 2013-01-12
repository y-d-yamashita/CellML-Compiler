package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;

public class CountVariableNumberVisitor implements Visitor {

	int count;
	public CountVariableNumberVisitor(){
		count =0;
	}
	@Override
	public void visit(MathFactor factor) {
		if(factor instanceof Math_ci){
				count++;
		}
	}
	
	public int getVariableCount(){
		return count;
	}

}
