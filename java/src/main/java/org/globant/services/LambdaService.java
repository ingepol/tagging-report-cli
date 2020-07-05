package org.globant.services;

import org.globant.model.ReportTag;
import org.globant.model.ResourceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.ListTagsRequest;
import software.amazon.awssdk.services.lambda.model.ListTagsResponse;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LambdaService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(SSMService.class);
    private static LambdaService lambdaService;
    LambdaClient lambda;

    private LambdaService(){
        Region region = Region.US_WEST_2;
        lambda = LambdaClient.builder()
                .region(region)
                .build();
    }

    public  static LambdaService getInstance() {
        if (lambdaService == null) {
            lambdaService = new LambdaService();
        }
        return lambdaService;
    }

    public List<ReportTag> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a function, Name: " + resource.getResourceName());
        List<ReportTag> tagSet = new ArrayList<ReportTag>();

        GetFunctionRequest request = GetFunctionRequest
                .builder()
                .functionName(resource.getResourceName())
                .build();

        GetFunctionResponse response = lambda.getFunction(request);

        for (Map.Entry<String,String> tag: response.tags().entrySet()) {
            tagSet.add(new ReportTag(tag.getKey(), tag.getValue()));
        }
        return tagSet;
    }
}
