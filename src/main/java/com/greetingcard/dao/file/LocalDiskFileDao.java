package com.greetingcard.dao.file;

import com.greetingcard.dao.FileDao;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Part;
import java.io.*;

@Slf4j
public class LocalDiskFileDao implements FileDao {

    public void saveFileInStorage(Part part, File file) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
             InputStream inputStream = part.getInputStream()) {

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
