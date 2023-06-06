export default {
    install: (app) => {

        const users = []

        const w = {
            getAccount: async (uId) => {
                const account = await app.axios.get('/accounts/' + uId)
                return account.data
            },

            getByUsername: async (username) => {
                const account = await app.axios.get('/accounts/username/' + username)
                return account.data
            },

            search: async (username) => {
                const account = await app.axios.get('/accounts/search/' + username)
                return account.data
            },

            getUser: async () => {
                const account = await app.axios.get('/accounts')
                return account.data
            },

            getNotifications: async () => {
                const notifications = await app.axios.get('/notifications')
                return notifications.data
            },

            readAllNotifications: async () => {
                const request = await app.axios.post('/notifications/readAll')
                return request.data
            },

            readNotification: async (nId) => {
                const request = await app.axios.post('/notifications/read/' + nId)
                return request.data
            },

            getStatus: async (uId) => {
                const status = await app.axios.get('/accounts/status/' + uId)
                return status.data  
            },

            getAccounts: async () => {
                const accounts = await app.axios.get('/accounts/all')
                return accounts.data
            },

            getAccountsCached: () => {
                return users
            },

            getAcountCached: async (uId) => {
                const user = await users.find(u => u.uid === uId)
                if (user)
                    return user
                const loaded = await w.getAccount(uId)
                users.push(loaded)
                return loaded
            },
            loadUser() {
                if (localStorage.getItem('token'))
                    w.getUser().then(res => {
                        users.push(...res)
                    })
            }
        }

        w.loadUser()

        app.config.globalProperties.$users = w
    }
}