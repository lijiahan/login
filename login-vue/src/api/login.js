import request from '@/utils/request'
export function login(name,password) {
	return request({
		url: '/User/Login',
		method: 'post',
		data: {
			'name': name,
			'password': password
		} 
	})
}

export function regist(name,password) {
	return request({
		url: '/User/regist',
		method: 'get',
		params: {
		 name: name,
		 password:password
		} 
	})
}

export function logout(name) {
	return request({
		url: '/User/logout',
		method: 'get',
		params: {
		  name: name
		} 
	})
}