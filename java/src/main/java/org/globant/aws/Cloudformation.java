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

import static org.globant.enums.TypesAws.*;

public class Cloudformation {

    private static final Logger LOG = LoggerFactory.getLogger(Cloudformation.class);

    CloudFormationClient cwf;

    public Cloudformation(){
        Region region = Region.US_WEST_2;
        cwf = CloudFormationClient.builder()
                .region(region)
                .build();
    }

    public List<Stack> listStacks(String filterStacks) {

        List<Stack> stackSuccessSet = new ArrayList<Stack>();

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
        List<ResourceReport> resourcesReport = new ArrayList<ResourceReport>();
        for (Stack stack: stacks) {
            LOG.info("Getting resources from " + stack.stackName());
            DescribeStackResourcesRequest describeStackResourcesRequest = DescribeStackResourcesRequest
                    .builder()
                    .stackName(stack.stackName())
                    .build();
            DescribeStackResourcesResponse response = cwf.describeStackResources(describeStackResourcesRequest);
            for (StackResource stackResource: response.stackResources()) {
                Boolean isTagged = false;
                for (TypesAws typeAws : TypesAws.values()){
                    if (typeAws.getKey().equals(stackResource.resourceType())){
                        ResourceReport resourceReport = new ResourceReport(
                                TypesAws.fromKey(stackResource.resourceType()),
                                CreatedBy.PIPELINE);
                        IServiceCatalog iService = ServiceCatalogService.getInstance();
                        switch (resourceReport.getType()){
                            case PORTAFOLIO:
                                ResourceReport resourcePortafolio = iService
                                        .getPortfolioById(stackResource.physicalResourceId());
                                resourceReport = resourcePortafolio;
                                resourcesReport.add(resourceReport);
                                break;
                            case PRODUCT:
                                List<ResourceReport> provisionedSetProduct = iService
                                        .getProvisionedProductByProductId(stackResource.physicalResourceId());
                                for (ResourceReport provisionedProduct: provisionedSetProduct) {
                                    resourcesReport.add(provisionedProduct);
                                }
                                break;
                            default:
                                resourceReport.setResourceName(stackResource.physicalResourceId());
                                resourcesReport.add(resourceReport);
                                break;
                        }

                        break;
                    } else {
                        LOG.warn("Type " + typeAws.getKey() + " doesn't has been implemented or doesn't support tags");
                    }
                }
            }
        }
        return resourcesReport;

    }
}
