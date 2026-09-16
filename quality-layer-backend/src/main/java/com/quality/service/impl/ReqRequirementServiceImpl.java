package com.quality.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quality.common.vo.ReqExportVO;
import com.quality.common.vo.ReqImportVO;
import com.quality.entity.ReqRequirement;
import com.quality.mapper.ReqRequirementMapper;
import com.quality.service.ReqRequirementService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReqRequirementServiceImpl implements ReqRequirementService {

    @Autowired
    private ReqRequirementMapper reqRequirementMapper;

    @Override
    public int add(ReqRequirement reqRequirement) {
        LocalDateTime now = LocalDateTime.now();
        reqRequirement.setCreateTime(now);
        reqRequirement.setUpdateTime(now);
        return reqRequirementMapper.insert(reqRequirement);
    }

    @Override
    public ReqRequirement getById(Long id) {
        return reqRequirementMapper.selectById(id);
    }

    @Override
    public PageInfo<ReqRequirement> pageList(Integer pageNum, Integer pageSize, String region, String status, String taskName) {
        PageHelper.startPage(pageNum, pageSize);
        List<ReqRequirement> list = reqRequirementMapper.selectList(region, status, taskName);
        return new PageInfo<>(list);
    }

    @Override
    public int update(ReqRequirement reqRequirement) {
        // 修改只更新修改时间
        reqRequirement.setUpdateTime(LocalDateTime.now());
        return reqRequirementMapper.update(reqRequirement);
    }

    @Override
    public int deleteById(Long id) {
        return reqRequirementMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<ReqRequirement> list) {
        int total = 0;
        LocalDateTime now = LocalDateTime.now();
        for (ReqRequirement item : list) {
            item.setCreateTime(now);
            item.setUpdateTime(now);
            total += reqRequirementMapper.insert(item);
        }
        return total;
    }

    @Override
    public List<Map<String, Object>> statGroupByRegion() {
        return reqRequirementMapper.selectStatByRegion();
    }

    @Override
    public List<Map<String, Object>> statMonthChart(Integer year) {
        if (year == null || year <= 0) {
            year = java.time.Year.now().getValue();
        }
        return reqRequirementMapper.selectStatByMonth(year);
    }

    @Override
    public void exportData(String region, String status, String taskName, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = URLEncoder.encode("需求列表", StandardCharsets.UTF_8).replace("+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            List<ReqExportVO> dataList = getExportList(region, status, taskName);
            //注册自动列宽处理器
            EasyExcel.write(response.getOutputStream(), ReqExportVO.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("需求数据")
                    .doWrite(dataList);
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    @Override
    public Map<String, Object> statTotal() {
        return reqRequirementMapper.selectStatTotal();
    }

    @Override
    public List<ReqRequirement> selectRecent(Integer pageSize) {
        return reqRequirementMapper.selectRecent(pageSize);
    }

    @Override
    public List<ReqExportVO> getExportList(String region, String status, String taskName) {
        List<ReqRequirement> entityList = reqRequirementMapper.selectExportList(region, status, taskName);
        return entityList.stream().map(entity -> {
            ReqExportVO vo = new ReqExportVO();
            vo.setRegion(entity.getRegion());
            vo.setTechManager(entity.getTechManager());
            vo.setDevManager(entity.getDevManager());
            vo.setDevLeader(entity.getDevLeader());
            vo.setTestLeader(entity.getTestLeader());
            vo.setTaskName(entity.getTaskName());
            vo.setTriggerItem(entity.getTriggerItem());
            vo.setConclusion(entity.getConclusion());
            vo.setDeadline(entity.getDeadline());
            vo.setStatus(entity.getStatus());
            vo.setRemark(entity.getRemark());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ReqRequirement> readExcel(MultipartFile file, String createUser) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        try {
            List<ReqImportVO> importVOList = EasyExcel.read(file.getInputStream())
                    .head(ReqImportVO.class)
                    .sheet()
                    .doReadSync();

            List<ReqRequirement> result = new ArrayList<>();
            for (ReqImportVO vo : importVOList) {
                ReqRequirement entity = new ReqRequirement();
                BeanUtils.copyProperties(vo, entity);
                entity.setCreateUser(createUser);
                entity.setUpdateTime(LocalDateTime.now());
                result.add(entity);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("解析Excel发生IO异常", e);
        }
    }

    @Override
    public List<Map<String, Object>> statGroupDimension(String startDate, String endDate, String region, String conclusion, String groupType) {
        // 白名单防止SQL注入，只允许预设字段
        String groupField = switch (groupType){
            case "region" -> "region";
            case "techManager" -> "tech_manager";
            case "devManager" -> "dev_manager";
            case "devLeader" -> "dev_leader";
            case "testLeader" -> "test_leader";
            case "conclusion" -> "conclusion";
            default -> "region";
        };
        return reqRequirementMapper.statDimension(startDate, endDate, region, conclusion, groupField);
    }

    @Override
    public List<String> selectAllRegion() {
        return reqRequirementMapper.selectAllRegion();
    }
}
