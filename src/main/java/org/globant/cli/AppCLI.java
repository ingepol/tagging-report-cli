package org.globant.cli;

import com.beust.jcommander.JCommander;
import org.globant.aws.CloudFormation;
import org.globant.busniess.TagsBusiness;
import org.globant.cli.parameters.ParametersCLI;
import org.globant.factory.ServiceFactory;
import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.globant.services.IService;
import org.globant.services.RegionService;
import org.globant.utils.CsvExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudformation.model.Stack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.globant.enums.TypesAws.STACK;

class AppCLI {
    private static final ParametersCLI PARAMS = new ParametersCLI();
    private static final Logger LOG = LoggerFactory.getLogger(RegionService.class);

    public static void main(String ... argv) {
        AppCLI main = new AppCLI();
        RegionService.createInstance(PARAMS.getRegion());
        JCommander.newBuilder()
                .addObject(PARAMS)
                .build()
                .parse(argv);
        main.run();
    }

    public void run() {
        List<ResourceReport> resources = getResourcesReport();
        resources = getTagsReport(resources);
        CsvExporter.write(resources);
    }

    private List<ResourceReport> getResourcesReport(){
        List<ResourceReport> resources = new ArrayList<>();
        if (PARAMS.getType().equals(STACK)) {
            LOG.info("Getting stacks matching "+PARAMS.getSearch());
            CloudFormation cloudformation = new CloudFormation();
            List<Stack> stackSet = cloudformation.listStacks(PARAMS.getSearch());
            resources = cloudformation.getAllStackResources(stackSet);
        } else {
            IService awsService = ServiceFactory.getService(PARAMS.getType());
            if (awsService != null) {
                try {
                    resources = awsService.getAllResource();
                } catch (UnsupportedOperationException uoe){
                    LOG.error("Unsoported operation getAllResource for type " +
                            PARAMS.getType().getValue());
                }
            }
        }
        return resources;
    }

    private List<ResourceReport> getTagsReport(List<ResourceReport> resources) {
        TagsBusiness tagsBusiness = new TagsBusiness();

        for (ResourceReport resource: resources) {
            IService awsService = ServiceFactory.getService(resource.getType());
            if (awsService != null) {
                List<TagReport> tags = Collections.emptyList();
                try {
                    tags = awsService.getTagResource(resource);

                    List<TagReport> requiredTags = tagsBusiness.getAllTags(tags);
                    List<String> missingTags = tagsBusiness.getMissingModernTags(tags);
                    resource.setTags(requiredTags);
                    resource.setMissingTags(missingTags);

                    resource.setClassicCoverage(tagsBusiness.getClassicCoverage(tags));
                    resource.setModernCoverage(tagsBusiness.getModernCoverage(tags));
                } catch (UnsupportedOperationException e) {
                    LOG.warn(e.getMessage());
                }
            }
        }

        return resources;
    }
}

