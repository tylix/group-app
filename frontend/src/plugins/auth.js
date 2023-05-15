export default {
    install: (app) => {

        const w = {
            login: async (username, password) => {
                const token = await app.axios.post("/auth/authenticate", {username, password})
                if (token.data.token) {
                    const parsedToken = w.parseJwt(token.data.token)
                    if (parsedToken === null) return false
                    if (parsedToken.tfa) {
                        app.config.globalProperties.$router.push('/otp?token=' + token.data.token);
                        return true;
                    }
                    w.setToken(token)
                    return true
                }
                return false
            },
            signInWith: async (provider, code) => {
                const response = await app.axios.post("/auth/signinwith?provider=" + provider + "&code=" + code)
                if (response.data.token) {
                    w.setToken(response)
                    return true
                }
                return false
            },
            signup: async (username, password, email) => {
                const request = await app.axios.post("/auth/register", {
                    username,
                    password,
                    firstName: '',
                    lastName: '',
                    email
                })
                if (request.data.token) {
                    w.setToken(request, false)
                    app.config.globalProperties.$router.push('/otp?token=' + request.data.token);
                    return true
                }
                return false
            },
            setToken: (token, redirect = true) => {
                localStorage.setItem('token', token.data.token)
                app.config.globalProperties.$users.getUser().then(res => {
                    localStorage.setItem('user', JSON.stringify(res))
                    if (redirect)
                        app.config.globalProperties.$router.push('/dashboard');
                    app.config.globalProperties.$toast.showNotification('Welcome back, ' + res.username, 2000, 'info')
                    app.config.globalProperties.$websocket.connect()
                })
            },
            isOtpEnabled: () => {
                return w.parseJwt(localStorage.getItem('token')).otp !== undefined
            },
            generateOTPQR: async () => {
                const request = await app.axios.post("/twofactor/generate")
                return request.data
            },
            verifyCode: async (code) => {
                const request = await app.axios.post("/twofactor/verify", {code})
                return request.data
            },
            sendOtp: async (code, token) => {
                const request = await app.axios.post("/auth/otp", {token, code})
                return request.data;
            },
            getStatus: async (token) => {
                try {
                    const {data} = await app.axios.post('/auth/status', {token})
                    return data
                } catch (e) {
                    return {status: 'error'}
                }
            },
            isAuthenticated: () => {
                const token = localStorage.getItem('token')
                return token !== null && (w.parseJwt(token) !== null && w.parseJwt(token).exp > new Date().getTime() / 1000) && !w.parseJwt(token).tfa
            },
            changeRoute: (to) => {
                if ((to.path === '/login' || to.path === 'otp') && w.isAuthenticated()) {
                    return {path: '/dashboard'}
                }
                if (to.meta.auth === undefined ? !w.isAuthenticated() : to.meta.auth && !w.isAuthenticated()) {
                    return {path: '/login'}
                }
                return true;
            },
            logout: async () => {
                await app.axios.post("/auth/logout")
                localStorage.removeItem('token')
                app.config.globalProperties.$router.push('/login')
            },
            parseJwt: (token) => {
                if (token === undefined) return null
                const base64Url = token.split('.')[1];
                if (base64Url === undefined) return null
                const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function (c) {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                }).join(''));
                return JSON.parse(jsonPayload);
            },

        }

        app.config.globalProperties.$auth = w
    }
}