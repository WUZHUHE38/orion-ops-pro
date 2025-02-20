package com.orion.ops.module.asset.handler.host.terminal.session;

import com.orion.lang.utils.io.Streams;
import com.orion.net.host.SessionStore;
import com.orion.net.host.ssh.shell.ShellExecutor;
import com.orion.ops.framework.common.constant.Const;
import com.orion.ops.framework.common.enums.BooleanBit;
import com.orion.ops.framework.websocket.core.utils.WebSockets;
import com.orion.ops.module.asset.define.AssetThreadPools;
import com.orion.ops.module.asset.handler.host.terminal.constant.TerminalMessage;
import com.orion.ops.module.asset.handler.host.terminal.enums.OutputTypeEnum;
import com.orion.ops.module.asset.handler.host.terminal.model.TerminalConfig;
import com.orion.ops.module.asset.handler.host.terminal.model.response.SshOutputResponse;
import com.orion.ops.module.asset.handler.host.terminal.model.response.TerminalCloseResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 终端 ssh 会话
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/1/2 17:28
 */
@Slf4j
public class SshSession extends TerminalSession implements ISshSession {

    private final SessionStore sessionStore;

    private ShellExecutor executor;

    @Getter
    private String lastLine;

    public SshSession(String sessionId,
                      WebSocketSession channel,
                      SessionStore sessionStore,
                      TerminalConfig config) {
        super(sessionId, channel, config);
        this.sessionStore = sessionStore;
    }

    @Override
    public void connect(String terminalType, int cols, int rows) {
        config.setCols(cols);
        config.setRows(rows);
        // 打开 shell
        this.executor = sessionStore.getShellExecutor();
        executor.size(cols, rows);
        executor.terminalType(terminalType);
        executor.streamHandler(this::streamHandler);
        executor.callback(this::close);
        executor.connect();
        // 开始监听输出
        AssetThreadPools.TERMINAL_STDOUT.execute(executor);
    }

    @Override
    public void resize(int cols, int rows) {
        // 大小发生变化 则修改大小
        if (cols != config.getCols() ||
                rows != config.getRows()) {
            config.setCols(cols);
            config.setRows(rows);
            executor.size(cols, rows);
            executor.resize();
        }
    }

    @Override
    public void write(String b) {
        executor.write(b);
    }

    @Override
    public void write(byte[] b) {
        executor.write(b);
    }

    @Override
    public void keepAlive() {
        try {
            // 发送个信号 保证 socket 不自动关闭
            executor.sendSignal(Const.EMPTY);
        } catch (Exception e) {
            log.error("ssh keep-alive error {}", sessionId, e);
        }
    }

    @Override
    protected void releaseResource() {
        Streams.close(executor);
        Streams.close(sessionStore);
    }

    /**
     * 标准输出处理
     *
     * @param inputStream stream
     */
    private void streamHandler(InputStream inputStream) {
        byte[] bs = new byte[Const.BUFFER_KB_4];
        BufferedInputStream in = new BufferedInputStream(inputStream, Const.BUFFER_KB_4);
        int read;
        try {
            while (channel.isOpen() && (read = in.read(bs)) != -1) {
                String body = lastLine = new String(bs, 0, read, config.getCharset());
                // 响应
                SshOutputResponse resp = SshOutputResponse.builder()
                        .type(OutputTypeEnum.SSH_OUTPUT.getType())
                        .sessionId(sessionId)
                        .body(body)
                        .build();
                WebSockets.sendText(channel, OutputTypeEnum.SSH_OUTPUT.format(resp));
            }
        } catch (IOException ex) {
            log.error("terminal 读取流失败", ex);
        }
    }

}
