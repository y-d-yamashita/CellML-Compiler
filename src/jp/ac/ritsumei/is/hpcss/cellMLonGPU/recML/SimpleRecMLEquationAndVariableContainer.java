package jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.SimpleRecMLVariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.SimpleRecMLVariableTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.CCLogger;

/**
 * Get variables and equations id to create graph
 * @author 
 *
 */
public class SimpleRecMLEquationAndVariableContainer {

	private SimpleRecMLVariableTable simpleRecMLVariableTable;
	private SimpleRecMLAnalyzer simpleRecMLAnalyzer;
	private List<Integer> equationIdList;
	private List<Integer> variableIdList;
	private List<SimpleRecMLVariableReference> varRefList;
	private boolean ignoreFlag;
	
	public SimpleRecMLEquationAndVariableContainer(SimpleRecMLAnalyzer analyzer,SimpleRecMLVariableTable table){
		simpleRecMLVariableTable=table;
		simpleRecMLAnalyzer=analyzer;
		ignoreFlag = false;
		
		setEquationIDs();
		setVarRefList();
		setVariableIDs();
	}
	
	public SimpleRecMLEquationAndVariableContainer(SimpleRecMLAnalyzer analyzer,SimpleRecMLVariableTable table,boolean flag){
		simpleRecMLVariableTable=table;
		simpleRecMLAnalyzer=analyzer;
		ignoreFlag = flag;
			
		setEquationIDs();
		setVarRefList();
		setVariableIDs();
	}

	

	private void setEquationIDs(){
		equationIdList = new ArrayList<Integer>();

		for(int i =0;i<simpleRecMLAnalyzer.getExpressionCount();i++){
			if(ignoreFlag || simpleRecMLAnalyzer.getExpressionNumfromApply(i)==null){
				equationIdList.add(i);
			}else{
				int id = simpleRecMLAnalyzer.getExpressionNumfromApply(i);
				equationIdList.add(id);
			}
		}
		
	}
	
	private void setVariableIDs(){
		variableIdList = new ArrayList<Integer>();
		for(SimpleRecMLVariableReference r:varRefList){
			try{
			switch(r.getRecMLVarType()){
			case CVAR_TYPE_RECURVAR:
			case CVAR_TYPE_ARITHVAR:
			case CVAR_TYPE_OUTPUT:
			case CVAR_TYPE_CONSTVAR:
				variableIdList.add(r.getID());
				break;
			case CVAR_TYPE_STEPVER:
			case CVAR_TYPE_DOUBLE:
//			case CVAR_TYPE_CONSTVAR:
			default:
				
			}
			}catch(NullPointerException e){
				CCLogger.log("Type is null. "+ r.toString());
				e.getStackTrace();
			}
		}
		
	}
	
	private void setVarRefList(){
		varRefList = simpleRecMLVariableTable.getSortedRecMLVariableReferencesList();
		
	}
	
	/**
	 * Get equations IDs
	 * @return equation ID list
	 */
	public List<Integer> getEquationIDs(){
			
		return equationIdList;
	}
	
	
	/**
	 * Get variable IDs
	 * @return variable ID list
	 */
	public List<Integer> getVariableIDs(){
		
		return variableIdList;
	}
	
	
	/**
	 * Get variable IDs used in a equation
	 * @param equationID
	 * @return variable ID list
	 */
	public List<Integer> getEqautionIDsOfVariable(int variableID){
		List<Integer> equIdList = new ArrayList<Integer>();
		HashSet<Integer> leftSideHashSet = new HashSet<Integer>();
		HashSet<Integer> rightSideHashSet = new HashSet<Integer>();
		
		leftSideHashSet.addAll(varRefList.get(variableID).getLeftHandSideExpressionIDs());
		rightSideHashSet.addAll(varRefList.get(variableID).getRightHandSideExpressionIDs());
		
		equIdList.addAll(leftSideHashSet);
		equIdList.addAll(rightSideHashSet);

		return equIdList;		
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("<RecMLEquationAndVariableContener>---------------\n");
		sb.append("Equation IDs:").append(getEquationIDs()).append("\n");
		sb.append("Variable IDs:").append(getVariableIDs()).append("\n");
		for(Integer i:getVariableIDs())
			sb.append("Variable[").append(i).append("]:").append(getEqautionIDsOfVariable(i)).append("\n");
		
		return sb.toString();
	}
	public MathExpression getEquation(int equationID){
		return simpleRecMLAnalyzer.getExpression(equationID);
	}

}
