package sample;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by golebiow on 2016-03-22.
 */
public class VCReader implements Iterable<String> {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String VCARD_FN = "FN:";
    public static final String VCARD_N = "N:";
    public static final String COLON = ":";
    public static final String USER_HOME_SYSTEM_PROPERTY = System.getProperty("user.home");
    public static final String DESKTOP = "/Desktop";
    public static final String MANIPULATED_VCF_FILE_NAME = "/manipulatedCard.vcf";
    public static final String BEGIN_VCARD_INDICATOR = "BEGIN:VCARD";
    public static final String END_VCARD_INDICATOR = "END:VCARD";
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
                    cachedFile.append(LINE_SEPARATOR);

                    if(line.contains(VCARD_FN)){                              //FN: 2.0    - supported,3.0-4.0-required
                        return line.substring(line.indexOf(COLON)+1);         //N:  2.0-3. - required,     4.0-supported
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileBuffered = true;
//            while (cachedFile.indexOf("FN;")!=-1)
//                deleteContactFromCachedFile("FN;"); //REMOVE RUBBISH - EMPTY EMAILS
//            while (cachedFile.indexOf("@")!=-1)
//                deleteContactFromCachedFile("@"); //REMOVE RUBBISH - EMPTY EMAILS
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
        File file = new File(USER_HOME_SYSTEM_PROPERTY + DESKTOP + MANIPULATED_VCF_FILE_NAME);
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
        int firstIndex = cachedFile.lastIndexOf(BEGIN_VCARD_INDICATOR,middleIndex);
        int lastIndex = cachedFile.indexOf(END_VCARD_INDICATOR,middleIndex);
        lastIndex += END_VCARD_INDICATOR.length()+LINE_SEPARATOR.length(); //CR+LF under Windows
        cachedFile.delete(firstIndex,lastIndex);
    }
}
