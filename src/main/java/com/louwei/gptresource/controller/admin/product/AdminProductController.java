package com.louwei.gptresource.controller.admin.product;

import cn.hutool.core.collection.CollUtil;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.louwei.gptresource.common.AjaxResult;
import com.louwei.gptresource.service.ChatOrdersService;
import com.louwei.gptresource.service.ChatProductService;
import com.louwei.gptresource.vo.ListQueryVo;
import com.louwei.gptresource.vo.TempVo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/vue-element-admin/product")
@Slf4j
public class AdminProductController {

    @Autowired
    private ChatProductService chatProductService;
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Value("${base.url}")
    private String baseUrl;

    /**
     * 后台管理查询商品
     * @param listQueryVo
     * @return
     */
    @GetMapping("/list")
    public AjaxResult listProductPage(@ModelAttribute ListQueryVo listQueryVo){
        System.out.println("开始执行后台管理查询商品数据模块:" + listQueryVo.getPage());
        AjaxResult ajaxResult = chatProductService.selectProductPage(listQueryVo);
        return ajaxResult;
    }

    /**
     * 后台管理更新商品
     * @param tempVo
     * @return
     */
    @PostMapping("/update")
    public AjaxResult updateProductPage(@RequestBody @NonNull TempVo tempVo){
        System.out.println("开始执行后台管理修改商品数据模块:" + tempVo.getCreator());
        int result = chatProductService.updateProductPage(tempVo);
        return AjaxResult.toAjax(result);
    }

    /**
     * 后台创建商品
     * @param tempVo
     * @return
     */
    @PostMapping("/create")
    public AjaxResult createProductPage(@RequestBody @NonNull TempVo tempVo){
        System.out.println("开始执行后台管理创建商品数据模块:" + tempVo.getCreator());
        int result = chatProductService.createProductPage(tempVo);
        return AjaxResult.toAjax(result);
    }

    /**
     * 商品主图上传
     * @return
     */
    @PostMapping("/uploadProductImg")
    public AjaxResult uploadProductImg(MultipartFile file){
        log.info("开始接受商品主图:" + file.getOriginalFilename());
        if(file != null && file.getSize() != 0){
            //获取文件名
            String filename = file.getOriginalFilename();
            String suffix = filename.substring(filename.lastIndexOf("."));
            StorePath storePath = null;
            try {
                storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String filePath = storePath.getFullPath();
            return AjaxResult.success(baseUrl + filePath);
        }
        return AjaxResult.fail("图片上传失败");
    }

    /**
     * 商品详情图上传
     * @return
     */
    @PostMapping("/uploadProductDescImg")
    public AjaxResult uploadProductDesImg(MultipartFile[] files){
        log.info("开始接受商品主图:" + files.length);
        if(CollUtil.isNotEmpty(Arrays.asList(files)) && files.length != 0){
            //获取后缀名
            List<String> filePaths = new ArrayList<>();
            for (MultipartFile file:files) {
                String filename = file.getOriginalFilename();
                String suffix = filename.substring(filename.lastIndexOf("."));
                StorePath storePath = null;
                try {
                    storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String filePath = storePath.getFullPath();
                filePaths.add(baseUrl + filePath);
            }
            return AjaxResult.success(filePaths);
        }
        return AjaxResult.fail("图片上传失败");
    }
}
