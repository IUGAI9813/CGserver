package com.example.cgserver.domain.common.service;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final JdbcTemplate jdbcTemplate;

    private final Map<String, String> codeCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
       refreshCache();
    }

    public void refreshCache(){
        String sql = "SELECT GROUP_CODE, CODE, CODE_NAME FROM TB_COMMON_CODE WHERE IS_ACTIVE = 'Y'";

        try {

            jdbcTemplate.query(sql, rs -> {
                String groupCode = rs.getString("GROUP_CODE");
                String code = rs.getString("CODE");
                String codeName = rs.getString("CODE_NAME");


                codeCache.put(groupCode + ":" + code , codeName);
            });
            log.info("================ COMMON CODES CACHE DUMP ================");
            codeCache.forEach((key, value) -> log.info("CACHE KEY: '{}' => VALUE: '{}'", key, value));
            log.info("TOTAL ITEMS IN CACHE: {}", codeCache.size());


        }catch (Exception e) {
            log.error("Failed to load code from database", e);
        }
    }

    public String getCache(String groupCode , String code) {
        if (code == null) return  null;
        return codeCache.getOrDefault(groupCode + ":" + code, code);
    }

}
