package com.biubiu.controller;


import com.biubiu.core.annotation.SystemLog;
import com.biubiu.dao.ThemeDao;
import com.biubiu.dto.Node;
import com.biubiu.pojo.Ecs;
import com.biubiu.pojo.Theme;
import com.biubiu.service.EcsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "ecs", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ThemeController {

    private final static Logger logger = LoggerFactory.getLogger(EcsController.class);

    @Autowired
    private ThemeDao themeDao;

    @SystemLog(description = "获取主题")
    @GetMapping(value = "theme")
    public ResponseEntity<?> getTheme() {

        Theme theme = themeDao.selectTheme();
        return new ResponseEntity<>(theme,HttpStatus.OK);
    }

    @SystemLog(description = "设置主题")
    @PostMapping(value = "settheme")
    public ResponseEntity<?> setTheme(@RequestBody Theme theme) {
        int count = themeDao.update(theme);
        return new ResponseEntity<>(theme,HttpStatus.OK);
    }

}
