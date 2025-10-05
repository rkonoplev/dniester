package com.example.newsplatform.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for SafeHtmlValidator.
 * Tests validation of HTML content with allowed and disallowed tags.
 */
class SafeHtmlValidatorTest {

    private SafeHtmlValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SafeHtmlValidator();
    }

    @Test
    void shouldAllowNullAndEmptyValues() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void shouldAllowPlainText() {
        assertTrue(validator.isValid("Plain text without HTML", null));
        assertTrue(validator.isValid("Text with special chars: @#$%^&*()", null));
    }

    @Test
    void shouldAllowSafeTags() {
        assertTrue(validator.isValid("<b>Bold text</b>", null));
        assertTrue(validator.isValid("<i>Italic text</i>", null));
        assertTrue(validator.isValid("<u>Underlined text</u>", null));
        assertTrue(validator.isValid("<strong>Strong text</strong>", null));
        assertTrue(validator.isValid("<em>Emphasized text</em>", null));
        assertTrue(validator.isValid("<a href=\"http://example.com\">Link</a>", null));
        assertTrue(validator.isValid("<img src=\"image.jpg\" alt=\"Image\">", null));
    }

    @Test
    void shouldRejectUnsafeTags() {
        assertFalse(validator.isValid("<script>alert('xss')</script>", null));
        assertFalse(validator.isValid("<div>Content</div>", null));
        assertFalse(validator.isValid("<span>Content</span>", null));
        assertFalse(validator.isValid("<p>Paragraph</p>", null));
        assertFalse(validator.isValid("<h1>Header</h1>", null));
        assertFalse(validator.isValid("<iframe src=\"evil.com\"></iframe>", null));
    }

    @Test
    void shouldRejectMixedSafeAndUnsafeTags() {
        String html = "<b>Safe</b> <script>unsafe</script>";
        assertFalse(validator.isValid(html, null));
    }
}