package com.example.phoebe.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for SafeHtml annotation.
 * Checks that HTML content contains only allowed tags and converts YouTube links to embed code.
 */
public class SafeHtmlValidator implements ConstraintValidator<SafeHtml, String> {

    private static final Set<String> ALLOWED_TAGS = Set.of(
        "img", "b", "i", "a", "u", "strong", "em", "iframe", "div", "p"
    );

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("</?([a-zA-Z][a-zA-Z0-9]*)[^>]*>");
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(
        "https?://(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        String processedValue = convertYouTubeLinks(value);
        
        Matcher matcher = HTML_TAG_PATTERN.matcher(processedValue);
        while (matcher.find()) {
            String tagName = matcher.group(1).toLowerCase();
            if (!ALLOWED_TAGS.contains(tagName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts YouTube URLs to responsive embed iframe code
     */
    public static String convertYouTubeLinks(String content) {
        if (content == null) {
            return null;
        }

        Matcher matcher = YOUTUBE_URL_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String videoId = matcher.group(1);
            String embedCode = String.format(
                "<div style=\"position: relative; padding-bottom: 56.25%%; height: 0; overflow: hidden;\">" +
                "<iframe src=\"https://www.youtube.com/embed/%s\" " +
                "style=\"position: absolute; top: 0; left: 0; width: 100%%; height: 100%%;\" " +
                "frameborder=\"0\" allowfullscreen></iframe></div>",
                videoId
            );
            matcher.appendReplacement(result, embedCode);
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
}