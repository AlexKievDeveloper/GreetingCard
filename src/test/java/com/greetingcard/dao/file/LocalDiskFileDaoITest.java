package com.greetingcard.dao.file;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalDiskFileDaoITest {
    @Mock
    private Part part;
    @Mock
    private InputStream inputStream;
    @InjectMocks
    private LocalDiskFileDao localDiskFileDao;

    @AfterEach
    public void removeTestFile(){
        File file = new File("test-file");
        assertTrue(file.delete());
    }
    @Test
    @DisplayName("Saving files to storages")
    void saveFileInStorage() throws IOException {
        //prepare
        File file = new File("test-file");
        when(part.getInputStream()).thenReturn(inputStream);
        when(inputStream.read(any())).thenReturn(1).thenReturn(-1);

        //when
        localDiskFileDao.saveFileInStorage(part, file);

        //then
        verify(part).getInputStream();
        verify(inputStream, times(2)).read(any());
    }
}