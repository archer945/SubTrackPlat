package com.defectmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.defectmanager.entity.Defect;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DefectMapper extends BaseMapper<Defect>{


}