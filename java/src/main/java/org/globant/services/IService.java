package org.globant.services;

import org.globant.model.TagReport;
import org.globant.model.ResourceReport;

import java.util.List;

public interface IService {
    public List<ResourceReport> getAllResource();
    public List<TagReport> getTagResource(ResourceReport resource);
}
