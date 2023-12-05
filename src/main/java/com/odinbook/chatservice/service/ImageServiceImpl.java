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
    public void createBlobs(String[] idList, MultipartFile[] fileList) throws RuntimeException, IOException {
        if(Objects.isNull(idList))
            return;
        if(idList.length == 0)
            return;

        for(int i =0;i<idList.length;i++){


            new BlobServiceClientBuilder()
                    .connectionString(connectStr)
                    .buildClient()
                    .getBlobContainerClient("images")
                    .getBlobClient(idList[i])
                    .upload(fileList[i].getInputStream());


        }



    }

    @Override
    public String injectImagesToHTML(String html, List<String> imageNameList) {
        AtomicInteger index = new AtomicInteger();

        Document document =  Jsoup.parse(html);
        document
                .select("img")
                .replaceAll(element ->
                        element.attributes().hasKey("is-new")?
                                element.attr("src", imageNameList.get(index.getAndIncrement()))
                                        .removeAttr("is-new"):
                                element.removeAttr("is-new")
                );

        return document.body().html();
    }

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
