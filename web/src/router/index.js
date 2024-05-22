import { createRouter, createWebHistory } from 'vue-router'
import store from "@/store";
import {notification} from "ant-design-vue";

const routes = [
   {
    path: '/login',
    component: () => import( '../views/login.vue')
  }, {
    path: '/',
    component: () => import( '../views/main.vue'),
        meta:{
        loginRequired: true
        },
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})
// 路由登录拦截
router.beforeEach((to, from, next) => {
    // 要不要对meta.loginRequired属性做拦截
    if(to.matched.some(function (item){
        console.log(item,"是否需要登录校验",item.meta.loginRequired||false);
        return item.meta.loginRequired
    })) {
        const user = store.state.member;
        console.log("页面登录校验开始",user);
        if (!user.token){
            console.log("用户未登录或登录超时");
            notification.error({description:"用户未登录或登录超时"});
            next('/login');
        }else {
            next();
    }
    }else {
        next();
    }
});
export default router
