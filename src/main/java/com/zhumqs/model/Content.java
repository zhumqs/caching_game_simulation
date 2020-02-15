package com.zhumqs.model;

import lombok.Data;

import java.util.List;

/**
 * @author mingqi zhu
 * @date 20191201
 */
@Data
public class Content {
    private Integer contentId;
    private List<Integer> themeList;
    private Integer size;
}
