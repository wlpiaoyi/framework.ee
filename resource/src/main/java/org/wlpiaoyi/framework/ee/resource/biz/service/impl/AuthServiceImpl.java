package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.resource.biz.service.IAuthService;

@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {
    @Override
    public boolean authAdmin(String token) {
        return false;
    }

    @Override
    public boolean authUser(String token) {
        return false;
    }
}
