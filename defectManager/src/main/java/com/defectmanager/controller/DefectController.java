package com.defectmanager.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.vo.JsonVO;
import com.defectmanager.entity.Defect;
import com.defectmanager.entity.DefectImage;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.DefectService;
import com.defectmanager.service.ImageService;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/defects")
public class DefectController {

    @Resource
    private DefectService defectService;
    @Resource
    private ImageService imageService;

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

    @PutMapping("/{id}/status")
    @ApiOperation("更新缺陷状态")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("X-User-Id") Long operatorId) { // 从请求头获取操作人

        try {
            boolean success = defectService.updateStatus(id, status, operatorId);
            return success ?
                    ResponseEntity.ok("状态更新成功") :
                    ResponseEntity.badRequest().body("更新失败");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取缺陷详细信息")
    public JsonVO<Defect> getDefectDetail(@PathVariable Long id) {
        // 1. 获取缺陷基本信息
        Defect defect = defectService.getById(id);
        if (defect == null) {
            return JsonVO.fail(null);
        }

        // 2. 获取关联的图片列表
        List<DefectImage> images = imageService.getImagesByDefectId(id);
        defect.setImages(images);

        return JsonVO.success(defect);
    }


}

