package org.wlpiaoyi.framework.ee.proxy.socket.stream;


import org.wlpiaoyi.framework.ee.proxy.socket.stream.StreamThread;

import java.util.Map;

/**
 * <p><b>{@code @description:}</b>  data stream run callback</p>
 * <p><b>{@code @date:}</b>         2024/3/21 16:45</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public interface StreamCourse {

    /**
     * <p><b>{@code @description:}</b>
     * data stream push start
     * </p>
     *
     * <p><b>@param</b> <b>stream</b>
     * {@link StreamThread}
     * </p>
     *
     * <p><b>@param</b> <b>userMap</b>
     * {@link Map<Object, Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/21 16:47</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void streamStart(StreamThread stream, Map<Object, Object> userMap);

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>stream</b>
     * {@link StreamThread}
     * </p>
     *
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>@param</b> <b>len</b>
     * {@link int}
     * </p>
     *
     * <p><b>@param</b> <b>userMap</b>
     * {@link Map<Object, Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/21 16:48</p>
     * <p><b>{@code @return:}</b>{@link byte[]}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    byte[] streaming(StreamThread stream, byte[] buffer, int len, Map<Object, Object> userMap);

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>stream</b>
     * {@link StreamThread}
     * </p>
     *
     * <p><b>@param</b> <b>userMap</b>
     * {@link Map<Object, Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/21 16:48</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void streamEnd(StreamThread stream, Map<Object, Object> userMap);

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>stream</b>
     * {@link StreamThread}
     * </p>
     *
     * <p><b>@param</b> <b>e</b>
     * {@link Exception}
     * </p>
     *
     * <p><b>@param</b> <b>userMap</b>
     * {@link Map<Object, Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/21 16:48</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void streamError(StreamThread stream, Exception e, Map<Object, Object> userMap);
}
