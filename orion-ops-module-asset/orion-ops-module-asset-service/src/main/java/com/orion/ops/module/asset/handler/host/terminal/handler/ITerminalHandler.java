package com.orion.ops.module.asset.handler.host.terminal.handler;

import org.springframework.web.socket.WebSocketSession;

/**
 * 终端消息处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/12/29 18:53
 */
public interface ITerminalHandler {

    /**
     * 处理消息
     *
     * @param session session
     * @param payload payload
     */
    void process(WebSocketSession session, String payload);

}
