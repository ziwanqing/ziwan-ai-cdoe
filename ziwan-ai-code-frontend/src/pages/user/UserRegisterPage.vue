<template>
  <div id="userRegisterPage">
    <div class="register-card">
      <h2 class="title">用户注册</h2>
      <div class="desc">不写一行代码，生成完整应用</div>

      <a-form
        :model="formState"
        :rules="rules"
        layout="vertical"
        @finish="handleSubmit"
      >
        <!-- 账号 -->
        <a-form-item label="账号" name="userAccount">
          <a-input
            v-model:value="formState.userAccount"
            placeholder="请输入账号"
            allow-clear
          />
        </a-form-item>

        <!-- 密码 -->
        <a-form-item label="密码" name="userPassword">
          <a-input-password
            v-model:value="formState.userPassword"
            placeholder="请输入密码"
            allow-clear
          />
        </a-form-item>

        <!-- 确认密码 -->
        <a-form-item label="确认密码" name="checkPassword">
          <a-input-password
            v-model:value="formState.checkPassword"
            placeholder="请再次输入密码"
            allow-clear
          />
        </a-form-item>

        <!-- 注册按钮 -->
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            :loading="loading"
            style="width: 100%"
          >
            注册
          </a-button>
        </a-form-item>

        <div class="tips">
          已有账号？
          <RouterLink to="/user/login">去登录</RouterLink>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { userRegister } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import { reactive, ref } from 'vue'

const router = useRouter()

const formState = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const loading = ref(false)

// 表单校验规则（和后端相一致）
const rules = {
  userAccount: [
    { required: true, message: '请输入账号' },
    { min: 4, message: '账号不能小于 4 位' },
  ],
  userPassword: [
    { required: true, message: '请输入密码' },
    { min: 8, message: '密码不能小于 8 位' },
  ],
  checkPassword: [
    { required: true, message: '请确认密码' },
    { min: 8, message: '密码不能小于 8 位' },
    {
      validator: async (_rule: any, value: string) => {
        if (value !== formState.userPassword) {
          return Promise.reject('两次输入密码不一致')
        }
        return Promise.resolve()
      },
    },
  ],
}

/**
 * 提交注册
 */
const handleSubmit = async (values: API.UserRegisterRequest) => {
  loading.value = true
  try {
    const res = await userRegister(values)
    if (res.data.code === 0) {
      message.success('注册成功')
      await router.push({
        path: '/user/login',
        replace: true,
      })
    } else {
      message.error('注册失败，' + res.data.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
#userRegisterPage {
  display: flex;
  justify-content: center;
  padding-top: 80px;
  padding-left: 24px;
  padding-right: 24px;
}

.register-card {
  width: 100%;
  max-width: 420px;
  background: white;
  padding: 32px;
  border-radius: 10px;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06);
}

.title {
  text-align: center;
  margin-bottom: 8px;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.desc {
  text-align: center;
  color: #888;
  margin-bottom: 20px;
}

.tips {
  text-align: center;
  margin-top: 12px;
  font-size: 14px;
  color: #555;
}

.tips a {
  color: #409eff;
}
</style>
