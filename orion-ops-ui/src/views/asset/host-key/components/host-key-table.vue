<template>
  <!-- 搜索 -->
  <a-card class="general-card table-search-card">
    <query-header :model="formModel"
                  label-align="left"
                  @submit="fetchTableData"
                  @reset="fetchTableData"
                  @keyup.enter="() => fetchTableData()">
      <!-- id -->
      <a-form-item field="id" label="id" label-col-flex="30px">
        <a-input-number v-model="formModel.id"
                        placeholder="请输入id"
                        allow-clear
                        hide-button />
      </a-form-item>
      <!-- 名称 -->
      <a-form-item field="name" label="名称" label-col-flex="30px">
        <a-input v-model="formModel.name" placeholder="请输入名称" allow-clear />
      </a-form-item>
    </query-header>
  </a-card>
  <!-- 表格 -->
  <a-card class="general-card table-card">
    <template #title>
      <!-- 左侧操作 -->
      <div class="table-left-bar-handle">
        <!-- 标题 -->
        <div class="table-title">
          秘钥列表
        </div>
      </div>
      <!-- 右侧操作 -->
      <div class="table-right-bar-handle">
        <a-space>
          <!-- 角色授权 -->
          <a-button type="primary"
                    v-permission="['asset:host-key:grant']"
                    @click="$router.push({ name: GrantRouteName, query: { key: GrantKey.HOST_KEY_ROLE }})">
            角色授权
            <template #icon>
              <icon-user-group />
            </template>
          </a-button>
          <!-- 用户授权 -->
          <a-button type="primary"
                    v-permission="['asset:host-key:grant']"
                    @click="$router.push({ name: GrantRouteName, query: { key: GrantKey.HOST_KEY_USER }})">
            用户授权
            <template #icon>
              <icon-user />
            </template>
          </a-button>
          <!-- 新增 -->
          <a-button type="primary"
                    v-permission="['asset:host-key:create']"
                    @click="emits('openAdd')">
            新增
            <template #icon>
              <icon-plus />
            </template>
          </a-button>
        </a-space>
      </div>
    </template>
    <!-- table -->
    <a-table row-key="id"
             ref="tableRef"
             :loading="loading"
             :columns="columns"
             :data="tableRenderData"
             :pagination="pagination"
             @page-change="(page) => fetchTableData(page, pagination.pageSize)"
             @page-size-change="(size) => fetchTableData(1, size)"
             :bordered="false">
      <!-- 操作 -->
      <template #handle="{ record }">
        <div class="table-handle-wrapper">
          <!-- 详情 -->
          <a-button type="text"
                    size="mini"
                    v-permission="['asset:host-key:detail', 'asset:host-key:update']"
                    @click="emits('openView', record)">
            详情
          </a-button>
          <!-- 修改 -->
          <a-button type="text"
                    size="mini"
                    v-permission="['asset:host-key:update']"
                    @click="emits('openUpdate', record)">
            修改
          </a-button>
          <!-- 删除 -->
          <a-popconfirm content="确认删除这条记录吗?"
                        position="left"
                        type="warning"
                        @ok="deleteRow(record)">
            <a-button v-permission="['asset:host-key:delete']"
                      type="text"
                      size="mini"
                      status="danger">
              删除
            </a-button>
          </a-popconfirm>
        </div>
      </template>
    </a-table>
  </a-card>
</template>

<script lang="ts">
  export default {
    name: 'assetHostKeyTable'
  };
</script>

<script lang="ts" setup>
  import type { HostKeyQueryRequest, HostKeyQueryResponse } from '@/api/asset/host-key';
  import { reactive, ref, onMounted } from 'vue';
  import { deleteHostKey, getHostKeyPage } from '@/api/asset/host-key';
  import { Message } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import columns from '../types/table.columns';
  import { usePagination } from '@/types/table';
  import { GrantKey, GrantRouteName } from '@/views/asset/grant/types/const';

  const emits = defineEmits(['openAdd', 'openUpdate', 'openView']);

  const tableRenderData = ref<HostKeyQueryResponse[]>([]);

  const pagination = usePagination();
  const { loading, setLoading } = useLoading();

  const formModel = reactive<HostKeyQueryRequest>({
    id: undefined,
    name: undefined,
    publicKey: undefined,
    privateKey: undefined,
  });

  // 删除当前行
  const deleteRow = async ({ id }: {
    id: number
  }) => {
    try {
      setLoading(true);
      // 调用删除接口
      await deleteHostKey(id);
      Message.success('删除成功');
      // 重新加载数据
      fetchTableData();
    } catch (e) {
    } finally {
      setLoading(false);
    }
  };

  // 添加后回调
  const addedCallback = () => {
    fetchTableData();
  };

  // 更新后回调
  const updatedCallback = () => {
    fetchTableData();
  };

  defineExpose({
    addedCallback, updatedCallback
  });

  // 加载数据
  const doFetchTableData = async (request: HostKeyQueryRequest) => {
    try {
      setLoading(true);
      const { data } = await getHostKeyPage(request);
      tableRenderData.value = data.rows;
      pagination.total = data.total;
      pagination.current = request.page;
      pagination.pageSize = request.limit;
    } catch (e) {
    } finally {
      setLoading(false);
    }
  };

  // 切换页码
  const fetchTableData = (page = 1, limit = pagination.pageSize, form = formModel) => {
    doFetchTableData({ page, limit, ...form });
  };

  onMounted(() => {
    fetchTableData();
  });

</script>

<style lang="less" scoped>

</style>
