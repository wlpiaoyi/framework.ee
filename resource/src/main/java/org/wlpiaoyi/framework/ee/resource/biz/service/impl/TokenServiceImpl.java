package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import org.wlpiaoyi.framework.ee.resource.biz.service.ITokenService;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.Token;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.TokenMapper;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.TokenVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.ro.TokenRo;
import org.wlpiaoyi.framework.ee.resource.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.security.RsaCipher;


/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	令牌 服务类实现
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
@Primary
@Service
public class TokenServiceImpl extends BaseServiceImpl<TokenMapper, Token> implements ITokenService {

    private static TokenVo ADMIN_TEMP_TOKEN = null;

    @Override
    public Token createAdminTempToken() {
//        RsaCipher rsaCipher= RsaCipher.build(0).loadRandomKey().loadConfig();
//        To
//        ADMIN_TEMP_TOKEN = TokenVo.builder().
//                .token(StringUtils.getUUID64())
//                .publicKey(rsaCipher.getPublicKey())
//                .privateKey(rsaCipher.getPrivateKey())
//                .build();
        return ADMIN_TEMP_TOKEN;
    }

    @Override
    public Token getAdminTempToken() {
        return ADMIN_TEMP_TOKEN;
    }

    @Override
    public void clearAdminTempToken() {
        ADMIN_TEMP_TOKEN = null;
    }
}
