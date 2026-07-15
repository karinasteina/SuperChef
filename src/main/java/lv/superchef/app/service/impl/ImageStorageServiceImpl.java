package lv.superchef.app.service.impl;

import lv.superchef.app.service.IImageStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl implements IImageStorageService
{
    private static final String DEFAULT_IMAGE_URL = "/images/recipes/recipe-00.webp";

    @Override
    public String storeCoverImage(MultipartFile coverImage)
    {
        if (coverImage == null || coverImage.isEmpty())
        {
            return DEFAULT_IMAGE_URL;
        }

        String extension = getExtension(coverImage.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;

        Path targetDir = Paths.get("src/main/resources/static/images/recipes").toAbsolutePath().normalize();
        Path targetFile = targetDir.resolve(fileName).normalize();

        try {
            Files.createDirectories(targetDir);
            Files.copy(coverImage.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not store recipe cover image", ex);
        }

        return "/images/recipes/" + fileName;
    }

    private String getExtension(String filename)
    {
        if (filename == null || !filename.contains("."))
        {
            return ".jpg"; // fallback extension
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
