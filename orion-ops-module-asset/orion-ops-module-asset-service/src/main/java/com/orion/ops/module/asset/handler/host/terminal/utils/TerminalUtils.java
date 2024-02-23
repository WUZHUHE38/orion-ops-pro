package com.orion.ops.module.asset.handler.host.terminal.utils;

import com.orion.ops.framework.biz.operator.log.core.model.OperatorLogModel;
import com.orion.ops.framework.biz.operator.log.core.utils.OperatorLogFiller;
import com.orion.ops.framework.common.constant.ExtraFieldConst;
import com.orion.ops.framework.websocket.core.utils.WebSockets;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * 终端工具类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/2/23 16:25
 */
public class TerminalUtils {

    private TerminalUtils() {
    }

    /**
     * 获取操作日志模型
     *
     * @param channel   channel
     * @param extra     extra
     * @param type      type
     * @param startTime startTime
     * @param ex        ex
     * @return model
     */
    public static OperatorLogModel getOperatorLogModel(WebSocketSession channel,
                                                       Map<String, Object> extra,
                                                       String type,
                                                       long startTime,
                                                       Exception ex) {
        // 日志参数
        return OperatorLogFiller.create()
                // 填充用户信息
                .fillUserInfo(WebSockets.getAttr(channel, ExtraFieldConst.USER_ID),
                        WebSockets.getAttr(channel, ExtraFieldConst.USERNAME))
                // 填充 traceId
                .fillTraceId(WebSockets.getAttr(channel, ExtraFieldConst.TRACE_ID))
                // 填充请求留痕信息
                .fillIdentity(WebSockets.getAttr(channel, ExtraFieldConst.IDENTITY))
                // 填充使用时间
                .fillUsedTime(startTime)
                // 填充结果信息
                .fillResult(null, ex)
                // 填充拓展信息
                .fillExtra(extra)
                // 填充日志
                .fillLogInfo(extra, type)
                .get();
    }

}
