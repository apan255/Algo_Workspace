import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class CopyOfdependency {
	//Variable to hold list of dimensions of given matrices.
	static Map<String, ArrayList<String>> adjacencyList;
	static Map<String, ArrayList<String>> reverseAdjList;
	final static String WHITE = "white";
	final static String GRAY = "gray";
	final static String BLACK = "black";
	
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			//scanner = new Scanner(System.in);
			scanner = new Scanner(new File("src/input_d1.txt"));
			//First line in input tells us about number of vertices.
			int numVertices = scanner.nextInt();
			//Constructs hash map with initial capacity of given number of vertices
			adjacencyList = new LinkedHashMap<String, ArrayList<String>>(numVertices);
			reverseAdjList = new LinkedHashMap<String, ArrayList<String>>(numVertices);
			//Create adjacency list 
			for (int i = 1; i <= numVertices; i++) {
				adjacencyList.put(scanner.next(), new ArrayList<String>());
			}
			int numReln = scanner.nextInt();
			for (int i = 1; i <= numReln; i++) {
				ArrayList<String> neighbors = adjacencyList.get(scanner.next());
				neighbors.add(scanner.next());
			}
			
			Stack<String> stack = new Stack<String>();
			Map<String, String> vertColor = new HashMap<String, String>(numVertices);
			
			CopyOfdependency dependencyObj = new CopyOfdependency();
			//Order vertices by reverse finishing time
			dependencyObj.reverseFinishTime(stack, vertColor);
			//Reverse all graph edges
			dependencyObj.reverseGraphEdges(numVertices);
			//Color all vertices white
			for (String value : adjacencyList.keySet()) {
				vertColor.put(value, WHITE);
			}
			//Traverse reversed graph for each values in stack
			for (Map.Entry<String, ArrayList<String>> entry : adjacencyList.entrySet()) {
			//while (!stack.isEmpty()) {
				String vertValue = entry.getKey();
				if (WHITE.equalsIgnoreCase(vertColor.get(vertValue))) {
					Stack<String> resultStack = new Stack<String>();
					dependencyObj.traverseDFS(resultStack, vertColor, vertValue);
					if (resultStack.size() > 1) {
						while (!resultStack.isEmpty()) {
							System.out.print(resultStack.pop() + " ");
						}
						System.out.print("\n");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
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
	
	public void traverseDFS(Stack<String> resultStack, Map<String, String> vertColor, String vertValue) {
		vertColor.put(vertValue, GRAY);
		
		for (String neighbor : reverseAdjList.get(vertValue)) {
			//If neighbor is white recursively call recReverseFinish
			if (WHITE.equalsIgnoreCase(vertColor.get(neighbor))) {
				traverseDFS(resultStack, vertColor, neighbor);
			}
		}
		resultStack.push(vertValue);
		vertColor.put(vertValue, BLACK);
	}
}
