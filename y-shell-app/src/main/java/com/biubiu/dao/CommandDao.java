package com.biubiu.dao;

import com.biubiu.pojo.Command;
import com.biubiu.pojo.Ecs;
import com.biubiu.pojo.Theme;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommandDao {


    @Insert({
            "insert into command",
            "(id, name, content, has_delete)",
            "values(#{id} ,#{name} , #{content},'0')"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public Integer insertCommand(Command command);


    public int updateCommand(Command command);

    public int removeCommand(String id);

    public List<Command> selectCommand();


}
