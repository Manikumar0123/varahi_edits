package com.varahiedits.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.varahiedits.model.Images;
import com.varahiedits.repository.ImageRepository;
@Service
public class ImageService {
	
	
	private final ImageRepository imageRepository;
	
    
    public ImageService(ImageRepository imageRepository) {
		super();
		this.imageRepository = imageRepository;
	}


	public Images saveImage(MultipartFile file) throws Exception {

        Images image = Images.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(file.getBytes())
                .build();

        return imageRepository.save(image);
    }

    
    public Images getImage(Integer id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }
}
