<template>
  <a-layout id="components-layout-demo-top-side" >
    <TheHeaderView></TheHeaderView>
      <a-layout style="padding: 24px 0; background: #fff">
        <the-sider></the-sider>
        <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
         <router-view></router-view>
        </a-layout-content>
      </a-layout>
    <a-layout-footer style="text-align: center">
      Ant Design ©2018 Created by Ant UED
    </a-layout-footer>
  </a-layout>
</template>
<script>
import TheHeaderView from "@/components/the-header.vue";
import TheSider from "@/components/the-sider.vue";
import {defineComponent, } from "vue";
export default defineComponent({
  components: {
    TheHeaderView,
    TheSider,
  },
  setup(){
  },
});
</script>
<style scoped>
#components-layout-demo-top-side .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

.site-layout-background {
  background: #fff;
}
</style>