<template>
  <div class="terminal-setting-block">
    <!-- 顶部 -->
    <div class="terminal-setting-subtitle-wrapper">
      <h3 class="terminal-setting-subtitle">
        主题设置
      </h3>
    </div>
    <!-- 加载中 -->
    <a-skeleton v-if="loading"
                class="skeleton-wrapper"
                :animation="true">
      <a-skeleton-line :rows="8" />
    </a-skeleton>
    <!-- 内容区域 -->
    <div v-else class="terminal-setting-body terminal-theme-container">
      <!-- 提示 -->
      <a-alert class="mb16">选择后会立刻保存, 刷新页面后生效</a-alert>
      <!-- 终端主题 -->
      <div class="theme-row"
           v-for="rowIndex in themes.length / 2"
           :key="rowIndex">
        <a-card v-for="(theme, colIndex) in [themes[(rowIndex - 1) * 2], themes[(rowIndex - 1) * 2 + 1]]"
                :key="theme.name"
                class="terminal-theme-card simple-card"
                :class="{
                  'terminal-theme-card-check': theme.name === currentThemeName
                }"
                :title="theme.name"
                :style="{
                  background: theme.schema.background,
                  marginRight: colIndex === 0 ? '16px' : 0
                }"
                :header-style="{
                  color: theme.dark ? 'rgba(255, 255, 255, .8)' : 'rgba(0, 0, 0, .8)',
                  userSelect: 'none'
                }"
                @click="selectTheme(theme)">
          <!-- 样例 -->
          <terminal-example :schema="theme.schema" />
          <!-- 选中按钮 -->
          <icon-check class="theme-check-icon"
                      v-show="theme.name === currentThemeName" />
        </a-card>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'TerminalThemeBlock'
  };
</script>

<script lang="ts" setup>
  import type { TerminalTheme } from '@/api/asset/host-terminal';
  import { useTerminalStore } from '@/store';
  import { TerminalPreferenceItem } from '@/store/modules/terminal';
  import { onMounted, ref } from 'vue';
  import { getTerminalThemes } from '@/api/asset/host-terminal';
  import { getPreference } from '@/api/user/preference';
  import useLoading from '@/hooks/loading';
  import TerminalExample from '../terminal-example.vue';

  const { updateTerminalPreference } = useTerminalStore();
  const { loading, setLoading } = useLoading();

  const currentThemeName = ref();
  const themes = ref<Array<TerminalTheme>>([]);

  // 选择主题
  const selectTheme = async (theme: TerminalTheme) => {
    currentThemeName.value = theme.name;
    await updateTerminalPreference(TerminalPreferenceItem.THEME, theme);
  };

  // 加载用户主题
  onMounted(async () => {
    try {
      const { data } = await getPreference<Record<string, any>>('TERMINAL', [TerminalPreferenceItem.THEME]);
      currentThemeName.value = data[TerminalPreferenceItem.THEME]?.name;
    } catch (e) {
    }
  });

  // 加载主题列表
  onMounted(async () => {
    setLoading(true);
    try {
      // 加载全部主题
      const { data } = await getTerminalThemes();
      themes.value = data;
    } catch (e) {
    } finally {
      setLoading(false);
    }
  });

</script>

<style lang="less" scoped>
  @terminal-width: 458px;
  @terminal-height: 138px;

  .terminal-theme-container {
    flex-direction: column;

    .theme-row {
      display: flex;
      margin-bottom: 16px;
    }
  }

  .terminal-theme-card {
    width: @terminal-width;
    height: calc(@terminal-height + 44px);
    border: 2px solid var(--color-border);
    cursor: pointer;

    :deep(.arco-card-header) {
      padding: 4px 16px;
      height: 40px;
      border-bottom: .5px solid var(--color-border-2);

      &-title {
        color: unset;
        font-size: 16px;
        font-weight: 600;
      }
    }

    :deep(.arco-card-body) {
      height: @terminal-height;
      padding: 0;
      display: flex;
      position: relative;

      .theme-check-icon {
        display: flex;
        position: absolute;
        color: #FFF;
        right: 0;
        bottom: 0;
        z-index: 10;
      }
    }

    &-check, &:hover {
      border: 2px solid rgb(var(--blue-6));
    }

    &-check::after {
      content: '';
      position: absolute;
      right: 0;
      bottom: 0;
      width: 0;
      height: 0;
      border-bottom: 28px solid rgb(var(--blue-6));
      border-left: 28px solid transparent;
    }
  }

</style>
