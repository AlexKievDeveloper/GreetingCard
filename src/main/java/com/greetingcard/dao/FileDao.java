package com.greetingcard.dao;

import javax.servlet.http.Part;
import java.io.File;

public interface FileDao {
    void saveFileInStorage(Part part, File file);
}
