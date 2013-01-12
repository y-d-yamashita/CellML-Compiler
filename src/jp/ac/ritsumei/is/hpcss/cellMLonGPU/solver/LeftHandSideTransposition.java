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
 * This class is Left side Transposition for code generator.
 * 
 * input:  
 * 	MathExpression
 * 	Math_ci (derived variable)
 * 
 * limitation of expression:
 * 	The operator contained in expression limits to the following one. 
 *	-plus,minus,times,divide,inc,dec,exp,ln,root,power,log
 *
 *  and non-linear operator (sin,cos,tan) is not supported by this system.
 *  In one equation, only one derived variable is permitted.
 * 
 * @author n-washio
 * 
 */

//2013.1.12
//変更　イコール直下にciを持たず,applyが間に入っている可能性があるのでこれを考慮する.
//変更　構造情報を与えるよう変更.SimpleRecML -> C file　テスト完了

//注意　apply直下にapplyを持つような場合は処理できない.


public class LeftHandSideTransposition {
	
	public MathExpression transporseExpression(MathExpression expression, Math_ci derivedVariable) throws MathException {
			
			//属性情報を取得
			String[] strAttr=((MathOperator)expression.getRootFactor()).getAttribute();
			
			//構造情報取得
			HashMap<String, String> info = ((Math_apply) expression.getRootFactor()).get_hashExpInfo();
			HashMap<Integer, String> attr = ((Math_apply) expression.getRootFactor()).get_hashAttr();
			
			//導出変数が右辺,左辺どちらにあるか探索
			Vector<Math_ci> exp_leftValiableList = new Vector<Math_ci>();
			Vector<Math_ci> exp_rightValiableList = new Vector<Math_ci>();
			
			if(expression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
				expression.getLeftExpression().getAllVariables_SusceptibleOfOverlapWithSelector(exp_leftValiableList);
			}else{
				exp_leftValiableList.add((Math_ci) expression.getLeftExpression().getRootFactor());
			}
			
			if(expression.getRightExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
				expression.getRightExpression().getAllVariables_SusceptibleOfOverlapWithSelector(exp_rightValiableList);
			}else{
				exp_rightValiableList.add((Math_ci) expression.getRightExpression().getRootFactor());
			}
			
			
			//導出変数の個数を確認
			int derivedVariable_count=0;
			for(int j=0;j<exp_leftValiableList.size();j++){
				if(exp_leftValiableList.get(j).toLegalString().equals(derivedVariable.toLegalString())){
					derivedVariable_count++;
				}
			}
			for(int j=0;j<exp_rightValiableList.size();j++){
				if(exp_rightValiableList.get(j).toLegalString().equals(derivedVariable.toLegalString())){
					derivedVariable_count++;
				}
			}
			if(derivedVariable_count!=1){
				//非線形方程式
				expression.addNonlinearFlag();
			}
			boolean targetSide_position=false;
			for(int j=0;j<exp_rightValiableList.size();j++){
				if(exp_rightValiableList.get(j).toLegalString().equals(derivedVariable.toLegalString())){
					targetSide_position=true;
				}
			}
			
			//導出変数を含む辺を左辺に移動
			if(targetSide_position==true){
				expression=replace_TwoSides(expression,strAttr,info,attr);
			}
			
			//導出変数がequal直下の左辺であるか検査
			boolean goal_flag=(check_EqDirectLeft(expression,derivedVariable));
			
			
			
			//equalの直下が導出変数となるまで移項を繰り返す.(非線形の場合は移項しない)
			while(goal_flag!=true){
				if(expression.getNonlinearFlag()==true) break;
				
				MathOperator targetSide = null;
				MathFactor oppositeSide = null;//反対辺はオペレータを含まない可能性があるのでMathFactorとして宣言
				
				//両辺をコピー.
				targetSide =(MathOperator) expression.getLeftExpression().getRootFactor();
				
				if(expression.getRightExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
					oppositeSide =(MathOperator) expression.getRightExpression().getRootFactor();
				}else{
					oppositeSide =(MathOperand) expression.getRightExpression().getRootFactor();
				}

				//左辺apply直下のオペレータの種類を調べ文字列に格納.
				String directOperatorKind = ((MathOperator)targetSide.getUnderFactor()).getOperatorKind().toString();
				
				//導出変数がオペレータの何番目の要素に含まれているかを調べる.
				int val_position = ((MathOperator) targetSide.getUnderFactor()).searchVariablePositionWithSelector(derivedVariable);
				
				int transpositionType=0;
				
				//	1.normal transposition
				//	2.abnormal transposition(target側の移項要素を反対辺の第1要素にするケース)
				//	3.base transposition (power第1要素,log第1要素)
				
				if(directOperatorKind.equals("MOP_PLUS")){
					transpositionType=1;
				}
				else if(directOperatorKind.equals("MOP_MINUS")){
					if(val_position==1)transpositionType=1;
					if(val_position==2)transpositionType=2;
				}
				else if(directOperatorKind.equals("MOP_TIMES")){
					transpositionType=1;
				}
				else if(directOperatorKind.equals("MOP_DIVIDE")){
					if(val_position==1)transpositionType=1;
					if(val_position==2)transpositionType=2;
				}
				else if(directOperatorKind.equals("MOP_INC")){
					transpositionType=1;
				}
				else if(directOperatorKind.equals("MOP_DEC")){
					transpositionType=1;
				}
				else if(directOperatorKind.equals("MOP_EXP")){
					transpositionType=1;
				}
				else if(directOperatorKind.equals("MOP_LN")){
					transpositionType=1;
				}
				else if(directOperatorKind.equals("MOP_ROOT")){
					transpositionType=1;
				}
				else if(directOperatorKind.equals("MOP_POWER")){
					if(val_position==1){
						transpositionType=3;
					}
					if(val_position==2)transpositionType=2;
				}
				else if(directOperatorKind.equals("MOP_LOG")){
					if(val_position==1){
						transpositionType=3;
					}
					if(val_position==2)transpositionType=2;
				}
				else{
					expression.addNonlinearFlag();
				}
				
				//移項処理メソッドへ
				if(transpositionType==1){
					expression=normal_transposition(
							expression,strAttr,targetSide,oppositeSide,directOperatorKind,val_position,info,attr);
				}
				if(transpositionType==2){
					expression=abnormal_transposition(
							expression,strAttr,targetSide,oppositeSide,directOperatorKind,val_position,info,attr);
				}
				if(transpositionType==3){
					expression=base_transposition(
							expression,strAttr,targetSide,oppositeSide,directOperatorKind,val_position,info,attr);
				}
				
				//移項後の判定
				goal_flag=check_EqDirectLeft(expression,derivedVariable);
			}
		
		return expression;
	}
	
	
	public MathExpression base_transposition(
			MathExpression expression,String[] strAttr,MathOperator targetSide,MathFactor oppositeSide,
			String operatorKind,int val_position,HashMap<String,String> info,HashMap<Integer,String> attr) throws MathException{
		
		//pow(x,a) = b  ->  x = exp(ln(b)/a)
		//log(x,a) = b  ->  x = exp(ln(a)/b)
		
		MathExpression pNewExpression = new MathExpression();
		pNewExpression.setExID((long) expression.getExID());
		pNewExpression.setCondref((long) expression.getCondRef());
		String operatorKindString=getOperatorKindString(operatorKind);
		
		if(operatorKindString.equals("power")){
			//pow(x,a) = b  ->  x = exp(ln(b)/a)
		
			pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
			
			//左辺追加 Targetは必ずオペレータ, その直下のファクタを判定
			if(((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position).matches(eMathMLClassification.MML_OPERATOR)){
				//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
				if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}else{
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}
			} else{
				pNewExpression.addOperand(
						((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)));
			}
			
			
			//右辺追加
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("exp"), strAttr));
			
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
					
			
					//divide 第1要素
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("ln"), strAttr));
			
					if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
						if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}else{
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
					} else{
						pNewExpression.addOperand((MathOperand)oppositeSide);
					}
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					//divide 第2要素
					if(((MathOperator)targetSide.getUnderFactor()).getChildFactor(2).matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
						if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
							pNewExpression.addOperator(
									(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}else{
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
							pNewExpression.addOperand(
									(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
					} else{
						pNewExpression.addOperand(
								((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)));
					}
					
			
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		}
		if(operatorKindString.equals("log")){
			//pow(x,a) = b  ->  x = exp(ln(b)/a)
		
			pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
			
			//左辺追加 Targetは必ずオペレータ, その直下のファクタを判定
			if(((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position).matches(eMathMLClassification.MML_OPERATOR)){
				//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
				if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}else{
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperand(
							(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				}
			} else{
				pNewExpression.addOperand(
						((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)));
			}
			
			
			//右辺追加
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("exp"), strAttr));
			
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
					
			
					//divide 第1要素
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("ln"), strAttr));
					
					if(((MathOperator)targetSide.getUnderFactor()).getChildFactor(2).matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
						if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
							pNewExpression.addOperator(
									(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}else{
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
							pNewExpression.addOperand(
									(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
					} else{
						pNewExpression.addOperand(
								((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)));
					}
				
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					//divide 第2要素
					if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
						if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}else{
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
					} else{
						pNewExpression.addOperand((MathOperand)oppositeSide);
					}
					
			
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		}
		
		//構造情報付与
		((Math_apply)pNewExpression.getRootFactor()).set_hashExpInfo(info);
		((Math_apply)pNewExpression.getRootFactor()).set_hashAttr(attr);
		return pNewExpression;
	}

	public MathExpression normal_transposition(
			MathExpression expression,String[] strAttr,MathOperator targetSide,MathFactor oppositeSide,
			String operatorKind,int val_position,HashMap<String,String> info,HashMap<Integer,String> attr) throws MathException{
		
		//移項処理メソッド
		
		MathExpression pNewExpression = new MathExpression();
		pNewExpression.setExID((long) expression.getExID());
		pNewExpression.setCondref((long) expression.getCondRef());
		int childFactorNum = ((MathOperator) targetSide.getUnderFactor()).getChildFactorNum();
		
		if(childFactorNum==1){//単項演算の場合
			
			String operatorKindString=getOperatorKindString(operatorKind);
			String correspondingOperatorKindString=getCorrespondingOperatorKindString(operatorKind);

			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
				
				//左辺追加 Targetは必ずオペレータ, その直下のファクタを判定
				if(((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position).matches(eMathMLClassification.MML_OPERATOR)){
					
					//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
					if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}else{
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperand(
								(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}
				} else{
					pNewExpression.addOperand(
							((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)));
				}
					
				//右辺追加
				if( operatorKindString.equals("plus")){
					if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
						if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}else{
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
					} else{
						pNewExpression.addOperand((MathOperand)oppositeSide);
					}
				} else if (operatorKindString.equals("minus")){
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId(operatorKindString), strAttr));
					
							//Operator第1要素
							if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
								//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
								if(((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
									pNewExpression.addOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
									pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
									pNewExpression.breakOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								}else{
									pNewExpression.addOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
									pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
									pNewExpression.breakOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								}
							} else{
								pNewExpression.addOperand((MathOperand)oppositeSide);
							}

					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				}else if (operatorKindString.equals("inc")||operatorKindString.equals("dec")||
						operatorKindString.equals("exp")||operatorKindString.equals("ln")){
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId(correspondingOperatorKindString), strAttr));
					
							//correspondingOperator第1要素
							if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
								
								//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
								if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
									pNewExpression.addOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
									pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
									pNewExpression.breakOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								}else{
									pNewExpression.addOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
									pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
									pNewExpression.breakOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								}
							} else{
								pNewExpression.addOperand((MathOperand)oppositeSide);
							}
							
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				}else if (operatorKindString.equals("root")){
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId(correspondingOperatorKindString), strAttr));
						
							//correspondingOperator第1要素
							if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
								
								//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
								if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
									pNewExpression.addOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
									pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
									pNewExpression.breakOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								}else{
									pNewExpression.addOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
									pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
									pNewExpression.breakOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								}
							} else{
								pNewExpression.addOperand((MathOperand)oppositeSide);
							}
							
							//correspondingOperator第2要素
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"2"));
							
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				}else if (operatorKindString.equals("log")){
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId(correspondingOperatorKindString), strAttr));
						
							//correspondingOperator第1要素
							//単項の場合はデフォルトの10
							pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"10"));
					
							//correspondingOperator第2要素
							if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
								
								//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
								if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
									pNewExpression.addOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
									pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
									pNewExpression.breakOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								}else{
									pNewExpression.addOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
									pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
									pNewExpression.breakOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								}
							} else{
								pNewExpression.addOperand((MathOperand)oppositeSide);
							}
							
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				}
				
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
		} else if(childFactorNum==2){
			
			
			String correspondingOperatorKindString=getCorrespondingOperatorKindString(operatorKind);
			
			//移項する要素の番号を決める.
			int notDerivedNum = 0;
			for(int i=1;i<=childFactorNum;i++){
				if(i!=val_position){
					notDerivedNum=i;break;
				}
			}
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
				
				//左辺追加 Targetは必ずオペレータ, その直下のファクタを判定
				
				if(((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position).matches(eMathMLClassification.MML_OPERATOR)){
					
					//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
					if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
					
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}else{
						
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperand(
								(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}
						
						
						
				} else{
					pNewExpression.addOperand(
							((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)));
				}
					
				//右辺追加
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId(correspondingOperatorKindString), strAttr));
						
						//correspondingOperator第1要素
						if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
							//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
							if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}else{
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
							
						} else{
							pNewExpression.addOperand((MathOperand)oppositeSide);
						}
						
						//correspondingOperator第2要素
						if( ((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum).matches(eMathMLClassification.MML_OPERATOR)){
							
							//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
							if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
							
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperator(
										(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}else{
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperand(
										(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
							
						} else{
							pNewExpression.addOperand((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum));
						}

				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
		}else if(childFactorNum>2){
			//導出変数以外に複数子要素があるケース(plus,times)
			String operatorKindString=getOperatorKindString(operatorKind);
			String correspondingOperatorKindString=getCorrespondingOperatorKindString(operatorKind);
			
			int notDerivedNum = 0;//移項する要素の番号
			for(int i=1;i<childFactorNum;i++){
				if(i!=val_position){
					notDerivedNum=i;break;
				}
			}
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
			
				//左辺追加 Targetは必ずオペレータ, その直下のファクタを判定
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId(operatorKindString), strAttr));
					for(int i=1;i<=childFactorNum;i++){
						if(i!=notDerivedNum){
							if( ((MathOperator)targetSide.getUnderFactor()).getChildFactor(i).matches(eMathMLClassification.MML_OPERATOR)){
								//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
								if(  ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(i)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
									pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
									pNewExpression.addOperator(
											(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(i)).getUnderFactor());
									pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								} else{
									pNewExpression.addOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
									pNewExpression.addOperand(
											(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(i)).getUnderFactor());
									pNewExpression.breakOperator(
											MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
								}
							} else{
								pNewExpression.addOperand((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(i));
							}
							
						}
					}
			
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
				//右辺追加
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId(correspondingOperatorKindString), strAttr));
					
						//correspondingOperator第1要素
						if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
							//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
							if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}else{
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
						} else{
							pNewExpression.addOperand((MathOperand)oppositeSide);
						}
						
						//correspondingOperator第2要素
						if( ((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum).matches(eMathMLClassification.MML_OPERATOR)){
							//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
							if( ((MathOperator) ((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperator(
										(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}else{
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperand(
										(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
							}
						} else{
							pNewExpression.addOperand((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum));
						}

				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

		}
		
		//構造情報付与
		((Math_apply)pNewExpression.getRootFactor()).set_hashExpInfo(info);
		((Math_apply)pNewExpression.getRootFactor()).set_hashAttr(attr);
		return pNewExpression;
	}
	
	public MathExpression abnormal_transposition(
			MathExpression expression,String[] strAttr,MathOperator targetSide,MathFactor oppositeSide,
			String operatorKind,int val_position,HashMap<String,String> info,HashMap<Integer,String> attr) throws MathException {
		
		MathExpression pNewExpression = new MathExpression();
		pNewExpression.setExID((long) expression.getExID());
		pNewExpression.setCondref((long) expression.getCondRef());
		String operatorKindString=getOperatorKindString(operatorKind);
		String correspondingOperatorKindString=getCorrespondingOperatorKindString(operatorKind);
		//移項する要素の番号
		int notDerivedNum = 1;
		
		//新しい数式を作成
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
			
		//左辺追加 Targetは必ずオペレータ, その直下のファクタを判定
		if(((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position).matches(eMathMLClassification.MML_OPERATOR)){
			//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
			if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			}else{
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperand(
						(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			}
		} else{
			pNewExpression.addOperand(
					((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)));
		}
			
		if(operatorKindString.equals("minus")||operatorKindString.equals("divide")){
			//右辺追加
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId(operatorKindString), strAttr));
					
					//Operator第1要素
					if( ((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum).matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
						if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperator(
									(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}else{
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperand(
									(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
					} else{
						pNewExpression.addOperand((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum));
					}
					
					//Operator第2要素
					if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
						//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
						if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}else{
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						}
					} else{
						pNewExpression.addOperand((MathOperand)oppositeSide);
					}

			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
	
		}else if(operatorKindString.equals("power")||operatorKindString.equals("log")){
		
		//右辺追加
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId(correspondingOperatorKindString), strAttr));

				//Operator第1要素
				if( ((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum).matches(eMathMLClassification.MML_OPERATOR)){
					//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
					if( ((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
						pNewExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}else{
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
						pNewExpression.addOperand(
								(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}
				} else{
					pNewExpression.addOperand((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum));
				}
				
				//Operator第2要素
				if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
					//オペレータ（apply）であってもその下はオペランドの可能性があるので判定する.
					if( ((MathOperator)oppositeSide).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
						pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}else{
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
						pNewExpression.addOperand((MathOperand) ((MathOperator) oppositeSide).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					}
				} else{
					pNewExpression.addOperand((MathOperand)oppositeSide);
				}

			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		}	
				
		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		
		//構造情報付与
		((Math_apply)pNewExpression.getRootFactor()).set_hashExpInfo(info);
		((Math_apply)pNewExpression.getRootFactor()).set_hashAttr(attr);
		return pNewExpression;
	}
	
	public String getOperatorKindString(String operatorKind) {
		
		//オペレータの種類の文字列を返す
		if(operatorKind.equals("MOP_PLUS")) return("plus");
		else if(operatorKind.equals("MOP_MINUS")) return("minus");
		else if(operatorKind.equals("MOP_TIMES")) return("times");
		else if(operatorKind.equals("MOP_DIVIDE")) return("divide");
		else if(operatorKind.equals("MOP_INC")) return("inc");
		else if(operatorKind.equals("MOP_DEC")) return("dec");
		else if(operatorKind.equals("MOP_EXP")) return("exp");
		else if(operatorKind.equals("MOP_LN")) return("ln");
		else if(operatorKind.equals("MOP_ROOT")) return("root");
		else if(operatorKind.equals("MOP_POWER")) return("power");
		else if(operatorKind.equals("MOP_LOG")) return("log");
		else{
			return null;
		}
	}

	public String getCorrespondingOperatorKindString(String operatorKind) {
		
		//対応するオペレータの種類の文字列を返す
		if(operatorKind.equals("MOP_PLUS")) return("minus");
		else if(operatorKind.equals("MOP_MINUS")) return("plus");
		else if(operatorKind.equals("MOP_TIMES")) return("divide");
		else if(operatorKind.equals("MOP_DIVIDE")) return("times");
		else if(operatorKind.equals("MOP_INC")) return("dec");
		else if(operatorKind.equals("MOP_DEC")) return("inc");
		else if(operatorKind.equals("MOP_EXP")) return("ln");
		else if(operatorKind.equals("MOP_LN")) return("exp");
		else if(operatorKind.equals("MOP_ROOT")) return("power");
		else if(operatorKind.equals("MOP_POWER")) return("log");
		else if(operatorKind.equals("MOP_LOG")) return("power");
		else{
			return null;
		}
	}



	public boolean check_EqDirectLeft(MathExpression expression,Math_ci val) throws MathException {
		
		//導出変数がequal直下であるかを判定するメソッド
		
		//2013.1.11
		//変更 イコール直下にciを持たず,applyが間に入っている可能性があるのでこれを考慮する.
		if(expression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			if( ((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor().toLegalString().equals(val.toLegalString())){
				return true;
			} else{
				return false;
			}
		}else{
			if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
				return true;
			} else{
				return false;
			}
			
		}
	}

	public MathExpression replace_TwoSides(MathExpression expression, String[] strAttr, HashMap<String,String> info,HashMap<Integer,String> attr) throws MathException {
		//2013.1.12
		//変更 イコール直下にciを持たず,applyが間に入っている可能性があるのでこれを考慮する.
				
		//applyであってもその下がoperatorかoperandかを判定する必要がある.
		
		
		//右辺に導出変数を含む際,右辺と左辺を入れ替えるメソッド
		
		//数式idとコンディションrefを取得.
		MathExpression newExpression = new MathExpression();
		newExpression.setExID((long) expression.getExID());
		newExpression.setCondref((long) expression.getCondRef());
		
		newExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		newExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
		
		//左辺追加
		if(expression.getRightExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			
			if(((MathOperator)expression.getRightExpression().getRootFactor()).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
			
				//右辺apply下がオペレータの場合
				MathOperator right =(MathOperator)((MathOperator)expression.getRightExpression().getRootFactor()).getUnderFactor();
				newExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				newExpression.addOperator(right);
				newExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			} else{
				//右辺apply下がオペランドの場合
				MathOperand right =(MathOperand)((MathOperator)expression.getRightExpression().getRootFactor()).getUnderFactor();
				newExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				newExpression.addOperand(right);
				newExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			}
			
		}else{
			//左辺直下がオペランドの場合
			MathOperand right = (MathOperand)expression.getRightExpression().getRootFactor();
			newExpression.addOperand(right);
		}
		
		
		//右辺追加
		if(expression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			
			if(((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor().matches(eMathMLClassification.MML_OPERATOR)){
				
				//左辺apply下がオペレータの場合
				MathOperator left =(MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor();
				newExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				newExpression.addOperator(left);
				newExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			} else{
				//左辺apply下がオペランドの場合
				MathOperand left =(MathOperand)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor();
				newExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				newExpression.addOperand(left);
				newExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			}

		}else{
			//左辺がオペランドの場合
			MathOperand left = (MathOperand)expression.getLeftExpression().getRootFactor();
			newExpression.addOperand(left);
		}
		
		newExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		//構造情報付与
		((Math_apply)newExpression.getRootFactor()).set_hashExpInfo(info);
		((Math_apply)newExpression.getRootFactor()).set_hashAttr(attr);
		return newExpression;
	}
	
	public static void main(String[] args) throws MathException {
		
		//数式の属性情報
		String strAttrLoop1 = "null";
		String strAttrLoop2 = "null";
		String strAttrLoop3 = "null";
		String strAttrLoop4 = "null";
		String strAttrLoop5 = "null";
		String[] strAttr = new String[] {strAttrLoop1, strAttrLoop2, strAttrLoop3, strAttrLoop4, strAttrLoop5};
	
		//テスト用変数定義
		Math_ci val1=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "A");
		Math_ci val2=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		val2.addArrayIndexToFront(0);
		Math_ci val3=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		val3.addArrayIndexToFront(1);
		Math_ci val4=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "C");
		Math_ci val5=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "D");
		Math_ci val6=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "E");
		Math_ci val7=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "F");
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
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("power"), strAttr));
			
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
			pNewExpression.addOperand(val1);
			pNewExpression.addOperand(val2);
			
			
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			pNewExpression.addOperand(val2);
			
			
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			//右辺追加
			pNewExpression.addOperand(val7);

		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

		
		
		System.out.println("Input Expression:");
		System.out.println(pNewExpression.toLegalString());
		System.out.println("");
		
		//テスト1
		/*
		MathExpression pNewExpression2 = new MathExpression();
		LeftHandSideTransposition lst2 = new LeftHandSideTransposition();
		pNewExpression2 = lst2.transporseExpression(pNewExpression,val2);
		System.out.println("Derived variable name : "+val2.toLegalString());
		System.out.println(pNewExpression2.toLegalString());
		System.out.println("");
		*/
		//テスト2
		/*
		MathExpression pNewExpression3 = new MathExpression();
		LeftHandSideTransposition lst3 = new LeftHandSideTransposition();
		pNewExpression3 = lst3.transporseExpression(pNewExpression,val3);
		System.out.println("Derived variable name : "+val3.toLegalString());
		System.out.println(pNewExpression3.toLegalString());
		System.out.println("");
		*/
		//テスト3
		/*
		MathExpression pNewExpression4 = new MathExpression();
		LeftHandSideTransposition lst4 = new LeftHandSideTransposition();
		pNewExpression4 = lst4.transporseExpression(pNewExpression,val4);
		System.out.println("Derived variable name : "+val4.toLegalString());
		System.out.println(pNewExpression4.toLegalString());
		System.out.println("");
		*/
		//テスト4
		/*
		MathExpression pNewExpression5 = new MathExpression();
		LeftHandSideTransposition lst5 = new LeftHandSideTransposition();
		pNewExpression5 = lst5.transporseExpression(pNewExpression,val5);
		System.out.println("Derived variable name : "+val5.toLegalString());
		System.out.println(pNewExpression5.toLegalString());
		System.out.println("");
		*/
		//テスト5
		/*
		MathExpression pNewExpression6 = new MathExpression();
		LeftHandSideTransposition lst6 = new LeftHandSideTransposition();
		pNewExpression6 = lst6.transporseExpression(pNewExpression,val6);
		System.out.println("Derived variable name : "+val6.toLegalString());
		System.out.println(pNewExpression6.toLegalString());
		System.out.println("");
		*/
		//テスト6
		
		MathExpression pNewExpression7 = new MathExpression();
		
		LeftHandSideTransposition lst7 = new LeftHandSideTransposition();
		pNewExpression7 = lst7.transporseExpression(pNewExpression,val1);
		System.out.println("Derived variable name : "+val1.toLegalString());
		System.out.println(pNewExpression7.toLegalString());
		System.out.println("");
		
	}
}
