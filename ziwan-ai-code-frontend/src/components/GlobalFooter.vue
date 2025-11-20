<template>
  <a-layout-footer class="global-footer">
    <div class="footer-inner">
      <span>© {{ year }} 紫菀</span>
    </div>
  </a-layout-footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
.global-footer {
  background: rgba(0, 0, 0, 0.3); /* 半透明深色 */
  text-align: center;
  padding: 24px 0;
  color: #ffffff;
  font-size: 14px;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
