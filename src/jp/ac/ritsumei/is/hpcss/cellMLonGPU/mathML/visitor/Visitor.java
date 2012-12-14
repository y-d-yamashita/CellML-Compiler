package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;

public interface Visitor {
	public abstract void visit(MathFactor factor);
}
