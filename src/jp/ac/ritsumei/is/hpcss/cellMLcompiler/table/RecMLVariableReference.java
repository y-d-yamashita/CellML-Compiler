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

	
	List<Integer> refExpressions;
	List<Integer> assignExpressions;

	public RecMLVariableReference(){
		strVariableName=null;
		variable=null;
		refExpressions=new ArrayList<Integer>();
		assignExpressions=new ArrayList<Integer>();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ").append(strVariableName);

		sb.append("  ").append("AssignExpr: ");
		if(assignExpressions.isEmpty()){
			sb.append("None");
		}else{
			for(Integer id:assignExpressions)
				sb.append(id).append(", ");
			sb.setLength(sb.length()-2);
		}
		
		sb.append("  ").append("RefExpr: ");
		if(refExpressions.isEmpty()){
			sb.append("None");
		}else{
			for(Integer id:refExpressions)
			sb.append(id).append(", ");
		sb.setLength(sb.length()-2);
		}
		
		sb.append("\n");
		return sb.toString();
	}
	public void addRefExpression(int expr){
		refExpressions.add(expr);
	}
	public void addAssignExpression(int expr){
		assignExpressions.add(expr);
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
