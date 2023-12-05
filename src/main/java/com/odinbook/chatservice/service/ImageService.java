package com.odinbook.chatservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    public void createBlobs(String[] idList, MultipartFile[] fileList) throws RuntimeException, IOException;
    public String injectImagesToHTML(String html, List<String> imageNameList);
    public void deleteImages(String content);
}
