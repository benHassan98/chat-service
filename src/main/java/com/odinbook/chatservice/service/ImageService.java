package com.odinbook.chatservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    public void createBlobs(String dir, MultipartFile[] imageList) throws RuntimeException;
    public String injectImagesToHTML(String html,MultipartFile [] imageList);
}
