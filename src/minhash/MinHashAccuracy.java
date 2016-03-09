package minhash;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/8/16.
 */
public class MinHashAccuracy {

    public static void main(String args[]){
        int [] permutations = {400,600,800};
        double[] errorMargin = {0.04,0.06,0.09};

        int resultIndex = 0;
        int accuracyArray[] = new int[9];

        for(int i=0;i<permutations.length;i++){
            MinHash minHash = new MinHash("/Users/nishanthsivakumar/Documents/ISU-CS/COMS-535/space/", permutations[i]);
            System.out.println(permutations[i]+" Permutations - ");
            for(int j=0;j<errorMargin.length;j++) {
                System.out.print(errorMargin[j]+"\t");
                double exactJC[][] = MinHashSpeed.getExactJCForAllPairs(minHash);
                double approxJC[][] = MinHashSpeed.getApproxJCForAllPairs(minHash);
                for (int x = 0; x < minHash.getNumPermutations(); x++) {
                    for (int y = 0; y < minHash.allDocs().length; y++) {
                        if((exactJC[x][y] - approxJC[x][y]) > errorMargin[j]){
                            accuracyArray[resultIndex] += 1;
                        }
                    }
                }
                System.out.println(accuracyArray[resultIndex]);
                resultIndex++;
            }
        }
    }
}
