package jp.ac.ritsumei.is.hpcss.cellMLonGPU.table;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML.RelMLDefinition.eRelMLVarType;

/**
 * 変数参照クラス.
 */
public class VariableReference implements Comparable< VariableReference>{

	/**変数名*/
	String strVariableName;
	/**親コンポーネントの変数テーブル*/
	VariableTable pParentTable;
	/**コネクション先参照構造体*/
	VariableReference pConnection;
	/**初期値文字列*/
	String strInitValue;

	Math_ci variable;
	
	private eRelMLVarType variableType;
	
	int id;
	
	List<Integer> rightHandSideExpressions;
	List<Integer> leftHandSideExpressions;

	public VariableReference(){
		strVariableName=null;
		variable=null;
		rightHandSideExpressions=new ArrayList<Integer>();
		leftHandSideExpressions=new ArrayList<Integer>();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ").append(strVariableName).append("  ");

		sb.append("  ").append("AssignExpr: ");
		if(leftHandSideExpressions.isEmpty()){
			sb.append("None");
		}else{
			for(Integer id:leftHandSideExpressions)
				sb.append(id).append(", ");
			sb.setLength(sb.length()-2);
		}
		
		sb.append("  ").append("RefExpr: ");
		if(rightHandSideExpressions.isEmpty()){
			sb.append("None");
		}else{
			for(Integer id:rightHandSideExpressions)
			sb.append(id).append(", ");
		sb.setLength(sb.length()-2);
		}
		
		sb.append("\n");
		return sb.toString();
	}
	
	public List<Integer> getRightHandSideExpressionIDs(){
		return rightHandSideExpressions;
	}
	public List<Integer> getLeftHandSideExpressionIDs(){
		return leftHandSideExpressions;
	}

	
	public void addRightHandSideExpression(int expr){
		rightHandSideExpressions.add(expr);
	}
	public void addLeftHandSideExpression(int expr){
		leftHandSideExpressions.add(expr);
	}
	public void setID(int id){
		this.id=id;
	}
	public int getID(){return id;}

	public int compareTo(VariableReference o) {
		if(this.id<o.id)
			return -1;
		else if(this.id==o.id)
			return 0;
		else
			return 1;
	}
	
	public Math_ci getMathCI(){
		return variable;
	}
	public void setMathCI(Math_ci ci){
		this.variable=ci;
	}
	
	public eRelMLVarType getVarType() {
		return variableType;
	}

	public void setRelMLVarType(eRelMLVarType relMLVarType) {
		this.variableType = relMLVarType;
	}
	
	public String getVariableName(){
		return this.strVariableName;
	}
}
