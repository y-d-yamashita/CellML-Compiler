package jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;


import jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure.DecisionLoopStructure;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure.HighSpeedDecisionLoopStructure;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure.RelationPattern;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.MathMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecMLAnalyzer;

public class Labelattr2 extends SimpleRecMLAnalyzer{
	
	//お引越し.
	public ArrayList<Integer> path_length;
	public ArrayList<String[]> prepostList = new  ArrayList<String[]>();
	public ArrayList<Integer[]> loop_nameList = new ArrayList<Integer[]>();
	public ArrayList<ArrayList<Integer[]>> loop_nameListPair = new ArrayList<ArrayList<Integer[]>>();
	public ArrayList<RelationPattern> loopStructure= new ArrayList<RelationPattern>();
	public HashMap<Integer,Integer> connectableNode = new HashMap<Integer,Integer>();
	//ここまで

	HashMap<Integer, HashMap<String, Integer>> m_HashMapNodeList;
	HashMap<Integer, HashMap<String, Integer>> m_HashMapEdgeList;
	Vector<Integer> m_VecEquOder;
	
	HashMap<Integer, Integer> m_finalAttrEquNumLists;	
	HashMap<Integer, Integer> m_initendAttrEquNumLists;
	HashMap<Integer, Integer> m_condrefAttrEquNumLists;
	HashMap<Integer, String> m_indexList;
	HashMap<Integer, HashMap<Integer, String>> m_AttrLists;
	
	ArrayList<RelationPattern> m_LoopStructure;
	
	public Labelattr2(
			HashMap<Integer, HashMap<String, Integer>> NodeHashMap,
			HashMap<Integer, HashMap<String, Integer>> EdgeHashMap,
			Vector<Integer> EquOderVec,
			HashMap<Integer, Integer> finalAttr,
			HashMap<Integer, Integer> initendAttr,
			HashMap<Integer, Integer> CondrefAttr,
			HashMap<Integer, String> indexList){

		m_HashMapNodeList = NodeHashMap;
		m_HashMapEdgeList = EdgeHashMap;
		m_VecEquOder = EquOderVec;
		
		m_finalAttrEquNumLists = finalAttr;
		m_initendAttrEquNumLists = initendAttr;
		m_condrefAttrEquNumLists = CondrefAttr;
		m_indexList = indexList;
		
		m_AttrLists = new HashMap<Integer, HashMap<Integer, String>>();
		
		m_LoopStructure = new ArrayList<RelationPattern>();

	}
	
	public HashMap<Integer, HashMap<String, Integer>> getM_HashMapNodeList() {
		return m_HashMapNodeList;
	}
	public HashMap<Integer, HashMap<String, Integer>> getM_HashMapEdgeList() {
		return m_HashMapEdgeList;
	}
	
	public void set_AttrLists(HashMap<Integer, HashMap<Integer, String>> lists){
		m_AttrLists = lists;
	}
	public HashMap<Integer, HashMap<Integer, String>> getM_AttrLists(){
		return m_AttrLists;
	}
	public ArrayList<RelationPattern> getM_LoopStrucuture(){
		return m_LoopStructure;
	}
	
	//========================================================
	//getLoopStructure
	// ループ構造決定メソッド
	// 全配列変数に対する属性情報からループ構造を決定する
	//========================================================
	public void getLoopStructure(){
		
		//---------------------------------------------------
		//全配列変数に対する属性情報を決定する
		//---------------------------------------------------
		labelAllocation();
//		System.out.println("------------Origin Attribute------------");
//		outputLabelSample(m_AttrLists);

		
		//---------------------------------------------------
		//配列変数の数によって解析方法を変更する
		//---------------------------------------------------
		if(m_indexList.size() == 0){
			
		}else if(m_indexList.size() == 1){
			RelationPattern rp = new RelationPattern(0,-1,"");
			m_LoopStructure.add(rp);
			
//			nullChange();
			
			//---------------------------------------------------
			//conditionに対して属性情報を割り振る
			//---------------------------------------------------
			addConditionAttr(m_AttrLists);
//			outputLabelSample(m_AttrLists);
		}else{
			
//			DecisionLoopStructure dls = new DecisionLoopStructure();
//			//---------------------------------------------------
//			//入力情報作成メソッド
//			//inputListを作成する。
//			//---------------------------------------------------
//			ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
//			inputList = dls.make_inputList(m_AttrLists, m_VecEquOder, m_indexList);
//
//			//---------------------------------------------------
//			//入力情報に対する部分的な依存関係処理
//			//---------------------------------------------------
//			inputList=dls.fix_PartialDependency(inputList);
//			
//			//---------------------------------------------------
//			//関係情報の全パターン列挙
//			//---------------------------------------------------
//			ArrayList<RelationPattern> relationList = new ArrayList<RelationPattern>();
//			relationList = dls.get_relationList(m_indexList);
//			
//			//---------------------------------------------------
//			//関係情報の組み合わせパターン列挙
//			//関係リストのパターンからループ数-1を選ぶ組み合わせをリスト化する
//			//---------------------------------------------------
//			ArrayList<ArrayList<RelationPattern>> combinationList = new ArrayList<ArrayList<RelationPattern>>();	
//			combinationList = dls.get_combinationList(relationList, m_indexList);
//			
//			//---------------------------------------------------
//			//関係情報の組み合わせパターンの削減（１）
//			//ループの種類を網羅していない要素を削除
//			//---------------------------------------------------
//			combinationList = dls.reduce_combinationList_1st(combinationList, m_indexList.size());
//			
//			//---------------------------------------------------
//			//関係情報の組み合わせパターンの削減（２）
//			//複数の異なる親をもつ子が存在するものを削除
//			//---------------------------------------------------
//			combinationList = dls.reduce_combinationList_2nd(combinationList, m_indexList.size());
//			
//			//---------------------------------------------------
//			//関係情報の組み合わせパターンの削減（３）
//			//複数の同じ属性を親がもつ組み合わせを削除
//			//---------------------------------------------------
//			combinationList = dls.reduce_combinationList_3rd(combinationList, m_indexList.size());
//			
//			//---------------------------------------------------
//			//ループ構造の決定
//			//孫以降へ継承する情報を入力情報と比較し、矛盾があれば削除
//			//---------------------------------------------------
//
//			m_LoopStructure = dls.get_LoopStructure(combinationList, m_indexList.size(), inputList);
//			
//			System.out.println("------------- debug Ouptup Loopstrucutre -------------");
//			DecisionLoopStructureVer2.toRecMLheader(m_LoopStructure);
			

			
			/*Hight Speed Ver*/
			
			HighSpeedDecisionLoopStructure2 hsdls = new HighSpeedDecisionLoopStructure2();

			//---------------------------------------------------
			//入力情報作成メソッド
			//inputListを作成
			//---------------------------------------------------
			ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
			inputList = hsdls.make_inputList(m_AttrLists, m_VecEquOder, m_indexList);
			
			//---------------------------------------------------
			//重複処理
			//---------------------------------------------------
			inputList= removeOverlap(inputList);
//			for(int i=0;i<inputList.size();i++){
//				System.out.print("--------------------------------washio1--" );
//				inputList.get(i).printContents();
//			}
			
			Set<Integer> loopName = m_indexList.keySet();
			Integer[] loopNameList = (Integer[]) loopName.toArray(new Integer[loopName.size()]);
			
			set_nameList(0, new int [m_indexList.size()], new boolean [m_indexList.size()],  loopNameList);
			
			make_pairList(0,new int [2], loop_nameList.size());

			//---------------------------------------------------
			//入力情報に対する部分的な依存関係処理
			//---------------------------------------------------
			inputList=hsdls.fix_PartialDependency(inputList);
			
			
			//---------------------------------------------------
			//入力情報に対するinner子要素の処理
			//あるノードのinner子要素がnullである場合,pre/postを割り当てる.
			//---------------------------------------------------
			inputList=hsdls.innerDependency(inputList,loopNameList);

			
			//---------------------------------------------------
			//入力情報に対するnull処理
			//ここで完全に独立しているノードは消えることになるのでリストに登録する.
			//---------------------------------------------------
		
			ArrayList<Integer> separateNodeList = new ArrayList<Integer>();
			separateNodeList=hsdls.make_separateNodeList(inputList,m_indexList);
			
			inputList=hsdls.remove_NullRelation(inputList);

			if(inputList.size()==0){
				//全て独立ノードである場合,関係を作成
				RelationPattern initialize = new RelationPattern(0,1,"post");
				inputList.add(initialize);
				initialize = new RelationPattern(1,0,"pre");
				inputList.add(initialize);
				separateNodeList.remove(0);
				separateNodeList.remove(1);
				//ループ数が1以下の場合の例外処理が必要か.
			}
			
			ArrayList<RelationPattern> fixed_inputList = new ArrayList<RelationPattern>();
			ArrayList<RelationPattern> inputList_new;
			
			for(int i=0;i<loop_nameListPair.size();i++){
				//見つかるまで削除手順を変えて繰り返す.
				inputList_new= new ArrayList<RelationPattern>();
				
				//---------------------------------------------------
				//継承関係の削除
				//---------------------------------------------------
				inputList_new = hsdls.remove_inhPath(inputList, loop_nameListPair.get(i));
				
				//---------------------------------------------------
				//双方向パスの処理
				//---------------------------------------------------
				
				ArrayList<Integer> pre_num = new ArrayList<Integer>();
				for(int j=0;j<inputList_new.size();j++){
					if(inputList_new.get(j).Attribute_name.equals("pre")) pre_num.add(j);
				}
				
				make_prepostList(0, new Integer[pre_num.size()]);

				fixed_inputList = fix_interactivePath(inputList_new,m_indexList.size());
				
				if(fixed_inputList!=null && hsdls.check_inh(fixed_inputList,inputList)){
					//継承判定をして矛盾しなければbreak
//					System.out.println(i+"------washio4--" + fixed_inputList.size() );
//					for(int x=0;x<fixed_inputList.size();x++){
//						fixed_inputList.get(x).printContents();
//					}
					break;
				}
				else{
					prepostList.clear();
				}
				
			}	
			//---------------------------------------------------
			//null置換およびループ構造の決定
			//---------------------------------------------------
			
			loopStructure = get_LoopStructure(fixed_inputList,separateNodeList);
//			hsdls.toRecMLheader(loopStructure);
			
			//---------------------------------------------------
			//ループ構造の決定
			//---------------------------------------------------

			m_LoopStructure = loopStructure;
			
			/*Hight Speed Ver*/
			
			//---------------------------------------------------
			//生成に必要のない属性情報を削る
			//---------------------------------------------------
			removeAttr(m_LoopStructure, m_AttrLists);
			System.out.println("------------- debug Ouptup removeAttr -------------");
			outputLabelSample(m_AttrLists);

			//---------------------------------------------------
			//conditionに対して属性情報を割り振る
			//---------------------------------------------------
			addConditionAttr(m_AttrLists);
			
//			outputLabelSample(m_AttrLists);
		}

	}

	
	//========================================================
	//labelAllocation
	// 属性情報決定メソッド
	//========================================================
	public void labelAllocation(){

		/*Node Listのコピーを作成*/
		HashMap<Integer, HashMap<String, Integer>> copy_HashMapNodeList = new HashMap<Integer, HashMap<String, Integer>>();
		Object cloneNode = m_HashMapNodeList.clone();
		copy_HashMapNodeList = (HashMap<Integer, HashMap<String, Integer>>) cloneNode;

		/*Edge Listのコピーを作成*/
		HashMap<Integer, HashMap<String, Integer>> copy_HashMapEdgeList = new HashMap<Integer, HashMap<String, Integer>>();
		Object cloneEdge = m_HashMapEdgeList.clone();
		copy_HashMapEdgeList = (HashMap<Integer, HashMap<String, Integer>>) cloneEdge;

		/*Start GoalのNodeとEdgeを作成*/
		copy_HashMapNodeList = addStartGoalNode(copy_HashMapNodeList);
		copy_HashMapEdgeList = addEdge(m_HashMapNodeList,copy_HashMapEdgeList);
		
		/*NodeとEdgeから属性情報を決定*/
		m_AttrLists = labelAttr(copy_HashMapNodeList,copy_HashMapEdgeList);
	
	}
	
