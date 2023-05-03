export default {
    install: (app) => {
        const w = {
            getGroupById: async (gId) => {
                const group = await app.axios.get('/groups/gid/' + gId)
                return group.data
            },

            getGroupByUser: async () => {
                const group = await app.axios.get('/groups')
                return group.data
            },

            createGroup: async (data) => {
                const group = await app.axios.post('/groups', data)
                return group.data
            },

            deleteGroup: async (gId) => {
                const group = await app.axios.delete('/groups/' + gId)
                return group.data
            },

            leaveGroup: async (gId) => {
                const group = await app.axios.post('/groups/leave/' + gId)
                return group.data
            },

            updateGroup: async (data) => {
                const group = await app.axios.post('/groups/update', data)
                return group.data
            },

            isInvited: (group, user) => {
                return group.invited.find(u => u.id === user.uid) !== undefined
            },

            getRequests: async (uId) => {
                const group = await app.axios.get('/groups/requests/' + uId)
                return group.data
            },

            joinGroup: async (gId) => {
                const group = await app.axios.post('/groups/join/' + gId)
                return group.data
            },

            getGroupRole: (group, uId) => {
                const member = group.member.find(m => m.id === uId)
                if (member) {
                    return member.role
                }
                return null
            },

            getColor: (index) => {
                const hue = index * 30 % 360;
                const saturation = 55;
                const lightness = 30;
                return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
            },

            getColorByMember: (group, uId) => {
                const index = group.member.findIndex(m => m.id === uId) + 1
                console.log(uId, index)
                return w.getColor(index)
            }
        }

        app.config.globalProperties.$groups = w
    }
}