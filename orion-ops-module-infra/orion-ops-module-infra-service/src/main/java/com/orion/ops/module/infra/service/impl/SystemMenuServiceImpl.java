package com.orion.ops.module.infra.service.impl;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.ops.framework.common.constant.Const;
import com.orion.ops.framework.common.constant.ErrorMessage;
import com.orion.ops.framework.common.utils.Valid;
import com.orion.ops.framework.mybatis.core.query.Conditions;
import com.orion.ops.module.infra.convert.SystemMenuConvert;
import com.orion.ops.module.infra.dao.SystemMenuDAO;
import com.orion.ops.module.infra.dao.SystemRoleMenuDAO;
import com.orion.ops.module.infra.entity.domain.SystemMenuDO;
import com.orion.ops.module.infra.entity.dto.SystemMenuCacheDTO;
import com.orion.ops.module.infra.entity.request.menu.SystemMenuCreateRequest;
import com.orion.ops.module.infra.entity.request.menu.SystemMenuQueryRequest;
import com.orion.ops.module.infra.entity.request.menu.SystemMenuUpdateRequest;
import com.orion.ops.module.infra.entity.request.menu.SystemMenuUpdateStatusRequest;
import com.orion.ops.module.infra.entity.vo.SystemMenuVO;
import com.orion.ops.module.infra.enums.MenuStatusEnum;
import com.orion.ops.module.infra.enums.MenuTypeEnum;
import com.orion.ops.module.infra.service.PermissionService;
import com.orion.ops.module.infra.service.SystemMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜单 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-7-17 11:39
 */
@Slf4j
@Service
public class SystemMenuServiceImpl implements SystemMenuService {

    @Resource
    private SystemMenuDAO systemMenuDAO;

    @Resource
    private SystemRoleMenuDAO systemRoleMenuDAO;

    @Resource
    private PermissionService permissionService;

    @Override
    public Long createSystemMenu(SystemMenuCreateRequest request) {
        // 转换
        SystemMenuDO record = SystemMenuConvert.MAPPER.to(request);
        record.setStatus(MenuStatusEnum.ENABLED.getStatus());
        // 验证参数
        this.validateRequest(record, request.getType());
        // 方法
        if (record.getSort() == null) {
            record.setSort(Const.DEFAULT_SORT);
        }
        // 保存数据
        int effect = systemMenuDAO.insert(record);
        log.info("SystemMenuService-createSystemMenu effect: {}, record: {}", effect, JSON.toJSONString(record));
        // 保存至缓存
        List<SystemMenuCacheDTO> menuCache = permissionService.getMenuCache();
        menuCache.add(SystemMenuConvert.MAPPER.toCache(record));
        return record.getId();
    }

    @Override
    public Integer updateSystemMenu(SystemMenuUpdateRequest request) {
        // 查询
        Long id = Valid.notNull(request.getId(), ErrorMessage.ID_MISSING);
        SystemMenuDO record = systemMenuDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 转换
        SystemMenuDO updateRecord = SystemMenuConvert.MAPPER.to(request);
        // 验证参数
        this.validateRequest(updateRecord, record.getType());
        // 更新
        int effect = systemMenuDAO.updateById(updateRecord);
        log.info("SystemMenuService-updateSystemMenu effect: {}, updateRecord: {}", effect, JSON.toJSONString(updateRecord));
        // 重新查询转换为缓存
        SystemMenuCacheDTO cache = SystemMenuConvert.MAPPER.toCache(systemMenuDAO.selectById(id));
        // 获取原始缓存
        permissionService.getMenuCache()
                .stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .ifPresent(s -> {
                    // 覆盖属性 防止引用丢失
                    BeanUtils.copyProperties(cache, s);
                });
        return effect;
    }

    @Override
    public SystemMenuVO getSystemMenu(Long id) {
        // 查询
        SystemMenuDO record = systemMenuDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 转换
        return SystemMenuConvert.MAPPER.to(record);
    }

