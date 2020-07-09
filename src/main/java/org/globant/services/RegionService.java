package org.globant.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;

public class RegionService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionService.class);

    private static RegionService SERVICE;
    private static Region REGION_AWS;

    private RegionService(String region){
        REGION_AWS = getRerionAwnByValue(region);
    }

    public static void createInstance(String region){
        if (SERVICE == null) {
            SERVICE = new RegionService(region);
        }
    }

    public static void createDefaultAwsInstance(){
        if (SERVICE == null) {
            SERVICE = new RegionService(Region.US_WEST_2.toString());
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

    private Region getRerionAwnByValue(String region){
        for (Region regionAws: Region.regions()) {
            if (regionAws.toString().equals(region)){
                return regionAws;
            }
        }
        LOG.info("The region " + region + " is not supported. Default region is configured");
        return Region.US_WEST_2;
    }



}
