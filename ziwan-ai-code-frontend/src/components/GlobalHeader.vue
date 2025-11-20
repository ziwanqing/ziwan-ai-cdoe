<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="@/assets/logo.jpg" alt="Logo" />
            <h1 class="site-title">ziwan</h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <div class="header-middle">
          <a-menu
            v-model:selectedKeys="selectedKeys"
            mode="horizontal"
            :items="menuItems"
            @click="handleMenuClick"
          />
        </div>
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="action-area">
          <template v-if="loginUserStore.loginUser.id">
            <a-dropdown
              trigger="click"
              placement="bottomRight"
              overlayClassName="user-card-dropdown"
            >
              <div class="user-dropdown-trigger">
                <a-avatar :src="loginUserStore.loginUser.userAvatar" :size="40" />
                <span class="user-name">
                  {{ loginUserStore.loginUser.userName ?? loginUserStore.loginUser.userAccount }}
                </span>
              </div>

              <template #overlay>
                <div class="user-card">
                  <div class="user-card-header">
                    <a-avatar :src="loginUserStore.loginUser.userAvatar" :size="64" />
                    <div class="user-info">
                      <div class="user-name-big">
                        {{
                          loginUserStore.loginUser.userName ?? loginUserStore.loginUser.userAccount
                        }}
                      </div>
                      <div class="user-account">
                        账号：{{ loginUserStore.loginUser.userAccount }}
                      </div>
                    </div>
                  </div>

                  <div class="user-card-body">
                    <a-button block @click="router.push('/user/profile')"> 个人中心 </a-button>
                    <p></p>
                    <a-button block danger @click="doLogout"> 退出登录 </a-button>
                  </div>
                </div>
              </template>
            </a-dropdown>
          </template>

          <template v-else>
            <div class="user-dropdown-logion">
              <a-button type="primary" href="/user/login">登录</a-button>
            </div>
          </template>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    label: '应用管理',
    title: '应用管理',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
/* More comprehensive text color override */
.header-middle .ant-menu,
.header-middle .ant-menu-item,
.header-middle .ant-menu-item-selected,
.header-middle .ant-menu-item-active,
.header-middle .ant-menu-submenu-title {
  color: #ffffff !important;
}
.header-middle .ant-menu-item,
.header-middle .ant-menu-submenu-title {
  font-size: 30px !important;
}

/* Ensure submenu items also follow the style */
.header-middle .ant-menu-horizontal:not(.ant-menu-dark) > .ant-menu-item,
.header-middle .ant-menu-horizontal:not(.ant-menu-dark) > .ant-menu-submenu {
  color: #ffffff !important;
}

.header {
  background: linear-gradient(135deg, #1f1f3e, #4c1f7d);
  padding: 0 32px; /* 增加左右间距 */
  height: 64px;
  line-height: 64px;
  font-size: 30px; /* Add this */
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.logo {
  height: 50px;
  width: 50px;
  border-radius: 12px; /* LOGO 美化圆角 */
  object-fit: cover;
}

.site-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
}

/* a-menu 背景透明，继承 header 背景 */
.header-middle .ant-menu {
  background: transparent !important;
}

/* ==================== 登录按钮 ==================== */
.action-area .ant-btn-primary {
  background-color: #ffffff;
  color: #000000;
  border: 1px solid #d9d9d9;
  transition: all 0.3s;
}

.action-area .ant-btn-primary:hover {
  background-color: #f0f0f0;
  color: #000000;
}

/* --- 右侧用户操作栏 --- */
.action-area {
  font-size: 28px;
  font-weight: 350;
  color: #f2f2f2;
}

/* --- 用户下拉 --- */
.user-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 12px;
  transition:
    background 0.25s,
    box-shadow 0.25s;
}

.user-dropdown-trigger:hover {
  background: rgba(255, 255, 255, 0.12);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.user-name {
  font-size: 17px;
  color: #ffffff;
}

/* 下拉卡片 */
.user-card {
  width: 260px;
  padding: 18px;
  border-radius: 16px;
  backdrop-filter: blur(14px);
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.28);
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.35);
  color: #ffffff;
}

.user-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.user-dropdown-logion {
  margin-top: -5px;
}


</style>
