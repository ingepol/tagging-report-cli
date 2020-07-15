package org.globant.cli.convert;

import com.beust.jcommander.IStringConverter;
import org.globant.enums.TypesAws;
import software.amazon.awssdk.regions.Region;

public class TypeConvert implements IStringConverter<TypesAws> {
    @Override
    public TypesAws convert(String type) {
        return TypesAws.fromValue(type);
    }
}
