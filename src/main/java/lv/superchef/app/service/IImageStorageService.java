package lv.superchef.app.service;

import org.springframework.web.multipart.MultipartFile;

public interface IImageStorageService
{
    String storeCoverImage(MultipartFile coverImage);
}