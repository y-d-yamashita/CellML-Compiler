package jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.visitor;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.SimpleRecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.table.SimpleRecMLVariableTable;

public class CreateSimpleRecMLVariableTableVisitor implements Visitor {

	SimpleRecMLVariableTable table;
	int id;
	public CreateSimpleRecMLVariableTableVisitor() {
		table = new SimpleRecMLVariableTable("SimpleRecMLVariableTable");
		id=0;
	}
	@Override
	public void visit(MathFactor factor) {			
		if(factor instanceof Math_ci){
			Math_ci variable = (Math_ci) factor;
			try {
				if(!table.isContain(variable.toLegalString())){
					table.insert(variable,id);
					id++;
				}
			} catch (MathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
	}
	
	public SimpleRecMLVariableTable getTable(){
		return table;
	}

}
