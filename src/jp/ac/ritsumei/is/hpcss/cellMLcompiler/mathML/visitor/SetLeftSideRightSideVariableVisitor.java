package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.RecMLVariableTable;

public class SetLeftSideRightSideVariableVisitor implements Visitor {

	RecMLVariableTable table;
	Mode mode;
	int exprID;
	MathFactor leftSide;
	MathFactor rightSide;
	
	public SetLeftSideRightSideVariableVisitor(RecMLVariableTable table) {
		this.table = table;
		mode=Mode.None;
		exprID=0;
	}
	enum Mode{
		Assign,
		Refer,
		None;
	}
	@Override
	public void visit(MathFactor factor) {		
		
			
		if(factor instanceof Math_eq){
			Math_eq eq=(Math_eq) factor;
			leftSide=eq.getLeftExpression();
			rightSide=eq.getRightExpression();
		}else if(factor==leftSide){
			mode=Mode.Assign;
		}else if(factor==rightSide){
			mode=Mode.Refer;
		}
		if(factor instanceof Math_ci){
			RecMLVariableReference variable = null;
		
			
			try {
				variable=table.find((Math_ci) factor);
			} catch (TableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
				switch(mode){
				case Refer:
					variable.addRightHandSideExpression(exprID);
					break;
				case Assign:
					variable.addLeftHandSideExpression(exprID);
					break;
				}
		}

	}
	public void reset(){
		mode=Mode.None;
		exprID++;
	}
	public RecMLVariableTable getTable(){
		return table;
	}

}
