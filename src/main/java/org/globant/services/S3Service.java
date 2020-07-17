package org.globant.services;

import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class S3Service implements  IService{
    private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
    private static S3Service s3Service;
    S3Client client;

    private S3Service(){
        Region region = RegionService.getInstance().getRegionAws();
        client = S3Client.builder()
                .region(region)
                .build();
    }

    public  static S3Service getInstance() {
        if (s3Service == null) {
            s3Service = new S3Service();
        }
        return s3Service;
    }

    @Override
    public List<ResourceReport> getAllResource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ResourceReport> getResourceBy(String filter) {
        throw new UnsupportedOperationException();
    }

    public List<TagReport> getTagResource(ResourceReport resource){
        LOG.info("Getting tags from a s3, Name:  " + resource.getName());
        List<TagReport> tagSet = new ArrayList<>();
        GetBucketTaggingRequest request = GetBucketTaggingRequest
                .builder()
                .bucket(resource.getName())
                .build();

        GetBucketTaggingResponse response = client.getBucketTagging(request);

        for (Tag tag: response.tagSet()) {
            tagSet.add(new TagReport(tag.key(), tag.value()));
        }
        return tagSet;
    }
}
