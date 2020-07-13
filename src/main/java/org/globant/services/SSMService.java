package org.globant.services;

import org.globant.enums.CreatedBy;
import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.globant.enums.TypesAws.PARAMETER;

public class SSMService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(SSMService.class);
    private static SSMService ssmService;
    SsmClient client;

    private SSMService(){
        Region region = RegionService.getInstance().getRegionAws();
        client = SsmClient.builder()
                .region(region)
                .build();
    }

    public  static SSMService getInstance() {
        if (ssmService == null) {
            ssmService = new SSMService();
        }
        return ssmService;
    }

    @Override
    public List<ResourceReport> getAllResource(){
        LOG.debug("Getting ssm resources..");
        List<ResourceReport> resources = new ArrayList<>();
        DescribeParametersResponse response = client.describeParameters();
        List<ParameterMetadata> parameters = new ArrayList<>(response.parameters());
        while(response.nextToken() != null){
            LOG.info("Fetched parameters " + parameters.size());
            DescribeParametersRequest request = DescribeParametersRequest
                    .builder()
                    .nextToken(response.nextToken())
                    .build();
            response = client.describeParameters(request);
            parameters.addAll(response.parameters());
        }
        LOG.info("Fetched parameters " + parameters.size());
        parameters
                .stream()
                .map(this::reportParameter)
                .forEach(resources::add);
        return resources;
    }

    @Override
    public List<ResourceReport> getResourceBy(String filter) {
        throw new UnsupportedOperationException();
    }

    public List<TagReport> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a parameter, Name:  " + resource.getResourceName());
        List<TagReport> tagSet = new ArrayList<>();

        ListTagsForResourceRequest request = ListTagsForResourceRequest
                .builder()
                .resourceId(resource.getResourceName())
                .resourceType(resource.getType().getValue())
                .build();

        ListTagsForResourceResponse response = client.listTagsForResource(request);

        for (Tag tag: response.tagList()) {
            tagSet.add(new TagReport(tag.key(), tag.value()));
        }
        return tagSet;
    }

    private ResourceReport reportParameter(ParameterMetadata parameter) {
        ResourceReport report = new ResourceReport(
                PARAMETER,
                parameter.name(),
                CreatedBy.CUSTOM
        );
        return report;
    }
}
