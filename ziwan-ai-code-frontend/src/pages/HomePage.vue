<template>
  <div id="about">这是 About 页面</div>
</template>

<script setup>
import { health } from '@/api/healthController.ts'

health().then((res) => {
  console.log(res)
})
</script>
