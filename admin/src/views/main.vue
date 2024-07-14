<template>
  <a-layout id="components-layout-demo-top-side" >
    <TheHeaderView></TheHeaderView>
      <a-layout style="padding: 24px 0; background: #fff">

        <the-sider></the-sider>
        <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
         <router-view></router-view>
        </a-layout-content>
      </a-layout>
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

</style>