	//========================================================
	//addStartGoalNode
	// 渡されたNodeにStartNodeとGoalNodeを追加
	//========================================================
	public HashMap<Integer, HashMap<String, Integer>> addStartGoalNode(HashMap<Integer, HashMap<String, Integer>> NodeList) {
		
		HashMap<String, Integer> startNode = new HashMap<String, Integer>();
		HashMap<String, Integer> goalNode = new HashMap<String, Integer>();

		int setNum = NodeList.size();

		/*STARTを追加する*/
		startNode.put("equation", -1);
		startNode.put("variable", -1);
		NodeList.put(setNum, startNode);
		
		setNum = NodeList.size();

		/*GOALを追加する*/
		goalNode.put("equation", -2);
		goalNode.put("variable", -2);
		NodeList.put(setNum, goalNode);
		
		return NodeList;

	}
	
	//========================================================
	//addEdge
	// 追加したStartNodeとGoalNodeに対してエッジを作成する
	// StartNodeはEdgeが入ってこない全てのNodeに向かってEdgeを作成する
	// GoalNodeはEdgeが出ていかない全てのNodeに向かってEdgeを作成する
	//========================================================
	public HashMap<Integer, HashMap<String, Integer>> addEdge(
			HashMap<Integer, HashMap<String, Integer>> originNodeList, 
			HashMap<Integer, HashMap<String, Integer>> EdgeList) {
		

		ArrayList<Integer> NodeId = new ArrayList<Integer>();
		ArrayList<Integer> copy_NodeId = new ArrayList<Integer>();
		
		/*Startに対する処理*/
		
		/*NodeID　Listを作成する*/
		int i = 0;
		for(; i<originNodeList.size();i++){
			NodeId.add(i);
		}
		copy_NodeId.addAll(NodeId);	

		/*NodeのdestにあるIDをNodeID　Listから削除する*/
		i = 0;
		Integer deleteId;
		for(; i<EdgeList.size();i++){
			deleteId = EdgeList.get(i).get("dest");
			copy_NodeId.remove(deleteId);
		}
		
		/*NodeID　Listに残ったIDに対してStartからEdgeを作成する*/
		i = 0;
		for(;i<copy_NodeId.size();i++){
			HashMap<String, Integer> addStratEdge = new HashMap<String, Integer>();
			addStratEdge.put("source", originNodeList.size());
			addStratEdge.put("dest", copy_NodeId.get(i));
			EdgeList.put(EdgeList.size(), addStratEdge);
		}
		
		copy_NodeId.clear();
		
		/*GOALに対する処理*/
		/*NodeID　Listを作成する*/
		copy_NodeId.addAll(NodeId);
		i = 0;
		
		/*NodeのsourceにあるIDをNodeID　Listから削除する*/
		for(; i<EdgeList.size();i++){
			deleteId = EdgeList.get(i).get("source");
			copy_NodeId.remove(deleteId);
		}
		
		/*NodeID　Listに残ったIDに対してGoalからEdgeを作成する*/
		i = 0;
		for(;i<copy_NodeId.size();i++){
			HashMap<String, Integer> addGoalEdge = new HashMap<String, Integer>();
			addGoalEdge.put("source", copy_NodeId.get(i));
			addGoalEdge.put("dest", originNodeList.size()+1);
			EdgeList.put(EdgeList.size(), addGoalEdge);
		}
						
		return EdgeList;

	}

