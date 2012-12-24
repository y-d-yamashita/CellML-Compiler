package jp.ac.ritsumei.is.hpcss.cellMLonGPU.loopstructure;

/**
 * RelPatt class define the Loop Relation Pattern.
 * This class has three elements( "Parent" "Child" "Attribute")
 *  
 * @author n-washio
 *
 */
public class RelationPath {
	public Integer Parent_name;
	public Integer Child_name;
	public String Attribute_name = new String();
	
	/*-----コンストラクタ-----*/
	public RelationPath(Integer index1, Integer index2, String index3) {	
		this.Parent_name = index1;
		this.Child_name = index2;
		this.Attribute_name = index3;
	}
	
	public void printContents(){
		//内容表示メソッド
		System.out.print("Parent:" + Parent_name+"\t");
		System.out.print("Child:" + Child_name+"\t");
		System.out.print("Attribute:" + Attribute_name+"\t");
		System.out.println();
	}
}
