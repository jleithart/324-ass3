//MatrixMultiplication

public class MatrixMultiply {

	public static void main( String[] args )
     {
        double [][]M1 = {{1, 0, 0, 0},
        				 {0, 1, 0, 0},
        				 {0, 0, 1, 0},
        				 {0, 0, 0, 1},
    					};

		double [][]M2 = {{1, 0, 2, 0},
        				 {0, 1, 0, 0},
        				 {0, 0, 3, 0},
        				 {0, 0, 0, 1},
    					};
    	double [][]M3;

    	M3 = Multiply(M2, M1);

    	PrintMatrix(M3);

     }

	public static double [][] Multiply(double[][] M, double[][] N){
		assert(M[0].length == N.length);
		int mColumns = M.length;
		int mRows = M[0].length;
		int nColumns = N.length;
		int nRows = N[0].length;


		double [][]retval = new double[mRows][nColumns];
		for(int i = 0; i < mRows; i++){
			for(int j = 0; j < nColumns; j++){
				retval[i][j] = 0.0;
			}
		}
		double sum;

		for(int col = 0; col < mRows; col++){
			for(int row = 0; row < nColumns; row++){
				sum = 0.0;
				for(int index = 0; index < mColumns; index++){
					sum += M[col][index]*N[index][row];
				}
				retval[col][row] = sum;
			}
		}

		return retval;
	}

	public static void PrintMatrix(double [][]M){
		for(int i = 0; i < M[0].length; i++){
			for(int j = 0; j < M.length; j++){
				System.out.print(M[i][j] + " ");
			}
			System.out.println();
		}
	}
}