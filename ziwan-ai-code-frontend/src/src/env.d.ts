/// <reference types="vite/client" />

declare module "*.vue" {
  import { DefineComponent } from "vue"
  const component: DefineComponent<{}, {}, any>
  export default component
}

// 如果你用到了 @ 别名，也可以加上：
declare module "@/*";
