package jp.ac.ritsumei.is.hpcss.cellMLcompiler.loopStructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.MathMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLcompiler.parser.SimpleRecMLAnalyzer;

public class HighSpeedLabelAttribute extends SimpleRecMLAnalyzer{

	HashMap<Integer, HashMap<String, Integer>> m_HashMapNodeList;
	HashMap<Integer, HashMap<String, Integer>> m_HashMapEdgeList;
	Vector<Integer> m_VecEquOder;
	
	HashMap<Integer, Integer> m_finalAttrEquNumLists;	
	HashMap<Integer, Integer> m_initendAttrEquNumLists;
	HashMap<Integer, Integer> m_condrefAttrEquNumLists;
	HashMap<Integer, String> m_indexList;
	HashMap<Integer, HashMap<Integer, String>> m_AttrLists;
	
	ArrayList<RelationPath> m_LoopStructure;
	
	public HighSpeedLabelAttribute(
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
		
		m_LoopStructure = new ArrayList<RelationPath>();

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
	public ArrayList<RelationPath> getM_LoopStrucuture(){
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
//		System.out.println("Origin Attribute");
//		outputLabelSample(m_AttrLists);

		
		//---------------------------------------------------
		//配列変数の数によって解析方法を変更する
		//---------------------------------------------------
		if(m_indexList.size() == 0){
			
		}else if(m_indexList.size() == 1){
			RelationPath rp = new RelationPath(0,-1,"");
			m_LoopStructure.add(rp);
			
			nullChange();
			
			//---------------------------------------------------
			//conditionに対して属性情報を割り振る
			//---------------------------------------------------
			addConditionAttr(m_AttrLists);
//			outputLabelSample(m_AttrLists);
		}else{
			
			HighSpeedDecisionLoopStructure hsdls = new HighSpeedDecisionLoopStructure();
			
			//---------------------------------------------------
			//入力情報作成メソッド
			//inputListを作成
			//---------------------------------------------------
			ArrayList<RelationPath>  inputList = new ArrayList<RelationPath>();
			inputList = hsdls.make_inputList(m_AttrLists, m_VecEquOder, m_indexList);

			//---------------------------------------------------
			//入力情報に対する部分的な依存関係処理
			//---------------------------------------------------
			inputList=hsdls.fix_PartialDependency(inputList);
			
			//---------------------------------------------------
			//入力情報に対するnull処理
			//ここで完全に独立しているノードは消えることになるのでリストに登録する.
			//---------------------------------------------------
		
			ArrayList<Integer> separateNodeList = new ArrayList<Integer>();
			separateNodeList=hsdls.make_separateNodeList(inputList,m_indexList);
			
			inputList=hsdls.remove_NullRelation(inputList);
			
			if(inputList.size()==0){
				//全て独立ノードである場合,関係を作成
				RelationPath initialize = new RelationPath(0,1,"post");
				inputList.add(initialize);
				initialize = new RelationPath(1,0,"pre");
				inputList.add(initialize);
				separateNodeList.remove(0);
				separateNodeList.remove(1);
				//ループ数が1以下の場合の例外処理が必要か.
			}
			
			ArrayList<RelationPath> fixed_inputList = new ArrayList<RelationPath>();
			ArrayList<RelationPath> inputList_new;
			
			
			for(int i=0;i<hsdls.loop_namePermutationListPair.size();i++){
				//見つかるまで削除手順を変えて繰り返す.
				
				inputList_new= new ArrayList<RelationPath>();
				
				//---------------------------------------------------
				//継承関係の削除
				//---------------------------------------------------
				
				inputList_new = hsdls.remove_inhPath(inputList, hsdls.loop_namePermutationListPair.get(i));
				
				//---------------------------------------------------
				//双方向パスの処理
				//---------------------------------------------------
				
				ArrayList<Integer> pre_num = new ArrayList<Integer>();
				for(int j=0;j<inputList_new.size();j++){
					if(inputList_new.get(j).Attribute_name.equals("pre")) pre_num.add(j);
				}
				
				hsdls.make_prepostList(0, new Integer[pre_num.size()]);

				fixed_inputList = hsdls.fix_interactivePath(inputList_new,m_indexList.size());
				
				if(fixed_inputList!=null && hsdls.check_inh(fixed_inputList,inputList)){
					//継承判定をして矛盾しなければbreak
					break;
				}
				else{
					hsdls.prepostList.clear();
				}
				
			}	
			//---------------------------------------------------
			//null置換およびループ構造の決定
			//---------------------------------------------------
			
			hsdls.loopStructure = hsdls.get_LoopStructure(fixed_inputList,separateNodeList);
			
			
			//---------------------------------------------------
			//ループ構造の決定
			//---------------------------------------------------

			m_LoopStructure = hsdls.loopStructure;
					
			//---------------------------------------------------
			//生成に必要のない属性情報を削る
			//---------------------------------------------------
			removeAttr(m_LoopStructure, m_AttrLists);

			//---------------------------------------------------
			//conditionに対して属性情報を割り振る
			//---------------------------------------------------
			addConditionAttr(m_AttrLists);
	
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

		/*依存関係グラフに対してGoalからStartに向かって属性情報を決定していく*/
		HashMapUpAttrList = upLabel(NodeList, EdgeList);
		
		/*2種類の探索結果の統合を行う*/
		HashMapMargeAttrList = mergeDownWithUp(HashMapDownAttrList, HashMapUpAttrList);
		
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
		int listSize =  AttrLists.size();
		int i = 0;
		while(listSize>0){
			if(AttrLists.containsKey(i)){
				int listInnerSize =  AttrLists.get(i).size();
				int j = 0;
				System.out.print("equNum"+ i);
				while(listInnerSize>0){
					if(AttrLists.get(i).containsKey(j)){
						System.out.print("-" + AttrLists.get(i).get(j) + "-");
						listInnerSize--;
					}else{
						System.out.print("-    -");
					}
					j++;
				}
				System.out.println();
				listSize--;
			}
			i++;
		}
	}
	
	//========================================================
	//　removeAttr
	// 生成に必要のない属性情報を削る
	//========================================================
	public HashMap<Integer, HashMap<Integer, String>> removeAttr(
			ArrayList<RelationPath> LoopStructure, 
			HashMap<Integer, HashMap<Integer, String>> AttrLists){

		/*全ての配列変数間で成り立つループ構造関係を取得*/
		ArrayList<HashMap<Integer,String>> loopNumOuter = new ArrayList<HashMap<Integer,String>>();
		for(int i=0;i<LoopStructure.size();i++){
			HashMap<Integer,String> loopNumInner = new HashMap<Integer,String>();
			loopNumInner = getRemoveLoopList(LoopStructure, i, LoopStructure.get(i).Attribute_name);
			loopNumOuter.add(loopNumInner);
		}
		
		/**/
		for(int i=0; i<loopNumOuter.size();i++){
			HashMap<Integer,String> check = loopNumOuter.get(i);
			int parentLoopNum = LoopStructure.get(i).Parent_name;
			
			/*全ての配列変数に対して
			 * その継承関係がある配列変数に処理を行う*/
			for(int j=0;j<m_indexList.size();j++){
				if(check.containsKey(j)){
					String Attribute = check.get(j);
					Vector<Integer> hitEquVec = new Vector<Integer>();
					int childLoopNum = j;

					for(int k= 0;k<m_VecEquOder.size();k++){
						/*属性情報リストが,ある数式番号で探索している配列変数に対して属性情報を持っているならば*/
						if(AttrLists.get(m_VecEquOder.get(k)).containsKey(parentLoopNum)){
							/*継承関係の根に相当する配列の属性情報*/
							String checkStr = AttrLists.get(m_VecEquOder.get(k)).get(parentLoopNum);
							/*継承関係の子に相当する配列の属性情報*/
							String checkStrChild = AttrLists.get(m_VecEquOder.get(k)).get(childLoopNum);
							if(checkStr.equals("null") && !(checkStrChild.equals("null"))){
								if(Attribute.equals("pre")){
									AttrLists.get(m_VecEquOder.get(k)).put(parentLoopNum,"pre");								
								}else if(Attribute.equals("post")){
									AttrLists.get(m_VecEquOder.get(k)).put(parentLoopNum,"post");	
								}
							}
							if(checkStrChild.equals("null") && !(checkStr.equals("null"))){
								AttrLists.get(m_VecEquOder.get(k)).put(childLoopNum,Attribute);
							}
						}
					}
				}
			}
			
		}
		
		/**/
		for(int i=0; i<loopNumOuter.size();i++){
			HashMap<Integer,String> check = loopNumOuter.get(i);
			int parentLoopNum = LoopStructure.get(i).Parent_name;
			
			/*全ての配列変数に対して
			 * その継承関係がある配列変数に処理を行う*/
			for(int j=0;j<m_indexList.size();j++){
				if(check.containsKey(j)){
					String Attribute = check.get(j);
					Vector<Integer> hitEquVec = new Vector<Integer>();
					int childLoopNum = j;		
					
					for(int k= 0;k<m_VecEquOder.size();k++){
						/*属性情報リストがある数式番号で探索している配列変数に対して属性情報を持っているならば*/
						if(AttrLists.get(m_VecEquOder.get(k)).containsKey(parentLoopNum)){
							/*継承関係の根に相当する配列の属性情報*/
							String checkStr = AttrLists.get(m_VecEquOder.get(k)).get(parentLoopNum);
							/*継承関係の子に相当する配列の属性情報*/
							String checkStrChild = AttrLists.get(m_VecEquOder.get(k)).get(childLoopNum);
							/*余分な属性情報の判定*/
							checkStr = AttrLists.get(m_VecEquOder.get(k)).get(parentLoopNum);
							checkStrChild = AttrLists.get(m_VecEquOder.get(k)).get(childLoopNum);
							if(!checkStr.equals(Attribute)){
								hitEquVec.add(m_VecEquOder.get(k));
								if((checkStr.equals("null")) && (checkStrChild.equals("null"))){
//									hitEquVec.remove(m_VecEquOder.get(k));
								}
							}
						}
					}
					
					int removeLoopNum = j;
					for(int k=0;k<hitEquVec.size();k++){
						AttrLists.get(hitEquVec.get(k)).remove(removeLoopNum);
					}
				}
			}
		}
		return AttrLists;
	}
	
	//========================================================
	//　getRemoveLoopList
	// 親にあるloopnum　を持つ　子と属性情報のリストを取得する
	//========================================================
	public HashMap<Integer,String> getRemoveLoopList(ArrayList<RelationPath> LoopStructure, int rootNum, String str){
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
			HighSpeedDecisionLoopStructure hsdls = new HighSpeedDecisionLoopStructure();
			hsdls.toRecMLheader(m_LoopStructure);
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
			HighSpeedDecisionLoopStructure hsdls = new HighSpeedDecisionLoopStructure();
			hsdls.toRecMLheader(m_LoopStructure);
		}
	}
}
