package jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure;

/**
 * LoopStructureHandler class is used "LabelAttribute"(@ m-Kawabata)
 *  to decide Loop Structure for Structured RecML generator.
 * 
 * @author n-washio
 * 
 */

import java.util.*;

//修正 2013/1/29 メモリ消費を抑える + スタートとゴールの選択方法を最適化
public class LoopStructureHandler {

	public ArrayList<Integer> path_length;
	
	public ArrayList<String[]> prepostList = new  ArrayList<String[]>();
	public ArrayList<Integer[]> loop_nameList = new ArrayList<Integer[]>();
	public ArrayList<RelationPattern> loopStructure= new ArrayList<RelationPattern>();
	public HashMap<Integer,Integer> connectableNode = new HashMap<Integer,Integer>();
		
	
	public boolean check_inh(ArrayList<RelationPattern> fixed_inputList,ArrayList<RelationPattern> inputList) {
		
		//継承関係と直接関係を全て入力情報と比較し、不一致があれば削除
		int loop_num = fixed_inputList.size()+1;
	
		ArrayList<RelationPattern> judgment_set = new ArrayList<RelationPattern>();

			for(int j=0;j<loop_num-1;j++){

				Integer c_name = fixed_inputList.get(j).Child_name;
				for(int k=0;k<loop_num-1;k++){
					if(j!=k){
						if(c_name.equals(fixed_inputList.get(k).Parent_name)){
							
							Integer pa = fixed_inputList.get(j).Parent_name;
							Integer ch =fixed_inputList.get(k).Child_name;
							String at = new String(fixed_inputList.get(j).Attribute_name);
							RelationPattern patternset = new RelationPattern(pa,ch,at);
							
							int match=0;
							for(int y=0;y<judgment_set.size();y++){
								if(patternset.Attribute_name.equals(judgment_set.get(y).Attribute_name)){
									if(patternset.Child_name.equals(judgment_set.get(y).Child_name)){
										if(patternset.Parent_name.equals(judgment_set.get(y).Parent_name)){
											match=1;
										}
									}
								}
							}
							if(match==0)judgment_set.add(patternset);
						}
					}
				}
			}
			
			if(loop_num>3){
				for(int x=0;x<loop_num -3 ;x++){//ループ数4以上では孫以降の継承情報も必要
				
					int setSize = judgment_set.size(); //初期サイズを記録して探索に使用
					for(int j=0;j<setSize;j++){
						Integer c_name = judgment_set.get(j).Child_name;
						for(int k=0;k<setSize;k++){
							if(j!=k){
								if(c_name.equals(judgment_set.get(k).Parent_name)){
									
									Integer pa = judgment_set.get(j).Parent_name;
									Integer ch = judgment_set.get(k).Child_name;
									String at = new String(judgment_set.get(j).Attribute_name);
									RelationPattern patternset = new RelationPattern(pa,ch,at);
									
									int match=0;
									for(int y=0;y<judgment_set.size();y++){
										if(patternset.Attribute_name.equals(judgment_set.get(y).Attribute_name)){
											if(patternset.Child_name.equals(judgment_set.get(y).Child_name)){
												if(patternset.Parent_name.equals(judgment_set.get(y).Parent_name)){
													match=1;
												}
											}
										}
									}
									if(match==0)judgment_set.add(patternset);
								}
							}
						}
					}
				}
			}

			int count=0;
			for(int j=0; j<judgment_set.size();j++){
				for(int k=0;k<inputList.size();k++){
					if(judgment_set.get(j).Parent_name.equals(inputList.get(k).Parent_name)){
						if(judgment_set.get(j).Child_name.equals(inputList.get(k).Child_name)){
							
							if(judgment_set.get(j).Attribute_name.equals(inputList.get(k).Attribute_name)){
								count++;//属性まで完全に一致でカウント
							}
							
							//入力側の属性がnullの場合, 判定した子要素が他にnull以外の親を持っているか入力を探索.
							if(inputList.get(k).Attribute_name.equals("null")){
								int accept_flag=0; 
								ArrayList<RelationPattern> not_nullSet = new ArrayList<RelationPattern>();
								for(int x=0;x<inputList.size();x++){
									//入力を探索し,null以外の関係がある親が存在しているかどうかを調べる.
									if(judgment_set.get(j).Child_name.equals(inputList.get(x).Child_name)){
										if(!inputList.get(x).Attribute_name.equals("null")){
											not_nullSet.add(inputList.get(x));//not_nullSetに格納
										}
									}
								}
								//null以外の関係がある場合,not_nullSetの１つがpattern_setにあるか探索
								if(not_nullSet.size() != 0){
									for(int x=0;x<not_nullSet.size();x++){
										for(int y=0;y<judgment_set.size();y++){
											if(judgment_set.get(y).Attribute_name.equals(not_nullSet.get(x).Attribute_name)){
												if(judgment_set.get(y).Parent_name.equals(not_nullSet.get(x).Parent_name)){
													if(judgment_set.get(y).Child_name.equals(not_nullSet.get(x).Child_name)){
														accept_flag=1;
													}
												}
											}
										}
									}
								} else{
									accept_flag=1;
								}
								if(accept_flag == 1){
									if(!judgment_set.get(j).Attribute_name.equals("inner")){
										count++;//innerでなければcount;		
									}
								}								
							}
						}
					}
				}
			}
			
			if(count==judgment_set.size()){
				//全ての関係が含まれている場合、適切な構造と見なす
			
				loopStructure=fixed_inputList;//１つ見つかった段階で終了
				 return true;
			}else {
				return false;
			}

		
	}

