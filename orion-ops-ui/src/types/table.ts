import type { PaginationProps, TableRowSelection } from '@arco-design/web-vue';
import { reactive } from 'vue';
import { useAppStore } from '@/store';
import { isNumber } from '@/utils/is';
import { TablePageSizeOptions } from '@/types/const';

/**
 * 创建列表分页
 */
export const usePagination = (ext?: PaginationProps): PaginationProps => {
  const appStore = useAppStore();
  return reactive({
    total: 0,
    current: 1,
    pageSize: isNumber(appStore.defaultTablePageSize) ? appStore.defaultTablePageSize : TablePageSizeOptions[0],
    showTotal: true,
    showPageSize: true,
    pageSizeOptions: TablePageSizeOptions,
    ...ext
  });
};

/**
 * 创建行选择器
 */
export const useRowSelection = (ext?: TableRowSelection): TableRowSelection => {
  return reactive({
    type: 'checkbox',
    showCheckedAll: true,
    onlyCurrent: true,
    ...ext
  });
};
