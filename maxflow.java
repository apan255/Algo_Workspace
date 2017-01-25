
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * This class implements maximum flow using Edmonds-Karp Algortihm
 * @author Aditya Pandey
 */
public class maxflow {
	//2D matrix to hold adjacency matrix.
	int[][] adjMatrix = null;
	//2D matrix to hold residual graph
	int[][] resdGraph = null;
	//Array to hold input edges to print the edges in same order as input.
	Pair[] inputEdgeOrder;
	//Declaring constant for vertex colors. 
	public static final String WHITE = "white";
	public static final String GREY = "grey";
	public static final String BLACK = "black";
	
	//Variable to hold total number of vertices in graph.
	int numVertices;
	//Variable to hold total number of edges in graph.
	int numEdges;
	
	public static void main(String[] args) {
		maxflow obj = new maxflow();
		obj.helperMethod();
	}
	
	/**
	 * Helper method to populate graph values from input.
	 */
	public void helperMethod() {
		Scanner scan = null;
		try {
			//scan = new Scanner(System.in);
			scan = new Scanner(new File("src/input_f3.txt"));
			//Populate number of vertices and edges from input.
			numVertices = scan.nextInt();
			numEdges = scan.nextInt();
			
			//Initialize adjList, resdGraph, inputEdgeOrder
			adjMatrix = new int[numVertices][numVertices];
			resdGraph = new int[numVertices][numVertices];
			inputEdgeOrder = new Pair[numEdges];
			
			//Populate adjacency matrix, residual graph, inputEdgeOrder from input.
			for(int i=0; i < numEdges; i++) {
				int source = scan.nextInt();
				int dest = scan.nextInt();
				int weight = scan.nextInt();
				//Populate adjacency matrix.
				adjMatrix[source][dest] = weight;
				resdGraph[source][dest] = weight;
				inputEdgeOrder[i] = new Pair(source, dest);
			}
			//Call to calculate maximum flow using Edmonds Karp Algorithm
			edmondsKarp();
			//Call to print output
			printOutput();
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if (scan != null)
				scan.close();
		}
	}
	
	/**
	 * This method implements Edmond Karp algorithm.
	 */
	public void edmondsKarp() {
		//Variable to hold maximum flow from source to sink.
		int maxFlow = 0;
		//Set source and destination
		int source = 0;
		int sink = 1;
		//Declare predecessor array to hold parent child relation.
		int[] predecessor = new int[numVertices];
		//While bfs traversal is possible keep increasing max flow as per augmenting path
		while(bfsTraversal(source, sink, maxFlow, predecessor)) {
			maxFlow += augmentingPath(source, sink, predecessor);
		}
		//Print maximum flow from source to sink
		System.out.println(maxFlow);
	}
	
	/**
	 * This method calculates augmenting path from source to sink.
	 */
	public int augmentingPath(int source, int sink, int[] predecessor) {
		//Variable to hold critical flow in augmenting path.
		int critFlow = Integer.MAX_VALUE;
		int dest = sink;
		//Calculate flow from critical path.
		while(dest != source) {
			int src = predecessor[dest];
			if(resdGraph[src][dest] < critFlow) {
				critFlow = resdGraph[src][dest];
			}
			dest = src;
		}
		
		//Decrease residual capacity in one direction and increase in reverse direction.
		dest = sink;
		while(dest != source) {
			//Figure out predecessor from predecessor array
			int src = predecessor[dest];
			//Increase capacity in one direction
			resdGraph[src][dest] -= critFlow;
			//Decrease capacity in reverse direction
			resdGraph[dest][src]+= critFlow;
			dest = src;
		}
		
		return critFlow;
	}
	
	/**
	 * This method performs BFS traversal to figure out path from source to sink.
	 */
	public boolean bfsTraversal(int source, int dest, int maxFlow, int[] predecessor) {
		String[] vertColor = new String[numVertices];
		//Setting predecessor undefined
		for (int i=0; i < numVertices; i++) {
			predecessor[i] = Integer.MIN_VALUE;
		}
		//Setting vertex color white
		for (int i=0; i < numVertices; i++) {
			vertColor[i] = WHITE;
		}
		//Add source vertex to queue and color it grey.
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(source);
		vertColor[source] = GREY;
		
		while(!queue.isEmpty()) {
			int currVertex = queue.poll();
			int[] neighbors = resdGraph[currVertex];
			//For each neighbors of current vertex
			for(int neighVert=0; neighVert < neighbors.length; neighVert++) {
				//If neighbor vertex is white and remaining capacity 
				//is greater than 0 traverse that vertex.
				if (resdGraph[currVertex][neighVert] != 0
						&& WHITE.equals(vertColor[neighVert])) {
					//Color neighbor vertex grey.
					vertColor[neighVert] = GREY;
					//Add current vertex as parent predecessor array
					predecessor[neighVert] = currVertex;
					//Add neighbor vertex to queue
					queue.add(neighVert);
				}
			}
			//Color vertex black.
			vertColor[currVertex] = BLACK;
		}
		if (predecessor[dest] == Integer.MIN_VALUE) {
			return false;
		}
		return true;
	}
	
	/**
	 * Class to hold Edges from input.
	 */
	static class Pair {
		int src;
		int dest;
		Pair(int src, int dest) {
			this.src = src;
			this.dest = dest;
		}
	}
	
	/**
	 * This method prints output in desired format.
	 */
	public void printOutput() {
		//For each edges in input print its max flow.
		for(int i=0; i < inputEdgeOrder.length; i++) {
			Pair edge = inputEdgeOrder[i];
			//Retrieve max flow across edge from resdGraph.
			System.out.println(edge.src + " " + edge.dest + " " + resdGraph[edge.dest][edge.src]);
		}
	}
}
