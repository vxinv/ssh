import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import api from './service';
import contentmenu from 'v-contextmenu'
import 'v-contextmenu/dist/index.css'
import store from './store'
import Vuex from 'vuex'
import VueClipboard from 'vue-clipboard2'
/*
* cnpm install --save axios
* cnpm install --save element-ui
* -- cnpm install --save element-ui/lib/theme-chalk/index.css
* cnpm install --save vue-router
* 右键菜单
* cnpm i v-contextmenu -S
* cnpm i -S v-contextmenu
* cnpm install --save vue-runtime-helpers
* */
Vue.prototype.$api = api;

Vue.config.productionTip = false

/* elementUI */
Vue.use(ElementUI);

Vue.use(contentmenu);

// 添加vuex
Vue.use(Vuex)

Vue.use(VueClipboard)



router.beforeEach((to, from, next) => {
  if(to.meta.title) {
    document.title = to.meta.title;
  }
  next();
});

// // bus全局事件,总线配置
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
