package org.globant.services;

import org.globant.model.ResourceReport;
import software.amazon.awssdk.services.servicecatalog.model.ProvisionedProductAttribute;

import java.util.List;

public interface IServiceCatalog {
    ResourceReport getPortfolioById(String id);
    List<ResourceReport> getProvisionedResourceByProductId(String id);
    List<ProvisionedProductAttribute> getProvisionedProductByProductId(String id);
}

