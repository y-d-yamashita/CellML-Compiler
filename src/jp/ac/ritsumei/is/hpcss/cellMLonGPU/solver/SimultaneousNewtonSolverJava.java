package jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;

/**
 *Simultaneous Newton-Solver for Java class
 *
 *  limitation of expression:
 * 	The operator contained in expression limits to the following one. 
 *	-plus,minus,times,divide,inc,dec,exp,ln,root,power,log,sin,cos,tan
 * 
 * @author n-washio
 * 
 * 
 */



public class SimultaneousNewtonSolverJava {
	
	
	
	public String makeSimultaneousNewtonSolver(Vector<MathExpression> expList, Vector<Math_ci> varList, double e, int max, int id) throws MathException {
		
		int n= varList.size();
		
		//含まれる変数リスト(導出する変数以外)を作成
		Vector<Math_ci> vList= new Vector<Math_ci>();
		Vector<Math_ci> v2List= new Vector<Math_ci>();
		
		for(int i=0;i<expList.size();i++){
			expList.get(i).getAllVariablesWithSelector(vList);
			for(int j=0;j<vList.size();j++){
				boolean flag = false;
				for(int k=0;k<v2List.size();k++){
					if(vList.get(j).toLegalJavaString().equals(v2List.get(k).toLegalJavaString())) flag = true;
				}
				if(!flag){
					boolean d_flag=false;
					//導出変数かどうか判定
					for(int d=0;d<varList.size();d++){
						if(varList.get(d).toLegalJavaString().equals(vList.get(j).toLegalJavaString())){
							d_flag=true;
						}
					}
					
					if(!d_flag)v2List.add(vList.get(j));
				}
			}
		}
				
		//ニュートン法計算関数
		String outputStr = "";
		
		
		outputStr=outputStr.concat("\tint max = 0;\n");
		outputStr=outputStr.concat("\tint i, j, k;\n");
		outputStr=outputStr.concat("\tdouble buf;\n");
		outputStr=outputStr.concat("\tdouble det;\n");
		outputStr=outputStr.concat("\tdouble pro;\n");
		outputStr=outputStr.concat("\tdouble eps;\n");
		outputStr=outputStr.concat("\tdouble[] f = new double["+n+"];\n");
		outputStr=outputStr.concat("\tdouble[][] jac = new double["+n+"]["+n+"];\n");
		outputStr=outputStr.concat("\tdouble[][] cpy = new double["+n+"]["+n+"];\n");
		outputStr=outputStr.concat("\tdouble[][] inv = new double["+n+"]["+n+"];\n");
		outputStr=outputStr.concat("\tdouble[] simulSet_next = new double["+n+"];\n");
		outputStr=outputStr.concat("\n\n");
		outputStr=outputStr.concat("\tdo {\n");
		outputStr=outputStr.concat("\t\tmax ++;\n");
		outputStr=outputStr.concat("\t\tif(max > "+max+"){\n");
		outputStr=outputStr.concat("\t\t\tSystem.out.println(\"error:no convergence\\n\");break;\n");
		outputStr=outputStr.concat("\t\t}\n");
		outputStr=outputStr.concat("\t\tdet = 1.0;\n");
		outputStr=outputStr.concat("\t\teps = 0.0;\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tfor(j=0;j<"+n+";j++){\n");
		outputStr=outputStr.concat("\t\t\t\tjac[i][j] = jacobi"+id+"(simulSet,");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat(v2List.get(j).getCodeName()+",");
		}
		outputStr=outputStr.concat("i,j);\n");
		outputStr=outputStr.concat("\t\t\t\tcpy[i][j] = jacobi"+id+"(simulSet,");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat(v2List.get(j).getCodeName()+",");
		}
		outputStr=outputStr.concat("i,j);\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\t\t\t}\n");
		outputStr=outputStr.concat("\t\t}\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tfor(j=0;j<"+n+";j++){\n");
		outputStr=outputStr.concat("\t\t\t\tif(i<j){\n");
		outputStr=outputStr.concat("\t\t\t\t\tbuf = cpy[j][i] / cpy[i][i];\n");
		outputStr=outputStr.concat("\t\t\t\t\tfor(k=0;k<"+n+";k++){\n");
		outputStr=outputStr.concat("\t\t\t\t\t\tcpy[j][k] -= cpy[i][k] * buf;\n");
		outputStr=outputStr.concat("\t\t\t\t\t}\n");
		outputStr=outputStr.concat("\t\t\t\t}\n");
		outputStr=outputStr.concat("\t\t\t}\n");
		outputStr=outputStr.concat("\t\t}\n");
		
		
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tdet *= cpy[i][i];\n");
		outputStr=outputStr.concat("\t\t}\n");
		outputStr=outputStr.concat("\t\tif(det == 0.0){\n");
		outputStr=outputStr.concat("\t\t\tSystem.out.println(\"error:det is zero\\n\");break;\n");
		outputStr=outputStr.concat("\t\t}\n");
		
		//単位行列を作成
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tfor(j=0;j<"+n+";j++){\n");
		outputStr=outputStr.concat("\t\t\t\tif(i==j) inv[i][j] = 1.0;\n");
		outputStr=outputStr.concat("\t\t\t\telse inv[i][j] = 0.0;\n");
		outputStr=outputStr.concat("\t\t\t}\n");
		outputStr=outputStr.concat("\t\t}\n");
		
		//掃き出し法による逆行列生成
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tbuf = 1 / jac[i][i];\n");
		outputStr=outputStr.concat("\t\t\tfor(j=0;j<"+n+";j++){\n");
		outputStr=outputStr.concat("\t\t\t\tjac[i][j] *= buf;\n");
		outputStr=outputStr.concat("\t\t\t\tinv[i][j] *= buf;\n");
		outputStr=outputStr.concat("\t\t\t}\n");
		outputStr=outputStr.concat("\t\t\tfor(j=0;j<"+n+";j++){\n");
		outputStr=outputStr.concat("\t\t\t\tif(i!=j){\n");
		outputStr=outputStr.concat("\t\t\t\t\tbuf = jac[j][i];\n");
		outputStr=outputStr.concat("\t\t\t\t\tfor(k=0;k<"+n+";k++){\n");
		outputStr=outputStr.concat("\t\t\t\t\t\tjac[j][k] -= jac[i][k]*buf;\n");
		outputStr=outputStr.concat("\t\t\t\t\t\tinv[j][k] -= inv[i][k]*buf;\n");
		outputStr=outputStr.concat("\t\t\t\t\t}\n");
		outputStr=outputStr.concat("\t\t\t\t}\n");
		outputStr=outputStr.concat("\t\t\t}\n");
		outputStr=outputStr.concat("\t\t}\n");
		
		//ニュートン法計算
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tpro = 0.0;\n");
		outputStr=outputStr.concat("\t\t\tfor(j=0;j<"+n+";j++){\n");
		outputStr=outputStr.concat("\t\t\t\tpro += inv[i][j] * simulFunc"+id+"(simulSet,");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat(v2List.get(j).getCodeName()+",");
		}
		outputStr=outputStr.concat("j);\n");
		outputStr=outputStr.concat("\t\t\t}\n");
		outputStr=outputStr.concat("\t\t\tsimulSet_next[i] = simulSet[i] - pro;\n");
		outputStr=outputStr.concat("\t\t}\n");
		
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tsimulSet[i] = simulSet_next[i];\n");
		outputStr=outputStr.concat("\t\t}\n");
		
		//判定値の計算
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tf[i] = simulFunc"+id+"(simulSet,");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat(v2List.get(j).getCodeName()+",");
		}
		outputStr=outputStr.concat("i);\n");
		outputStr=outputStr.concat("\t\t\teps += f[i]*f[i];\n");
		outputStr=outputStr.concat("\t\t}\n");
		outputStr=outputStr.concat("\t} while ("+e+" < Math.sqrt(eps));\n");
		outputStr=outputStr.concat("\n");
		
		

		return(outputStr);
		
	}

	public String makeJacobiFunc(Vector<MathExpression> expList, Vector<Math_ci> varList) throws MathException {
	
		int n= varList.size();
		Jacobian jc = new Jacobian();
		Vector<Vector<MathFactor>> JacobianMatrix = new Vector<Vector<MathFactor>>();
		Vector<MathExpression> expressionList = new Vector<MathExpression>();
		
		for(int i=0;i<expList.size();i++){
			expressionList.add(expList.get(i).createCopy());
		}
		
		ImplicitFunctionTransposition it = new ImplicitFunctionTransposition();
		
		for(int i=0;i<expressionList.size();i++){
			
			expressionList.set(i,it.transporseExpression(expressionList.get(i), expressionList.get(i).getDerivedVariable()));
		}
		for(int i=0;i<expressionList.size();i++){
			
			expressionList.set(i,it.transporseExpression(expressionList.get(i), expressionList.get(i).getDerivedVariable()));
			expressionList.get(i).setDerivedVariable(expList.get(i).getDerivedVariable());
			expressionList.get(i).setCodeVariable(expList.get(i).getCodeVariable());
			expressionList.get(i).setAllVariableCodeName();
		}
		
		
		JacobianMatrix = jc.makeJacobian(expressionList, varList);
		
		
		
		//ヤコビ行列
		String outputStr = "";
		outputStr=outputStr.concat("\n");
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat("\tdouble "+varList.get(i).codeName+" = simulSet["+i+"];\n");
		}
		
		
		//コード文字列へ変換
		
		Vector<Vector<MathExpression>> JacobianRoot = new  Vector<Vector<MathExpression>>();
		
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
		for(int i=0;i<JacobianMatrix.size();i++){
			Vector<MathExpression> JacobianSet = new Vector<MathExpression>();
			for(int j=0;j<JacobianMatrix.size();j++){
				
				MathExpression exp = new MathExpression();
				exp.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				if(JacobianMatrix.get(i).get(j).matches(eMathMLClassification.MML_OPERATOR)){
					exp.addOperator((MathOperator) JacobianMatrix.get(i).get(j));
					exp.setCodeVariable(expList.get(0).getCodeVariable());
				}else{
					exp.addOperator((MathOperator) JacobianMatrix.get(i).get(j));
					exp.setCodeVariable(expList.get(0).getCodeVariable());
				}
				JacobianSet.add(exp);
			}
			
			JacobianRoot.add(JacobianSet);
		}
		
		for(int i=0;i<JacobianMatrix.size();i++){
			
			for(int j=0;j<JacobianMatrix.size();j++){
				
				JacobianRoot.get(i).get(j).replaceCodeVariable();
			}
		}
		
		outputStr=outputStr.concat("\n");
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				outputStr=outputStr.concat("\tif(i=="+i+" && j=="+j+") return "+JacobianRoot.get(i).get(j).toLegalJavaString()+";\n");
			}
		}
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\telse return 0.0;\n");
	
		return outputStr;
		
	}

	public String makeFunc(Vector<MathExpression> expList, Vector<Math_ci> varList) throws MathException {
		
		int n= varList.size();
		
		Vector<MathExpression> expressionList = new Vector<MathExpression>();
		
		for(int i=0;i<expList.size();i++){
			expressionList.add(expList.get(i).createCopy());
		}
		
		ImplicitFunctionTransposition it = new ImplicitFunctionTransposition();
		
		for(int i=0;i<expressionList.size();i++){
			
			expressionList.set(i,it.transporseExpression(expressionList.get(i), expressionList.get(i).getDerivedVariable()).createCopy());
			expressionList.get(i).setDerivedVariable(expList.get(i).getDerivedVariable());
			expressionList.get(i).setCodeVariable(expList.get(i).getCodeVariable());
			expressionList.get(i).setAllVariableCodeName();
		}
		
		

		//左辺関数
		String outputStr = "";
		outputStr=outputStr.concat("\n");
		
		for(int i=0;i<varList.size();i++){
			outputStr=outputStr.concat("\tdouble "+varList.get(i).getCodeName()+" = simulSet["+i+"];\n");
		}
		

		//コード文字列へ変換
		for(int i=0;i<expressionList.size();i++){
			expressionList.get(i).replaceCodeVariable();
		}

		outputStr=outputStr.concat("\n");
		for(int i=0;i<n;i++){
			outputStr=outputStr.concat("\tif(i=="+i+") return "+expressionList.get(i).getLeftExpression().toLegalJavaString()+";\n");
		}
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\telse return 0.0;\n");


		return(outputStr);
		
	}
	
	public static void main(String[] args) throws MathException {
		
		//数式の属性情報
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
	

		//テスト用変数定義
		//テスト用変数定義
		Math_ci val1=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "A");
		//val1.addIndexList((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
		//val1.addArrayIndexToFront(((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "0")));
		Math_ci val2=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		//val2.addIndexList((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
		//val2.addArrayIndexToFront(((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "1")));
		Math_cn zero = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
		Math_cn num1 = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1");
		Math_cn num2 = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "2");
		Math_cn num3 = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "3");
		//Math_cn num4 = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "4");
		
		
		MathExpression pNewExpression = new MathExpression();
		pNewExpression.setExID(1);
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
				
				pNewExpression.addOperand(val1);
				pNewExpression.addOperand(val1);
				pNewExpression.addOperand(num1);
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				//plus第2要素
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				pNewExpression.addOperand(val2);
				pNewExpression.addOperand(num2);
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
			
	
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			
			//右辺追加
			pNewExpression.addOperand(zero);

		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

		
		
		
		
		MathExpression pNewExpression2 = new MathExpression();
		pNewExpression2.setExID(2);
		pNewExpression2.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression2.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
	
			//左辺追加
			pNewExpression2.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression2.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("plus"), strAttr));
			
				//plus第１要素
				pNewExpression2.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression2.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				pNewExpression2.addOperand(val1);
				pNewExpression2.addOperand(num3);
				
				pNewExpression2.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				//plus第2要素
				pNewExpression2.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression2.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				pNewExpression2.addOperand(val2);
				pNewExpression2.addOperand(num1);
				pNewExpression2.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
			
	
			pNewExpression2.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			
			//右辺追加
			pNewExpression2.addOperand(zero);

		pNewExpression2.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
		
		Vector<MathExpression> eqList = new Vector<MathExpression>();
		eqList.add(pNewExpression);
		eqList.add(pNewExpression2);
		
		//ある連立式から導出される変数集合は,同じ名称でインデックスが異なる変数として格納する.
		Vector<Math_ci> varList = new Vector<Math_ci>();
		varList.add(val1);
		varList.add(val2);
		
		double eps = 0.001;
		int max = 1000;
		int id =0;
		
		SimultaneousNewtonSolverJava sns = new SimultaneousNewtonSolverJava();
		sns.makeSimultaneousNewtonSolver(eqList, varList, eps ,max, id);
		
		
	}
	
	
}
