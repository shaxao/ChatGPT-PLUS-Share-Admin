package com.louwei.gptresource.common;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页返回类
 * @param <T>
 */
@Data
public class PageModel<T> implements Serializable {
    private Integer page;

    private Integer size;

    private T data;
}
