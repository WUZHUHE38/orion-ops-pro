⚡ 注意: 应用不支持跨版本升级, 可以进行多次升级

## v1.0.1

> sql 脚本

```sql
DROP TABLE IF EXISTS `command_template`;
ALTER TABLE `operator_log` ADD INDEX `idx_type`(`type`);
-- 菜单配置
DELETE FROM `system_menu` WHERE id IN (148, 149);
INSERT INTO `system_menu` VALUES (148, 152, '连接日志', NULL, 2, 10, 1, 1, 1, 0, 'IconLink', NULL, 'assetAuditConnectLog', '2023-12-26 22:53:07', '2024-03-05 23:31:23', NULL, '1', 0);
INSERT INTO `system_menu` VALUES (149, 148, '查询连接日志', 'asset:host-connect-log:management:query', 3, 10, 1, 1, 1, 0, NULL, NULL, NULL, '2023-12-26 22:53:08', '2024-03-04 13:40:42', NULL, '1', 0);
INSERT INTO `system_menu` VALUES (152, 0, '运维审计', NULL, 1, 410, 1, 1, 1, 0, 'IconSafe', NULL, 'assetAudit', '2024-01-04 17:54:56', '2024-03-05 23:31:10', '1', '1', 0);
INSERT INTO `system_menu` VALUES (153, 148, '删除连接日志', 'asset:host-connect-log:management:delete', 3, 20, 1, 1, 1, 0, NULL, NULL, NULL, '2024-03-04 13:39:46', '2024-03-04 13:40:29', '1', '1', 0);
INSERT INTO `system_menu` VALUES (154, 148, '清空连接日志', 'asset:host-connect-log:management:clear', 3, 30, 1, 1, 1, 0, NULL, NULL, NULL, '2024-03-04 13:40:05', '2024-03-04 13:40:34', '1', '1', 0);
INSERT INTO `system_menu` VALUES (155, 148, '强制断开连接', 'asset:host-connect-log:management:force-offline', 3, 40, 1, 1, 1, 0, NULL, NULL, NULL, '2024-03-04 13:41:02', '2024-03-05 23:32:01', '1', '1', 0);
INSERT INTO `system_menu` VALUES (156, 122, '删除操作日志', 'infra:operator-log:delete', 3, 20, 1, 1, 1, 0, NULL, NULL, NULL, '2024-03-04 17:06:55', '2024-03-04 17:08:22', '1', '1', 0);
INSERT INTO `system_menu` VALUES (157, 122, '清空操作日志', 'infra:operator-log:clear', 3, 30, 1, 1, 1, 0, NULL, NULL, NULL, '2024-03-04 17:07:25', '2024-03-04 17:08:27', '1', '1', 0);
INSERT INTO `system_menu` VALUES (158, 152, 'SFTP 操作日志', NULL, 2, 20, 1, 1, 1, 0, 'IconFile', NULL, 'assetAuditSftpLog', '2024-03-05 15:30:13', '2024-03-05 23:31:32', '1', '1', 0);
INSERT INTO `system_menu` VALUES (159, 158, '查询 SFTP 操作日志', 'asset:host-sftp-log:management:query', 3, 10, 1, 1, 1, 0, NULL, NULL, NULL, '2024-03-05 15:31:02', '2024-03-05 15:57:20', '1', '1', 0);
INSERT INTO `system_menu` VALUES (160, 158, '删除 SFTP 操作日志', 'asset:host-sftp-log:management:delete', 3, 20, 1, 1, 1, 0, NULL, NULL, NULL, '2024-03-05 15:31:17', '2024-03-05 15:57:30', '1', '1', 0);
-- 字典配置项
INSERT INTO `dict_key` VALUES (33, 'sftpOperatorType', 'STRING', '[]', 'SFTP 操作类型', '2024-03-05 16:49:54', '2024-03-05 16:49:54', '1', '1', 0);
-- 字典配置值
INSERT INTO `dict_value` VALUES (214, 28, 'hostConnectStatus', 'FORCE_OFFLINE', '强制下线', '{\"color\": \"rgb(var(--red-6))\"}', 40, '2024-03-04 12:51:13', '2024-03-04 12:51:13', '1', '1', 0);
INSERT INTO `dict_value` VALUES (215, 1, 'operatorLogModule', 'asset:host-connect-log', '主机连接日志', '{}', 2060, '2024-03-04 13:43:33', '2024-03-04 13:43:33', '1', '1', 0);
INSERT INTO `dict_value` VALUES (216, 2, 'operatorLogType', 'host-connect-log:delete', '删除记录', '{}', 10, '2024-03-04 13:44:34', '2024-03-04 13:44:34', '1', '1', 0);
INSERT INTO `dict_value` VALUES (217, 2, 'operatorLogType', 'host-connect-log:clear', '清空记录', '{}', 20, '2024-03-04 13:45:07', '2024-03-04 14:22:08', '1', '1', 0);
INSERT INTO `dict_value` VALUES (218, 2, 'operatorLogType', 'host-connect-log:force-offline', '强制下线', '{}', 30, '2024-03-04 13:45:36', '2024-03-04 13:45:36', '1', '1', 0);
INSERT INTO `dict_value` VALUES (219, 1, 'operatorLogModule', 'infra:operator-log', '操作日志', '{}', 1060, '2024-03-04 16:32:11', '2024-03-04 16:32:11', '1', '1', 0);
INSERT INTO `dict_value` VALUES (220, 2, 'operatorLogType', 'operator-log:delete', '删除操作日志', '{}', 10, '2024-03-04 16:33:11', '2024-03-04 16:33:44', '1', '1', 0);
INSERT INTO `dict_value` VALUES (221, 2, 'operatorLogType', 'operator-log:clear', '清空操作日志', '{}', 20, '2024-03-04 16:33:31', '2024-03-04 16:33:31', '1', '1', 0);
INSERT INTO `dict_value` VALUES (222, 2, 'operatorLogType', 'host-terminal:delete-sftp-log', '删除SFTP操作日志', '{}', 15, '2024-03-05 15:28:00', '2024-03-05 17:40:47', '1', '1', 0);
INSERT INTO `dict_value` VALUES (223, 33, 'sftpOperatorType', 'host-terminal:sftp-mkdir', '创建文件夹', '{}', 10, '2024-03-05 16:50:17', '2024-03-05 16:50:17', '1', '1', 0);
INSERT INTO `dict_value` VALUES (224, 33, 'sftpOperatorType', 'host-terminal:sftp-touch', '创建文件', '{}', 20, '2024-03-05 16:50:27', '2024-03-05 16:50:27', '1', '1', 0);
INSERT INTO `dict_value` VALUES (225, 33, 'sftpOperatorType', 'host-terminal:sftp-move', '移动文件', '{}', 30, '2024-03-05 16:50:41', '2024-03-05 16:50:41', '1', '1', 0);
INSERT INTO `dict_value` VALUES (226, 33, 'sftpOperatorType', 'host-terminal:sftp-remove', '删除文件', '{}', 40, '2024-03-05 16:50:53', '2024-03-05 16:50:53', '1', '1', 0);
INSERT INTO `dict_value` VALUES (227, 33, 'sftpOperatorType', 'host-terminal:sftp-truncate', '截断文件', '{}', 50, '2024-03-05 16:51:04', '2024-03-05 16:51:04', '1', '1', 0);
INSERT INTO `dict_value` VALUES (228, 33, 'sftpOperatorType', 'host-terminal:sftp-chmod', '文件提权', '{}', 60, '2024-03-05 16:51:15', '2024-03-05 16:51:15', '1', '1', 0);
INSERT INTO `dict_value` VALUES (229, 33, 'sftpOperatorType', 'host-terminal:sftp-set-content', '修改文件内容', '{}', 70, '2024-03-05 16:51:30', '2024-03-05 16:51:48', '1', '1', 0);
INSERT INTO `dict_value` VALUES (230, 33, 'sftpOperatorType', 'host-terminal:sftp-upload', '上传文件', '{}', 80, '2024-03-05 16:52:06', '2024-03-05 16:52:06', '1', '1', 0);
INSERT INTO `dict_value` VALUES (231, 33, 'sftpOperatorType', 'host-terminal:sftp-download', '下载文件', '{}', 90, '2024-03-05 16:52:18', '2024-03-05 16:52:18', '1', '1', 0);
```

