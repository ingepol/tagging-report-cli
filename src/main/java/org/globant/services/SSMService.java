package org.globant.services;

import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.ssm.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.ssm.model.Tag;

import java.util.ArrayList;
import java.util.List;

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
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ResourceReport> getResourceBy(String filter) {
        throw new UnsupportedOperationException();
    }

    public List<TagReport> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a parameter, Name:  " + resource.getResourceName());
        List<TagReport> tagSet = new ArrayList<TagReport>();

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
}
