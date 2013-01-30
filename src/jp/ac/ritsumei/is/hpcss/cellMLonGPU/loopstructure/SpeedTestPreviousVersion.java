package jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure;


/**
 * This class is the test to decide Loop Structure previous version.
 *  
 * @author n-washio
 *
 */


import java.util.*;

// 2013/1/30 構造を生成する度に判定するように修正　メモリ消費量削減
public class SpeedTestPreviousVersion {
	
	public static ArrayList<RelationPattern> judgment_set = new ArrayList<RelationPattern>();
	public static ArrayList<RelationPattern> loopStructure= new ArrayList<RelationPattern>();
	
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
		pattern = new RelationPattern(3,4,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(3,5,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(3,6,"post");
		inputList.add(pattern);
		pattern = new RelationPattern(3,7,"post");
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
		pattern = new RelationPattern(6,1,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(6,2,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(6,3,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(6,7,"inner");
		inputList.add(pattern);
		pattern = new RelationPattern(7,1,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(7,2,"pre");
		inputList.add(pattern);
		pattern = new RelationPattern(7,3,"pre");
		inputList.add(pattern);
		Integer[] loop_name = {1,2,3,4,5,6,7};
		
		
		
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
		ArrayList<RelationPattern> relationList = new ArrayList<RelationPattern>();
		relationList = get_relationList(loop_num,loop_name);

		//---------------------------------------------------
		//関係情報の組み合わせパターン列挙
		//関係リストのパターンからループ数-1を選ぶ組み合わせをリスト化する
		//---------------------------------------------------
		
		get_loopStructure(relationList,loop_num,inputList);
		
		System.out.println("決定されたループ構造（Loop_Structure）----------------");
		
		for(int i=0;i<loopStructure.size();i++){
				loopStructure.get(i).printContents();
		}
		System.out.println();
		
		long stop = System.currentTimeMillis();
		System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
	}	
	
	
	
	public static void get_loopStructure(ArrayList<RelationPattern> patternList,int loop_num, ArrayList<RelationPattern> inputList){
		
		//pattenList.sizeから(loop_num-1)選ぶ組み合わせを列挙するメソッド
		//patternList.sizeからloop_num-1個を選ぶ
		int n = patternList.size();
		int r = loop_num-1;
	    int[] c = new int[10];
		for ( int i=0; i<10; i++ ){
			c[i] = 0;
	    }
		
		combine_pattern(1,n,r,c,patternList,inputList);
	}
	
    public static void combine_pattern( int m ,int n,int r,int[] c,ArrayList<RelationPattern> patternList,ArrayList<RelationPattern> inputList) {
    	
    	if ( m <= r) {
	    	if(loopStructure.size()==0){
	          for ( int i=c[m-1]+1; i<=n-r+m; i++ ){
	             c[m] = i;
	             combine_pattern(m+1,n,r,c,patternList,inputList); 
	          }
	          
	        }
    	}
        else {
           ArrayList<RelationPattern> pattern = new ArrayList<RelationPattern>();
           for ( int i=1; i<=r; i++ ){
        	  pattern.add(patternList.get((c[i]-1)));
        	  //組み合わせ番号は正数の組み合わせなのでリストの格納番号（0スタート）に置き換えて取得
           }
           
           
           //patternがループ構造を満たすか確認する.
           while(true){
        	   	
        	   
               //---------------------------------------------------
          		//関係情報の組み合わせパターンの削減（１）
          		//ループの種類を網羅していない要素を削除する
          		//---------------------------------------------------
        	   
        	   	ArrayList<Integer> nameList = new ArrayList<Integer>();
   			
	   			for(int j=0;j<r;j++){
	   				//親の名前を重複しないように登録
	   				int match =0;
	   				for(int x=0;x<nameList.size();x++){
	   					if(pattern.get(j).Parent_name.equals(nameList.get(x))){
	   						match=1;
	   					}
	   				}
	   				if(match==0) nameList.add(pattern.get(j).Parent_name);
	   			}
	   			
	   			for(int j=0;j<r;j++){
	   				//子の名前を重複しないように登録
	   				int match=0;
	   				for(int x=0;x<nameList.size();x++){
	   					if(pattern.get(j).Child_name.equals(nameList.get(x))){
	   						match=1;
	   					}
	   				}
	   				if(match==0) nameList.add(pattern.get(j).Child_name);
	   			}
	   			
	   			if(nameList.size()==r+1){

	   			}else{
	   				break;
	   			}
	   			
	   	   		//---------------------------------------------------
	   	   		//関係情報の組み合わせパターンの削減（２）
	   	   		//複数の異なる親をもつ子が存在するものを削除
	   	   		//---------------------------------------------------
	   			
	   			ArrayList<Integer> c_nameList;
	   			c_nameList = new ArrayList<Integer>();
   				
   				for(int j=0;j<r;j++){
   					//子の名前を重複しないように登録
   					int match =0;
   					for(int x=0;x<c_nameList.size();x++){
   						if(pattern.get(j).Child_name.equals(c_nameList.get(x))){
   							match=1;
   						}
   					}
   					if(match==0) c_nameList.add(pattern.get(j).Child_name);
   				}
   				if(c_nameList.size()==r){

   				}else{
   					break;
   				}
   				
   				
   				
   		   		//---------------------------------------------------
   		   		//関係情報の組み合わせパターンの削減（３）
   		   		//複数の同じ属性を親がもつ組み合わせを削除
   		   		//---------------------------------------------------
   		   			
   				
   				ArrayList<Integer> p_nameList = new ArrayList<Integer>();
   				
   				for(int j=0;j<r;j++){
   					//親の名前を重複しないように登録
   					int match =0;
   					for(int x=0;x<p_nameList.size();x++){
   						if(pattern.get(j).Parent_name.equals(p_nameList.get(x))){
   							match=1;
   						}
   					}
   					if(match==0) p_nameList.add(pattern.get(j).Parent_name);
   				}
   				
   				boolean flag=false;
   				for(int k=0;k<p_nameList.size();k++){
   					//各親の名ごとに探索し、属性要素が重複していたらフラグを立てる。
   					
   					ArrayList<String> a_nameList= new ArrayList<String>();
   					for(int j=0;j<r;j++){
   						if(pattern.get(j).Parent_name.equals(p_nameList.get(k))){
   							boolean match = false;
   							for(int x=0;x<a_nameList.size();x++){
   								if(pattern.get(j).Attribute_name.equals(a_nameList.get(x))){
   									match=true;
   								}
   							}
   							if(!match)a_nameList.add(pattern.get(j).Attribute_name);
   							else flag=true;
   						}
   					}
   				}
   				if(!flag) {

   				}else{
   					break;
   				}
   				
   				
 
   				
   				//継承関係と直接関係を全て入力情報と比較し、不一致があれば削除
   				ArrayList<RelationPattern> judgment_set = new ArrayList<RelationPattern>();

   					for(int j=0;j<r;j++){
   						
   						judgment_set.add(pattern.get(j));	//継承以外の情報も格納する
   						
   						Integer c_name = pattern.get(j).Child_name;
   						for(int k=0;k<r;k++){
   							if(j!=k){
   								if(c_name.equals(pattern.get(k).Parent_name)){
   									
   									Integer pa = pattern.get(j).Parent_name;
   									Integer ch =pattern.get(k).Child_name;
   									String at = new String(pattern.get(j).Attribute_name);
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
   					
   					if(r+1>3){
   						for(int x=0;x<r+1 -3;x++){//ループ数4以上では孫以降の継承情報も必要
   						
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
   						System.out.println("find");
   	   					loopStructure=pattern;//１つ見つかった段階で終了
   	   					
   					}
   					
   		           break;
   				
   				
           }
           
        }
    	
    }
	
	
	public static ArrayList<RelationPattern> get_relationList(int loop_num,Integer[] loop_name){
		
		//関係パターンの列挙を取得するメソッド
		//列挙数を計算し、その数だけstruPatt[]を返す
		
		int attr_num = 3;
		String[] attr_name ={"pre","inner","post"};
		
		ArrayList<RelationPattern> patternList = new ArrayList<RelationPattern>();
		for(int i=0; i<loop_num;i++){	
			for(int j=0; j<loop_num;j++){
				for(int k=0; k<attr_num;k++){
					if(i!=j){
						RelationPattern pattern = new RelationPattern(loop_name[i],loop_name[j],attr_name[k]);
						patternList.add(pattern);
					}
				}
			}
		}
		return patternList;
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

	public static void add_judgment(Integer root, String attribute, Integer child, ArrayList<RelationPattern> fixed_inputList) {
		
		
		//継承関係追加
		RelationPattern pattern_new = new RelationPattern(root,child,attribute);
		judgment_set.add(pattern_new);
		
		for(int i=0;i<fixed_inputList.size();i++){
			if(child.equals(fixed_inputList.get(i).Parent_name)){
				add_judgment(root,attribute,fixed_inputList.get(i).Child_name,fixed_inputList);
			}
		}
	}
		
		
	
	public static boolean check_judgment(ArrayList<RelationPattern> fixed_inputList,ArrayList<RelationPattern> inputList) {
		
		//継承関係セットを作成し、全て入力セットに含まれていれば矛盾しない構造であると見なす
		for(int i=0;i<fixed_inputList.size();i++){
			//子要素の名前について,親になっているかを調べ,親になっていればその子要素に対して継承関係を作成する.
			//この処理を再帰的に行う.			
			//継承関係の追加
			Integer c_name = fixed_inputList.get(i).Child_name;
			
			for(int j=0;j<fixed_inputList.size();j++){
				
				if(i!=j){
					if(c_name.equals(fixed_inputList.get(j).Parent_name)){
						
						add_judgment(fixed_inputList.get(i).Parent_name,
								fixed_inputList.get(i).Attribute_name,
								fixed_inputList.get(i).Child_name,fixed_inputList);

					}
				}
			}
			
		}
		
		int count=0;
		
		
		//直接関係も判定には必要
		for(int i=0;i<fixed_inputList.size();i++){
			judgment_set.add(fixed_inputList.get(i));
		}
		
		
		for(int i=0;i<judgment_set.size();i++){
			boolean flag=false;
			for(int j=0;j<inputList.size();j++){
				
				if(judgment_set.get(i).Parent_name.equals(inputList.get(j).Parent_name)){
					if(judgment_set.get(i).Child_name.equals(inputList.get(j).Child_name)){

							if(judgment_set.get(i).Attribute_name.equals(inputList.get(j).Attribute_name)){
								count++;
								break;
							}
							
							//入力側の属性がnullの場合, 判定した子要素が他にnull以外の親を持っているか入力を探索.
							if(inputList.get(j).Attribute_name.equals("null")){
								int accept_flag=0; 
								
								ArrayList<RelationPattern> not_nullSet = new ArrayList<RelationPattern>();
								
								for(int x=0;x<inputList.size();x++){
									//入力を探索し,null以外の関係がある親が存在しているかどうかを調べる.
									if(judgment_set.get(i).Child_name.equals(inputList.get(x).Child_name)){
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
			if(flag)count++;
		}
		if(judgment_set.size()==count) return true;
		else return false;

	}

}

	
	

