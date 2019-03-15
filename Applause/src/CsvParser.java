import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    public static List<List<String>> parseCSV(String fileName) {
        String fileToParse = fileName;
        BufferedReader fileReader = null;
        List<List<String>> res = new ArrayList<>();

        try {
            fileReader = new BufferedReader(new FileReader(fileToParse));

            //Go through all lines in CSV file
            String line = fileReader.readLine();
            while (line != null) {

                List<String> row = new ArrayList<>();
                //Get all attributes in line
                String[] attributes = line.split(",");
                for(String attribute : attributes) {
                    //remove the double quote in attribute
                    row.add(attribute.replaceAll("\"", ""));
                }
                res.add(row);
                //read next line
                line = fileReader.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //remove the title of CSV file
        if (res.size() > 1) {
            res.remove(0);
        }
        return res;

    }
}
