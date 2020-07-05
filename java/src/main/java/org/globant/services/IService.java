package org.globant.services;

import org.globant.model.ReportTag;
import org.globant.model.ResourceReport;

import java.util.List;

public interface IService {
    public List<ReportTag> getTagResource(ResourceReport resource);
}
