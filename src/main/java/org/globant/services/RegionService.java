package org.globant.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;

public class RegionService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionService.class);

    private static RegionService SERVICE;
    private static Region REGION_AWS;

    private RegionService(Region region){
        REGION_AWS = region;
    }

    public static void createInstance(Region region){
        if (SERVICE == null) {
            SERVICE = new RegionService(region);
            LOG.info("Region selected: " + SERVICE.toString());
        }
    }

    public static RegionService getInstance(){
        return SERVICE;
    }

    public Region getRegionAws(){
        return REGION_AWS;
    }

    public Region getRegionGlobalAws(){
        return Region.AWS_GLOBAL;
    }
}
