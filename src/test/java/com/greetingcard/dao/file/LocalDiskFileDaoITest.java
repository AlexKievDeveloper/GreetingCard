package com.greetingcard.dao.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalDiskFileDaoITest {
    @Mock
    private MultipartFile multipartFile;
    @Mock
    private InputStream inputStream;
    @InjectMocks
    private LocalDiskFileDao localDiskFileDao;

    @Test
    @DisplayName("Saving files to storages")
    void saveFileInStorage() throws IOException {
        //prepare
        File file = new File("test-file");
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(inputStream.read(any())).thenReturn(1).thenReturn(-1);

        //when
        localDiskFileDao.saveFileInStorage(multipartFile, file);

        //then
        verify(multipartFile).getInputStream();
        verify(inputStream, times(2)).read(any());
        assertTrue(file.delete());
    }
}