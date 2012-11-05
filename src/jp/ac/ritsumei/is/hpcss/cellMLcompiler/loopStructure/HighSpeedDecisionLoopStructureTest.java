package jp.ac.ritsumei.is.hpcss.cellMLcompiler.loopStructure;

/**
 * This class is the test to decide Loop Structure　quickly.
 *  
 * @author n-washio
 *
 */

import java.util.*;

//修正版 2012/11/5
public class HighSpeedDecisionLoopStructureTest {

	public static ArrayList<Integer> path_length;
	public static ArrayList<String[]> prepostList = new  ArrayList<String[]>();
	public static ArrayList<Integer[]> loop_nameList = new ArrayList<Integer[]>();
	public static ArrayList<ArrayList<Integer[]>> loop_nameListPair = new ArrayList<ArrayList<Integer[]>>();
	public static ArrayList<RelationPath> loopStructure= new ArrayList<RelationPath>();
	public static HashMap<Integer,Integer> connectableNode = new HashMap<Integer,Integer>();
	
	public static void main(String[] args) {
		//---------------------------------------------------
		//テスト　main　クラス
		//入力：	属性情報リストHashMap<Integer,HashMap<String,String>> AttrList
		//---------------------------------------------------		

		//---------------------------------------------------
		//入力情報作成メソッド
		//inputListを作成する。
		//---------------------------------------------------
		
		//ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
		//inputList = make_inputList(AttrList, loopAttrList,loop_name);
		ArrayList<RelationPath>inputList = new ArrayList<RelationPath>();
		RelationPath pattern;
		
		pattern = new RelationPath(1,2,"null");
		inputList.add(pattern);
		pattern = new RelationPath(1,3,"null");
		inputList.add(pattern);
		pattern = new RelationPath(1,4,"null");
		inputList.add(pattern);
		pattern = new RelationPath(1,5,"null");
		inputList.add(pattern);
		pattern = new RelationPath(1,6,"null");
		inputList.add(pattern);
		pattern = new RelationPath(2,1,"null");
		inputList.add(pattern);
		pattern = new RelationPath(2,3,"null");
		inputList.add(pattern);
		pattern = new RelationPath(2,4,"null");
		inputList.add(pattern);
		pattern = new RelationPath(2,5,"null");
		inputList.add(pattern);
		pattern = new RelationPath(2,6,"null");
		inputList.add(pattern);
		pattern = new RelationPath(3,1,"null");
		inputList.add(pattern);
		pattern = new RelationPath(3,2,"null");
		inputList.add(pattern);
		pattern = new RelationPath(3,4,"null");
		inputList.add(pattern);
		pattern = new RelationPath(3,5,"null");
		inputList.add(pattern);
		pattern = new RelationPath(3,6,"null");
		inputList.add(pattern);
		pattern = new RelationPath(4,1,"null");
		inputList.add(pattern);
		pattern = new RelationPath(4,2,"null");
		inputList.add(pattern);
		pattern = new RelationPath(4,3,"null");
		inputList.add(pattern);
		pattern = new RelationPath(4,5,"null");
		inputList.add(pattern);
		pattern = new RelationPath(4,6,"null");
		inputList.add(pattern);
		pattern = new RelationPath(5,1,"null");
		inputList.add(pattern);
		pattern = new RelationPath(5,2,"null");
		inputList.add(pattern);
		pattern = new RelationPath(5,3,"null");
		inputList.add(pattern);
		pattern = new RelationPath(5,4,"null");
		inputList.add(pattern);
		pattern = new RelationPath(5,6,"null");
		inputList.add(pattern);
		pattern = new RelationPath(6,1,"null");
		inputList.add(pattern);
		pattern = new RelationPath(6,2,"null");
		inputList.add(pattern);
		pattern = new RelationPath(6,3,"null");
		inputList.add(pattern);
		pattern = new RelationPath(6,4,"null");
		inputList.add(pattern);
		pattern = new RelationPath(6,5,"null");
		
		Integer[] loop_name = {1,2,3,4,5,6};
		set_nameList(0, new int [loop_name.length], new boolean [loop_name.length],loop_name);
		make_pairList(0,new int [2], loop_nameList.size());
		
		
		//計測開始
		long start = System.currentTimeMillis();
		//---------------------------------------------------
		//入力情報に対するnull処理
		//---------------------------------------------------
		
		inputList=fix_PartialDependency(inputList);
		
		//---------------------------------------------------
		//入力情報に対するnull処理２
		//ここで完全に独立しているノードは消えることになるのでリストに登録する.
		//---------------------------------------------------
	
		ArrayList<Integer> separateNodeList = new ArrayList<Integer>();
		separateNodeList=make_separateNodeList(inputList,loop_name);
		
		inputList=remove_NullRelation(inputList);
		
		
		if(inputList.size()==0){
			//全て独立ノードである場合,関係を作成
			pattern = new RelationPath(loop_name[0],loop_name[1],"post");
			inputList.add(pattern);
			pattern = new RelationPath(loop_name[1],loop_name[0],"pre");
			inputList.add(pattern);
			separateNodeList.remove(loop_name[0]);
			separateNodeList.remove(loop_name[1]);
		}
		
		ArrayList<RelationPath> fixed_inputList = new ArrayList<RelationPath>();
		ArrayList<RelationPath> inputList_new;
		
		for(int i=0;i<loop_nameListPair.size();i++){
			//見つかるまで削除手順を変えて繰り返す.
			
			inputList_new= new ArrayList<RelationPath>();
			
			//---------------------------------------------------
			//継承関係の削除
			//---------------------------------------------------
			
			inputList_new = remove_inhPath(inputList, loop_nameListPair.get(i));
			
			//---------------------------------------------------
			//双方向パスの処理
			//---------------------------------------------------
			
			ArrayList<Integer> pre_num = new ArrayList<Integer>();
			for(int j=0;j<inputList_new.size();j++){
				if(inputList_new.get(j).Attribute_name.equals("pre")) pre_num.add(j);
			}
			
			make_prepostList(0, new Integer[pre_num.size()]);

			fixed_inputList = fix_interactivePath(inputList_new,loop_name.length);
			
			if(fixed_inputList!=null &&check_inh(fixed_inputList,inputList)){
				//継承判定をして矛盾しなければbreak
				break;
			}
			else{
				prepostList.clear();
			}
			
		}	
		
		
		//---------------------------------------------------
		//null置換
		//---------------------------------------------------
		
		loopStructure=get_LoopStructure(fixed_inputList,separateNodeList);
		System.out.println("決定されたループ構造（Loop_Structure）----------------");
		if(loopStructure!=null){
			for(int i=0;i<loopStructure.size();i++){
				loopStructure.get(i).printContents();
			}
		}
		System.out.println();
		long stop = System.currentTimeMillis();
		System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
	
		//toRecMLheader(LoopStructure);
		
	}	

