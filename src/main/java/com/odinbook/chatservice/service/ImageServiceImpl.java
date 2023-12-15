package com.odinbook.chatservice.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ImageServiceImpl implements ImageService{
    @Value("${spring.cloud.azure.storage.connection-string}")
    private String connectStr;
    @Override
    public void deleteImages(String content) {

        BlobContainerClient blobContainerClient = new BlobServiceClientBuilder()
                .connectionString(connectStr)
                .buildClient()
                .getBlobContainerClient("images");

        Jsoup
                .parse(content)
                .select("img")
                .forEach(image->{
                    blobContainerClient
                            .getBlobClient(image.attr("src"))
                            .deleteIfExists();
                });
    }
}
