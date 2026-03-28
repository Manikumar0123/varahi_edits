package com.varahiedits.controller;


import com.varahiedits.model.Images;
import com.varahiedits.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // 🔥 Upload Image
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        Images saved = imageService.saveImage(file);
        return "Image uploaded with ID: " + saved.getId();
    }

    // 🔥 Get Image
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {

        Images image = imageService.getImage(id);

        return ResponseEntity.ok()
        		.contentType(MediaType.parseMediaType(image.getType()))
                .body(image.getImageData());
    }
}