## v1.0.0

> sql 脚本

```sql
INSERT INTO `dict_key` VALUES (32, 'terminalTabColor', 'COLOR', '[]', '终端标签页颜色', '2024-03-01 15:01:44', '2024-03-01 15:01:44', '1', '1', 0);
INSERT INTO `dict_value` VALUES (203, 32, 'terminalTabColor', 'rgb(var(--red-6))', '红色', '{}', 10, '2024-03-01 15:07:41', '2024-03-01 15:07:41', '1', '1', 0);
INSERT INTO `dict_value` VALUES (204, 32, 'terminalTabColor', 'rgb(var(--orange-6))', '橙色', '{}', 20, '2024-03-01 15:07:55', '2024-03-01 15:07:55', '1', '1', 0);
INSERT INTO `dict_value` VALUES (205, 32, 'terminalTabColor', 'rgb(var(--yellow-6))', '黄色', '{}', 30, '2024-03-01 15:08:13', '2024-03-01 15:08:13', '1', '1', 0);
INSERT INTO `dict_value` VALUES (206, 32, 'terminalTabColor', 'rgb(var(--green-6))', '绿色', '{}', 40, '2024-03-01 15:08:23', '2024-03-01 15:08:23', '1', '1', 0);
INSERT INTO `dict_value` VALUES (207, 32, 'terminalTabColor', 'rgb(var(--cyan-6))', '青色', '{}', 50, '2024-03-01 15:08:46', '2024-03-01 15:08:46', '1', '1', 0);
INSERT INTO `dict_value` VALUES (208, 32, 'terminalTabColor', 'rgb(var(--blue-6))', '浅蓝', '{}', 60, '2024-03-01 15:11:01', '2024-03-01 15:11:01', '1', '1', 0);
INSERT INTO `dict_value` VALUES (209, 32, 'terminalTabColor', 'rgb(var(--arcoblue-6))', '蓝色', '{}', 70, '2024-03-01 15:11:11', '2024-03-01 15:11:11', '1', '1', 0);
INSERT INTO `dict_value` VALUES (210, 32, 'terminalTabColor', 'rgb(var(--purple-6))', '紫色', '{}', 80, '2024-03-01 15:11:20', '2024-03-01 15:11:20', '1', '1', 0);
INSERT INTO `dict_value` VALUES (211, 32, 'terminalTabColor', 'rgb(var(--pinkpurple-6))', '粉紫', '{}', 90, '2024-03-01 15:11:41', '2024-03-01 15:11:41', '1', '1', 0);
INSERT INTO `dict_value` VALUES (213, 32, 'terminalTabColor', 'rgb(var(--gray-6))', '灰色', '{}', 100, '2024-03-01 15:12:01', '2024-03-01 15:39:34', '1', '1', 0);
```
