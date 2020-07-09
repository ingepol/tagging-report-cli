package org.globant.services;

import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
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
    IamClient client;

    private RoleService(){
        Region region = RegionService.getInstance().getRegionGlobalAws();
        client = IamClient.builder()
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
        throw new UnsupportedOperationException();
    }

    public List<TagReport> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a role, Name:  " + resource.getResourceName());
        List<TagReport> tagSet = new ArrayList<TagReport>();
        ListRoleTagsRequest request = ListRoleTagsRequest
                .builder()
                .roleName(resource.getResourceName())
                .build();

        ListRoleTagsResponse response = client.listRoleTags(request);

        for (Tag tag: response.tags()) {
            tagSet.add(new TagReport(tag.key(), tag.value()));
        }
        return tagSet;
    }
}
