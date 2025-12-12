package com.example.wikipro.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/*** @TableName wx_user */
@TableName(value ="wx_user")
@Data
public class WxUser implements Serializable {
    private Integer userId;
    private String username;
    private String password;
    private String userImg;
    private String flag;
    private static final long serialVersionUID = 1L;
}
