package com.example.newsplatform.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SafeHtmlValidatorTest {

    private final SafeHtmlValidator validator = new SafeHtmlValidator();

    @Test
    void testYouTubeLinkConversion() {
        String content = "Check this video: https://www.youtube.com/watch?v=Q_eG_S-oJ6w";
        String result = SafeHtmlValidator.convertYouTubeLinks(content);
        
        assertTrue(result.contains("iframe"));
        assertTrue(result.contains("youtube.com/embed/Q_eG_S-oJ6w"));
        assertTrue(result.contains("position: relative"));
    }

    @Test
    void testShortYouTubeLinkConversion() {
        String content = "Short link: https://youtu.be/Q_eG_S-oJ6w";
        String result = SafeHtmlValidator.convertYouTubeLinks(content);
        
        assertTrue(result.contains("iframe"));
        assertTrue(result.contains("youtube.com/embed/Q_eG_S-oJ6w"));
    }

    @Test
    void testAllowedTags() {
        String content = "<p><b>Bold</b> <i>Italic</i> <strong>Strong</strong></p>";
        assertTrue(validator.isValid(content, null));
    }

    @Test
    void testIframeAllowed() {
        String content = "<iframe src=\"https://www.youtube.com/embed/test\"></iframe>";
        assertTrue(validator.isValid(content, null));
    }
}