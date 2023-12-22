package org.wlpiaoyi.framework.ee.file.manager.biz.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileMenuService;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo.FileMenuVo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.ro.FileMenuRo;
import org.springframework.web.bind.annotation.*;
import org.wlpiaoyi.framework.ee.utils.request.Condition;
import org.wlpiaoyi.framework.ee.utils.response.R;
import org.wlpiaoyi.framework.ee.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.utils.ValueUtils;


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
	@Operation(summary = "文件目录 详情")
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
	@Operation(summary = "文件目录 分页")
	public R<IPage<FileMenuVo>> list(@RequestBody FileMenuRo.Query body) {
		LambdaQueryWrapper<FileMenu> wrapper = Wrappers.<FileMenu>lambdaQuery();
		if(ValueUtils.isNotBlank(body.getName())){
			wrapper.like(FileMenu::getName, body.getName());
		}
		if(ValueUtils.isNotBlank(body.getSuffix())){
			wrapper.eq(FileMenu::getSuffix, body.getSuffix());
		}
		IPage<FileMenu> pages = fileMenuService.page(Condition.getPage(body), wrapper);
		return R.success(ModelWrapper.parseForPage(pages, FileMenuVo.class));
	}

	/**
	 * 文件目录 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@Operation(summary = "文件目录 修改")
	public R<Boolean> update(@Validated @RequestBody FileMenuRo.Submit body) {
		return R.success(fileMenuService.updateById(ModelWrapper.parseOne(body, FileMenu.class)));
	}

	/**
	 * 文件目录 删除
	 */
	@GetMapping("/remove")
	@ApiOperationSupport(order = 7)
	@Operation(summary = "文件目录 逻辑删除")
	public R remove(@Parameter(description = "主键集合", required = true) @RequestParam String ids) {
		return R.success(fileMenuService.deleteLogic(ValueUtils.toLongList(ids)));
	}

}
