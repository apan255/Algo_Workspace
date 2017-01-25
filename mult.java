import java.util.ArrayList;
import java.util.Scanner;

public class mult {
	//Variable to hold list of dimensions of given matrices.
	static ArrayList<Integer> dimensionList = new ArrayList<Integer>();
	
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(System.in);
			while(scanner.hasNextLine()) {
				dimensionList.add(Integer.parseInt(scanner.nextLine()));
			} 
			//Variable to hold number of matrices to be multiplied. As described in question, number of matrices to be multiplied is at index 0.
			int numMatrices = dimensionList.remove(0);
			
			//m is the matrix to hold minimum number of scalar multiplication in order to compute the matrix chain multiplications. 
			//Zeroth row and column is ignored for simplicity of program.
			int[][] m = new int[numMatrices+1][numMatrices+1];
			
			//s is the matrix which shows how to multiply the matrices.
			//Zeroth row and column is ignored for simplicity of program.
			int[][] s = new int[numMatrices+1][numMatrices+1];
			
			mult multObj = new mult();
			multObj.matrixChainOrder(m, s, numMatrices);
			multObj.printOptimalParen(s, 1, numMatrices, true);
			//Expected output has new line at the end of line one. This output is added in order to match same.  
			System.out.print("\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}
	}
	
	/*
	 * This method populates m and s matrices.
	 */
	public void matrixChainOrder(int[][] m, int[][] s, int n) {
		for (int i = 1; i <= n; i++) {
			m[i][i] = 0;
		}
		for (int l = 2; l <= n; l++) {
			for (int i = 1; i <= n-l+1; i++) {
				int j = i+l-1;
				m[i][j] = Integer.MAX_VALUE;
				for (int k = i; k <= j-1; k++) {
					//Find the minimum computation time at m[i][j]
					int q = m[i][k] + m[k+1][j] + dimensionList.get(i-1)*dimensionList.get(k)*dimensionList.get(j);
					if (q < m[i][j]) {
						m[i][j] = q;
						s[i][j] = k;
					}
				}
			}
		}
	}
	
	/**
	 * This method traverses s matrix in order to parenthesize matrices for optimal solution.
	 * Parameter fromMain is added to avoid parentheses around the entire expression.
	 * fromMain is set to true from main method and false otherwise.
	 */
	public void printOptimalParen(int[][] s, int i, int j, boolean fromMain) {
		if (i == j) {
			System.out.print("M" + i);
		} else {
			if (!fromMain) {
				System.out.print("( ");
			}
			printOptimalParen(s, i, s[i][j], false);
			System.out.print(" * ");
			printOptimalParen(s, s[i][j]+1, j, false);
			if (!fromMain) {
				System.out.print(" )");
			}
		}
	}
}
