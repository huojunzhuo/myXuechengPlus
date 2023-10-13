package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName: CourseBaseInfoDto
 * Package: com.xuecheng.content.model.dto
 * Description:课程基本信息
 *
 * @Author huojz
 * @Create 2023/10/12 19:33
 * @Version 1.0
 */
@Data
public class CourseBaseInfoDto extends CourseBase {
    /**
     * 收费规则，对应数据字典
     */
    @ApiModelProperty(value = "收费规则")
    private String charge;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格")
    private Float price;


    /**
     * 原价
     */
    @ApiModelProperty(value = "原价")
    private Float originalPrice;

    /**
     * 咨询qq
     */
    @ApiModelProperty(value = "咨询qq")
    private String qq;

    /**
     * 微信
     */
    @ApiModelProperty(value = "微信")
    private String wechat;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * 有效期天数
     */
    @ApiModelProperty(value = "有效期天数")
    private Integer validDays;

    /**
     * 大分类名称
     */
    @ApiModelProperty(value = "大分类名称")
    private String mtName;

    /**
     * 小分类名称
     */
    @ApiModelProperty(value = "小分类名称")
    private String stName;
}
