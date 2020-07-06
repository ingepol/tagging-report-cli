package org.globant.services;

import org.globant.model.TagReport;
import org.globant.model.ResourceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

import java.util.ArrayList;
import java.util.List;

public class SSMService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(SSMService.class);
    private static SSMService ssmService;
    SsmClient ssm;

    private SSMService(){
        Region region = Region.US_WEST_2;
        ssm = SsmClient.builder()
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
        return null;
    }

    public List<TagReport> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a parameter, Name:  " + resource.getResourceName());
        List<TagReport> tagSet = new ArrayList<TagReport>();

        ListTagsForResourceRequest request = ListTagsForResourceRequest
                .builder()
                .resourceId(resource.getResourceName())
                .resourceType(resource.getRequestType())
                .build();

        ListTagsForResourceResponse response = ssm.listTagsForResource(request);

        for (Tag tag: response.tagList()) {
            tagSet.add(new TagReport(tag.key(), tag.value()));
        }
        return tagSet;
    }
}
