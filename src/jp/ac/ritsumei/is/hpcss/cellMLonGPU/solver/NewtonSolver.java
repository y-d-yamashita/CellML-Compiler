package jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;

/**
 * 数式ライブラリ対応 Newton法ソルバーテストクラス
 * @author n-washio
 * 
 * plus(1,2項,多項)
 * minus(1,2項,多項)
 * times(2項,多項)
 * divide
 * sin,cos,tan
 * inc,dec
 * power,root,exp
 * ln,log
 * 
 */
public class NewtonSolver {
	public void writeNewtonSolver(MathExpression expression, Math_ci derivedVariable, double e) throws MathException {
		
		Differentiation diff = new Differentiation();
		MathExpression pDiffExpression = new MathExpression();
		//数式の左辺を微分して取得
		pDiffExpression = diff.differentiate(expression ,derivedVariable);
		
		//含まれる変数リストを作成
		Vector<Math_ci> valList= new Vector<Math_ci>();
		expression.getAllVariables(valList);
		
		//main関数に記述される構文
		System.out.print(derivedVariable.toLegalString()+" = newton"+expression.getExID()+"(");
		for(int i=0;i<valList.size();i++){
			System.out.print(valList.get(i).toLegalString());
			if(i!=valList.size()-1)System.out.print(",");
		}
		System.out.println(");");
		System.out.println();
		
		
		
		//ニュートン法計算関数
		System.out.print("double newton"+expression.getExID()+"(");
		
		for(int i=0;i<valList.size();i++){
			System.out.print("double "+valList.get(i).toLegalString());
			if(i!=valList.size()-1)System.out.print(",");
		}
		System.out.println(") {");
		System.out.println();
		System.out.println("\tdouble e;");
		System.out.println("\tdouble " +derivedVariable.toLegalString()+"_next;");
		
		System.out.println();
		System.out.println("\tdo {");
		System.out.print("\t\t"+derivedVariable.toLegalString()+"_next = "+derivedVariable.toLegalString()+" - ( func"+expression.getExID()+ "(");
		
		for(int i=0;i<valList.size();i++){
			System.out.print(valList.get(i).toLegalString());
			if(i!=valList.size()-1)System.out.print(",");
		}
		System.out.print(") / ");
		System.out.print( "dfunc"+expression.getExID()+ "(");
		for(int i=0;i<valList.size();i++){
			System.out.print(valList.get(i).toLegalString());
			if(i!=valList.size()-1)System.out.print(",");
		}
		System.out.println(") );");
		System.out.println("\t\te = "+derivedVariable.toLegalString()+"_next - "+derivedVariable.toLegalString()+";");
		
		System.out.println("\t\t"+derivedVariable.toLegalString()+" = "+derivedVariable.toLegalString()+"_next;");
		
		System.out.println("\t} while( e > "+e+" );" );
		System.out.println();
		System.out.println("\treturn "+ derivedVariable.toLegalString()+";");
		System.out.println("}");
		System.out.println();
		
		
		
		//左辺関数
		System.out.print( "double func"+expression.getExID()+ "(");
		for(int i=0;i<valList.size();i++){
			System.out.print("double "+valList.get(i).toLegalString());
			if(i!=valList.size()-1)System.out.print(",");
		}
		System.out.println(") {");
		System.out.println("\treturn "+expression.getLeftExpression().toLegalString()+";");
		System.out.println("}");
		
		
		//左辺微分関数
		System.out.print( "double dfunc"+expression.getExID()+ "(");
		for(int i=0;i<valList.size();i++){
			System.out.print("double "+valList.get(i).toLegalString());
			if(i!=valList.size()-1)System.out.print(",");
		}
		System.out.println(") {");
		System.out.println("\treturn "+pDiffExpression.getLeftExpression().toLegalString()+";");
		System.out.println("}");
		
	}
}
