package org.wlpiaoyi.framework.ee.file.manager.biz.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileMenuService;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo.FileMenuVo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.ro.FileMenuRo;
import org.wlpiaoyi.framework.ee.file.manager.utils.tools.Condition;
import org.wlpiaoyi.framework.ee.file.manager.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.ee.file.manager.utils.tools.response.R;
import org.springframework.web.bind.annotation.*;
import org.wlpiaoyi.framework.utils.ValueUtils;

import javax.validation.Valid;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件目录 控制器
 * {@code @date:} 			2023-12-08 16:48:27
 * {@code @version:}: 		1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/file_menu")
@Tag(name = "文件目录接口")
public class FileMenuController {

	private final IFileMenuService fileMenuService;

	/**
	 * 文件目录 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@Operation(summary = "FileMenu 详情")
	public R<FileMenuVo> detail(FileMenuRo.Query body) {
		FileMenuVo fileMenu = ModelWrapper.parseOne(
				this.fileMenuService.getOne(
						Condition.getQueryWrapper(ModelWrapper.parseOne(body, FileMenu.class))
				),
				FileMenuVo.class
		);
		return R.success(fileMenu);

	}

	/**
	 * 文件目录 分页
	 */
	@PostMapping("/list")
	@ApiOperationSupport(order = 2)
	@Operation(summary = "FileMenu 分页")
	public R<IPage<FileMenuVo>> list(@RequestBody FileMenuRo.Query body) {
		LambdaQueryWrapper<FileMenu> wrapper = Wrappers.<FileMenu>lambdaQuery();
		IPage<FileMenu> pages = fileMenuService.page(Condition.getPage(body), wrapper);
		return R.success(ModelWrapper.parseForPage(pages, FileMenuVo.class));
	}

	/**
	 * 文件目录 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@Operation(summary = "FileMenu 新增")
	public R<Boolean> save(@Valid @RequestBody FileMenuRo.Submit body) {
		return R.success(fileMenuService.save(ModelWrapper.parseOne(body, FileMenu.class)));
	}

	/**
	 * 文件目录 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@Operation(summary = "FileMenu 修改")
	public R<Boolean> update(@RequestBody FileMenuRo.Submit body) {
		return R.success(fileMenuService.updateById(ModelWrapper.parseOne(body, FileMenu.class)));
	}

	/**
	 * 文件目录 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@Operation(summary = "FileMenu 新增或修改")
	public R<Boolean> submit(@Valid @RequestBody FileMenuRo.Submit body) {
		return R.success(fileMenuService.saveOrUpdate(ModelWrapper.parseOne(body, FileMenu.class)));
	}

	/**
	 * 文件目录 删除
	 */
	@GetMapping("/remove")
	@ApiOperationSupport(order = 7)
	@Operation(summary = "FileMenu 逻辑删除")
	public R remove(@Parameter(description = "主键集合", required = true) @RequestParam String ids) {
		return R.success(fileMenuService.deleteLogic(ValueUtils.toLongList(ids)));
	}

}
