
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * This class implements all pair shortest path using Floyd-Warshall Algortihm
 * @author Aditya Pandey
 */
public class allpairs {
	//Array to hold words and its integer value mapping. 
	ArrayList<String> vertices = null;
	//2D array/table to hold dynamic programming result.
	int[][] dp = null;
	//2D array/table to hold adjacency matrix.
	int[][] adjMatrix = null;
	//2d array/table to hold k values for recovering shortest path in linear time.  
	int[][] kTable = null;
	//Array to hold number of queries.
	Query[] queries = null;
	
	public static void main(String[] args) {
		allpairs obj = new allpairs();
		obj.helperMethod();
	}
	
	/**
	 * Helper method to populate graph values from input.
	 */
	public void helperMethod() {
		Scanner scan = null;
		try {
			scan = new Scanner(System.in);
			//Populate number of vertices from input.
			int numVertices = scan.nextInt();
			//For simplicity I have defined length of vertices, dp, adjMatrix, kTable one value greater than number of vertices.
			vertices = new ArrayList<String>(numVertices+1);
			dp = new int[numVertices+1][numVertices+1];
			adjMatrix = new int[numVertices+1][numVertices+1];
			kTable = new int[numVertices+1][numVertices+1];
			//Add empty string at 0th index for simplicity.
			vertices.add(0, "");
			//Populate vertices array from input.
			for (int i=1; i < numVertices+1; i++) {
				vertices.add(i, scan.next());
			}
			//Populate number of queries from input.
			int numQueries = scan.nextInt();
			queries = new Query[numQueries];
			//Populate array of queries from input.
			for(int i=0; i < numQueries; i++) {
				queries[i] = new Query(scan.next(), scan.next());
			}
			//Create adjacency matrix
			createAdjMatrix();
			//Call to populate shortest paths dp table 
			floydWarshall();
			//Call to print average reachable words
			avgReachabeWords();
			//Call to print shortest paths
			printShortPaths();
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if (scan != null)
				scan.close();
		}
	}
	
	/**
	 * This method creates adjacency matrix from given input words.
	 */
	public void createAdjMatrix() {
		//Each input word is compared with other words.
		for (int i=1; i < vertices.size(); i++) {
			char[] source = vertices.get(i).toCharArray();
			for(int j=i; j < vertices.size(); j++) {
				//If comparing same vertex/word, set cell in adjacency matrix undefined/Integer.MAX_VALUE and continue.
				if(i == j) {
					adjMatrix[i][j] = Integer.MAX_VALUE;
					continue;
				}
				//Else figure out if there is an edge.
				char[] dest = vertices.get(j).toCharArray();
				int numDiff = 0;
				int weight = Integer.MAX_VALUE;
				//Only compare if source and destination vertex/words are of same length
				if(source.length == dest.length) {
					for (int k=0; k < dest.length; k++) {
						//If there is mismatch in char value go inside if condition.
						if (source[k] != dest[k]) {
							//Calculate weight by taking absolute difference of char mismatch.
							weight = Math.abs(source[k]-dest[k]);
							//If total number of char mismatch between words are greater than 1, 
							//break and set weight undefined/Integer.MAX_VALUE.
							if (++numDiff > 1) {
								weight = Integer.MAX_VALUE;
								break;
							}
						}
					}
				}
				//Populate weight in adjacency matrix
				adjMatrix[i][j] = weight;
				adjMatrix[j][i] = weight;
			}
		}
	}
	
	/**
	 * This method implements Floyd Warshall algorithm
	 * and populates dp array to store dynamic programming result. 
	 */
	public void floydWarshall() {
		//Populate dp array form adjacency matrix. Set diagonal elements 0.
		//Populate k table with max integer value.
		for (int i=1; i < adjMatrix.length; i++) {
			for (int j=1; j < adjMatrix[0].length; j++) {
				if (i == j) {
					dp[i][j] = 0;
				} else {
					dp[i][j] = adjMatrix[i][j];
				}
				kTable[i][j] = Integer.MAX_VALUE;
			}
		}
		
		//Iterate to populate all pairs shortest path matrix
		for (int k=1; k < dp.length; k++) {
			for (int i=1; i < dp.length; i++) {
				for (int j=1; j < dp.length; j++) {
					//If value is undefined(i.e. Integer.MAX_VALUE) in cells dp[i][k] and dp[k][j] 
					//we can't add them, so continue. Else if current value of cell 
					//is larger than sum of dp[i][k] and dp[k][j], replace current valuedp[i][j].
					if (dp[i][k] != Integer.MAX_VALUE && dp[k][j] != Integer.MAX_VALUE 
							&& dp[i][j] > dp[i][k]+dp[k][j]) {
						dp[i][j] = dp[i][k]+dp[k][j];
						kTable[i][j] = k;
					}
				}
			}
		}
	}
	
	/**
	 * This method calculates average number of reachable words in graph.
	 */
	public void avgReachabeWords() {
		int count = 0;
		//Iterate over each cell of dp table and increase count if reachable.
		for (int i=1; i < dp.length; i++) {
			for (int j=1; j < dp[0].length; j++) {
				if (dp[i][j] != Integer.MAX_VALUE) {
					count++;
				}
			}
		}
		//type casting to double and rounding off to 2 decimal place.
		double average = (double)count/(vertices.size()-1);
		DecimalFormat decFormat = new DecimalFormat("#.##");
		System.out.println(decFormat.format(average));
	}
	
	/**
	 * This method prints shortest path between given list of queries in input.
	 */
	public void printShortPaths() {
		//Iterate over given list of queries.
		for (int i=0, length = queries.length; i < length; i++) {
			//Figure out numerical value of start vertex.
			int start = vertices.indexOf(queries[i].start);
			//Figure out numerical value of end vertex.
			int end = vertices.indexOf(queries[i].end);
			//If path not found, print not reachable
			if (dp[start][end] == Integer.MAX_VALUE) {
				System.out.print(queries[i].start + " " + queries[i].end + " not reachable");
			} else {
				//Print shortest path length
				System.out.print(dp[start][end]);
				//ArrayList to hold shortest path
				ArrayList<String> shortPath = new ArrayList<String>();
				//Add start vertex to shortest path
				shortPath.add(vertices.get(start));
				recurShortPath(start, end, shortPath);
				//Add end vertex to shortest path
				shortPath.add(vertices.get(end));
				//Print shortest path
				for(String value : shortPath) {
					System.out.print(" " + value);
				}
			}
			System.out.println();
		}
	}
	
	/**
	 * This method recovers shortest path in linear time with the help of k table.
	 */
	public void recurShortPath(int start, int end, ArrayList<String> shortPath) {
		//If shortest path is same as in adjacency matrix then return.
		if (dp[start][end] == adjMatrix[start][end])
			return;
		//Find intermediate vertex of shortest path.
		int mid = kTable[start][end];
		//Recover left part of shortest path.
		recurShortPath(start, mid, shortPath);
		//Add shortest path to arrayList.
		shortPath.add(vertices.get(mid));
		//Recover right part of shortest path.
		recurShortPath(mid, end, shortPath);
	}
	
	/**
	 * Class to hold given queries in input
	 */
	static class Query {
		//Starting vertex of query 
		String start;
		//Ending vertex of query
		String end;
		Query(String start, String end) {
			this.start = start;
			this.end = end;
		}
		public String toString(){
			return "start: " + start + " end: " + end + " |";
		}
	}
}
