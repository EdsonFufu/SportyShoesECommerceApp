package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Image;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.paging.Paged;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.paging.Paging;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ImageService {
    @Autowired private ImageRepository imageRepository;
    public Paged<Image> getPage(int pageNumber, int size) {
        PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Image> postPage = imageRepository.findAll(request);
        return new Paged<>(postPage, Paging.of(postPage.getTotalPages(), pageNumber, size));
    }
}
