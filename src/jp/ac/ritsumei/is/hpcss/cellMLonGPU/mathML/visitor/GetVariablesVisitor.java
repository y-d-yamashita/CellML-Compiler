package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;

public class GetVariablesVisitor implements Visitor {

	Map<String,Math_ci> variableMap;

	public GetVariablesVisitor(){
		variableMap = new HashMap<String, Math_ci>();
	}
	public Set<Math_ci> geVariableSet(){
		return new HashSet<Math_ci>(variableMap.values());
	}
	public Map<String,Math_ci> geVariableMap(){
		return variableMap;
	}
	
	@Override
	public void visit(MathFactor factor) {
		if(factor instanceof Math_ci){

			Math_ci ci = (Math_ci) factor;
			String varName = null;
			try {
				varName = ci.toLegalString();
			} catch (MathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!variableMap.containsKey(varName)){
				variableMap.put(varName, ci);
			}

		}

	}
}