package com.quality.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ReqRequirementServiceImpl implements ReqRequirementService {

    @Autowired
    private ReqRequirementMapper reqRequirementMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 清理所有首页统计缓存（新增/修改/删除需求后调用）
    private void clearStatsCache() {
        stringRedisTemplate.delete("stat:total");
        stringRedisTemplate.delete("stat:region");
        // month / recent 的 key 带参数，用通配符批量删
        Set<String> monthKeys = stringRedisTemplate.keys("stat:month:*");
        if (monthKeys != null && !monthKeys.isEmpty()) {
            stringRedisTemplate.delete(monthKeys);
        }
        Set<String> recentKeys = stringRedisTemplate.keys("stat:recent:*");
        if (recentKeys != null && !recentKeys.isEmpty()) {
            stringRedisTemplate.delete(recentKeys);
        }
    }

    @Override
    public int add(ReqRequirement reqRequirement) {
        LocalDateTime now = LocalDateTime.now();
        reqRequirement.setCreateTime(now);
        reqRequirement.setUpdateTime(now);
        int rows = reqRequirementMapper.insert(reqRequirement);

        // ===== 新增：首页统计缓存全部失效 =====
        clearStatsCache();
        return rows;
    }

    @Override
    public ReqRequirement getById(Long id) {
        String key = "requirement:detail:" + id;

        // ① 先查缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            // 空字符串 = 之前查过数据库没有这条（防穿透的空值标记），直接返回 null
            if (json.isEmpty()) {
                return null;
            }
            return JSONUtil.toBean(json, ReqRequirement.class);   // JSON → 实体
        }

        // ② 缓存未命中 → 查数据库
        ReqRequirement req = reqRequirementMapper.selectById(id);

        // ③ 回填缓存：有数据存10分钟；没数据存空串2分钟（防止频繁查不存在的id，即缓存穿透）
        if (req != null) {
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(req), 10, TimeUnit.MINUTES);
        } else {
            stringRedisTemplate.opsForValue().set(key, "", 2, TimeUnit.MINUTES);
        }
        return req;
    }

    @Override
    public PageInfo<ReqRequirement> pageList(Integer pageNum, Integer pageSize, String region, String status, String taskName) {
        PageHelper.startPage(pageNum, pageSize);
        List<ReqRequirement> list = reqRequirementMapper.selectList(region, status, taskName);
        return new PageInfo<>(list);
    }

    @Override
    public int update(ReqRequirement reqRequirement) {
        reqRequirement.setUpdateTime(LocalDateTime.now());
        int rows = reqRequirementMapper.update(reqRequirement);

        // ===== 新增：删这条需求的详情缓存 =====
        if (reqRequirement.getId() != null) {
            stringRedisTemplate.delete("requirement:detail:" + reqRequirement.getId());
        }
        // ===== 新增：首页统计缓存失效 =====
        clearStatsCache();
        return rows;
    }

    @Override
    public int deleteById(Long id) {
        int rows = reqRequirementMapper.deleteById(id);

        // ===== 新增 =====
        stringRedisTemplate.delete("requirement:detail:" + id);
        clearStatsCache();
        return rows;
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

        // ===== 新增 =====
        clearStatsCache();
        return total;
    }

    @Override
    public List<Map<String, Object>> statGroupByRegion() {
        String key = "stat:region";

        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> data = (List<Map<String, Object>>) (List<?>) JSONUtil.toList(json, Map.class);
            return data;
        }

        List<Map<String, Object>> data = reqRequirementMapper.selectStatByRegion();   // 原有逻辑

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(data), 5, TimeUnit.MINUTES);
        return data;
    }

    @Override
    public List<Map<String, Object>> statMonthChart(Integer year) {
        if (year == null || year <= 0) {
            year = java.time.Year.now().getValue();
        }
        String key = "stat:month:" + year;   // 不同年份互不干扰

        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> monthStat = (List<Map<String, Object>>) (List<?>) JSONUtil.toList(json, Map.class);
            return monthStat;
        }

        List<Map<String, Object>> monthStat = reqRequirementMapper.selectStatByMonth(year);

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(monthStat), 5, TimeUnit.MINUTES);
        return monthStat;
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
        String key = "stat:total";

        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            return JSONUtil.toBean(json, Map.class);
        }

        Map<String, Object> map = reqRequirementMapper.selectStatTotal();   // 你原有逻辑

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(map), 5, TimeUnit.MINUTES);
        return map;
    }

    @Override
    public List<ReqRequirement> selectRecent(Integer pageSize) {
        String key = "stat:recent:" + pageSize;

        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            return JSONUtil.toList(json, ReqRequirement.class);
        }

        List<ReqRequirement> list = reqRequirementMapper.selectRecent(pageSize);   // 原有逻辑

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(list), 5, TimeUnit.MINUTES);
        return list;
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
