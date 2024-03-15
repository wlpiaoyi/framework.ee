package org.wlpiaoyi.framework.ee.fileScan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Slf4j
@Component
public class ApplicationListens implements CommandLineRunner, DisposableBean {


    @Value("${server.port}")
    private int port;

    //应用启动成功后的回调
    @Override
    public void run(String... args) throws Exception {
        log.info("应用启动成功，预相关加载数据");
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            // 获得与该网络接口绑定的 IP 地址，一般只有一个
            Enumeration<InetAddress> addresses = nif.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (!(addr instanceof Inet4Address)) {
                    continue;
                }
                log.info("Host[{}:{}]", addr.getHostAddress(), this.port);
            }
        }
    }

    //应用启动关闭前的回调
    @Override
    public void destroy() throws Exception {
        log.info("应用正在关闭，清理相关数据");
    }
}
