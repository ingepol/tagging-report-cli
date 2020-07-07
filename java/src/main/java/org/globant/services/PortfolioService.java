package org.globant.services;

import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;
import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.servicecatalog.model.DescribePortfolioRequest;
import software.amazon.awssdk.services.servicecatalog.model.ListPortfoliosRequest;
import software.amazon.awssdk.services.servicecatalog.model.PortfolioDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortfolioService implements IService {
    private static final Logger LOG = LoggerFactory.getLogger(PortfolioService.class);
    private static PortfolioService SERVICE;

    public static PortfolioService getInstance() {
        if (SERVICE == null) {
            SERVICE = new PortfolioService();
        }
        return SERVICE;
    }

    private ServiceCatalogClient client = ServiceCatalogClient.builder().region(Region.US_WEST_2).build();

    @Override
    public List<ResourceReport> getAllResource() {
        List<ResourceReport> resources = new ArrayList<>();

        LOG.debug("Getting PORTFOLIO resources..");
        client.listPortfolios(ListPortfoliosRequest.builder().build()).portfolioDetails().stream()
                .map(this::reportPortfolio)
                .forEach(resources::add);

        return resources;
    }

    @Override
    public List<TagReport> getTagResource(ResourceReport resource) {
        List<TagReport> report = Collections.emptyList();
        client.describePortfolio(DescribePortfolioRequest.builder().id(resource.getArn()).build()).tags().stream()
                .map(tag -> new TagReport(tag.key(), tag.value()))
                .forEach(report::add);
        return report;
    }

    private ResourceReport reportPortfolio(PortfolioDetail portfolio) {
        ResourceReport report = new ResourceReport(
                TypesAws.PORTAFOLIO.getKey(),
                portfolio.displayName(),
                CreatedBy.CUSTOM
        );
        report.setArn(portfolio.arn());
        return report;
    }
}
