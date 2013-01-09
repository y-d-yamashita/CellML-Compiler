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
 * 数式ライブラリ対応 非線形連立方程式系Newton法ソルバーテストクラス
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
public class SimultaneousNewtonSolver {
	public void writeSimultaneousNewtonSolver(Vector<MathExpression> expList, Vector<Math_ci> varList, double e, int max) throws MathException {
		
		int n= varList.size();
		Jacobian jc = new Jacobian();
		jc.makeJacobian(expList, varList);
		//jc.changeInverseMatrix();
		
		//含まれる変数リスト(導出する変数以外)を作成
		Vector<Math_ci> vList= new Vector<Math_ci>();
		Vector<Math_ci> v2List= new Vector<Math_ci>();
		for(int i=0;i<expList.size();i++){
			expList.get(i).getAllVariablesWithSelector(vList);
		}
		for(int j=0;j<vList.size();j++){
			boolean flag = false;
			for(int k=0;k<n;k++){
				if(vList.get(j).toLegalString().equals(varList.get(k).toLegalString())) flag = true;
			}
			if(flag==false) v2List.add(vList.get(j));
		}
		//main関数に記述される構文
		System.out.println("main() {");
		System.out.println("\tdouble "+varList.get(0).getM_strPresentText()+"[] = 1.0, 1.0;");
		System.out.print("\tsimulNewton(");
		System.out.print(varList.get(0).getM_strPresentText());
		if(v2List.size()!=0){
			System.out.print(", ");
		}
		for(int j=0;j<v2List.size();j++){
			System.out.print("double "+ v2List.get(j).toLegalString());
			if(j!=v2List.size()-1){
				System.out.print(", ");
			}
		}
		System.out.println(");");
		System.out.println("}");
		
		
		System.out.println();
	
		
		//ニュートン法計算関数
		
			
		System.out.print("void simulNewton(");
		System.out.print("double *"+ varList.get(0).getM_strPresentText());
		if(v2List.size()!=0){
				System.out.print(", ");
		}
		for(int j=0;j<v2List.size();j++){
			System.out.print("double "+ v2List.get(j).toLegalString());
			if(j!=v2List.size()-1){
				System.out.print(", ");
			}
		}
		
		System.out.println(") {");
		System.out.println();
		
		System.out.println("\tint max = 0;");
		System.out.println("\tint i, j, k;");
		System.out.println("\tdouble buf;");
		System.out.println("\tdouble det;");
		System.out.println("\tdouble pro;");
		System.out.println("\tdouble eps;");
		System.out.println("\tdouble f["+n+"];");
		System.out.println("\tdouble jac["+n+"]["+n+"];");
		System.out.println("\tdouble cpy["+n+"]["+n+"];");
		System.out.println("\tdouble inv["+n+"]["+n+"];");
		System.out.println("\tdouble " +varList.get(0).getM_strPresentText()+"_next["+n+"];");
		System.out.println();
		System.out.println("\tdo {");
		System.out.println("\t\tmax ++;");
		System.out.println("\t\tif(max > "+max+"){");
		System.out.println("\t\t\tprintf(\"error:no convergence\\n\");break;");
		System.out.println("\t\t}");
		System.out.println("\t\tdet = 1.0;");
		System.out.println("\t\teps = 0.0;");
		System.out.println();
		System.out.println("\t\tfor(i=0;i<"+n+";i++){");
		System.out.println("\t\t\tfor(j=0;j<"+n+";j++){");
		System.out.print("\t\t\t\tjac[i][j] = jacobi("+varList.get(0).getM_strPresentText()+",");
		for(int j=0;j<v2List.size();j++){
			System.out.print(v2List.get(j).toLegalString()+",");
		}
		System.out.println("i,j);");
		System.out.print("\t\t\t\tcpy[i][j] = jacobi("+varList.get(0).getM_strPresentText()+",");
		for(int j=0;j<v2List.size();j++){
			System.out.print(v2List.get(j).toLegalString()+",");
		}
		System.out.println("i,j);");
		System.out.println();
		System.out.println("\t\t\t}");
		System.out.println("\t\t}");
		System.out.println();
		System.out.println("\t\tfor(i=0;i<"+n+";i++){");
		System.out.println("\t\t\tfor(j=0;j<"+n+";j++){");
		System.out.println("\t\t\t\tif(i<j){");
		System.out.println("\t\t\t\t\tbuf = cpy[j][i] / cpy[i][i];");
		System.out.println("\t\t\t\t\tfor(k=0;k<"+n+";k++){");
		System.out.println("\t\t\t\t\t\tcpy[j][k] -= cpy[i][k] * buf;");
		System.out.println("\t\t\t\t\t}");
		System.out.println("\t\t\t\t}");
		System.out.println("\t\t\t}");
		System.out.println("\t\t}");
		
		
		System.out.println("\t\tfor(i=0;i<"+n+";i++){");
		System.out.println("\t\t\tdet *= cpy[i][i];");
		System.out.println("\t\t}");
		System.out.println("\t\tif(det == 0.0){");
		System.out.println("\t\t\tprintf(\"error:det is zero\\n\");break;");
		System.out.println("\t\t}");
		
		//単位行列を作成
		System.out.println("\t\tfor(i=0;i<"+n+";i++){");
		System.out.println("\t\t\tfor(j=0;j<"+n+";j++){");
		System.out.println("\t\t\t\tif(i==j) inv[i][j] = 1.0;");
		System.out.println("\t\t\t\telse inv[i][j] = 0.0;");
		System.out.println("\t\t\t}");
		System.out.println("\t\t}");
		
		//掃き出し法による逆行列生成
		System.out.println("\t\tfor(i=0;i<"+n+";i++){");
		System.out.println("\t\t\tbuf = 1 / jac[i][i];");
		System.out.println("\t\t\tfor(j=0;j<"+n+";j++){");
		System.out.println("\t\t\t\tjac[i][j] *= buf;");
		System.out.println("\t\t\t\tinv[i][j] *= buf;");
		System.out.println("\t\t\t}");
		System.out.println("\t\t\tfor(j=0;j<"+n+";j++){");
		System.out.println("\t\t\t\tif(i!=j){");
		System.out.println("\t\t\t\t\tbuf = jac[j][i];");
		System.out.println("\t\t\t\t\tfor(k=0;k<"+n+";k++){");
		System.out.println("\t\t\t\t\t\tjac[j][k] -= jac[i][k]*buf;");
		System.out.println("\t\t\t\t\t\tinv[j][k] -= inv[i][k]*buf;");
		System.out.println("\t\t\t\t\t}");
		System.out.println("\t\t\t\t}");
		System.out.println("\t\t\t}");
		System.out.println("\t\t}");
		
		//ニュートン法計算
		System.out.println("\t\tfor(i=0;i<"+n+";i++){");
		System.out.println("\t\t\tpro = 0.0;");
		System.out.println("\t\t\tfor(j=0;j<"+n+";j++){");
		System.out.print("\t\t\t\tpro += inv[i][j] * func("+varList.get(0).getM_strPresentText()+",");
		for(int j=0;j<v2List.size();j++){
			System.out.print(v2List.get(j).toLegalString()+",");
		}
		System.out.println("j);");
		System.out.println("\t\t\t}");
		System.out.println("\t\t\t"+varList.get(0).getM_strPresentText()+"_next[i] = "+varList.get(0).getM_strPresentText()+"[i] - pro;");
		System.out.println("\t\t}");
		
		System.out.println("\t\tfor(i=0;i<"+n+";i++){");
		System.out.println("\t\t\t"+varList.get(0).getM_strPresentText()+"[i] = "+varList.get(0).getM_strPresentText()+"_next[i];");
		System.out.println("\t\t}");
		
		//判定値の計算
		System.out.println("\t\tfor(i=0;i<"+n+";i++){");
		System.out.print("\t\t\tf[i] = func("+varList.get(0).getM_strPresentText()+",");
		for(int j=0;j<v2List.size();j++){
			System.out.print(v2List.get(j).toLegalString()+",");
		}
		System.out.println("i);");
		System.out.println("\t\t\teps += f[i]*f[i];");
		System.out.println("\t\t}");
		System.out.println("\t} while ("+e+" < sqrt(eps));");
		System.out.println("}");
		System.out.println();
		
		
		//左辺関数func
		System.out.print("double func(");
		System.out.print("double *"+ varList.get(0).getM_strPresentText()+", ");
		for(int j=0;j<v2List.size();j++){
			System.out.print("double "+ v2List.get(j).toLegalString());
			System.out.print(", ");
		}
		System.out.println("int i) {");
		System.out.println();
		for(int i=0;i<n;i++){
			System.out.println("\tif(i=="+i+") return "+expList.get(i).getLeftExpression().toLegalString()+";");
		}
		System.out.println();
		System.out.println("}");
		System.out.println();
		
		//ヤコビ行列
		System.out.print("double jacobi(");
		System.out.print("double *"+ varList.get(0).getM_strPresentText()+", ");
		for(int j=0;j<v2List.size();j++){
			System.out.print("double "+ v2List.get(j).toLegalString());
			System.out.print(", ");
		}
		System.out.println("int i, int j) {");
		System.out.println();
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				System.out.println("\tif(i=="+i+" && j=="+j+") return "+jc.getFactor(i,j).toLegalString()+";");
			}
		}
		System.out.println();
		System.out.println("}");

	}
	
	
}
