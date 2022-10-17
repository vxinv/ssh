import Vue from 'vue'
import Vuex from 'vuex'


Vue.use(Vuex)

const store = new Vuex.Store({
    state: {
        theme: {},
    },
    mutations: {
        setTheme(state, theme) {
           // console.log("fontsize",theme)
            state.theme = theme
        },

    },
    actions: {
        getTheme(context,changeSetting) {
            //console.log("vuex",context)
            Vue.prototype.$api.get('ecs/theme', {}, (res) => {
                if (res !== undefined && res.status !== undefined && res.status === 200) {
                    context.commit('setTheme',res.data)
                    changeSetting(res.data)
                } else {
                    this.openLayer('消息', res.data, 'error');
                }
            });
        }
    }
})
export default store