package com.quality.service;
import com.github.pagehelper.PageInfo;
import com.quality.common.vo.ReqExportVO;
import com.quality.entity.ReqRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ReqRequirementService {
    int add(ReqRequirement reqRequirement);
    ReqRequirement getById(Long id);
    PageInfo<ReqRequirement> pageList(Integer pageNum, Integer pageSize, String region, String status, String taskName);
    int update(ReqRequirement reqRequirement);
    int deleteById(Long id);
    int batchInsert(List<ReqRequirement> list);
    List<Map<String, Object>> statGroupByRegion();
    List<Map<String, Object>> statMonthChart(Integer year);
    void exportData(String region, String status, String taskName, HttpServletResponse response);
    Map<String,Object> statTotal();

    List<ReqRequirement> selectRecent(Integer pageSize);
    List<ReqExportVO> getExportList(String region, String status, String taskName);
    List<ReqRequirement> readExcel(MultipartFile file, String createUser);

    List<Map<String, Object>> statGroupDimension(String startDate, String endDate, String region, String conclusion, String groupType);

    List<String> selectAllRegion();
}