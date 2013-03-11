package jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;


import jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure.RelationPattern;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.SimpleRecMLAnalyzer;


public class LabelAttribute extends SimpleRecMLAnalyzer{
	
	HashMap<Integer, HashMap<String, Integer>> m_HashMapNodeList;
	HashMap<Integer, HashMap<String, Integer>> m_HashMapEdgeList;
	Vector<Integer> m_VecEquOder;
	
	HashMap<Integer, Integer> m_finalAttrEquNumLists;	
	HashMap<Integer, Integer> m_initendAttrEquNumLists;
	HashMap<Integer, Integer> m_condrefAttrEquNumLists;
	HashMap<Integer, String> m_indexList;
	HashMap<Integer, HashMap<Integer, String>> m_AttrLists;
	
	ArrayList<RelationPattern> m_LoopStructure;
	ArrayList<ArrayList<Integer>> simulEquList;
	
	public LabelAttribute(
			HashMap<Integer, HashMap<String, Integer>> NodeHashMap,
			HashMap<Integer, HashMap<String, Integer>> EdgeHashMap,
			Vector<Integer> EquOderVec,
			HashMap<Integer, Integer> finalAttr,
			HashMap<Integer, Integer> initendAttr,
			HashMap<Integer, Integer> CondrefAttr,
			HashMap<Integer, String> indexList,
			ArrayList<ArrayList<Integer>> simul){

		m_HashMapNodeList = NodeHashMap;
		m_HashMapEdgeList = EdgeHashMap;
		m_VecEquOder = EquOderVec;
		m_finalAttrEquNumLists = finalAttr;
		m_initendAttrEquNumLists = initendAttr;
		m_condrefAttrEquNumLists = CondrefAttr;
		m_indexList = indexList;		
		m_AttrLists = new HashMap<Integer, HashMap<Integer, String>>();
		m_LoopStructure = new ArrayList<RelationPattern>();
		simulEquList = simul;

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

		
		//---------------------------------------------------
		//配列変数の数によって解析方法を変更する
		//---------------------------------------------------
		if(m_indexList.size() == 0){
			
		}else if(m_indexList.size() == 1){
			RelationPattern rp = new RelationPattern(0,-1,"");
			m_LoopStructure.add(rp);
			
			//---------------------------------------------------
			//conditionに対して属性情報を割り振る
			//---------------------------------------------------
			addConditionAttr(m_AttrLists);

		}else{
			
			/**
			 * To decide Loop Structure part (high-speed version). 
			 * @author n-washio
			 * 2013/3/7 Latest Update
			 */

			LoopStructureHandler lsh = new LoopStructureHandler();

			//---------------------------------------------------
			//入力情報作成メソッド
			//inputListを作成
			//---------------------------------------------------
			ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
			inputList = lsh.make_inputList(m_AttrLists, m_VecEquOder, m_indexList);
			
			//---------------------------------------------------
			//重複処理
			//---------------------------------------------------
			inputList= lsh.removeOverlap(inputList);
		
			Set<Integer> loopName = m_indexList.keySet();
			Integer[] loopNameList = (Integer[]) loopName.toArray(new Integer[loopName.size()]);
			
			//---------------------------------------------------
			//入力情報に対する部分的な依存関係処理
			//---------------------------------------------------
			inputList=lsh.fix_PartialDependency(inputList);

			//---------------------------------------------------
			//入力情報に対するnull処理
			//ここで完全に独立しているノードは消えることになるのでリストに登録する.
			//---------------------------------------------------
			
			//属性競合不可避なケースに対するnullへのpre/post割り当て
			inputList=lsh.assignInnerDependency(inputList, loopNameList);
			
			ArrayList<Integer> separateNodeList = new ArrayList<Integer>();
			separateNodeList=lsh.make_separateNodeList(inputList,m_indexList);
			
			inputList=lsh.remove_NullRelation(inputList);

			if(inputList.size()==0){
				//全て独立ノードである場合,関係を作成
				RelationPattern initialize = new RelationPattern(0,1,"post");
				inputList.add(initialize);
				initialize = new RelationPattern(1,0,"pre");
				inputList.add(initialize);
				separateNodeList.remove(0);
				separateNodeList.remove(1);
			}
			
			
			
			//名前の順列を格納
			lsh.set_nameList(0, new int [m_indexList.size()], new boolean [m_indexList.size()],  loopNameList);
			
			boolean findFlag=false;
			
			for(int start=0;start<lsh.loop_nameList.size();start++){
				
				
				for(int goal=0;goal<lsh.loop_nameList.size();goal++){
					
					
						
						
					ArrayList<Integer[]> Pair_Pattern = new ArrayList<Integer[]>();
					Pair_Pattern.add(lsh.loop_nameList.get(start));
					Pair_Pattern.add(lsh.loop_nameList.get(goal));
					
					ArrayList<RelationPattern> inputList_new = new ArrayList<RelationPattern>();
		    		
		    		//見つかるまで削除手順を変えて繰り返す.
					inputList_new= new ArrayList<RelationPattern>();
					
					//---------------------------------------------------
					//継承関係の削除
					//---------------------------------------------------
					inputList_new = lsh.remove_inhPath(inputList, Pair_Pattern);
					
					//---------------------------------------------------
					//双方向パスの処理
					//---------------------------------------------------
					
					ArrayList<Integer> pre_num = new ArrayList<Integer>();
					for(int j=0;j<inputList_new.size();j++){
						if(inputList_new.get(j).Attribute_name.equals("pre")) pre_num.add(j);
					}
					
					lsh.make_prepostList(0, new Integer[pre_num.size()]);

					inputList_new = lsh.fix_interactivePath(inputList_new,lsh.loop_nameList.size());
					
					if(inputList_new!=null &&lsh.check_inh(inputList_new,inputList)){
						
						//継承判定をして矛盾しなければbreak
						lsh.loopStructure=inputList_new;
						findFlag=true;
						break;
					}
					else{
						lsh.prepostList.clear();
					}
					
		
				}
				if(findFlag)break;
			}	
			//---------------------------------------------------
			//null置換およびループ構造の決定
			//---------------------------------------------------
			
			lsh.loopStructure = lsh.get_LoopStructure(lsh.loopStructure,separateNodeList);

			//---------------------------------------------------
			//ループ構造の決定
			//---------------------------------------------------

			m_LoopStructure = lsh.loopStructure;
			
			
			//---------------------------------------------------
			//生成に必要のない属性情報を削る
			//---------------------------------------------------
			removeAttr(m_LoopStructure, m_AttrLists);

			//---------------------------------------------------
			//conditionに対して属性情報を割り振る
			//---------------------------------------------------
			addConditionAttr(m_AttrLists);
			
			outputLabelSample(m_AttrLists);
		}

	}

	
	//========================================================
	//labelAllocation
	// 属性情報決定メソッド
	//========================================================
	@SuppressWarnings("unchecked")
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
		//System.out.println("------------- debug Ouptup downwardLabel -------------");
		//outputLabelSample(HashMapDownAttrList);

