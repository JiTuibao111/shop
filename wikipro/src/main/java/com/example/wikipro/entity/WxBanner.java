package com.example.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName wx_banner
 */
@TableName(value ="wx_banner")
@Data
public class WxBanner implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer bannerId;

    /**
     * 
     */
    private String imgurl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}