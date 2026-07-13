package com.m4zek.backend.model.dto.read;

import com.m4zek.backend.model.FileType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {

    private Long id;
    private String filename;
    private String imageUrl;
    private String key;
    private FileType type;
}
