package org.globant.services;

import org.globant.model.TagReport;
import org.globant.model.ResourceReport;

import java.util.List;

public interface IService {
    List<ResourceReport> getAllResource();
    List<ResourceReport> getResourceBy(String filter);
    List<TagReport> getTagResource(ResourceReport resource);
}
