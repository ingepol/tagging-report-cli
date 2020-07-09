package org.globant.utils;

import org.globant.model.ResourceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private static String convertToStringSeparatedByTab(String[] data) {
        return Stream.of(data)
                .map(Utils::escapeSpecialCharacters)
                .collect(Collectors.joining("\t"));
    }

    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public static void writeCSV(List<ResourceReport> resources ){
        try {

            File csvOutputFile = File.createTempFile("report", ".csv");

            List<String[]> dataLines = new ArrayList<>();
            String[] headers = new String[] {
                    "Type","Resource Name","Tags","Missing Tags","Created by","Classic Coverage","Modern Coverage"};
            dataLines.add(headers);

            Comparator<ResourceReport> compareByTypeThenResorceName = Comparator
                    .comparing(ResourceReport::getType)
                    .thenComparing(ResourceReport::getResourceName);

            List<ResourceReport> sortedResources = resources.stream()
                    .sorted(compareByTypeThenResorceName)
                    .collect(Collectors.toList());

            for (ResourceReport resource: sortedResources) {
                    dataLines.add(new String[] {
                            resource.getType().getValue(),
                            resource.getResourceName(),
                            resource.getStringTags(),
                            resource.getStringMissingTags(),
                            resource.getCreated().name(),
                            resource.getClassic().toString(),
                            resource.getModern().toString()
                    });
            }

            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                dataLines.stream()
                        .map(Utils::convertToStringSeparatedByTab)
                        .forEach(pw::println);
            }

            if (csvOutputFile.exists()) {
                LOG.info("CSV generated. Path: " + csvOutputFile.getAbsolutePath());
            } else {
                LOG.error("CSV was not generated");
            }
        } catch (IOException e) {
            LOG.error("IOException " + e.getMessage());
        }
    }

}