    @Override
    public List<SystemMenuVO> getSystemMenuList(SystemMenuQueryRequest request) {
        String name = request.getName();
        Integer type = request.getType();
        Integer status = request.getStatus();
        // 从缓存中查询
        List<SystemMenuVO> menus = permissionService.getMenuCache()
                .stream()
                .filter(s -> Strings.isBlank(name) || s.getName().contains(name))
                .filter(s -> type == null || s.getType().equals(type))
                .filter(s -> status == null || s.getStatus().equals(status))
                .map(SystemMenuConvert.MAPPER::to)
                .collect(Collectors.toList());
        if (menus.isEmpty()) {
            return Lists.empty();
        }
        // 构建菜单树
        return this.buildSystemMenuTree(menus);
    }

    @Override
    public List<SystemMenuVO> buildSystemMenuTree(List<SystemMenuVO> menus) {
        // id 映射数据
        Map<Long, SystemMenuVO> idMapping = menus.stream()
                .collect(Collectors.toMap(SystemMenuVO::getId, Function.identity()));

        // 子级节点数据
        Map<Long, List<SystemMenuVO>> childrenNodesGroup = menus.stream()
                .collect(Collectors.groupingBy(SystemMenuVO::getParentId));

        // 寻找根节点
        List<SystemMenuVO> rootNodes = menus.stream()
                .filter(s -> idMapping.get(s.getParentId()) == null)
                .collect(Collectors.toList());

        // 设置子节点
        this.setChildrenNodes(rootNodes, childrenNodesGroup);
        return rootNodes;
    }

    /**
     * 设置子节点
     *
     * @param parentNodes        父节点
     * @param childrenNodesGroup 子节点组
     */
    private void setChildrenNodes(List<SystemMenuVO> parentNodes, Map<Long, List<SystemMenuVO>> childrenNodesGroup) {
        // 为空则跳出
        if (Lists.isEmpty(parentNodes)) {
            return;
        }
        // 排序
        parentNodes.sort(Comparator.comparing(SystemMenuVO::getType)
                .thenComparing(SystemMenuVO::getSort)
                .thenComparing(SystemMenuVO::getId));
        // 设置子节点
        for (SystemMenuVO parentNode : parentNodes) {
            List<SystemMenuVO> childrenNodes = childrenNodesGroup.get(parentNode.getId());
            parentNode.setChildren(childrenNodes);
            // 非叶子节点继续设置
            if (Lists.isNotEmpty(childrenNodes)) {
                // 级联设置
                this.setChildrenNodes(childrenNodes, childrenNodesGroup);
            }
        }
    }

