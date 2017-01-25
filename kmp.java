import java.io.File;
import java.util.Scanner;

public class kmp {

	public static void main(String[] args) {
		String hayStack = null;
		String needle = null;
		Scanner scan = null;
		try {
			kmp obj = new kmp();
			if(args.length != 0) {
				scan = new Scanner(new File(args[0]));
				hayStack = scan.next();
				needle = scan.next();
			} else {
				hayStack = obj.createHayStack();
				needle = obj.createNeedle();
			}
			
			//Naive
			System.out.print("found at: ");
			long startNaive = getMilliseconds();
			int indexNaive = obj.naive(hayStack, needle);
			long endNaive = getMilliseconds();
			System.out.println(indexNaive);
			System.out.println("naive search time: " + (endNaive-startNaive));
			
			//Standard
			System.out.print("found at: ");
			long startStand = getMilliseconds();
			int indexStand = hayStack.indexOf(needle);
			long endStand = getMilliseconds();
			System.out.println(indexStand);
			System.out.println("standard search time: " + (endStand-startStand));
			
			//kmp
			System.out.print("found at: ");
			long startKMP = getMilliseconds();
			int indexKMP = obj.kmpSearch(hayStack, needle);
			long endKMP = getMilliseconds();
			System.out.println(indexKMP);
			System.out.println("kmp search time: " + (endKMP-startKMP));

		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if (scan != null)
				scan.close();
		}
	}
	
	public int naive(String hayStack, String needle) {
		//If haystack's length is greater than that of haystack return -1.
		int lenHayStack = hayStack.length();
		int lenNeedle = needle.length();
		if (lenNeedle > lenHayStack)
			return -1;

		char[] hayStackArray = hayStack.toCharArray();
		char[] needleArray = needle.toCharArray();
		for (int i=0; i<=lenHayStack-lenNeedle; i++) {
			int j=0;
			for (; j<lenNeedle; j++) {
				//If match not found in needle move pattern forward
				if(hayStackArray[i+j] != needleArray[j]) {
					break;
				}
			}
			//If needle found in haystack return starting indexof haystack 
			if (j == lenNeedle) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * KMP search to find needle in haystack.
	 */
	public int kmpSearch(String hayStack, String needle) {
		//If haystack's length is greater than that of haystack return -1.
		int lenHayStack = hayStack.length();
		int lenNeedle = needle.length();
		if (lenNeedle > lenHayStack)
			return -1;
		int[] prefixArray = new int[lenNeedle];
		char[] hayStackArray = hayStack.toCharArray();
		char[] needleArray = needle.toCharArray();
		//Compute prefix array
		computePrefix(needleArray, prefixArray);
		int q = 0;
		//Check for needle in haystack
		for (int i=0; i < lenHayStack; i++) {
			while (q>0 && needleArray[q] != hayStackArray[i]) {
				q = prefixArray[q-1];
			}
			if (needleArray[q] == hayStackArray[i]) {
				q++;
			}
			if(q == lenNeedle) {
				return i-lenNeedle+1;
			}
		}
		return -1;
	}
	
	public void computePrefix(char[] needle, int[] prefixArray) {
		prefixArray[0] = 0;
		int k = 0;
		int lenNeedle = needle.length;
		for (int i=1; i< lenNeedle; i++) {
			while (k>0 && needle[k] != needle[i]) {
				k = prefixArray[k-1];
			}
			if (needle[k] == needle[i]) {
				k++;
			}
			prefixArray[i] = k;
		}
	}
	
	public static long getMilliseconds() {
		return System.currentTimeMillis();
	}
	
	/**
	 * This method creates needle.
	 */
	public String createNeedle() {
		StringBuilder sBuilder = new StringBuilder();
		String needle = "acacabacacabacacad";
		for (int i=1; i < 1000; i++) {
			sBuilder.append(needle);
		}
		sBuilder.append("acacabacacabacacac");
		return sBuilder.toString();
	}
	
	/**
	 * This method creates haystack.
	 */
	public String createHayStack() {
		StringBuilder sBuilder = new StringBuilder();
		String hayStack = "acacabacacabacacad";
		for (int i=1; i < 100000; i++) {
			sBuilder.append(hayStack);
		}
		sBuilder.append("acacabacacabacacac");
		return sBuilder.toString();
	}
}