	//========================================================
	//labelAttr
	// NodeとEdgeから属性情報を決定
	//========================================================
	public HashMap<Integer, HashMap<Integer, String>> labelAttr
	(HashMap<Integer, HashMap<String, Integer>> NodeList, 
			HashMap<Integer, HashMap<String, Integer>> EdgeList){
			
		HashMap<Integer, HashMap<Integer, String>> HashMapMargeAttrList = new HashMap<Integer, HashMap<Integer, String>>();
		HashMap<Integer, HashMap<Integer, String>> HashMapDownAttrList = new HashMap<Integer, HashMap<Integer, String>>();
		HashMap<Integer, HashMap<Integer, String>> HashMapUpAttrList = new HashMap<Integer, HashMap<Integer, String>>();
		
		/*依存関係グラフに対してStartからGoalに向かって属性情報を決定していく*/
		HashMapDownAttrList = downwardLabel(NodeList,EdgeList);
		System.out.println("------------- debug Ouptup downwardLabel -------------");
		outputLabelSample(HashMapDownAttrList);

		/*依存関係グラフに対してGoalからStartに向かって属性情報を決定していく*/
		HashMapUpAttrList = upLabel(NodeList, EdgeList);
		System.out.println("------------- debug Ouptup upLabel -------------");
		outputLabelSample(HashMapUpAttrList);
		
		/*2種類の探索結果の統合を行う*/
		HashMapMargeAttrList = mergeDownWithUp(HashMapDownAttrList, HashMapUpAttrList);
		System.out.println("------------- debug Ouptup mergeDownWithUp -------------");
		outputLabelSample(HashMapMargeAttrList);
		
		/*StartとGoalに対する属性情報を削除する*/
		HashMapMargeAttrList.remove(-1);
		HashMapMargeAttrList.remove(-2);
		
		return HashMapMargeAttrList;
		
	}
	
	//========================================================
	//mergeDownWithUp
	// 2種類(StartからGoal,GoalからStart)の探索結果の統合を行う
	//========================================================
	public HashMap<Integer, HashMap<Integer, String>> mergeDownWithUp(
			HashMap<Integer, HashMap<Integer, String>> hashMapDownAttrList, 
			HashMap<Integer, HashMap<Integer, String>> hashMapUpAttrList) {

		HashMap<Integer, HashMap<Integer, String>> list = new HashMap<Integer, HashMap<Integer, String>>();
		int loopSize = 0;
		loopSize = m_indexList.size();
		try {
		for(int i = 0;i<m_VecEquOder.size();i++){
			/*数式番号を取得する*/
			int checkEquNum = m_VecEquOder.get(i);
			HashMap<Integer, String> attr = new HashMap<Integer, String>();
			for(int j = 0; j<loopSize; j++){
				/*属性情報をそれぞれ取得*/
				String downAttr = hashMapDownAttrList.get(checkEquNum).get(j);
				String upAttr = hashMapUpAttrList.get(checkEquNum).get(j);
				/*属性情報の判定*/
				if(downAttr.equals(upAttr)){
					attr.put(j, downAttr);
				}else{
					if(!downAttr.equals("null") && upAttr.equals("null")){
						attr.put(j, downAttr);
					}else if(!upAttr.equals("null") && downAttr.equals("null")){
						attr.put(j, upAttr);
					}else{
						 throw new IOException();
					}
				}
			}
			list.put(checkEquNum, attr);
		}	
		} catch (Exception e) {
			 System.out.println("Attribute conflict");
		}
		return list;
	}

	//========================================================
	//downwardLabel
	// 依存関係グラフに対してStartからGoalに向かって属性情報を決定していく
	//========================================================
	public HashMap<Integer, HashMap<Integer, String>> downwardLabel
	(HashMap<Integer, HashMap<String, Integer>> NodeList, 
			HashMap<Integer, HashMap<String, Integer>> EdgeList){
		HashMap<Integer, HashMap<Integer, String>> HashMapDownAttrLists = new HashMap<Integer, HashMap<Integer, String>>();
		
		/*スタートとなる属性情報配列を作成する
		 * 計算順序を取得する
		 * 計算順序に沿って探索開始 
		 **/
		
		HashMap<Integer, String> attrList = new HashMap<Integer, String>();
		
		int loopSize = 0;
		loopSize =  m_indexList.size();
		for(int i=0 ; i < loopSize;i++){
			attrList.put(i, "null");
		}
//		attrList.put(0, "null");
//		attrList.put(1, "null");
//		attrList.put(2, "null");
		
		/*スタートNodeの属性情報を登録する*/
		HashMapDownAttrLists.put(-1, attrList);
		
		/*equation oder順に属性情報リストを決定していく*/		
		for(int i = 0;i<this.m_VecEquOder.size();i++){
			/*数式番号を取得する*/
			int checkEquNum = m_VecEquOder.get(i);
//				/*debug*/
//				System.out.println("EquNum: " + checkEquNum);
			
			/*数式番号が登録されているNodeIdを取得する*/
			int checkNodeId;
			checkNodeId =  getNodeId(checkEquNum, NodeList);
			
			/*destにNodeIdを持つsourceのリストを取得する*/
			ArrayList<Integer> checkSorceIdList;
			checkSorceIdList =  getSourceId(checkNodeId, EdgeList);
//			/*debug*/
//			System.out.println("checkSorceIdList_size:"+checkSorceIdList.size());
			
			/*checkSorceIdListの0番目のものを決定する属性情報としてまず選択する*/
			HashMap<Integer, String> cheakAttrList = new HashMap<Integer, String>();
			int sourceNodeEquNum_0 = NodeList.get(checkSorceIdList.get(0)).get("equation");
			cheakAttrList = HashMapDownAttrLists.get(sourceNodeEquNum_0);
			
			/*checkSorceIdListのk番目のものを決定する属性情報とと比較する*/
			for(int k=0;k<checkSorceIdList.size();k++){
				int sourceNodeEquNum_k = NodeList.get(checkSorceIdList.get(k)).get("equation");
//				/*debug*/
//				System.out.println("sourceNodeEquNum_k:" + sourceNodeEquNum_k );
				HashMap<Integer, String> sourceAttrList = HashMapDownAttrLists.get(sourceNodeEquNum_k);
				cheakAttrList = mergeDownAttrList(cheakAttrList,sourceAttrList);	
			}	
			/*finalの処理*/			
			if(m_finalAttrEquNumLists.containsKey(checkEquNum)){
				int intSetLoopNum_final;
				intSetLoopNum_final = m_finalAttrEquNumLists.get(checkEquNum);
				cheakAttrList.put(intSetLoopNum_final, "final");
			}
			/*initに変更する処理を行う*/
			/*checkNodeIdのdestのidを取得する*/
			/*destにNodeIdを持つsourceのNodeIDを取得する*/
			if(m_initendAttrEquNumLists.containsKey(checkEquNum)){
				int intSetLoopNum_initend;
				intSetLoopNum_initend =m_initendAttrEquNumLists.get(checkEquNum);
				cheakAttrList.put(intSetLoopNum_initend, "initend");
				int SourceId = 0;
				for(int l = 0;l<EdgeList.size();l++){
					HashMap<String, Integer> checkNode = EdgeList.get(l);
					if(checkNode.get("dest") == checkNodeId){
						SourceId = (checkNode.get("source"));
						break;
					}
				}
				int replaceEquNum = NodeList.get(SourceId).get("equation");
//				/*debug*/
//				System.out.println("replaceEquNum" + replaceEquNum);
				HashMap<Integer, String> replaceAttrList = new HashMap<Integer, String>();
				replaceAttrList = HashMapDownAttrLists.get(replaceEquNum);
				replaceAttrList.put(intSetLoopNum_initend, "init");
				HashMapDownAttrLists.put(replaceEquNum,replaceAttrList);
			}
			HashMapDownAttrLists.put(checkEquNum,cheakAttrList);
		}
		
		return HashMapDownAttrLists;
	}
	
