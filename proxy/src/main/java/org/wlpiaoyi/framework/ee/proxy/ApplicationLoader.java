package org.wlpiaoyi.framework.ee.proxy;

import com.google.gson.Gson;
import org.wlpiaoyi.framework.ee.proxy.socket.ProxyLoaderImpl;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.util.List;
import java.util.Map;

/**
 * <p><b>{@code @description:}</b>  </p>
 * <p><b>{@code @date:}</b>         2024-03-28 11:35:05</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class ApplicationLoader {


    public static void main(String[] args) throws Exception {
        String PATH = System.getProperty("user.dir") + "/config/socket.proxy.json";
        String jsonStr = DataUtils.readFile(PATH);
        Map configurations = new Gson().fromJson(jsonStr, Map.class);
        List<Map> socketConfigs = MapUtils.getList(configurations, "socket");
        ProxyLoader socketLoader = new ProxyLoaderImpl();
        socketLoader.load(socketConfigs);
        while (true) Thread.sleep(7 * 24 * 60 * 60 * 1000);

    }

}
