import {createApp} from 'vue'
import App from './App.vue'

import router from "@/router/router.js";
import auth from "@/plugins/auth.js";
import users from "@/plugins/users";
import groups from "@/plugins/groups";
import toast from "@/plugins/toast";

import axios from 'axios'
import VueAxios from 'vue-axios'
import VueCryptojs from 'vue-cryptojs/src'

import './assets/main.css'


export const app = createApp(App)

axios.defaults.baseURL = 'https://api.maximilianwiegmann.com'
//axios.defaults.baseURL = 'http://127.0.0.1:8080'

axios.interceptors.request.use(async function (config) {
    const token = localStorage.getItem('token')
    if (token !== undefined && token !== null) {
        config.headers["Authorization"] = "Bearer " + token;
    }
    return config;
});

app.use(VueAxios, axios)
app.use(router)
app.use(auth)
app.use(users)
app.use(groups)
app.use(toast)
app.use(VueCryptojs)

router.isReady().then(() => {
    app.mount('#app')
})
