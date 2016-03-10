package minhash;

import objects.Permutation;
import utilities.FileUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/5/16.
 */
public class MinHash {

    private static final Logger LOGGER = Logger.getLogger(MinHash.class.getName());
    private String CLASS_NAME = MinHash.class.getName();

    private String folder;
    private int numPermutations;
    private String[] filenames;
    private int[][] termDocumentMatrix;
    private int[][] minHashMatrix;
    private List<String> termList;
    private HashMap<String, Set<String>> termMap;
    private Permutation[] permutations;

    public int getNumPermutations() {
        return numPermutations;
    }

    public void setNumPermutations(int numPermutations) {
        this.numPermutations = numPermutations;
    }

    public int numTerms(){
        return this.termList.size();
    }

    public int[][] minHashMatrix(){
        return this.minHashMatrix;
    }

    public MinHash(String folder, int numPermutations){
        this.folder = folder;
        this.numPermutations = numPermutations;
        this.filenames = getAllFileNames();
        this.termMap = new HashMap<>();
        this.termList = new ArrayList<>();
        this.permutations = new Permutation[numPermutations];
        this.minHashMatrix = new int[numPermutations][filenames.length];
        createTermSet();
    }

    public String[] allDocs(){
        return filenames;
    }

    private String[] getAllFileNames(){
        final String METHOD_NAME = "getAllFileNames";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        String[] filepaths = FileUtil.getFiles(folder).toArray(new String[1]);
        String[] filenames = new String[filepaths.length];
        for(int i=0;i<filepaths.length;i++){
            File file = new File(filepaths[i]);
            filenames[i] = file.getName();
            LOGGER.log(Level.FINE, filenames[i]);
        }

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return filenames;
    }

    public void computeMinHashSig(){
        final String METHOD_NAME = "computeMinHashSig";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        LOGGER.log(Level.INFO, this.termList.size()+"");
        createTermDocumentMatrix();
        generatePermutations();
        LOGGER.log(Level.INFO,"Generated Permutations!");
        //compute minhashsignature
        for(int i=0;i<this.numPermutations; i++){
            Permutation currentPerm = permutations[i];
            for(int j=0;j<this.filenames.length;j++){
                minHashMatrix[i][j] = getMinimumTerm(getTermDocumentColumn(j),currentPerm).hashCode();
            }
        }
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
    }

    private int getFileIndex(String filename){
        final String METHOD_NAME = "getFileIndex";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        int columnIndex = -1;
        for(int i=0;i<filenames.length;i++) {
            if (filenames[i].equals(filename)) {
                columnIndex = i;
                break;
            }
        }

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return columnIndex;
    }

    public int[] minHashSig(String filename){
        final String METHOD_NAME = "minHashSig";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        int columnIndex = getFileIndex(filename);
        int[] docMinHashSig = new int[this.numPermutations];

        if(columnIndex != -1){
            for(int i=0;i<this.numPermutations;i++){
                docMinHashSig[i] = this.minHashMatrix[i][columnIndex];
            }
        }

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return docMinHashSig;
    }

    private int[] getTermDocumentColumn(int columnNumber){
        final String METHOD_NAME = "getTermDocumentColumn";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        LOGGER.log(Level.FINER,"Col Num - "+columnNumber);
        int[] termDocColumn = new int[this.termList.size()];
        for(int i=0;i<termList.size();i++){
            termDocColumn[i] = termDocumentMatrix[i][columnNumber];
        }

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return termDocColumn;
    }

