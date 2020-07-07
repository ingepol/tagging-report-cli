package org.globant.services;

import com.amazonaws.services.databasemigrationservice.AWSDatabaseMigrationService;
import com.amazonaws.services.databasemigrationservice.AWSDatabaseMigrationServiceClient;
import com.amazonaws.services.databasemigrationservice.AWSDatabaseMigrationServiceClientBuilder;
import com.amazonaws.services.databasemigrationservice.model.*;
import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;
import org.globant.model.TagReport;
import org.globant.model.ResourceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;


import java.util.ArrayList;
import java.util.List;

public class DmsService implements IService {

    private static final Logger LOG = LoggerFactory.getLogger(DmsService.class);
    private static final String CUSTOM = "Custom Lambda";
    private static DmsService dmsService;
    AWSDatabaseMigrationService dms;
    List<ResourceReport> resourceReportSet;

    private DmsService(){
        dms = AWSDatabaseMigrationServiceClientBuilder
                .standard()
                .build();
        resourceReportSet = new ArrayList<ResourceReport>();
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
        List<TagReport> tagSet = new ArrayList<TagReport>();
        String arn = resource.getArn() != null ? resource.getArn() : resource.getResourceName();
        ListTagsForResourceResult result = getTagsbyArn(arn);
        for (Tag tag: result.getTagList()) {
            tagSet.add(new TagReport(tag.getKey(), tag.getValue()));
        }
        return tagSet;
    }

    private ListTagsForResourceResult getTagsbyArn(String arn){

        String idAccunt = StsService.getInstance().getCurrentAccount();
        String arnRequest = arn.contains("arn:aws:dms") ? arn:"arn:aws:dms:us-west-2:"+idAccunt+":subgrp:" + arn;
        ListTagsForResourceRequest request = new ListTagsForResourceRequest().withResourceArn(arnRequest);
        ListTagsForResourceResult result = dms.listTagsForResource(request);
        return result;
    }

    private void addResourceSubnetGroup (){
        DescribeReplicationSubnetGroupsRequest requestsg = new DescribeReplicationSubnetGroupsRequest();
        DescribeReplicationSubnetGroupsResult resultSubnetGroup = dms.describeReplicationSubnetGroups(requestsg);
        for (ReplicationSubnetGroup subnetGroup: resultSubnetGroup.getReplicationSubnetGroups()) {
            ResourceReport resource =  new ResourceReport(
                    TypesAws.DMS_SUBNET_GROUP.getKey(),
                    subnetGroup.getReplicationSubnetGroupIdentifier(),
                    CreatedBy.CUSTOM
            );
            resourceReportSet.add(resource);
        }
    }

    private void addResourcesEndpoints(){
        DescribeEndpointsRequest requestEndpoints = new DescribeEndpointsRequest();
        DescribeEndpointsResult resultEndpoints = dms.describeEndpoints(requestEndpoints);

        for (Endpoint endpoint: resultEndpoints.getEndpoints()) {
            ResourceReport resource =  new ResourceReport(
                    TypesAws.DMS_ENDPOINT.getKey(),
                    endpoint.getEndpointIdentifier(),
                    CreatedBy.CUSTOM
            );
            resource.setArn(endpoint.getEndpointArn());
            resourceReportSet.add(resource);
        }
    }

    private void addResourcesInstances(){
        DescribeReplicationInstancesRequest resuest = new DescribeReplicationInstancesRequest();
        DescribeReplicationInstancesResult result = dms.describeReplicationInstances(resuest);

        for (ReplicationInstance repInstance: result.getReplicationInstances()) {
            ResourceReport resource =  new ResourceReport(
                    TypesAws.DMS_INSTANCE.getKey(),
                    repInstance.getReplicationInstanceIdentifier(),
                    CreatedBy.CUSTOM
            );
            resource.setArn(repInstance.getReplicationInstanceArn());
            resourceReportSet.add(resource);
        }
    }

    private void addResourcesTask(){
        DescribeReplicationTasksRequest resuest = new DescribeReplicationTasksRequest();
        DescribeReplicationTasksResult result = dms.describeReplicationTasks(resuest);

        for (ReplicationTask repTask: result.getReplicationTasks()) {
            ResourceReport resource =  new ResourceReport(
                    TypesAws.DMS_TASK.getKey(),
                    repTask.getReplicationTaskIdentifier(),
                    CreatedBy.CUSTOM
            );
            resource.setArn(repTask.getReplicationInstanceArn());
            resourceReportSet.add(resource);
        }
    }
}
