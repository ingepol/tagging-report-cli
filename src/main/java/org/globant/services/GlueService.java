package org.globant.services;

import org.apache.commons.lang3.StringUtils;
import org.globant.enums.TypesAws;
import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.globant.enums.TypesAws.*;

public class GlueService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(GlueService.class);

    private static GlueService SERVICE;
    public static GlueService getInstance() {
        if (SERVICE == null) {
            SERVICE = new GlueService();
        }
        return SERVICE;
    }

    private final GlueClient client;
    private final Map<TypesAws, Consumer<List<ResourceReport>>> listings;
    private GlueService() {
        Region region = RegionService.getInstance().getRegionAws();
        String account = StsService.getInstance().getCurrentAccount();
        client = GlueClient.builder().region(region).build();
        listings = new HashMap<>();

        listings.put(CRAWLER, resources -> { client
                    .listCrawlers(ListCrawlersRequest.builder().build())
                    .crawlerNames()
                    .stream()
                    .map(crawlerName -> ResourceReport.builder()
                            .withRegion(region.id())
                            .withAccount(account)
                            .withType(CRAWLER)
                            .withName(crawlerName))
                    .forEach(resources::add);
        });

        listings.put(DATABASE, resources -> client
                .getDatabases(GetDatabasesRequest.builder().build())
                .databaseList()
                .stream()
                .map(Database::name)
                .map(database -> ResourceReport
                        .with(region.id(), account)
                        .build(DATABASE, database))
                .forEach(resources::add));

        listings.put(JOB, resources -> client
                .listJobs(ListJobsRequest.builder().build())
                .jobNames()
                .stream()
                .map(jobName -> ResourceReport
                        .with(region.id(), account)
                        .build(JOB, jobName))
                .forEach(resources::add));

        listings.put(TRIGGER, resources -> client
                .listTriggers(ListTriggersRequest.builder().build())
                .triggerNames()
                .stream()
                .map(triggerName -> ResourceReport
                        .with(region.id(), account)
                        .build(TRIGGER, triggerName))
                .forEach(resources::add));
    }

    @Override
    public List<ResourceReport> getAllResource() {
        List<ResourceReport> resources = new ArrayList<>();
        listings.forEach((type, getResources) -> {
            LOG.debug("Getting resources " + type.getKey());
            getResources.accept(resources);
        });
        return resources;
    }

    @Override
    public List<TagReport> getTagResource(ResourceReport resource) {
        if(StringUtils.isEmpty(resource.getId())) {
            throw new UnsupportedOperationException(resource.getType().getKey() + " does not support tags");
        }
        List<TagReport> tags = new ArrayList<>();
        client.getTags(GetTagsRequest.builder().resourceArn(resource.getId()).build())
                .tags()
                .forEach((key, value) -> tags.add(new TagReport(key, value)));
        return tags;
    }

    @Override
    public List<ResourceReport> getResourceBy(String filter) {
        return null;
    }
}