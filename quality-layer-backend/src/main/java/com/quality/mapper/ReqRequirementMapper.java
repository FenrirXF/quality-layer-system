package com.quality.mapper;
import com.quality.entity.ReqRequirement;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ReqRequirementMapper {
    int insert(ReqRequirement req);
    ReqRequirement selectById(Long id);
    List<ReqRequirement> selectList(@Param("region") String region,
                                    @Param("status") String status,
                                    @Param("taskName") String taskName);
    int update(ReqRequirement req);
    int deleteById(Long id);

    List<Map<String, Object>> selectStatByRegion();

    Map<String,Object> selectStatTotal();

    List<Map<String, Object>> selectStatByMonth(@Param("year") Integer year);

    List<ReqRequirement> selectAllForExport(@Param("region") String region,
                                            @Param("status") String status,
                                            @Param("taskName") String taskName);

    List<ReqRequirement> selectRecent(@Param("limit") int limit);

    List<ReqRequirement> selectExportList(
            @Param("region") String region,
            @Param("status") String status,
            @Param("taskName") String taskName
    );

    List<Map<String, Object>> statDimension(String startDate, String endDate, String region, String conclusion, String groupField);

    List<String> selectAllRegion();
}