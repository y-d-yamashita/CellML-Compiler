package jp.ac.ritsumei.is.hpcss.cellMLcompiler.loopStructure;

/**
 * This class is the test to decide Loop Structure.
 *  
 * @author n-washio
 *
 */


import java.util.*;

//修正版v6 2012/11/5
public class DecisionLoopStructureTest {
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
		
		pattern = new RelationPath(1,2,"inner");
		inputList.add(pattern);
		pattern = new RelationPath(1,3,"null");
		inputList.add(pattern);
		pattern = new RelationPath(1,4,"null");
		inputList.add(pattern);
		pattern = new RelationPath(2,3,"null");
		inputList.add(pattern);
		pattern = new RelationPath(2,4,"null");
		inputList.add(pattern);
		pattern = new RelationPath(3,1,"null");
		inputList.add(pattern);
		pattern = new RelationPath(3,2,"null");
		inputList.add(pattern);
		pattern = new RelationPath(3,4,"inner");
		inputList.add(pattern);
		pattern = new RelationPath(4,1,"null");
		inputList.add(pattern);
		pattern = new RelationPath(4,2,"null");
		inputList.add(pattern);
		
		Integer[] loop_name = {1,2,3,4};
		HashMap<Integer, String> m_indexList = new HashMap<Integer, String>();
		m_indexList.put(1, "a");
		m_indexList.put(2, "b");
		m_indexList.put(3, "c");
		m_indexList.put(4, "d");
		
		int loop_num=loop_name.length;
		//計測開始		
		long start = System.currentTimeMillis();
		
		//---------------------------------------------------
		//入力情報に対するnull処理
		//---------------------------------------------------
		
		inputList=fix_PartialDependency(inputList);
		
		//---------------------------------------------------
		//関係情報の全パターン列挙
		//---------------------------------------------------
		ArrayList<RelationPath> relationList = new ArrayList<RelationPath>();
		relationList = get_relationList(m_indexList);

		//---------------------------------------------------
		//関係情報の組み合わせパターン列挙
		//関係リストのパターンからループ数-1を選ぶ組み合わせをリスト化する
		//---------------------------------------------------
		ArrayList<ArrayList<RelationPath>> combinationList = new ArrayList<ArrayList<RelationPath>>();	
		combinationList = get_combinationList(relationList,m_indexList);
		
		
		//---------------------------------------------------
		//関係情報の組み合わせパターンの削減（１）
		//ループの種類を網羅していない要素を削除する
		//---------------------------------------------------
		
		combinationList = reduce_combinationList_1st(combinationList,loop_num);


		//---------------------------------------------------
		//関係情報の組み合わせパターンの削減（２）
		//複数の異なる親をもつ子が存在するものを削除
		//---------------------------------------------------
				
		combinationList = reduce_combinationList_2nd(combinationList,loop_num);
				
		//---------------------------------------------------
		//関係情報の組み合わせパターンの削減（３）
		//複数の同じ属性を親がもつ組み合わせを削除
		//---------------------------------------------------
				
		combinationList = reduce_combinationList_3rd(combinationList,loop_num);	
		
		//---------------------------------------------------
		//ループ構造の決定
		//孫以降へ継承する情報を入力情報と比較し、矛盾があれば削除
		//---------------------------------------------------

		ArrayList<RelationPath> LoopStructure = new ArrayList<RelationPath>();
		LoopStructure = get_LoopStructure(combinationList,loop_num, inputList);	
		
