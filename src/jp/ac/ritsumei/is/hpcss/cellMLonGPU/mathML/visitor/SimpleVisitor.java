package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;

public class SimpleVisitor implements Visitor {

	@Override
	public void visit(MathFactor factor) {
		System.out.println("Class Name:"+factor.getClass().getName());
	}

}
