package ca.sheridancollege.yoojiw.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {


    public String saveImage(MultipartFile image) throws IOException {
        // 이미지를 서버에 저장하고 파일 경로 또는 URL을 반환
        String imageUrl = "/images/" + image.getOriginalFilename();
        byte[] imageBytes = image.getBytes();
        
        Path imagePath = Paths.get("src/main/resources/static" + imageUrl);
        Files.write(imagePath, imageBytes);
        
        return imageUrl;
    }
}




