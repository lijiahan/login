  import { login,regist,logout } from '@/api/login'
  import { setName,setToken } from '@/utils/auth'
  const user = {
	  namespaced: true,
	  state: {
	   name:'',
	   token:''
	  },
	  mutations: {
	   SET_NAME: (state, name) => {
		   state.name = name;
	   },
	   SET_TOKEN: (state, token) => {
		   state.token = token;
		   setToken(token);
	   }
	  },
	  actions: {
		  // 登录
		  Login({ commit }, userInfo) {
			  const name = userInfo.username;
			  const password = userInfo.password;
			  return new Promise((resolve, reject) => {
				  login(name, password).then(response => {
					  const data = response.data;
					  commit('SET_NAME', data.name);
					  commit('SET_TOKEN', data.token);
					  setName(data.name);
					  setToken(data.token);
					  resolve(response); }
				  ).catch(error => { reject(error) }) })
		  },
		   // 注册 
		  Regist({ commit }, userInfo) {
			  const name= userInfo.name.trim();
			  const password = userInfo.password.trim();
			  return new Promise((resolve, reject) => {
				  regist(name, password).then(response => {
					  const data = response.data; 
					  commit('SET_NAME', data.name); 
					  commit('SET_TOKEN', data.token);
					  setName(data.name);setToken(data.token);
					  }).catch(error => { reject(error) }) }) 
		  }, 
		  // 登出
		  LogOut({ commit, state }) {
			  return new Promise((resolve, reject) => {
				  logout().then(response => {
					  commit('SET_NAME', '');
					  commit('SET_TOKEN', '');
					  setName('');
					  setToken(false);
					  resolve(response);
					  }).catch(error => { reject(error) }) })
	      },
		  // 前端 登出
		  FedLogOut({ commit }) {
			  return new Promise(resolve => {
				  setToken(false);
				  commit('SET_TOKEN', false);
				  resolve()
				  })
	      }
	   }
  }
  export default user