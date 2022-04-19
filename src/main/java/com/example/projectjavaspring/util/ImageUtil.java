package com.example.projectjavaspring.util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtil {

  private ImageUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static String uploadImage(MultipartFile multipartFile) throws IOException {
    String path = new File("src/main/resources/static/img/").getAbsolutePath();
    if (multipartFile == null) {
      return null;
    }
    String fileName = multipartFile.getOriginalFilename();
    if (fileName != null && fileName.equals("")) {
      return null;
    }
    String fileNameNew = FilenameUtils.getBaseName(fileName) + "-" + System.nanoTime() + "." +
        FilenameUtils.getExtension(fileName);
    var file = new File(path, fileNameNew);
    multipartFile.transferTo(file);
    return fileNameNew;
  }

}
