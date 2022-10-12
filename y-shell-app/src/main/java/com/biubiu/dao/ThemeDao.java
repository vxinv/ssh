package com.biubiu.dao;

import com.biubiu.pojo.Ecs;
import com.biubiu.pojo.Theme;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ThemeDao {
    @Insert({
            "update theme ",
            "set",
            "fontsize = #{fontsize},",
            "hearttime = #{hearttime}",
            "where id = 1"
    })
    int update(Theme theme);

    @Select({
            "select *",
                "from theme",
            "where id = 1 limit 1"
    })
    Theme selectTheme();
}
