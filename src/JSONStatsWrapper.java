import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Date;

public class JSONStatsWrapper {

    JSON json;
    JSON out;

    /**
     * Constructor of Wrapper given a JSON object
     * @param in JSON
     */
    public JSONStatsWrapper(JSON in) {
        json = in;
    }

    /**
     * Determines which distribution does a given data series follow
     * and computes its characteristic values
     * @throws IOException
     */
    public void verification() throws IOException {

        //We start by retrieving the array from the JSON
        long[] a = json.getValues();
        String[] names = new String[2];
        names[0] = "Distribution";
        names[1] = "Characteristic values";

        if(a.length<20 || a.length > 100000000){
            errorOutput();
        }
        else {
            //Check distibution type

            boolean isNormal = testChiSquare(a);


            out = new JSON(a, names, 1);
            out.setFileName(json.getFileName());
            out.setPath(json.getPath());
            out.outputFile();
        }
    }


    /**
     * Computes the 95% prediction interval of the data series
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
            //Check distibution type


            out = new JSON(a, distr, 2);
            out.setFileName(json.getFileName());
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
        String content = "Quantity of elements in series out of bounds (min: 30, max: 10⁸)" + "\n";
        File file = new File(json.getPath() + json.getFileName() + "_ERROR_" + date + "txt");
        long []a = json.getValues();

        // if file doesn't exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }

    boolean testChiSquare(long [] a){
        //Compute mean and std. deviation
        long mean = summation(a) / a.length;
        double stdDev = Math.sqrt( 1/a.length * summation(a, mean, 2));

        //create array of integer data series
        int [] intValues = new int[a.length];
        for(int i=0; i<a.length; i++){
            intValues[i] = (int) a[i];
        }
        //Store each unique integer into ArrayList, and frequency in another one
        boolean exists; //Avoid storing if already exists
        ArrayList<Integer> uniques = new ArrayList<Integer>(0);
        ArrayList<Integer> frequency  = new ArrayList<Integer>(0);

        //Outer loop traverses all integers
        for(int i=0; i<intValues.length; i++){
            exists = false;

            //Inner loop traverses ArrayList of uniques
            for(int j=0; j<uniques.size(); j++){
                //If already in Arraylist, mark it and add to frequency
                if(intValues[i]==uniques.get(j)) {
                    exists = true;
                    int newfreq = frequency.get(j)+1;
                    frequency.set(j, newfreq);
                }
            }
            //If not marked, add to arraylist
            if(exists==false) uniques.add(intValues[i]);
        }

        //We compute now the density function and Chi-square for each unique
        long densityFn [] = new long[uniques.size()];
        long chisquare [] = new long[uniques.size()];
        for(int i=0; i<densityFn.length; i++){
            densityFn[i] = (long) (1 / (stdDev * Math.sqrt(2 * Math.PI)) *
                    (Math.exp(-1/2 * Math.pow((uniques.get(i) - mean), 2) / stdDev)));

            chisquare[i] = (long) (Math.pow((frequency.get(i) - densityFn[i] * 100), 2) / (densityFn[i] * 100));
        }
        long chiSum = summation(chisquare);

        //Determine whether it's normal or not
        boolean isNormal;

        return isNormal;
    }

    /**
     * Computes the summation of one data array
     * @param arr array of data to sum
     * @return result of summation (long)
     */
    public long summation(long[] arr) {
        long sum = 0;
        for (int n = 0; n < arr.length; n++) {
            sum = sum + arr[n];
        }
        return sum;
    }

    /**
     * Computes the summation of one data array
     * with subtracted value at each step and exponent
     * @param arr array of data to sum
     * @return result of summation (long)
     */
    public long summation(long [] arr, long subtract, int exp){
        long sum = 0;
        for (int n = 0; n < arr.length; n++) {
            sum = sum + ((arr[n] - subtract) ^ exp);
        }
        return sum;
    }

    /**
     * Computes the summation of one data array inverted (SUMMATION: 1/xi)
     * @param arr array of data to sum
     * @return result of summation (long)
     */
    public long summation (long [] arr, int numerator, int exp) {
        long sum = 0;
        for (int n = 0; n < arr.length; n++) {
            sum = sum + ( 1 / (arr[n]) ^ exp);
        }
        return sum;
    }

    /**
     * Computes the summation of one data array divided by another,
     * with subtracter value at each step and exponent
     * @param arr1 array of data to sum in numerator
     * @param arr2 array of data to sum in denominator
     * @return result of summation (long)
     */
    public long summation (long [] arr1, long [] arr2, long sub1, long sub2, int exp1, int exp2) {
        long sum = 0;
        if(arr1.length!=arr2.length) {

        }
        for (int n = 0; n < arr1.length; n++) {
            sum = sum + (((arr1[n] - sub1) ^ exp1) / ((arr2[n] - sub2) ^ exp2));
        }
        return sum;
    }

}