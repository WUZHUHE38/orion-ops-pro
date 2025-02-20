<template>
  <!-- 表格 -->
  <a-table row-key="id"
           ref="tableRef"
           :loading="loading"
           :columns="tableColumns"
           :data="tableRenderData"
           :pagination="pagination"
           @page-change="(page) => fetchTableData(page, pagination.pageSize)"
           @page-size-change="(size) => fetchTableData(1, size)"
           :bordered="false">
    <!-- 操作模块 -->
    <template #module="{ record }">
      {{ getDictValue(operatorLogModuleKey, record.module) }}
      <icon-oblique-line />
      {{ getDictValue(operatorLogTypeKey, record.type) }}
    </template>
    <!-- 风险等级 -->
    <template #riskLevel="{ record }">
      <a-tag :color="getDictValue(operatorRiskLevelKey, record.riskLevel, 'color')">
        {{ getDictValue(operatorRiskLevelKey, record.riskLevel) }}
      </a-tag>
    </template>
    <!-- 执行结果 -->
    <template #result="{ record }">
      <a-tag :color="getDictValue(operatorLogResultKey, record.result, 'color')">
        {{ getDictValue(operatorLogResultKey, record.result) }}
      </a-tag>
    </template>
    <!-- 操作日志 -->
    <template #originLogInfo="{ record }">
      <icon-copy class="copy-left" @click="copy(record.originLogInfo, '已复制')" />
      <span v-html="replaceHtmlTag(record.logInfo)" />
    </template>
    <!-- 留痕地址 -->
    <template #address="{ record }">
      <span class="operator-location" :title="record.location">
        {{ record.location }}
      </span>
      <br>
      <span class="operator-address text-copy"
            :title="record.address"
            @click="copy(record.address)">
        {{ record.address }}
      </span>
    </template>
    <!-- 操作 -->
    <template #handle="{ record }">
      <div class="table-handle-wrapper">
        <!-- 详情 -->
        <a-button type="text"
                  size="mini"
                  @click="openLogDetail(record)">
          详情
        </a-button>
      </div>
    </template>
  </a-table>
  <!-- json 查看器模态框 -->
  <json-editor-modal ref="jsonView" />
</template>

<script lang="ts">
  export default {
    name: 'userOperatorLogSimpleTable'
  };
</script>

<script lang="ts" setup>
  import type { OperatorLogQueryRequest, OperatorLogQueryResponse } from '@/api/user/operator-log';
  import { ref, onMounted, onBeforeMount } from 'vue';
  import { operatorLogModuleKey, operatorLogTypeKey, operatorRiskLevelKey, operatorLogResultKey, dictKeys } from '../types/const';
  import columns from '../types/table.columns';
  import { getLogDetail } from '../types/const';
  import useCopy from '@/hooks/copy';
  import useLoading from '@/hooks/loading';
  import { usePagination } from '@/types/table';
  import { useDictStore } from '@/store';
  import { getOperatorLogPage } from '@/api/user/operator-log';
  import { getCurrentUserOperatorLog } from '@/api/user/mine';
  import { replaceHtmlTag, clearHtmlTag } from '@/utils';
  import JsonEditorModal from '@/components/view/json-editor/json-editor-modal.vue';

  const props = defineProps({
    handleColumn: {
      type: Boolean,
      default: true
    },
    current: {
      type: Boolean,
      default: false
    },
    baseParams: {
      type: Object,
      default: () => {
        return {};
      }
    }
  });

  const pagination = usePagination();
  const { loading, setLoading } = useLoading();
  const { getDictValue } = useDictStore();
  const { copy } = useCopy();

  const jsonView = ref();
  const tableColumns = ref();
  const tableRenderData = ref<OperatorLogQueryResponse[]>([]);

  // 查看详情
  const openLogDetail = (record: OperatorLogQueryResponse) => {
    jsonView.value.open(getLogDetail(record));
  };

  // 加载数据
  const doFetchTableData = async (request: OperatorLogQueryRequest) => {
    try {
      setLoading(true);
      let rows;
      if (props.current) {
        // 查询当前用户
        const { data } = await getCurrentUserOperatorLog(request);
        rows = data;
      } else {
        // 查询所有
        const { data } = await getOperatorLogPage({ ...request, ...props.baseParams });
        rows = data;
      }
      tableRenderData.value = rows.rows.map(s => {
        return { ...s, originLogInfo: clearHtmlTag(s.logInfo) };
      });
      pagination.total = rows.total;
      pagination.current = request.page;
      pagination.pageSize = request.limit;
    } catch (e) {
    } finally {
      setLoading(false);
    }
  };

  // 切换页码
  const fetchTableData = (page = 1, limit = pagination.pageSize, form = {}) => {
    doFetchTableData({ page, limit, ...form });
  };

  defineExpose({
    fetchTableData
  });

  // 加载字典值
  onBeforeMount(async () => {
    const dictStore = useDictStore();
    await dictStore.loadKeys(dictKeys);
  });

  // 初始化
  onMounted(() => {
    let cols = columns.map(s => {
      return { ...s };
    }).filter(s => s.dataIndex !== 'username');
    if (props.handleColumn) {
      const handleCol = cols.find(s => s.dataIndex === 'handle');
      // 设置操作项宽度
      if (handleCol) {
        handleCol.width = 80;
      }
    } else {
      // 不显示操作
      cols = cols.filter(s => s.dataIndex !== 'handle');
    }
    tableColumns.value = cols;
    fetchTableData();
  });

</script>

<style lang="less" scoped>
  .operator-location {
    color: var(--color-text-2);
  }

  .operator-address {
    margin-top: 4px;
    display: inline-block;
    color: var(--color-text-3);
  }
</style>
