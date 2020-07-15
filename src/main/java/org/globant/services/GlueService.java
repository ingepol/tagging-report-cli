package org.globant.services;

import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;
import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.GetDatabasesRequest;
import software.amazon.awssdk.services.glue.model.ListCrawlersRequest;
import software.amazon.awssdk.services.glue.model.ListJobsRequest;
import software.amazon.awssdk.services.glue.model.ListTriggersRequest;

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
    private final Map<TypesAws, Consumer<List<ResourceReport>>> details;
    private GlueService() {
        Region region = RegionService.getInstance().getRegionAws();
        client = GlueClient.builder().region(region).build();
        listings = new HashMap<>();
        details = new HashMap<>();

        listings.put(CRAWLER, resources -> client
                .listCrawlers(ListCrawlersRequest.builder().build())
                .crawlerNames()
                .stream()
                .map(crawlerName -> ResourceReport.builder(crawlerName)
                        .withCreate(CreatedBy.CUSTOM)
                        .withType(CRAWLER)
                        .build())
                .forEach(resources::add));

        listings.put(DATABASE, resources -> client
                .getDatabases(GetDatabasesRequest.builder().build())
                .databaseList()
                .stream()
                .map(database -> ResourceReport.builder(database.name())
                        .withCreate(CreatedBy.CUSTOM)
                        .withType(DATABASE)
                        .build())
                .forEach(resources::add));

        listings.put(JOB, resources -> client
                .listJobs(ListJobsRequest.builder().build())
                .jobNames()
                .stream()
                .map(jobName -> ResourceReport.builder(jobName)
                        .withCreate(CreatedBy.CUSTOM)
                        .withType(JOB)
                        .build())
                .forEach(resources::add));

        listings.put(TRIGGER, resources -> client
                .listTriggers(ListTriggersRequest.builder().build())
                .triggerNames()
                .stream()
                .map(triggerName -> ResourceReport.builder(triggerName)
                        .withCreate(CreatedBy.CUSTOM)
                        .withType(JOB)
                        .build())
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
    public List<ResourceReport> getResourceBy(String filter) {
        return null;
    }

    @Override
    public List<TagReport> getTagResource(ResourceReport resource) {
        return null;
    }
}