	//========================================================
	//upLabel
	// 依存関係グラフに対してGoalからStartに向かって属性情報を決定していく
	//========================================================
	public HashMap<Integer, HashMap<Integer, String>> upLabel
	(HashMap<Integer, HashMap<String, Integer>> NodeList, 
			HashMap<Integer, HashMap<String, Integer>> EdgeList){
		HashMap<Integer, HashMap<Integer, String>> HashMapUpAttrList = new HashMap<Integer, HashMap<Integer, String>>();
		
		/*ゴールとなる属性情報配列を作成する
		 * 計算順序を取得する
		 * 計算順序の逆順に沿って探索開始 
		 **/
		
		HashMap<Integer, String> attrList = new HashMap<Integer, String>();
		
		int loopSize = 0;
		loopSize = m_indexList.size();
		for(int i=0 ; i < loopSize;i++){
			attrList.put(i, "null");
		}
//		attrList.put(0, "null");
//		attrList.put(1, "null");
//		attrList.put(2, "null");
		
		/*ゴールNodeのアトリビュートリストを登録する*/
		HashMapUpAttrList.put(-2, attrList);
			
		/*equation oder順にアｔロリビューとリストを決定していく*/		
		for(int i = m_VecEquOder.size()-1; -1<i ;i--){
			/*数式番号を取得する*/
			int checkEquNum = m_VecEquOder.get(i);
//				/*debug*/
//				System.out.println("EquNum: " + checkEquNum);
			
			/*数式番号が登録されているNodeIdを取得する*/
			int checkNodeId;
			checkNodeId =  getNodeId(checkEquNum, NodeList);
			
			/*sourceにNodeIdを持つdestのリストを取得する*/
			ArrayList<Integer> checkDestIdList;
			checkDestIdList =  getDestId(checkNodeId, EdgeList);
			HashMap<Integer, String> cheakAttrList = new HashMap<Integer, String>();
			int destNodeEquNum_0 = NodeList.get(checkDestIdList.get(0)).get("equation");
			cheakAttrList = HashMapUpAttrList.get(destNodeEquNum_0);
			for(int k=0;k<checkDestIdList.size();k++){	
				int destNodeEquNum_k = NodeList.get(checkDestIdList.get(k)).get("equation");
				HashMap<Integer, String> destAttrList = HashMapUpAttrList.get(destNodeEquNum_k);
				cheakAttrList = mergeUpAttrList(cheakAttrList,destAttrList);			
			}
				/*finalの処理*/	
				int intSetLoopNum;			
				if(m_finalAttrEquNumLists.containsKey(checkEquNum)){
					intSetLoopNum = m_finalAttrEquNumLists.get(checkEquNum);
					cheakAttrList.put(intSetLoopNum, "final");
				}
//					/*initに変更する処理を行う*/
//					/*checkNodeIdのdestのidを取得する*/
//					/*destにNodeIdを持つsourceのNodeIDを取得する*/
				if(m_initendAttrEquNumLists.containsKey(checkEquNum)){
					intSetLoopNum =m_initendAttrEquNumLists.get(checkEquNum);
					cheakAttrList.put(intSetLoopNum, "initend");
				}
			HashMapUpAttrList.put(checkEquNum,cheakAttrList);
		}
		
		
		return HashMapUpAttrList;
	}

	//========================================================
	//　getSourceId
	// nodeに対してある数式番号を持つnodeのIDを取得する
	//========================================================
	public int  getNodeId(int EquNum, HashMap<Integer, HashMap<String, Integer>> NodeList){
		int NodeId = 0;
		
		for(int i = 0;i<NodeList.size();i++){
			HashMap<String, Integer> checkNode = NodeList.get(i);
			if(checkNode.get("equation") == EquNum){
				NodeId = i;
			}
		}
		
		return NodeId;
	}

	//========================================================
	//　getSourceId
	// Edgeに対してあるdestを持つsourceのID listを取得する
	//========================================================
	public ArrayList<Integer>  getSourceId(int NodeNum, HashMap<Integer, HashMap<String, Integer>> EdgeList){
		ArrayList<Integer> SourceId = new ArrayList<Integer>();
		
		for(int i = 0;i<EdgeList.size();i++){
			HashMap<String, Integer> checkNode = EdgeList.get(i);
			if(checkNode.get("dest") == NodeNum){
				SourceId.add(checkNode.get("source"));
			}
		}
		
		return SourceId;
	}
	
	//========================================================
	//　getSourceId
	// Edgeに対してあるsourceを持つdestのID listを取得する
	//========================================================
	public ArrayList<Integer> getDestId(int NodeNum, HashMap<Integer, HashMap<String, Integer>> EdgeList){
		ArrayList<Integer> destId = new ArrayList<Integer>();
		
		for(int i = 0;i<EdgeList.size();i++){
			HashMap<String, Integer> checkNode = EdgeList.get(i);
			if(checkNode.get("source") == NodeNum){
				destId.add(checkNode.get("dest"));
			}
		}
		
		return destId;
	}
	
	//========================================================
	//　mergeDownAttrList
	// 下向き探索における2つからなる属性情報の統合を行う
	//========================================================
	public HashMap<Integer, String> mergeDownAttrList(HashMap<Integer, String> destAttrList, HashMap<Integer, String> sourceAttrList){
		HashMap<Integer, String> returnAttrList = new HashMap<Integer, String>();
		
		for(int i = 0;i<destAttrList.size();i++){
			returnAttrList.put(i,selectAttrDownward(destAttrList.get(i),sourceAttrList.get(i)));
		}
		
		return returnAttrList;
	}
	
	//========================================================
	//　selectAttrDownward
	// mergeDownAttrListで使われる属性情報の判断方法
	//========================================================
	public String  selectAttrDownward(String destAttr, String sourceAttr){
		if(destAttr.equals("post")){
			return "post";
		}else if(destAttr.equals("final")){
			if((sourceAttr.equals("final")) || (sourceAttr.equals("post"))) return "post";
			else return "final";
		}else if(destAttr.equals("inner")){
			if((sourceAttr.equals("final")) || (sourceAttr.equals("post"))) return "post";
			else return "inner";
		}else if(destAttr.equals("initend")){
			if((sourceAttr.equals("final")) || (sourceAttr.equals("post"))) return "post";
			else if(sourceAttr.equals("inner") || sourceAttr.equals("initend")) return "inner";
			else return "initend";
		}else if(destAttr.equals("init")){
			if((sourceAttr.equals("final")) || (sourceAttr.equals("post"))) return "post";
			else if((sourceAttr.equals("inner")) || (sourceAttr.equals("initend"))) return "inner";
			else return "init";
		}else if(destAttr.equals("pre")){
			if((sourceAttr.equals("final")) || (sourceAttr.equals("post"))) return "post";
			else if((sourceAttr.equals("inner")) || (sourceAttr.equals("initend"))) return "inner";
			else if(sourceAttr.equals("init")) return "init";
			else return "pre";
		}else{
			if(sourceAttr.equals("post")) return "post";
			else if(sourceAttr.equals("final")) return "post";
			else if(sourceAttr.equals("inner")) return "inner";
			else if(sourceAttr.equals("initend")) return "inner";
			else if(sourceAttr.equals("init")) return "initend";
			else if(sourceAttr.equals("pre")) return "pre";
			return sourceAttr;
		}
	}

	//========================================================
	//　mergeUpAttrList
	// 上向き探索における2つからなる属性情報の統合を行う
	//========================================================
	public HashMap<Integer, String>  mergeUpAttrList(HashMap<Integer, String> sourceAttrList, HashMap<Integer, String> destAttrList){
		HashMap<Integer, String> returnAttrList = new HashMap<Integer, String>();
		
		for(int i = 0;i<sourceAttrList.size();i++){
			returnAttrList.put(i, 
					selectAttrUpward(sourceAttrList.get(i),destAttrList.get(i)));
		}
		
		return returnAttrList;
	}

	//========================================================
	//　selectAttrUpward
	// mergeUpAttrListで使われる属性情報の判断方法
	//========================================================
	public String selectAttrUpward(String sourceAttr, String destAttr){
		if(sourceAttr.equals("pre")){
			return sourceAttr;
		}else if(sourceAttr.equals("init")){
			if((destAttr.equals("pre")) || (destAttr.equals("init"))) return "pre";
			else return sourceAttr;
		}else if(sourceAttr.equals("initend")){
			if((destAttr.equals("pre")) || (destAttr.equals("init"))) return "pre";
			else if (destAttr.equals("initend")) return "init";
			else return sourceAttr;
		}else if(sourceAttr.equals("inner")){
			if((destAttr.equals("pre")) || (destAttr.equals("init"))) return "pre";
			else if (destAttr.equals("initend")) return "init";
			else return sourceAttr;
		}else if(sourceAttr.equals("final")){
			if((destAttr.equals("pre")) || (destAttr.equals("init"))) return "pre";
			else if (destAttr.equals("initend")) return "init";
			else if (destAttr.equals("inner") || destAttr.equals("final")) return "inner";
			else return sourceAttr;
		}else if(sourceAttr.equals("post")){
			if((destAttr.equals("pre")) || (destAttr.equals("init"))) return "pre";
			else if (destAttr.equals("initend")) return "init";
			else if (destAttr.equals("inner") || destAttr.equals("final")) return "inner";
			else return sourceAttr;
		}else{
			if(destAttr.equals("post")) return "post";
			else if(destAttr.equals("final")) return "inner";
			else if(destAttr.equals("inner")) return "inner";
			else if(destAttr.equals("initend")) return "init";
			else if(destAttr.equals("init")) return "pre";
			else if(destAttr.equals("pre")) return "pre";
			return destAttr;
		}
	}
	
