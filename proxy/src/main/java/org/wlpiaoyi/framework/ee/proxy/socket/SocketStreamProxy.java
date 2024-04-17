package org.wlpiaoyi.framework.ee.proxy.socket;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.ee.proxy.socket.thread.SocketThread;
import org.wlpiaoyi.framework.ee.proxy.socket.thread.SocketCourse;
import org.wlpiaoyi.framework.ee.proxy.socket.stream.StreamThread;
import org.wlpiaoyi.framework.ee.proxy.socket.stream.StreamCourse;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//nohup java -jar proxy.socket.jar > proxy.socket.temp.log 2>&1 &
/*
vpnsetup_centos.sh
wget https://git.io/vpnsetup -O vpnsetup_centos.sh && sudo \
VPN_IPSEC_PSK='000000 \
VPN_USER='vpnname' \
VPN_PASSWORD='000000' \
sh vpnsetup_centos.sh;

 */
@Slf4j
class SocketStreamProxy implements SocketCourse, StreamCourse {


    private static boolean hasLog = true;

    private static final Map<Integer, SocketStreamProxy> servers = new HashMap<>();

    @Getter
    private final int listenPort;

    @Getter @Setter
    private Proxy proxy;

    private final Set<SocketThread> clients = new HashSet<>();

    private final ServerSocket serverSocket;

    private byte[][] encryptionDatas;

    SocketStreamProxy(int listenPort) throws IOException {
        this.listenPort = listenPort;
        this.encryptionDatas = null;
        this.serverSocket = new ServerSocket(this.listenPort);
        this.proxy = null;
    }
    SocketStreamProxy(int listenPort, byte[][] encryptionDatas) throws IOException {
        this.listenPort = listenPort;
        this.encryptionDatas = encryptionDatas;
        this.serverSocket = new ServerSocket(this.listenPort);
        this.proxy = null;
    }

    void synStart(){
        try{
            SocketStreamProxy.servers.put(listenPort, this);
            if(hasLog)log.info("server start port:{} encryption:{}", listenPort, this.encryptionDatas != null);
            while (this.serverSocket.isClosed() == false) {
                try {
                    Socket socket = serverSocket.accept();
                    SocketThread socketThread;
                    if(this.proxy == null){
                        socketThread = new SocketThread(socket, this.encryptionDatas);
                    }else{
                        socketThread = new SocketThread(socket, this.proxy, this.encryptionDatas);
                    }
                    socketThread.setStreamOperation(this);
                    socketThread.setSocketOperation(this);
                    socketThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            this.close();
        }
    }


    void asynStart(){
        new Thread(this::synStart).start();
    }

    void close(){
        try {
            this.closeAllClient();
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void closeAllClient(){
        if(this.clients.isEmpty()) return;
        synchronized (this.clients){
            for (SocketThread socketThread : this.clients){
                try{
                    socketThread.close();
                }catch(Exception e){};
            }
            this.clients.clear();
        }
    }


    void setProxy(String proxyIP,int proxyPort) {
        if(ValueUtils.isBlank(proxyIP) || proxyPort <= 0){
            this.proxy = null;
        }else {
            this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyIP, proxyPort));
        }
    }

    @Override
    public boolean socketOpen(SocketThread socketThread, Map<Object, Object> userMap) {
        synchronized (this.clients){
            this.clients.add(socketThread);
        }
        if(hasLog)log.info("socket ip:{} port:{} come in count ======>{}", socketThread.getRequestDomain(), socketThread.getRequestPort(), this.clients.size());
        return true;
    }


    @Override
    public boolean socketConnect(SocketThread socketThread, Map<Object, Object> userMap) {
        if(hasLog)log.info("socket ip:{} port:{} connect to domain:{} port:{} ", socketThread.getRequestDomain(), socketThread.getRequestPort(), socketThread.getResponseDomain(), socketThread.getResponsePort());
        return true;
    }

    @Override
    public void socketClose(SocketThread socketThread, Map<Object, Object> userMap) {
        synchronized (this.clients){
            this.clients.remove(socketThread);
        }
        if(hasLog)log.info("socket ip:{} port:{} come out count <======{}", socketThread.getRequestDomain(), socketThread.getRequestPort(), this.clients.size());
    }

    @Override
    public void socketException(SocketThread socketThread, Exception e, Map<Object, Object> userMap) {
        if(hasLog)log.error ("socket ip:" + socketThread.getRequestDomain() + "port:" + socketThread.getRequestDomain() + " exception domain:" + socketThread.getRequestPort() + " port:" + socketThread.getResponseDomain(), e);
    }

    public static Set<Map.Entry<Integer, SocketStreamProxy>> getServers() {
        return servers.entrySet();
    }
    public static SocketStreamProxy remove(int listenPort){
        return servers.remove(listenPort);
    }
    public static SocketStreamProxy get(int listenPort){
        return servers.get(listenPort);
    }

    @Override
    public void streamStart(StreamThread stream, Map<Object, Object> userMap) {

    }

    @Override
    public byte[] streaming(StreamThread stream, byte[] buffer, int len, Map<Object, Object> userMap) {
//        try {
//            log.info("buffer hex:{}", ValueUtils.bytesToHex(buffer, 0, len));
//            log.info("buffer hex:{}", new String(buffer, 0, len, "UTF-8"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if(userMap.containsKey("responseDomain")
//                && userMap.get("responseDomain").equals("mobile.yangkeduo.com") && userMap.get("responsePort").equals(80)){
//            StringBuffer sb = new StringBuffer();
//            String suffix = "\r\n";
//            sb.append("HTTP/1.1 200 OK");
//            sb.append(suffix);
//            sb.append("Host:mobile.yangkeduo.com");
//            sb.append(suffix);
//            sb.append("server: stgw/1.3.12_1.13.5");
//            sb.append(suffix);
//            sb.append("Content-Type: text/html; charset=UTF-8");
//            sb.append(suffix);
//            sb.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            sb.append(suffix);
//            sb.append("Upgrade-Insecure-Requests: 1");
//            sb.append(suffix);
//            sb.append("set-cookie: pdd_user_id=2748077958336; Path=/; Expires=Sun, 01 Mar 2030 09:51:54 GMT");
//            sb.append(suffix);
//            sb.append("set-cookie: PDDAccessToken=33OTWG66GXGKGVXTCT4FNRV4OIT2UVDV5QDO4NQ7TWLPS26AYCJQ110c070; Path=/; Expires=Sun, 01 Mar 2030 09:51:54 GMT");
//            sb.append(suffix);
//            sb.append("User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/78.0.3904.84 Mobile/15E148 Safari/604.1");
//            sb.append(suffix);
//            sb.append("Accept-Language: zh-cn");
//            sb.append(suffix);
//            sb.append("Connection: keep-alive");
//            sb.append(suffix);
//            sb.append(suffix);
//            sb.append(suffix);
//            sb.append("<html>" +
//                    "<script>" +
////                    "window.location.href='https://mobile.yangkeduo.com/goods.html?goods_id=64482125726';" +
//                    "</script>" +
//                    "</html>");
//            sb.append(suffix);
//            sb.append(suffix);
//            sb.append(suffix);
//            String returnValue = sb.toString();
//
//            try {
//                return returnValue.getBytes("UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }else
//            return null;
        return null;
    }

    @Override
    public void streamEnd(StreamThread stream, Map<Object, Object> userMap) {

    }

    @Override
    public void streamError(StreamThread stream, Exception e, Map<Object, Object> userMap) {

    }
}
