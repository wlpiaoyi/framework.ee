package org.wlpiaoyi.framework.ee.utils;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.wlpiaoyi.framework.ee.utils.filter.idempotence.Idempotence;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.web.response.R;

import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/15 22:19
 * {@code @version:}:       1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("test/kk")
public class TestController {

    /**
     * 详情
     */
    @GetMapping("testGet1")
    public R<String> get1() {
        return R.success("我返回的无参数Get数据");
    }

    /**
     * 详情
     */
    @Idempotence
    @GetMapping("testGet2")
    public R<String> get2(@RequestParam String v1, @RequestHeader String token) {
        System.out.println("token:" + token);
        return R.success("我返回的无参数Get数据,v1=" + v1);
    }
    /**
     * 详情
     */
    @PostMapping("testPost1")
    public R<Map> detail(@RequestBody Map body) {
        return R.success(body);
    }
    /**
     * 详情
     */
    @Idempotence
    @PostMapping("testPost2")
    public R<Map> detail2(@RequestBody Map body, @RequestHeader String token) {
        System.out.println("body:" + GsonBuilder.gsonDefault().toJson(body, Map.class));
        System.out.println("token:" + token);
        return R.success(body);
    }
}
