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
	public void writeNewtonSolver(MathExpression expression, Math_ci derivedVariable, double e, int max) throws MathException {
		
		Differentiation diff = new Differentiation();
		MathExpression pDiffExpression = new MathExpression();
		//数式の左辺を微分して取得
		pDiffExpression = diff.differentiate(expression ,derivedVariable);
		
		//含まれる変数リストを作成
		Vector<Math_ci> valList= new Vector<Math_ci>();
		expression.getAllVariables(valList);
		
		//main関数に記述される呼び出しサンプル
		
		String main = "";
		main=main.concat("main() {\n");
		for(int i=0;i<valList.size();i++){
			main=main.concat("\tdouble "+valList.get(i).toLegalString()+" = 1.0;\n");
		}
		
		main=main.concat("\t"+derivedVariable.toLegalString()+" = newton"+expression.getExID()+"(");
		for(int i=0;i<valList.size();i++){
			
			main=main.concat(valList.get(i).toLegalString());
			if(i!=valList.size()-1)main=main.concat(",");
		}
		main=main.concat(");\n");
		main=main.concat("}\n");
		main=main.concat("\n");
		
		
		String outputStr = "";
		//ニュートン法計算関数
		outputStr=outputStr.concat("double newton"+expression.getExID()+"(");
		
		for(int i=0;i<valList.size();i++){
			outputStr=outputStr.concat("double "+valList.get(i).toLegalString());
			if(i!=valList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") {\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\tint max = 0;\n");
		outputStr=outputStr.concat("\tdouble eps;\n");
		outputStr=outputStr.concat("\tdouble " +derivedVariable.toLegalString()+"_next;\n");
		
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\tdo {\n");
		outputStr=outputStr.concat("\t\tmax ++;\n");
		outputStr=outputStr.concat("\t\tif(max > "+max+"){\n");
		outputStr=outputStr.concat("\t\t\tprintf(\"error:no convergence\\n\");break;\n");
		outputStr=outputStr.concat("\t\t}\n");
		outputStr=outputStr.concat("\t\t"+derivedVariable.toLegalString()+"_next = "+derivedVariable.toLegalString()+" - ( func"+expression.getExID()+ "(");
		
		for(int i=0;i<valList.size();i++){
			outputStr=outputStr.concat(valList.get(i).toLegalString());
			if(i!=valList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") / ");
		outputStr=outputStr.concat( "dfunc"+expression.getExID()+ "(");
		for(int i=0;i<valList.size();i++){
			outputStr=outputStr.concat(valList.get(i).toLegalString());
			if(i!=valList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") );\n");
		outputStr=outputStr.concat("\t\t"+derivedVariable.toLegalString()+" = "+derivedVariable.toLegalString()+"_next;\n");
		
		outputStr=outputStr.concat("\t\teps = func" +expression.getExID()+ "(");
		for(int i=0;i<valList.size();i++){
			outputStr=outputStr.concat(valList.get(i).toLegalString());
			if(i!=valList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(");\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\t} while( eps < "+-1.0*e+" || "+e+" < eps );\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\treturn "+ derivedVariable.toLegalString()+";\n");
		outputStr=outputStr.concat("}\n");
		outputStr=outputStr.concat("\n");
		
		//左辺関数
		outputStr=outputStr.concat( "double func"+expression.getExID()+ "(");
		for(int i=0;i<valList.size();i++){
			outputStr=outputStr.concat("double "+valList.get(i).toLegalString());
			if(i!=valList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") {\n");
		outputStr=outputStr.concat("\treturn "+expression.getLeftExpression().toLegalString()+";\n");
		outputStr=outputStr.concat("}\n");
		
		
		//左辺微分関数
		outputStr=outputStr.concat( "double dfunc"+expression.getExID()+ "(");
		for(int i=0;i<valList.size();i++){
			outputStr=outputStr.concat("double "+valList.get(i).toLegalString());
			if(i!=valList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") {\n");
		outputStr=outputStr.concat("\treturn "+pDiffExpression.getLeftExpression().toLegalString()+";\n");
		outputStr=outputStr.concat("}\n");
		
		
		//標準出力
		System.out.println(main);
		System.out.println(outputStr);
	}
}
