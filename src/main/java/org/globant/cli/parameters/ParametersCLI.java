package org.globant.cli.parameters;

import com.beust.jcommander.Parameter;
import org.globant.cli.convert.RegionConvert;
import org.globant.cli.convert.TypeConvert;
import org.globant.cli.validate.ValidateRegion;
import org.globant.cli.validate.ValidateType;
import org.globant.enums.TypesAws;
import software.amazon.awssdk.regions.Region;

public class ParametersCLI {
    @Parameter(names={"--type", "-t"},
            description = "Type of resource that you need evaluate",
            required = true,
            converter = TypeConvert.class,
            validateWith = ValidateType.class
    )
    TypesAws type;

    @Parameter(names={"--search", "-s"},
            description = "Filter to search a specific resource or stack")
    String search;

    @Parameter(names={"--region", "-r"},
            description = "Indicate the aws region, default is us-west-2",
            converter = RegionConvert.class,
            validateWith = ValidateRegion.class)
    Region region = Region.US_WEST_2;

    public TypesAws getType() {
        return type;
    }

    public String getSearch() {
        return search;
    }

    public Region getRegion() {
        return region;
    }
}
