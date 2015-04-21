
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;

//Date
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


//JSON
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSON {

    private long[] values;
    private String[] names;
    private String whichFuncReq = "";


    protected String path;
    protected String fileNa;
    private String fileExtension = ".txt";

    public JSON(String filePath, String fileName){
            //Open JSON
        this.path = filePath;
        this.fileNa = fileName;

        String InputFileLoc = path + fileNa + fileExtension;

        try{
            FileReader InputReader = new FileReader(InputFileLoc);

            //Contruct JSON array
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject;
            try{
                jsonObject = (JSONObject) jsonParser.parse(InputReader);
                JSONArray jArray = (JSONArray) jsonObject.get("dataseries");
                //Store data into our array values
                values = new long[jArray.size()];
                for (int i = 0; i < jArray.size(); i++) {
                    values[i] = (long) jArray.get(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public JSON (long []arr, String[] nam, int fr) {
        this.values = arr;
        this.names = nam;
        switch (fr) {
            case 1:
                this.whichFuncReq = "_distribution_";
                break;
            case 2:
                this.whichFuncReq = "_prediction_";
                break;
        }
    }

    public void outputFile(){
        JSONObject jsonObject = new JSONObject();

        if(whichFuncReq=="_distribution_"){
            for (int i = 0; i < names.length; i++) {
                jsonObject.put(names[i], values[i]);
            }
        }
        else{
            jsonObject.put(names[0], values[0]);
        }


        try {
            String content = jsonObject.toString();

            Date date = new Date();
            File file = new File(path + fileNa + whichFuncReq + date + fileExtension);

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public long[] getValues(){ return this.values; }

    public String getPath() { return path; }

    public String getFileNa() { return fileNa; }

    public void setPath(String path) { this.path = path; }

    public void setFileNa(String fileNa) { this.fileNa = fileNa; }

}
