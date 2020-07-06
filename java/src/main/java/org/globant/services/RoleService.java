package org.globant.services;

import org.globant.model.TagReport;
import org.globant.model.ResourceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.ListRoleTagsRequest;
import software.amazon.awssdk.services.iam.model.ListRoleTagsResponse;
import software.amazon.awssdk.services.iam.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class RoleService implements IService{
    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);
    private static RoleService roleService;
    IamClient iam;

    private RoleService(){
        Region region = Region.AWS_GLOBAL;
        iam = IamClient.builder()
                .region(region)
                .build();
    }

    public  static RoleService getInstance() {
        if (roleService == null) {
            roleService = new RoleService();
        }
        return roleService;
    }

    @Override
    public List<ResourceReport> getAllResource() {
        return null;
    }

    public List<TagReport> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a role, Name:  " + resource.getResourceName());
        List<TagReport> tagSet = new ArrayList<TagReport>();
        ListRoleTagsRequest request = ListRoleTagsRequest
                .builder()
                .roleName(resource.getResourceName())
                .build();

        ListRoleTagsResponse response = iam.listRoleTags(request);

        for (Tag tag: response.tags()) {
            tagSet.add(new TagReport(tag.key(), tag.value()));
        }
        return tagSet;
    }
}
