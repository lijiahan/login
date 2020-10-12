// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import store from './store/store.js'

import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.config.productionTip = false

Vue.use(ElementUI, {
    size: 'small'
})

// router.beforeEach((to, from, next) => {
// 	console.log(store.state.user.name)
//     if (!store.state.state) {
//         console.log('111111')
//     } else {
//         if (to.path !== '/login') {
//             next()
//         } else {
//             next(from.fullPath)
//         }
//     }
// })


/* eslint-disable no-new */
new Vue({
  el: '#app',
  store,
  router,
  components: { App },
  template: '<App/>'
})
