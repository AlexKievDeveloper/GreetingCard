package com.greetingcard.service;

import org.springframework.web.multipart.MultipartFile;

public interface AmazonService {

    void uploadFile(MultipartFile multipartFile, String fileName);

    void deleteFileFromS3Bucket(String fileUrl);
}
