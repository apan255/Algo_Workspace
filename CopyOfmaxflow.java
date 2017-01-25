
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
/**
 * This class implements maximum flow using Edmonds-Karp Algortihm
 * @author Aditya Pandey
 */
public class CopyOfmaxflow {
	//ArrayList to hold Adjacency List of given graph.
	ArrayList<ArrayList<Pair>> adjList = null;
	//ArrayList to hold residual graph.
	ArrayList<ArrayList<Pair>> resdGraph = null;
	
	//Declaring constant for vertex colors. 
	public static final String WHITE = "white";
	public static final String GREY = "grey";
	public static final String BLACK = "black";
	
	//Variable to hold total number of vertices in graph.
	int numVertices;
	//Variable to hold total number of edges in graph.
	int numEdges;
	//2D array/table to hold residual capacities of residual graph.
	int[][] resdCapacity = null;
	
	public static void main(String[] args) {
		CopyOfmaxflow obj = new CopyOfmaxflow();
		obj.helperMethod();
	}
	
	/**
	 * Helper method to populate graph values from input.
	 */
	public void helperMethod() {
		Scanner scan = null;
		try {
			scan = new Scanner(System.in);
			
			//Populate number of vertices and edges from input.
			numVertices = scan.nextInt();
			numEdges = scan.nextInt();
			
			//Initialize adjList, resdGraph, resdCapacity
			adjList = new ArrayList<ArrayList<Pair>>(numVertices);
			resdGraph = new ArrayList<ArrayList<Pair>>(numVertices);
			resdCapacity = new int[numVertices][numVertices];
			
			//Initialize  neighbors of adjacency list and residual graph.
			for (int i=0; i < numVertices; i++) {
				adjList.add(new ArrayList<Pair>());
				resdGraph.add(new ArrayList<Pair>());
			}
			
			//Populate adjacency list, residual graph and resdCapacity from input.
			for(int i=0; i < numEdges; i++) {
				int source = scan.nextInt();
				int dest = scan.nextInt();
				int weight = scan.nextInt();
				//Populate adjacency list.
				ArrayList<Pair> neighbour = adjList.get(source);
				neighbour.add(new Pair(dest, weight));
				//Populate adjacency list for residual graph.
				ArrayList<Pair> resNeighbour = resdGraph.get(source);
				resNeighbour.add(new Pair(dest, weight));
				//Initialize resdCapacity for residual graph from adjacency list by its weight.
				resdCapacity[source][dest] = weight;
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
		//Call to construct residual graph
		constResidualGraph();
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
			if(resdCapacity[src][dest] < critFlow) {
				critFlow = resdCapacity[src][dest];
			}
			dest = src;
		}
		
		//Decrease residual capacity in one direction and increase in reverse direction.
		dest = sink;
		while(dest != source) {
			//Figure out predecessor from predecessor array
			int src = predecessor[dest];
			//Increase capacity in one direction
			resdCapacity[src][dest] -= critFlow;
			//Decrease capacity in reverse direction
			resdCapacity[dest][src]+= critFlow;
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
			ArrayList<Pair> neighbors = resdGraph.get(currVertex);
			//For each neighbors of current vertex
			for(int src=0; src < neighbors.size(); src++) {
				int neighVert = neighbors.get(src).nebVertex;
				//If neighbor vertex is white and remaining capacity 
				//is greater than 0 traverse that vertex.
				if (WHITE.equals(vertColor[neighVert]) 
						&& resdCapacity[currVertex][neighVert] != 0) {
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
	 * This method constructs residual graph
	 */
	public void constResidualGraph() {
		//For each edge i->j in adjacency list, add edge j->i with weight 0 in residual graph.
		for(int source=0; source < adjList.size(); source++) {
			ArrayList<Pair> neighbors = adjList.get(source);
			for(int j=0; j < neighbors.size(); j++) {
				//Add edge from destination to source with weight 0 in residual graph.
				ArrayList<Pair> dest = resdGraph.get(j);
				dest.add(new Pair(source, 0));
			}
		}
	}
	
	/**
	 * Class to hold neighbors of vertex and its weight.
	 */
	static class Pair {
		int nebVertex;
		int weight;
		Pair(int nebVertex, int weight) {
			this.nebVertex = nebVertex;
			this.weight = weight;
		}
	}
	
	/**
	 * This method prints output in desired format.
	 */
	public void printOutput() {
		//For each edges in graph print its flow.
		for(int source=0; source < adjList.size(); source++) {
			ArrayList<Pair> neighbors = adjList.get(source);
			for(int j=0; j < neighbors.size(); j++) {
				int destVertex = neighbors.get(j).nebVertex;
				//Retrieve max flow across edge from resdCapacity table.
				System.out.println(source + " " + destVertex + " " + resdCapacity[destVertex][source]);
			}
		}
	}
}
