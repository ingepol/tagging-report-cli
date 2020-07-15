package org.globant.cli.validate;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import software.amazon.awssdk.regions.Region;

public class ValidateRegion  implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
        boolean exist = false;
        for (Region regionAws: Region.regions()) {
            if (regionAws.toString().equals(value)){
                exist = true;
            }
        }
        if (!exist){
            throw new ParameterException("Parameter  " + name + " is wrong. Region " +
                    value + " doesn't exists");
        }
    }
}