	public ArrayList<Integer> make_separateNodeList(ArrayList<RelationPattern> inputList, HashMap<Integer, String> m_indexList) {
		
		Set<Integer> loop_nameSet = m_indexList.keySet();
		Integer[] loop_name = loop_nameSet.toArray(new Integer[0]);
		
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

	public ArrayList<RelationPattern> remove_NullRelation(ArrayList<RelationPattern> inputList) {
		
		ArrayList<RelationPattern> inputList_new = new ArrayList<RelationPattern>();
		
		for(int i=0;i<inputList.size();i++){
			//nullでない関係情報のみを抽出する
			if(!inputList.get(i).Attribute_name.equals("null")){
				inputList_new.add(inputList.get(i));
			}			
		}
		
		return inputList_new;
	}
	

	
	public ArrayList<RelationPattern> remove_inhPath(ArrayList<RelationPattern> inputList, ArrayList<Integer[]> loop_namePair){
		ArrayList<RelationPattern> inputList_new =  new ArrayList<RelationPattern>(inputList);
		
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
	
	
	
	
	public void search_Child(ArrayList<RelationPattern> inputList,int i,int j,ArrayList<Integer> nameList){
		
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
	
	public ArrayList<RelationPattern> fix_PartialDependency(ArrayList<RelationPattern> inputList){
		//nullをもつ親子のペアに対して、null以外をもつものが見つかればnullをもつ情報は削除
 		
		ArrayList<RelationPattern> inputList_new= new ArrayList<RelationPattern>();
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
	
	public ArrayList<RelationPattern> make_inputList(HashMap<Integer,HashMap<Integer,String>> AttrList,
			Vector<Integer> equOderVec, HashMap<Integer, String> IndexList){
//			HashMap<Integer,String> loopAttrList, Integer[] loop_name){
		
		//AttrListからInputListを作成するメソッド(数式がランダムでも対応する)
		//AttrListは番号1からスタートしていると仮定している。
		
		int loop_num= IndexList.size();	//ループの数
		
		//ループ変数nのリスト上にある全てのinitを探索する。（複数の場合は並列ループ）
		//initの属性を記録し、nのリスト上にその属性でinnerとfinalが各1つ以上あるか判定
		//見つかれば子要素に追加する。（nullであってもすべて）
		
		ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
		
		for(int i=0;i<loop_num;i++){
			for(int j=0;j<equOderVec.size();j++){
				int equNum1 = equOderVec.get(j);
				if(AttrList.get(equNum1).get(i).equals("init")){
					for(int k=0;k<loop_num;k++){
						
						if(k!=i){
							String init_attr = new String(AttrList.get(equNum1).get(k));
							int init_count=0;
							int inner_count=0;
							int final_count=0;
							for(int m=0;m<equOderVec.size();m++){
								int equNum2 = equOderVec.get(m);
								if(AttrList.get(equNum2).get(k).equals(init_attr)){
									if(AttrList.get(equNum2).get(i).equals("init")) init_count++;
									if(AttrList.get(equNum2).get(i).equals("inner")) inner_count++;
									if(AttrList.get(equNum2).get(i).equals("final")) final_count++;
								}		
							}
							if(init_count!=0 &&  inner_count!=0 && final_count!=0){								
								inputList.add(new RelationPattern(k,i,init_attr));
							}
						}
					}
				}
			}			
		}
		return inputList;
	}
	
	public ArrayList<RelationPattern> AssignInnerDependency(ArrayList<RelationPattern> inputList, Integer[] loopNameList) {
		
	
		//innerを複数持っているノードをリスト化する.
		ArrayList<Integer> multiInnerNode = new ArrayList<Integer>();
		for(int i=0;i<loopNameList.length;i++){
			int innerCount=0;
			for(int j=0;j<inputList.size();j++){
				if(loopNameList[i].equals(inputList.get(j).Parent_name)){
					if(inputList.get(j).Attribute_name.equals("inner")) innerCount++;
				}
			}
			if(innerCount>1) multiInnerNode.add(loopNameList[i]);
		}
		
		ArrayList<Integer> innerchildList;
		ArrayList<Integer> innerchildNullList;
		ArrayList<Integer> innerchildNullList_new;
		ArrayList<Integer> innerchildInnerList;
		ArrayList<RelationPattern> innerElements;
		
		
		if(multiInnerNode.size()!=0){
			for(int i=0;i<multiInnerNode.size();i++){

				//inner子要素を全て格納する
				innerchildList = new ArrayList<Integer>();
				for(int j=0;j<inputList.size();j++){
					if(inputList.get(j).Parent_name.equals(multiInnerNode.get(i))){
						if(inputList.get(j).Attribute_name.equals("inner")){
							innerchildList.add(inputList.get(j).Child_name);
						}
					}
				}
				
				//inner子要素だけで構成される構造要素を抽出する
				innerElements = new ArrayList<RelationPattern>();
				for(int j=0;j<inputList.size();j++){
					boolean f1 = false;
					boolean f2 = false;
					for(int k=0;k<innerchildList.size();k++){
						if(inputList.get(j).Parent_name.equals(innerchildList.get(k))) f1=true;
						else if(inputList.get(j).Child_name.equals(innerchildList.get(k))) f2=true;
					}
					if(f1 && f2){
						innerElements.add(inputList.get(j));
					}
				}
				
				//null関係が存在する場合,そのノードを格納する.
				innerchildNullList = new ArrayList<Integer>();
				for(int j=0;j<innerElements.size();j++){
					if(innerElements.get(j).Attribute_name.equals("null")){
						boolean overlap_p =false;
						boolean overlap_c =false;
						for(int k=0;k<innerchildNullList.size();k++){
							if(innerchildNullList.get(k).equals(innerElements.get(j).Parent_name)) overlap_p = true;
							else if(innerchildNullList.get(k).equals(innerElements.get(j).Child_name)) overlap_c = true;
						}
						if(!overlap_p){
							innerchildNullList.add(innerElements.get(j).Parent_name);
						}
						if(!overlap_c){
							innerchildNullList.add(innerElements.get(j).Child_name);
						}
					}
				}
				
				
				if(innerchildNullList.size()!=0){
					
					//innerの中にnullが存在する場合,pre/postを割り当てる必要がある.
					//innerの子要素になっているノードにpre/postを割り当てることはできないので,nullのリストから外す.
					innerchildInnerList = new ArrayList<Integer>();
					for(int j=0;j<innerElements.size();j++){
						if(innerElements.get(j).Attribute_name.equals("inner")){
							boolean overlap = false;
							for(int k=0;k<innerchildInnerList.size();k++){
								if(innerchildInnerList.get(k).equals(innerElements.get(j).Child_name)) overlap = true;

							}
							if(!overlap){
								innerchildInnerList.add(innerElements.get(j).Child_name);
							}
						}
					}

					innerchildNullList_new = new ArrayList<Integer>();
					for(int j=0;j<innerchildNullList.size();j++){
						boolean flag =false;
						for(int k=0;k<innerchildInnerList.size();k++){
							if(innerchildInnerList.get(k).equals(innerchildNullList.get(j))) flag = true;
						}
						if(!flag){
							innerchildNullList_new.add(innerchildNullList.get(j));
						}
					}
					

					//pre/postを割り当てる
					for(int j=0;j<innerchildNullList_new.size();j++){
						boolean flag=false;
						for(int k=0;k<innerchildNullList_new.size();k++){
							
							if(j!=k){
								for(int n=0;n<innerElements.size();n++){
									if(innerElements.get(n).Parent_name.equals(innerchildNullList_new.get(j))){
										if(innerElements.get(n).Child_name.equals(innerchildNullList_new.get(k))){
											if( ! (innerElements.get(n).Attribute_name.equals("null")) ){
												flag = true;
											}
										}
									}
									
									if(innerElements.get(n).Parent_name.equals(innerchildNullList_new.get(k))){
										if(innerElements.get(n).Child_name.equals(innerchildNullList_new.get(j))){
											if( ! (innerElements.get(n).Attribute_name.equals("null")) ){
												flag = true;
											}
										}
									}
								}
								if(!flag){
									RelationPattern pattern = new RelationPattern(innerchildNullList_new.get(j),innerchildNullList_new.get(k),"pre");
									inputList.add(pattern);
									innerElements.add(pattern);
									RelationPattern pattern2 = new RelationPattern(innerchildNullList_new.get(k),innerchildNullList_new.get(j),"post");
									inputList.add(pattern2);
									innerElements.add(pattern2);
								}	
							}	
						}
					}
				}

			}
		}
		
		return inputList;
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
}

	
	