	//========================================================
	//　outputLabelSample
	// 属性情報List出力メソッド
	//========================================================
	public void outputLabelSample(HashMap<Integer, HashMap<Integer, String>> AttrLists){
		for(Integer i:AttrLists.keySet()){
			HashMap<Integer, String> hm = AttrLists.get(i);
			System.out.print("equNum"+ i);
			for(Integer j:hm.keySet()){
				System.out.print("-" + AttrLists.get(i).get(j) + "-");
			}
			System.out.println();
		}
	}
	
	//========================================================
	//　removeAttr
	// 生成に必要のない属性情報を削る
	//========================================================
	public HashMap<Integer, HashMap<Integer, String>> removeAttr(
			ArrayList<RelationPattern> LoopStructure, 
			HashMap<Integer, HashMap<Integer, String>> AttrLists){

		/*決定できなかったnullの置き換え*/
		for(int i=0;i<m_LoopStructure.size();i++){
			int parentID = m_LoopStructure.get(i).Parent_name;
			int childID = m_LoopStructure.get(i).Child_name;
			String attr = m_LoopStructure.get(i).Attribute_name;
			
			kawabataTest1212_1(parentID, childID, attr);
		}
		
		/*余分な箇所を消す処理*/
		for(int i=0;i<m_LoopStructure.size();i++){
			int parentID = m_LoopStructure.get(i).Parent_name;
			int childID = m_LoopStructure.get(i).Child_name;
			String attr = m_LoopStructure.get(i).Attribute_name;
			
			kawabataTest1212_4(parentID, childID, attr);
		}	
//		
//		
//		/*全ての配列変数間で成り立つループ構造関係を取得*/
//		ArrayList<HashMap<Integer,String>> loopNumOuter = new ArrayList<HashMap<Integer,String>>();
//		for(int i=0;i<LoopStructure.size();i++){
//			HashMap<Integer,String> loopNumInner = new HashMap<Integer,String>();
//			loopNumInner = getRemoveLoopList(LoopStructure, i, LoopStructure.get(i).Attribute_name);
//			loopNumOuter.add(loopNumInner);
//		}
//		
//		for(HashMap<Integer,String> hm:loopNumOuter){
//			for(Integer i:hm.keySet()){
//				System.out.println("------------------------removeAttr--debug:" + i + "----" + hm.get(i));
//			}
//		}
//		
//		/**/
//		for(int i=0; i<loopNumOuter.size();i++){
//			HashMap<Integer,String> check = loopNumOuter.get(i);
//			int parentLoopNum = LoopStructure.get(i).Parent_name;
//			
//			/*全ての配列変数に対して
//			 * その継承関係がある配列変数に処理を行う*/
//			for(int j=0;j<m_indexList.size();j++){
//				if(check.containsKey(j)){
//					String Attribute = check.get(j);
//					Vector<Integer> hitEquVec = new Vector<Integer>();
//					int childLoopNum = j;
//
//					for(int k= 0;k<m_VecEquOder.size();k++){
//						/*属性情報リストが,ある数式番号で探索している配列変数に対して属性情報を持っているならば*/
//						if(AttrLists.get(m_VecEquOder.get(k)).containsKey(parentLoopNum)){
//							/*継承関係の根に相当する配列の属性情報*/
//							String checkStr = AttrLists.get(m_VecEquOder.get(k)).get(parentLoopNum);
//							/*継承関係の子に相当する配列の属性情報*/
//							String checkStrChild = AttrLists.get(m_VecEquOder.get(k)).get(childLoopNum);
//							if(checkStr.equals("null") && !(checkStrChild.equals("null"))){
//								if(Attribute.equals("pre")){
//									AttrLists.get(m_VecEquOder.get(k)).put(parentLoopNum,"pre");								
//								}else if(Attribute.equals("post")){
//									AttrLists.get(m_VecEquOder.get(k)).put(parentLoopNum,"post");	
//								}
//							}
//							if(checkStrChild.equals("null") && !(checkStr.equals("null"))){
//								/*　問題の箇所　*/
//								AttrLists.get(m_VecEquOder.get(k)).put(childLoopNum,Attribute);
////								AttrLists.get(m_VecEquOder.get(k)).remove(childLoopNum);
//
//							}
//						}
//					}
//					
//				}
//			}
//			
//		}
//		
//		/**/
//		for(int i=0; i<loopNumOuter.size();i++){
//			HashMap<Integer,String> check = loopNumOuter.get(i);
//			int parentLoopNum = LoopStructure.get(i).Parent_name;
//			
//			/*全ての配列変数に対して
//			 * その継承関係がある配列変数に処理を行う*/
//			for(int j=0;j<m_indexList.size();j++){
//				if(check.containsKey(j)){
//					String Attribute = check.get(j);
//					Vector<Integer> hitEquVec = new Vector<Integer>();
//					int childLoopNum = j;		
//					
//					for(int k= 0;k<m_VecEquOder.size();k++){
//						/*属性情報リストがある数式番号で探索している配列変数に対して属性情報を持っているならば*/
//						if(AttrLists.get(m_VecEquOder.get(k)).containsKey(parentLoopNum)){
//							/*継承関係の根に相当する配列の属性情報*/
//							String checkStr = AttrLists.get(m_VecEquOder.get(k)).get(parentLoopNum);
//							/*継承関係の子に相当する配列の属性情報*/
//							String checkStrChild = AttrLists.get(m_VecEquOder.get(k)).get(childLoopNum);
//							/*余分な属性情報の判定*/
//							checkStr = AttrLists.get(m_VecEquOder.get(k)).get(parentLoopNum);
//							checkStrChild = AttrLists.get(m_VecEquOder.get(k)).get(childLoopNum);
//							if(!checkStr.equals(Attribute)){
//								hitEquVec.add(m_VecEquOder.get(k));
//								if((checkStr.equals("null")) && (checkStrChild.equals("null"))){
//									hitEquVec.remove(m_VecEquOder.get(k));
//								}
//							}
//						}
//					}
//					
//					int removeLoopNum = j;
//					for(int k=0;k<hitEquVec.size();k++){
//						AttrLists.get(hitEquVec.get(k)).remove(removeLoopNum);
//					}
//				}
//			}
//		}
		
		
		return AttrLists;
	}
	

	private void kawabataTest1212_1(int PARENT_ID, int childID, String ATTR) {
		// TODO Auto-generated method stub
//		kawabataTest1212_2(PARENT_ID,childID, ATTR);
//		kawabataTest1212_3(PARENT_ID,childID, ATTR);
		
		kawabataTest1212_2(PARENT_ID, childID, ATTR);
		System.out.println("-----------------debug----------- PARENT_ID:"+ PARENT_ID + " -- childID: " + childID +" -- ATTR:"+ATTR);

		for(int i=0;i<m_LoopStructure.size();i++){
			int pa = m_LoopStructure.get(i).Parent_name;
			int ch = m_LoopStructure.get(i).Child_name;
			if(childID == pa){
				/*二重処理が生じている*/
//				kawabataTest1212_2(PARENT_ID, childID, ATTR);
//				System.out.println("-----------------debug----------- PARENT_ID:"+ PARENT_ID + " -- childID: " + childID +" -- ATTR:"+ATTR);
				kawabataTest1212_1(PARENT_ID, ch, ATTR);
			}
		}

		
	}

	private void kawabataTest1212_2(int parentID, int childID, String attr) {
		// TODO Auto-generated method stub
		Boolean b_pre = new Boolean(true);
		Boolean b_post = new Boolean(true);

		for(int i=0;i<m_VecEquOder.size();i++){
			int eqNum = m_VecEquOder.get(i);
			HashMap<Integer, String> hm = m_AttrLists.get(eqNum);
			String pareAttr = hm.get(parentID);
			String chilAttr = hm.get(childID);

			if(pareAttr.endsWith("null")){
				m_AttrLists.get(eqNum).put(parentID, attr);
			}
			
			if(chilAttr.equals("init")){
				b_pre = false;
			}
			if(chilAttr.equals("final")){
				b_post = false;
			}

			if(chilAttr.equals("null")){
				if(b_pre && b_post){
					m_AttrLists.get(eqNum).put(childID, "pre");
				}
				if(b_pre.equals(false) && b_post){
					m_AttrLists.get(eqNum).put(childID, "inner");
				}
				if(b_pre.equals(false) && b_post.equals(false)){
					m_AttrLists.get(eqNum).put(childID, "post");
				}
			}
			
		}
	}

