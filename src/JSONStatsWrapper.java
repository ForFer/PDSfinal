import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.Date;

public class JSONStatsWrapper {

    JSON json;
    JSON out;

    public JSONStatsWrapper(JSON in) {
        json = in;
    }

    /**
     * Computes the summation of one or several data arrays
     * (subtracted at each step) with optional power computing at each step
     * @param exp int with exponent of each summation step
     * @param typeOfSummation
     * values: 0 = no exp, 1 = 1/xi no exp, 2 = xi-mean(x) with exp, 3 = (Xi - /x)(Yi - /y)
     * @param optional second long array, used in type 3.
     * @return result of summation (long)
     */
    public long Summation(int exp, int typeOfSummation, long[] optional) {
        long sum = 0;
        long[] a = json.getValues();

        //This switch will compute the different types of summation.
        switch (typeOfSummation) {
            //Normal summation with no exp
            case 0:
                //Retrieve array of values
                sum = 0;
                for (int n = 0; n < a.length; n++) {
                    sum = sum + a[n];
                }
                break;

            //summation of 1/xi with no exp
            case 1:
                sum = 0;
                for (int n = 0; n < a.length; n++) {
                    sum = sum + (1 / a[n]);
                }
                break;

            //summation of xi - /x with exp
            //Not developed, due to unnecesariness
            case 2:
                break;

            //summation of (Xi - /x)(Yi - /y)
            //Not developed, due to unnecesariness
            case 3:
                break;

        }
        return sum;

    }

    /**
     * Compute quartiles, and percentiles if possible
     * In the srs example given, it says calculate nearest, but, if it's the lower bound, it can be actually lower than
     * the real n-th percentile/quartile, since it leaves behind less than n%
     * @throws IOException
     */
    public void verification() throws IOException {

        //We start by retrieving the array from the JSON
        long[] a = json.getValues();
        String[] names = new String[2];
        names[0] = "Distribution";
        names[1] = "Characteristic values";

        if(a.length<30 || a.length > 100000000){
            errorOutput();
        }
        else {

            /*CODIGO DE COMPROBAR CUAL ES LA DISTR*/

            out = new JSON(a, names, 1);
            out.setFileNa(json.getFileNa());
            out.setPath(json.getPath());
            out.outputFile();
        }
    }


    /**
     * Computation of mean, median, mode, geometric_mean, harmonic_mean
     * @throws IOException
     */
    public void predictionInterval() throws IOException {

        long[] a = json.getValues();
        String [] distr = {"Predicted interval"};
        int jsonSize = a.length;


        if(a.length<30 || a.length > 100000000){
            errorOutput();
        }
        else {

            /*CODIGO DE COMPROBAR CUAL ES LA DISTR*/

            out = new JSON(a, distr, 2);
            out.setFileNa(json.getFileNa());
            out.setPath(json.getPath());
            out.outputFile();
        }
    }

    /**
     * Writes a log whenever bounds for a computation are not fulfilled
     * @throws IOException
     */
    public void errorOutput() throws IOException {
        Date date = new Date();
        String content = "If numbers are shown, insufficient amount of values, if not, too large amount (min: 30, max = 10⁸)" + "\n";
        File file = new File(json.getPath() + json.getFileNa() + "_ERROR_" + date + "txt");
        long []a = json.getValues();
        int countNewLine = 0;

        if (a.length<30) {
            //Formatting the input sample in a text file, so that user can see the sample used.
            for (int i = 0; i < a.length; i++) {
                content = content +  a[i] + "\t";
                countNewLine++;
                if (countNewLine == 10) {
                    content = content + "\n";
                    countNewLine = 0;
                }
            }
        }
        // if file doesn't exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }
}