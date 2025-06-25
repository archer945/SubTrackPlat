package com.defectmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.defectmanager.entity.Defect;
import com.defectmanager.query.DefectQuery;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DefectMapper extends BaseMapper<Defect>{

    List<Defect> selectByQuery(@Param("query") DefectQuery query);
}