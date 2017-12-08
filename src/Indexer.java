import java.io.*;
import java.util.*;

public class Indexer {
    TreeMap<String, LinkedList<Document>> Docs;
    public static int i = 0;

    public Indexer() {
        new File("PostingList").mkdir();
    }


    public void setDocs(TreeMap<String, LinkedList<Document>> _docs) {
        this.Docs = _docs;
        tempPosting();
    }

    private void tempPosting() {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("PostingList//postingList" + i + ".txt"));
            i++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, LinkedList<Document>> entry : Docs.entrySet()) {
            String key = entry.getKey();
            LinkedList<Document> value = entry.getValue();
            try {
                bw.write(key + " ");
                for (Document document : value) {
                    bw.write("<" + document.docId + "," + document.termFr.get(key) + ">" + " ");
                    bw.flush();
                }
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (i == 71) {
            mergeFiles();
        }
    }

    private void mergeFiles() {
        //Initial Stage
        //Upload 3 LINE FROM EACH FILE
        File folder = new File("PostingList");
        File[] listOfFiles = folder.listFiles();
        try {
            //BUFFER READER FOR EACH FILE
            BufferedReader[] bufferedReaderArr = new BufferedReader[71];
            //priority Queue of terms
            PriorityQueue<termLine> pQ = new PriorityQueue<termLine>();
            //hash function
            //Key->termline Value->buffer reader
            HashMap<termLine, ArrayList<BufferedReader>> readFromFileHash = new HashMap<termLine, ArrayList<BufferedReader>>();
            for (File f : listOfFiles) {
                int k = 0;
                bufferedReaderArr[k] = new BufferedReader(new FileReader(f));
                //Reads 3 lines from each file
                String line1 = bufferedReaderArr[k].readLine();
                String line2 = bufferedReaderArr[k].readLine();
                String line3 = bufferedReaderArr[k].readLine();
                //convert 3 lines into termLine
                termLine t1 = convertToTermLine(line1);
                termLine t2 = convertToTermLine(line2);
                termLine t3 = convertToTermLine(line3);
                //add 3 line into ordered Queue
                pQ.add(t1);
                pQ.add(t2);
                pQ.add(t3);
                //add 3 line into hash that allows duplicate Keys
                addHash(t1, bufferedReaderArr[k], readFromFileHash);
                addHash(t2, bufferedReaderArr[k], readFromFileHash);
                addHash(t3, bufferedReaderArr[k], readFromFileHash);
                //get to the next file
                k++;
            }
            while (readFromFileHash.size() == 0 || pQ.size() == 0) {
                //Move The Best Choice Into The Disc
                //IF THERE ARE COUPLE OF CHOICES THAT ARE WITH THE SAME KEY, MERGE THEM UP
                termLine best = pQ.poll();
                ArrayList<BufferedReader> brArr = readFromFileHash.get(best);
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("FinalPostingList"));
                //Unique Key
                if (brArr.size() == 1) {
                    String s = best.term + best.Link;
                    bufferedWriter.write(s);
                    //Read Next Line If Exists
                    BufferedReader br=brArr.get(0);
                    String line = br.readLine();
                    readFromFileHash.remove(best);
                    if (line!=null) {
                        termLine lineCon=convertToTermLine(line);
                        pQ.add(lineCon);
                        addHash(lineCon,br,readFromFileHash);
                    }
                }
                //Duplicate Keys
                else if (brArr.size() > 1) {
                    termLine best1 = pQ.poll();
                    BufferedReader br1 = brArr.get(0);
                    brArr.remove(0);
//                    if (brArr.size() == 0) {
//                        readFromFileHash.remove(best);
//                    }
                    String s = best.term + mergePostEqualTerms(best.Link, best1.Link);
                    termLine merged= convertToTermLine(s);
                    pQ.add(merged);

                    //UPDATE KEY IN HASH MAP
                    ArrayList<BufferedReader> temp= readFromFileHash.get(merged);
                    readFromFileHash.remove(merged);
                    readFromFileHash.put(merged,temp);

                    //randomly read one line in one of the files
                    termLine termNext1=convertToTermLine(br1.readLine());
                    addHash(termNext1,br1,readFromFileHash);
                    pQ.add(termNext1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String mergePostEqualTerms(String link, String link1) {
        return "";
    }

    private void addHash(termLine key, BufferedReader value, HashMap<termLine, ArrayList<BufferedReader>> hashMap) {
        ArrayList<BufferedReader> tempList = null;
        if (hashMap.containsKey(key)) {
            tempList = hashMap.get(key);
            if (tempList == null)
                tempList = new ArrayList<BufferedReader>();
            tempList.add(value);
        } else {
            tempList = new ArrayList<BufferedReader>();
            tempList.add(value);
        }
        hashMap.put(key, tempList);
    }

    private termLine convertToTermLine(String line) {
        int index = line.indexOf("<") - 1;
        return new termLine(line.substring(0, index), line.substring(index + 1, line.length()));
    }

    //sorting by tf
    private void merge(String line0, String line1) {
        int index0 = 0, index1 = 0, close0 = 0, close1 = 0, i, j;
        index0 = line0.indexOf(',', index0);
        index1 = line1.indexOf(',', index1);
        close0 = line0.indexOf('>', close0);
        close1 = line1.indexOf('>', close1);
    }
}
