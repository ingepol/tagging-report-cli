package org.globant.busniess;

import org.globant.enums.ClassicTags;
import org.globant.enums.ModernTags;
import org.globant.model.ReportTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagsBusniess {

    private static final Logger LOG = LoggerFactory.getLogger(TagsBusniess.class);

    public List<ReportTag> getModernTags(List<ReportTag> tags){
        List<ReportTag> reportTags = new ArrayList<ReportTag>();
        try {

        } catch (Exception ex) {
            LOG.error("Moder tags busniess failed!");
        }
        return reportTags;
    }


    public List<ReportTag> getAllTags(List<ReportTag> tags){
        List<ReportTag> classicTags = new ArrayList<ReportTag>();
        List<ReportTag> modernTags = new ArrayList<ReportTag>();
        try {
            classicTags =
                    tags.stream()
                            .filter(tag -> Arrays.stream(ClassicTags.values())
                                    .map(ClassicTags::getKey)
                                    .anyMatch(value -> value.equals(tag.getKey())))
                            .collect(Collectors.toList());
            modernTags =
                    tags.stream()
                            .filter(tag -> Arrays.stream(ModernTags.values())
                                    .map(ModernTags::getValue)
                                    .anyMatch(value -> value.equals(tag.getKey())))
                            .collect(Collectors.toList());
        } catch (Exception ex) {
            LOG.error("Classic tags busniess failed!");
        }
        List<ReportTag> allTags =  Stream
                .concat(modernTags.stream(), classicTags.stream())
                .collect(Collectors.toList());
        return allTags;
    }

    public List<String> getMissingRequiredTags(List<ReportTag> tags){

        List<String> modernEnumTags = Arrays
                .stream(ModernTags.values())
                .map(ModernTags::getValue)
                .collect(Collectors.toList());

        List<String> missingModernTags = missingTasg(tags, modernEnumTags);

        return missingModernTags;
    }

    private List<String> missingTasg(List<ReportTag> tags, List<String> comparableTags){
        List<String> missingTags = new ArrayList<String>();
        List<String> keyTags = tags.stream()
                .map(ReportTag::getKey)
                .collect(Collectors.toList());

        for (String comparableTag:comparableTags) {
            if (!keyTags.contains(comparableTag)) {
                missingTags.add(comparableTag);
            }
        }
        return missingTags;
    }
}
