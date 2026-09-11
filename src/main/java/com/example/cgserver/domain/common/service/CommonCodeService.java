package com.example.cgserver.domain.common.service;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final JdbcTemplate jdbcTemplate;

    private volatile Map<String, String> codeCache =  Collections.emptyMap();

    @PostConstruct
    public void init(){
       refreshCache();
    }

    public synchronized void refreshCache(){
        String sql = "SELECT GROUP_CODE, CODE, CODE_NAME FROM TB_COMMON_CODE WHERE IS_ACTIVE = 'Y'";

        try {

            Map<String, String> newCache = new HashMap<>();
 
            jdbcTemplate.query(sql, rs -> {
                String groupCode = rs.getString("GROUP_CODE");
                String code = rs.getString("CODE");
                String codeName = rs.getString("CODE_NAME");
            
                if (groupCode != null && code != null){
                      newCache.put(groupCode + ":" + code , codeName);
                }

            });
            
          
            this.codeCache = Collections.unmodifiableMap(newCache);
            
            log.info("================ COMMON CODES CACHE DUMP ================");
            codeCache.forEach((key, value) -> log.info("CACHE KEY: '{}' => VALUE: '{}'", key, value));
            log.info("TOTAL ITEMS IN CACHE: {}", codeCache.size());


        }catch (Exception e) {
            log.error("Failed to load code from database", e);
        }
    }

    public String getCache(String groupCode , String code) {
        if (code == null) return  null;
        return codeCache.get(groupCode + ":" + code);
    }

}
