package org.globant.services;

import org.globant.model.TagReport;
import org.globant.model.ResourceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.sns.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.sns.model.Tag;


import java.util.ArrayList;
import java.util.List;

public class TopicService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(TopicService.class);
    private static TopicService topicService;
    SnsClient client;

    private TopicService(){
        Region region = RegionService.getInstance().getRegionAws();
        client = SnsClient.builder()
                .region(region)
                .build();
    }

    public  static TopicService getInstance() {
        if (topicService == null) {
            topicService = new TopicService();
        }
        return topicService;
    }

    @Override
    public List<ResourceReport> getAllResource() {
        throw new UnsupportedOperationException();
    }

    public List<TagReport> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a topic, Name: " + resource.getResourceName());
        List<TagReport> tagSet = new ArrayList<TagReport>();

        ListTagsForResourceRequest request = ListTagsForResourceRequest
                .builder()
                .resourceArn(resource.getResourceName())
                .build();

        ListTagsForResourceResponse response = client.listTagsForResource(request);

        for (Tag tag: response.tags()) {
            tagSet.add(new TagReport(tag.key(), tag.value()));
        }
        return tagSet;
    }
}
