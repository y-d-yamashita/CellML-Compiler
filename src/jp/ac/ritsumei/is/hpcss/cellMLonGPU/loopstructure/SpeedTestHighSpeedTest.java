package jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure;


/**
 * This class is the test to decide Loop Structure　High-Speed version.
 *  
 * @author n-washio
 *
 */

import java.util.*;

//修正 2013/1/31 ループ数10の一般的な多重ループテスト

public class SpeedTestHighSpeedTest {

	public static ArrayList<Integer> path_length;
	public static ArrayList<String[]> prepostList = new  ArrayList<String[]>();
	
	public static ArrayList<Integer[]> loop_nameList = new ArrayList<Integer[]>();
	public static ArrayList<Integer[]> loop_nameList_inv = new ArrayList<Integer[]>();
	
	public static ArrayList<RelationPattern> loopStructure= new ArrayList<RelationPattern>();
	public static HashMap<Integer,Integer> connectableNode = new HashMap<Integer,Integer>();
	public static ArrayList<RelationPattern>inh_set = new ArrayList<RelationPattern>();
	
	public static void main(String[] args) {
		//---------------------------------------------------
		//テスト　main　クラス
		//入力：	属性情報リストHashMap<Integer,HashMap<String,String>> AttrList
		//---------------------------------------------------		

		ArrayList<RelationPattern>inputList = new ArrayList<RelationPattern>();
		RelationPattern pattern;
		pattern = new RelationPattern(1,2,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(1,3,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(1,4,"post");
		inputList.add(pattern);	
		pattern = new RelationPattern(1,5,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(1,6,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(1,7,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(1,8,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(1,9,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(1,10,"post");
		inputList.add(pattern);
		
		pattern = new RelationPattern(2,3,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(2,4,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(2,5,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(2,6,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(2,7,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(2,8,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(2,9,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(2,10,"post");
		inputList.add(pattern);
		
		pattern = new RelationPattern(3,4,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(3,5,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(3,6,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(3,7,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(3,8,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(3,9,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(3,10,"post");
		inputList.add(pattern);
		
		pattern = new RelationPattern(4,1,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(4,2,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(4,3,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(4,5,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(4,6,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(4,7,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(4,8,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(4,9,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(4,10,"post");
		inputList.add(pattern);
		
		pattern = new RelationPattern(5,1,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(5,2,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(5,3,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(5,6,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(5,7,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(5,8,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(5,9,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(5,10,"post");
		inputList.add(pattern);
		
		pattern = new RelationPattern(6,1,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(6,2,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(6,3,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(6,7,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(6,8,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(6,9,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(6,10,"post");
		inputList.add(pattern);
		
		pattern = new RelationPattern(7,1,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(7,2,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(7,3,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(7,8,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(7,9,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(7,10,"post");
		inputList.add(pattern);
		
		pattern = new RelationPattern(8,1,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(8,2,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(8,3,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(8,4,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(8,5,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(8,6,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(8,7,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(8,9,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(8,10,"inner");
		inputList.add(pattern);
		
		pattern = new RelationPattern(9,1,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(9,2,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(9,3,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(9,4,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(9,5,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(9,6,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(9,7,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(9,10,"post");
		inputList.add(pattern);
		
		pattern = new RelationPattern(10,1,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(10,2,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(10,3,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(10,4,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(10,5,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(10,6,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(10,7,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(10,9,"pre");
		inputList.add(pattern);
		
		Integer[] loop_name = {1,2,3,4,5,6,7,8,9,10};
		
		
		
		
		
		//計測開始
		long start = System.currentTimeMillis();
		
		inputList= removeOverlap(inputList);
		//---------------------------------------------------
		//入力情報に対するnull処理
		//---------------------------------------------------
		
		inputList=fix_PartialDependency(inputList);
		
		
		//---------------------------------------------------
		//入力情報に対するinner子要素の処理
		//あるノードのinner子要素がnullである場合,pre/postを割り当てる.
		//---------------------------------------------------
		
		inputList=AssignInnerDependency(inputList,loop_name);
		
		
		//---------------------------------------------------
		//入力情報に対するnull処理２
		//---------------------------------------------------
		ArrayList<Integer> separateNodeList = new ArrayList<Integer>();
		separateNodeList=make_separateNodeList(inputList,loop_name);
		
		inputList=remove_NullRelation(inputList);
		
		
		if(inputList.size()==0){
			//nullを削除した際に全て独立ノードで入力が消滅した場合,骨格の関係を作成
			pattern = new RelationPattern(loop_name[0],loop_name[1],"post");
			inputList.add(pattern);
			pattern = new RelationPattern(loop_name[1],loop_name[0],"pre");
			inputList.add(pattern);
			separateNodeList.remove(loop_name[0]);
			separateNodeList.remove(loop_name[1]);
		}
		
		
		//名前の順列を格納
		set_nameList(0, new int [loop_name.length], new boolean [loop_name.length],loop_name);
		
		
		//リーフノードを格納(inner子要素)
		ArrayList<Integer> leafList = new ArrayList<Integer>();
		for(int a=0;a<inputList.size();a++){
			if(inputList.get(a).Attribute_name.equals("inner")){
				
				boolean flag=false;
				for(int b=0;b<leafList.size();b++){
					if(leafList.get(b)==inputList.get(a).Child_name)flag=true;
				}
				if(!flag)leafList.add(inputList.get(a).Child_name);
			}
		}

		
		boolean findFlag = false;
		for(int s=0;s<loop_nameList.size();s++){
						
			boolean ignore_flag = false;

			//inner条件から削減可能
			boolean ignore = false;
			
			for(int j=0;j<inputList.size();j++){
				if(inputList.get(j).Attribute_name.equals("inner")){
					boolean child = false;
					boolean flag = false;
							
					//childに対してparentが先行して配置される要素を削除
					for(int k=0;k<loop_nameList.get(s).length;k++){
								
						if(loop_nameList.get(s)[k].equals(inputList.get(j).Child_name)){
							child=true;
						}
						if(loop_nameList.get(s)[k].equals(inputList.get(j).Parent_name)){
							if(!child) flag = true;break;
						}
					}
							
					if(flag){
						ignore=true;
						break;
					}
				}	
			}
			if(ignore){
				ignore_flag=true;
			}
			
			
			for(int g=0;g<loop_nameList.size();g++){
				
				if(ignore_flag)break;
				//start順列の末尾とgoal順列の先頭(すなわちリーフノードでない部分)は一致しているという条件でも決定に十分
				int rootFactorSize = loop_name.length - leafList.size();
				
				int count=0;
				for(int i=0;i<rootFactorSize;i++){
					if(loop_nameList.get(g)[i]==loop_nameList.get(s)[(loop_name.length-1) - i ]){
						count++;
					}
				}
				
				if(count==rootFactorSize){
					
				
					ArrayList<Integer[]> Pair_Pattern = new ArrayList<Integer[]>();
					
					
					Pair_Pattern.add(loop_nameList.get(s));
					Pair_Pattern.add(loop_nameList.get(g));

					
					ArrayList<RelationPattern> inputList_new = new ArrayList<RelationPattern>();
		    		
		    		//見つかるまで削除手順を変えて繰り返す.
					inputList_new= new ArrayList<RelationPattern>();
					
					//---------------------------------------------------
					//継承関係の削除
					//---------------------------------------------------
					inputList_new = remove_inhPath(inputList, Pair_Pattern);
					
					//---------------------------------------------------
					//双方向パスの処理
					//---------------------------------------------------
					
					ArrayList<Integer> pre_num = new ArrayList<Integer>();
					for(int j=0;j<inputList_new.size();j++){
						if(inputList_new.get(j).Attribute_name.equals("pre")) pre_num.add(j);
					}
					
					make_prepostList(0, new Integer[pre_num.size()]);

					inputList_new = fix_interactivePath(inputList_new);
					
					if(inputList_new!=null && check_inh(inputList_new,inputList)){
						
						//継承判定をして矛盾しなければbreak
						for(int l=0;l<loop_nameList.get(s).length;l++){
							System.out.print(loop_nameList.get(s)[l]);
						}System.out.println();
						
						
						loopStructure=inputList_new;
						findFlag=true;
						System.out.println("find");
						break;
					}
					else{
						prepostList.clear();
						inh_set.clear();
					}
				}
	
			}
			if(findFlag)break;
		}
		//---------------------------------------------------
		//null置換
		//---------------------------------------------------
		
		
		loopStructure=get_LoopStructure(loopStructure,separateNodeList);
		
		System.out.println("決定されたループ構造（Loop_Structure）----------------");
		if(loopStructure!=null){
			for(int i=0;i<loopStructure.size();i++){
				loopStructure.get(i).printContents();
			}
		}
		
		System.out.println();
		long stop = System.currentTimeMillis();
		System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
	
		
	}	

	public static void add_inh(Integer root, String attribute, Integer child, ArrayList<RelationPattern> fixed_inputList) {
		
		
		//継承関係追加
		RelationPattern pattern_inh = new RelationPattern(root,child,attribute);
		inh_set.add(pattern_inh);
		
		for(int i=0;i<fixed_inputList.size();i++){
			if(child.equals(fixed_inputList.get(i).Parent_name)){
				add_inh(root,attribute,fixed_inputList.get(i).Child_name,fixed_inputList);
			}
		}
	}
		
		
	
	public static boolean check_inh(ArrayList<RelationPattern> fixed_inputList,ArrayList<RelationPattern> inputList) {
		
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


	public static ArrayList<Integer> make_separateNodeList(ArrayList<RelationPattern> inputList, Integer[] loop_name) {
		
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

	public static ArrayList<RelationPattern> get_LoopStructure(ArrayList<RelationPattern> inputList,ArrayList<Integer> nullNodeList){

		ArrayList<RelationPattern> LoopStructure= new ArrayList<RelationPattern>(inputList);

		
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
				LoopStructure.add(relation);
			}
			RelationPattern relation;
			if(nullNodeList.size()!=0){
				//rootリストの最後のツリーの接続可能箇所に独立ノードを接続
				Integer last_root=connectableNode.get(root_nameList.get(root_nameList.size()-1));
				relation = new RelationPattern(last_root,nullNodeList.get(0),"post");
				LoopStructure.add(relation);
				if(nullNodeList.size()>1){
					//独立ノード同士は必ず接続できる
					for(int i=1;i<nullNodeList.size();i++){
						relation = new RelationPattern(nullNodeList.get(i-1),nullNodeList.get(i),"post");
						LoopStructure.add(relation);
					}
				}
			}
		}
		
		return LoopStructure;
	}
	
	public static void search_ConnectableNode(Integer root_name,Integer My_name, ArrayList<RelationPattern> inputList){
		
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
	


	public static ArrayList<RelationPattern> remove_NullRelation(ArrayList<RelationPattern> inputList) {
		
		ArrayList<RelationPattern> inputList_new = new ArrayList<RelationPattern>();
		
		for(int i=0;i<inputList.size();i++){
			//nullでない関係情報のみを抽出する
			if(!inputList.get(i).Attribute_name.equals("null")){
				inputList_new.add(inputList.get(i));
			}			
		}
		
		return inputList_new;
	}	
	public static ArrayList<RelationPattern> fix_interactivePath(ArrayList<RelationPattern> inputList) {
		

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
	
	public static ArrayList<RelationPattern> remove_inhPath(ArrayList<RelationPattern> inputList, ArrayList<Integer[]> loop_namePair){
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
	
	
	
	
	public static void search_Child(ArrayList<RelationPattern> inputList,int i,int j,ArrayList<Integer> nameList){
		
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
	
	public static ArrayList<RelationPattern> fix_PartialDependency(ArrayList<RelationPattern> inputList){
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
	
	public static ArrayList<RelationPattern> make_inputList(HashMap<Integer,HashMap<Integer,String>> AttrList,
			HashMap<Integer,String> loopAttrList, Integer[] loop_name){
		
		//AttrListからInputListを作成するメソッド(数式がランダムでも対応する)
		//AttrListは番号1からスタートしていると仮定している。
		
		int val_num= AttrList.size();		//変数の数
		int loop_num= loopAttrList.size();	//ループの数
		
		//ループ変数nのリスト上にある全てのinitを探索する。（複数の場合は並列ループ）
		//initの属性を記録し、nのリスト上にその属性でinnerとfinalが各1つ以上あるか判定
		//見つかれば子要素に追加する。（nullであってもすべて）
		
		ArrayList<RelationPattern>  inputList = new ArrayList<RelationPattern>();
		
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
								inputList.add(new RelationPattern(loop_name[k],loop_name[i],init_attr));
							}
						}
					}
				}
			}			
		}
		return inputList;
	}
	public static ArrayList<RelationPattern> AssignInnerDependency(ArrayList<RelationPattern> inputList, Integer[] loopNameList) {
		
		
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
	
	public static ArrayList<RelationPattern> removeOverlap(ArrayList<RelationPattern> inputList) {
		
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
	
}

	
	

