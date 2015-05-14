import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fernando on 14/05/15.
 */
public class verificationTest {

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

    /**Testcase Name: 10-outputValidationNormal
     *Input/Output Parameters Analyzed: Correctness of output regarding distribution type and characteristic values of it
     *Input value description: data series following normal distribution
     *Testing Technique: assert to check whether the values are correct (distribution type and characteristic values)
     *Expected Result: Correct execution
     * @throws Exception
     */
    @Test
    public void normalDistritbuion() throws Exception{
        JSON json = new JSON("files/", "normal");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        double resultado []= myStats1.verification();


        double expected [] = {0, 49.71, 2.022};


        assertEquals(expected[0], resultado[0], 0);
        assertEquals(expected[1], resultado[1], 0.02);
        assertEquals(expected[2], resultado[2], 0.02);
    }

    /**Testcase Name: 11-outputValidationBinomial
     *Input/Output Parameters Analyzed: Correctness of output regarding distribution type and characteristic values of it
     *Input value description: data series following binomial distribution
     *Testing Technique: assert to check whether the values are correct (distribution type and characteristic values)
     *Expected Result: Correct execution
     * @throws Exception
     */
    @Test
    public void binomialDistritbuion() throws Exception{
        JSON json = new JSON("files/", "binomial");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);
        double resultado []= myStats1.verification();


        double expected [] = {2, 100, 0.4971};


        assertEquals(expected[0], resultado[0], 0);
        assertEquals(expected[1], resultado[1], 0.02);
        assertEquals(expected[2], resultado[2], 0.02);
    }
}