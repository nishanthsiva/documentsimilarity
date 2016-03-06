package lsh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/6/16.
 */
public class LSH {
	
	private List<HashMap<Integer, String>> hashTables = null;
	private String[] docNames;
	private Integer[][] minHashMatrix;
	private Integer bands;
	
	//hash values 
	private int a;
	private int b;
	
	public LSH(Integer[][] minHashMatrix, String[] docNames, int bands) {
		this.docNames = docNames;
		this.minHashMatrix = minHashMatrix;
		this.bands = bands;
		int n = minHashMatrix.length;
		a = (int) (Math.random() * bands);
		b = (int) (Math.random() * bands);
		hashTables = new ArrayList<HashMap<Integer, String>>(bands);
		for(int i = 0; i<bands; i++){
			hashTables.set(i, new HashMap<Integer, String>());
		}
		for (int document = 0; document < n; document++) {
			breakBandsAndWrite(document);
		}
	}

	private void breakBandsAndWrite(int document) {
		for (int band = 0; band < bands; band++) {
			Integer hashValue = getHashValueForBandForDocument(document, band);
			hashTables.get(bands).put(hashValue, docNames[document]);
		}
	}

	private Integer getHashValueForBandForDocument(int document, int band) {
		Integer hashSums = 0;
		int k = minHashMatrix[0].length;
		int rowsPerBand = k/bands;
		int startIndex = band*rowsPerBand;
		int endIndex;
		if (band == bands - 1){
			endIndex = k;
		} else {
			endIndex = startIndex + rowsPerBand;
		}
		for(int row = startIndex; row < endIndex; row++) {
			hashSums += minHashMatrix[document][row];
		}
		return hashValue(hashSums, bands);
	}
	
	private Integer hashValue(Integer number, Integer size){
		return (a*number + b)%size;
	}
	
	public String[] nearDuplicatesOf(String docName) {
		ArrayList<String> nearDuplicates = new ArrayList<String>();
		
		return nearDuplicates.toArray( new String[0]);
	}
	
}
