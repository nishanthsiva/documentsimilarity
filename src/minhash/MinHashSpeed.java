package minhash;

import com.javafx.tools.doclets.formats.html.SourceToHTMLConverter;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/8/16.
 */
public class MinHashSpeed {

    public static void initializeMatrix(double[][] matrix, int rows, int columns, int initValue){
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                matrix[i][j] = initValue;
            }
        }
    }

    public static double[][] getExactJCForAllPairs(MinHash minHash){
        double[][] exactJC = new double[minHash.allDocs().length][minHash.allDocs().length];
        initializeMatrix(exactJC,minHash.allDocs().length,minHash.allDocs().length,-1);

        for(int i=0;i<minHash.allDocs().length;i++){
            for(int j=0;j<minHash.allDocs().length;j++){
                if(i==j){
                    exactJC[i][j] = 1;
                }
                else if(exactJC[i][j] == -1){
                    exactJC[i][j] = minHash.exactJaccard("space-"+i+".txt","space-"+j+".txt");
                    exactJC[j][i] = exactJC[i][j];
                }
            }
        }
        return exactJC;

    }

    public static double[][] getApproxJCForAllPairs(MinHash minHash){
        double[][] approxJC = new double[minHash.allDocs().length][minHash.allDocs().length];
        initializeMatrix(approxJC,minHash.allDocs().length,minHash.allDocs().length,-1);
        minHash.computeMinHashSig();

        for(int i=0;i<minHash.allDocs().length;i++){
            for(int j=0;j<minHash.allDocs().length;j++){
                if(i==j){
                    approxJC[i][j] = 1;
                }
                else if(approxJC[i][j] == -1){
                    approxJC[i][j] = minHash.approximateJaccard("space-"+i+".txt","space-"+j+".txt");
                    approxJC[j][i] = approxJC[i][j];
                }
            }
        }

        return approxJC;
    }

    public static void main(String args[]){
        MinHash minHash = new MinHash("/Users/nishanthsivakumar/Documents/ISU-CS/COMS-535/space/",400);

        double[][] exactJC = new double[minHash.allDocs().length][minHash.allDocs().length];
        initializeMatrix(exactJC,minHash.allDocs().length,minHash.allDocs().length,-1);

        long startTime = System.currentTimeMillis();
        exactJC = getExactJCForAllPairs(minHash);
        long timeTaken = System.currentTimeMillis() - startTime;
        System.out.printf("Time Taken to Compute Exact Jaccard Similarity = "+(timeTaken/1000)+" seconds");

        double[][] approxJC = new double[minHash.allDocs().length][minHash.allDocs().length];
        initializeMatrix(approxJC,minHash.allDocs().length,minHash.allDocs().length,-1);

        startTime = System.currentTimeMillis();
        approxJC = getApproxJCForAllPairs(minHash);
        timeTaken = System.currentTimeMillis() - startTime;
        System.out.printf("Time Taken to Compute Approximate Jaccard Similarity = "+(timeTaken/1000)+" seconds");
    }
}
