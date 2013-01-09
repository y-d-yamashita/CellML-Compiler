package jp.ac.ritsumei.is.hpcss.cellMLonGPU.solver;

import java.util.HashMap;
import java.util.Set;
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
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.visitor.GetVariablesVisitor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.CCLogger;

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

//非線形フラグ対応
//2012/12/24 BaseTransposition対応
public class LeftHandSideTransposition {
	
	public MathExpression transporseExpression(MathExpression expression, Math_ci derivedVariable) throws MathException {
			
			//属性情報を取得
			String[] strAttr=((MathOperator)expression.getRootFactor()).getAttribute();
			
			//導出変数が右辺,左辺どちらにあるか探索
//			Vector<Math_ci> exp_leftValiableList = new Vector<Math_ci>();
//			Vector<Math_ci> exp_rightValiableList = new Vector<Math_ci>();

			Vector<MathOperand> exp_leftValiableList = new Vector<MathOperand>();
			Vector<MathOperand> exp_rightValiableList = new Vector<MathOperand>();

			if(expression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
				//expression.getLeftExpression().getAllVariablesWithSelector(exp_leftValiableList);
				expression.getLeftExpression().getAllVariables_SusceptibleOfOverlapWithSelector(exp_leftValiableList);
			}else{
				//Changed due to CallCastException (Math_cn to Math_ci) by y-yamashita
				//exp_leftValiableList.add((Math_ci) expression.getLeftExpression().getRootFactor());
				if(expression.getLeftExpression().getRootFactor() instanceof Math_ci){
					exp_rightValiableList.add((Math_ci) expression.getLeftExpression().getRootFactor());
				}else{
					exp_rightValiableList.add((Math_cn) expression.getLeftExpression().getRootFactor());
				}
			}
			if(expression.getRightExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
				//expression.getRightExpression().getAllVariablesWithSelector(exp_rightValiableList);
				expression.getRightExpression().getAllVariables_SusceptibleOfOverlapWithSelector(exp_rightValiableList);
			}else{
				//Changed due to CallCastException (Math_cn to Math_ci) by y-yamashita
				//exp_rightValiableList.add((Math_ci) expression.getRightExpression().getRootFactor());
				if(expression.getRightExpression().getRootFactor() instanceof Math_ci){
					exp_rightValiableList.add((Math_ci) expression.getRightExpression().getRootFactor());
				}else{
					exp_rightValiableList.add((Math_cn) expression.getRightExpression().getRootFactor());
				}
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
				//throw new MathException("LeftSideTransposition","transporseExpression","can't transpose");
			}
			boolean targetSide_position=false;
			for(int j=0;j<exp_rightValiableList.size();j++){
				if(exp_rightValiableList.get(j).toLegalString().equals(derivedVariable.toLegalString())){
					//右辺であればtrue
					targetSide_position=true;
				}
			}
			
			//導出変数を含む辺を左辺に移動
			if(targetSide_position==true){
				expression=replace_TwoSides(expression,strAttr);
			}
			
			//導出変数がequal直下の左辺であるか検査
			boolean goal_flag=(check_EqDirectLeft(expression,derivedVariable));
			
			
			
			//equalの直下が導出変数となるまで移項を繰り返す.(非線形の場合は移項しない)
			while(goal_flag!=true || expression.getNonlinearFlag()==true){
				
				MathOperator targetSide = null;
				MathFactor oppositeSide = null;//反対辺はオペレータを含まない可能性があるのでMathFactorとして宣言
				
				CCLogger.log(expression.toLegalString());
				
				
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
				
				int transpositionType=0;//移項方式を判定.
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
						//expression.addNonlinearFlag();
						//throw new MathException("LeftSideTransposition","transporseExpression","can't transpose");
					}
					if(val_position==2)transpositionType=2;
				}
				else if(directOperatorKind.equals("MOP_LOG")){
					if(val_position==1){
						transpositionType=3;
						//expression.addNonlinearFlag();
						//throw new MathException("LeftSideTransposition","transporseExpression","can't transpose");
					}
					if(val_position==2)transpositionType=2;
				}
				else{
					throw new MathException("LeftSideTransposition","transporseExpression","can't transpose");
				}
				
