package org.wlpiaoyi.framework.ee.file.manager.biz.controller;



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
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.ro.FileInfoRo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo.FileInfoVo;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileInfoService;
import org.springframework.web.bind.annotation.*;
import org.wlpiaoyi.framework.ee.file.manager.config.FileConfig;
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

	/**
	 * 文件信息 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@Operation(summary = "文件信息 详情")
	public R<FileInfoVo> detail(FileInfoRo.Query body) {
		FileInfoVo fileMenu = ModelWrapper.parseOne(
				this.fileDataService.getOne(
						Condition.getQueryWrapper(ModelWrapper.parseOne(body, FileInfo.class))
				),
				FileInfoVo.class
		);
		return R.success(fileMenu);

	}

	/**
	 * 文件信息 分页
	 */
	@PostMapping("/list")
	@ApiOperationSupport(order = 2)
	@Operation(summary = "文件信息 分页")
	public R<IPage<FileInfoVo>> list(@RequestBody FileInfoRo.Query body) {
		LambdaQueryWrapper<FileInfo> wrapper = Wrappers.<FileInfo>lambdaQuery();
		if(ValueUtils.isNotBlank(body.getName())){
			wrapper.like(FileInfo::getName, body.getName());
		}
		if(ValueUtils.isNotBlank(body.getSuffix())){
			wrapper.eq(FileInfo::getSuffix, body.getSuffix());
		}
		IPage<FileInfo> pages = fileDataService.page(Condition.getPage(body), wrapper);
		return R.success(ModelWrapper.parseForPage(pages, FileInfoVo.class));
	}

	/**
	 * 文件信息 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@Operation(summary = "文件信息 修改")
	public R<Boolean> update(@Validated @RequestBody FileInfoRo.Submit body) {
		return R.success(fileDataService.updateById(ModelWrapper.parseOne(body, FileInfo.class)));
	}

	@Autowired
	private FileConfig fileConfig;
	/**
	 * 文件信息 删除
	 */
	@SneakyThrows
	@GetMapping("/remove")
	@ApiOperationSupport(order = 7)
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
