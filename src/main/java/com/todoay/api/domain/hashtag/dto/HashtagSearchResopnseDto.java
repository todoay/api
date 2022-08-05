package com.todoay.api.domain.hashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class HashtagSearchResopnseDto {


    private boolean hasNext;
    private int nextPageNum;
    private List<HashtagInfoDto> infos;
}