				//移項処理メソッドへ
				if(transpositionType==1){
					expression=normal_transposition(
							expression,strAttr,targetSide,oppositeSide,directOperatorKind,val_position);
				}
				if(transpositionType==2){
					expression=abnormal_transposition(
							expression,strAttr,targetSide,oppositeSide,directOperatorKind,val_position);
				}
				if(transpositionType==3){
					expression=base_transposition(
							expression,strAttr,targetSide,oppositeSide,directOperatorKind,val_position);
				}
				
				//移項後の判定
				goal_flag=check_EqDirectLeft(expression,derivedVariable);
			}
		
		return expression;
	}
	
	
	public MathExpression base_transposition(
			MathExpression expression,String[] strAttr,MathOperator targetSide,MathFactor oppositeSide,
			String operatorKind,int val_position) throws MathException{
		
		//pow(x,a) = b  ->  x = exp(ln(b)/a)
		//log(x,a) = b  ->  x = exp(ln(a)/b)
		
		MathExpression pNewExpression = new MathExpression();
		String operatorKindString=getOperatorKindString(operatorKind);
		
		if(operatorKindString.equals("power")){
			//pow(x,a) = b  ->  x = exp(ln(b)/a)
		
			pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
			
			//左辺追加 Targetは必ずオペレータ, その直下のファクタを判定
			if(((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position).matches(eMathMLClassification.MML_OPERATOR)){
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
						pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					} else{
						pNewExpression.addOperand((MathOperand)oppositeSide);
					}
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					//divide 第2要素
					if(((MathOperator)targetSide.getUnderFactor()).getChildFactor(2).matches(eMathMLClassification.MML_OPERATOR)){
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
				pNewExpression.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression.addOperator(
						(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
						pNewExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					} else{
						pNewExpression.addOperand(
								((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(2)));
					}
				
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					
					//divide 第2要素
					if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
						pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					} else{
						pNewExpression.addOperand((MathOperand)oppositeSide);
					}
					
			
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		}
		
		return pNewExpression;
	}

	public MathExpression normal_transposition(
			MathExpression expression,String[] strAttr,MathOperator targetSide,MathFactor oppositeSide,
			String operatorKind,int val_position) throws MathException{
		
		//移項処理メソッド
		
		MathExpression pNewExpression = new MathExpression();
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
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					pNewExpression.addOperand(
							((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)));
				}
					
				//右辺追加
				if( operatorKindString.equals("plus")){
					if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
						pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
								pNewExpression.addOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
								pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
								pNewExpression.breakOperator(
										MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));

					//Changed due to ClassCastException by y-yamashita
					if(((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor() instanceof MathOperator){
						pNewExpression.addOperator(
								(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
					}else{
						pNewExpression.addOperand(
								(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
					}

					
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						} else{
							pNewExpression.addOperand((MathOperand)oppositeSide);
						}
						
						//correspondingOperator第2要素
						if( ((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum).matches(eMathMLClassification.MML_OPERATOR)){
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							//pNewExpression.addOperator(
							//		(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());

							//Changed due to ClassCastException by y-yamashita
							if(((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor() instanceof MathOperator){
								pNewExpression.addOperator(
										(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
							}else{
								pNewExpression.addOperand(
										(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
							}

							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
								pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));

								//Changed due to CallCastException (Math_ci to MathOperator) by y-yamashita
								//pNewExpression.addOperator(
								//		(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(i)).getUnderFactor());
								if(((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(i)).getUnderFactor() instanceof MathOperator){
									pNewExpression.addOperator(
											(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(i)).getUnderFactor());
								}else{
									pNewExpression.addOperand(
											(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(i)).getUnderFactor());
								}

								
								pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
							pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						} else{
							pNewExpression.addOperand((MathOperand)oppositeSide);
						}
						
						//correspondingOperator第2要素
						if( ((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum).matches(eMathMLClassification.MML_OPERATOR)){
							pNewExpression.addOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	

							//Changed due to CallCastException (Math_ci to MathOperator) by y-yamashita
							//pNewExpression.addOperator(
							//		(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
							if(((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor() instanceof MathOperator){
								pNewExpression.addOperator(
										(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
							}else{
								pNewExpression.addOperand(
										(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
							}

							pNewExpression.breakOperator(
									MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
						} else{
							pNewExpression.addOperand((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum));
						}

				pNewExpression.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

		}
		return pNewExpression;
	}
	
	public MathExpression abnormal_transposition(
			MathExpression expression,String[] strAttr,MathOperator targetSide,MathFactor oppositeSide,
			String operatorKind,int val_position) throws MathException {
		
		MathExpression pNewExpression = new MathExpression();
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
			pNewExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			//Changed due to CallCastException (Math_ci to MathOperator) by y-yamashita
			//pNewExpression.addOperator(
			//		(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
			if(((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor() instanceof MathOperator){
				pNewExpression.addOperator(
						(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
			}else{
				pNewExpression.addOperand(
						(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
			}

			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
						//Changed due to ClassCastException by y-yamashita
						//pNewExpression.addOperator(
						//		(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
						if(((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor() instanceof MathOperator){
							pNewExpression.addOperator(
									(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
						}else{
							pNewExpression.addOperand(
									(MathOperand)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
						}

						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
					} else{
						pNewExpression.addOperand((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum));
					}
					
					//Operator第2要素
					if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
						pNewExpression.addOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
						pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
						pNewExpression.breakOperator(
								MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
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
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
					pNewExpression.addOperator(
							(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					pNewExpression.addOperand((MathOperand)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum));
				}
				
				//Operator第2要素
				if(oppositeSide.matches(eMathMLClassification.MML_OPERATOR)){
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));	
					pNewExpression.addOperator((MathOperator) ((MathOperator) oppositeSide).getUnderFactor());
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				} else{
					pNewExpression.addOperand((MathOperand)oppositeSide);
				}

			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		}	
				
		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
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
		if(expression.getLeftExpression().getRootFactor().toLegalString().equals(val.toLegalString())){
			return true;
		} else{
			return false;
		}
	}

	public MathExpression replace_TwoSides(MathExpression expression, String[] strAttr) throws MathException {
		
		//右辺に導出変数を含む際,右辺と左辺を入れ替えるメソッド
		MathExpression newExpression = new MathExpression();		
		newExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		newExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
		
		//左辺追加
		if(expression.getRightExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			//左辺がオペレータの場合
			//Commented out by y-yamashita
			//MathOperator right =(MathOperator)((MathOperator)expression.getRightExpression().getRootFactor()).getUnderFactor();
			newExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));

			//Changed by y-yamashita ClassCasetException
			if(((MathOperator)expression.getRightExpression().getRootFactor()).getUnderFactor() instanceof MathOperator){
			//newExpression.addOperator(right);
			newExpression.addOperator((MathOperator)((MathOperator)expression.getRightExpression().getRootFactor()).getUnderFactor());
			}else{
				newExpression.addOperand((MathOperand)((MathOperator)expression.getRightExpression().getRootFactor()).getUnderFactor());	
			}
			
			newExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		}else{
			//左辺がオペランドの場合
			MathOperand Right = (MathOperand)expression.getRightExpression().getRootFactor();
			newExpression.addOperand(Right);
		}
		
		//右辺追加
		if(expression.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			//左辺がオペレータの場合
			MathOperator left =(MathOperator)((MathOperator)expression.getLeftExpression().getRootFactor()).getUnderFactor();
			newExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			newExpression.addOperator(left);
			newExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		}else{
			//左辺がオペランドの場合
			MathOperand left = (MathOperand)expression.getLeftExpression().getRootFactor();
			newExpression.addOperand(left);
		}
		
		newExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		
		return newExpression;
	}
	
	/**
	 * 式変換が必要がチェックするメッソド
	 * @param expression
	 * @param derivedVariable
	 * @return "target変数  == ~"の形なら式変形の必要がないのでfalse, それ以外はtrue
	 * @author y-yamashita
	 */
	public boolean isNecessaryTransporse(MathExpression expression, Math_ci derivedVariable){
		MathExpression left = expression.getLeftExpression();
		GetVariablesVisitor visitor = new GetVariablesVisitor();
		left.traverse(visitor);
		Set<Math_ci> variableSet = visitor.geVariableSet();
		
		if(variableSet.size()==1){
			try {
				String varName = variableSet.iterator().next().toLegalString();
				String derivedVarName = derivedVariable.toLegalString();
				if(varName.equals(derivedVarName)){
					return false;
				}
			} catch (MathException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
