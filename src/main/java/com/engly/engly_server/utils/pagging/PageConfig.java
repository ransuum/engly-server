package com.engly.engly_server.utils.pagging;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageConfig<T> {

    public Map<String, Object> response(Page<T> page, Class<T> type) {
        Map<String, Object> result = new HashMap<>();
        List<T> list = page.getContent();

        result.put(type.getSimpleName()
                .toLowerCase().replace("dto", ""), list);
        result.put("totalElements", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("current_page", page.getNumber());

        return result;
    }
}
