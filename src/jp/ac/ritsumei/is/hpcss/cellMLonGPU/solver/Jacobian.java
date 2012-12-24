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
	
	public Vector<Vector<MathExpression>> Jacobian;
	
	//コンストラクタ
	public Jacobian(){
		Jacobian = new Vector<Vector<MathExpression>>();
	}
	
	
	public Vector<Vector<MathExpression>> makeJacobian(
			Vector<MathExpression> expressionList, Vector<Math_ci> varList) throws MathException {
		
		Vector<MathExpression> JacobianLine;
		Differentiation diff = new Differentiation();
		
		for(int i=0;i<expressionList.size();i++){
			JacobianLine = new Vector<MathExpression>();
			for(int j=0;j<varList.size();j++){
				
				JacobianLine.add(diff.differentiate(expressionList.get(i),varList.get(j)).getLeftExpression());
			}
			Jacobian.add(i,JacobianLine);
		}
		 
		return Jacobian;
	}
	
	
	public Vector<Vector<MathExpression>> changeInverseMatrix() throws MathException{
		
		Vector<MathExpression> JacobianLine;
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
		//掃き出し法による逆行列生成メソッド
		int n = getDimension();
		Vector<Vector<MathExpression>> inverseMatrix = new Vector<Vector<MathExpression>>();
		Vector<MathExpression> inverseMatrixLine;
		
		
		//一時記憶バッファ
		MathExpression buf;
		MathExpression buf2;
		MathExpression buf3;
		MathExpression buf4;
		MathExpression buf5;
		MathExpression buf6;
		
		//単位行列生成
		for(int i=0;i<n;i++){
			inverseMatrixLine = new Vector<MathExpression>();
			for(int j=0;j<n;j++){
				if(i!=j){
					MathExpression exp = new MathExpression();
					Math_cn zero = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
					exp.addOperand(zero);
					inverseMatrixLine.add(exp);
				} else{
					MathExpression exp = new MathExpression();
					Math_cn one = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1");
					exp.addOperand(one);
					inverseMatrixLine.add(exp);
				}
			}
			inverseMatrix.add(inverseMatrixLine);
		}
		
		//掃き出し法の実行
		for(int i=0;i<n;i++){
			//行の初期化
			inverseMatrixLine = new Vector<MathExpression>(); 
			JacobianLine = new Vector<MathExpression>();
			
			// buf　に　1/a[i][i]を格納
			
			buf = new MathExpression();
			Math_cn one = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1");
			buf.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			buf.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divie"), strAttr));
			
				buf.addOperand(one);
				
				if(Jacobian.get(i).get(i).getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
					buf.addOperator((MathOperator) Jacobian.get(i).get(i).getRootFactor());
					buf.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}
				else{
					buf.addOperand((MathOperand) Jacobian.get(i).get(i).getRootFactor());
				}
				
			buf.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			
			// a[i][j]  = a[i][j] * buf[i][i]
			for(int j=0;j<n;j++){
				buf2 = new MathExpression();
				buf2.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				buf2.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				//times第1要素
				if(Jacobian.get(i).get(j).getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){

					buf2.addOperator((MathOperator) Jacobian.get(i).get(j).getRootFactor());
					buf2.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}
				else{
					buf2.addOperand((MathOperand) Jacobian.get(i).get(j).getRootFactor());
				}
				
				//times第2要素
				if(buf.getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
					buf2.addOperator((MathOperator) buf.getRootFactor());
					buf2.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}else{
					buf2.addOperand((MathOperand) buf.getRootFactor());
				}
				
				buf2.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				JacobianLine.add(j, buf2);
				
				
				
				//inv_a[i][j] = ident[i][j]*buf[i][i]
				buf3 = new MathExpression();
				buf3.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				buf3.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				buf3.addOperand((MathOperand) inverseMatrix.get(i).get(j).getRootFactor());
				
				if(buf.getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
					buf3.addOperator((MathOperator) buf.getRootFactor());
					buf3.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}else{
					buf3.addOperand((MathOperand) buf.getRootFactor());
				}
				
				buf3.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				inverseMatrixLine.add(j,buf3);
				
				
			}
			Jacobian.add(i, JacobianLine);
			inverseMatrix.add(i,inverseMatrixLine);
			
			
			
			
			for(int j=0;j<n;j++){
				buf4 = new MathExpression();
				JacobianLine = new Vector<MathExpression>();
				inverseMatrixLine = new Vector<MathExpression>();
				if(i!=j){
					//buf4 = a[j][i]
					if(Jacobian.get(j).get(i).getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){

						buf4.addOperator((MathOperator) Jacobian.get(j).get(i).getRootFactor());
						buf4.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}
					else{
						buf4.addOperand((MathOperand) Jacobian.get(j).get(i).getRootFactor());
					}
					
					for(int k=0;k<n;k++){
						//a[j][k]= a[j][k] -  a[i][k]* buf4;
						buf5 = new MathExpression();
						buf5.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						buf5.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
						
						
						//minus第1要素
						if(Jacobian.get(j).get(k).getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){

							buf5.addOperator((MathOperator) Jacobian.get(j).get(k).getRootFactor());
							buf5.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
						else{
							buf5.addOperand((MathOperand) Jacobian.get(j).get(k).getRootFactor());
						}
						
						
						//minus第2要素
						buf5.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						buf5.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
						
						
							//times第1要素
							if(Jacobian.get(i).get(k).getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
	
								buf5.addOperator((MathOperator) Jacobian.get(i).get(k).getRootFactor());
								buf5.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
							else{
								buf5.addOperand((MathOperand) Jacobian.get(i).get(k).getRootFactor());
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
						
						JacobianLine.add(k,buf5);
						
						//inv_a[j][k] = inv_a[j][k] -  inv_a[i][k]* buf4;
						buf6 = new MathExpression();
						buf6.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						buf6.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
						
						
						//minus第1要素
						if(inverseMatrix.get(j).get(k).getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){

							buf6.addOperator((MathOperator) inverseMatrix.get(j).get(k).getRootFactor());
							buf6.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
						else{
							buf6.addOperand((MathOperand) inverseMatrix.get(j).get(k).getRootFactor());
						}
						
						
						//minus第2要素
						buf6.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						buf6.addOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
						
						
							//times第1要素
							if(inverseMatrix.get(i).get(k).getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
	
								buf6.addOperator((MathOperator) inverseMatrix.get(i).get(k).getRootFactor());
								buf6.breakOperator(MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
							else{
								buf6.addOperand((MathOperand) inverseMatrix.get(i).get(k).getRootFactor());
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
						
						inverseMatrixLine.add(k,buf6);
						
											
					}
					Jacobian.add(j, JacobianLine);
					inverseMatrix.add(j,inverseMatrixLine);
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