		/*依存関係グラフに対してGoalからStartに向かって属性情報を決定していく*/
		HashMapUpAttrList = upLabel(NodeList, EdgeList);
		//System.out.println("------------- debug Ouptup upLabel -------------");
		//outputLabelSample(HashMapUpAttrList);
		
		/*2種類の探索結果の統合を行う*/
		HashMapMargeAttrList = mergeDownWithUp(HashMapDownAttrList, HashMapUpAttrList);
		//System.out.println("------------- debug Ouptup mergeDownWithUp -------------");
		//outputLabelSample(HashMapMargeAttrList);
		
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
		
		/*スタートNodeの属性情報を登録する*/
		HashMapDownAttrLists.put(-1, attrList);
		
		/*equation oder順に属性情報リストを決定していく*/		
		for(int i = 0;i<this.m_VecEquOder.size();i++){
			/*数式番号を取得する*/
			int checkEquNum = m_VecEquOder.get(i);
//			/*debug*/
//			System.out.println("EquNum: " + checkEquNum);
			
			ArrayList<Integer> checkSorceIdList = null;
			/*数式番号が登録されているNodeIdを取得する*/
			int checkNodeId;
			checkNodeId =  getNodeId(checkEquNum, NodeList);
			/*check of simultaneous equations*/

			boolean normalEqu = true;
			for(ArrayList<Integer> siENumList :simulEquList){
				if(siENumList.contains(checkEquNum)){
					normalEqu = false;
					/*simultaneous equations*/
					boolean first = true;
					for(int Enum:siENumList){					
						int simulNodeId =  getNodeId(Enum, NodeList);
						ArrayList<Integer> ar = getSourceId(simulNodeId, EdgeList);
						if(first){
							checkSorceIdList = ar;
							first = false;
						}else{
							checkSorceIdList.addAll(ar);
						}
					}
				}
			}
			if(normalEqu){
				checkSorceIdList =  getSourceId(checkNodeId, EdgeList);
			}

			
			/*checkSorceIdListの0番目のものを決定する属性情報としてまず選択する*/
			HashMap<Integer, String> cheakAttrList = new HashMap<Integer, String>();
			int sourceNodeEquNum_0 = NodeList.get(checkSorceIdList.get(0)).get("equation");
			
			for(int oder=0;oder<checkSorceIdList.size();oder++){
				sourceNodeEquNum_0 = NodeList.get(checkSorceIdList.get(oder)).get("equation");
				if(HashMapDownAttrLists.containsKey(sourceNodeEquNum_0)){
					break;
				}
			}
			cheakAttrList = HashMapDownAttrLists.get(sourceNodeEquNum_0);
			
			/*checkSorceIdListのk番目のものを決定する属性情報とと比較する*/
			for(int k=0;k<checkSorceIdList.size();k++){
				int sourceNodeEquNum_k = NodeList.get(checkSorceIdList.get(k)).get("equation");
//				/*debug*/			
//				System.out.println(checkEquNum+" , "+sourceNodeEquNum_k);
		
				if(HashMapDownAttrLists.containsKey(sourceNodeEquNum_k)){
//					/*debug*/
//					System.out.println("sourceNodeEquNum_k:" + sourceNodeEquNum_k );
					HashMap<Integer, String> sourceAttrList = HashMapDownAttrLists.get(sourceNodeEquNum_k);
					cheakAttrList = mergeDownAttrList(cheakAttrList,sourceAttrList);	
				}
				
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
	@SuppressWarnings("unused")
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
//			/*debug*/
//			System.out.println("EquNum: " + checkEquNum);

			for(int xx:HashMapUpAttrList.keySet()){
				//System.out.print(xx+", ");
			}
			//System.out.println();
			
			ArrayList<Integer> checkDestIdList = null;
			/*数式番号が登録されているNodeIdを取得する*/
			int checkNodeId;
			checkNodeId =  getNodeId(checkEquNum, NodeList);
			
			
			boolean normalEqu = true;
			for(ArrayList<Integer> siENumList :simulEquList){
				if(siENumList.contains(checkEquNum)){
					normalEqu = false;
					/*simultaneous equations*/
					boolean first = true;
					for(int Enum:siENumList){					
						int simulNodeId =  getNodeId(Enum, NodeList);
						ArrayList<Integer> ar = getDestId(simulNodeId, EdgeList);
						if(first){
							checkDestIdList = ar;
							first = false;
						}else{
							checkDestIdList.addAll(ar);
						}
					}
				}
			}
			if(normalEqu){
				checkDestIdList =  getDestId(checkNodeId, EdgeList);
			}
			/*sourceにNodeIdを持つdestのリストを取得する*/
//			ArrayList<Integer> checkDestIdList;
//			checkDestIdList =  getDestId(checkNodeId, EdgeList);
			
			
			HashMap<Integer, String> cheakAttrList = new HashMap<Integer, String>();
			int destNodeEquNum_0 = NodeList.get(checkDestIdList.get(0)).get("equation");

			for(int oder=0;oder<checkDestIdList.size();oder++){
				destNodeEquNum_0 = NodeList.get(checkDestIdList.get(oder)).get("equation");
				if(HashMapUpAttrList.containsKey(destNodeEquNum_0)){
					//System.out.println("destNodeEquNum_0:" + destNodeEquNum_0 );
					break;
				}
			}
			
			cheakAttrList = HashMapUpAttrList.get(destNodeEquNum_0);
			
			for(int k=0;k<checkDestIdList.size();k++){	
				int destNodeEquNum_k = NodeList.get(checkDestIdList.get(k)).get("equation");
				
				if(HashMapUpAttrList.containsKey(destNodeEquNum_k)){
////					/*debug*/
//					System.out.println("checkEquNum:" + checkEquNum );
//					System.out.println("destNodeEquNum_0:" + destNodeEquNum_0 );
//					System.out.println("sourceNodeEquNum_k:" + destNodeEquNum_k );
//					System.out.println();
					HashMap<Integer, String> destAttrList = HashMapUpAttrList.get(destNodeEquNum_k);
					cheakAttrList = mergeUpAttrList(cheakAttrList,destAttrList);
				}
		
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
		
		for(int i:destAttrList.keySet()){
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
	@SuppressWarnings("unused")
	public void outputLabelSample(HashMap<Integer, HashMap<Integer, String>> AttrLists){
		for(Integer i:AttrLists.keySet()){
			HashMap<Integer, String> hm = AttrLists.get(i);
			//System.out.print("equNum"+ i);
			for(Integer j:hm.keySet()){
				//System.out.print("-" + AttrLists.get(i).get(j) + "-");
			}
			//System.out.println();
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

		return AttrLists;
	}
	

	private void kawabataTest1212_1(int PARENT_ID, int childID, String ATTR) {
		// TODO Auto-generated method stub
//		kawabataTest1212_2(PARENT_ID,childID, ATTR);
//		kawabataTest1212_3(PARENT_ID,childID, ATTR);
		
		kawabataTest1212_2(PARENT_ID, childID, ATTR);
		//System.out.println("-----------------debug----------- PARENT_ID:"+ PARENT_ID + " -- childID: " + childID +" -- ATTR:"+ATTR);

		for(int i=0;i<m_LoopStructure.size();i++){
			int pa = m_LoopStructure.get(i).Parent_name;
			int ch = m_LoopStructure.get(i).Child_name;
			if(childID == pa){
				/*二重処理が生じている*/
				kawabataTest1212_2(PARENT_ID, childID, ATTR);
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
		//System.out.println("-----------------debug----------- PARENT_ID:"+ PARENT_ID + " -- childID: " + childID +" -- ATTR:"+ATTR);

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
	
	@SuppressWarnings("unused")
	private void kawabataTest1212_3(int PARENT_ID, int childID, String ATTR) {
		// TODO Auto-generated method stub
		kawabataTest1212_2(PARENT_ID, childID, ATTR);
		//System.out.println("-----------------debug----------- PARENT_ID:"+ PARENT_ID + " -- childID: " + childID +" -- ATTR:"+ATTR);

		for(int i=0;i<m_LoopStructure.size();i++){
			int pa = m_LoopStructure.get(i).Parent_name;
			int ch = m_LoopStructure.get(i).Child_name;
			if(childID == pa){
				kawabataTest1212_2(PARENT_ID, childID, ATTR);
				//System.out.println("-----------------debug----------- PARENT_ID:"+ PARENT_ID + " -- childID: " + childID +" -- ATTR:"+ATTR);
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
	@SuppressWarnings("unchecked")
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
	


}
