package jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility;


/**
 * Pair class
 * @author y-yamashita
 * @param <FIRST> Type of First element
 * @param <SECOND> Type of Second element
 */
public class Pair<FIRST, SECOND> {
	private final FIRST first;
	private final SECOND second;
	
	/**
	 * Constructor
	 * @param a
	 * @param b
	 */
	public Pair(FIRST first,SECOND second){
		this.first=first;
		this.second=second;
	}
	
	/**
	 * Obtain first
	 * @return first
	 */
	public final FIRST getFirst(){
		return first;
	}
	
	/**
	 * Obtain second
	 * @return second
	 */
	public final SECOND getSecond(){
		return second;
	}
	
	/**
	 * equals method
	 */
	public boolean equals(Object obj){
		if(obj == null){return false;}
		if(obj instanceof Pair){
			Object[] that = getValues((Pair<FIRST,SECOND>) obj);
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
	private static <FIRST,SECOND> Object[] getValues(Pair<FIRST,SECOND> v){
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
