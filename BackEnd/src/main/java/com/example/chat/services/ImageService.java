package com.example.chat.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.chat.exception.CustomException;
import com.example.chat.models.LocalUser;

@Service
public class ImageService {

    private CloudinaryService cloudinaryService;

    public ImageService(CloudinaryService cloudinaryService) {

        this.cloudinaryService = cloudinaryService;
    }

    @SuppressWarnings("rawtypes")
    public LocalUser uploadImage(MultipartFile image, LocalUser user) throws IOException {
        if (image == null) {
            throw new CustomException("Image is required", HttpStatus.BAD_REQUEST);
        }
        try {
            BufferedImage bi = ImageIO.read(image.getInputStream());
            if (bi == null) {
                throw new CustomException("Invalid image file", HttpStatus.BAD_REQUEST);
            }
            Map result = cloudinaryService.upload(image, user.getName() + user.getId());
            if (user.getImageId() != null)
                cloudinaryService.delete(user.getImageId());

            user.setImageId(result.get("public_id").toString());
            user.setImageUrl(result.get("url").toString());

            return user;

        } catch (CustomException e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return user;
        }
    }

}
