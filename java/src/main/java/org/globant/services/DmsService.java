package org.globant.services;

import com.amazonaws.services.databasemigrationservice.AWSDatabaseMigrationService;
import com.amazonaws.services.databasemigrationservice.AWSDatabaseMigrationServiceClientBuilder;
import com.amazonaws.services.databasemigrationservice.model.*;
import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;
import org.globant.model.TagReport;
import org.globant.model.ResourceReport;
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
    public List<TagReport> getTagResource(ResourceReport resource) {
        LOG.info("Getting tags from " + resource.getType() + ", Identifier: " + resource.getResourceName());
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
            return resource.getArn();
        }
        else if (type.equals(DMS_SUBNET_GROUP))
            arn.append(":subgrp:");
        else
            arn.append(":es:");

        return arn.append(resource.getResourceName()).toString();
    }

    private void addResourceSubnetGroup (){
        DescribeReplicationSubnetGroupsResult resultSubnetGroup = dms
                .describeReplicationSubnetGroups(new DescribeReplicationSubnetGroupsRequest());
        for (ReplicationSubnetGroup subnetGroup: resultSubnetGroup.getReplicationSubnetGroups()) {
            ResourceReport resource =  new ResourceReport(
                    DMS_SUBNET_GROUP,
                    subnetGroup.getReplicationSubnetGroupIdentifier(),
                    CreatedBy.CUSTOM
            );
            resourceReportSet.add(resource);
        }
    }

    private void addResourcesEndpoints(){
        DescribeEndpointsResult resultEndpoints = dms.describeEndpoints(new DescribeEndpointsRequest());

        for (Endpoint endpoint: resultEndpoints.getEndpoints()) {
            ResourceReport resource =  new ResourceReport(
                    DMS_ENDPOINT,
                    endpoint.getEndpointIdentifier(),
                    CreatedBy.CUSTOM
            );
            resource.setArn(endpoint.getEndpointArn());
            resourceReportSet.add(resource);
        }
    }

    private void addResourcesInstances(){
        DescribeReplicationInstancesResult result = dms
                .describeReplicationInstances(new DescribeReplicationInstancesRequest());

        for (ReplicationInstance repInstance: result.getReplicationInstances()) {
            ResourceReport resource =  new ResourceReport(
                    DMS_INSTANCE,
                    repInstance.getReplicationInstanceIdentifier(),
                    CreatedBy.CUSTOM
            );
            resource.setArn(repInstance.getReplicationInstanceArn());
            resourceReportSet.add(resource);
        }
    }

    private void addResourcesTask(){
        DescribeReplicationTasksResult result = dms.describeReplicationTasks(new DescribeReplicationTasksRequest());

        for (ReplicationTask repTask: result.getReplicationTasks()) {
            ResourceReport resource =  new ResourceReport(
                    DMS_TASK,
                    repTask.getReplicationTaskIdentifier(),
                    CreatedBy.CUSTOM
            );
            resource.setArn(repTask.getReplicationInstanceArn());
            resourceReportSet.add(resource);
        }
    }
}
