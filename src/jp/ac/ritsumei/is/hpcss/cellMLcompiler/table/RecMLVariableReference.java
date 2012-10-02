package jp.ac.ritsumei.is.hpcss.cellMLcompiler.table;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;

/**
 * 変数参照クラス.
 */
public class RecMLVariableReference implements Comparable< RecMLVariableReference>{

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
	
	//List<MathExpression> refedExpressions;
	//List<MathExpression> assignExpressions;

	
	List<Integer> rightHandSideExpressions;
	List<Integer> leftHandSideExpressions;

	public RecMLVariableReference(){
		strVariableName=null;
		variable=null;
		rightHandSideExpressions=new ArrayList<Integer>();
		leftHandSideExpressions=new ArrayList<Integer>();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ").append(strVariableName);

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
	public int compareTo(RecMLVariableReference o) {
		if(this.id<o.id)
			return -1;
		else if(this.id==o.id)
			return 0;
		else
			return 1;
	}
}
