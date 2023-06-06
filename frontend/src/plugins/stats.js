export default {
    install: (app) => {
        const w= {
            getStats: async (from, to) => {
                const request = await app.axios.get(`/statistics?from=${from}&to=${to}`)
                return request.data
            }
        }

        app.config.globalProperties.$stats = w
    }

}