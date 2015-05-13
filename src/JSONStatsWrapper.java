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
        double[] a = json.getValues();
        String[] names = new String[2];
        names[0] = "Distribution";
        names[1] = "Characteristic values";
        double values [] = new double[3];

        if(a.length<20 || a.length > 100000000){
            errorOutput();
        }
        else {
            //Check distibution type
            double mean = 0, stdDev = 0;
            int isWhichDistr = testChiSquare(a, mean, stdDev);

            //If normal
            if(isWhichDistr==0){

                values[0] = 0;
                values[1] = (long) mean;
                values[2] = (long) stdDev;
            }
            //If Exponential
            else if(isWhichDistr==1){

            }
            //If Binomial
            else if(isWhichDistr==2){

            }
            //If Student's T
            else if(isWhichDistr==3){

            }
            //If not following any
            else{

            }

            //generate output JSON
            out = new JSON(values, names, 1);
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

        double[] a = json.getValues();
        String [] distr = {"Predicted interval"};
        int jsonSize = a.length;


        if(a.length<30 || a.length > 100000000){
            errorOutput();
        }
        else {
            //Compute mean and std. deviation
            double mean = summation(a) / a.length;
            double stdDev = Math.sqrt( 1/a.length * summation(a, mean, 2));

            //Compute 95% prediction interval (z from normal distr)
            double zValue = 1.96;
            double bounds [] = new double [2];
            bounds[0] = mean - (zValue * stdDev);
            bounds[1] = mean + (zValue * stdDev);

            //generate output JSON
            out = new JSON(bounds, distr, 2);
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
        double []a = json.getValues();

        // if file doesn't exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }


    /**
     * Function which tests which distribution does a data list belong to, if any.
     * @param a         input data list
     * @param mean      variable referenced from verification() method
     * @param stdDev    variable referenced from verification() method
     * @return
     */
    private int testChiSquare(double [] a, double mean, double stdDev){
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
        int isWhichDistribution = -1;

        //Table used for fitting
        //Source: http://www.statext.com/tables/Chi-SquareTable(GreaterThanChi).jpg
        double chiTable [] = {3.841, 5.991, 7.815, 9.488, 11.071, 12.592, 14.067, 15.507, 16.919, 18.307,
                19.675, 21.026, 22.362, 23.685, 24.996, 26.296, 27.587, 28.869, 30.144, 31.410,
                32.671, 33.924, 35.172, 36.415, 37.652, 38.885, 40.113, 41.337, 42.557, 43.773,
                44.985, 46.194, 47.4, 48.602, 49.802, 55.758, 67.505, 79.082, 90.531, 101.879,
                113.145, 124.145, 135.480, 146.567};

        //Loop checking fitting for all possible Distributions
        for(int j=0; j<4; j++){

            //loop computing density function and chi square for each datum
            for(int i=0; i<densityFn.length; i++){
                densityFn[i] = densityFunction(uniques, stdDev, mean, i, j);
                chisquare[i] = (Math.pow((frequency.get(i) - densityFn[i] * 100), 2) / (densityFn[i] * 100));
            }
            double chiSum = summation(chisquare);

            //Set degrees of freedom to check in fitting table
            int degreesOfFreedom = a.length-1;
            if(degreesOfFreedom>=35 && degreesOfFreedom<40) degreesOfFreedom=35;
            else if(degreesOfFreedom>=40 && degreesOfFreedom<50) degreesOfFreedom=36;
            else if(degreesOfFreedom>=50 && degreesOfFreedom<60) degreesOfFreedom=37;
            else if(degreesOfFreedom>=60 && degreesOfFreedom<70) degreesOfFreedom=38;
            else if(degreesOfFreedom>=70 && degreesOfFreedom<80) degreesOfFreedom=39;
            else if(degreesOfFreedom>=80 && degreesOfFreedom<90) degreesOfFreedom=40;
            else if(degreesOfFreedom>=90 && degreesOfFreedom<100) degreesOfFreedom=41;
            else if(degreesOfFreedom>=100 && degreesOfFreedom<110) degreesOfFreedom=42;
            else if(degreesOfFreedom>=110 && degreesOfFreedom<120) degreesOfFreedom=43;
            else if(degreesOfFreedom>120) degreesOfFreedom=44;

            double degree = chiTable[degreesOfFreedom-1];

            //If within the value
            if(chiSum<=degree) {
                isWhichDistribution = j;
                return isWhichDistribution;
            }
        }
        return isWhichDistribution;
    }


    /**
     * Function which returns the density Function of the required distribution type
     * @param uniques   data list used for computations
     * @param stdDev    variable referenced from verification() method
     * @param mean      variable referenced from verification() method
     * @param i         used for computing for each data from list
     * @param j         determines distribution type
     * @return
     */
    private double densityFunction(ArrayList<Integer> uniques, double stdDev, double mean, int i, int j){
        switch(j){
            case 0://Normal
                return (1 / (stdDev * Math.sqrt(2 * Math.PI)) * (Math.exp(-1/2 * Math.pow((uniques.get(i) - mean), 2) / stdDev)));
            case 1://Exponential
                return (1 / (stdDev * Math.sqrt(2 * Math.PI)) * (Math.exp(-1/2 * Math.pow((uniques.get(i) - mean), 2) / stdDev)));
            case 2://Binomial
                return (1 / (stdDev * Math.sqrt(2 * Math.PI)) * (Math.exp(-1/2 * Math.pow((uniques.get(i) - mean), 2) / stdDev)));
            case 3://Student's T
                return (1 / (stdDev * Math.sqrt(2 * Math.PI)) * (Math.exp(-1/2 * Math.pow((uniques.get(i) - mean), 2) / stdDev)));
        }
        return -1;
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
     * with subtracted value at each step and exponent
     * @param arr array of data to sum
     * @return result of summation (long)
     */
    public double summation(double [] arr, double subtract, int exp){
        double sum = 0;
        for (int n = 0; n < arr.length; n++) {
            sum = sum + ( Math.pow( (arr[n] - subtract), exp) );
        }
        return sum;
    }

}