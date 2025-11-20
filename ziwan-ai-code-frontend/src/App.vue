<template>
  <BasicLayout />
</template>

<script setup>
import BasicLayout from '@/layouts/BasicLayout.vue'

import { useLoginUserStore } from '@/stores/loginUser'
const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser();
</script>

<style>

</style>
