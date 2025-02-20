package com.orion.ops.module.infra.handler.preference.strategy;

import com.alibaba.fastjson.JSONObject;
import com.orion.lang.utils.collect.Lists;
import com.orion.net.host.ssh.TerminalType;
import com.orion.ops.module.infra.handler.preference.model.TerminalPreferenceModel;
import org.springframework.stereotype.Component;

/**
 * 终端偏好处理策略
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/12/8 14:46
 */
@Component
public class TerminalPreferenceStrategy implements IPreferenceStrategy<TerminalPreferenceModel> {

    @Override
    public TerminalPreferenceModel getDefault() {
        // 默认显示设置
        String defaultDisplaySetting = TerminalPreferenceModel.DisplaySettingModel
                .builder()
                .fontFamily("_")
                .fontSize(14)
                .lineHeight(1.00)
                .fontWeight("normal")
                .fontWeightBold("bold")
                .cursorStyle("bar")
                .cursorBlink(true)
                .build()
                .toJsonString();
        // 默认交互设置
        String defaultInteractSetting = TerminalPreferenceModel.InteractSettingModel.builder()
                .fastScrollModifier(true)
                .altClickMovesCursor(true)
                .rightClickSelectsWord(false)
                .selectionChangeCopy(false)
                .copyAutoTrim(false)
                .pasteAutoTrim(false)
                .rightClickPaste(false)
                .enableRightClickMenu(true)
                .enableBell(false)
                .wordSeparator("/\\()\"'` -.,:;<>~!@#$%^&*|+=[]{}~?│")
                .build()
                .toJsonString();
        // 默认插件设置
        String defaultPluginsSetting = TerminalPreferenceModel.PluginsSettingModel.builder()
                .enableWeblinkPlugin(true)
                .enableWebglPlugin(true)
                .enableImagePlugin(false)
                .build()
                .toJsonString();
        // 默认会话设置
        String defaultSessionSetting = TerminalPreferenceModel.SessionSettingModel.builder()
                .terminalEmulationType(TerminalType.XTERM.getType())
                .scrollBackLine(1000)
                .build()
                .toJsonString();
        // 默认快捷键设置
        String shortcutSetting = TerminalPreferenceModel.ShortcutSettingModel.builder()
                .enabled(true)
                .keys(Lists.of(
                        // 全局快捷键
                        new TerminalPreferenceModel.ShortcutKeysModel("closeTab", true, true, true, "KeyW", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("changeToPrevTab", true, true, true, "BracketLeft", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("changeToNextTab", true, true, true, "BracketRight", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("openNewConnectTab", true, true, true, "KeyN", true),
                        // 会话快捷键
                        new TerminalPreferenceModel.ShortcutKeysModel("openNewConnectModal", true, false, true, "KeyN", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("copySession", true, false, true, "KeyO", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("closeSession", true, false, true, "KeyW", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("changeToPrevSession", true, false, true, "BracketLeft", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("changeToNextSession", true, false, true, "BracketRight", true),
                        // 终端快捷键
                        new TerminalPreferenceModel.ShortcutKeysModel("copy", true, true, false, "KeyC", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("paste", true, true, false, "KeyV", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("toTop", true, true, false, "ArrowUp", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("toBottom", true, true, false, "ArrowDown", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("selectAll", true, true, false, "KeyA", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("search", true, true, false, "KeyF", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("commandEditor", true, false, true, "KeyE", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("fontSizePlus", true, false, true, "Equal", true),
                        new TerminalPreferenceModel.ShortcutKeysModel("fontSizeSubtract", true, false, true, "Minus", true)
                ))
                .build()
                .toJsonString();
        // 操作栏设置
        String actionBarSetting = TerminalPreferenceModel.ActionBarSettingModel.builder()
                .commandInput(false)
                .connectStatus(true)
                .toTop(true)
                .toBottom(true)
                .selectAll(false)
                .search(true)
                .copy(true)
                .paste(true)
                .interrupt(false)
                .enter(false)
                .fontSizePlus(false)
                .fontSizeSubtract(false)
                .commandEditor(true)
                .openSftp(true)
                .clear(true)
                .disconnect(false)
                .closeTab(true)
                .build()
                .toJsonString();
        // 默认配置
        return TerminalPreferenceModel.builder()
                .newConnectionType("group")
                .theme(new JSONObject())
                .displaySetting(JSONObject.parseObject(defaultDisplaySetting))
                .actionBarSetting(JSONObject.parseObject(actionBarSetting))
                .rightMenuSetting(Lists.of("copy", "paste", "checkAll", "search", "clear"))
                .interactSetting(JSONObject.parseObject(defaultInteractSetting))
                .pluginsSetting(JSONObject.parseObject(defaultPluginsSetting))
                .sessionSetting(JSONObject.parseObject(defaultSessionSetting))
                .shortcutSetting(JSONObject.parseObject(shortcutSetting))
                .build();
    }

}
