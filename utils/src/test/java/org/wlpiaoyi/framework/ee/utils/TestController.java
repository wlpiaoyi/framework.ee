package org.wlpiaoyi.framework.ee.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.wlpiaoyi.framework.ee.utils.filter.idempotence.Idempotence;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.web.response.R;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
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

    @Data
    public static class TestParams implements Serializable {
        private int varInt;
        private String varStr;
        private Date varDate;
        private LocalDateTime varLDateTime;

    }

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
    public R<String> get2(@RequestParam String varStr, @RequestParam Date varDate, @RequestHeader String token) {
        System.out.println("token:" + token);
        return R.success("我返回的无参数Get数据,v1=" + varStr + ", v2=" + varDate);
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
    public R<TestParams> detail2(@RequestBody TestParams body, @RequestHeader String token) {
        System.out.println("body:" + GsonBuilder.gsonDefault().toJson(body, TestParams.class));
        System.out.println("token:" + token);
        body.setVarDate(new Date());
        body.setVarLDateTime(LocalDateTime.now());
        return R.success(body);
    }
}
