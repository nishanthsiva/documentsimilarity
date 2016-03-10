package lsh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import minhash.MinHash;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/6/16.
 */
public class LSH {
	
	private static final Logger LOGGER = Logger.getLogger(LSH.class.getName());
	
	private List<HashMap<Long, List<String>>> hashTables = null;
	private String[] docNames;
	private int[][] minHashMatrix;
	private Integer bands;
	
	//hash values 
	private int a;
	private int b;
	
	public LSH(int[][] minHashMatrix, String[] docNames, int bands) {
		this.docNames = docNames;
		this.minHashMatrix = minHashMatrix;
		this.bands = bands;
		int n = minHashMatrix[0].length;
		a = (int) (Math.random() * bands);
		b = (int) (Math.random() * bands);
		hashTables = new ArrayList<HashMap<Long, List<String>>>(bands);
		System.out.println(minHashMatrix.length/bands);
		for(int i = 0; i<bands; i++){
			hashTables.add(new HashMap<Long, List<String>>());
		}
		for (int document = 0; document < n; document++) {
			for (int band = 0; band < bands; band++) {
				Long hashValue = getHashValueForBandForDocument(document, band);
				if(hashTables.get(band).get(hashValue) == null) {
					hashTables.get(band).put(hashValue, new ArrayList<String>());
				}
				hashTables.get(band).get(hashValue).add(docNames[document]);
			}
		}
	}

	private Long getHashValueForBandForDocument(int document, int band) {
		Long hashSums = (long) 0;
		String bandValues = "";
		int k = minHashMatrix.length;
		int rowsPerBand = k/bands;
		int startIndex = band*rowsPerBand;
		int endIndex;
		
		// each band will contain k/bands values except for the last band
		// which may contain more
		if (band == bands - 1){
			endIndex = k - 1;
		} else {
			endIndex = startIndex + rowsPerBand - 1;
		}
		for(int row = startIndex; row < endIndex; row++) {
			 // hashSums += minHashMatrix[row][document];
			 //hashSums +=  minHashMatrix[row][document] * String.valueOf(row).hashCode();
			 bandValues += String.valueOf(minHashMatrix[row][document]);
		}
		// return hashValue(hashSums, bands);
		 return fnvHash64Bit(bandValues);
		// return hashSums;
	}
	
	private Long hashValue(Long number, Integer size){
		return (long) (a*number + b);
	}
	
	public String[] nearDuplicatesOf(String docName) {
		HashSet<String> nearDuplicates = new HashSet<String>();
		//find the index of the document
		Integer docIndex = null;
		for (int i=0 ; i<docNames.length; i++){
			if (docNames[i].equals(docName)){
				docIndex = i;
				break;
			}
		}
		// the document was not found
		if (docIndex == null) {
			LOGGER.severe("File not found - " + docName);
			return null;
		}
		// has the corresponding minHash matrix band values to find 
		for (int band = 0; band < bands; band++) {
			Long hashValue = getHashValueForBandForDocument(docIndex, band);
			nearDuplicates.addAll(hashTables.get(band).get(hashValue));
		}
		// as we put all documents in hashed bands into the set, this set
		// would also contain the document we are searching against itself
		nearDuplicates.remove(docName);
		return nearDuplicates.toArray(new String[0]);
	}
	
	public long fnvHash64Bit(String s) {
		long FNV_64_INIT = 0xcbf29ce484222325L;
	    long FNV_64_PRIME = 0x100000001b3L;
        long rv = FNV_64_INIT;
        int len = s.length();
        for(int i = 0; i < len; i++) {
            rv ^= s.charAt(i);
            rv *= FNV_64_PRIME;
        }
        return rv;
	}
	
	public static void main(String[] args){
		MinHash minHash = new MinHash("C://Users//Sriram//Desktop//Study//535//PA2//pa2-partial//", 400);
		minHash.computeMinHashSig();
		LSH lsh = new LSH(minHash.minHashMatrix(), minHash.allDocs(), 50);
		int count=0;
		String[] files = lsh.nearDuplicatesOf("space-1.txt");
		for(String file : files) {
			System.out.println(file);
			count++;
		}
		System.out.println(count);
	}
	
}
