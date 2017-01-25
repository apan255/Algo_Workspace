import java.util.ArrayList;
import java.util.Scanner;

public class qsort {
	static ArrayList<Integer> arrayList = new ArrayList<Integer>();
	static int switchSorting;
	
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(System.in);
			//Read the input and add to array list
			while(scanner.hasNextLine()) {
				arrayList.add(Integer.parseInt(scanner.nextLine()));
			}
			//Read value of switchSorting from command line
			switchSorting = Integer.parseInt(args[0]);
			
			long time0 = getMilliseconds();
			quickSort(0, arrayList.size()-1);
			long time1 = getMilliseconds();
			//Print time taken to run the sorting algorithm
			System.err.println(time1 - time0);

			//Print sorted array
			printSortedArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}
	}
	
	public static void quickSort(int leftIndex, int rightIndex) {
		if (leftIndex < rightIndex) {
			/*If size of array to be sorted is less than switchSorting then
			switch sorting from quick sort to insertion sort.*/
			if (rightIndex-leftIndex > switchSorting) {
				int pivot = partition(leftIndex, rightIndex);
				quickSort(leftIndex, pivot-1);
				quickSort(pivot+1, rightIndex);
			} else {
				insertionSort(leftIndex, rightIndex);
			}
		}
	}
	
	public static int partition(int leftIndex, int rightIndex) {
		int i = leftIndex-1;
		int pivot = arrayList.get(rightIndex);
		for (int j = leftIndex; j <= rightIndex-1; j++) {
			if (arrayList.get(j) < pivot) {
				i += 1;
				swap(i, j);
			}
		}
		swap(i+1, rightIndex);
		return i+1;
	}
	
	public static void insertionSort(int leftIndex, int rightIndex) {
		for (int i = leftIndex+1; i <= rightIndex; i++) {
			int value = arrayList.get(i);
			int j = i-1;
			while (j >= leftIndex && arrayList.get(j) > value) {
				arrayList.set(j+1, arrayList.get(j));
				j--;
			}
			arrayList.set(j+1, value);
		}
	}
	
	public static void swap (int indexOne, int indexTwo) {
		int temp = arrayList.get(indexOne);
		arrayList.set(indexOne, arrayList.get(indexTwo));
		arrayList.set(indexTwo, temp);
	}
	
	public static long getMilliseconds() {
		return System.currentTimeMillis();
	}
	
	public static void printSortedArray() {
		for (int element: arrayList) {
			System.out.println(element);
		}
	}
	
}