	public static boolean check_inh(ArrayList<RelationPath> fixed_inputList,ArrayList<RelationPath> inputList) {
		
		//継承関係を含むセットを作成し、全て入力セットに含まれていれば矛盾しない構造であると見なす
		
		ArrayList<RelationPath>inh_set = new ArrayList<RelationPath>();
		int count=0;
		for(int i=0;i<fixed_inputList.size();i++){
			inh_set.add(fixed_inputList.get(i));//
			count++;
			//子要素の名前について,親になっているかを調べ,親になっていればその子要素に対して継承関係を作成する.
			
			for(int j=0;j<fixed_inputList.size();j++){
				Integer c_name = fixed_inputList.get(i).Child_name;
				if(i!=j){
					if(c_name.equals(fixed_inputList.get(j).Parent_name)){
						
						Integer p = fixed_inputList.get(i).Parent_name;
						Integer c = fixed_inputList.get(j).Child_name;
						String a = new String(fixed_inputList.get(i).Attribute_name);
						RelationPath pattern = new RelationPath(p,c,a);
						inh_set.add(pattern);
						for(int k=0;k<inputList.size();k++){
							if(pattern.Parent_name.equals(inputList.get(k).Parent_name)){
								if(pattern.Child_name.equals(inputList.get(k).Child_name)){
									if(pattern.Attribute_name.equals(inputList.get(k).Attribute_name)){
										count++;
									}
								}
							}
						}
					}
				}
			}
		}
		
			
		if(fixed_inputList.size()>2){
			for(int x=0;x<fixed_inputList.size()-2;x++){
				
				int setSize = inh_set.size(); //初期サイズを記録して探索に使用
				for(int j=0;j<setSize;j++){
					Integer c_name = inh_set.get(j).Child_name;
					for(int k=0;k<setSize;k++){
						if(j!=k){
							if(c_name.equals(inh_set.get(k).Parent_name)){
								
								Integer p = inh_set.get(j).Parent_name;
								Integer c = inh_set.get(k).Child_name;
								String a = new String(inh_set.get(j).Attribute_name);
								RelationPath pattern = new RelationPath(p,c,a);
								inh_set.add(pattern);
								for(int h=0;h<inputList.size();h++){
									if(pattern.Parent_name.equals(inputList.get(h).Parent_name)){
										if(pattern.Child_name.equals(inputList.get(h).Child_name)){
											if(pattern.Attribute_name.equals(inputList.get(h).Attribute_name)){
												count++;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if(count==inh_set.size()) return true;
		else return false;

	}

	public static ArrayList<Integer> make_separateNodeList(ArrayList<RelationPath> inputList, Integer[] loop_name) {
		
		//ほかの全ての要素に対してnull関係しか持たないものを登録する.
		ArrayList<Integer> nullNodeList = new ArrayList<Integer>();
		
		for(int i=0;i<loop_name.length;i++){
			int flag=0;//null以外の関係をもつかどうかを探索
			for(int j=0;j<inputList.size();j++){
				if(inputList.get(j).Parent_name.equals(loop_name[i]) || inputList.get(j).Child_name.equals(loop_name[i])){
					if(!inputList.get(j).Attribute_name.equals("null")){
						flag=1;
					}
				}
			}
			if(flag==0) nullNodeList.add(loop_name[i]);
		}
		
		
		return nullNodeList;
	}

	public static ArrayList<RelationPath> get_LoopStructure(ArrayList<RelationPath> inputList,ArrayList<Integer> nullNodeList){

		ArrayList<RelationPath> LoopStructure= new ArrayList<RelationPath>(inputList);

		
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
				RelationPath relation = new RelationPath(connectableNode.get(root_nameList.get(i-1)),root_nameList.get(i),"post");
				LoopStructure.add(relation);
			}
			RelationPath relation;
			if(nullNodeList.size()!=0){
				//rootリストの最後のツリーの接続可能箇所に独立ノードを接続
				Integer last_root=connectableNode.get(root_nameList.get(root_nameList.size()-1));
				relation = new RelationPath(last_root,nullNodeList.get(0),"post");
				LoopStructure.add(relation);
				if(nullNodeList.size()>1){
					//独立ノード同士は必ず接続できる
					for(int i=1;i<nullNodeList.size();i++){
						relation = new RelationPath(nullNodeList.get(i-1),nullNodeList.get(i),"post");
						LoopStructure.add(relation);
					}
				}
			}
		}
		
		return LoopStructure;
	}
	
	public static void search_ConnectableNode(Integer root_name,Integer My_name, ArrayList<RelationPath> inputList){
		
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
	
	public static void toRecMLheader(ArrayList<RelationPath> LoopStructure){

		//rootの探索
		ArrayList<Integer> root_num = new ArrayList<Integer>();
		for(int i=0;i<LoopStructure.size();i++){
			//各親要素の名前について子要素の中を探索し、見つからなければrootである
			int flag=0;
			for(int j=0;j<LoopStructure.size();j++){
				if(LoopStructure.get(i).Parent_name.equals(LoopStructure.get(j).Child_name)){
					flag=1;
				}
			}
			if(flag==0)	root_num.add(i);//rootとなる関係情報を記録していく
		}
		
		int tab_count=2;//まず2回tabが入った状態で子要素は呼ばれる。
		System.out.println("<loopstruct name="+ "\""+LoopStructure.get(root_num.get(0)).Parent_name+"\""+">");
		for(int i=0;i<root_num.size();i++){
			System.out.println("\t"+"<position name="+ "\""+LoopStructure.get(root_num.get(i)).Attribute_name+ "\""+">");
			
			
			int p_flag=0;
			for(int j=0;j<LoopStructure.size();j++){
				if(LoopStructure.get(i).Child_name.equals(LoopStructure.get(j).Parent_name)){
					p_flag=1;//親になっているフラグ
				}
			}
			if(p_flag==1)System.out.println("\t\t"+"<loopstruct name="+ "\""+LoopStructure.get(root_num.get(i)).Child_name+ "\""+">");
			else System.out.println("\t\t"+"<loopstruct name="+ "\""+LoopStructure.get(root_num.get(i)).Child_name+ "\""+"/>");
			
			call_child(LoopStructure, LoopStructure.get(root_num.get(i)).Child_name, tab_count);
		}
		System.out.println("</loopstruct name>");
	}
	
	public static void call_child(ArrayList<RelationPath> LoopStructure,Integer My_name, int tab_count){
		//子要素の名前を受け取り、親になっていれば子要素を表示する動作を再帰的に行う。
		int flag=0;
		
		for(int i=0;i<LoopStructure.size();i++){
			if(My_name.equals(LoopStructure.get(i).Parent_name)){
				if(flag==0)tab_count++;
				for(int j=0;j<tab_count;j++) System.out.print("\t");
				System.out.println("<position name="+ "\""+LoopStructure.get(i).Attribute_name+ "\""+">");
				
				tab_count++;
				for(int j=0;j<tab_count;j++) System.out.print("\t");
				
				int p_flag=0;
				for(int j=0;j<LoopStructure.size();j++){
					if(LoopStructure.get(i).Child_name.equals(LoopStructure.get(j).Parent_name)){
						p_flag=1;//親になっているフラグ
					}
				}
				if(p_flag==1)System.out.println("<loopstruct name="+ "\""+LoopStructure.get(i).Child_name+ "\""+">");
				else System.out.println("<loopstruct name="+ "\""+LoopStructure.get(i).Child_name+ "\""+"/>");
				call_child(LoopStructure,LoopStructure.get(i).Child_name, tab_count);
				tab_count--;
				flag=1;//子要素ありのフラグ
			}
		}
		if(flag==1){
			//子要素を持つ場合の処理（tab++が必要）
			tab_count--;
			for(int j=0;j<tab_count;j++) System.out.print("\t");
			System.out.println("</loopstruct name>");
		
		
			tab_count--;
			for(int j=0;j<tab_count;j++) System.out.print("\t");
			System.out.println("</position name>");
			
		}
		else{
			//子要素を持たない場合
			tab_count--;
			for(int j=0;j<tab_count;j++) System.out.print("\t");
			System.out.println("</position name>");
			
		}

	}
	
	
	

	public static ArrayList<RelationPath> remove_NullRelation(ArrayList<RelationPath> inputList) {
		
		ArrayList<RelationPath> inputList_new = new ArrayList<RelationPath>();
		
		for(int i=0;i<inputList.size();i++){
			//nullでない関係情報のみを抽出する
			if(!inputList.get(i).Attribute_name.equals("null")){
				inputList_new.add(inputList.get(i));
			}			
		}
		
		return inputList_new;
	}	
	public static ArrayList<RelationPath> fix_interactivePath(ArrayList<RelationPath> inputList, int loop_num) {
		

		int find=0;
		ArrayList<RelationPath> inputList_new = new ArrayList<RelationPath>();
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
	
	public static void make_prepostList(int n, Integer[] perm){

		
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
	
	
	public static void make_pairList(int n, int[] perm, int ListSize){
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
	


	public static void set_nameList(int n, int[] perm, boolean[] flag, Integer[] loop_name){
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
	
	public static ArrayList<RelationPath> remove_inhPath(ArrayList<RelationPath> inputList, ArrayList<Integer[]> loop_namePair){
		ArrayList<RelationPath> inputList_new =  new ArrayList<RelationPath>(inputList);
		
		//処理のアウトライン
		//自身以外の全ノードへの間接パスと直接パスを探索。見つかったループ名は記録していく。
		//While(目的ノードを発見するまで, または記録された名前が重複するまで)		
		//if(間接パスが存在){
		//　　直接パスを全て消す処理を行う。
		//　　if(preを消す時) ペアのpostも消す。
		//  else if(postを消す時)ペアのpreも消す。
		//}
		
		for(int i=0;i<loop_namePair.get(0).length;i++){
			for(int j=0;j<loop_namePair.get(1).length;j++){
				
				
				int someSetflag=0;
				if(loop_namePair.get(0)[i]!=loop_namePair.get(1)[j]){
					//iからjへのpathを探索
					ArrayList<Integer> nameList= new ArrayList<Integer>();
					path_length= new ArrayList<Integer>();
					nameList.add(loop_namePair.get(0)[i]);//出発名を経路リストに追加
				
					
					search_Child(inputList_new,loop_namePair.get(0)[i],loop_namePair.get(1)[j],nameList);
				
					
					if(path_length.size()!=1) someSetflag=1;
					
					int flag=0;
					for(int x=0;x<path_length.size();x++){
						if(path_length.get(x).equals(1)) flag=1;
					}
					
					if(someSetflag==1 && flag==1){
						
						for(int y=0;y<inputList_new.size();y++){
							if(inputList_new.get(y).Parent_name.equals(loop_namePair.get(0)[i]) && inputList_new.get(y).Child_name.equals(loop_namePair.get(1)[j])){
								inputList_new.remove(y);
							}
						}
						for(int y=0;y<inputList_new.size();y++){
							if(inputList_new.get(y).Child_name.equals(loop_namePair.get(0)[i]) && inputList_new.get(y).Parent_name.equals(loop_namePair.get(1)[j])){
								inputList_new.remove(y);
							}
						}
					}					
				}
			}
		}
		
		return inputList_new;
	}
	
	
	
	
	public static void search_Child(ArrayList<RelationPath> inputList,int i,int j,ArrayList<Integer> nameList){
		
		ArrayList<Integer> nameList_new;//追加は分岐させるたびに別のリストに入れる。
		for(int x=0;x<inputList.size();x++){
			if(inputList.get(x).Parent_name.equals(i)){//iが親になっていた場合
				
				if(inputList.get(x).Child_name.equals(j)){//子要素が目的のものであれば記録
					path_length.add(nameList.size());
					
					
				} else{//子要素が目的のものでなければ、その子要素を親に変更し再度探索（重複なしの条件で）
					int flag=0;
					for(int y=0;y<nameList.size();y++){
						if(nameList.get(y).equals(inputList.get(x).Child_name)) flag=1;					
					}
					if(flag==0){
						nameList_new= new ArrayList<Integer>(nameList);
						nameList_new.add(i);
						search_Child(inputList,inputList.get(x).Child_name,j,nameList_new);						
						
					}
				}
			}
		}
	}
	
	public static ArrayList<RelationPath> fix_PartialDependency(ArrayList<RelationPath> inputList){
		//nullをもつ親子のペアに対して、null以外をもつものが見つかればnullをもつ情報は削除
 		
		ArrayList<RelationPath> inputList_new= new ArrayList<RelationPath>();
		for(int i=0;i<inputList.size();i++){
			
			if(!inputList.get(i).Attribute_name.equals("null")){
				inputList_new.add(inputList.get(i));
			}else{
				Integer Parent = inputList.get(i).Parent_name;
				Integer Child = inputList.get(i).Child_name;
			
				int flag = 0;
				
				for(int j=0;j<inputList.size();j++){
					if(i!=j){
						if(inputList.get(j).Parent_name.equals(Child) && inputList.get(j).Child_name.equals(Parent)||
								inputList.get(j).Parent_name.equals(Parent) && inputList.get(j).Child_name.equals(Child)){
							if(!inputList.get(j).Attribute_name.equals("null")){
								flag=1;
							}
						}
					}
				}
				if(flag!=1) inputList_new.add(inputList.get(i));
			}
		}
		
		return inputList_new;
	}
	
	public static ArrayList<RelationPath> make_inputList(HashMap<Integer,HashMap<Integer,String>> AttrList,
			HashMap<Integer,String> loopAttrList, Integer[] loop_name){
		
		//AttrListからInputListを作成するメソッド(数式がランダムでも対応する)
		//AttrListは番号1からスタートしていると仮定している。
		
		int val_num= AttrList.size();		//変数の数
		int loop_num= loopAttrList.size();	//ループの数
		
		//ループ変数nのリスト上にある全てのinitを探索する。（複数の場合は並列ループ）
		//initの属性を記録し、nのリスト上にその属性でinnerとfinalが各1つ以上あるか判定
		//見つかれば子要素に追加する。（nullであってもすべて）
		
		ArrayList<RelationPath>  inputList = new ArrayList<RelationPath>();
		
		for(int i=0;i<loop_num;i++){
			for(int j=1;j<val_num+1;j++){
				if(AttrList.get(j).get(loop_name[i]).equals("init")){
					for(int k=0;k<loop_num;k++){
						
						if(k!=i){
							String init_attr = new String(AttrList.get(j).get(loop_name[k]));
							int init_count=0;
							int inner_count=0;
							int final_count=0;
							for(int m=1;m<val_num+1;m++){
								if(AttrList.get(m).get(loop_name[k]).equals(init_attr)){
									if(AttrList.get(m).get(loop_name[i]).equals("init")) init_count++;
									if(AttrList.get(m).get(loop_name[i]).equals("inner")) inner_count++;
									if(AttrList.get(m).get(loop_name[i]).equals("final")) final_count++;
								}		
							}
							if(init_count!=0 &&  inner_count!=0 && final_count!=0){								
								inputList.add(new RelationPath(loop_name[k],loop_name[i],init_attr));
							}
						}
					}
				}
			}			
		}
		return inputList;
	}
}

	
	

