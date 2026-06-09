import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    adminInfo: JSON.parse(localStorage.getItem('adminInfo') || '{}'),
    isAdmin: localStorage.getItem('isAdmin') === 'true'
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER(state, user) {
      state.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    CLEAR_USER(state) {
      state.token = ''
      state.user = {}
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
    SET_ADMIN_INFO(state, adminInfo) {
      state.adminInfo = adminInfo
      state.isAdmin = true
      localStorage.setItem('adminInfo', JSON.stringify(adminInfo))
      localStorage.setItem('isAdmin', 'true')
    },
    CLEAR_ADMIN_INFO(state) {
      state.adminInfo = {}
      state.isAdmin = false
      state.token = ''
      localStorage.removeItem('adminInfo')
      localStorage.removeItem('isAdmin')
      localStorage.removeItem('token')
    }
  },
  actions: {
    login({ commit }, data) {
      commit('SET_TOKEN', data.token)
      commit('SET_USER', data.user)
    },
    logout({ commit }) {
      commit('CLEAR_USER')
    },
    adminLogin({ commit }, data) {
      commit('SET_TOKEN', data.token)
      commit('SET_ADMIN_INFO', data.admin)
    },
    adminLogout({ commit }) {
      commit('CLEAR_ADMIN_INFO')
    }
  },
  getters: {
    isLoggedIn: state => !!state.token,
    user: state => state.user,
    isAdmin: state => state.isAdmin,
    adminInfo: state => state.adminInfo
  }
})
