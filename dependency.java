import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class dependency {
	//Variable to hold list of dimensions of given matrices.
	static Map<String, ArrayList<String>> adjacencyList;
	static Map<String, ArrayList<String>> reverseAdjList;
	//Helper array to sort output as per insertion order.
	static Map<String, Integer> vertInsertOrder;
	final static String WHITE = "white";
	final static String GRAY = "gray";
	final static String BLACK = "black";
	
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(System.in);

			//First line in input tells us about number of vertices.
			int numVertices = scanner.nextInt();
			//Constructs hash map with initial capacity of given number of vertices
			adjacencyList = new LinkedHashMap<String, ArrayList<String>>(numVertices);
			reverseAdjList = new LinkedHashMap<String, ArrayList<String>>(numVertices);
			vertInsertOrder = new LinkedHashMap<String, Integer>(numVertices);
			//Create adjacency list 
			for (int i = 1; i <= numVertices; i++) {
				String key = scanner.next();
				adjacencyList.put(key, new ArrayList<String>());
				vertInsertOrder.put(key, i);
			}
			int numReln = scanner.nextInt();
			for (int i = 1; i <= numReln; i++) {
				ArrayList<String> neighbors = adjacencyList.get(scanner.next());
				neighbors.add(scanner.next());
			}
			
			Stack<String> stack = new Stack<String>();
			Map<String, String> vertColor = new HashMap<String, String>(numVertices);
			
			dependency dependencyObj = new dependency();
			//Order vertices by reverse finishing time
			dependencyObj.reverseFinishTime(stack, vertColor);
			//Reverse all graph edges
			dependencyObj.reverseGraphEdges(numVertices);
			//Color all vertices white
			for (String value : adjacencyList.keySet()) {
				vertColor.put(value, WHITE);
			}
			//Traverse reversed graph for each values in stack
			ArrayList<ArrayList<VertexOrder>> resultArray = new ArrayList<ArrayList<VertexOrder>>();
			while (!stack.isEmpty()) {
				String vertValue = stack.pop();
				if (WHITE.equalsIgnoreCase(vertColor.get(vertValue))) {
					ArrayList<VertexOrder> result = new ArrayList<VertexOrder>();
					dependencyObj.traverseDFS(result, vertColor, vertValue);
					if (result.size() > 1) {
						Collections.sort(result, new dependency().new VertInsOrdComparator());
						resultArray.add(result);
					}
				}
			}
			Collections.sort(resultArray, new dependency().new VertInsArrayComparator());
			printResult(resultArray);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}
	}

	private static void printResult(
			ArrayList<ArrayList<VertexOrder>> resultArray) {
		for (int i = 0; i < resultArray.size(); i++) {
			ArrayList<VertexOrder> arrayList = resultArray.get(i);
			int arraySize = arrayList.size();
			for (int j = 0; j < arraySize; j++) {
				if (j != arraySize-1) {
					System.out.print(arrayList.get(j).value + " ");
				} else {
					System.out.print(arrayList.get(j).value);
				}
			}
			System.out.print("\n");
		}
	}
	
	public void reverseFinishTime(Stack<String> stack, Map<String, String> vertColor) {
		for (String value : adjacencyList.keySet()) {
			vertColor.put(value, WHITE);
		}
		
		for (Map.Entry<String, ArrayList<String>> entry : adjacencyList.entrySet()) {
			//If vertex is white perform DFS.
			if (WHITE.equalsIgnoreCase(vertColor.get(entry.getKey()))) {
				recReverseFinish(stack, vertColor, entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void recReverseFinish(Stack<String> stack, Map<String, String> vertColor, 
			String vertValue, ArrayList<String> vertNeighbors) {
		vertColor.put(vertValue, GRAY);
		for (String neighbor : vertNeighbors) {
			//If neighbor is white recursively call recReverseFinish
			if (WHITE.equalsIgnoreCase(vertColor.get(neighbor))) {
				recReverseFinish(stack, vertColor, neighbor, adjacencyList.get(neighbor));
			}
		}
		vertColor.put(vertValue, BLACK);
		stack.push(vertValue);
	}
	
	public void reverseGraphEdges(int numVertices) {
		//Create reverse adjacency hashmap
		for (String vertValue : adjacencyList.keySet()) {
			reverseAdjList.put(vertValue, new ArrayList<String>());
		}
		//Associate relations in reverse order
		for (Map.Entry<String, ArrayList<String>> entry : adjacencyList.entrySet()) {
			//Iterate over each of neighbors for vertices
			for (String neigbour : entry.getValue()) {
				ArrayList<String> neighbours = reverseAdjList.get(neigbour);
				neighbours.add(entry.getKey());
			}
		}
	}
	
	public void traverseDFS(ArrayList<VertexOrder> result, Map<String, String> vertColor, String vertValue) {
		vertColor.put(vertValue, GRAY);
		for (String neighbor : reverseAdjList.get(vertValue)) {
			//If neighbor is white recursively call recReverseFinish
			if (WHITE.equalsIgnoreCase(vertColor.get(neighbor))) {
				traverseDFS(result, vertColor, neighbor);
			}
		}
		result.add(new VertexOrder(vertValue));
		vertColor.put(vertValue, BLACK);
	}
	
	class VertexOrder {
		String value;
		int orderAdjMatrx;
		VertexOrder(String value) {
			this.value = value;
			this.orderAdjMatrx = vertInsertOrder.get(value);
		}
	}
	
	class VertInsOrdComparator implements Comparator<VertexOrder> {
		@Override
	    public int compare(VertexOrder vertOrder1, VertexOrder vertOrder2) {
	        return vertOrder1.orderAdjMatrx - vertOrder2.orderAdjMatrx;
	    }
	}
	
	class VertInsArrayComparator implements Comparator<ArrayList<VertexOrder>> {
		@Override
	    public int compare(ArrayList<VertexOrder> vertOrder1, ArrayList<VertexOrder> vertOrder2) {
	        return vertOrder1.get(0).orderAdjMatrx - vertOrder2.get(0).orderAdjMatrx;
	    }
	}
}
