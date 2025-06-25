package com.defectmanager.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.defectmanager.entity.DefectImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DefectImageMapper extends BaseMapper<DefectImage> {

    // 根据缺陷ID查询图片列表
    @Select("SELECT * FROM defect_image WHERE defect_id = #{defectId} ORDER BY uploaded_at DESC")
    List<DefectImage> selectByDefectId(@Param("defectId") Long defectId);
}
