package jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver;


import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;

/**
 * This class is Differentiation for code generator.
 * 
 * input:  
 * 	MathExpression
 * 	Math_ci
 * 
 * output
 *  MathExpression(Left-hand side is differentiated by derived variable)
 * 
 * @author n-washio
 * 
 */

public class Differentiation {
	
	public MathExpression differentiate(MathExpression expression, Math_ci val) throws MathException {
		
		//数式の属性情報
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
		MathExpression pNewExpression = new MathExpression();
		
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
		
		//左辺を導出変数で微分したツリーを付加
		if(expression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			//apply直下のオペレータの種類を格納
			String underOperatorKind =
				((MathOperator)((MathOperator)(expression.getLeftExpression().getRootFactor())).getUnderFactor()).getOperatorKind().toString();
			
			
			
			
			
			
			//plusの場合
			if(underOperatorKind.equals("MOP_PLUS")){
				
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("plus"), strAttr));
				
				//多項演算子なので要素をカウント
				int childFactorNum = 
						((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactorNum();
				
				for(int i=0;i<childFactorNum;i++){
					
					if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i).matches(
							eMathMLClassification.MML_OPERATOR) ){
						
						//微分する関数を左辺とし,再帰取得して追加.
						MathExpression pExpression = new MathExpression();
						
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
						
						pExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
						
						pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						
						
						pExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						pExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

						pExpression = differentiate( pExpression , val );
						
						if(pExpression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
							//オペレータの場合
							pNewExpression.addOperator(
									(MathOperator) pExpression.getLeftExpression().getRootFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}else{
							//オペランドの場合
							pNewExpression.addOperand(
									(MathOperand) pExpression.getLeftExpression().getRootFactor());
						}
						
					} else{
						//導出変数であれば1,それ以外の変数または定数は全て0とする
						if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
						}else{
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						}	
					}
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			}
			
			
			
			
			
			
			
			//minusの場合
			else if(underOperatorKind.equals("MOP_MINUS")){
				
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
				
				//2項演算子なので要素をカウント
				int childFactorNum = 
						((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactorNum();
				
				for(int i=0;i<childFactorNum;i++){
					
					if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i).matches(
							eMathMLClassification.MML_OPERATOR) ){
						
						//微分する関数を左辺とし,再帰取得して追加.
						MathExpression pExpression = new MathExpression();
						
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
						
						pExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
						
						pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						
						
						pExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						pExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

						pExpression = differentiate( pExpression , val );
						
						if(pExpression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
							//オペレータの場合
							pNewExpression.addOperator(
									(MathOperator) pExpression.getLeftExpression().getRootFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}else{
							//オペランドの場合
							pNewExpression.addOperand(
									(MathOperand) pExpression.getLeftExpression().getRootFactor());
						}
						
					} else{
						//導出変数であれば1,それ以外の変数または定数は全て0とする
						if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
						}else{
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						}	
					}
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			} 
			
			
			
			
			
			
			//timesの場合(未完成：現在2項演算のみ対応)
			else if(underOperatorKind.equals("MOP_TIMES")){
				//ライプニッツルールを適用 ( f'*g + f*g' )
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("plus"), strAttr));
				
				
				
				//f' * g
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					
					
					pExpression = differentiate( pExpression , val );
					
					pNewExpression.addOperator(
							(MathOperator) pExpression.getLeftExpression().getRootFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					//導出変数であれば1,それ以外の変数または定数は全て0とする
					if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
	
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				} else{
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				
				
				
				//f * g'
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				} else{
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					
					
					pExpression = differentiate( pExpression , val );
					
					pNewExpression.addOperator(
							(MathOperator) pExpression.getLeftExpression().getRootFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					//導出変数であれば1,それ以外の変数または定数は全て0とする
					if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
	
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				

				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			}
			
			
			
			
			
			
			//divideの場合
			else if(underOperatorKind.equals("MOP_DIVIDE")){
				//商の微分法を適用 ( (f'*g - f*g') / g^2 )
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
				
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
				
				
				
				//f' * g
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					
					
					pExpression = differentiate( pExpression , val );
					
					pNewExpression.addOperator(
							(MathOperator) pExpression.getLeftExpression().getRootFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					//導出変数であれば1,それ以外の変数または定数は全て0とする
					if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
	
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				} else{
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				
				
				
				//f * g'
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				} else{
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					
					
					pExpression = differentiate( pExpression , val );
					
					pNewExpression.addOperator(
							(MathOperator) pExpression.getLeftExpression().getRootFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					//導出変数であれば1,それ以外の変数または定数は全て0とする
					if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
	
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				
				//g^2
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
					
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				} else{
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
					
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}
				
				
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			}
			
			
			
			
			
			
			
			else if(underOperatorKind.equals("MOP_SIN")){
				//合成関数の微分に対応
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				
				//cos(f(x))
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("cos"), strAttr));
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
				}
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				//f'(x)
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression = differentiate( pExpression , val );
					
					pNewExpression.addOperator(
							(MathOperator) pExpression.getLeftExpression().getRootFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
				} else{
					//導出変数であれば1,それ以外の変数または定数は全て0とする
					if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			}
			
			
			
			
			
			
			
			else if(underOperatorKind.equals("MOP_COS")){
				//合成関数の微分に対応
				
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
				
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				//-sin(f(x))
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("sin"), strAttr));
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else {
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
				}
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				//f'(x)
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression = differentiate( pExpression , val );
					
