package org.globant.utils;

import com.opencsv.CSVReader;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.globant.model.ResourceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class CsvExporter {
    private static final Logger LOG = LoggerFactory.getLogger(CsvExporter.class);

    public static void write(List<ResourceReport> resources) {
        if(CollectionUtils.isEmpty(resources)) {
            LOG.warn("The process didn't found resources. "
                    + "Try with other resource type or change the seatch parameter");
            return;
        }

        try {
            File csv = File.createTempFile("report", ".csv");
            FileWriter writer = new FileWriter(csv);
            StatefulBeanToCsv<ResourceReport> toCsv = new StatefulBeanToCsvBuilder<ResourceReport>(writer)
                    .withMappingStrategy(new ResourceReportMapping())
                    .build();
            toCsv.write(resources);
            writer.close();
            LOG.info(csv.toString());
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            LOG.error("Error generating CSV report: " + e.getMessage());
        }
    }

    private static class ResourceReportMapping implements MappingStrategy<ResourceReport> {
        @Override
        public String[] generateHeader(ResourceReport bean) throws CsvRequiredFieldEmptyException {
            return new String[]{
                    "Type", "Resource Name", "Tags", "Missing Tags", "Created by", "Classic Coverage", "Modern Coverage"};
        }

        @Override
        public String[] transmuteBean(ResourceReport resource)
                throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
            return new String[]{
                    resource.getType().getValue(),
                    resource.getName(),
                    resource.getStringTags(),
                    resource.getStringMissingTags(),
                    resource.getCreatedBy().name(),
                    resource.getClassicCoverage() !=null? resource.getClassicCoverage().toString() + "%": "N/A",
                    resource.getModernCoverage()  !=null? resource.getModernCoverage().toString()  + "%": "N/A",
            };
        }

        @Override
        public void captureHeader(CSVReader reader) throws IOException, CsvRequiredFieldEmptyException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public ResourceReport populateNewBean(String[] line) throws CsvBeanIntrospectionException,
                CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, CsvConstraintViolationException,
                CsvValidationException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void setType(Class<? extends ResourceReport> type) throws CsvBadConverterException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void ignoreFields(MultiValuedMap<Class<?>, Field> fields) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}