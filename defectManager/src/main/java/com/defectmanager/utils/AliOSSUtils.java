package com.defectmanager.utils;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云 OSS 工具类
 */
@Component
public class AliOSSUtils {

    private String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
    private String accessKeyId = System.getenv("ALI_ACCESS_KEY_ID");
    private String accessKeySecret = System.getenv("ALI_ACCESS_KEY_SECRET");
    private String bucketName = "liumeilin-web-tlias";

    /**
     * 实现上传图片到OSS
     */
    public String upload(MultipartFile file) throws IOException {
        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        // 避免文件覆盖
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        //文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;
        // 关闭ossClient
        ossClient.shutdown();
        return url;// 把上传到oss的路径返回
    }
    /**
     * 从OSS删除文件
     * @param fileUrl 文件的完整URL或对象键（如 "abc.jpg" 或 "https://bucket.endpoint/abc.jpg"）
     * @return 是否删除成功
     */
    public boolean delete(String fileUrl) {
        OSS ossClient = null;
        try {
            // 1. 创建OSS客户端
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 2. 提取对象键（处理URL和直接对象键两种情况）
            String objectKey;
            if (fileUrl.startsWith("http")) {
                // 如果是完整URL，提取路径部分（去掉协议和域名）
                String[] parts = fileUrl.split("/");
                objectKey = parts[parts.length - 1];
            } else {
                // 直接使用传入的对象键
                objectKey = fileUrl;
            }

            // 3. 执行删除
            ossClient.deleteObject(bucketName, objectKey);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // 4. 关闭客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}
