package com.defectmanager.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.vo.JsonVO;
import com.defectmanager.entity.Defect;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.DefectService;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/defects")
public class DefectController {

    @Resource
    private DefectService defectService;

    @PostMapping("/page")
    @ApiOperation("分页查询缺陷信息")
    public JsonVO<Page<Defect>> query(@RequestBody DefectQuery query) {
        return JsonVO.success(defectService.queryByCondition(query));
    }

    @PostMapping("/add")
    @ApiOperation("添加缺陷信息")
    public JsonVO<Defect> add(@RequestBody Defect defect) {
        Defect defect1 = defectService.add(defect);
        return JsonVO.success(defect1);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除缺陷")
    public JsonVO<Boolean> delete(@PathVariable Long id) {
        return JsonVO.success(defectService.deleteDefect(id));
    }



}
