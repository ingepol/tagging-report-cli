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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.globant.enums.TypesAws.PORTAFOLIO;
import static org.globant.enums.TypesAws.PRODUCT;

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
        client.listRecordHistory().recordDetails().stream()
                .map(RecordDetail::provisionedProductId)
                .collect(Collectors.toSet())
                .stream()
                .map(DescribeProvisionedProductRequest.builder()::id)
                .map(DescribeProvisionedProductRequest.Builder::build)
                .map(req -> {
                    try {
                        return client.describeProvisionedProduct(req);
                    } catch (ResourceNotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(DescribeProvisionedProductResponse::provisionedProductDetail)
                .map(ProvisionedProductDetail::lastRecordId)
                .map(DescribeRecordRequest.builder()::id)
                .map(DescribeRecordRequest.Builder::build)
                .map(client::describeRecord)
                .map(DescribeRecordResponse::recordDetail)
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
        List<TagReport> report = new ArrayList<>();
        client.describePortfolio(DescribePortfolioRequest.builder().id(resource.getArn()).build()).tags().stream()
                .map(tag -> new TagReport(tag.key(), tag.value()))
                .forEach(report::add);
        return report;
    }

    private List<TagReport> getTagResourceProduct(ResourceReport resource) {
        List<TagReport> report = new ArrayList<>();
        client.describeRecord(
                DescribeRecordRequest.builder().id(resource.getArn()).build()
        ).recordDetail().recordTags().stream()
                .map(recordTag -> new TagReport(recordTag.key(), recordTag.value()))
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

    private ResourceReport reportProvisionedProduct(RecordDetail product) {
        ResourceReport report = new ResourceReport(
                PRODUCT,
                product.provisionedProductName(),
                CreatedBy.PIPELINE
        );
        report.setArn(product.recordId());
        return report;
    }
}
