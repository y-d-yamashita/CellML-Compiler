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
	
	public String makeLeftFunc(MathExpression expression, Math_ci derivedVariable) throws MathException {
		
		ImplicitFunctionTransposition it = new ImplicitFunctionTransposition();
		expression = it.transporseExpression(expression, derivedVariable);
		String outputStr = "";
		outputStr=outputStr.concat("\treturn "+expression.getLeftExpression().toLegalString()+";\n");
		outputStr=outputStr.concat("\n");
		return outputStr;
		
	}
	
	public String makeDiffFunc(MathExpression expression, Math_ci derivedVariable) throws MathException {
		
		Differentiation diff = new Differentiation();
		MathExpression pDiffExpression = new MathExpression();
		
		ImplicitFunctionTransposition it = new ImplicitFunctionTransposition();
		expression = it.transporseExpression(expression, derivedVariable);
		
		//数式の左辺を微分して取得
		pDiffExpression = diff.differentiate(expression ,derivedVariable);
		
		String outputStr = "";
		outputStr=outputStr.concat("\treturn "+pDiffExpression.getLeftExpression().toLegalString()+";\n");
		outputStr=outputStr.concat("\n");
		return outputStr;
		
	}
	
	public String makeNewtonSolver(MathExpression expression, Math_ci derivedVariable, double e, int max) throws MathException {
		
		
		
		//含まれる変数リストを作成
		Vector<Math_ci> varList= new Vector<Math_ci>();
		expression.getAllVariablesWithSelector(varList);
		
		//main関数に記述される呼び出しサンプル
		
		String main = "";

		
		String outputStr = "";
		//ニュートン法計算関数

		outputStr=outputStr.concat("\tint max = 0;\n");
		outputStr=outputStr.concat("\tdouble eps;\n");
		outputStr=outputStr.concat("\tdouble " +derivedVariable.toLegalString()+"_next;\n");
		
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\tdo {\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\t\tmax ++;\n");
		outputStr=outputStr.concat("\t\tif(max > "+max+"){\n");
		outputStr=outputStr.concat("\t\t\tprintf(\"error:no convergence\\n\");break;\n");
		outputStr=outputStr.concat("\t\t}\n");
		outputStr=outputStr.concat("\t\t"+derivedVariable.toLegalString()+"_next = "+derivedVariable.toLegalString()+" - ( func"+expression.getExID()+ "(");
		
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat(varList.get(i).toLegalString());
			if(i!=varList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") / ");
		outputStr=outputStr.concat( "\tdfunc"+expression.getExID()+ "(");
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat(varList.get(i).toLegalString());
			if(i!=varList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") );\n");
		outputStr=outputStr.concat("\t\t"+derivedVariable.toLegalString()+" = "+derivedVariable.toLegalString()+"_next;\n");
		
		outputStr=outputStr.concat("\t\teps = func" +expression.getExID()+ "(");
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat(varList.get(i).toLegalString());
			if(i!=varList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(");\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\t} while( eps < "+-1.0*e+" || "+e+" < eps );\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\treturn "+ derivedVariable.toLegalString()+";\n");

		return outputStr;
	}
	public String writeNewtonSolver(MathExpression expression, Math_ci derivedVariable, double e, int max) throws MathException {
		
		Differentiation diff = new Differentiation();
		MathExpression pDiffExpression = new MathExpression();
		//数式の左辺を微分して取得
		pDiffExpression = diff.differentiate(expression ,derivedVariable);
		
		//含まれる変数リストを作成
		Vector<Math_ci> varList= new Vector<Math_ci>();
		expression.getAllVariablesWithSelector(varList);
		
		//main関数に記述される呼び出しサンプル
		
		String main = "";
		main=main.concat("main() {\n");
		for(int i=0;i<varList.size();i++){
			main=main.concat("\tdouble "+varList.get(i).toLegalString()+" = 1.0;\n");
		}
		
		main=main.concat("\t"+derivedVariable.toLegalString()+" = newton"+expression.getExID()+"(");
		for(int i=0;i<varList.size();i++){
			
			main=main.concat(varList.get(i).toLegalString());
			if(i!=varList.size()-1)main=main.concat(",");
		}
		main=main.concat(");\n");
		main=main.concat("}\n");
		main=main.concat("\n");
		
		
		String outputStr = "";
		//ニュートン法計算関数
		outputStr=outputStr.concat("double newton"+expression.getExID()+"(");
		
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat("double "+varList.get(i).toLegalString());
			if(i!=varList.size()-1)outputStr=outputStr.concat(",");
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
		
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat(varList.get(i).toLegalString());
			if(i!=varList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") / ");
		outputStr=outputStr.concat( "dfunc"+expression.getExID()+ "(");
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat(varList.get(i).toLegalString());
			if(i!=varList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") );\n");
		outputStr=outputStr.concat("\t\t"+derivedVariable.toLegalString()+" = "+derivedVariable.toLegalString()+"_next;\n");
		
		outputStr=outputStr.concat("\t\teps = func" +expression.getExID()+ "(");
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat(varList.get(i).toLegalString());
			if(i!=varList.size()-1)outputStr=outputStr.concat(",");
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
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat("double "+varList.get(i).toLegalString());
			if(i!=varList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") {\n");
		outputStr=outputStr.concat("\treturn "+expression.getLeftExpression().toLegalString()+";\n");
		outputStr=outputStr.concat("}\n");
		
		
		//左辺微分関数
		outputStr=outputStr.concat( "double dfunc"+expression.getExID()+ "(");
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat("double "+varList.get(i).toLegalString());
			if(i!=varList.size()-1)outputStr=outputStr.concat(",");
		}
		outputStr=outputStr.concat(") {\n");
		outputStr=outputStr.concat("\treturn "+pDiffExpression.getLeftExpression().toLegalString()+";\n");
		outputStr=outputStr.concat("}\n");
		
		
		//標準出力
		//System.out.println(main);
		//System.out.println(outputStr);
		
		return outputStr;
	}
public static void main(String[] args) throws MathException {
		
		//数式の属性情報
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
	
		//テスト用変数定義
		//テスト用変数定義
		Math_ci var1=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "A");
		Math_ci var2=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		//val2.addIndexList((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
		Math_ci var3=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		//val3.addIndexList((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
		Math_ci var4=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "C");
		Math_ci var5=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "D");
		Math_ci var6=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "E");
		
		
		
		Math_cn zero = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
		Math_cn num = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "5");
		MathExpression pNewExpression = new MathExpression();
		pNewExpression.setExID(7);
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
	
			
			
			//左辺追加
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("plus"), strAttr));
			
				//plus第１要素
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				pNewExpression.addOperand(var1);
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
				pNewExpression.addOperand(var2);
				pNewExpression.addOperand(num);
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				//plus第2要素
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				pNewExpression.addOperand(var3);
				pNewExpression.addOperand(var5);
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
	
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			
			//右辺追加
			pNewExpression.addOperand(zero);

		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

		
		
//		System.out.println("Input Function : ");
//		System.out.println(pNewExpression.getLeftExpression().toLegalString());
//		System.out.println("");

		//導出変数を設定
		Math_ci derivedVar = var1;
		
		double e = 1.0e-50;
		int max = 1000;
		NewtonSolver ns = new NewtonSolver();
		ns.makeNewtonSolver(pNewExpression, derivedVar, e, max);
		ns.makeLeftFunc(pNewExpression, derivedVar);
		ns.makeDiffFunc(pNewExpression, derivedVar);
		
		
	}
}
