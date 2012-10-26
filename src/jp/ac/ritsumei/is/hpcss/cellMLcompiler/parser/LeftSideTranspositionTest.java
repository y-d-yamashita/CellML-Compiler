package jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.MathMLDefinition.eMathOperand;
/**
 * 数式ライブラリ対応 導出変数左辺移項テストクラス
 * 
 *	対応オペレータ
 *	plus(単項演算,2項演算,多項演算)
 *	minus(単項演算,2項演算)
 *	times(2項演算,多項演算)
 *	divide(2項演算)
 *	inc(単項演算)
 *	dec(単項演算)
 *	exp(単項演算)
 *	ln(単項演算)
 *	root(単項演算)
 *	power(2項演算)
 *	log(単項演算,2項演算)
 * 
 */

public class LeftSideTranspositionTest {

	public static void main(String[] args) throws MathException {
		
		//数式の属性情報(nullにしておく)
		String strAttrLoop1 = "null";
		String strAttrLoop2 = "null";
		String strAttrLoop3 = "null";
		String strAttrLoop4 = "null";
		String strAttrLoop5 = "null";
		String[] strAttr = new String[] {strAttrLoop1, strAttrLoop2, strAttrLoop3, strAttrLoop4, strAttrLoop5};
	
		//テスト用変数定義
		Math_ci val1=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "A");
		Math_ci val2=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "B");
		Math_ci val3=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "C");
		Math_ci val4=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "D");
		Math_ci val5=(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, "E");
		Math_cn num = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "2");
		
		MathExpression pNewExpression5 = new MathExpression();
		pNewExpression5.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		pNewExpression5.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
	
			//左辺追加
			pNewExpression5.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression5.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("plus"), strAttr));
			pNewExpression5.addOperand(val1);
			pNewExpression5.addOperand(val2);
			pNewExpression5.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
			
			//右辺追加
			pNewExpression5.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			pNewExpression5.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("plus"), strAttr));
			
				//plus第１要素
				pNewExpression5.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression5.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("times"), strAttr));
				pNewExpression5.addOperand(val3);
				pNewExpression5.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression5.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("minus"), strAttr));
				pNewExpression5.addOperand(val4);
				pNewExpression5.addOperand(num);
				pNewExpression5.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				pNewExpression5.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
				
				//plus第2要素
				pNewExpression5.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression5.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("log"), strAttr));
				pNewExpression5.addOperand(val5);
				pNewExpression5.addOperand(val4);
				pNewExpression5.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
				
				//plus第3要素
				pNewExpression5.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
				pNewExpression5.addOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
				pNewExpression5.addOperand(val1);
				pNewExpression5.addOperand(val2);
				pNewExpression5.breakOperator(
						MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
	
			pNewExpression5.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
			
		pNewExpression5.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));

		
		System.out.println("Input Expression: derived variable name = "+val5.toLegalString());
		System.out.println(pNewExpression5.toLegalString());
		System.out.println();
		
		//属性情報を取得
		strAttr=((MathOperator)pNewExpression5.getRootFactor()).getAttribute();
		
		//導出変数が右辺,左辺どちらにあるか探索
		Vector<Math_ci> exp_leftValiableList = new Vector<Math_ci>();
		Vector<Math_ci> exp_rightValiableList = new Vector<Math_ci>();
		
		if(pNewExpression5.getLeftExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			pNewExpression5.getLeftExpression().getAllVariables(exp_leftValiableList);
		}else{
			exp_leftValiableList.add((Math_ci) pNewExpression5.getLeftExpression().getRootFactor());
		}
		if(pNewExpression5.getRightExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			pNewExpression5.getRightExpression().getAllVariables(exp_rightValiableList);
		}else{
			exp_rightValiableList.add((Math_ci) pNewExpression5.getRightExpression().getRootFactor());
		}		
		
		boolean targetSide_position=false;
		for(int i=0;i<exp_rightValiableList.size();i++){
			if(exp_rightValiableList.get(i).matches(val5)){
				//右辺であればtrue
				targetSide_position=true;
			}
		}
		
		//導出変数を含む辺を左辺に移動
		if(targetSide_position==true){
			pNewExpression5=replace_TwoSides(pNewExpression5,strAttr);
		}
		
		//導出変数がequal直下の左辺であるか検査
		boolean goal_flag=(check_EqDirectLeft(pNewExpression5,val5));
		
		//equalの直下が導出変数となるまで移項を繰り返す.
		while(goal_flag!=true){
			
			MathOperator targetSide = null;
			MathFactor oppositeSide = null;//反対辺はオペレータを含まない可能性があるのでMathFactorとして宣言
			
			//両辺をコピー.
			targetSide =(MathOperator) pNewExpression5.getLeftExpression().getRootFactor();
			if(pNewExpression5.getRightExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
				oppositeSide =(MathOperator) pNewExpression5.getRightExpression().getRootFactor();
			}else{
				oppositeSide =(MathOperand) pNewExpression5.getRightExpression().getRootFactor();
			}

			//左辺apply直下のオペレータの種類を調べ文字列に格納.
			String directOperatorKind = ((MathOperator)targetSide.getUnderFactor()).getOperatorKind().toString();
			
			//導出変数がオペレータの何番目の要素に含まれているかを調べる.
			int val_position = ((MathOperator) targetSide.getUnderFactor()).searchVariablePosition(val5);
			
			int transpositionType=0;//移項方式を判定.
			//	1.normal transposition
			//	2.abnormal transposition(target側の移項要素を反対辺の第1要素にするケース)
			//	3.base transposition	(導出変数が底となっているケース)
			
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
				if(val_position==1)transpositionType=3;
				if(val_position==2)transpositionType=2;
			}
			else if(directOperatorKind.equals("MOP_LOG")){
				if(val_position==1)transpositionType=3;
				if(val_position==2)transpositionType=2;
			}
			else{
				System.out.println("cannot transport operator");
			}
			
			//移項処理メソッドへ
			if(transpositionType==1){
				pNewExpression5=normal_transposition(
						pNewExpression5,strAttr,targetSide,oppositeSide,directOperatorKind,val_position);
			}
			if(transpositionType==2){
				pNewExpression5=abnormal_transposition(
						pNewExpression5,strAttr,targetSide,oppositeSide,directOperatorKind,val_position);
			}
			if(transpositionType==3){
				pNewExpression5=base_transposition(
						pNewExpression5,strAttr,targetSide,oppositeSide,directOperatorKind,val_position);
			}
			
			//移項後の判定
			goal_flag=check_EqDirectLeft(pNewExpression5,val5);
		}
				
		System.out.println("Output Expression:");
		System.out.println(pNewExpression5.toLegalString());
	}
	
	public static MathExpression normal_transposition(
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
							pNewExpression.addOperator(
									(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
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
								pNewExpression.addOperator(
										(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(i)).getUnderFactor());
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
							pNewExpression.addOperator(
									(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(notDerivedNum)).getUnderFactor());
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
	
	public static MathExpression abnormal_transposition(
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
			pNewExpression.addOperator(
					(MathOperator)((MathOperator)((MathOperator)targetSide.getUnderFactor()).getChildFactor(val_position)).getUnderFactor());
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
	public static MathExpression base_transposition(
			MathExpression expression,String[] strAttr,MathOperator targetSide,MathFactor oppositeSide,
			String operatorKind,int val_position) throws MathException {
		
		MathExpression pNewExpression = new MathExpression();
		String operatorKindString=getOperatorKindString(operatorKind);
		String correspondingOperatorKindString=getCorrespondingOperatorKindString(operatorKind);
		//移項する要素の番号
		int notDerivedNum = 2;
		
		//新しい数式を作成
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
			
		if(operatorKindString.equals("power")){
			//右辺追加
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
					
					//Operator第2要素
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
						
						//divide第1要素
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"1"));
					
						//divide第2要素
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
						
					pNewExpression.breakOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));	

			pNewExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
	
		}else if(operatorKindString.equals("log")){
		
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
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
					pNewExpression.addOperator(
							MathFactory.createOperator(MathMLDefinition.getMathOperatorId("divide"), strAttr));
						
						//divide第1要素
						pNewExpression.addOperand((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,"1"));
					
						//divide第2要素
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
				
		pNewExpression.breakOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply")));
		
		return pNewExpression;
	}
	
	public static String getOperatorKindString(String operatorKind) {
		
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

	public static String getCorrespondingOperatorKindString(String operatorKind) {
		
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



	public static boolean check_EqDirectLeft(MathExpression expression,Math_ci val) {

		//導出変数がequal直下であるかを判定するメソッド
		if(expression.getLeftExpression().getRootFactor().matches(val)){
			return true;
		} else{
			return false;
		}
	}

	public static MathExpression replace_TwoSides(MathExpression expression, String[] strAttr) throws MathException {
		
		//右辺に導出変数を含む際,右辺と左辺を入れ替えるメソッド
		MathExpression newExpression = new MathExpression();		
		newExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
		newExpression.addOperator(
				MathFactory.createOperator(MathMLDefinition.getMathOperatorId("eq"), strAttr));
		
		//左辺追加
		if(expression.getRightExpression().getRootFactor().matches(eMathMLClassification.MML_OPERATOR)){
			//左辺がオペレータの場合
			MathOperator right =(MathOperator)((MathOperator)expression.getRightExpression().getRootFactor()).getUnderFactor();
			newExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId("apply"), strAttr));
			newExpression.addOperator(right);
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
	
}
