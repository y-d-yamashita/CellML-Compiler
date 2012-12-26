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
	public void writeSimultaneousNewtonSolver(Vector<MathExpression> expList, Vector<Math_ci> varList, double e) throws MathException {
		
		int n= expList.size();
		Jacobian jc = new Jacobian();
		jc.makeJacobian(expList, varList);
		jc.changeInverseMatrix();
		
		
		//main関数に記述される構文
		for(int i=0;i<n;i++){
			System.out.print(varList.get(i).toLegalString()+" = simulNewton"+expList.get(i).getExID()+"(");
			for(int j=0;j<n;j++){
				System.out.print(varList.get(j).toLegalString());
				if(j!=n-1)System.out.print(",");
			}
			System.out.println(");");
		}
		
		System.out.println();
	
		
		//ニュートン法計算関数
		for(int i=0;i<n;i++){
			
			System.out.print("double simulNewton"+expList.get(i).getExID()+"(");
			
			for(int j=0;j<n;j++){
				System.out.print("double "+varList.get(j).toLegalString());
				if(j!=n-1)System.out.print(",");
			}
			System.out.println(") {");
			System.out.println();
			
			System.out.println("\tdouble e;");
			System.out.println("\tdouble " +varList.get(i).toLegalString()+"_next;");
			System.out.println("\tdouble buf = 0.0;");
			System.out.println();
			System.out.println("\tdo {");
			for(int j=0;j<n;j++){
				System.out.print("\t\tbuf += inv_jacobian"+expList.get(i).getExID()+varList.get(j).toLegalString()+"(");
				for(int k=0;k<n;k++){
					System.out.print(varList.get(k).toLegalString());
					if(k!=n-1)System.out.print(",");
				}
				System.out.println(");");
			}
			System.out.println("\t\t"+varList.get(i).toLegalString()+"_next = "+varList.get(i).toLegalString()+" - buf;");
			System.out.println("\t\te = "+varList.get(i).toLegalString()+"_next - "+varList.get(i).toLegalString()+";");
			System.out.println("\t\t"+varList.get(i).toLegalString()+" = "+varList.get(i).toLegalString()+"_next;");
			System.out.println("\t} while(e > "+e+");");
			System.out.println();
			System.out.println("\treturn "+varList.get(i).toLegalString()+";");
			System.out.println("}");
			System.out.println();
		}
		
		//左辺関数
		for(int i=0;i<n;i++){
			System.out.print("double func"+expList.get(i).getExID()+"(");
			for(int j=0;j<n;j++){
				System.out.print("double "+varList.get(j).toLegalString());
				if(j!=n-1)System.out.print(",");
			}
			System.out.println(") {");
			System.out.println();
			System.out.println("\treturn "+expList.get(i).getLeftExpression().toLegalString()+";");
			System.out.println("}");
		}
		
		//ヤコビアン関数
		for(int i=0;i<n;i++){
			System.out.print("double func"+expList.get(i).getExID()+"(");
			for(int j=0;j<n;j++){
				System.out.print("double "+varList.get(j).toLegalString());
				if(j!=n-1)System.out.print(",");
			}
			System.out.println(") {");
			System.out.println();
			System.out.println("\treturn "+expList.get(i).getLeftExpression().toLegalString()+";");
			System.out.println("}");
		}
	}
	
	
}
