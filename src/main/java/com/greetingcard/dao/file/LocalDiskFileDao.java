package com.greetingcard.dao.file;

import com.greetingcard.dao.FileDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
public class LocalDiskFileDao implements FileDao {

    public void saveFileInStorage(MultipartFile multipartFile, File file) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
             InputStream inputStream = multipartFile.getInputStream()) {

            byte[] buffer = new byte[8192];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, count);
            }
        } catch (IOException e) {
            log.error("Error while saving file", e);
            throw new RuntimeException("Error while saving file", e);
        }
    }
}
