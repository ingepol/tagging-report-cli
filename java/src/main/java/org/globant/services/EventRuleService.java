package org.globant.services;

import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.cloudwatchevents.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class EventRuleService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(EventRuleService.class);
    private static EventRuleService eventRuleService;
    CloudWatchEventsClient events;

    private EventRuleService(){
        Region region = RegionService.getInstance().getRegionAws();
        events = CloudWatchEventsClient.builder()
                .region(region)
                .build();
    }

    public  static EventRuleService getInstance() {
        if (eventRuleService == null) {
            eventRuleService = new EventRuleService();
        }
        return eventRuleService;
    }

    @Override
    public List<ResourceReport> getAllResource() {
        throw new UnsupportedOperationException();
    }

    public List<TagReport> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a s3, Name:  " + resource.getResourceName());
        List<TagReport> tagSet = new ArrayList<>();
        ListTagsForResourceRequest request = ListTagsForResourceRequest
                .builder()
                .resourceARN(buildArnResource(resource))
                .build();

        ListTagsForResourceResponse response = events.listTagsForResource(request);

        for (Tag tag: response.tags()) {
            tagSet.add(new TagReport(tag.key(), tag.value()));
        }
        return tagSet;
    }

    private String buildArnResource(ResourceReport resource){
        StringBuilder arn = new StringBuilder("arn:aws:events:");
        arn.append(RegionService.getInstance().getRegionAws().toString())
                .append(":")
                .append(StsService.getInstance().getCurrentAccount())
                .append(":rule/")
                .append(resource.getResourceName());
        return arn.toString();
    }

}
