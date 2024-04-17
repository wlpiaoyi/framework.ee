package org.wlpiaoyi.framework.ee.proxy.socket;

import lombok.SneakyThrows;
import org.wlpiaoyi.framework.ee.proxy.ProxyLoader;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ProxyLoaderImpl implements ProxyLoader {

    @SneakyThrows
    @Override
    public void load(List<Map> configurations) {

        byte[][] encryptionDatas = new byte[2][];
        for(Map configuration : configurations){
            int port = MapUtils.getInteger(configuration, "port");
            boolean verify = MapUtils.getBoolean(configuration, "verify", false);
            SocketStreamProxy socketStreamProxy;
            if(verify){
                String name = MapUtils.getString(configuration, "name");
                String password = MapUtils.getString(configuration, "password");
                encryptionDatas[0] = name.getBytes(StandardCharsets.UTF_8);
                encryptionDatas[1] = password.getBytes(StandardCharsets.UTF_8);
                socketStreamProxy = new SocketStreamProxy(port, encryptionDatas);
            }else {
                socketStreamProxy = new SocketStreamProxy(port);
            }

            String proxy = MapUtils.getString(configuration, "proxy");
            if(!ValueUtils.isBlank(proxy)){
                String[] proxyArr = proxy.split(":");
                socketStreamProxy.setProxy(proxyArr[0], Integer.parseInt(proxyArr[1]));
            }
            socketStreamProxy.asynStart();
        }
    }
}
