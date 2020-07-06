package org.globant.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;

public class StsService {
    private static final Logger LOG = LoggerFactory.getLogger(SSMService.class);
    private static StsService SERVICE;
    private static Region REGION = Region.US_WEST_2;
    private StsClient sts;

    private StsService(){
        sts = StsClient.builder().region(REGION).build();;
        LOG.debug("Created STS Service " + sts.getCallerIdentity());
    }

    public static StsService getInstance() {
        if (SERVICE == null) {
            SERVICE = new StsService();
        }
        return SERVICE;
    }

    public String getCurrentAccount(){
        return sts.getCallerIdentity().account();
    }
}
