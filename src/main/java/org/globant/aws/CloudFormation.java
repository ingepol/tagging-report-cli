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
import software.amazon.awssdk.services.servicecatalog.model.ProvisionedProductAttribute;

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
                    IServiceCatalog iServiceCatalog = ServiceCatalogService.getInstance();

                    switch (resourceType){
                        case PRODUCT:
                            iServiceCatalog
                                    .getProvisionedProductByProductId(stackResource.physicalResourceId())
                                    .stream()
                                    .map(rrProd -> {
                                        rrProd.setCreatedBy(CreatedBy.PIPELINE);
                                        return rrProd;
                                    })
                                    .forEach(resourcesReport::add);
                            break;
                        default:
                            ResourceReport rr = ResourceReport.classicBuilder()
                                    .withType(resourceType)
                                    .withName(stackResource.physicalResourceId())
                                    .withId(stackResource.physicalResourceId())
                                    .build();
                            rr.setCreatedBy(CreatedBy.PIPELINE);
                            resourcesReport.add(rr);
                            break;
                    }
                } else {
                    LOG.warn("Type " + stackResource.resourceType() + " has not been implemented");
                }
            }
        }
        return resourcesReport;

    }
}
