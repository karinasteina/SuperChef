package lv.superchef.app.controller;

import lv.superchef.app.service.IImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageUploadController
{

    @Autowired
    private IImageStorageService imageStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file)
    {
        String imageUrl = imageStorageService.storeCoverImage(file);

        // Returns {"url": "/images/recipes/your-uuid.jpg"}
        return ResponseEntity.ok(Map.of("url", imageUrl));
    }
}