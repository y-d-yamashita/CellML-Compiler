package jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver;


import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_apply;
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
//変更 1/12 applyがオペランドを直下に持つ可能性があるのでこれを考慮する.
public class Differentiation {
	
	public MathExpression differentiate(MathExpression expression, Math_ci val) throws MathException {
		
		//数式の属性情報
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
		MathExpression pNewExpression = new MathExpression();
		HashMap<String, String> info = ((Math_apply) expression.getRootFactor()).get_hashExpInfo();
		HashMap<Integer, String> attr = ((Math_apply) expression.getRootFactor()).get_hashAttr();
		
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
	
		//左辺を導出変数で微分したツリーを付加
		String underOperatorKind = null;
		
		if(expression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			
			//applyの下がオペレータであるか判定
			if( ((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
				
				//apply直下のオペレータであれば種類を格納
				underOperatorKind =
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
				else if(underOperatorKind.equals("MOP_INC")){
					
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
				else if(underOperatorKind.equals("MOP_DEC")){
					
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
				else if(underOperatorKind.equals("MOP_POWER")){
										
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
				else if(underOperatorKind.equals("MOP_ROOT")){
					
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
				else if(underOperatorKind.equals("MOP_EXP")){
					
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
				else if(underOperatorKind.equals("MOP_LN")){
					
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
				else if(underOperatorKind.equals("MOP_LOG")){
					
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
				//apply下にapplyがある場合の処理
				else if(underOperatorKind.equals("MOP_APPLY")){
					
					
					//apply下のapplyの下がオペレータの場合
					if( ((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getUnderFactor().matches(
							eMathMLClassification.MML_OPERATOR) ){
						
						//微分する関数を左辺とし,再帰取得して追加.
						MathExpression pExpression = new MathExpression();
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
						
						
						pExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()));
						
						pExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						pExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						
						
						pExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						
						
						pExpression = differentiate( pExpression , val );
						
						pNewExpression.addOperator((MathOperator) pExpression.getLeftExpression().getRootFactor());
					
					//apply下のapplyの下がオペランドの場合
					}else{
						
						//導出変数であれば1,それ以外の変数または定数は全て0とする
						if(((MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).getUnderFactor().toLegalString().equals(
								val.toLegalString())){
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
						}else{
						
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
						}	
						
					}

					
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					
				}
				
				//想定しないオペレータの場合
				else{
					throw new MathException("Differentiation","differentiate","can't differentiate(not supported operator)");
				}
				

				
			} else{
				
				//apply下がオペランドであれば判定
				
				//導出変数であれば1,それ以外の変数または定数は全て0とする
				if(((MathOperand)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor()).toLegalString().equals(
						val.toLegalString())){
					pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
				}else{
				
					pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
				}	
				
			}
				
			
		}
		
		//右辺追加
		pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
		
		
		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		
		//構造情報付与
		((Math_apply)pNewExpression.getRootFactor()).set_hashExpInfo(info);
		((Math_apply)pNewExpression.getRootFactor()).set_hashAttr(attr);
		return pNewExpression;
	}
	
public static void main(String[] args) throws MathException {
		
		//数式の属性情報
		String[] strAttr = new String[] {"null", "null", "null", "null", "null"};
	
		//テスト用変数定義
		//テスト用変数定義
		Math_ci val1=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "X");
		val1.addIndexList((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
		Math_ci val2=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "X");
		val2.addIndexList((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
		Math_ci val3=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "X");
		val3.addIndexList((MathFactor)MathFactory.createOperand(eMathOperand.MOPD_CN, "2"));
		
		Math_ci val4=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "A");
		Math_ci val5=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		Math_ci val6=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "C");
		Math_ci val7=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "D");
		
		
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
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperand(val1);
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperand(val2);
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperand(val3);
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

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
		Math_ci derivedVal = val1;

		
		//数式の左辺を微分して取得
		pNewExpression = diff.differentiate(pNewExpression  ,derivedVal);
		
		System.out.println("diff Function Form : ");
		System.out.println(pNewExpression.getLeftExpression().toLegalString());
		System.out.println("");
	}
}
