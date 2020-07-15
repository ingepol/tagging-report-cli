package org.globant.cli.validate;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import org.globant.enums.TypesAws;

public class ValidateType implements IParameterValidator{
    @Override
    public void validate(String name, String value) throws ParameterException {
        if (!TypesAws.hasValue(value)) {
            throw new ParameterException("Parameter  " + name + " is wrong. Type  "
                    + value + " is not supported");
        }
    }
}
