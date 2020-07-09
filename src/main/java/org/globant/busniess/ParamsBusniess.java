package org.globant.busniess;

import org.globant.enums.TypesAws;
import org.globant.model.ParamsCLI;
import org.globant.services.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Stream;

public class ParamsBusniess {

    private static final Logger LOG = LoggerFactory.getLogger(ParamsBusniess.class);

    public boolean validateRequiredArgs(String[] args){
        boolean resultType = Stream.of(args).anyMatch(arg -> arg.startsWith("type="));
        boolean resultTypeStack = Stream.of(args).anyMatch(arg -> arg.equals("type=Stack"));
        boolean resultFilter = Stream.of(args).anyMatch(arg -> arg.startsWith("filter=")) && resultTypeStack;
        if(!resultType){
            LOG.info("type is required");
        }
        if (resultTypeStack && !resultFilter){
            LOG.info("filter is required when type is Stack ");
        }

        if(resultTypeStack){
            return resultType && resultFilter;
        }
        return resultType;
    }

    public ParamsCLI getParamsCLI(String[] args){

        ParamsCLI paramsCLI = new ParamsCLI();
        for (String arg: args){
            if (arg.startsWith("type=")){
                paramsCLI.setType(TypesAws.fromValue(arg.substring(5)));
                if (paramsCLI.getType() == null){
                    LOG.error("The type doesn't exist or it's not implemented, yet");
                    System.exit(0);
                }
                LOG.info("Type: " + paramsCLI.getType().getValue());
            }
            if (arg.startsWith("filter=")){
                paramsCLI.setFilter(arg.substring(7));
                LOG.info("Filter: " + paramsCLI.getFilter());
            }

            if (arg.startsWith("region=")){
                RegionService.createInstance(arg.substring(7));
            }
        }
        if (RegionService.getInstance() == null){
            RegionService.createDefaultAwsInstance();
        }
        paramsCLI.setRegion(RegionService.getInstance().getRegionAws());
        LOG.info("Region: " + paramsCLI.getRegion().toString());

        return paramsCLI;
    }
}
