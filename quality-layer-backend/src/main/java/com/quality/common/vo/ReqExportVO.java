package com.quality.common.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReqExportVO {

    @ExcelProperty("区域")
    @ColumnWidth(12)
    private String region;

    @ExcelProperty("技术经理")
    @ColumnWidth(12)
    private String techManager;

    @ExcelProperty("研发负责人")
    @ColumnWidth(14)
    private String devManager;

    @ExcelProperty("研发组长")
    @ColumnWidth(12)
    private String devLeader;

    @ExcelProperty("测试组长")
    @ColumnWidth(12)
    private String testLeader;

    @ExcelProperty("项目需求/任务名称")
    @ColumnWidth(26)
    private String taskName;

    @ExcelProperty("触发评估项")
    @ColumnWidth(24)
    private String triggerItem;

    @ExcelProperty("评估结论")
    @ColumnWidth(16)
    private String conclusion;

    @ExcelProperty("上线日期")
    @DateTimeFormat("yyyy-MM-dd")
    @ColumnWidth(14)
    private LocalDate deadline;

    @ExcelProperty("状态")
    @ColumnWidth(12)
    private String status;

    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String remark;
}