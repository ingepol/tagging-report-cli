package org.globant.services;

import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.servicecatalog.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.globant.enums.TypesAws.PORTFOLIO;
import static org.globant.enums.TypesAws.PRODUCT;

public class ServiceCatalogService implements IService, IServiceCatalog {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceCatalogService.class);
    private static ServiceCatalogService SERVICE;
    private ServiceCatalogClient client;
    private static final AccessLevelFilter ACCOUNT_FILTER = AccessLevelFilter.builder()
            .key(AccessLevelFilterKey.ACCOUNT).value("self").build();

    private ServiceCatalogService(){
        Region region = RegionService.getInstance().getRegionAws();
        client = ServiceCatalogClient.builder().region(region).build();
    }

    public static ServiceCatalogService getInstance() {
        if (SERVICE == null) {
            SERVICE = new ServiceCatalogService();
        }
        return SERVICE;
    }

    @Override
    public List<ResourceReport> getAllResource() {
        List<ResourceReport> resources = new ArrayList<>();

        LOG.debug("Getting PORTFOLIO resources..");
        client.listPortfolios(ListPortfoliosRequest.builder().build()).portfolioDetails()
                .stream()
                .map(this::reportPortfolio)
                .forEach(resources::add);
        LOG.debug("Getting PRODUCT resources..");
        client.searchProvisionedProducts(
                SearchProvisionedProductsRequest.builder().accessLevelFilter(ACCOUNT_FILTER).build()
        ).provisionedProducts().stream()
                .filter(r -> r.status().equals(ProvisionedProductStatus.AVAILABLE))
                .map(this::reportProvisionedProduct)
                .forEach(resources::add);
        return resources;
    }

    @Override
    public List<ResourceReport> getResourceBy(String filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TagReport> getTagResource(ResourceReport resource) {
        switch (resource.getType()) {
            case PORTFOLIO:
                return getTagResourcePortfolio(resource);
            case PRODUCT:
                return getTagResourceProduct(resource);
            default:
                throw new UnsupportedOperationException("Unknown resource type: " + resource.getType());
        }
    }

    private List<TagReport> getTagResourcePortfolio(ResourceReport resource) {
        LOG.info("Getting tags from a portfolio, Name:  " + resource.getName());
        List<TagReport> report = new ArrayList<>();
        client.describePortfolio(DescribePortfolioRequest.builder().id(resource.getId()).build()).tags().stream()
                .map(tag -> new TagReport(tag.key(), tag.value()))
                .forEach(report::add);
        return report;
    }

    private List<TagReport> getTagResourceProduct(ResourceReport resource) {
        LOG.info("Getting tags from a product, Name:  " + resource.getName());
        List<TagReport> report = new ArrayList<>();
        HashMap<ProvisionedProductViewFilterBy, List<String>> filterResource = new HashMap<>();
        filterResource.put(ProvisionedProductViewFilterBy.SEARCH_QUERY, Collections.singletonList(resource.getId()));
        client.searchProvisionedProducts(
                SearchProvisionedProductsRequest.builder()
                        .accessLevelFilter(ACCOUNT_FILTER)
                        .filters(filterResource)
                        .build()
        ).provisionedProducts().get(0).tags()
                .stream()
                .map(tag -> new TagReport(tag.key(), tag.value()))
                .forEach(report::add);
        return report;
    }

    @Override
    public  ResourceReport getPortfolioById(String id){
        PortfolioDetail detail = client.describePortfolio(DescribePortfolioRequest
                .builder().id(id).build()).portfolioDetail();
        return reportPortfolio(detail);
    }

    @Override
    public List<ResourceReport> getProvisionedProductByProductId(String id){
        List<ResourceReport> resources = new ArrayList<>();
        HashMap<ProvisionedProductViewFilterBy, List<String>> filterProduct = new HashMap<>();
        filterProduct.put(ProvisionedProductViewFilterBy.SEARCH_QUERY, Collections.singletonList(id));
        SearchProvisionedProductsResponse response = client.searchProvisionedProducts(
                SearchProvisionedProductsRequest.builder()
                        .accessLevelFilter(ACCOUNT_FILTER)
                        .filters(filterProduct)
                        .build()
        );
        List<ProvisionedProductAttribute> provisionedProducts = new ArrayList<>(response.provisionedProducts());
        while(response.nextPageToken() != null){
            LOG.info("Fetched provisioned products: " + provisionedProducts.size());
            response = client.searchProvisionedProducts(
                    SearchProvisionedProductsRequest.builder()
                            .accessLevelFilter(ACCOUNT_FILTER)
                            .filters(filterProduct)
                            .pageToken(response.nextPageToken())
                            .build()
            );
            provisionedProducts.addAll(response.provisionedProducts());
        }
        LOG.info("Fetched provisioned products: " + provisionedProducts.size());
        provisionedProducts
                .stream()
                .filter(r -> r.status().equals(ProvisionedProductStatus.AVAILABLE))
                .map(this::reportProvisionedProduct)
                .forEach(resources::add);
        return resources;
    }

    private ResourceReport reportPortfolio(PortfolioDetail portfolio) {
        LOG.debug(portfolio.toString());
        return ResourceReport.classicBuilder()
                .withType(PORTFOLIO)
                .withName(portfolio.displayName())
                .withId(portfolio.id())
                .build();
    }

    private ResourceReport reportProvisionedProduct(ProvisionedProductAttribute product) {
        LOG.debug(product.toString());
        return ResourceReport.classicBuilder()
                .withType(PRODUCT)
                .withName(product.name())
                .withId(product.provisioningArtifactId())
                .build();
    }
}