    private int getNextPrime(int startNumber){
        final String METHOD_NAME ="getNextPrime";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        SecureRandom random = new SecureRandom();
        BigInteger nextPrime = new BigInteger(this.termList.size()+"");
        nextPrime = nextPrime.nextProbablePrime();

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return nextPrime.intValue();


    }
    private void generatePermutations(){
        final String METHOD_NAME = "generatePermutations";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);
        //TODO Verify this randomization
        int prime = getNextPrime(termList.size());
        LOGGER.log(Level.INFO,"Prime found at "+prime);
        Set<Integer> aSet = new TreeSet<>();
        Set<Integer> bSet = new TreeSet<>();
        Random random = new Random();
        int a = random.nextInt(prime-1), b = random.nextInt(prime-1);
        int i=0;
        while(i< this.numPermutations){
            if(!aSet.contains(new Integer(a)) || !bSet.contains(new Integer(b))){
                LOGGER.log(Level.FINE,"Found "+a+" and "+b+" for perm "+(i+1));
                permutations[i] = new Permutation(a,b);
                i++;
                aSet.add(new Integer(a));
                bSet.add(new Integer(b));
            }
            a = random.nextInt(prime-1);
            b = random.nextInt(prime-1);
        }
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
    }
    private String getMinimumTerm(int[] termDocument, Permutation p){
        final String METHOD_NAME = "getMinimumTerm";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        List<String> occurringTerms = new ArrayList<>();

        for(int i=0;i<termDocument.length;i++){
            if(termDocument[i] == 1){
                int index = ((p.getA()+i)+p.getB())%this.termList.size();
                occurringTerms.add(this.termList.get(index));
            }
        }
        Collections.sort(occurringTerms);

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return occurringTerms.get(0);
    }

    private void createTermSet(){
        final String METHOD_NAME = "createTermSet";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        String [] filenames = allDocs();
        LOGGER.log(Level.INFO,filenames.length+" files read!");
        Set<String> localTermSet = new TreeSet<>();
        for(String file: filenames){
            Set<String> fileTerms = FileUtil.getFileTerms(this.folder+file);
            this.termMap.put(file, fileTerms);
            localTermSet.addAll(fileTerms);
        }
        this.termList.addAll(localTermSet);
        LOGGER.log(Level.INFO, this.termList.size()+" terms found!");
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
    }

    private void createTermDocumentMatrix(){
        final String METHOD_NAME = "createTermDocumentMatrix";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        this.termDocumentMatrix = new int[termList.size()][filenames.length];
        for(int i = 0; i< termList.size(); i++){
            String term = this.termList.get(i);
            for(int j=0;j<filenames.length;j++){
                String filename = filenames[j];
                if(this.termMap.get(filename).contains(term)){
                    this.termDocumentMatrix[i][j] = 1;
                }else{
                    this.termDocumentMatrix[i][j] = 0;
                }
            }
        }
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
    }

    private Set<String> getTermIntersection(Set<String> termSet1, Set<String> termSet2){
        final String METHOD_NAME = "getTermIntersection";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        Set<String> intersectionSet = new TreeSet<>();
        Iterator<String> file1TermIterator = termSet1.iterator();
        while(file1TermIterator.hasNext()){
            String term = file1TermIterator.next();
            if(termSet2.contains(term)){
                intersectionSet.add(term);
            }
        }
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return intersectionSet;
    }

    public double exactJaccard(String file1, String file2){
        final String METHOD_NAME = "exactJaccard";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        double jaccardSim = 0.0;
        Set<String> file1Terms = this.termMap.get(file1);
        Set<String> file2Terms = this.termMap.get(file2);

        Set<String> termIntersection = getTermIntersection(file1Terms, file2Terms);


        Set<String> unionSet = new TreeSet<>();
        unionSet.addAll(file1Terms);
        unionSet.addAll(file2Terms);
        jaccardSim = (double)termIntersection.size()/(unionSet.size());

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return jaccardSim;
    }

    public double approximateJaccard(String file1, String file2){
        final String METHOD_NAME = "approximateJaccard";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        int[] file1MinHash = minHashSig(file1);
        int[] file2MinHash = minHashSig(file2);

        double matchingCount = 0;

        for(int i=0;i<file1MinHash.length;i++){
            if(file1MinHash[i] == file2MinHash[i]) {
                matchingCount++;
            }
        }
        LOGGER.log(Level.FINE,"MatchingCount - "+matchingCount);
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return (matchingCount/file1MinHash.length);
    }

    public static void main(String args[]){
        MinHash minHash = new MinHash("/Users/nishanthsivakumar/Documents/ISU-CS/COMS-535/space/",400);
        double[][] exactJC = new double[4][4];
        double[][] approxJC = new double[4][4];

        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(exactJC[i][j] == 0){
                    exactJC[i][j] = minHash.exactJaccard("space-"+i+".txt","space-"+j+".txt");
                }
                if(approxJC[i][j] == 0){
                    approxJC[i][j] = minHash.approximateJaccard("space-"+i+".txt","space-"+j+".txt");
                }
            }

        }
        for(int i=0;i<4;i++) {
            for (int j = 0; j <4; j++) {
                System.out.print(String.format("%.2f",(float)exactJC[i][j])+"\t\t\t");
            }
            System.out.println();
        }
        System.out.println("\n");
        for(int i=0;i<4;i++) {
            for (int j = 0; j <4; j++) {
                System.out.print(String.format("%.2f",(float)approxJC[i][j])+"\t\t\t");
            }
            System.out.println();
        }


    }


}
