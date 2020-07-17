package org.globant.services;

import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LambdaService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(SSMService.class);
    private static LambdaService lambdaService;
    LambdaClient client;

    private LambdaService(){
        Region region = RegionService.getInstance().getRegionAws();
        client = LambdaClient.builder()
                .region(region)
                .build();
    }

    public  static LambdaService getInstance() {
        if (lambdaService == null) {
            lambdaService = new LambdaService();
        }
        return lambdaService;
    }

    @Override
    public List<ResourceReport> getAllResource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ResourceReport> getResourceBy(String filter) {
        throw new UnsupportedOperationException();
    }

    public List<TagReport> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a function, Name: " + resource.getName());
        List<TagReport> tagSet = new ArrayList<>();

        GetFunctionRequest request = GetFunctionRequest
                .builder()
                .functionName(resource.getName())
                .build();

        GetFunctionResponse response = client.getFunction(request);

        for (Map.Entry<String,String> tag: response.tags().entrySet()) {
            tagSet.add(new TagReport(tag.getKey(), tag.getValue()));
        }
        return tagSet;
    }
}
