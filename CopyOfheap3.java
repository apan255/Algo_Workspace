import java.util.ArrayList;
import java.util.Scanner;

public class CopyOfheap3 {
	static ArrayList<Integer> heapArray = new ArrayList<Integer>();
	
	public static void add(int newElement) {
		//Increase heap array size and add at last location
		heapArray.add(newElement);
		percolateUp(heapArray.size()-1);
	}
	
	public static void percolateUp(int currentIndex) {
		//If root node reached then return
		if (currentIndex == 0)
			return;
		//Find parent node and swap if child is smaller than parent
		int parentIndex = parentIndex(currentIndex);
		if (heapArray.get(currentIndex) < heapArray.get(parentIndex)) {
			swap(currentIndex, parentIndex);
			percolateUp(parentIndex);
		}
	}
	
	public static int parentIndex(int currentIndex) {
		return (currentIndex-1)/3;
	}
	
	public static void remove() {
		//Get element at root node
		int removedElement = heapArray.get(0);
		System.out.println(removedElement);
		int lastElementIndex = heapArray.size()-1;
		//Swap last element with that at root node
		swap(0, lastElementIndex);
		//Remove last element
		heapArray.remove(lastElementIndex);
		//Max heapify starting at root node
		maxHeapify(0);
	}
	
	public static void maxHeapify (int parentIndex) {
		int smallestChildIndex = smallestChildIndex(parentIndex);
		//If child node doesn't exist i.e. parent node is leaf node then return
		if (smallestChildIndex == -1) 
			return;
		//Swap parent with smallest child
		if (heapArray.get(parentIndex) > heapArray.get(smallestChildIndex)) {
			swap(parentIndex, smallestChildIndex);
			maxHeapify(smallestChildIndex);
		}
	}
	
	public static int smallestChildIndex(int parentIndex) {
		ArrayList<Integer> childIndexes = new ArrayList<Integer>();
		//Extracting child indexes
		int lastElementIndex = heapArray.size()-1;
		for (int i = 1; i <= 3; i++) {
			int childIndex = 3*parentIndex + i;
			//Break if child does not exist in heap
			if(childIndex > (lastElementIndex)) {
				break;
			} else {
				childIndexes.add(childIndex);
			}
		}
		
		//If no child found i.e. parent is leaf node then return -1
		if (childIndexes.size() == 0) {
			return -1;
		}
		
		//Iterate through child nodes to figure out smallest child node among them
		int smallestChildIndex = childIndexes.get(0);
		for (int i = 1; i < childIndexes.size(); i++) {
			if (heapArray.get(childIndexes.get(i)) < heapArray.get(smallestChildIndex)) {
				smallestChildIndex = childIndexes.get(i);
			}
		}
		return smallestChildIndex;
	}
	
	public static void swap (int indexOne, int indexTwo) {
		int temp = heapArray.get(indexOne);
		heapArray.set(indexOne, heapArray.get(indexTwo));
		heapArray.set(indexTwo, temp);
	}
}
