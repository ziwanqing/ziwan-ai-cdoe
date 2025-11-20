<template>
  <div id="userLoginPage">
    <div class="login-card">
      <h2 class="title">用户登录</h2>
      <div class="desc">不写一行代码，生成完整应用</div>

      <a-form
        :model="formState"
        :rules="rules"
        layout="vertical"
        @finish="handleSubmit"
      >
        <a-form-item label="账号" name="userAccount">
          <a-input
            v-model:value="formState.userAccount"
            placeholder="请输入账号"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="密码" name="userPassword">
          <a-input-password
            v-model:value="formState.userPassword"
            placeholder="请输入密码"
            allow-clear
          />
        </a-form-item>

        <div class="actions">
          <a-checkbox v-model:checked="rememberMe">记住我</a-checkbox>
          <RouterLink to="/user/forget" class="forget">忘记密码？</RouterLink>
        </div>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            :loading="loading"
            style="width: 100%"
          >
            登录
          </a-button>
        </a-form-item>

        <div class="tips">
          没有账号？
          <RouterLink to="/user/register">去注册</RouterLink>
        </div>
      </a-form>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 表单数据
const formState = reactive({
  userAccount: '',
  userPassword: '',
})

const rememberMe = ref(true)
const loading = ref(false)

// 校验规则
const rules = {
  userAccount: [
    { required: true, message: '请输入账号' },
    { min: 4, message: '账号不能少于 4 位' },
  ],
  userPassword: [
    { required: true, message: '请输入密码' },
    { min: 8, message: '密码不能少于 8 位' },
  ],
}

/**
 * 提交登录
 */
const handleSubmit = async (values: any) => {
  loading.value = true
  try {
    const res = await userLogin(values)
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()

      if (rememberMe.value) {
        localStorage.setItem('autoLogin', '1')
      } else {
        localStorage.removeItem('autoLogin')
      }

      message.success('登录成功')
      await router.push({ path: '/', replace: true })
    } else {
      message.error('登录失败，' + res.data.message)
    }
  } finally {
    loading.value = false
  }
}
</script>
<style scoped>
#userLoginPage {
  display: flex;
  justify-content: center;
  padding-top: 80px;
  padding-left: 24px;
  padding-right: 24px;
}

.login-card {
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

.actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.forget {
  font-size: 13px;
  color: #409eff;
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
