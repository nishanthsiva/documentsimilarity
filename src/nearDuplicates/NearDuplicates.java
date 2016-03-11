package nearDuplicates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
		List<String> files = new ArrayList<String>(Arrays.asList(nearSimilarFile));
		Iterator<String> itr = files.iterator();
		int falsePostives = 0;
		while (itr.hasNext()){
			if(minHash.approximateJaccard(itr.next(), docName) < similarityThreshold) {
				falsePostives++;
				itr.remove();
			}
		}
		System.out.println("False postives - " + falsePostives);
		return files.toArray(new String[0]);
	}
	
	public static void main(String[] args){
		String docFolder = "C://Users//Sriram//Desktop//Study//535//PA2//pa2-partial//";
		Integer numPermutations = 300;
		Integer numBands = 50;
		Float similarityThreshold = 0.8f;
		String docName = "space-0.txt";
		String[] nearSimilarFile = sSimilarDocuments(docFolder, numPermutations, numBands, similarityThreshold, docName);
		for ( String file : nearSimilarFile) {
			System.out.println(file);
		}
	}
}
