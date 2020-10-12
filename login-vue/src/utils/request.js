import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '@/store/store'
// 创建axios实例
const service = axios.create({
	 baseURL: 'http://localhost:9010', // api的base_url
	 timeout: 5000,     // 请求超时时间
	 validateStatus(status) {
		 switch (status) {
		 case 400:
			 Message.error('请求出错')
			 break
		 case 401:
			 Message.warning({
				 message: '授权失败，请重新登录'
			 })
			 store.commit('LOGIN_OUT')
			 setTimeout(() => {
				 window.location.reload()
			 }, 1000)
			 return
		 case 403:
			 Message.warning({
				 message: '拒绝访问'
			 })
			 break
		 case 404:
			 Message.warning({
				 message: '请求错误,未找到该资源'
			 })
			 break
		 case 500:
			 Message.warning({
				 message: '服务端错误'
			 })
			 break
		 }
		 return status >= 200 && status < 300
	 }
});
// respone拦截器
service.interceptors.response.use(
	response => {
		 /**
		 * code为非200是抛错 可结合自己业务进行修改
		 */
		const res = response.data;
		 //const res = response;
		if (res.code !== '200' && res.code !== 200) {
			if (res.code === '4001' || res.code === 4001) {
				MessageBox.confirm('用户名或密码错误，请重新登录', '重新登录', {
				confirmButtonText: '重新登录',
				cancelButtonText: '取消',
				type: 'warning'
				}).then(() => {
					store.dispatch('FedLogOut').then(() => {
					location.reload()// 为了重新实例化vue-router对象 避免bug
					})
				})
			}
		if (res.code === '4009' || res.code === 4009) {
				MessageBox.confirm('该用户名已存在，请重新注册！', '重新注册', {
				confirmButtonText: '重新注册',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				store.dispatch('FedLogOut').then(() => {
				location.reload()// 为了重新实例化vue-router对象 避免bug
				})
			})
		}
			return Promise.reject('error')
		} else {
			return response.data
		}
	},
	error => {
		if (error && error.response) {
		} else {
			 error.message = '连接服务器失败'
		}
		return Promise.reject(error.response)
	}
)
export default service