	private void kawabataTest1212_4(int PARENT_ID, int childID, String ATTR) {
		// TODO Auto-generated method stub
		kawabataTest1212_5(PARENT_ID, childID, ATTR);
		System.out.println("-----------------debug----------- PARENT_ID:"+ PARENT_ID + " -- childID: " + childID +" -- ATTR:"+ATTR);

		for(int i=0;i<m_LoopStructure.size();i++){
			int pa = m_LoopStructure.get(i).Parent_name;
			int ch = m_LoopStructure.get(i).Child_name;
			if(childID == pa){
				/*二重処理が生じている*/
				kawabataTest1212_4(PARENT_ID, ch, ATTR);
			}
		}		
	}

	private void kawabataTest1212_5(int parentID, int childID, String attr) {
		// TODO Auto-generated method stub
		for(int i=0;i<m_VecEquOder.size();i++){
			int eqNum = m_VecEquOder.get(i);
			HashMap<Integer, String> hm = m_AttrLists.get(eqNum);
			if(hm.containsKey(parentID)&&hm.containsKey(childID)){
				String pareAttr = hm.get(parentID);
				if(!pareAttr.equals(attr)){
					m_AttrLists.get(eqNum).remove(childID);
				}				
			}
			
		}	
	}
	
	private void kawabataTest1212_3(int PARENT_ID, int childID, String ATTR) {
		// TODO Auto-generated method stub
		kawabataTest1212_2(PARENT_ID, childID, ATTR);
		System.out.println("-----------------debug----------- PARENT_ID:"+ PARENT_ID + " -- childID: " + childID +" -- ATTR:"+ATTR);

		for(int i=0;i<m_LoopStructure.size();i++){
			int pa = m_LoopStructure.get(i).Parent_name;
			int ch = m_LoopStructure.get(i).Child_name;
			if(childID == pa){
				kawabataTest1212_2(PARENT_ID, childID, ATTR);
				System.out.println("-----------------debug----------- PARENT_ID:"+ PARENT_ID + " -- childID: " + childID +" -- ATTR:"+ATTR);
				kawabataTest1212_3(PARENT_ID, ch, ATTR);
			}
		}
	}

	//========================================================
	//　getRemoveLoopList
	// 親にあるloopnum　を持つ　子と属性情報のリストを取得する
	//========================================================
	public HashMap<Integer,String> getRemoveLoopList(ArrayList<RelationPattern> LoopStructure, int rootNum, String str){
		HashMap<Integer,String> loopNum = new HashMap<Integer,String>();
		for(int i=0;i<LoopStructure.size();i++){
			if(rootNum == LoopStructure.get(i).Parent_name){
				loopNum.put(LoopStructure.get(i).Child_name,LoopStructure.get(i).Attribute_name);
				loopNum.putAll(getRemoveLoopList(LoopStructure, LoopStructure.get(i).Child_name, str));
			}
		}
		return loopNum;
	}

	//========================================================
	//　addConditionAttr
	// conditionに対して属性情報を割り振る
	//========================================================
	public void addConditionAttr(HashMap<Integer, HashMap<Integer, String>> AttrLists){
		int listSize =  m_condrefAttrEquNumLists.size();
		int i = 0;
		while(listSize>0){
			if(m_condrefAttrEquNumLists.containsKey(i)){
				HashMap<Integer, String> originAttr = new HashMap<Integer, String>();
				HashMap<Integer, String> addAttr = new HashMap<Integer, String>();

				int finalEquNum = m_condrefAttrEquNumLists.get(i);
				originAttr = AttrLists.get(finalEquNum);
				addAttr = (HashMap<Integer, String>) originAttr.clone();
				
				int replaceLoopNum = m_finalAttrEquNumLists.get(finalEquNum);
				addAttr.put(replaceLoopNum, "loopcond");
				AttrLists.put(i, addAttr);
				listSize--;
			}
			i++;
		}
				
	}

	//========================================================
	//　toRecMLLoopstructure
	// StrucuturedRecML LoopStruct Out put Method
	//========================================================
	public void toRecMLLoopstructure(){
		if(m_indexList.size() > 0){
			DecisionLoopStructureVer2.toRecMLheader(m_LoopStructure);
		}
	}
	
	public void nullChange(){
		
		int i = 0;
		for(;i<m_AttrLists.size();i++){
			int j = 0;
			int flag = 0;
			for(;j<m_indexList.size();j++){
				String str = m_AttrLists.get(i).get(j);
				if(str.equals("null")){
					flag++;
				}
			}
			if(flag == m_indexList.size()){
				for(j=0;j<m_indexList.size();j++){
					m_AttrLists.get(i).put(j, "post");
				}
			}
		}
		if(m_indexList.size() > 0){
			DecisionLoopStructureVer2.toRecMLheader(m_LoopStructure);
		}
	}
	
	
	/*High Speed*/
	public void make_pairList(int n, int[] perm, int ListSize){
		//リストのサイズから2つ重複を許して選ぶ組み合わせをpermに格納
	    if(n == perm.length){
	    		ArrayList<Integer[]> Pair_Pattern = new ArrayList<Integer[]>();
	    		for(int i=0;i<2;i++){
	    			Pair_Pattern.add(loop_nameList.get(perm[i]));
	    		}
	    		loop_nameListPair.add(Pair_Pattern);
	    		
	    } else {
	      for(int i = 0; i < ListSize; i++){
	        perm[n] = i;
	        make_pairList(n + 1, perm,ListSize);
	      }
	    }
	  }
	


	public void set_nameList(int n, int[] perm, boolean[] flag, Integer[] loop_name){
	    if(n == perm.length){
	    	Integer[] loop_namePattern= new Integer[perm.length];
	    	for(int i=0;i<perm.length;i++)loop_namePattern[i]=loop_name[perm[i]];
	    	loop_nameList.add(loop_namePattern);
	    } else {
	      for(int i = 0; i < perm.length; i++){
	        if(flag[i]) continue;
	        perm[n] = i;
	        flag[i] = true;
	        set_nameList(n + 1, perm, flag,loop_name);
	        flag[i] = false;
	        }
	    }
	}
	
	public ArrayList<RelationPattern> fix_interactivePath(ArrayList<RelationPattern> inputList, int loop_num) {
		

		int find=0;
		ArrayList<RelationPattern> inputList_new = new ArrayList<RelationPattern>();
		ArrayList<Integer> pre_num = new ArrayList<Integer>();		
		
		for(int i=0;i<inputList.size();i++){
			if(inputList.get(i).Attribute_name.equals("pre")){
				pre_num.add(i);
			}
		}
		
		for(int x=0;x<prepostList.size();x++){
			
			//preでもpostでもない情報は処理対象外なのでコピー
			for(int i=0;i<inputList.size();i++){
				if(!inputList.get(i).Attribute_name.equals("pre")){
					if(!inputList.get(i).Attribute_name.equals("post")){
						inputList_new.add(inputList.get(i));
					}
				}
			}
			
			int Attrflag=0;
			int Childflag=0;
		
			
			for(int i=0;i<pre_num.size();i++){
				if(prepostList.get(x)[i]=="pre"){
					inputList_new.add(inputList.get(pre_num.get(i)));
				}
				if(prepostList.get(x)[i]=="post"){
					for(int j=0;j<inputList.size();j++){
						if(inputList.get(j).Parent_name.equals(inputList.get(pre_num.get(i)).Child_name)){
							if(inputList.get(j).Child_name.equals(inputList.get(pre_num.get(i)).Parent_name)){
								inputList_new.add(inputList.get(j));
							}
						}
					}
				}
			}
			
			//構造に対する判定（クリアしなければフラグを立てるようにする）
			
			ArrayList<Integer> p_nameList= new ArrayList<Integer>();
			ArrayList<String> a_nameList;
			ArrayList<Integer> c_nameList = new ArrayList<Integer>();
			
			for(int i=0;i<inputList_new.size();i++){
				int match =0;
				for(int j=0;j<p_nameList.size();j++){
					if(inputList_new.get(i).Parent_name.equals(p_nameList.get(j))) match=1;
				}
				if(match==0) p_nameList.add(inputList_new.get(i).Parent_name);
			}
			

			for(int i=0;i<p_nameList.size();i++){
				a_nameList = new ArrayList<String>();
				for(int j=0;j<inputList_new.size();j++){
					if(inputList_new.get(j).Parent_name.equals(p_nameList.get(i))){
						int match=0;
						for(int k=0;k<a_nameList.size();k++){
							if(inputList_new.get(j).Attribute_name.equals(a_nameList.get(k))){
								match=1;
							}
						}
						if(match==0)a_nameList.add(inputList_new.get(j).Attribute_name);
						else Attrflag=1;
					}
				}
			}
			
			for(int i=0;i<inputList_new.size();i++){
				int match =0;
				for(int j=0;j<c_nameList.size();j++){
					if(inputList_new.get(i).Child_name.equals(c_nameList.get(j))) match=1;
				}
				if(match==0) c_nameList.add(inputList_new.get(i).Child_name);
				else Childflag=1;
			}
			
			if(Attrflag==0 && Childflag==0){
				find=1;
				
				break;	
			}
			
			inputList_new.clear();
		}
		if(find==1)return inputList_new;
		else return null;
	}
	
