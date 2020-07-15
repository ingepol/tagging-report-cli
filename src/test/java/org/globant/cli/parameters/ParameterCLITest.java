package org.globant.cli.parameters;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import software.amazon.awssdk.regions.Region;

public class ParameterCLITest {

    ParametersCLI parametersCLI;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup(){
        parametersCLI = new ParametersCLI();
    }

    @Test
    public void typeParameterNotNull(){
        //Actions
        String[] argv = { "-t", "Stack" };
        JCommander.newBuilder()
                .addObject(parametersCLI)
                .args(argv)
                .build();

        //Asserts
        Assert.assertNotNull(parametersCLI.getType());
    }

    @Test
    public void typeParameterRequired(){
        //Expected exception message
        exceptionRule.expect(ParameterException.class);
        exceptionRule.expectMessage("required");

        //Actions
        String[] argv = {};
        JCommander.newBuilder()
                .addObject(parametersCLI)
                .args(argv)
                .build();
    }

    @Test
    public void typeParameterWrongType(){

        //Expected exception message
        String wrongType = "DontExists";
        exceptionRule.expect(ParameterException.class);
        exceptionRule.expectMessage("wrong");
        exceptionRule.expectMessage(wrongType);
        //Actions
        String[] argv = { "-t", wrongType };
        JCommander.newBuilder()
                .addObject(parametersCLI)
                .args(argv)
                .build();

    }

    @Test
    public void searchParameterNotNull(){
        //Actions
        String[] argv = { "-t", "Stack", "-s", "ecp" };
        JCommander.newBuilder()
                .addObject(parametersCLI)
                .args(argv)
                .build();

        //Asserts
        Assert.assertNotNull(parametersCLI.getSearch());
    }

    @Test
    public void searchParameterHasTaskValue(){
        //Expected value
        String searchExpectedValue = "task";
        //Actions
        String[] argv = { "-t", "Stack", "-s", searchExpectedValue };
        JCommander.newBuilder()
                .addObject(parametersCLI)
                .args(argv)
                .build();

        //Asserts
        Assert.assertNotNull(parametersCLI.getSearch(), searchExpectedValue);
    }

    @Test
    public void regionParameterNotNull(){
        //Actions
        String[] argv = { "-t", "Stack"};
        JCommander.newBuilder()
                .addObject(parametersCLI)
                .args(argv)
                .build();

        //Asserts
        Assert.assertNotNull(parametersCLI.getRegion());
    }

    @Test
    public void regionParameterDefaultRegion(){

        //Actions
        String[] argv = { "-t", "Stack"};
        JCommander.newBuilder()
                .addObject(parametersCLI)
                .args(argv)
                .build();

        //Asserts
        Assert.assertNotNull(parametersCLI.getRegion());
        Assert.assertEquals(parametersCLI.getRegion(), Region.US_WEST_2);
    }

    @Test
    public void regionParameterUsWest1(){
        //Expected value
        Region regionExpectedValue = Region.US_WEST_1;
        //Actions
        String[] argv = { "-t", "Stack", "-r", regionExpectedValue.toString()};
        JCommander.newBuilder()
                .addObject(parametersCLI)
                .args(argv)
                .build();

        //Asserts
        Assert.assertNotNull(parametersCLI.getRegion());
        Assert.assertEquals(parametersCLI.getRegion(), regionExpectedValue);
    }

    @Test
    public void regionParameterWrongRegion(){
        //Expected exception message
        exceptionRule.expect(ParameterException.class);
        exceptionRule.expectMessage("wrong");
        exceptionRule.expectMessage("region");
        //Actions
        String[] argv = { "-t", "Stack", "-r", "region_wrong"};
        JCommander.newBuilder()
                .addObject(parametersCLI)
                .args(argv)
                .build();

        //Asserts
        Assert.assertNotNull(parametersCLI.getRegion());
        Assert.assertEquals(parametersCLI.getRegion(), Region.US_WEST_1);
    }



    @After
    public void tearDown(){
        parametersCLI = null;
    }
}
