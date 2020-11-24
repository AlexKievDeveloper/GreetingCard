package com.greetingcard.dao;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileDao {
    void saveFileInStorage(MultipartFile multipartFile, File file);
}
