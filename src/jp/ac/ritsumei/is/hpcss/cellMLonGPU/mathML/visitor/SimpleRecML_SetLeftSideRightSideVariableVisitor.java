package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.SimpleRecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.SimpleRecMLVariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.CCLogger;

public class SimpleRecML_SetLeftSideRightSideVariableVisitor implements Visitor {

	SimpleRecMLVariableTable table;
	Mode mode;
	int exprID;
	MathFactor leftSide;
	MathFactor rightSide;
	boolean ignoreExpNumFlag;
	
	public SimpleRecML_SetLeftSideRightSideVariableVisitor(SimpleRecMLVariableTable table) {
		this.table = table;
		mode=Mode.None;
		exprID=0;
		ignoreExpNumFlag = false;
	}
	enum Mode{
		Assign,
		Refer,
		None;
	}
	@Override
	public void visit(MathFactor factor) {
		
		if(!ignoreExpNumFlag && factor instanceof Math_apply){
			Math_apply apply = (Math_apply) factor;
			Integer num = apply.getExpNum();
			if(num != null){
				exprID=num;
			}
		}
		
			
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
	
	public void reset(int i){
		mode=Mode.None;
		exprID = i;
	}

	
	public SimpleRecMLVariableTable getTable(){
		return table;
	}
	
	public void setExpIdProperty(boolean flag){
		this.ignoreExpNumFlag = flag;
	}
}
