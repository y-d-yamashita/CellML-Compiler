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
 * This class is Differentiation for code generator.
 * 
 * input:  
 * 	MathExpression f(x)=0
 * 	Math_ci
 * 
 * output f'(x)=0
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
				
				
				for(int i=1;i<childFactorNum+1;i++){
					
					if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i).matches(
							eMathMLClassification.MML_OPERATOR) ){
						
						//微分する関数を左辺とし,再帰取得して追加.
						MathExpression pExpression = new MathExpression();
						
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
						
						pExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i));
						
						pExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						
						
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
						if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i).toLegalString().equals(
								val.toLegalString())){
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
						}else{
						
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						}	
					}
				}
				
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
				
				for(int i=1;i<childFactorNum+1;i++){
					
					if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i).matches(
							eMathMLClassification.MML_OPERATOR) ){
						
						//微分する関数を左辺とし,再帰取得して追加.
						MathExpression pExpression = new MathExpression();
						
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
						
						pExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i));
						
						pExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						
						
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
						if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i).toLegalString().equals(
								val.toLegalString())){
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
						}else{
						
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						}	
					}
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			} 
			
			//timesの場合(多項演算)
			else if(underOperatorKind.equals("MOP_TIMES")&&
					((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactorNum()>2){
				
				int childFactorNum = 
						((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactorNum();
				
				//2項演算として微分
				MathExpression pExpression = new MathExpression();
				
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
				
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				
				//f(x)第1要素				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					pExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
				}
				
				//第2要素以降は1つのオペレータにまとめる
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				for(int i=2;i<childFactorNum+1;i++){
					if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i).matches(
							eMathMLClassification.MML_OPERATOR) ){
						pExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i));
						pExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					} else{
						pExpression.addOperand(
								(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(i));
					}
				}
				
				pExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break;
				
				pExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break;
				
				
				pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
				
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
			
			//timesの場合(2項演算)
			else if(underOperatorKind.equals("MOP_TIMES")&&
					((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactorNum()==2){
				
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
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
	
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				} else{
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break;
				
				//f * g'
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
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
				
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).matches(
						eMathMLClassification.MML_OPERATOR) ){
					
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
	
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break;
				
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
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
	
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				} else{
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break;

				
				//f * g'
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
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
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).matches(
						eMathMLClassification.MML_OPERATOR) ){
					
					//微分する関数を左辺とし,再帰取得して追加.
					MathExpression pExpression = new MathExpression();
					
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
					
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
	
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break

				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //minus break;
				
				//g^2
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
					
						pNewExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
						
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						pNewExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
						
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break;
				
				} else{
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
					
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
					
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break;
				
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
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //cos break;
				
				//f'(x)
				
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
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break;
				
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
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else {
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
				}
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //sin break;
				
				//f'(x)
				
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
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break;
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //minus break;
				
			}

			else if(underOperatorKind.equals("MOP_TAN")){
				//合成関数の微分に対応
				// 1/cos(f(x))^2 * 
				
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
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
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else {
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
				}
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //cos break;
				
				 //cos(f(x))
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("cos"), strAttr));
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else {
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
				}
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //cos break;
				
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//divide break
				
				//f'(x)
				
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
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //times break;
				
			}
			
			//incの場合
			if(underOperatorKind.equals("MOP_INC")){
				
				//inc(x) = x + 1

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
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
					
				}
			}

			//decの場合
			if(underOperatorKind.equals("MOP_DEC")){
				
				//dec(x) = x - 1

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
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
							val.toLegalString())){
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
							
							pExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							
							pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
							
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
							if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
									val.toLegalString())){
								pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
							}else{
							
								pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
							}	
							
						}
						
						if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).matches(
								eMathMLClassification.MML_OPERATOR) ){
							pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						} else{
							pNewExpression.addOperand(
									(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
									
						}
						
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break;
						
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
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//divide break;
						
						//log(f)*g'
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
						
						
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("ln"), strAttr));
						
						
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
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//ln break;
					
						//g'
						if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).matches(
								eMathMLClassification.MML_OPERATOR) ){
							//微分する関数を左辺とし,再帰取得して追加.
							MathExpression pExpression = new MathExpression();
							
							pExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
							pExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
							
							pExpression.addOperator(
									(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
							
							pExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							
							pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
							
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
							if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).toLegalString().equals(
									val.toLegalString())){
								pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
							}else{
							
								pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
							}	
							
						}
						
						
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break;
						
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//plus break;

					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//times break;			
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
					
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
							eMathMLClassification.MML_OPERATOR)){
						pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					
						pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					} else{
						pExpression.addOperand(
								(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					}
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0.5"));
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//power break;
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));//eq break;

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
				//合成関数の微分に対応
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				
				//e^(f(x))
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("exp"), strAttr));
				
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
				
				//f'(x)
				
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
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));

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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}	
				}
				
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
			}
			
			//lnの場合
			if(underOperatorKind.equals("MOP_LN")){
				
				//{ln(f(x))}' = f'(x) / (f(x))
				//合成関数の微分に対応
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
				
				//f'(x)
				
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
					
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					
					
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
					if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).toLegalString().equals(
							val.toLegalString())){
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
					}else{
					
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
					}		
				}
	
				//f(x)
				
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
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"))); //divide break;
				
			}
			
			//logの場合
			if(underOperatorKind.equals("MOP_LOG")){
				
				//{log(f(x),g(x))}' = {ln(g(x)) / ln(f(x))}'
				//底変換により微分.
				
				//微分する関数を左辺とし,再帰取得して追加.
				MathExpression pExpression = new MathExpression();
				
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
				
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
				
				
				//ln(g(x))
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("ln"), strAttr));
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					pExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(2));
				}
				pExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));				
				
				//ln(f(x))
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("ln"), strAttr));
				
				if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1).matches(
						eMathMLClassification.MML_OPERATOR) ){
					pExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
					pExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					pExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getChildFactor(1));
				}
				
				pExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				
				pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
				
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
			/*
			else{
				
				
				throw new MathException("Differentiation","differentiate","can't differentiate(not supported operator)");
			}
			*/
			
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
	
public static void main(String[] args) throws MathException {
		
		//数式の属性情報
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
	
		//テスト用変数定義
		//テスト用変数定義
		Math_ci val1=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "A");
		Math_ci val2=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		val2.addIndexList((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
		Math_ci val3=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		val3.addIndexList((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
		Math_ci val4=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "C");
		Math_ci val5=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "D");
		Math_ci val6=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "E");
		Math_ci val7=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "F");
		
		
		Math_cn zero = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0");
		Math_cn num = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "5");
		MathExpression pNewExpression = new MathExpression();
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
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("power"), strAttr));
				pNewExpression.addOperand(val1);
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
				pNewExpression.addOperand(val2);
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
				pNewExpression.addOperand(val3);
				pNewExpression.addOperand(val4);
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
				
				//plus第3要素
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
				pNewExpression.addOperand(val5);
				pNewExpression.addOperand(val6);
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
	
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			
			//右辺追加
			pNewExpression.addOperand(zero);

		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

		
		
		System.out.println("Input Function : ");
		System.out.println(pNewExpression.getLeftExpression().toLegalString());
		System.out.println("");
		Differentiation diff = new Differentiation();
		//導出変数を設定
		Math_ci derivedVal = val5;

		
		//数式の左辺を微分して取得
		pNewExpression = diff.differentiate(pNewExpression  ,derivedVal);
		
		System.out.println("diff Function Form : ");
		System.out.println(pNewExpression.getLeftExpression().toLegalString());
		System.out.println("");
	}
}
