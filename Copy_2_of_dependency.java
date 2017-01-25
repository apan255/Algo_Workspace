import java.io.File;
import java.util.*;

public class Copy_2_of_dependency {
	//Variable to hold list of dimensions of given matrices.
	static Map<Vertex, ArrayList<String>> adjacencyList;
	static Map<Vertex, ArrayList<String>> reverseAdjList;
	final static String WHITE = "white";
	final static String GRAY = "gray";
	final static String BLACK = "black";
	
	public static void main(String[] args) {
		String array[]={"Hi", "Hello", "Howdy", "Bye"};
		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(array));

		/*
		Scanner scanner = null;
		try {
			//scanner = new Scanner(System.in);
			scanner = new Scanner(new File("src/input_d1.txt"));
			//First line in input tells us about number of vertices.
			int numVertices = scanner.nextInt();
			
			//Constructs hash map with initial capacity of given number of vertices
			adjacencyList = new LinkedHashMap<Vertex, ArrayList<String>>(numVertices);
			reverseAdjList = new LinkedHashMap<Vertex, ArrayList<String>>(numVertices);
			
			//Create adjacency list 
			for (int i = 1; i <= numVertices; i++) {
				Vertex vertex = new Vertex(scanner.next(), i);
				adjacencyList.put(vertex, new ArrayList<String>());
			}
			
			int numReln = scanner.nextInt();
			for (int i = 1; i <= numReln; i++) {
				ArrayList<String> neighbors = adjacencyList.get(scanner.next());
				neighbors.add(scanner.next());
			}
			
			Stack<String> stack = new Stack<String>();
			Map<String, String> vertColor = new HashMap<String, String>(numVertices);
			
			Copy_2_of_dependency dependencyObj = new Copy_2_of_dependency();
			//Order vertices by reverse finishing time
			dependencyObj.reverseFinishTime(stack, vertColor);
			//Reverse all graph edges
			dependencyObj.reverseGraphEdges(numVertices);
			//Color all vertices white
			for ( Map.Entry<Vertex, ArrayList<String>> entry : adjacencyList.entrySet()) {
				vertColor.put(entry.getKey().vertValue, WHITE);
			}
			//Traverse reversed graph for each values in stack
			for (Map.Entry<Vertex, ArrayList<String>> entry : reverseAdjList.entrySet()) {
			//while (!stack.isEmpty()) {
				String vertValue = entry.getKey().vertValue;
				if (WHITE.equalsIgnoreCase(vertColor.get(vertValue))) {
					Stack<Vertex> resultStack = new Stack<Vertex>();
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
	*/}
	
	public void reverseFinishTime(Stack<String> stack, Map<String, String> vertColor) {
		for (Vertex vertex : adjacencyList.keySet()) {
			vertColor.put(vertex.vertValue, WHITE);
		}
		
		for (Map.Entry<Vertex, ArrayList<String>> entry : adjacencyList.entrySet()) {
			//If vertex is white perform DFS.
			if (WHITE.equalsIgnoreCase(vertColor.get(entry.getKey()))) {
				recReverseFinish(stack, vertColor, entry.getKey().vertValue, entry.getValue());
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
		for (Vertex vertValue : adjacencyList.keySet()) {
			reverseAdjList.put(vertValue, new ArrayList<String>());
		}
		//Associate relations in reverse order
		for (Map.Entry<Vertex, ArrayList<String>> entry : adjacencyList.entrySet()) {
			//Iterate over each of neighbors for vertices
			for (String neigbour : entry.getValue()) {
				ArrayList<String> neighbours = reverseAdjList.get(neigbour);
				neighbours.add(entry.getKey().vertValue);
			}
		}
	}
	
	public void traverseDFS(Stack<Vertex> resultStack, Map<String, String> vertColor, String vertValue) {
		vertColor.put(vertValue, GRAY);
		
		for (String neighbor : reverseAdjList.get(vertValue)) {
			//If neighbor is white recursively call recReverseFinish
			if (WHITE.equalsIgnoreCase(vertColor.get(neighbor))) {
				traverseDFS(resultStack, vertColor, neighbor);
			}
		}
		//resultStack.push(vertValue);
		vertColor.put(vertValue, BLACK);
	}
	
	class Vertex {
		String vertValue;
		int inserOrder;
		
		Vertex(String vertValue, int order) {
			this.vertValue = vertValue;
			this.inserOrder = order;
		}
		
		@Override
	    public int hashCode() {
	        return vertValue.hashCode();
	    }
	 
	    //Compare only account numbers
	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        Vertex other = (Vertex) obj;
	        if (vertValue != other.vertValue)
	            return false;
	        return true;
	    }
	}
}
