package org.globant.services;

import com.amazonaws.services.databasemigrationservice.AWSDatabaseMigrationService;
import com.amazonaws.services.databasemigrationservice.AWSDatabaseMigrationServiceClientBuilder;
import com.amazonaws.services.databasemigrationservice.model.*;
import org.globant.enums.TypesAws;
import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.globant.enums.TypesAws.*;

public class DmsService implements IService {

    private static final Logger LOG = LoggerFactory.getLogger(DmsService.class);
    private static DmsService dmsService;
    AWSDatabaseMigrationService dms;
    List<ResourceReport> resourceReportSet;

    private DmsService(){
        dms = AWSDatabaseMigrationServiceClientBuilder
                .standard()
                .build();
        resourceReportSet = new ArrayList<>();
    }

    public  static DmsService getInstance() {
        if (dmsService == null) {
            dmsService = new DmsService();
        }
        return dmsService;
    }

    @Override
    public List<ResourceReport> getAllResource() {
        LOG.debug("Getting subnet groups resources..");
        addResourceSubnetGroup();
        LOG.debug("Getting enpoints resources..");
        addResourcesEndpoints();
        LOG.debug("Getting replication intstances resources..");
        addResourcesInstances();
        LOG.debug("Getting replication tasks resources..");
        addResourcesTask();
        return resourceReportSet;
    }

    @Override
    public List<ResourceReport> getResourceBy(String filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TagReport> getTagResource(ResourceReport resource) {
        LOG.info("Getting tags from " + resource.getType() + ", Identifier: " + resource.getName());
        List<TagReport> tagSet = new ArrayList<>();
        String arn = getOrBuildArnResource(resource);
        ListTagsForResourceRequest request = new ListTagsForResourceRequest().withResourceArn(arn);
        ListTagsForResourceResult result = dms.listTagsForResource(request);
        for (Tag tag: result.getTagList()) {
            tagSet.add(new TagReport(tag.getKey(), tag.getValue()));
        }
        return tagSet;
    }

    private String getOrBuildArnResource(ResourceReport resource){
        TypesAws type = resource.getType();
        StringBuilder arn = new StringBuilder("arn:aws:dms:");
        arn.append(RegionService.getInstance().getRegionAws().toString())
                .append(":")
                .append(StsService.getInstance().getCurrentAccount());
        if (type.equals(DMS_ENDPOINT) || type.equals(DMS_INSTANCE) ||
                type.equals(DMS_TASK)){
            return resource.getId();
        }
        else if (type.equals(DMS_SUBNET_GROUP))
            arn.append(":subgrp:");
        else
            arn.append(":es:");

        return arn.append(resource.getName()).toString();
    }

    private void addResourceSubnetGroup (){
        DescribeReplicationSubnetGroupsResult resultSubnetGroup = dms
                .describeReplicationSubnetGroups(new DescribeReplicationSubnetGroupsRequest());
        for (ReplicationSubnetGroup subnetGroup: resultSubnetGroup.getReplicationSubnetGroups()) {
            resourceReportSet.add(
                    ResourceReport.classicBuilder()
                            .withId(subnetGroup.getReplicationSubnetGroupIdentifier())
                            .withType(DMS_SUBNET_GROUP)
                            .build());
        }
    }

    private void addResourcesEndpoints(){
        DescribeEndpointsResult resultEndpoints = dms.describeEndpoints(new DescribeEndpointsRequest());
        for (Endpoint endpoint: resultEndpoints.getEndpoints()) {
            resourceReportSet.add(
                    ResourceReport.classicBuilder()
                            .withId(endpoint.getEndpointArn())
                            .withName(endpoint.getEndpointIdentifier())
                            .withType(DMS_ENDPOINT)
                            .build());
        }
    }

    private void addResourcesInstances(){
        DescribeReplicationInstancesResult result = dms
                .describeReplicationInstances(new DescribeReplicationInstancesRequest());
        for (ReplicationInstance repInstance: result.getReplicationInstances()) {
            resourceReportSet.add(
                    ResourceReport.classicBuilder()
                            .withId(repInstance.getReplicationInstanceArn())
                            .withName(repInstance.getReplicationInstanceIdentifier())
                            .withType(DMS_INSTANCE)
                            .build());
        }
    }

    private void addResourcesTask(){
        DescribeReplicationTasksResult result = dms.describeReplicationTasks(new DescribeReplicationTasksRequest());
        for (ReplicationTask repTask: result.getReplicationTasks()) {
            resourceReportSet.add(
                    ResourceReport.classicBuilder().withId(repTask.getReplicationInstanceArn())
                            .withName(repTask.getReplicationTaskIdentifier())
                            .withType(DMS_INSTANCE)
                            .build());
        }
    }
}
