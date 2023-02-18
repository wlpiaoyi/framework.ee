package org.wlpiaoyi.framework.ee.utils.advice.filter;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/18 10:39
 * {@code @version:}:       1.0
 */
class ResponseWrapper extends HttpServletResponseWrapper {


    private final ByteArrayOutputStream bufferStream;
    private final ServletOutputStream outputStream;
    private final PrintWriter writer;

    public ResponseWrapper(@NonNull HttpServletResponse response) throws IOException {
        super(response);
        this.bufferStream = new ByteArrayOutputStream();
        this.outputStream = new WrappedOutputStream(bufferStream);
        this.writer = new PrintWriter(new OutputStreamWriter(
                bufferStream, this.getCharacterEncoding()
        ));
    }

    /** 重载父类获取outputstream的方法 */
    @Override
    public ServletOutputStream getOutputStream() {
        return this.outputStream;
    }

    /** 重载父类获取writer的方法 */
    @Override
    public PrintWriter getWriter() {
        return this.writer;
    }

    /** 重载父类获取flushBuffer的方法 */
    @Override
    public void flushBuffer() throws IOException {
        if (this.outputStream != null) {
            this.outputStream.flush();
        }
        if (this.writer != null) {
            this.writer.flush();
        }
    }

    @Override
    public void reset() {
        this.bufferStream.reset();
    }

    /** 将out、writer中的数据强制输出到WapperedResponse的buffer里面，否则取不到数据 */
    public byte[] getResponseData() throws IOException {
        this.flushBuffer();
        return this.bufferStream.toByteArray();
    }

    /**
     * 内部类，对ServletOutputStream进行包装
     */
    private static class WrappedOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream bos;

        public WrappedOutputStream(@NonNull ByteArrayOutputStream stream) {
            this.bos = stream;
        }

        @Override
        public void write(int b) {
            this.bos.write(b);
        }

        @Override
        public void write(@NonNull byte @NotNull [] b) {
            this.bos.write(b, 0, b.length);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

}
