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

            getAccounts: async () => {
                const accounts = await app.axios.get('/accounts/all')
                return accounts.data
            },

            getAccountsCached: () => {
                return users
            },

            getAcountCached: async (uId) => {
                return await users.find(u => u.uid === uId)
            }
        }

        w.getAccounts().then(res => {
            users.push(...res)
        })

        app.config.globalProperties.$users = w
    }
}