package jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility;


/**
 * Pair class
 * @author y-yamashita
 * @param <First> Type of First element
 * @param <Second> Type of Second element
 */
public class Pair<First,Second> {
	private final First first;
	private final Second second;
	
	/**
	 * Constructor
	 * @param a
	 * @param b
	 */
	public Pair(First fst,Second sec){
		this.first=fst;
		this.second=sec;
	}
	
	/**
	 * Obtain first
	 * @return first
	 */
	public final First getFirst(){
		return first;
	}
	
	/**
	 * Obtain second
	 * @return second
	 */
	public final Second getSecond(){
		return second;
	}
	
	/**
	 * equals method
	 */
	public boolean equals(Object obj){
		if(obj == null){return false;}
		if(obj instanceof Pair){
			Object[] that = getValues((Pair<First,Second>) obj);
			return java.util.Arrays.deepEquals(that,getValues(this));
		}
		return false;		
	}
	
	/**
	 * has code mehod
	 */
	public int hashCode(){
		return java.util.Arrays.deepHashCode(getValues(this));
	}
	
	/**
	 * Get values method
	 * @param v
	 * @return object array
	 */
	private static <First,Second> Object[] getValues(Pair<First,Second> v){
		return new Object[]	{v.first,v.second};
	}
	
	/**
	 * toString method
	 */
	@Override 
	public String toString(){
		return new StringBuilder().append(this.first).append(",").
				append(this.second).toString();
	}
	
}
