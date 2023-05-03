export default {
    install: (app) => {
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
            }
        }

        app.config.globalProperties.$users = w
    }
}