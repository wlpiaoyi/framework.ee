package org.wlpiaoyi.framework.ee.utils.filter.encrypt;



import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/18 12:07
 * {@code @version:}:       1.0
 */
class RequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }


    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.body = this.toByteArray(request.getInputStream());
    }

    private byte[] toByteArray(ServletInputStream inputStream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n;
        while((n= inputStream.read(buffer)) != -1){
            byteStream.write(buffer, 0, n);
        }
        return byteStream.toByteArray();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteStream.read();
            }
        };
    }
}
