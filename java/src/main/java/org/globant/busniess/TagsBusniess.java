package org.globant.busniess;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.globant.enums.ClassicTags;
import org.globant.enums.ModernTags;
import org.globant.model.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagsBusniess {
    private static final Logger LOG = LoggerFactory.getLogger(TagsBusniess.class);

    private List<TagReport> getClassicTags(List<TagReport> tags) {
        return tags
                .stream()
                .filter(tag -> Arrays.stream(ClassicTags.values())
                        .map(ClassicTags::getKey)
                        .anyMatch(value -> value.equals(tag.getKey())))
                .collect(Collectors.toList());
    }

    private List<TagReport> getModernTags(List<TagReport> tags) {
        return tags
                .stream()
                .filter(tag -> Arrays.stream(ModernTags.values())
                        .map(ModernTags::getValue)
                        .anyMatch(value -> value.equals(tag.getKey())))
                .collect(Collectors.toList());
    }

    public List<TagReport> getAllTags(List<TagReport> tags){
        final ListOrderedSet<TagReport> all = new ListOrderedSet<>();
        all.addAll(getClassicTags(tags));
        all.addAll(getModernTags(tags));
        return all.asList();
    }

    public List<String> getMissingModernTags(List<TagReport> tags){
        List<String> modernEnumTags = Arrays
                .stream(ModernTags.values())
                .map(ModernTags::getValue)
                .collect(Collectors.toList());

        List<String> missingTags = new ArrayList<String>();
        List<String> keyTags = tags.stream()
                .map(TagReport::getKey)
                .collect(Collectors.toList());

        for (String comparableTag:modernEnumTags) {
            if (!keyTags.contains(comparableTag)) {
                missingTags.add(comparableTag);
            }
        }
        return missingTags;
    }

    public Integer getClassicCoverage(List<TagReport> tags) {
        float have = getClassicTags(tags).size();
        float want = ClassicTags.values().length;
        return (int) (have/want*100);
    }

    public Integer getModernCoverage(List<TagReport> tags) {
        float have = getModernTags(tags).size();
        float want = ModernTags.values().length;
        return (int) (have/want*100);
    }
}