package com.cahnggou.file.controller;

import com.cahnggou.file.utils.FastDFSClient;
import com.cahnggou.file.utils.FastDFSFile;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zps on 2020/1/8 23:27
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        try {
            //判断文件是否存在
            if (file == null) {
                new RuntimeException("文件不存在");
            }
            //获取文件名称
            String originalFilename = file.getOriginalFilename();
            //判断文件名是否为空
            if (StringUtils.isEmpty(originalFilename)) {
                new RuntimeException("文件名不存在");
            }
            //获取文件内容
            byte[] bytes = file.getBytes();
            //获取文件拓展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //封装对象
            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename, bytes, extension);
            //上传文件
            String[] upload = FastDFSClient.upload(fastDFSFile);
            //设置返回信息
            String url = upload[0] + "/" + upload[1];
            return new Result(true, StatusCode.OK, "文件上传成功", url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, StatusCode.ERROR, "上传文件失败");


    }
}
