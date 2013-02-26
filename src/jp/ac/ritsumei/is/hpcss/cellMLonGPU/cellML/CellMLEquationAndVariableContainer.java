package jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.VariableReference;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.VariableTable;

/**
 * Get variables and equations id from cellml to create graph
 * @author m-ara
 *
 */
public class CellMLEquationAndVariableContainer {
	private VariableTable cellMLVariableTable;
	private CellMLAnalyzer cellMLAnalyzer;
	private List<Integer> equationIdList;
	private List<Integer> variableIdList;
	private List<VariableReference> varRefList;

	
	public CellMLEquationAndVariableContainer(CellMLAnalyzer analyzer){
		cellMLAnalyzer=analyzer;
		try {
			cellMLVariableTable=analyzer.getVariableTable();
		} catch (TableException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		setEquationIDs();
		setVarRefList();
		setVariableIDs();

	}
	
	private void setEquationIDs(){
		equationIdList = new ArrayList<Integer>();
//		for(int i =0;i<simpleRecMLAnalyzer.getExpressionCount();i++)
//			equationIdList.add(i);
		
		for(int i =0;i<cellMLAnalyzer.getExpressionCount();i++){
			MathExpression ex = cellMLAnalyzer.getExpression(i);
			int id = (int)ex.getExID();
			equationIdList.add(id);
		}
		
	}
	
	private void setVariableIDs(){
		variableIdList = new ArrayList<Integer>();
		for(VariableReference r:varRefList){
			switch(r.getVarType()){
			case RVAR_TYPE_ARITHVAR:
				variableIdList.add(r.getID());
				break;
			case RVAR_TYPE_RECURVAR:
			case RVAR_TYPE_STEPVAR:
			case RVAR_TYPE_CONSTVAR:
			default:
				
			}
		}
		
	}
	
	private void setVarRefList(){
		varRefList =cellMLVariableTable.getSortedVariableReferencesList();
		
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
}