		System.out.println("決定されたループ構造（Loop_Structure）----------------");
		if(LoopStructure!=null){
			for(int i=0;i<LoopStructure.size();i++){
				LoopStructure.get(i).printContents();
			}
		}
		System.out.println();
		
		
		//toRecMLheader(LoopStructure);
		long stop = System.currentTimeMillis();
		System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
		//mainメソッド終了
		//System.out.println("finish");
		
	}	
	
	public static void toRecMLheader(ArrayList<RelationPath> LoopStructure){
		
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
	
	
	public static ArrayList<RelationPath> get_LoopStructure(ArrayList<ArrayList<RelationPath>> combinationList, int indexListsize, ArrayList<RelationPath>  inputList){
		
		//継承関係を含めて入力情報と比較し、矛盾があれば削除
		ArrayList<RelationPath> loopStructure = new ArrayList<RelationPath>();
		ArrayList<RelationPath> judgment_set;
		
		for(int i=0;i<combinationList.size();i++){
			judgment_set = new ArrayList<RelationPath>();

			for(int j=0;j<indexListsize-1;j++){
				judgment_set.add(combinationList.get(i).get(j));	//継承以外の情報も格納する
				Integer c_name = combinationList.get(i).get(j).Child_name;
				for(int k=0;k<indexListsize-1;k++){
					if(j!=k){
						if(c_name.equals(combinationList.get(i).get(k).Parent_name)){
							
							Integer p = combinationList.get(i).get(j).Parent_name;
							Integer c = combinationList.get(i).get(k).Child_name;
							String a = new String(combinationList.get(i).get(j).Attribute_name);
							RelationPath pattern = new RelationPath(p,c,a);
							
							int match=0;
							for(int y=0;y<judgment_set.size();y++){
								if(pattern.Attribute_name.equals(judgment_set.get(y).Attribute_name)){
									if(pattern.Child_name.equals(judgment_set.get(y).Child_name)){
										if(pattern.Parent_name.equals(judgment_set.get(y).Parent_name)){
											match=1;
										}
									}
								}
							}
							if(match==0)judgment_set.add(pattern);
						}
					}
				}
			}
			
			if(indexListsize>3){
				for(int x=0;x<indexListsize-3;x++){//ループ数4以上では曾孫以降の継承情報も必要
				
					int setSize = judgment_set.size(); //初期サイズを記録して探索に使用
					for(int j=0;j<setSize;j++){
						Integer c_name = judgment_set.get(j).Child_name;
						for(int k=0;k<setSize;k++){
							if(j!=k){
								if(c_name.equals(judgment_set.get(k).Parent_name)){
									
									Integer p = judgment_set.get(j).Parent_name;
									Integer c = judgment_set.get(k).Child_name;
									String a = new String(judgment_set.get(j).Attribute_name);
									RelationPath pattern = new RelationPath(p,c,a);
									
									int match=0;
									for(int y=0;y<judgment_set.size();y++){
										if(pattern.Attribute_name.equals(judgment_set.get(y).Attribute_name)){
											if(pattern.Child_name.equals(judgment_set.get(y).Child_name)){
												if(pattern.Parent_name.equals(judgment_set.get(y).Parent_name)){
													match=1;
												}
											}
										}
									}
									if(match==0)judgment_set.add(pattern);
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
								ArrayList<RelationPath> not_nullSet = new ArrayList<RelationPath>();
								for(int m=0;m<inputList.size();m++){
									//入力を探索し,null以外の関係がある親が存在しているかどうかを調べる.
									if(judgment_set.get(j).Child_name.equals(inputList.get(m).Child_name)){
										if(!inputList.get(m).Attribute_name.equals("null")){
											not_nullSet.add(inputList.get(m));//not_nullSetに格納
										}
									}
								}
								//null以外の関係がある場合,not_nullSetの１つがjudgment_setにあるか探索
								if(not_nullSet.size() != 0){
									for(int m=0;m<not_nullSet.size();m++){
										for(int n=0;n<judgment_set.size();n++){
											if(judgment_set.get(n).Attribute_name.equals(not_nullSet.get(m).Attribute_name)){
												if(judgment_set.get(n).Parent_name.equals(not_nullSet.get(m).Parent_name)){
													if(judgment_set.get(n).Child_name.equals(not_nullSet.get(m).Child_name)){
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
				loopStructure = combinationList.get(i);//１つ見つかった段階で終了
				break;
			}
		}
		return loopStructure;
		
	}
	
	public static ArrayList<ArrayList<RelationPath>> reduce_combinationList_3rd(ArrayList<ArrayList<RelationPath>> combinationList, int indexListsize){
		
		//複数の同じ属性を親がもつ組み合わせを削除
		
		ArrayList<ArrayList<RelationPath>> combList_new = new ArrayList<ArrayList<RelationPath>>();
		ArrayList<Integer> p_nameList;
		ArrayList<String> a_nameList;
		//各組み合わせの親の名前を重複チェックしながら探索
		//各親に対して属性を探索し、属性の重複があればフラグを立てて削除する。
		
		for(int i=0;i<combinationList.size();i++){
			p_nameList = new ArrayList<Integer>();
			
			for(int j=0;j<indexListsize-1;j++){
				//親の名前を重複しないように登録
				int match =0;
				for(int x=0;x<p_nameList.size();x++){
					if(combinationList.get(i).get(j).Parent_name.equals(p_nameList.get(x))){
						match=1;
					}
				}
				if(match==0) p_nameList.add(combinationList.get(i).get(j).Parent_name);
			}
			
			int flag=0;
			for(int k=0;k<p_nameList.size();k++){
				//各親の名ごとに探索し、属性要素が重複していたらフラグを立てる。
				
				a_nameList= new ArrayList<String>();
				for(int m=0;m<indexListsize-1;m++){
					if(combinationList.get(i).get(m).Parent_name.equals(p_nameList.get(k))){
						int match=0;
						for(int x=0;x<a_nameList.size();x++){
							if(combinationList.get(i).get(m).Attribute_name.equals(a_nameList.get(x))){
								match=1;
							}
						}
						if(match==0)a_nameList.add(combinationList.get(i).get(m).Attribute_name);
						else flag=1;
					}
				}
			}
			if(flag!=1)combList_new.add(combinationList.get(i));
			
		}
		
		
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPath>> reduce_combinationList_2nd(ArrayList<ArrayList<RelationPath>> combinationList, int indexListsize){
		
		//複数の異なる親をもつ子が存在するものを削除
		//親が複数あるかどうか＝子要素の名前の重複をチェックすれば十分
		
		ArrayList<ArrayList<RelationPath>> combList_new = new ArrayList<ArrayList<RelationPath>>();
		ArrayList<Integer> c_nameList;
		//各組み合わせの子の名前を重複チェックしながら探索
		//各子に対して親を探索し、親が複数あればフラグを立てて削除する。
		
		for(int i=0;i<combinationList.size();i++){
			c_nameList = new ArrayList<Integer>();
			
			for(int j=0;j<indexListsize-1;j++){
				//子の名前を重複しないように登録
				int match =0;
				for(int x=0;x<c_nameList.size();x++){
					if(combinationList.get(i).get(j).Child_name.equals(c_nameList.get(x))){
						match=1;
					}
				}
				if(match==0) c_nameList.add(combinationList.get(i).get(j).Child_name);
			}
			if(c_nameList.size()==indexListsize-1) combList_new.add(combinationList.get(i));
		}
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPath>> reduce_combinationList_1st(ArrayList<ArrayList<RelationPath>> combinationList, int indexListsize){
		
		//ループの種類を網羅していない組み合わせを削除
		ArrayList<ArrayList<RelationPath>> combList_new = new ArrayList<ArrayList<RelationPath>>();
		ArrayList<Integer> nameList;
		for(int i=0;i<combinationList.size();i++){
			
			nameList = new ArrayList<Integer>();
			
			for(int j=0;j<indexListsize-1;j++){
				//親の名前を重複しないように登録
				int match =0;
				for(int x=0;x<nameList.size();x++){
					if(combinationList.get(i).get(j).Parent_name.equals(nameList.get(x))){
						match=1;
					}
				}
				if(match==0) nameList.add(combinationList.get(i).get(j).Parent_name);
			}
			
			for(int j=0;j<combinationList.get(i).size();j++){
				//子の名前を重複しないように登録
				int match=0;
				for(int x=0;x<nameList.size();x++){
					if(combinationList.get(i).get(j).Child_name.equals(nameList.get(x))){
						match=1;
					}
				}
				if(match==0) nameList.add(combinationList.get(i).get(j).Child_name);
			}
			
			if(nameList.size()==indexListsize){
				combList_new.add(combinationList.get(i));
			}
		}
		return combList_new;
	}
	
	public static ArrayList<ArrayList<RelationPath>> get_combinationList(ArrayList<RelationPath> patternList,HashMap<Integer, String> IndexList){
		
		//pattenList.sizeから(loop_num-1)選ぶ組み合わせを列挙するメソッド
		//patternList.sizeからloop_num-1個を選ぶ
		int n = patternList.size();
		int r = IndexList.size() - 1;
	    int[] c = new int[10];
		for ( int i=0; i<10; i++ ){
			c[i] = 0;
	    }
		ArrayList<ArrayList<RelationPath>> combinationList = new ArrayList<ArrayList<RelationPath>>();
		combine_pattern(1,n,r,c,patternList, combinationList);
		
		return  combinationList;
	}
	
    public static void combine_pattern( int m ,int n,int r,int[] c,ArrayList<RelationPath> patternList,ArrayList<ArrayList<RelationPath>> combinationList) {
    	
    	if ( m <= r ) {
          for ( int i=c[m-1]+1; i<=n-r+m; i++ ){
             c[m] = i;
             combine_pattern(m+1,n,r,c,patternList,combinationList);
          }
        }
        else {
           ArrayList<RelationPath> pattern = new ArrayList<RelationPath>();
           for ( int i=1; i<=r; i++ ){
        	  pattern.add(patternList.get((c[i]-1)));
        	  //組み合わせ番号は正数の組み合わせなのでリストの格納番号（0スタート）に置き換えて取得
          }
          combinationList.add(pattern);
        }
    }
	
	
	public static ArrayList<RelationPath> get_relationList(HashMap<Integer, String> IndexList){
		
		//関係パターンの列挙を取得するメソッド
		//列挙数を計算し、その数だけstruPatt[]を返す
		
		int attr_num = 3;
		String[] attr_name ={"pre","inner","post"};
		
		ArrayList<RelationPath> patternList = new ArrayList<RelationPath>();
		for(int i=0; i<IndexList.size();i++){	
			for(int j=0; j<IndexList.size();j++){
				for(int k=0; k<attr_num;k++){
					if(i!=j){
						RelationPath pattern = new RelationPath(i,j,attr_name[k]);
						patternList.add(pattern);
					}
				}
			}
		}
		return patternList;
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
			Vector<Integer> equOderVec, HashMap<Integer, String> IndexList){
//			HashMap<Integer,String> loopAttrList, Integer[] loop_name){
		
		//AttrListからInputListを作成するメソッド(数式がランダムでも対応する)
		//AttrListは番号1からスタートしていると仮定している。
		
		int val_num= AttrList.size();		//変数の数
		int loop_num= IndexList.size();	//ループの数
		
		//ループ変数nのリスト上にある全てのinitを探索する。（複数の場合は並列ループ）
		//initの属性を記録し、nのリスト上にその属性でinnerとfinalが各1つ以上あるか判定
		//見つかれば子要素に追加する。（nullであってもすべて）
		
		ArrayList<RelationPath>  inputList = new ArrayList<RelationPath>();
		
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
								inputList.add(new RelationPath(k,i,init_attr));
							}
						}
					}
				}
			}			
		}
		return inputList;
	}	
}

	
	

