package com.defectmanager.controller;

import com.common.domain.vo.JsonVO;
import com.defectmanager.entity.DefectImage;
import com.defectmanager.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/defectimage")
public class DefectImageController {
    @Autowired
    private ImageService imageService;

    /*
    * 上传单个图片
    * */
    @PostMapping(value = "/{defectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JsonVO<DefectImage> uploadImage(
            @PathVariable Long defectId,
            @RequestPart MultipartFile file) throws IOException {
        return JsonVO.success(imageService.uploadImage(defectId, file));
    }

    /*
    * 上传多个图片
    * */
    @PostMapping(value = "/batch/{defectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JsonVO<List<DefectImage>> uploadImages(
            @PathVariable Long defectId,
            @RequestPart MultipartFile[] files) throws IOException {
        return JsonVO.success(imageService.batchUploadImages(defectId, List.of(files)));
    }

//    @GetMapping("/{defectId}")
//    public JsonVO<List<DefectImage>> getImages(@PathVariable Long defectId) {
//        return JsonVO.success(imageService.getImagesByDefectId(defectId));
//    }



}
