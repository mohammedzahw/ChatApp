package com.example.chat.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
@SuppressWarnings("rawtypes")
public class CloudinaryService {
    Cloudinary cloudinary;

    public CloudinaryService() {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cloud_name", "dcvrdin4a");
        valuesMap.put("api_key", "412872152882384");
        valuesMap.put("api_secret", "Jv_O6IoGQhF6FwPhUw47xol5fFg");
        cloudinary = new Cloudinary(valuesMap);
    }

    /**
     * @throws Exception
     ****************************************************************************************************************/

    /******************************************************************************************************************/

    public Map upload(MultipartFile file, String folder) throws IOException {
        byte[] fileBytes = file.getBytes();

        try {

            Map result = cloudinary.uploader().upload(fileBytes,
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", "chat/" + folder,
                            // "tags", "chat",
                            "invalidate", true // Optional: Invalidate the cached resources
                    ));

            // System.out.println(result);
            return result;
        } catch (IOException exception) {
            // throw new CustomException(exception.getMessage(), HttpStatus.BAD_REQUEST);
            System.out.println(exception.getMessage());
            return null;
        }

    }

    /******************************************************************************************************************/

    public Map delete(String id) throws IOException {
        return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
    }

    /******************************************************************************************************************/

    public Map deleteByFolder(String folder) throws Exception {
        // return cloudinary.api().delete(tag, ObjectUtils.emptyMap());
        // return cloudinary.api().deleteFolder(tag, ObjectUtils.emptyMap());
        cloudinary.api().deleteAllResources(
                ObjectUtils.asMap(
                        "type", "upload",
                        "prefix", "chat/" + folder,
                        "resource_type", "image"));
        cloudinary.api().deleteAllResources(
                ObjectUtils.asMap(
                        "type", "upload",
                        "prefix", "chat/" + folder,
                        "resource_type", "video"));
        cloudinary.api().deleteAllResources(
                ObjectUtils.asMap(
                        "type", "upload",
                        "prefix", "chat/" + folder,
                        "resource_type", "raw"));
        cloudinary.api().deleteFolder(folder, ObjectUtils.emptyMap());
        return null;
    }

    /******************************************************************************************************************/

}
