package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;

public interface Visitor {
	public abstract void visit(MathFactor factor);
}
