package com.quality.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.quality.common.result.Result;
import com.quality.common.vo.ReqExportVO;
import com.quality.entity.ReqRequirement;
import com.quality.entity.SysUser;
import com.quality.service.ReqRequirementService;
import com.quality.service.SysOperLogService;
import com.quality.util.OperLogUtil;
import com.quality.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"/requirement","/api/requirement"})
public class ReqRequirementController {
    @Autowired
    private ReqRequirementService reqRequirementService;

    @Autowired
    private SysOperLogService sysOperLogService;

    // 分页查询需求
    @GetMapping("/page")
    public Result<PageInfo<ReqRequirement>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String region, String status, String taskName
    ) {
        PageInfo<ReqRequirement> page = reqRequirementService.pageList(pageNum, pageSize, region, status, taskName);
        return Result.success(page);
    }

    // 单条详情
    @GetMapping("/{id}")
    public Result<ReqRequirement> getById(@PathVariable Long id) {
        return Result.success(reqRequirementService.getById(id));
    }

    // 新增需求
    @PostMapping("/add")
    public Result<Integer> add(@RequestBody ReqRequirement req, HttpServletRequest request) {
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        // 核心修复：赋值创建人，解决 create_user null报错
        req.setCreateUser(loginUser.getUsername());
        int rows = reqRequirementService.add(req);

        // 记录操作日志
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "需求管理", "新增", "新增需求：" + req.getTaskName());
        return Result.success(rows);
    }

    // 修改需求
    @PutMapping("/update")
    public Result<Integer> update(@RequestBody ReqRequirement req, HttpServletRequest request) {
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        // 更新人赋值
        req.setUpdateUser(loginUser.getUsername());
        int rows = reqRequirementService.update(req);

        // 记录操作日志
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "需求管理", "编辑", "编辑需求：" + req.getTaskName());
        return Result.success(rows);
    }

    // 删除需求
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id, HttpServletRequest request) {
        int rows = reqRequirementService.deleteById(id);
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "需求管理", "删除", "删除需求ID：" + id);
        return Result.success(rows);
    }

    // 批量导入需求
    @PostMapping("/batchAdd")
    public Result<Integer> batchAdd(@RequestBody List<ReqRequirement> list, HttpServletRequest request) {
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        String currentUsername = loginUser.getUsername();
        // 循环给每条设置创建人
        for (ReqRequirement item : list) {
            item.setCreateUser(currentUsername);
        }
        int rows = reqRequirementService.batchInsert(list);
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "需求管理", "新增", "批量导入需求，共" + list.size() + "条");
        return Result.success(rows);
    }

    // Excel文件批量导入（专门接收上传文件）
    @PostMapping("/batchImport")
    public Result<Integer> batchImport(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        String currentUsername = loginUser.getUsername();
        // 调用service，使用EasyExcel读取file，转换成 List<ReqRequirement>
        List<ReqRequirement> list = reqRequirementService.readExcel(file, currentUsername);
        int rows = reqRequirementService.batchInsert(list);
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "需求管理", "新增", "批量导入需求，共" + list.size() + "条");
        return Result.success(rows);
    }

    //批量导出需求
    @GetMapping("/export")
    public void exportExcel(
            String region, String taskName, String conclusion,
            Boolean isTemplate,
            HttpServletResponse response
    ) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode("需求列表", StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<ReqExportVO> dataList;
        if(Boolean.TRUE.equals(isTemplate)){
            // 模板：空集合，只输出表头
            dataList = new ArrayList<>();
        }else {
            dataList = reqRequirementService.getExportList(region, taskName, conclusion);
        }

        EasyExcel.write(response.getOutputStream(), ReqExportVO.class)
                .sheet("需求数据")
                .doWrite(dataList);
    }

    // 按区域分组统计需求数量
    @GetMapping("/stat/region")
    public Result<List<Map<String, Object>>> statByRegion() {
        List<Map<String, Object>> data = reqRequirementService.statGroupByRegion();
        return Result.success(data);
    }

    // 首页月度大盘统计（折线图）
    @GetMapping("/stat/month")
    public Result<List<Map<String, Object>>> statMonthData(
            @RequestParam(required = false) Integer year
    ) {
        List<Map<String, Object>> monthStat = reqRequirementService.statMonthChart(year);
        return Result.success(monthStat);
    }

    // 首页汇总统计
    @GetMapping("/stat/total")
    public Result<Map<String, Object>> statTotal() {
        Map<String, Object> map = reqRequirementService.statTotal();
        return Result.success(map);
    }

    // 首页最近5条需求
    @GetMapping("/recent")
    public Result<List<ReqRequirement>> getRecent(@RequestParam(defaultValue = "5") Integer pageSize) {
        List<ReqRequirement> list = reqRequirementService.selectRecent(pageSize);
        return Result.success(list);
    }

    @GetMapping("/stat/group")
    public Result<List<Map<String, Object>>> statGroup(
            String startDate,
            String endDate,
            String region,
            String conclusion,
            @RequestParam(defaultValue = "region") String groupType
    ){
        List<Map<String, Object>> list = reqRequirementService.statGroupDimension(startDate,endDate,region,conclusion,groupType);
        return Result.success(list);
    }

    @GetMapping("/region/list")
    public Result<List<String>> getRegionList() {
        List<String> regionList = reqRequirementService.selectAllRegion();
        return Result.success(regionList);
    }
}