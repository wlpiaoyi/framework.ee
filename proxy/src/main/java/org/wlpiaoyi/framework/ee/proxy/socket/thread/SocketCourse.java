package org.wlpiaoyi.framework.ee.proxy.socket.thread;


import org.wlpiaoyi.framework.ee.proxy.socket.thread.SocketThread;

import java.util.Map;

/**
 * <p><b>{@code @description:}</b>  for the socket connection thread done callback</p>
 * <p><b>{@code @date:}</b>         2024/3/21 16:47</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public interface SocketCourse {


    /**
     * <p><b>{@code @description:}</b>
     * Stop if it returns false, otherwise continue open
     * </p>
     *
     * <p><b>@param</b> <b>socketThread</b>
     * {@link SocketThread}
     * </p>
     *
     * <p><b>@param</b> <b>userMap</b>
     * {@link Map<Object, Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/21 16:42</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    boolean socketOpen(SocketThread socketThread, Map<Object, Object> userMap);

    /**
     * <p><b>{@code @description:}</b>
     * Stop if it returns false, otherwise continue connect
     * </p>
     *
     * <p><b>@param</b> <b>socketThread</b>
     * {@link SocketThread}
     * </p>
     *
     * <p><b>@param</b> <b>userMap</b>
     * {@link Map<Object, Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/21 16:43</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    boolean socketConnect(SocketThread socketThread, Map<Object, Object> userMap);

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>socketThread</b>
     * {@link SocketThread}
     * </p>
     *
     * <p><b>@param</b> <b>userMap</b>
     * {@link Map<Object, Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/21 16:44</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void socketClose(SocketThread socketThread, Map<Object, Object> userMap);


    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>socketThread</b>
     * {@link SocketThread}
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
     * <p><b>{@code @date:}</b>2024/3/21 16:44</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void socketException(SocketThread socketThread, Exception e, Map<Object, Object> userMap);

}
