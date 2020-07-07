package org.globant.services;

import org.globant.enums.CreatedBy;
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

import static org.globant.enums.TypesAws.*;

public class ServiceCatalogService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceCatalogService.class);
    private static ServiceCatalogService SERVICE;

    public static ServiceCatalogService getInstance() {
        if (SERVICE == null) {
            SERVICE = new ServiceCatalogService();
        }
        return SERVICE;
    }

    private final ServiceCatalogClient client = ServiceCatalogClient.builder().region(Region.US_WEST_2).build();

    @Override
    public List<ResourceReport> getAllResource() {
        List<ResourceReport> resources = new ArrayList<>();

        LOG.debug("Getting PORTFOLIO resources..");
        client.listPortfolios(ListPortfoliosRequest.builder().build()).portfolioDetails().stream()
                .map(this::reportPortfolio)
                .forEach(resources::add);
        LOG.debug("Getting PRODUCT resources..");
        client.listProvisionedProductPlans().provisionedProductPlans().stream()
                .map(this::reportProvisionedProduct)
                .forEach(resources::add);

        return resources;
    }

    @Override
    public List<TagReport> getTagResource(ResourceReport resource) {
        switch (resource.getType()) {
            case PORTAFOLIO:
                return getTagResourcePortfolio(resource);
            case PRODUCT:
                return getTagResourceProduct(resource);
            default:
                throw new UnsupportedOperationException("Unknown resource type: " + resource.getType());
        }
    }

    private List<TagReport> getTagResourcePortfolio(ResourceReport resource) {
        List<TagReport> report = Collections.emptyList();
        client.describePortfolio(DescribePortfolioRequest.builder().id(resource.getArn()).build()).tags().stream()
                .map(tag -> new TagReport(tag.key(), tag.value()))
                .forEach(report::add);
        return report;
    }

    private List<TagReport> getTagResourceProduct(ResourceReport resource) {
        List<TagReport> report = Collections.emptyList();
        client.describeProvisionedProductPlan(
                DescribeProvisionedProductPlanRequest.builder().planId(resource.getArn()).build()
        ).provisionedProductPlanDetails().tags().stream()
                .map(tag -> new TagReport(tag.key(), tag.value()))
                .forEach(report::add);
        return report;
    }

    private ResourceReport reportPortfolio(PortfolioDetail portfolio) {
        ResourceReport report = new ResourceReport(
                PORTAFOLIO,
                portfolio.displayName(),
                CreatedBy.PIPELINE
        );
        report.setArn(portfolio.id());
        return report;
    }

    private ResourceReport reportProvisionedProduct(ProvisionedProductPlanSummary product) {
        ResourceReport report = new ResourceReport(
                PRODUCT,
                product.provisionProductName(),
                CreatedBy.PIPELINE
        );
        report.setArn(product.planId());
        return report;
    }
}
