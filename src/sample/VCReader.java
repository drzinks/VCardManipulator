package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by golebiow on 2016-03-22.
 */
public class VCReader implements Iterable<String> {

    private BufferedReader bufferedReader;
    private String line;
    private StringBuilder cachedFile = new StringBuilder();
    boolean fileBuffered = false;
    private Iterator<String> myIterator= new Iterator<String>() {

        @Override
        public boolean hasNext() {
            try {
                return bufferedReader.ready();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public String next() {
            while(hasNext()){
                try {
                    line = bufferedReader.readLine();
                    cachedFile = cachedFile.append(line);
                    cachedFile.append(System.getProperty("line.separator"));

                    if(line.contains("FN:")){
                        return line.substring(line.indexOf(":")+1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileBuffered = true;
            while (cachedFile.indexOf("FN;")!=-1)
                deleteContactFromCachedFile("FN;"); //REMOVE RUBBISH - EMPTY EMAILS
            while (cachedFile.indexOf("@")!=-1)
                deleteContactFromCachedFile("@"); //REMOVE RUBBISH - EMPTY EMAILS
            return null;
        }

        @Override
        public void remove() {

        }
    };

    public VCReader(Reader reader) {
        this.bufferedReader = new BufferedReader(reader);
    }

    @Override
    public Iterator<String> iterator() {
        return myIterator;
    }

    public void remove(String formalName, List<String> contactList){
        deleteContactFromCachedFile(formalName);
        File file = new File(System.getProperty("user.home")+"/Desktop"+"/manipulatedCard.vcf");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(cachedFile.toString());
            fileWriter.close();
            contactList.remove(formalName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteContactFromCachedFile(String formalName) {
        int middleIndex = cachedFile.indexOf(formalName);
        int firstIndex = cachedFile.lastIndexOf("BEGIN:VCARD",middleIndex);
        int lastIndex = cachedFile.indexOf("END:VCARD",middleIndex);
        lastIndex += "END:VCARD".length()+2;//+cr lf
        cachedFile.delete(firstIndex,lastIndex);
    }
}
