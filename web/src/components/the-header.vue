<template>
  <a-layout-header class="header">
    <div class="logo" />
    <div style="float: right;color: white" >
      您好:{{member.mobile}}&nbsp;&nbsp;
      <router-link to="/login">
        退出登录
      </router-link>
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys1"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="1">nav 1qq</a-menu-item>
      <a-menu-item key="2">nav 2</a-menu-item>
      <a-menu-item key="3">nav 3</a-menu-item>
    </a-menu>
  </a-layout-header>

</template>

<script>
import {defineComponent, ref} from 'vue';
import store from "@/store";
export default defineComponent({
  name:"the-header-view",
  setup(){
    let member = store.state.member;
    return{
      selectedKeys1:ref['2'],
      openKeys : ref(['sub2']),
      member
    }
  }
})

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

</style>