					pNewExpression.addOperator(
							(MathOperator) pExpression.getLeftExpression().getRootFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
				} else{
					//導出変数であれば1,それ以外の変数または定数は全て0とする
					if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			}
			
			
			
			
			
			
			else if(underOperatorKind.equals("MOP_TAN")){
				//合成関数の微分に対応
				
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				
				// 1/cos(f(x))^2
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
				
				
				pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
				
				
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				 //cos(f(x))
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("cos"), strAttr));
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else {
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
				}
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				 //cos(f(x))
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("cos"), strAttr));
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else {
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
				}
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//apply break
				
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//divide break
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//apply break
				
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//apply break
				
				
				//f'(x)
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression = differentiate( pExpression , val );
					
					pNewExpression.addOperator(
							(MathOperator) pExpression.getLeftExpression().getRootFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
				} else{
					//導出変数であれば1,それ以外の変数または定数は全て0とする
					if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			}
			
			
			
			
			
			//incの場合
			if(underOperatorKind.equals("MOP_INC")){
				
				//inc(x) = x + 1

				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

					pExpression = differentiate( pExpression , val );
					
					if(pExpression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータの場合
						pNewExpression.addOperator(
								(MathOperator) pExpression.getLeftExpression().getRootFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}else{
						//オペランドの場合
						pNewExpression.addOperand(
								(MathOperand) pExpression.getLeftExpression().getRootFactor());
					}
					
					
					
				} else{
					//導出変数であれば1,それ以外の変数または定数は全て0とする
					if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
					
				}
			}
			
			
			
			
			//decの場合
			if(underOperatorKind.equals("MOP_DEC")){
				
				//dec(x) = x - 1

				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
						eMathMLClassification.MML_OPERATOR) ){
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

					pExpression = differentiate( pExpression , val );
					
					if(pExpression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータの場合
						pNewExpression.addOperator(
								(MathOperator) pExpression.getLeftExpression().getRootFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}else{
						//オペランドの場合
						pNewExpression.addOperand(
								(MathOperand) pExpression.getLeftExpression().getRootFactor());
					}
					
					
					
				} else{
					//導出変数であれば1,それ以外の変数または定数は全て0とする
					if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
					
				}
			}
			
			
			
			
			//powerの場合
			if(underOperatorKind.equals("MOP_POWER")){
									
				//一般冪関数の微分	
					//{f^(g)}' = f^(g) * { (f'*g)/f  +  log(f)*g' }
					
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
					
					
					// f^(g)
					if(expression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
						pNewExpression.addOperator(
								(MathOperator)expression.getLeftExpression().getRootFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					} else{
						pNewExpression.addOperand(
								(MathOperand)expression.getLeftExpression().getRootFactor());
					}
					
					// { (f'*g)/f  +  log(f)*g' }
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("plus"), strAttr));
					
					
					
						//(f'*g)/f
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
						
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
						
						
						
						if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
								eMathMLClassification.MML_OPERATOR) ){
							//微分する関数を左辺とし,再帰取得して追加.
							MathExpression pExpression = new MathExpression();
							
							pExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
							pExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
							
							pExpression.addOperator(
									(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
							
							pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
							
							
							pExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							pExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
							pExpression = differentiate( pExpression , val );
							
							if(pExpression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
								//オペレータの場合
								pNewExpression.addOperator(
										(MathOperator) pExpression.getLeftExpression().getRootFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}else{
								//オペランドの場合
								pNewExpression.addOperand(
										(MathOperand) pExpression.getLeftExpression().getRootFactor());
							}
	
						} else{
							//導出変数であれば1,それ以外の変数または定数は全て0とする
							if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
								pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
							}else{
								pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
							}	
							
						}
						
						if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
								eMathMLClassification.MML_OPERATOR) ){
							pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						} else{
							pNewExpression.addOperand(
									(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
									
						}
						
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break;
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
								eMathMLClassification.MML_OPERATOR) ){
							pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						} else{
							pNewExpression.addOperand(
									(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
									
						}
						
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//divide break;
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						
						//log(f)*g'
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
						
						
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("ln"), strAttr));
						
						
						if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
								eMathMLClassification.MML_OPERATOR) ){
							pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						} else{
							pNewExpression.addOperand(
									(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
									
						}
						
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//ln break;
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						
						//g'
						if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
								eMathMLClassification.MML_OPERATOR) ){
							//微分する関数を左辺とし,再帰取得して追加.
							MathExpression pExpression = new MathExpression();
							
							pExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
							pExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
							
							pExpression.addOperator(
									(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
							
							pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
							
							
							pExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							pExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
							pExpression = differentiate( pExpression , val );
							
							if(pExpression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
								//オペレータの場合
								pNewExpression.addOperator(
										(MathOperator) pExpression.getLeftExpression().getRootFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}else{
								//オペランドの場合
								pNewExpression.addOperand(
										(MathOperand) pExpression.getLeftExpression().getRootFactor());
							}
	
						} else{
							//導出変数であれば1,それ以外の変数または定数は全て0とする
							if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
								pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
							}else{
								pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
							}	
							
						}
						
						
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break;
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//plus break;
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					
					
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break;
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				
				
			}
			
			
			
			
			//rootの場合
			if(underOperatorKind.equals("MOP_ROOT")){
				
				//root(x)=power(x,0.5)とする
				
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("power"), strAttr));
					
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
							eMathMLClassification.MML_OPERATOR)){
						pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
						pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					} else{
						pExpression.addOperand(
								(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					}
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0.5"));
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//power break;
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//eq break;
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

					pExpression = differentiate( pExpression , val );
					
					if(pExpression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータの場合
						pNewExpression.addOperator(
								(MathOperator) pExpression.getLeftExpression().getRootFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}else{
						//オペランドの場合
						pNewExpression.addOperand(
								(MathOperand) pExpression.getLeftExpression().getRootFactor());
					}
				
			}
			
			
			
			
			//expの場合
			if(underOperatorKind.equals("MOP_EXP")){
				
				//{e^(f(x))}' = e^(f(x)) * f'(x)
				
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("power"), strAttr));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "2.7"));
					
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0).matches(
							eMathMLClassification.MML_OPERATOR)){
						pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					
						pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					} else{
						pExpression.addOperand(
								(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(0));
					}
					
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//power break;
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//eq break;
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

					pExpression = differentiate( pExpression , val );
					
					if(pExpression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータの場合
						pNewExpression.addOperator(
								(MathOperator) pExpression.getLeftExpression().getRootFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}else{
						//オペランドの場合
						pNewExpression.addOperand(
								(MathOperand) pExpression.getLeftExpression().getRootFactor());
					}
				
			}
			
			
			else{
				throw new MathException("Differentiation","differentiate","can't differentiate(not supported operator)");
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}else{
			//導出変数であれば微分値は1,それ以外であれば0
			if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
				pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
			}else{
				pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
			}			
		}
		
		//右辺追加
		pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
		
		
		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		return pNewExpression;
	}
	
}
