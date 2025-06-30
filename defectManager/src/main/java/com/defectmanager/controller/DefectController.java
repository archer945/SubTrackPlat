package com.defectmanager.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.vo.JsonVO;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.entity.Defect;
import com.defectmanager.entity.DefectImage;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.DefectService;
import com.defectmanager.service.ImageService;
import com.defectmanager.service.exportService;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/defects")
public class DefectController {

    @Resource
    private DefectService defectService;
    @Resource
    private ImageService imageService;
    @Resource
    private exportService exportService;

    @PostMapping("/page")
    @ApiOperation("分页查询缺陷信息")
    public JsonVO<Page<Defect>> query(@RequestBody DefectQuery query) {
        Page<Defect> result = defectService.queryByCondition(query);
        return JsonVO.success(result);
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
        if (!defectService.deleteDefect(id)) {
            return JsonVO.fail(null);
        }
        return JsonVO.success(defectService.deleteDefect(id));
    }

    @PutMapping("/{id}/status")
    @ApiOperation("更新缺陷状态")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("X-User-Id") Long operatorId) { // 从请求头获取操作人

        try {
            // 将字符串状态转换为枚举
            DefectStatusEnum statusEnum = DefectStatusEnum.fromDbValue(status);

            boolean success = defectService.updateStatus(id, statusEnum, operatorId);
            return success ?
                    ResponseEntity.ok("状态更新成功") :
                    ResponseEntity.badRequest().body("更新失败");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("无效的状态值: " + status);
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

    @GetMapping("/export")
    @ApiOperation("导出缺陷数据")
    public void exportDefects(DefectQuery query, HttpServletResponse response) {
        try {
            // 1. 设置响应头
            String fileName = "缺陷数据_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 2. 生成Excel文件
            ByteArrayOutputStream outputStream = exportService.exportDefectsToExcel(query,response);

            // 3. 写入响应流
            response.getOutputStream().write(outputStream.toByteArray());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("导出失败", e);
        }
    }



}

