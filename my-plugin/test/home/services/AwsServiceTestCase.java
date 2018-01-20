package home.services;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.AwsProfileRegionProvider;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import org.junit.Test;

public class AwsServiceTestCase {

    @Test
    public void testDefaultRegion() throws Exception {
        AwsProfileRegionProvider p = new AwsProfileRegionProvider("default");
        System.out.println(p.getRegion());


    }
}
