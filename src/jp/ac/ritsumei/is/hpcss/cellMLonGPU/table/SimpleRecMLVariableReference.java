package jp.ac.ritsumei.is.hpcss.cellMLonGPU.table;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.recML.SimpleRecMLDefinition.eRecMLVarType;

/**
 * 変数参照クラス.
 */
public class SimpleRecMLVariableReference implements Comparable< SimpleRecMLVariableReference>{

	/**変数名*/
	String strVariableName;
	/**親コンポーネントの変数テーブル*/
	//VariableTable pParentTable;
	/**コネクション先参照構造体*/
	//RecMLVariableReference pConnection;
	/**初期値文字列*/
	//String strInitValue;
	
	Math_ci variable;
	
	int id;
	
	private eRecMLVarType simpleRecMLVarType;
	
	//List<MathExpression> refedExpressions;
	//List<MathExpression> assignExpressions;

	
	List<Integer> rightHandSideExpressions;
	List<Integer> leftHandSideExpressions;

	public SimpleRecMLVariableReference(){
		strVariableName=null;
		variable=null;
		rightHandSideExpressions=new ArrayList<Integer>();
		leftHandSideExpressions=new ArrayList<Integer>();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ").append(strVariableName).append("  ");
		sb.append("Type: ").append(this.getRecMLVarType());

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

	@Override
	public int compareTo(SimpleRecMLVariableReference o) {
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

	public eRecMLVarType getRecMLVarType() {
		return simpleRecMLVarType;
	}

	public void setRecMLVarType(eRecMLVarType recMLVarType) {
		this.simpleRecMLVarType = recMLVarType;
	}
}
