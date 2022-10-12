package com.biubiu.controller;

import com.biubiu.core.annotation.SystemLog;
import com.biubiu.dao.CommandDao;
import com.biubiu.pojo.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "command", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CommandController {

    private final static Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    private CommandDao commandDao;

    @SystemLog(description = "获取所有的指令")
    @GetMapping(value = "get_command")
    public ResponseEntity<?> getCommands() {

        List<Command> commands = commandDao.selectCommand();
        return new ResponseEntity<>(commands, HttpStatus.OK);
    }

    @SystemLog(description = "更改指令-必传id")
    @PostMapping(value = "update_command")
    public ResponseEntity<?> updateCommand(@RequestBody Command command) {
        int count = commandDao.updateCommand(command);
        return new ResponseEntity<>(count,HttpStatus.OK);
    }

    @SystemLog(description = "删除指令-必传id")
    @GetMapping(value = "remove_command")
    public ResponseEntity<?> removeCommand(@RequestParam("id") String id) {
        int count = commandDao.removeCommand(id);
        return new ResponseEntity<>(count,HttpStatus.OK);
    }

    @SystemLog(description = "增加指令-必传id")
    @PostMapping(value = "add_command")
    public ResponseEntity<?> addCommand(@RequestBody Command command) {
        return new ResponseEntity<>(commandDao.insertCommand(command),HttpStatus.OK);
    }


}
