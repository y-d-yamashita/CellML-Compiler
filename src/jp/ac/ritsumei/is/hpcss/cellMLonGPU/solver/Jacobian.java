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
 * This class is Jacobian for code generator.
 * 
 * @author n-washio
 * 
 */

public class Jacobian {
	
	public Vector<Vector<MathFactor>> Jacobian;
	
	//コンストラクタ
	public Jacobian(){
		Jacobian = new Vector<Vector<MathFactor>>();
	}
	
	public MathFactor getFactor(int i,int j){
		return Jacobian.get(i).get(j);
	}
	public void printJacobian() throws MathException{
		int n = getDimension();
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				System.out.print(Jacobian.get(i).get(j).toLegalString());
				if(j!=n-1)System.out.print("  , ");
			}System.out.println();
		}
	}
	public Vector<Vector<MathFactor>> makeJacobian(
			Vector<MathExpression> expressionList, Vector<Math_ci> varList) throws MathException {
		
		Vector<MathFactor> JacobianLine;
		Differentiation diff = new Differentiation();
		
		for(int i=0;i<expressionList.size();i++){
			JacobianLine = new Vector<MathFactor>();
			for(int j=0;j<varList.size();j++){
				
				JacobianLine.add(diff.differentiate(expressionList.get(i),varList.get(j)).getLeftExpression().getRootFactor());
			}
			Jacobian.add(i,JacobianLine);
		}
		 
		return Jacobian;
	}
	
	
	public Vector<Vector<MathFactor>> changeInverseMatrix() throws MathException{
		
		Vector<MathFactor> JacobianLine;
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
		//掃き出し法による逆行列生成メソッド
		int n = getDimension();
		Vector<Vector<MathFactor>> inverseMatrix = new Vector<Vector<MathFactor>>();
		Vector<MathFactor> inverseMatrixLine;
		
		
		//一時記憶バッファ
		MathExpression buf;
		MathExpression buf2;
		MathExpression buf3;
		MathExpression buf4;
		MathExpression buf5;
		MathExpression buf6;
		
		//単位行列生成
		for(int i=0;i<n;i++){
			inverseMatrixLine = new Vector<MathFactor>();
			for(int j=0;j<n;j++){
				if(i!=j){
					Math_cn zero = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
					inverseMatrixLine.add(zero);
				} else{
					Math_cn one = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1");
					inverseMatrixLine.add(one);
				}
			}
			inverseMatrix.add(inverseMatrixLine);
		}

		//掃き出し法の実行
		for(int i=0;i<n;i++){
			//行の初期化
			inverseMatrixLine = new Vector<MathFactor>(); 
			JacobianLine = new Vector<MathFactor>();
			
			// buf　に　1/a[i][i]を格納
			
			buf = new MathExpression();
			Math_cn one = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1");
			buf.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			buf.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
			
				buf.addOperand(one);
				
				if(Jacobian.get(i).get(i).matches(eMathMLClassification.MML_OPERATOR)){
					buf.addOperator((MathOperator) Jacobian.get(i).get(i));
					buf.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}
				else{
					buf.addOperand((MathOperand) Jacobian.get(i).get(i));
				}
				
			buf.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			
			// a[i][j]  = a[i][j] * buf[i][i]
			for(int j=0;j<n;j++){
				buf2 = new MathExpression();
				buf2.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				buf2.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				//times第1要素
				if(Jacobian.get(i).get(j).matches(eMathMLClassification.MML_OPERATOR)){

					buf2.addOperator((MathOperator) Jacobian.get(i).get(j));
					buf2.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}
				else{
					buf2.addOperand((MathOperand) Jacobian.get(i).get(j));
				}
				
				//times第2要素
				if(buf.getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
					buf2.addOperator((MathOperator) buf.getRootFactor());
					buf2.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}else{
					buf2.addOperand((MathOperand) buf.getRootFactor());
				}
				
				buf2.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				JacobianLine.add(j, buf2.getRootFactor());
				
				
				
				//inv_a[i][j] = ident[i][j]*buf[i][i]
				buf3 = new MathExpression();
				buf3.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				buf3.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				if(inverseMatrix.get(i).get(j).matches(eMathMLClassification.MML_OPERATOR)){
					buf3.addOperator((MathOperator) inverseMatrix.get(i).get(j));
					buf3.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}else{
					buf3.addOperand((MathOperand) inverseMatrix.get(i).get(j));
				}
				
				if(buf.getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
					buf3.addOperator((MathOperator) buf.getRootFactor());
					buf3.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}else{
					buf3.addOperand((MathOperand) buf.getRootFactor());
				}
				
				buf3.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				inverseMatrixLine.add(j,buf3.getRootFactor());
				
				
			}
			Jacobian.set(i, JacobianLine);
			inverseMatrix.set(i,inverseMatrixLine);
			
			
			
			
			for(int j=0;j<n;j++){
				buf4 = new MathExpression();
				JacobianLine = new Vector<MathFactor>();
				inverseMatrixLine = new Vector<MathFactor>();
				if(i!=j){
					//buf4 = a[j][i]
					if(Jacobian.get(j).get(i).matches(eMathMLClassification.MML_OPERATOR)){

						buf4.addOperator((MathOperator) Jacobian.get(j).get(i));
						buf4.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}
					else{
						buf4.addOperand((MathOperand) Jacobian.get(j).get(i));
					}
					
					for(int k=0;k<n;k++){
						//a[j][k]= a[j][k] -  a[i][k]* buf4;
						buf5 = new MathExpression();
						buf5.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						buf5.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
						
						
						//minus第1要素
						if(Jacobian.get(j).get(k).matches(eMathMLClassification.MML_OPERATOR)){

							buf5.addOperator((MathOperator) Jacobian.get(j).get(k));
							buf5.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
						else{
							buf5.addOperand((MathOperand) Jacobian.get(j).get(k));
						}
						
						
						//minus第2要素
						buf5.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						buf5.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
						
						
							//times第1要素
							if(Jacobian.get(i).get(k).matches(eMathMLClassification.MML_OPERATOR)){
	
								buf5.addOperator((MathOperator) Jacobian.get(i).get(k));
								buf5.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
							else{
								buf5.addOperand((MathOperand) Jacobian.get(i).get(k));
							}
	
							//times第2要素
							if(buf4.getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
	
								buf5.addOperator((MathOperator) buf4.getRootFactor());
								buf5.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
							else{
								buf5.addOperand((MathOperand) buf4.getRootFactor());
							}
						
						buf5.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						
						
						buf5.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						JacobianLine.add(k,buf5.getRootFactor());
						
						//inv_a[j][k] = inv_a[j][k] -  inv_a[i][k]* buf4;
						buf6 = new MathExpression();
						buf6.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						buf6.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
						
						
						//minus第1要素
						if(inverseMatrix.get(j).get(k).matches(eMathMLClassification.MML_OPERATOR)){

							buf6.addOperator((MathOperator) inverseMatrix.get(j).get(k));
							buf6.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
						else{
							buf6.addOperand((MathOperand) inverseMatrix.get(j).get(k));
						}
						
						
						//minus第2要素
						buf6.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						buf6.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
						
						
							//times第1要素
							if(inverseMatrix.get(i).get(k).matches(eMathMLClassification.MML_OPERATOR)){
	
								buf6.addOperator((MathOperator) inverseMatrix.get(i).get(k));
								buf6.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
							else{
								buf6.addOperand((MathOperand) inverseMatrix.get(i).get(k));
							}
	
							//times第2要素
							if(buf4.getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
	
								buf6.addOperator((MathOperator) buf4.getRootFactor());
								buf6.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
							else{
								buf6.addOperand((MathOperand) buf4.getRootFactor());
							}
						
						buf6.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						
						
						buf6.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						inverseMatrixLine.add(k,buf6.getRootFactor());
						
											
					}
					Jacobian.set(j, JacobianLine);
					inverseMatrix.set(j,inverseMatrixLine);
				}
			}
			
			
		}
		
		return inverseMatrix;
	}

	public int getDimension() {
		//ヤコビアンの次元を返す
		return this.Jacobian.size();
	}
	
}
