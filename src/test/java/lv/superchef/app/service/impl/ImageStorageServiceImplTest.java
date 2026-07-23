package lv.superchef.app.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ImageStorageServiceImpl Unit Tests")
class ImageStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private ImageStorageServiceImpl imageStorageService;

    @BeforeEach
    void setUp()
    {
        imageStorageService = new ImageStorageServiceImpl(tempDir.toString());
    }

    @Test
    @DisplayName("Should return default image URL when cover image is null")
    void shouldReturnDefaultUrlWhenImageIsNull()
    {
        String result = imageStorageService.storeCoverImage(null);

        assertThat(result).isEqualTo("/images/recipes/recipe-00.webp");
    }

    @Test
    @DisplayName("Should return default image URL when cover image is empty")
    void shouldReturnDefaultUrlWhenImageIsEmpty()
    {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "coverImage",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        String result = imageStorageService.storeCoverImage(emptyFile);

        assertThat(result).isEqualTo("/images/recipes/recipe-00.webp");
    }

    @Test
    @DisplayName("Should save valid image and return web path with preserved extension")
    void shouldSaveImageAndReturnPathWithOriginalExtension()
    {
        MockMultipartFile validFile = new MockMultipartFile(
                "coverImage",
                "delicious-food.png",
                "image/png",
                "fake image content".getBytes()
        );

        String result = imageStorageService.storeCoverImage(validFile);

        assertThat(result)
                .startsWith("/images/recipes/")
                .endsWith(".png");

        String generatedFileName = result.replace("/images/recipes/", "");
        Path targetFile = tempDir.resolve(generatedFileName);

        assertThat(Files.exists(targetFile)).isTrue();
    }

    @Test
    @DisplayName("Should fallback to .jpg extension when filename has no extension or is null")
    void shouldFallbackToJpgExtensionWhenNoExtensionProvided()
    {
        MockMultipartFile fileWithoutExtension = new MockMultipartFile(
                "coverImage",
                "raw-image-file",
                "image/jpeg",
                "fake image content".getBytes()
        );

        String result = imageStorageService.storeCoverImage(fileWithoutExtension);

        assertThat(result).endsWith(".jpg");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when file input stream throws IOException")
    void shouldThrowIllegalStateExceptionOnIOException() throws Exception
    {
        MultipartFile faultyFile = mock(MultipartFile.class);
        when(faultyFile.isEmpty()).thenReturn(false);
        when(faultyFile.getOriginalFilename()).thenReturn("test.jpg");
        when(faultyFile.getInputStream()).thenThrow(new IOException("Disk write error"));

        assertThatThrownBy(() -> imageStorageService.storeCoverImage(faultyFile))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Could not store recipe cover image")
                .hasCauseInstanceOf(IOException.class);
    }
}