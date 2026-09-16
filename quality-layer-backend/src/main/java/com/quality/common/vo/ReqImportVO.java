package com.quality.common.vo;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReqImportVO {
    @ExcelProperty("区域")
    private String region;
    @ExcelProperty("技术经理")
    private String techManager;
    @ExcelProperty("研发负责人")
    private String devManager;
    @ExcelProperty("研发组长")
    private String devLeader;
    @ExcelProperty("测试组长")
    private String testLeader;
    @ExcelProperty("项目需求/任务名称")
    private String taskName;
    @ExcelProperty("触发评估项")
    private String triggerItem;
    @ExcelProperty("评估结论")
    private String conclusion;
    @ExcelProperty("上线日期")
    private LocalDate deadline;
    @ExcelProperty("状态")
    private String status;
    @ExcelProperty("备注")
    private String remark;
}