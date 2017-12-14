import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Indexer {
    LinkedHashMap<String, LinkedList<Document>> Docs;
    HashMap<String, Integer> termDf;
    public static int i = 0;
    public int discLine;
    public int cacheLine;
    public static boolean stemOrNot;

    public Indexer() {
        new File("PostingList").mkdir();
        discLine = 0;
        cacheLine=0;
        stemOrNot = true;
    }

    public void setDocs(LinkedHashMap<String, LinkedList<Document>> _docs) {
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
                    bw.write("<" + document.docId + "," + document.terms.get(key).getTf() + "," + document.terms.get(key).getFirst_index() + ">" + " ");
                    bw.flush();
                }
                bw.newLine();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void mergeFiles() {
        //Sorting Cache
        HashSet<String> cache = readCache();
        //Get Document Frequency
        HashMap<String, Integer> df = new StemmerGenerator().getDf();
        //Initial Stage
        //Upload 3 LINE FROM EACH FILE
        File folder = new File("PostingList");
        File[] listOfFiles = folder.listFiles();
        try {
            //BUFFER READER FOR EACH FILE
            BufferedReader[] bufferedReaderArr = new BufferedReader[73];
            //priority Queue of terms
            PriorityQueue<termLine> pQ = new PriorityQueue<termLine>();
            //hash function
            //Key->termLine Value->buffer reader
            HashMap<termLine, LinkedList<BufferedReader>> readFromFileHash = new HashMap<termLine, LinkedList<BufferedReader>>();
            int k = 0;
            for (File f : listOfFiles) {
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
            BufferedWriter brCache = new BufferedWriter(new FileWriter("Cache.txt"));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("FinalPostingList.txt"));
            BufferedWriter brDf = new BufferedWriter(new FileWriter("Df.txt"));
            while ((readFromFileHash.size() != 0 || pQ.size() != 0)) {
                //Move The Best Choice Into The Disc
                termLine best = pQ.poll();
                LinkedList<BufferedReader> brArr = readFromFileHash.get(best);
                //Unique Key
                if (brArr.size() == 1) {
                    //Write 30% Of The Cache Posting List
                    if (cache.contains(best.term)) {
                        brCache.write(best.term + PartialPosting(best.Link));
                        brCache.flush();
                        brCache.newLine();
                        brDf.write("Key= " + best.term + " Document Frequency= " + df.get(best.term) + " C= " + cacheLine);
                        brDf.newLine();
                        brDf.flush();
                        cacheLine++;
                    } else { //Write The Dictionary
                        //Pointer To Disc
                        brDf.write("Key= " + best.term + " Document Frequency= " + df.get(best.term) + " D= " + discLine);
                        brDf.newLine();
                        brDf.flush();
                        discLine++;
                    }
                    //Write The Final Posting List
                    String s = best.term + best.Link;
                    bufferedWriter.write(s);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    //Read The Next Line If Exists
                    BufferedReader br = brArr.getFirst();
                    if (br != null) {
                        String line = br.readLine();
                        readFromFileHash.remove(best);
                        //IF THE FILE HAS AT LEAST ONE OR MORE LINE THAN CONTINUE THE READING, ELSE STOP.
                        if (line != null) {
                            termLine lineCon = convertToTermLine(line);
                            pQ.add(lineCon);
                            addHash(lineCon, br, readFromFileHash);
                        }
                    }
                }
                //Duplicate Keys
                else if (brArr.size() > 1) {
                    //GET THE SECOND DUPLICATED KEY
                    termLine best1 = pQ.poll();
                    BufferedReader br1 = brArr.removeFirst();
                    String s = best.term + " " + mergePostEqualTerms(best.Link, best1.Link);
                    termLine merged = convertToTermLine(s);
                    pQ.add(merged);
                    //UPDATE KEY IN HASH MAP (first remove and then add again, the brute force way)
                    LinkedList<BufferedReader> temp = readFromFileHash.get(merged);
                    readFromFileHash.remove(merged);
                    readFromFileHash.put(merged, temp);
                    //randomly read one line in one of the files
                    String line = br1.readLine();
                    if (line != null) {
                        termLine termNext1 = convertToTermLine(line);
                        addHash(termNext1, br1, readFromFileHash);
                        pQ.add(termNext1);
                    }
                }

            }
            //Close All The Buffers
            for (BufferedReader br : bufferedReaderArr) {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Return 10 Doc for each term
    private String PartialPosting(String posting) {
        int counter = 0;
        StringBuilder st = new StringBuilder(256);
        for (int j = 0; j < posting.length() && counter<100; j++) {
            char c = posting.charAt(j);
            if ((c == ' '))
                counter++;
            st.append(c);
        }
        return st.toString();
    }

    private HashSet<String> readCache() {
        HashSet<String> cache = new HashSet<>();
        try {
            BufferedReader br;
            if (stemOrNot) {
                br = new BufferedReader(new FileReader("cacheWordsStemmed.txt"));
            } else {
                br = new BufferedReader(new FileReader("cacheWordsNotStemmed.txt"));
            }
            String line;
            while ((line = br.readLine()) != null) {
                cache.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cache;
    }

    //MERGING VALUES EQUALS KEYS
    //FOR EXAMPLE DOG->D1 AND DOG->D2 => DOG->D1,D2
    private String mergePostEqualTerms(String link, String link1) {
        Pattern hyphen = Pattern.compile("\\s+");
        String[] newl0 = hyphen.split(link);
        String[] newline1 = hyphen.split(link1);
        StringBuilder s = new StringBuilder();
        int index0, index1, close0, close1, i = 0, j = 0;
        String tf0, tf1;
        while (i < newl0.length && j < newline1.length) {
            index0 = newl0[i].indexOf(',', 0);
            index1 = newline1[j].indexOf(',', 0);
            close0 = newl0[i].indexOf(',', index0 + 1);
            close1 = newline1[j].indexOf(',', index1 + 1);
            tf0 = newl0[i].substring(index0 + 1, close0);
            tf1 = newline1[j].substring(index1 + 1, close1);
            //-------
            if (tf0.contains(",") || tf1.contains(",")) {
                System.out.println(link);
                System.out.println(link1);
                System.out.println(tf0);
                System.out.println(tf1);
            }
            int x = Integer.parseInt(tf0);
            int y = Integer.parseInt(tf1);
            if (x > y) {
                s.append(newl0[i] + " ");
                i++;
            } else if (y > x) {
                s.append(newline1[j] + " ");
                j++;
            } else {
                s.append(newl0[i] + " ");
                s.append(newline1[j] + " ");
                i++;
                j++;
            }
        }
        int k, q;
        for (k = i; k < newl0.length - 1; k++) {
            s.append(newl0[k] + " ");
        }
        if (k < newl0.length) {
            s.append(newl0[k]);
        }

        for (q = j; q < newline1.length - 1; q++) {
            s.append(newline1[q] + " ");
        }
        if (q < newline1.length) {
            s.append(newline1[q]);
        }
        return s.toString();
    }

    //extracting the tf's from the string
    private static String getTF(String st) {
        int index = st.indexOf(',');
        int close = st.indexOf('>');
        return st.substring(index + 1, close);
    }

    //ADD FUNCTION THAT ALLOWS DUPLICATE VALUES BY COLLISIONS, EACH DUPLICATED VALUE IS ADDED TO THE HASH MAP VALUE.
    private void addHash(termLine key, BufferedReader value, HashMap<termLine, LinkedList<BufferedReader>> hashMap) {
        LinkedList<BufferedReader> tempList = null;
        if (hashMap.containsKey(key)) {
            tempList = hashMap.get(key);
            tempList.add(value);
        } else {
            tempList = new LinkedList<BufferedReader>();
            tempList.add(value);
        }
        hashMap.put(key, tempList);
    }

    private termLine convertToTermLine(String line) {
        int index = line.indexOf("<");
        return new termLine(line.substring(0, index - 1), line.substring(index, line.length()));
    }

}
