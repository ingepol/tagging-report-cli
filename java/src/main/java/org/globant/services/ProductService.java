package org.globant.services;

import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;
import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.servicecatalog.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private static ProductService SERVICE;

    public static ProductService getInstance() {
        if (SERVICE == null) {
            SERVICE = new ProductService();
        }
        return SERVICE;
    }

    private ServiceCatalogClient client = ServiceCatalogClient.builder().region(Region.US_WEST_2).build();

    @Override
    public List<ResourceReport> getAllResource() {
        List<ResourceReport> resources = new ArrayList<>();

        LOG.debug("Getting PRODUCT resources..");
        client.listProvisionedProductPlans().provisionedProductPlans().stream()
                .map(this::reportProvisionedProduct)
                .forEach(resources::add);

        return resources;
    }

    @Override
    public List<TagReport> getTagResource(ResourceReport resource) {
        List<TagReport> report = Collections.emptyList();
        client.describeProvisionedProductPlan(
                DescribeProvisionedProductPlanRequest.builder().planId(resource.getArn()).build()
        ).provisionedProductPlanDetails().tags().stream()
                .map(tag -> new TagReport(tag.key(), tag.value()))
                .forEach(report::add);
        return report;
    }

    private ResourceReport reportProvisionedProduct(ProvisionedProductPlanSummary product) {
        ResourceReport report = new ResourceReport(
                TypesAws.PORTAFOLIO.getKey(),
                product.provisionProductName(),
                CreatedBy.CUSTOM
        );
        report.setArn(product.planId());
        return report;
    }
}
