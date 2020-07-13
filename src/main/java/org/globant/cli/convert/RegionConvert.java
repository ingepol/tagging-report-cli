package org.globant.cli.convert;

import com.beust.jcommander.IStringConverter;
import software.amazon.awssdk.regions.Region;

public class RegionConvert implements IStringConverter<Region> {
    @Override
    public Region convert(String region) {
        return Region.regions().
                stream()
                .filter(r -> r.toString().equals(region))
                .findFirst()
                .get();
    }
}