    @Override
    public Integer updateSystemMenuStatus(SystemMenuUpdateStatusRequest request) {
        Long id = request.getId();
        Integer status = Valid.valid(MenuStatusEnum::of, request.getStatus()).getStatus();
        // 查询
        SystemMenuDO record = systemMenuDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 从缓存中查询
        List<SystemMenuCacheDTO> cache = permissionService.getMenuCache();
        // 获取要删除的id
        List<Long> updateIdList = this.getChildrenIdList(id, cache, record.getType());
        // 修改状态
        SystemMenuDO update = new SystemMenuDO();
        update.setStatus(status);
        int effect = systemMenuDAO.update(update, Conditions.in(SystemMenuDO::getId, updateIdList));
        // 修改引用缓存状态
        cache.stream()
                .filter(s -> updateIdList.contains(s.getId()))
                .forEach(s -> s.setStatus(status));
        log.info("SystemMenuService-updateSystemMenuStatus updateIdList: {}, effect: {}", JSON.toJSONString(updateIdList), effect);
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteSystemMenu(Long id) {
        // 查询
        SystemMenuDO record = systemMenuDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 从缓存中查询
        List<SystemMenuCacheDTO> cache = permissionService.getMenuCache();
        // 获取要删除的id
        List<Long> deletedIdList = this.getChildrenIdList(id, cache, record.getType());
        // 删除菜单
        int effect = systemMenuDAO.deleteBatchIds(deletedIdList);
        // 删除角色菜单关联
        effect += systemRoleMenuDAO.deleteByMenuId(deletedIdList);
        // 删除菜单缓存
        cache.removeIf(s -> deletedIdList.contains(s.getId()));
        // 删除引用缓存
        permissionService.getRoleMenuCache()
                .values()
                .forEach(roleMenus -> roleMenus.removeIf(s -> deletedIdList.contains(s.getId())));
        log.info("SystemMenuService-deleteSystemMenu deletedIdList: {}, effect: {}", JSON.toJSONString(deletedIdList), effect);
        return effect;
    }

    /**
     * 获取子节点id集合
     *
     * @param id    id
     * @param cache cache
     * @param type  type
     * @return childrenId
     */
    private List<Long> getChildrenIdList(Long id, List<SystemMenuCacheDTO> cache, Integer type) {
        // 需要更新的菜单id
        List<Long> idList = new ArrayList<>();
        idList.add(id);
        // 级联查询
        if (MenuTypeEnum.PARENT_MENU.getType().equals(type)) {
            // 查询子节点
            List<Long> childrenIdList = cache.stream()
                    .filter(s -> s.getParentId().equals(id))
                    .map(SystemMenuCacheDTO::getId)
                    .collect(Collectors.toList());
            idList.addAll(childrenIdList);
            // 级联查询子节点
            cache.stream()
                    .filter(s -> childrenIdList.contains(s.getParentId()))
                    .map(SystemMenuCacheDTO::getId)
                    .forEach(idList::add);
        } else if (MenuTypeEnum.SUB_MENU.getType().equals(type)) {
            // 查询子节点
            cache.stream()
                    .filter(s -> s.getParentId().equals(id))
                    .map(SystemMenuCacheDTO::getId)
                    .forEach(idList::add);
        }
        return idList;
    }

    /**
     * 验证创建菜单参数 不进行重复性校验
     *
     * @param domain   domain
     * @param menuType menuType
     */
    private void validateRequest(SystemMenuDO domain, Integer menuType) {
        // 父id不能为当前id
        Valid.isFalse(Objects.equals(domain.getParentId(), domain.getId()), ErrorMessage.INVALID_PARENT_MENU);
        // 检查菜单类型
        MenuTypeEnum type = Valid.valid(MenuTypeEnum::of, menuType);
        // 验证父菜单参数
        if (MenuTypeEnum.PARENT_MENU.equals(type)) {
            // 父菜单创建的 parentId 为 0
            domain.setParentId(Const.ROOT_MENU_ID);
            // 验证必填参数
            Valid.valid(SystemMenuConvert.MAPPER.toMenuValidate(domain));
            return;
        }
        // 验证 parentId 是否存在
        SystemMenuDO parent = Valid.notNull(systemMenuDAO.selectById(domain.getParentId()), ErrorMessage.PARENT_MENU_ABSENT);
        // 验证子菜单/功能参数
        if (MenuTypeEnum.SUB_MENU.equals(type)) {
            // 验证必填参数
            Valid.valid(SystemMenuConvert.MAPPER.toMenuValidate(domain));
            // 父级必须为父菜单
            Valid.eq(parent.getType(), MenuTypeEnum.PARENT_MENU.getType(), ErrorMessage.INVALID_PARENT_MENU);
        } else if (MenuTypeEnum.FUNCTION.equals(type)) {
            // 验证必填参数
            Valid.valid(SystemMenuConvert.MAPPER.toFunctionValidate(domain));
            // 父级必须不能为自己
            Valid.neq(parent.getType(), MenuTypeEnum.FUNCTION.getType(), ErrorMessage.INVALID_PARENT_MENU);
        }
    }

}
