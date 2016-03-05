package minhash;

import utilities.FileUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
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
    private Set<String> termSet;
    private HashMap<String, Set<String>> termMap;

    public int getNumPermutations() {
        return numPermutations;
    }

    public void setNumPermutations(int numPermutations) {
        this.numPermutations = numPermutations;
    }

    public MinHash(String folder, int numPermutations){
        this.folder = folder;
        this.numPermutations = numPermutations;
        this.filenames = getAllFileNames();
        this.termMap = new HashMap<>();
        computeMinHashSig();
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
            String[] tokens = filepaths[i].split("/");
            filenames[i] = tokens[tokens.length-1];
        }

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return filenames;
    }

    private Set<String> getTermIntersection(Set<String> termSet1, Set<String> termSet2){
        final String METHOD_NAME = "getTermIntersection";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        Set<String> termSet = new TreeSet<String>();
        Iterator<String> file1TermIterator = termSet1.iterator();
        while(file1TermIterator.hasNext()){
            String term = file1TermIterator.next();
            if(termSet2.contains(term)){
                termSet.add(term);
            }
        }
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return termSet;
    }

    private void computeMinHashSig(){
        final String METHOD_NAME = "computeMinHashSig";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        createTermSet();
        createTermDocumentMatrix();


        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
    }

    private void createTermSet(){
        final String METHOD_NAME = "createTermSet";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        String [] filenames = allDocs();
        for(String file: filenames){
            String filepath = folder.endsWith("/") ? (folder + file) : (folder + "/" + file);
            Set<String> fileTerms = FileUtil.getFileTerms(filepath);
            this.termMap.put(file, fileTerms);
            termSet.addAll(fileTerms);
        }

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
    }

    private void createTermDocumentMatrix(){
        final String METHOD_NAME = "createTermDocumentMatrix";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        this.termDocumentMatrix = new int[termSet.size()][filenames.length];
        Iterator<String> termSetIterator = termSet.iterator();
        for(int i=0;i<termSet.size() && termSetIterator.hasNext();i++){
            String term = termSetIterator.next();
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
    public double exactJaccard(String file1, String file2){
        final String METHOD_NAME = "exactJaccard";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        double jaccardSim = 0.0;
        String file1path = folder.endsWith("/") ? (folder + file1) : (folder + "/" + file1);
        String file2path = folder.endsWith("/") ? (folder + file2) : (folder + "/" + file2);

        Set<String> file1Terms = FileUtil.getFileTerms(file1path);
        Set<String> file2Terms = FileUtil.getFileTerms(file2path);

        Set<String> termSet = getTermIntersection(file1Terms, file2Terms);

        file1Terms.addAll(file2Terms);
        jaccardSim = termSet.size()/(file1Terms.size());

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return jaccardSim;
    }


}
