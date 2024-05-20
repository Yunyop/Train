import { createStore } from 'vuex'
const MEMBER="MEMBER"
export default createStore({
  state: {
    // 避免空指针异常
    member:window.SessionStorage.get(MEMBER)||{}
  },
  getters: {
  },
  mutations: {
    setMember (state, member) {
      state.member=member;
      window.SessionStorage.set(MEMBER,member);
    }
  },
  actions: {
  },
  modules: {
  }
})
