package org.globant.services;

import org.globant.model.ResourceReport;

import java.util.List;

public interface IServiceCatalog {
    ResourceReport getPortfolioById(String id);
    List<ResourceReport> getProvisionedProductByProductId(String id);
}

