import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
public class CustomCSVReader {
    public CustomCSVReader(){}
    public List<String[]> readCSVFile(String fileToPath) throws IOException {
        try {
            FileReader filereader = new FileReader(fileToPath);
            CSVReader csvReader = new CSVReader(filereader);
            List<String[]> records = new ArrayList<>();
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                records.add(nextRecord);
            }
            return records;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
