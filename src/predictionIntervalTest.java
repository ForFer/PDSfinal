import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fernando on 14/05/15.
 */
public class predictionIntervalTest {


    /**Testcase Name: 01-invalidInput1
     *Input/Output Parameters Analyzed: data series passed as dataList
     *Input value description:  A string containing numbers separated by ", "
     *In this case, there is no “dataseries” string inside the JSON
     *Testing Technique: Incorrect JSON passed as parameter
     *Expected Result: Test should be correct
     * @throws Exception
     */
    @Test (expected = Exception.class) //No "dataseries"
    public void invalidInput1() throws Exception {
        JSON json = new JSON("files/", "noDataseries");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        myStats1.verification();
    }

    /**Testcase Name: 02-invalidInput2
     *Input/Output Parameters Analyzed: data series passed as dataList
     *Input value description:  A string containing numbers separated by ", "
     *In this case, no commas appear in the JSON
     *Testing Technique: Incorrect JSON passed as parameter
     *Expected Result: Test should be correct
     * @throws Exception
     */
    @Test (expected = Exception.class) //No ", "
    public void invalidInput2() throws Exception {
        JSON json = new JSON("files/", "noCommas.json");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        myStats1.verification();
    }

    /**Testcase Name: 03-invalidInput3
     *Input/Output Parameters Analyzed: data series passed as dataList
     *Input value description:  A string containing numbers separated by ", "
     *In this case, no brackets appear in the JSON
     *Testing Technique: Incorrect JSON passed as parameter
     *Expected Result: Test should be correct
     * @throws Exception
     */
    @Test (expected = Exception.class) //No "{ .. }"
    public void invalidInput3() throws Exception {
        JSON json = new JSON("files/", "noBrackets");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        myStats1.verification();
    }

    /**Testcase Name: 04-upperBoundCheck
     *Input/Output Parameters Analyzed: data series passed as dataList
     *Input value description:  A string containing numbers separated by ", "
     *In this case, the JSON has 10^7+1 elements
     *Testing Technique: Incorrect JSON passed as parameter
     *Expected Result: Test should be error
     * @throws Exception
     */
    @Test (expected = Exception.class) //10^7 +1
    public void upperBoundCheck() throws Exception {
        JSON json = new JSON("files/", "oneAboveUpperLimit");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        myStats1.verification();
    }

    /**Testcase Name: 05-upperBoundCheck1
     *Input/Output Parameters Analyzed: data series passed as dataList
     *Input value description:  A string containing numbers separated by ", "
     *In this case, the JSON has 10^7 elements
     *Testing Technique: Incorrect JSON passed as parameter
     *Expected Result: Test should be correct
     * @throws Exception
     */
    @Test //10^7
    public void upperBoundCheck1() throws Exception {
        JSON json = new JSON("files/", "UpperLimit");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        myStats1.verification();
    }

    /**Testcase Name: 06-upperBoundCheck2
     *Input/Output Parameters Analyzed: data series passed as dataList
     *Input value description:  A string containing numbers separated by ", "
     *In this case, the JSON has 10^7-1 elements
     *Testing Technique: Incorrect JSON passed as parameter
     *Expected Result: Test should be correct
     * @throws Exception
     */
    @Test //10^7 - 1
    public void upperBoundCheck2() throws Exception {
        JSON json = new JSON("files/", "oneBelowUpperLimit");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        myStats1.verification();
    }

    /**Testcase Name: 07-lowerBoundCheck
     *Input/Output Parameters Analyzed: data series passed as dataList
     *Input value description:  A string containing numbers separated by ", "
     *In this case, the JSON has 21 elements
     *Testing Technique: Incorrect JSON passed as parameter
     *Expected Result: Test should be correct
     * @throws Exception
     */
    @Test  // 21
    public void lowerBoundCheck() throws Exception {
        JSON json = new JSON("files/", "oneAboveLowerLimit");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        myStats1.verification();
    }

    /**Testcase Name: 08-lowerBoundCheck1
     *Input/Output Parameters Analyzed: data series passed as dataList
     *Input value description:  A string containing numbers separated by ", "
     *In this case, the JSON has 20 elements
     *Testing Technique: Incorrect JSON passed as parameter
     *Expected Result: Test should be correct
     * @throws Exception
     */
    @Test // 20
    public void lowerBoundCheck1() throws Exception {
        JSON json = new JSON("files/", "LowerLimit");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        myStats1.verification();
    }

    /**Testcase Name: 09-lowerBoundCheck2
     *Input/Output Parameters Analyzed: data series passed as dataList
     *Input value description:  A string containing numbers separated by ", "
     *In this case, the JSON has 19 elements
     *Testing Technique: Incorrect JSON passed as parameter
     *Expected Result: Test should be error
     * @throws Exception
     */
    @Test (expected = Exception.class) // 19
    public void lowerBoundCheck2() throws Exception {
        JSON json = new JSON("files/", "oneBelowLowerLimit");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        myStats1.verification();
    }

    /**Testcase Name: 12-outputPredictionIntervalNormal
     *Input/Output Parameters Analyzed: Correctness of calculation of the interval
     *Input value description: dataseries following normal distribution
     *Testing Technique: Checking if input is properly calculated (comparing with result in excel)
     *Expected Result: Correct execution
     * @throws Exception
     */
    @Test
    public void confidenceIntervalNormal() throws Exception{
        JSON json = new JSON("files/", "normal");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        double resultado []= myStats1.predictionInterval();

        double expected [] = {50.6259, 49.8189};

        assertEquals(expected[0], resultado[0], 0.75);
        assertEquals(expected[1], resultado[1], 0.75);
    }

}