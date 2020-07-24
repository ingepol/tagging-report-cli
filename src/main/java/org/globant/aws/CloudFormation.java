package org.globant.aws;

import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;
import org.globant.model.ResourceReport;
import org.globant.services.IServiceCatalog;
import org.globant.services.ServiceCatalogService;
import org.globant.services.StsService;
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
    private static final Logger NOP = LoggerFactory.getLogger("NOT_IMPLEMENTED_LOGGER");
    private static final Region REGION = Region.US_WEST_2;

    private final CloudFormationClient cwf;
    private final String account;

    public CloudFormation(){
        cwf = CloudFormationClient.builder()
                .region(REGION)
                .build();
        account = StsService.getInstance().getCurrentAccount();
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

                    if (TypesAws.PRODUCT == resourceType) {
                        iServiceCatalog
                                .getProvisionedResourceByProductId(stackResource.physicalResourceId())
                                .stream()
                                .peek(product -> product.setCreatedBy(CreatedBy.PIPELINE))
                                .forEach(resourcesReport::add);
                        iServiceCatalog
                                .getProvisionedProductByProductId(stackResource.physicalResourceId())
                                .forEach(pa -> {
                                    LOG.info("Getting stacks matching " + pa.id());
                                    getAllStackResources(listStacks(pa.id()))
                                            .stream()
                                            .peek(productResource -> productResource.setCreatedBy(CreatedBy.CUSTOM))
                                            .forEach(resourcesReport::add);
                                });
                    } else {
                        LOG.debug("Creating ResourceReport for " + stackResource);
                        ResourceReport rr = ResourceReport.builder()
                                .withRegion(REGION.id())
                                .withAccount(account)
                                .withType(resourceType)
                                .withName(stackResource.physicalResourceId());
                        rr.setCreatedBy(CreatedBy.PIPELINE);
                        resourcesReport.add(rr);
                    }
                } else {
                    LOG.warn("Type " + stackResource.resourceType() + " has not been implemented");
                    NOP.info(stackResource.resourceType());
                }
            }
        }
        return resourcesReport;
    }
}
