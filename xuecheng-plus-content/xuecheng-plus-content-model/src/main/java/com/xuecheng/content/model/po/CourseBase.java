package com.xuecheng.content.model.po;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 课程基本信息
 * @TableName course_base
 */
@ApiModel(value = "课程基本信息",description = "课程基本信息")
@Data
public class CourseBase implements Serializable {
    /**
     * 主键
     */
    @TableId
    @ApiModelProperty(value ="主键",required = true )
    private Long id;

    /**
     * 机构ID
     */
    @ApiModelProperty(value ="机构ID",required = true )
    private Long companyId;

    /**
     * 机构名称
     */
    @ApiModelProperty(value ="机构名称",required = true )
    private String companyName;

    @ApiModelProperty("课程名称")
    /**
     * 课程名称
     */
    private String name;
    @ApiModelProperty("适用人群")
    /**
     * 适用人群
     */
    private String users;

    @ApiModelProperty("课程标签")
    /**
     * 课程标签
     */
    private String tags;
    @ApiModelProperty("大分类")
    /**
     * 大分类
     */
    private String mt;
    @ApiModelProperty("小分类")
    /**
     * 小分类
     */
    private String st;
    @ApiModelProperty("课程等级")
    /**
     * 课程等级
     */
    private String grade;
    @ApiModelProperty("教育模式(common普通，record 录播，live直播等）")
    /**
     * 教育模式(common普通，record 录播，live直播等）
     */
    private String teachmode;
    @ApiModelProperty("课程介绍")
    /**
     * 课程介绍
     */
    private String description;
    @ApiModelProperty("课程图片")
    /**
     * 课程图片
     */
    private String pic;
    @ApiModelProperty("创建时间")
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    @ApiModelProperty("修改时间")
    /**
     * 修改时间
     */
    private LocalDateTime changeDate;
    @ApiModelProperty("创建人")
    /**
     * 创建人
     */
    private String createPeople;
    @ApiModelProperty("更新人")
    /**
     * 更新人
     */
    private String changePeople;
    @ApiModelProperty("审核状态")
    /**
     * 审核状态
     */
    private String auditStatus;
    @ApiModelProperty("课程发布状态 未发布  已发布 下线")
    /**
     * 课程发布状态 未发布  已发布 下线
     */
    private String status;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CourseBase other = (CourseBase) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCompanyId() == null ? other.getCompanyId() == null : this.getCompanyId().equals(other.getCompanyId()))
            && (this.getCompanyName() == null ? other.getCompanyName() == null : this.getCompanyName().equals(other.getCompanyName()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getUsers() == null ? other.getUsers() == null : this.getUsers().equals(other.getUsers()))
            && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
            && (this.getMt() == null ? other.getMt() == null : this.getMt().equals(other.getMt()))
            && (this.getSt() == null ? other.getSt() == null : this.getSt().equals(other.getSt()))
            && (this.getGrade() == null ? other.getGrade() == null : this.getGrade().equals(other.getGrade()))
            && (this.getTeachmode() == null ? other.getTeachmode() == null : this.getTeachmode().equals(other.getTeachmode()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getPic() == null ? other.getPic() == null : this.getPic().equals(other.getPic()))
            && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
            && (this.getChangeDate() == null ? other.getChangeDate() == null : this.getChangeDate().equals(other.getChangeDate()))
            && (this.getCreatePeople() == null ? other.getCreatePeople() == null : this.getCreatePeople().equals(other.getCreatePeople()))
            && (this.getChangePeople() == null ? other.getChangePeople() == null : this.getChangePeople().equals(other.getChangePeople()))
            && (this.getAuditStatus() == null ? other.getAuditStatus() == null : this.getAuditStatus().equals(other.getAuditStatus()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCompanyId() == null) ? 0 : getCompanyId().hashCode());
        result = prime * result + ((getCompanyName() == null) ? 0 : getCompanyName().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getUsers() == null) ? 0 : getUsers().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getMt() == null) ? 0 : getMt().hashCode());
        result = prime * result + ((getSt() == null) ? 0 : getSt().hashCode());
        result = prime * result + ((getGrade() == null) ? 0 : getGrade().hashCode());
        result = prime * result + ((getTeachmode() == null) ? 0 : getTeachmode().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getPic() == null) ? 0 : getPic().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getChangeDate() == null) ? 0 : getChangeDate().hashCode());
        result = prime * result + ((getCreatePeople() == null) ? 0 : getCreatePeople().hashCode());
        result = prime * result + ((getChangePeople() == null) ? 0 : getChangePeople().hashCode());
        result = prime * result + ((getAuditStatus() == null) ? 0 : getAuditStatus().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", companyId=").append(companyId);
        sb.append(", companyName=").append(companyName);
        sb.append(", name=").append(name);
        sb.append(", users=").append(users);
        sb.append(", tags=").append(tags);
        sb.append(", mt=").append(mt);
        sb.append(", st=").append(st);
        sb.append(", grade=").append(grade);
        sb.append(", teachmode=").append(teachmode);
        sb.append(", description=").append(description);
        sb.append(", pic=").append(pic);
        sb.append(", createDate=").append(createDate);
        sb.append(", changeDate=").append(changeDate);
        sb.append(", createPeople=").append(createPeople);
        sb.append(", changePeople=").append(changePeople);
        sb.append(", auditStatus=").append(auditStatus);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}