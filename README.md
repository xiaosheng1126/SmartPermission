# SmartPermission
一个支持任何地方请求权限的开源库 不需要依赖任何Activity,Fragment,主要以回调形式来实现

## 使用方式

### 1.引入 SmartPermission   implementation "cn.dxy.smart:smart-permission:xxxx"

### 2.如何调用
    ``` new AspirinPermissionIntercept()).granted(() ->showShort("权限被允许")).denied(() -> showShort("权限被禁止")).build().request()  ```

