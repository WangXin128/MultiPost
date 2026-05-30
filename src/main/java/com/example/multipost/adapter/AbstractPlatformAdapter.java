package com.example.multipost.adapter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlatformAdapter implements PlatformAdapter {
    protected String shorten(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    protected String summaryOrFallback(StandardContent content, int maxLength) {
        String source = content.summary();
        if (source == null || source.isBlank()) {
            source = content.body().replaceAll("\\s+", " ").trim();
        }
        return shorten(source, maxLength);
    }

    protected String normalizeParagraphs(String body) {
        return String.join("\n\n", body.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .toList());
    }

    protected List<String> limitTags(List<String> tags, int limit) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return tags.stream()
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .limit(limit)
                .toList();
    }

    protected List<String> hashtagTags(List<String> tags, int limit) {
        return limitTags(tags, limit).stream()
                .map(tag -> "#" + tag.replaceAll("\\s+", ""))
                .toList();
    }

    protected PlatformValidationResult validateRequired(AdaptedContent content, int maxTitleLength, int maxTags) {
        List<String> errors = new ArrayList<>();
        if (content.title() == null || content.title().isBlank()) {
            errors.add("title is required");
        }
        if (content.title() != null && content.title().length() > maxTitleLength) {
            errors.add("title exceeds " + maxTitleLength + " characters");
        }
        if (content.body() == null || content.body().isBlank()) {
            errors.add("body is required");
        }
        if (content.tags() != null && content.tags().size() > maxTags) {
            errors.add("too many tags");
        }
        return errors.isEmpty() ? PlatformValidationResult.ok() : PlatformValidationResult.failed(errors);
    }
}
