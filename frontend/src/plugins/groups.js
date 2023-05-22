export default {
    install: (app) => {
        var colors = [
            '#2196F3', '#32c787', '#00BCD4', '#ff5652',
            '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
        ];
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

            joinByLink: async (token) => {
                const group = await app.axios.post('/groups/join_link/' + token)
                return group.data
            },

            getGroupRole: (group, uId) => {
                const member = group.member.find(m => m.id === uId)
                if (member) {
                    return member.role
                }
                return null
            },

            /*getColor: (index) => {
                const hue = index * 30 % 360;
                const saturation = 55;
                const lightness = 30;
                return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
            },*/
            getAvatarColor: (messageSender) => {
                var hash = 0;
                for (var i = 0; i < messageSender.length; i++) {
                    hash = 31 * hash + messageSender.charCodeAt(i);
                }
                var index = Math.abs(hash % colors.length);
                return colors[index];
            },

            createInvite: async (gId, expire, maxUses, receiver) => {
                const response = await app.axios.post("/groups/invite/" + gId, {
                    expire: expire,
                    receiver: receiver,
                    maxUses: maxUses,
                })

                return response
            },

            getGroupByInviteLink: async (link) => {
                const response = await app.axios.get("/groups/invite/" + link)
                return response.data
            },
            time_ago: (time) => {
                switch (typeof time) {
                    case 'number':
                        break;
                    case 'string':
                        time = +new Date(time);
                        break;
                    case 'object':
                        if (time.constructor === Date) time = time.getTime();
                        break;
                    default:
                        time = +new Date();
                }
                const time_formats = [
                    [60, 'seconds', 1], // 60
                    [120, '1 minute ago', '1 minute from now'], // 60*2
                    [3600, 'minutes', 60], // 60*60, 60
                    [7200, '1 hour ago', '1 hour from now'], // 60*60*2
                    [86400, 'hours', 3600], // 60*60*24, 60*60
                    [172800, 'Yesterday', 'Tomorrow'], // 60*60*24*2
                    [604800, 'days', 86400], // 60*60*24*7, 60*60*24
                    [1209600, 'Last week', 'Next week'], // 60*60*24*7*4*2
                    [2419200, 'weeks', 604800], // 60*60*24*7*4, 60*60*24*7
                    [4838400, 'Last month', 'Next month'], // 60*60*24*7*4*2
                    [29030400, 'months', 2419200], // 60*60*24*7*4*12, 60*60*24*7*4
                    [58060800, 'Last year', 'Next year'], // 60*60*24*7*4*12*2
                    [2903040000, 'years', 29030400], // 60*60*24*7*4*12*100, 60*60*24*7*4*12
                    [5806080000, 'Last century', 'Next century'], // 60*60*24*7*4*12*100*2
                    [58060800000, 'centuries', 2903040000] // 60*60*24*7*4*12*100*20, 60*60*24*7*4*12*100
                ];
                let seconds = (+new Date() - time) / 1000,
                    token = 'ago',
                    list_choice = 1;

                if (Math.round(seconds) === 0) {
                    return 'Just now'
                }
                if (seconds < 0) {
                    seconds = Math.abs(seconds);
                    token = 'from now';
                    list_choice = 2;
                }
                let i = 0,
                    format;
                while (format = time_formats[i++])
                    if (seconds < format[0]) {
                        if (typeof format[2] == 'string')
                            return format[list_choice];
                        else
                            return Math.floor(seconds / format[2]) + ' ' + format[1] + ' ' + token;
                    }
                return time;
            }
        }

        app.config.globalProperties.$groups = w
    }
}