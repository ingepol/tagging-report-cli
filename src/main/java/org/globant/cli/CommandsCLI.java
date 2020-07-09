package org.globant.cli;

import org.apache.commons.cli.*;
import org.globant.enums.TypesAws;
import org.globant.services.RegionService;

import static org.globant.enums.TypesAws.STACK;

public class CommandsCLI {

    public org.globant.model.ParamsCLI getParamsCLI(String[] args){

        org.globant.model.ParamsCLI paramsCLI = new org.globant.model.ParamsCLI();

        Options options = new Options();


        options.addOption(Option.builder("r")
                .longOpt("region")
                .hasArg(true)
                .desc("Apecify the region, default is us-west-2")
                .required(false)
                .build());
        options.addOption(Option.builder("f")
                .longOpt("filter")
                .hasArg(true)
                .desc("Filter to search a specific stack or several stack, " +
                        "it can be used as prefix. \n  " +
                        "It's required if type is Stack.")
                .required(false)
                .build());
        options.addOption(Option.builder("t")
                .longOpt("type")
                .hasArg(true)
                .desc("type name of resource that you need ([REQUIRED])")
                .required()
                .build());

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            if (cmd.hasOption("t")) {
                String type = cmd.getOptionValue("t");
                paramsCLI.setType(TypesAws.fromValue(type));
                System.out.println("Type " + paramsCLI.getType().getValue());

                if (cmd.hasOption("f")) {
                    String filter = cmd.getOptionValue("f");
                    paramsCLI.setFilter(filter);
                    System.out.println("Filter: " + paramsCLI.getFilter());
                } else if (paramsCLI.getType().equals(STACK)){
                    throw new ParseException("Filter is required, for " +
                            paramsCLI.getType().getValue() +" type.");
                }
            }
            if (cmd.hasOption("r")) {
                String region = cmd.getOptionValue("r");
                RegionService.createInstance(region);
            }else {
                RegionService.createDefaultAwsInstance();
            }
            paramsCLI.setRegion(RegionService.getInstance().getRegionAws());
            System.out.println("Region: " + paramsCLI.getRegion().toString());
        } catch (ParseException pe) {
            System.err.println("Error parsing command-line arguments!");
            System.err.println(pe.getMessage());
            System.out.println("Please, follow the instructions below:");
            printHelp(options);
            System.exit(0);
        }

        return paramsCLI;
    }

    private void printHelp(Options options){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "Generate tagging report for aws resources", options );
    }
}
