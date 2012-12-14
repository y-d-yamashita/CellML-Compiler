package jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility;

import java.util.ArrayList;
import java.util.List;

public class PrimeFactors {

	private static int[] primeNumberList = {2,3,5,7,11,13};
	public int[] getPrimeNmbers() {
		return primeNumberList;
	}
	
	public PrimeFactors() {
		
	}
	
	public boolean isPrime(int number) {
		boolean isprime = false;
		for (int i=0; i<primeNumberList.length; i++) {
			if (number == primeNumberList[i])
				isprime = true;
		}
		
		return isprime;
		
	}
	
	public List<Integer> primeFactors(int number) {
		
		int n = number;
		List<Integer> factors = new ArrayList<Integer>();
		for (int i=2; i<14; i++) {
			while (n % i == 0) {
		        factors.add(i);
		        n /= i;
		    }
		}
		return factors;
	}
	
	/** NOTES
	 * This is based on the fact that a any loop we have already tried 
	 * to divide n by the values between 2 and i-1. 
	 * Therefore i can only be a divisor of n if it is a prime 
	 * (otherwise we would have found a fitting divisor already in the loop between 2 and i-1) . 
	 */
	
	public static void main(String[] args) {
		
//		for (Integer integer : primeFactors(30)) {
//			System.out.println(integer);  
//		}
//		for (Integer integer : primeFactors(105)) {
//			System.out.println(integer);
//		}
		
//		boolean isPrime;
//		
//		System.out.println(isPrime(11));
		
		
	}
}
