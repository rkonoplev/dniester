package com.example.phoebe.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentProcessingServiceTest {

    private final ContentProcessingService service = new ContentProcessingService();

    @Test
    void testProcessContentWithYouTubeLink() {
        String content = "Посмотрите это видео: https://www.youtube.com/watch?v=Q_eG_S-oJ6w\n" +
                        "Также есть короткая ссылка: https://youtu.be/ABC123def45";
        
        String result = service.processContent(content);
        
        // Проверяем что YouTube ссылки преобразованы в embed код
        assertTrue(result.contains("iframe"));
        assertTrue(result.contains("youtube.com/embed/Q_eG_S-oJ6w"));
        assertTrue(result.contains("youtube.com/embed/ABC123def45"));
        
        // Проверяем адаптивность (responsive design)
        assertTrue(result.contains("position: relative"));
        assertTrue(result.contains("padding-bottom: 56.25%"));
        assertTrue(result.contains("width: 100%"));
        assertTrue(result.contains("height: 100%"));
    }

    @Test
    void testProcessContentWithHtml() {
        String content = "<p>Текст с <b>жирным</b> и <i>курсивом</i></p>";
        String result = service.processContent(content);
        assertEquals(content, result); // HTML остается без изменений
    }
}