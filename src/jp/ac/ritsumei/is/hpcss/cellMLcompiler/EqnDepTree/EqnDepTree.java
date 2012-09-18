package jp.ac.ritsumei.is.hpcss.cellMLcompiler.EqnDepTree;

import java.util.Stack;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;

public class EqnDepTree {
	public int loopNumber;
	public EqnDepTree n_pre, n_init, n_inner, n_loopcond, n_final, n_post;
	private Stack<EqnDepTree> nodeStack;
	
	public EqnDepTree(int loopNum) {
		loopNumber = loopNum;
		nodeStack = new Stack<EqnDepTree>();
	}
	
	public void push(EqnDepTree newNode) {
		this.nodeStack.push(newNode);
	}
	
	public EqnDepTree pop() {
		return this.nodeStack.pop();
	}
	
	public boolean emptyStack() {
		return this.nodeStack.empty();
	}
	
	public void printString() {
		System.out.println(this.loopNumber + " pre");
		if (this.n_pre != null) {
			this.n_pre.printString();
		}
		System.out.println(this.loopNumber + " init");
		if (this.n_init != null) {
			this.n_init.printString();
		}
		System.out.println(this.loopNumber + " inner");
		if (this.n_inner != null) {
			this.n_inner.printString();
		}
		System.out.println(this.loopNumber + " loopcond");
		if (this.n_loopcond != null) {
			this.n_loopcond.toString();
		}
		System.out.println(this.loopNumber + " finall");
		if (this.n_final != null) {
			this.n_final.toString();
		}
		System.out.println(this.loopNumber + " post");
		if (this.n_post != null) {
			this.n_post.toString();
		}
	}
	
	public void printString(String indent) {
		System.out.println(indent+this.loopNumber + " pre");
		if (this.n_pre != null) {
			this.n_pre.printString(indent+indent);
		}
		System.out.println(indent+this.loopNumber + " init");
		if (this.n_init != null) {
			this.n_init.printString(indent+indent);
		}
		System.out.println(indent+this.loopNumber + " inner");
		if (this.n_inner != null) {
			this.n_inner.printString(indent+indent);
		}
		System.out.println(indent+this.loopNumber + " loopcond");
		if (this.n_loopcond != null) {
			this.n_loopcond.printString(indent+indent);
		}
		System.out.println(indent+this.loopNumber + " finall");
		if (this.n_final != null) {
			this.n_final.printString(indent+indent);
		}
		System.out.println(this.loopNumber + " post");
		if (this.n_post != null) {
			this.n_post.printString(indent+indent);
		}
	}
	
	
	int exprIndex=0;	
	public void printString(Vector<MathExpression> m_vecMathExpression
			,String indent) throws MathException {
		System.out.println(indent+"Loop["+this.loopNumber + "]<pre>:  "+m_vecMathExpression.get(exprIndex).toLegalString());
		exprIndex++;
		if (this.n_pre != null) {
			this.n_pre.printString(m_vecMathExpression,indent+indent);
		}
		System.out.println(indent+"Loop["+this.loopNumber + "]<init>:  "+m_vecMathExpression.get(exprIndex).toLegalString());
		exprIndex++;
		if (this.n_init != null) {
			this.n_init.printString(m_vecMathExpression,indent+indent);
		}
		System.out.println(indent+"Loop["+this.loopNumber + "]<inner>:  "+m_vecMathExpression.get(exprIndex).toLegalString());
		exprIndex++;
		if (this.n_inner != null) {
			this.n_inner.printString(m_vecMathExpression,indent+indent);
		}
		System.out.println(indent+"Loop["+this.loopNumber + "]<loopcond>:  "+m_vecMathExpression.get(exprIndex).toLegalString());
		exprIndex++;
		if (this.n_loopcond != null) {
			this.n_loopcond.printString(m_vecMathExpression,indent+indent);
		}
		System.out.println(indent+"Loop["+this.loopNumber + "]<final>:  "+m_vecMathExpression.get(exprIndex).toLegalString());
		exprIndex++;
		if (this.n_final != null) {
			this.n_final.printString(m_vecMathExpression,indent+indent);
		}
		System.out.println(indent+"Loop["+this.loopNumber + "]<post>:  "+m_vecMathExpression.get(exprIndex).toLegalString());
		exprIndex++;
		if (this.n_post != null) {
			this.n_post.printString(m_vecMathExpression,indent+indent);
		}
	}
	

	
	
	public void start() {
		EqnDepTree root = new EqnDepTree(0);
		EqnDepTree now = root;
		
		root.push(now);
		  now.n_pre = new EqnDepTree(2);
		  now = now.n_pre;
		  //now.printString();
		  // process final
		now = root.pop();

		root.push(now);
		  now.n_init = new EqnDepTree(4);
		  now = now.n_init;
		  //now.printString();
		  // process final
		now = root.pop();

		root.push(now);
		  now.n_post = new EqnDepTree(5);
		  now = now.n_post;
		  //now.printString();
		  // process final
		now = root.pop();

		root.push(now);
		  now.n_inner = new EqnDepTree(1);
		  now = now.n_inner;
		  // process now
		  root.push(now);
		    now.n_inner = new EqnDepTree(3);
		    now = now.n_inner;
		    // process now
		  now = root.pop();
		now = root.pop();
		if (now == root) {
			System.out.println("true");
		}
		
		root.printString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EqnDepTree edt = new EqnDepTree(0);
		edt.start();
	}

}
