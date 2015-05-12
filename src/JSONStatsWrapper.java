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

            //Normal
            double mean = 0, stdDev = 0;
            boolean isNormal = testChiSquare(a, mean, stdDev);

            //Exponential
            //boolean isExp = testExp();

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

    boolean testChiSquare(long [] a, double mean, double stdDev){
        //Compute mean and std. deviation
        mean = summation(a) / a.length;
        stdDev = Math.sqrt( 1/a.length * summation(a, mean, 2));

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
        double densityFn [] = new double[uniques.size()];
        double chisquare [] = new double[uniques.size()];
        for(int i=0; i<densityFn.length; i++){
            densityFn[i] = (1 / (stdDev * Math.sqrt(2 * Math.PI)) *
                    (Math.exp(-1/2 * Math.pow((uniques.get(i) - mean), 2) / stdDev)));

            chisquare[i] = (Math.pow((frequency.get(i) - densityFn[i] * 100), 2) / (densityFn[i] * 100));
        }
        double chiSum = summation(chisquare);

        //Determine whether it's normal or not
        boolean isNormal;

        ///IMPORTANTE, CAMBIAR
        ///IMPORTANTE, CAMBIAR
        ///IMPORTANTE, CAMBIAR
        ///IMPORTANTE, CAMBIAR
        ///IMPORTANTE, CAMBIAR

        int degreesOfFreedom = a.length;
        if(degreesOfFreedom>120) degreesOfFreedom=120;
        double chiTable [] = {3.841, 5.991, 7.815, 9.488, 11.071, 12.592, 14.067, 15.507, 16.919, 18.307,
        19.675, 21.026, 22.362, 23.685, 24.996, 26.296, 27.587, 28.869, 30.144, 31.410,
        32.671, 33.924, 35.172, 36.415, 37.652, 38.885, 40.113, 41.337, 42.557, 43.773,
        44.985, 46.194, 47.4, 48.602, 49.802, 55.758, 67.505, 79.082, 90.531, 101.879,
        113.145, 124.145, 135.480, 146.567};
        double degree = chiTable[degreesOfFreedom-1];

        ///IMPORTANTE, CAMBIAR
        ///IMPORTANTE, CAMBIAR
        ///IMPORTANTE, CAMBIAR
        //If within the value
        if(chiSum<=degree) isNormal = true;
        else isNormal = false;
        return isNormal;
    }

    /**
     * Computes the summation of one data array
     * @param arr array of data to sum
     * @return result of summation (long)
     */
    public double summation(double[] arr) {
        double sum = 0;
        for (int n = 0; n < arr.length; n++) {
            sum = sum + arr[n];
        }
        return sum;
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
    public double summation(long [] arr, double subtract, int exp){
        double sum = 0;
        for (int n = 0; n < arr.length; n++) {
            sum = sum + ( Math.pow( (arr[n] - subtract), exp) );
        }
        return sum;
    }

    /**
     * Computes the summation of one data array inverted (SUMMATION: 1/xi)
     * @param arr array of data to sum
     * @return result of summation (long)
     */
    public double summation (double [] arr, int numerator, int exp) {
        double sum = 0;
        for (int n = 0; n < arr.length; n++) {
            sum = sum + ( 1 / Math.pow( (arr[n]) , exp));
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
    public double summation (double [] arr1, double [] arr2, double sub1, double sub2, int exp1, int exp2) {
        double sum = 0;
        if(arr1.length!=arr2.length) {

        }
        for (int n = 0; n < arr1.length; n++) {
            sum = sum + (Math.pow( (arr1[n] - sub1), exp1) / Math.pow((arr2[n] - sub2) , exp2));
        }
        return sum;
    }

}