package com.backend.api.photo.service;

import com.backend.api.photo.dto.PhotoInfoResponseDto;
import com.backend.domain.photo.entity.Photo;
import com.backend.domain.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PhotoInfoService {

    private final PhotoRepository photoRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<List<PhotoInfoResponseDto>> searchAllDesc() {
        List<Photo> photos = photoRepository.findAllByOrderByCreatedAtDesc();

        List<PhotoInfoResponseDto> photoInfoResponseDtos = photos.stream()
                .map(PhotoInfoResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(photoInfoResponseDtos);
    }

    // 삭제
    public void delete(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

            photoRepository.delete(photo);
    }
}
