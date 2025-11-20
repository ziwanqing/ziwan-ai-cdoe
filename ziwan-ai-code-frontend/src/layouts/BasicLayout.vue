<template>
  <a-layout class="basic-layout">
    <GlobalHeader />

    <a-layout-content class="content-wrapper">
      <div class="inner-content">
        <router-view />
      </div>
    </a-layout-content>

    <GlobalFooter />
  </a-layout>
</template>

<script setup>
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(135deg, #1f1f3e, #4c1f7d); /* 深蓝到紫色渐变 */
  color: #ffffff;
}

.content-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 60px 24px;
}

.inner-content {
  width: 100%;
  max-width: 1200px;
}
</style>