	public void make_prepostList(int n, Integer[] perm){

		
		if(n == perm.length){
			String[] pp_pattern = new String[perm.length];
			for(int i=0;i<perm.length;i++){
				if(perm[i]==1) pp_pattern[i]="pre";
				if(perm[i]==2) pp_pattern[i]="post";
			}
			prepostList.add(pp_pattern);

		} else {
			for(int i = 1; i <= 2; i++){
				perm[n]=i;
				make_prepostList(n + 1, perm);
			}
		}
	}
	
	public void search_ConnectableNode(Integer root_name,Integer My_name, ArrayList<RelationPattern> inputList){
		
		//postに接続可能かどうかを判定する.
		//preでもいいのだが、どこに接続できるかを記録する手間を省くためpostに限定する.
		//postにある子要素を見る処理を繰り返し、接続できるノードを返す.
		
		Integer post_num=null;
		for(int j=0;j<inputList.size();j++){
			if(inputList.get(j).Parent_name.equals(My_name)&&inputList.get(j).Attribute_name.equals("post"))post_num=j;
		}
		
		if(post_num!=null){
			search_ConnectableNode(root_name,inputList.get(post_num).Child_name,inputList);
		} else{
			connectableNode.put(root_name, My_name);
			
		}
		
	}
	
	
	public ArrayList<RelationPattern> get_LoopStructure(ArrayList<RelationPattern> inputList,ArrayList<Integer> nullNodeList){

		ArrayList<RelationPattern> loopStructure= new ArrayList<RelationPattern>(inputList);

		
		//rootを探索
		ArrayList<Integer> root_nameList =new ArrayList<Integer>();
		for(int i=0;i<inputList.size();i++){
			int match=0;
			for(int j=0;j<root_nameList.size();j++){
				if(inputList.get(i).Parent_name.equals(root_nameList.get(j)))match=1;
			}
			if(match==0){
				//子要素に現れないかどうかを調べる.
				int flag=0;
				for(int j=0;j<inputList.size();j++){
					if(inputList.get(i).Parent_name.equals(inputList.get(j).Child_name)){
						flag=1;
					}
				}
				if(flag==0){
					root_nameList.add(inputList.get(i).Parent_name);
				}
			}
		}

		if(root_nameList.size()>0){
			//リストにある各root名について、接続可能箇所を探索
		
			for(int i=0;i<root_nameList.size();i++){
				search_ConnectableNode(root_nameList.get(i),root_nameList.get(i),inputList);
			}

			//rootが一つになるまで接続を繰り返す
			for(int i=1;i<root_nameList.size();i++){
				//i番目のルートをi-1番目のツリーにある接続可能箇所に接続する関係を追加する
				RelationPattern relation = new RelationPattern(connectableNode.get(root_nameList.get(i-1)),root_nameList.get(i),"post");
				loopStructure.add(relation);
			}
			RelationPattern relation;
			if(nullNodeList.size()!=0){
				//rootリストの最後のツリーの接続可能箇所に独立ノードを接続
				Integer last_root=connectableNode.get(root_nameList.get(root_nameList.size()-1));
				relation = new RelationPattern(last_root,nullNodeList.get(0),"post");
				loopStructure.add(relation);
				if(nullNodeList.size()>1){
					//独立ノード同士は必ず接続できる
					for(int i=1;i<nullNodeList.size();i++){
						relation = new RelationPattern(nullNodeList.get(i-1),nullNodeList.get(i),"post");
						loopStructure.add(relation);
					}
				}
			}
		}
		
		return loopStructure;
	}
	
	
	
	
	
