package lv.superchef.app.service;

import org.springframework.stereotype.Service;

@Service
public class TextService {
    public ShortenedText shorten(String text, int length) {
        {
            if (text.length() <= length) {
                return new ShortenedText(text, false);
            }
            return new ShortenedText(text.substring(0, length) + "...", true);
        }
    }
}
