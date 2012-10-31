package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.visitor;

import java.util.Stack;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;

public class MathFactorStackingVisitor implements Visitor {

	/** taverseで要素を格納するスタック*/
	private Stack<MathFactor> m_stack;
	
	public MathFactorStackingVisitor() {
		m_stack=new Stack<MathFactor>();
	}
	
	@Override
	public void visit(MathFactor factor) {
		m_stack.push(factor);
	}
	
	public Stack<MathFactor> getStack(){
		return m_stack;
	}
	
	public void clear(){
		m_stack.clear();
	}
 
}
