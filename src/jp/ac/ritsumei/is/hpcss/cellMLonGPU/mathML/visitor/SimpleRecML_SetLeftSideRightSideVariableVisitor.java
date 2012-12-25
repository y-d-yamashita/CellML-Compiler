package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.SimpleRecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.SimpleRecMLVariableTable;

public class SimpleRecML_SetLeftSideRightSideVariableVisitor implements Visitor {

	SimpleRecMLVariableTable table;
	Mode mode;
	int exprID;
	MathFactor leftSide;
	MathFactor rightSide;
	
	public SimpleRecML_SetLeftSideRightSideVariableVisitor(SimpleRecMLVariableTable table) {
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
			SimpleRecMLVariableReference variable = null;
		
			
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
	public SimpleRecMLVariableTable getTable(){
		return table;
	}

}
