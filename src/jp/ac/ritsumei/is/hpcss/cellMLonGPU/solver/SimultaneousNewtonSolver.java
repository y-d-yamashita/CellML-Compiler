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
		
		//main関数に記述される呼び出しサンプル
		String main = "";
		main=main.concat("main() {\n");
		main=main.concat("\tdouble "+varList.get(0).getM_strPresentText()+"[] = {10.0, 10.0};\n");
		main=main.concat("\tsimulNewton(");
		main=main.concat(varList.get(0).getM_strPresentText());
		if(v2List.size()!=0){
			main=main.concat(", ");
		}
		for(int j=0;j<v2List.size();j++){
			main=main.concat("double "+ v2List.get(j).toLegalString());
			if(j!=v2List.size()-1){
				main=main.concat(", ");
			}
		}
		main=main.concat(");\n");
		main=main.concat("}\n");
		main=main.concat("\n");
	
		
		//ニュートン法計算関数
		String outputStr = "";
			
		outputStr=outputStr.concat("void simulNewton(");
		outputStr=outputStr.concat("double *"+ varList.get(0).getM_strPresentText());
		if(v2List.size()!=0){
			outputStr=outputStr.concat(", ");
		}
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat("double "+ v2List.get(j).toLegalString());
			if(j!=v2List.size()-1){
				outputStr=outputStr.concat(", ");
			}
		}
		
		outputStr=outputStr.concat(") {\n");
		outputStr=outputStr.concat("\n");
		
		outputStr=outputStr.concat("\tint max = 0;\n");
		outputStr=outputStr.concat("\tint i, j, k;\n");
		outputStr=outputStr.concat("\tdouble buf;\n");
		outputStr=outputStr.concat("\tdouble det;\n");
		outputStr=outputStr.concat("\tdouble pro;\n");
		outputStr=outputStr.concat("\tdouble eps;\n");
		outputStr=outputStr.concat("\tdouble f["+n+"];\n");
		outputStr=outputStr.concat("\tdouble jac["+n+"]["+n+"];\n");
		outputStr=outputStr.concat("\tdouble cpy["+n+"]["+n+"];\n");
		outputStr=outputStr.concat("\tdouble inv["+n+"]["+n+"];\n");
		outputStr=outputStr.concat("\tdouble " +varList.get(0).getM_strPresentText()+"_next["+n+"];\n");
		outputStr=outputStr.concat("\n\n");
		outputStr=outputStr.concat("\tdo {\n");
		outputStr=outputStr.concat("\t\tmax ++;\n");
		outputStr=outputStr.concat("\t\tif(max > "+max+"){\n");
		outputStr=outputStr.concat("\t\t\tprintf(\"error:no convergence\\n\");break;\n");
		outputStr=outputStr.concat("\t\t}\n");
		outputStr=outputStr.concat("\t\tdet = 1.0;\n");
		outputStr=outputStr.concat("\t\teps = 0.0;\n");
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tfor(j=0;j<"+n+";j++){\n");
		outputStr=outputStr.concat("\t\t\t\tjac[i][j] = jacobi("+varList.get(0).getM_strPresentText()+",");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat(v2List.get(j).toLegalString()+",");
		}
		outputStr=outputStr.concat("i,j);\n");
		outputStr=outputStr.concat("\t\t\t\tcpy[i][j] = jacobi("+varList.get(0).getM_strPresentText()+",");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat(v2List.get(j).toLegalString()+",");
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
		outputStr=outputStr.concat("\t\t\tprintf(\"error:det is zero\\n\");break;\n");
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
		outputStr=outputStr.concat("\t\t\t\tpro += inv[i][j] * func("+varList.get(0).getM_strPresentText()+",");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat(v2List.get(j).toLegalString()+",");
		}
		outputStr=outputStr.concat("j);\n");
		outputStr=outputStr.concat("\t\t\t}\n");
		outputStr=outputStr.concat("\t\t\t"+varList.get(0).getM_strPresentText()+"_next[i] = "+varList.get(0).getM_strPresentText()+"[i] - pro;\n");
		outputStr=outputStr.concat("\t\t}\n");
		
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\t"+varList.get(0).getM_strPresentText()+"[i] = "+varList.get(0).getM_strPresentText()+"_next[i];\n");
		outputStr=outputStr.concat("\t\t}\n");
		
		//判定値の計算
		outputStr=outputStr.concat("\t\tfor(i=0;i<"+n+";i++){\n");
		outputStr=outputStr.concat("\t\t\tf[i] = func("+varList.get(0).getM_strPresentText()+",");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat(v2List.get(j).toLegalString()+",");
		}
		outputStr=outputStr.concat("i);\n");
		outputStr=outputStr.concat("\t\t\teps += f[i]*f[i];\n");
		outputStr=outputStr.concat("\t\t}\n");
		outputStr=outputStr.concat("\t} while ("+e+" < sqrt(eps));\n");
		outputStr=outputStr.concat("}\n");
		outputStr=outputStr.concat("\n");
		
		
		//左辺関数
		outputStr=outputStr.concat("double func(");
		outputStr=outputStr.concat("double *"+ varList.get(0).getM_strPresentText()+", ");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat("double "+ v2List.get(j).toLegalString());
			outputStr=outputStr.concat(", ");
		}
		outputStr=outputStr.concat("int i) {\n");
		outputStr=outputStr.concat("\n");
		for(int i=0;i<n;i++){
			outputStr=outputStr.concat("\tif(i=="+i+") return "+expList.get(i).getLeftExpression().toLegalString()+";\n");
		}
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("}\n");
		outputStr=outputStr.concat("\n");
		
		//ヤコビ行列
		outputStr=outputStr.concat("double jacobi(");
		outputStr=outputStr.concat("double *"+ varList.get(0).getM_strPresentText()+", ");
		for(int j=0;j<v2List.size();j++){
			outputStr=outputStr.concat("double "+ v2List.get(j).toLegalString());
			outputStr=outputStr.concat(", ");
		}
		outputStr=outputStr.concat("int i, int j) {\n");
		outputStr=outputStr.concat("\n");
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				outputStr=outputStr.concat("\tif(i=="+i+" && j=="+j+") return "+jc.getFactor(i,j).toLegalString()+";\n");
			}
		}
		outputStr=outputStr.concat("\n");
		outputStr=outputStr.concat("}\n");
		
		//標準出力
		System.out.println(main);
		System.out.println(outputStr);
		
	}
	
	
}
