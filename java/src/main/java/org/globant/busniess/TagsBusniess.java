package org.globant.busniess;

import org.globant.enums.ClassicTags;
import org.globant.enums.ModernTags;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagsBusniess {

    private static final Logger LOG = LoggerFactory.getLogger(TagsBusniess.class);

    public List<TagReport> getModernTags(List<TagReport> tags){
        List<TagReport> tagReports = new ArrayList<TagReport>();
        try {

        } catch (Exception ex) {
            LOG.error("Moder tags busniess failed!");
        }
        return tagReports;
    }


    public List<TagReport> getAllTags(List<TagReport> tags){
        List<TagReport> classicTags = new ArrayList<TagReport>();
        List<TagReport> modernTags = new ArrayList<TagReport>();
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
        List<TagReport> allTags =  Stream
                .concat(modernTags.stream(), classicTags.stream())
                .collect(Collectors.toList());
        return allTags;
    }

    public List<String> getMissingRequiredTags(List<TagReport> tags){

        List<String> modernEnumTags = Arrays
                .stream(ModernTags.values())
                .map(ModernTags::getValue)
                .collect(Collectors.toList());

        List<String> missingModernTags = missingTasg(tags, modernEnumTags);

        return missingModernTags;
    }

    private List<String> missingTasg(List<TagReport> tags, List<String> comparableTags){
        List<String> missingTags = new ArrayList<String>();
        List<String> keyTags = tags.stream()
                .map(TagReport::getKey)
                .collect(Collectors.toList());

        for (String comparableTag:comparableTags) {
            if (!keyTags.contains(comparableTag)) {
                missingTags.add(comparableTag);
            }
        }
        return missingTags;
    }
}
