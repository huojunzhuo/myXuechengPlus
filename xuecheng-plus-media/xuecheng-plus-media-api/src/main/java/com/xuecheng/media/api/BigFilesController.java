package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * ClassName: BigFilesController
 * Package: com.xuecheng.media.api
 * Description: BigFilesController
 *
 * @Author huojz
 * @Create 2023/10/25 20:27
 * @Version 1.0
 */

@Api(value = "大文件上传管理接口", tags = "大文件上传管理接口")
@RestController
public class BigFilesController {

    @Autowired
    MediaFileService mediaFileService;
    @ApiOperation(value = "文件上传前根据md5值检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5){
        return mediaFileService.checkFile(fileMd5);
    }
    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.checkChunk(fileMd5,chunk);
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        //服务器创建本地临时File文件
        File tempFile = File.createTempFile("minio", ".temp");
        //接收上传参数转换至服务器本地的File文件
        file.transferTo(tempFile);
        //获取分块文件本地路径
        String absolutePath = tempFile.getAbsolutePath();
        return mediaFileService.uploadChunk(fileMd5, chunk, absolutePath);
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);

        return mediaFileService.mergechunks(companyId, fileMd5, chunkTotal, uploadFileParamsDto);

    }


}
