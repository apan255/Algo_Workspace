import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class huffman {
	//Helper map to read input values
	static Map<String, Integer> charFreqMap = new HashMap<String, Integer>(256);
	//Map to store results
	Map<String, String> resultTable = new TreeMap<String, String>();
	//Creating object of Ternary min heap 
	static heap3<HuffNode> queue = new huffman().new heap3<HuffNode>();
	
	static final String EOF = "EOF";
	
	public static void main(String[] args) {
		DataInputStream in = null;
		try {
			in = new DataInputStream(System.in);

			//Create hash map and initialize each to 1
			for (int i = 0; i < 256; i++) {
				charFreqMap.put(String.valueOf((char)i), 1);
			}
			charFreqMap.put(EOF, 1);
			//int inputByte;
			while(in.available() > 0) {
				int inputByte = in.readUnsignedByte();
				String input = String.valueOf((char)inputByte);
				int currentVal = charFreqMap.get(input);
				charFreqMap.put(input, currentVal+1);
			}
			
			huffman huffmanObj = new huffman();
			//Add value and frequency of input values as HuffNode
			huffmanObj.createPriorQueue();
			//Create huffman tree
			HuffNode rootNode = huffmanObj.buildHuffTree();
			huffmanObj.createResultTable(rootNode, new StringBuffer());
			huffmanObj.printOutput();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public HuffNode buildHuffTree() {
		while (queue.size() > 1) {
			HuffNode nodeOne = queue.poll();
			HuffNode nodeTwo = queue.poll();
			int totalFreq = nodeOne.freq + nodeTwo.freq;
			HuffNode parentNode = new HuffNode(totalFreq, nodeOne, nodeTwo, null);
			nodeOne.parentNode = parentNode;
			nodeTwo.parentNode = parentNode;
			queue.add(parentNode);
		}
		return queue.poll();
	}
	
	public void createPriorQueue() {
		for (Map.Entry<String, Integer> entry : charFreqMap.entrySet()) {
			//Add each node to ternary heap
			queue.add(new HuffNode(entry.getKey(), entry.getValue()));
		}
	}
		
	public void createResultTable(HuffNode node, StringBuffer resultStr) {
		if (node == null) {
			return;
		}
		
		//If leaf node
		if (node.leftChild == null && node.rightChild == null) {
			resultTable.put(node.value, resultStr.toString());
			return;
		}
		
		//Append 0 for left child
		resultStr.append("0");
		createResultTable(node.leftChild, resultStr);
		
		//Add 1 for right child. Replace 0 at end of string buffer with 1.
		int length = resultStr.length();
		resultStr.replace(length-1, length, "1");
		createResultTable(node.rightChild, resultStr);
		
		resultStr.deleteCharAt(length-1);
	}
	
	//HuffNode to store value, frequency and parent child relation
	class HuffNode implements Comparable<HuffNode>{
		String value;
		int freq;
		HuffNode leftChild;
		HuffNode rightChild;
		HuffNode parentNode;
		
		public HuffNode(int freq, HuffNode leftChild,
				HuffNode rightChild, HuffNode parentNode) {
			super();
			this.freq = freq;
			this.leftChild = leftChild;
			this.rightChild = rightChild;
			this.parentNode = parentNode;
		}
		
		HuffNode(String value, int freq) {
			this.value = value;
			this.freq = freq;
		}
		
		public HuffNode() {
			super();
		}

		public int compareTo(HuffNode obj) {
			return this.freq - obj.freq;
		}

	}
	
	public void printOutput() {
		for (int i = 0; i <= 256; i++) {
			if (i >= 33 && i <= 126) {
				System.out.println("   " + (char)i + " " + resultTable.get(String.valueOf((char)i)));
			} else if (i == 256) {
				System.out.println(" EOF " + resultTable.get(EOF));
			} else {
				System.out.println("  " + String.format("%02X", i)+ " " + resultTable.get(String.valueOf((char)i)));
			}
		}
	}
	
	//Ternary min heap implementation
	public class heap3<E extends Comparable<E>> {
		ArrayList<E> pQueue;
		
		heap3() {
			this.pQueue = new ArrayList<E>();
		}
		
		int size() {
			return pQueue.size();
		}
		
		public void add(E newElement) {
			//Increase heap array size and add at last location
			pQueue.add(newElement);
			percolateUp(pQueue.size()-1);
		}
		
		public void percolateUp(int currentIndex) {
			//If root node reached then return
			if (currentIndex == 0)
				return;
			//Find parent node and swap if child is smaller than parent
			int parentIndex = parentIndex(currentIndex);
			if (pQueue.get(currentIndex).compareTo(pQueue.get(parentIndex)) < 0) {
				swap(currentIndex, parentIndex);
				percolateUp(parentIndex);
			}
		}
		
		public int parentIndex(int currentIndex) {
			return (currentIndex-1)/3;
		}
		
		public E poll() {
			int lastElementIndex = pQueue.size()-1;
			//Swap last element with that at root node
			swap(0, lastElementIndex);
			//Remove last element
			E removedObj = pQueue.remove(lastElementIndex);
			//Max heapify starting at root node
			maxHeapify(0);
			return removedObj;
		}
		
		public void maxHeapify (int parentIndex) {
			int smallestChildIndex = smallestChildIndex(parentIndex);
			//If child node doesn't exist i.e. parent node is leaf node then return
			if (smallestChildIndex == -1) 
				return;
			//Swap parent with smallest child
			if (pQueue.get(parentIndex).compareTo(pQueue.get(smallestChildIndex)) > 0) {
				swap(parentIndex, smallestChildIndex);
				maxHeapify(smallestChildIndex);
			}
		}
		
		public int smallestChildIndex(int parentIndex) {
			ArrayList<Integer> childIndexes = new ArrayList<Integer>();
			//Extracting child indexes
			int lastElementIndex = pQueue.size()-1;
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
				if (pQueue.get(childIndexes.get(i)).compareTo(pQueue.get(smallestChildIndex)) < 0) {
					smallestChildIndex = childIndexes.get(i);
				}
			}
			return smallestChildIndex;
		}
		
		public void swap (int indexOne, int indexTwo) {
			E temp = pQueue.get(indexOne);
			pQueue.set(indexOne, pQueue.get(indexTwo));
			pQueue.set(indexTwo, temp);
		}
	}

}
