package lv.superchef.app.service.impl;

import lv.superchef.app.service.ShortenedText;
import lv.superchef.app.service.TextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TextService Unit Tests")
class TextServiceTest
{

    private TextService textService;

    @BeforeEach
    void setUp()
    {
        textService = new TextService();
    }

    @Test
    @DisplayName("Should return original text and shortened=false when text length is strictly less than target length")
    void shouldReturnOriginalTextWhenShorterThanLength()
    {
        String input = "Hello";
        int targetLength = 10;

        ShortenedText result = textService.shorten(input, targetLength);

        assertThat(result.text()).isEqualTo("Hello");
        assertThat(result.isShortened()).isFalse();
    }

    @Test
    @DisplayName("Should return original text and shortened=false when text length equals target length exactly")
    void shouldReturnOriginalTextWhenLengthIsEqual()
    {
        String input = "Hello";
        int targetLength = 5;

        ShortenedText result = textService.shorten(input, targetLength);

        assertThat(result.text()).isEqualTo("Hello");
        assertThat(result.isShortened()).isFalse();
    }

    @Test
    @DisplayName("Should shorten text and add ellipsis with shortened=true when text exceeds target length")
    void shouldShortenTextWhenLongerThanLength()
    {
        String input = "Hello World";
        int targetLength = 5;

        ShortenedText result = textService.shorten(input, targetLength);

        assertThat(result.text()).isEqualTo("Hello...");
        assertThat(result.isShortened()).isTrue();
    }

    @ParameterizedTest(name = "Input: \"{0}\", Length: {1} -> Expected Text: \"{2}\", Shortened: {3}")
    @DisplayName("Parameterized test for various text shortening boundary scenarios")
    @CsvSource({
            "'', 5, '', false",
            "'A', 1, 'A', false",
            "'Java', 0, '...', true",
            "'SuperChef', 5, 'Super...', true"
    })
    void testShortenBoundaries(String input, int targetLength, String expectedText, boolean expectedTruncated)
    {
        ShortenedText result = textService.shorten(input, targetLength);

        assertThat(result.text()).isEqualTo(expectedText);
        assertThat(result.isShortened()).isEqualTo(expectedTruncated);
    }
}