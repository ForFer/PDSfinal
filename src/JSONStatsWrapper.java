import sun.invoke.util.Wrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Date;
import java.util.DoubleSummaryStatistics;

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
        names[0] = "Distribution: ";
        names[1] = "Characteristic values";
        double values [];

        if(a.length<20 || a.length > 100000000){
            errorOutput();
        }
        else {
            //Check distibution type
            double mean = 0, stdDev = 0;
            ArrayList<Double> arr = testChiSquare(a, mean, stdDev, a.length);
            double isWhichDistr = arr.get(0);
            mean = arr.get(1);
            stdDev = arr.get(2);

            switch ((int) isWhichDistr) {

                case 0://If Normal
                    values = new double[2];
                    names[0] = names[0] + "Normal";
                    values[0] = mean; //Mean
                    values[1] = stdDev; //Standard Deviation
                    break;

                case 1://If Exponential
                    names[0] = names[0] + "Exponential";
                    values = new double[1];
                    values[0] = 1/mean; //this is Rate
                    break;

                case 2://If Binomial
                    names[0] = names[0] + "Binomial";
                    values = new double [2];
                    values[0] = a.length; //This is n
                    values[1] = mean / a.length; //This is p
                    break;

                case 3://If Student's T
                    names[0] = names[0] + "Student's T";
                    values = new double[1];
                    values[0] = a.length-1; //this is Degrees of freedom
                    break;

                default://If not following any
                    names[0] = names[0] + "None";
                    values = new double[1];
                    values[0] = -1;
                    break;
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
            double stdDev = Math.sqrt(1 / a.length * summation(a, mean, 2));

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
    private ArrayList<Double> testChiSquare(double [] a, double mean, double stdDev, int degreesOfFreedom){

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

            //Compute mean
            for(int i=0; i<uniques.size(); i++){
                mean = mean + (uniques.get(i) * ( frequency.get(i) / a.length ));
            }

            //Compute std dev and extras for each possible distr.
            switch(j){
                case 0://Normal
                    stdDev = Math.sqrt( 1/a.length * summation(a, mean, 2)); break;

                case 1://Exponential
                    stdDev = 1/mean; break;

                case 2://Binomial
                    double p = mean / a.length;
                    stdDev = a.length * p * (1 - p); break;

                case 3://Student's T
                    stdDev = Math.sqrt((a.length-1)/(a.length-3)); break;
            }

            //loop computing density function and chi square for each datum
            for(int i=0; i<densityFn.length; i++){
                densityFn[i] = densityFunction(uniques, stdDev, mean, i, j, a.length);
                chisquare[i] = (Math.pow((frequency.get(i) - densityFn[i] * a.length), 2) / (densityFn[i] * a.length));
            }
            double chiSum = summation(chisquare);

            //Set degrees of freedom to check in fitting table
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
                //Exit loop
                break;
            }
        }
        //Put result in arraylist
        ArrayList<Double> result = new ArrayList<>(3);
        result.add((double) isWhichDistribution);
        result.add(mean);
        result.add(stdDev);
        return result;
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
    private double densityFunction(ArrayList<Integer> uniques, double stdDev, double mean, int i, int j, int samplesize){
        switch(j){
            case 0://Normal
                return (1 / (stdDev * Math.sqrt(2 * Math.PI)) * (Math.exp(-1/2 * Math.pow((uniques.get(i) - mean), 2) / stdDev)));

            case 1://Exponential
                return  (1/mean) * Math.pow(Math.E, (-1 * (1/mean) * uniques.get(i))) ;

            case 2://Binomial
                return factorial(samplesize) / (factorial(uniques.get(i)) * factorial(samplesize - uniques.get(i))) *
                        Math.pow(mean/samplesize, uniques.get(i)) * Math.pow(1 -  mean/samplesize, samplesize - uniques.get(i));

            case 3://Student's T
                return factorial((samplesize+1) / 2 - 1) / ( Math.sqrt(samplesize*Math.PI) * factorial(samplesize/2 -1)) *
                        Math.pow(1 + (uniques.get(i)/samplesize), -1*(samplesize+1)/2 );
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

    /**
     * Function which computes the factorial of a given double param
     * @param n
     * @return
     */
    public double factorial(double n){
        if( n <= 1 )
            return 1;
        else
            return n * factorial(n - 1);
    }
}