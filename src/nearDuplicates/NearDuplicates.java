package nearDuplicates;

import lsh.LSH;
import minhash.MinHash;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/6/16.
 */
public class NearDuplicates {
	
	public static String[] sSimilarDocuments(String docFolder, Integer numPermutations, Integer numBands, Float similarityThreshold, String docName) {
		MinHash minHash = new MinHash(docFolder, numPermutations);
		minHash.computeMinHashSig();
		LSH lsh = new LSH(minHash.minHashMatrix(), minHash.allDocs(), numBands);
		String[] nearSimilarFile = lsh.nearDuplicatesOf(docName);
		for ( String file : nearSimilarFile) {
			System.out.println(file + " - " + minHash.approximateJaccard(file, docName));
		}
		return nearSimilarFile;
	}
	
	public static void main(String[] args){
		String docFolder = "C://Users//Sriram//Desktop//Study//535//PA2//pa2-partial//";
		Integer numPermutations = 300;
		Integer numBands = 50;
		Float similarityThreshold = 0.9f;
		String docName = "space-0.txt";
		sSimilarDocuments(docFolder, numPermutations, numBands, similarityThreshold, docName);
	}
}
