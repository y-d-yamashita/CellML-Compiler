package jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure;

/**
 * This class is used "Labelattr.java" to decide Loop Structure for Structured RecML generator.
 * This is a high-speed version about Decision Loop Structure. 
 * 
 * @author n-washio
 * 
 */

import java.util.*;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;

//クラス化　2012/11/14
public class HighSpeedDecisionLoopStructure2 {

	public ArrayList<Integer> path_length;

	public ArrayList<RelationPattern> loopStructure= new ArrayList<RelationPattern>();
	//public HashMap<Integer,Integer> connectableNode = new HashMap<Integer,Integer>();
	
	
	public void toRecMLheader(ArrayList<RelationPattern> LoopStructure){
		
		//loopが1つの場合
		if(LoopStructure.size() == 1){
			if(LoopStructure.get(0).Child_name == -1 && LoopStructure.get(0).Attribute_name.length() == 0){
				System.out.println("<loopstruct name="+ "\""+LoopStructure.get(0).Parent_name+ "\""+">");
			}
		}else{
			
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
	}
	
	public void call_child(ArrayList<RelationPattern> LoopStructure,Integer My_name, int tab_count){
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

	public boolean check_inh(ArrayList<RelationPattern> fixed_inputList,ArrayList<RelationPattern> inputList) {
		
		//継承関係を含むセットを作成し、全て入力セットに含まれていれば矛盾しない構造であると見なす
		
		ArrayList<RelationPattern>inh_set = new ArrayList<RelationPattern>();
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
						RelationPattern pattern = new RelationPattern(p,c,a);
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
								RelationPattern pattern = new RelationPattern(p,c,a);
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
	
	public ArrayList<RelationPattern> innerDependency(ArrayList<RelationPattern> inputList, Integer[] loopNameList) {
		
		
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
								
								if(j!=innerchildNullList_new.size()-1){
									RelationPattern pattern = new RelationPattern(innerchildNullList_new.get(j),innerchildNullList_new.get(j+1),"pre");
									inputList.add(pattern);
									RelationPattern pattern2 = new RelationPattern(innerchildNullList_new.get(j+1),innerchildNullList_new.get(j),"post");
									inputList.add(pattern2);
								}

							}
						}

					}
				}
				
				return inputList;
	}
}

	
	