	public ArrayList<RelationPattern> removeOverlap(ArrayList<RelationPattern> inputList) {
		
		//入力に重複があれば削除するメソッド
		ArrayList<RelationPattern> inputList_new = new  ArrayList<RelationPattern>();
		ArrayList<Integer> overlapNum = new ArrayList<Integer>();
		for(int i=0;i<inputList.size();i++){
			for(int j=i+1;j<inputList.size();j++){
				if(inputList.get(i).Parent_name.equals(inputList.get(j).Parent_name)){
					if(inputList.get(i).Child_name.equals(inputList.get(j).Child_name)){
						if(inputList.get(i).Attribute_name.equals(inputList.get(j).Attribute_name)){		
							overlapNum.add(j);
						}
					}
				}
			}
		}
		
		for(int i=0;i<inputList.size();i++){
			boolean flag = false;
			
			//重複チェック
			for(int j=0;j<overlapNum.size();j++){
				if(i==overlapNum.get(j)) flag=true;
			}
			
			if(flag!=true) inputList_new.add(inputList.get(i));
		}
		
		return inputList_new;
	}
	
	
	
	
	
//	public ArrayList<RelationPattern> remove_inhPath(ArrayList<RelationPattern> inputList, ArrayList<Integer[]> loop_namePair){
//		ArrayList<RelationPattern> inputList_new =  new ArrayList<RelationPattern>(inputList);
//		
//		//処理のアウトライン
//		//自身以外の全ノードへの間接パスと直接パスを探索。見つかったループ名は記録していく。
//		//While(目的ノードを発見するまで, または記録された名前が重複するまで)		
//		//if(間接パスが存在){
//		//　　直接パスを全て消す処理を行う。
//		//　　if(preを消す時) ペアのpostも消す。
//		//  else if(postを消す時)ペアのpreも消す。
//		//}
//		for(int i=0;i<loop_namePair.get(0).length;i++){
//			for(int j=0;j<loop_namePair.get(1).length;j++){
//				
//				int someSetflag=0;
//				if(loop_namePair.get(0)[i]!=loop_namePair.get(1)[j]){
//					//iからjへのpathを探索
//					ArrayList<Integer> nameList= new ArrayList<Integer>();
//					path_length= new ArrayList<Integer>();
//					nameList.add(loop_namePair.get(0)[i]);//出発名を経路リストに追加
//				
//					
//					search_Child(inputList_new,loop_namePair.get(0)[i],loop_namePair.get(1)[j],nameList);
//				
//					
//					if(path_length.size()!=1) someSetflag=1;
//					
//					int flag=0;
//					for(int x=0;x<path_length.size();x++){
//						if(path_length.get(x).equals(1)) flag=1;
//					}
//					
//					if(someSetflag==1 && flag==1){
//						
//						for(int y=0;y<inputList_new.size();y++){
//							if(inputList_new.get(y).Parent_name.equals(loop_namePair.get(0)[i]) && inputList_new.get(y).Child_name.equals(loop_namePair.get(1)[j])){
//								inputList_new.remove(y);
//							}
//						}
//						for(int y=0;y<inputList_new.size();y++){
//							if(inputList_new.get(y).Child_name.equals(loop_namePair.get(0)[i]) && inputList_new.get(y).Parent_name.equals(loop_namePair.get(1)[j])){
//								inputList_new.remove(y);
//							}
//						}
//					}					
//				}
//			}
//		}
//		
//		return inputList_new;
//	}
//	public void search_Child(ArrayList<RelationPattern> inputList,int i,int j,ArrayList<Integer> nameList){
//		
//		ArrayList<Integer> nameList_new;//追加は分岐させるたびに別のリストに入れる。
//		for(int x=0;x<inputList.size();x++){
//			if(inputList.get(x).Parent_name.equals(i)){//iが親になっていた場合
//				
//				if(inputList.get(x).Child_name.equals(j)){//子要素が目的のものであれば記録
//					path_length.add(nameList.size());
//					
//					
//				} else{//子要素が目的のものでなければ、その子要素を親に変更し再度探索（重複なしの条件で）
//					int flag=0;
//					for(int y=0;y<nameList.size();y++){
//						if(nameList.get(y).equals(inputList.get(x).Child_name)) flag=1;					
//					}
//					if(flag==0){
//						nameList_new= new ArrayList<Integer>(nameList);
//						nameList_new.add(i);
//						search_Child(inputList,inputList.get(x).Child_name,j,nameList_new);						
//						
//					}
//				}
//			}
//		}
//	}
//	
//	
//	
//	public ArrayList<RelationPattern> make_inputList(HashMap<Integer,HashMap<Integer,String>> AttrList,
//			Vector<Integer> equOderVec, HashMap<Integer, String> IndexList){
////			HashMap<Integer,String> loopAttrList, Integer[] loop_name){
//		
//		//AttrListからInputListを作成するメソッド(数式がランダムでも対応する)
//		//AttrListは番号1からスタートしていると仮定している。
//		
//		int loop_num= IndexList.size();	//ループの数
//		
//		//ループ変数nのリスト上にある全てのinitを探索する。（複数の場合は並列ループ）
//		//initの属性を記録し、nのリスト上にその属性でinnerとfinalが各1つ以上あるか判定
//		//見つかれば子要素に追加する。（nullであってもすべて）
//		
//		ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
//		
//		for(int i=0;i<loop_num;i++){
//			for(int j=0;j<equOderVec.size();j++){
//				int equNum1 = equOderVec.get(j);
//				if(AttrList.get(equNum1).get(i).equals("init")){
//					for(int k=0;k<loop_num;k++){
//						
//						if(k!=i){
//							String init_attr = new String(AttrList.get(equNum1).get(k));
//							int init_count=0;
//							int inner_count=0;
//							int final_count=0;
//							for(int m=0;m<equOderVec.size();m++){
//								int equNum2 = equOderVec.get(m);
//								if(AttrList.get(equNum2).get(k).equals(init_attr)){
//									if(AttrList.get(equNum2).get(i).equals("init")) init_count++;
//									if(AttrList.get(equNum2).get(i).equals("inner")) inner_count++;
//									if(AttrList.get(equNum2).get(i).equals("final")) final_count++;
//								}		
//							}
//							if(init_count!=0 &&  inner_count!=0 && final_count!=0){								
//								inputList.add(new RelationPattern(k,i,init_attr));
//							}
//						}
//					}
//				}
//			}			
//		}
//		return inputList;
//	}	
//	
//	public ArrayList<RelationPattern> fix_PartialDependency(ArrayList<RelationPattern> inputList){
//		//nullをもつ親子のペアに対して、null以外をもつものが見つかればnullをもつ情報は削除
// 		
//		ArrayList<RelationPattern> inputList_new= new ArrayList<RelationPattern>();
//		for(int i=0;i<inputList.size();i++){
//			
//			if(!inputList.get(i).Attribute_name.equals("null")){
//				inputList_new.add(inputList.get(i));
//			}else{
//				Integer Parent = inputList.get(i).Parent_name;
//				Integer Child = inputList.get(i).Child_name;
//			
//				int flag = 0;
//				
//				for(int j=0;j<inputList.size();j++){
//					if(i!=j){
//						if(inputList.get(j).Parent_name.equals(Child) && inputList.get(j).Child_name.equals(Parent)||
//								inputList.get(j).Parent_name.equals(Parent) && inputList.get(j).Child_name.equals(Child)){
//							if(!inputList.get(j).Attribute_name.equals("null")){
//								flag=1;
//							}
//						}
//					}
//				}
//				if(flag!=1) inputList_new.add(inputList.get(i));
//			}
//		}
//		
//		return inputList_new;
//	}
//	
//	public ArrayList<Integer> make_separateNodeList(ArrayList<RelationPattern> inputList, HashMap<Integer, String> m_indexList) {
//		
//		Set<Integer> loop_nameSet = m_indexList.keySet();
//		Integer[] loop_name = loop_nameSet.toArray(new Integer[0]);
//		
//		//ほかの全ての要素に対してnull関係しか持たないものを登録する.
//		ArrayList<Integer> nullNodeList = new ArrayList<Integer>();
//		for(int i=0;i<loop_name.length;i++){
//			int flag=0;//null以外の関係をもつかどうかを探索
//			for(int j=0;j<inputList.size();j++){
//				if(inputList.get(j).Parent_name.equals(loop_name[i]) || inputList.get(j).Child_name.equals(loop_name[i])){
//					if(!inputList.get(j).Attribute_name.equals("null")){
//						flag=1;
//					}
//				}
//			}
//			if(flag==0) nullNodeList.add(loop_name[i]);
//		}
//		
//		
//		return nullNodeList;
//	}
//	
//	
//	public ArrayList<RelationPattern> remove_NullRelation(ArrayList<RelationPattern> inputList) {
//		
//		ArrayList<RelationPattern> inputList_new = new ArrayList<RelationPattern>();
//		
//		for(int i=0;i<inputList.size();i++){
//			//nullでない関係情報のみを抽出する
//			if(!inputList.get(i).Attribute_name.equals("null")){
//				inputList_new.add(inputList.get(i));
//			}			
//		}
//		
//		return inputList_new;
//	}
//	
//	
//	public boolean check_inh(ArrayList<RelationPattern> fixed_inputList,ArrayList<RelationPattern> inputList) {
//		
//		//継承関係を含むセットを作成し、全て入力セットに含まれていれば矛盾しない構造であると見なす
//		
//		ArrayList<RelationPattern>inh_set = new ArrayList<RelationPattern>();
//		int count=0;
//		for(int i=0;i<fixed_inputList.size();i++){
//			inh_set.add(fixed_inputList.get(i));//
//			count++;
//			//子要素の名前について,親になっているかを調べ,親になっていればその子要素に対して継承関係を作成する.
//			
//			for(int j=0;j<fixed_inputList.size();j++){
//				Integer c_name = fixed_inputList.get(i).Child_name;
//				if(i!=j){
//					if(c_name.equals(fixed_inputList.get(j).Parent_name)){
//						
//						Integer p = fixed_inputList.get(i).Parent_name;
//						Integer c = fixed_inputList.get(j).Child_name;
//						String a = new String(fixed_inputList.get(i).Attribute_name);
//						RelationPattern pattern = new RelationPattern(p,c,a);
//						inh_set.add(pattern);
//						for(int k=0;k<inputList.size();k++){
//							if(pattern.Parent_name.equals(inputList.get(k).Parent_name)){
//								if(pattern.Child_name.equals(inputList.get(k).Child_name)){
//									if(pattern.Attribute_name.equals(inputList.get(k).Attribute_name)){
//										count++;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		
//			
//		if(fixed_inputList.size()>2){
//			for(int x=0;x<fixed_inputList.size()-2;x++){
//				
//				int setSize = inh_set.size(); //初期サイズを記録して探索に使用
//				for(int j=0;j<setSize;j++){
//					Integer c_name = inh_set.get(j).Child_name;
//					for(int k=0;k<setSize;k++){
//						if(j!=k){
//							if(c_name.equals(inh_set.get(k).Parent_name)){
//								
//								Integer p = inh_set.get(j).Parent_name;
//								Integer c = inh_set.get(k).Child_name;
//								String a = new String(inh_set.get(j).Attribute_name);
//								RelationPattern pattern = new RelationPattern(p,c,a);
//								inh_set.add(pattern);
//								for(int h=0;h<inputList.size();h++){
//									if(pattern.Parent_name.equals(inputList.get(h).Parent_name)){
//										if(pattern.Child_name.equals(inputList.get(h).Child_name)){
//											if(pattern.Attribute_name.equals(inputList.get(h).Attribute_name)){
//												count++;
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		if(count==inh_set.size()) return true;
//		else return false;
//
//	}

}
