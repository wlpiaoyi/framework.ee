package org.wlpiaoyi.framework.ee.resource.biz.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.ro.FileInfoRo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.FileInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.ImageInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.VideoInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IFileInfoService;
import org.springframework.web.bind.annotation.*;
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.IVideoInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.impl.file.FileImageHandle;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.utils.request.Condition;
import org.wlpiaoyi.framework.ee.utils.response.R;
import org.wlpiaoyi.framework.ee.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件信息 控制器
 * {@code @date:} 			2023-12-08 16:48:27
 * {@code @version:}: 		1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/file_info")
@Tag(name = "文件信息接口")
public class FileInfoController {

	private final IFileInfoService fileDataService;

	private final IImageInfoService imageInfoService;

	private final IVideoInfoService videoInfoService;

	/**
	 * 文件信息 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 10)
	@Operation(summary = "文件信息 修改")
	public R<Boolean> update(@Validated @RequestBody FileInfoRo.Submit body) {
		return R.success(fileDataService.updateById(ModelWrapper.parseOne(body, FileInfo.class)));
	}


	/**
	 * 文件信息 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 20)
	@Operation(summary = "文件信息 详情")
	public R<FileInfo> detail(@RequestParam Long id) {
		FileInfo fileMenu = this.fileDataService.getById(id);
		return R.success(fileMenu);
	}

	/**
	 * 视频信息 详情
	 */
	@GetMapping("/video_detail")
	@ApiOperationSupport(order = 21)
	@Operation(summary = "视频信息 详情")
	public R<VideoInfoVo> videoDetail(@RequestParam Long fileId) {
		VideoInfoVo videoInfoVo = this.videoInfoService.detailByFileId(fileId);
		return R.success(videoInfoVo);
	}


	/**
	 * 图片信息 详情
	 */
	@GetMapping("/image_detail")
	@ApiOperationSupport(order = 21)
	@Operation(summary = "图片信息 详情")
	public R<ImageInfoVo> imageDetail(@RequestParam Long fileId) {
		ImageInfoVo imageInfoVo = this.imageInfoService.detailByFileId(fileId);
		return R.success(imageInfoVo);
	}

	/**
	 * 文件信息 分页
	 */
	@PostMapping("/list")
	@ApiOperationSupport(order = 30)
	@Operation(summary = "文件信息 分页")
	public R<IPage<FileInfoVo>> list(@RequestBody FileInfoRo.Query body) {
		LambdaQueryWrapper<FileInfo> wrapper = Wrappers.<FileInfo>lambdaQuery();
		if(ValueUtils.isNotBlank(body.getName())){
			wrapper.like(FileInfo::getName, body.getName());
		}
		if(ValueUtils.isNotBlank(body.getSuffix())){
			wrapper.eq(FileInfo::getSuffix, body.getSuffix());
		}
		wrapper.orderByDesc(FileInfo::getCreateTime);
		IPage<FileInfo> pages = fileDataService.page(Condition.getPage(body), wrapper);
		return R.success(ModelWrapper.parseForPage(pages, FileInfoVo.class));
	}

	@Autowired
	private FileConfig fileConfig;
	/**
	 * 文件信息 删除
	 */
	@SneakyThrows
	@GetMapping("/remove")
	@ApiOperationSupport(order = 40)
	@Operation(summary = "文件信息 逻辑删除")
	public R remove(@Parameter(description = "token集合", required = true) @RequestParam String tokens) {
		List<String> tokenList = ValueUtils.toStringList(tokens);
		List<Long> ids = new ArrayList<>();
		for (String token : tokenList){
			ids.add(new Long(new String(this.fileConfig.getAesCipher().decrypt(this.fileConfig.dataDecode(token)))));
		}
		return R.success(fileDataService.deleteLogic(ids));
	}

}
