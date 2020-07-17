package org.globant.aws;

import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;
import org.globant.model.ResourceReport;
import org.globant.services.IServiceCatalog;
import org.globant.services.ServiceCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CloudFormation {

    private static final Logger LOG = LoggerFactory.getLogger(CloudFormation.class);

    CloudFormationClient cwf;

    public CloudFormation(){
        Region region = Region.US_WEST_2;
        cwf = CloudFormationClient.builder()
                .region(region)
                .build();
    }

    public List<Stack> listStacks(String filterStacks) {

        List<Stack> stackSuccessSet = new ArrayList<>();

        try {
            // get stacks
            DescribeStacksResponse stacks = cwf.describeStacks();
            List<Stack> stackSet = new ArrayList<>(stacks.stacks());
            while (stacks.nextToken() != null){
                LOG.info("Fetched stacks " + stackSet.size());
                DescribeStacksRequest describeStacksRequest = DescribeStacksRequest
                        .builder()
                        .nextToken(stacks.nextToken())
                        .build();

                stacks = cwf.describeStacks(describeStacksRequest);
                stackSet.addAll(stacks.stacks());
            }

            stackSuccessSet = stackSet
                    .stream()
                    .filter(s -> s.stackName().contains(filterStacks) &&
                            (s.stackStatus().equals(StackStatus.CREATE_COMPLETE) ||
                                    s.stackStatus().equals(StackStatus.UPDATE_COMPLETE)))
                    .collect(Collectors.toList());

        } catch (CloudFormationException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return stackSuccessSet;
    }

    public List<ResourceReport> getAllStackResources(List<Stack> stacks){
        List<ResourceReport> resourcesReport = new ArrayList<>();
        for (Stack stack: stacks) {
            LOG.info("Getting resources from " + stack.stackName());
            DescribeStackResourcesRequest describeStackResourcesRequest = DescribeStackResourcesRequest
                    .builder()
                    .stackName(stack.stackName())
                    .build();
            DescribeStackResourcesResponse response = cwf.describeStackResources(describeStackResourcesRequest);
            for (StackResource stackResource: response.stackResources()) {
                if(TypesAws.hasKey(stackResource.resourceType())){
                    TypesAws resourceType = TypesAws.fromKey(stackResource.resourceType());
                    IServiceCatalog iService = ServiceCatalogService.getInstance();

                    switch (resourceType){
                        case PORTFOLIO:
                            ResourceReport rrPort = iService.getPortfolioById(stackResource.physicalResourceId());
                            rrPort.setCreate(CreatedBy.PIPELINE);
                            resourcesReport.add(rrPort);
                            break;
                        case PRODUCT:
                            iService
                                    .getProvisionedProductByProductId(stackResource.physicalResourceId())
                                    .stream()
                                    .map(rrProd -> {
                                        ResourceReport resourceReport = ResourceReport
                                                .classicBuilder().withId(rrProd.getId())
                                                .withName(rrProd.getName())
                                                .withType(rrProd.getType())
                                                .build();
                                        resourceReport.setCreate(CreatedBy.PIPELINE);
                                        return resourceReport;
                                    })
                                    .forEach(resourcesReport::add);
                            break;
                        default:
                            ResourceReport rr = ResourceReport.classicBuilder()
                                    .withType(resourceType)
                                    .withId(stackResource.physicalResourceId())
                                    .build();
                            rr.setCreate(CreatedBy.PIPELINE);
                            resourcesReport.add(rr);
                            break;
                    }
                } else {
                    LOG.warn("Type " + stackResource.resourceType() +
                            " has not been implemented or not support tagging");
                }
            }
        }
        return resourcesReport;

    }
}
