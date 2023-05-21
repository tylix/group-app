<template>
    <div>
        <i v-if="!this.notifications" class="bx bx-loader-alt bx-spin" />
        <div v-else v-for="(notification, index) in this.notifications" :key="index">
            <div>
                {{ notification.title }}
            </div>
            <div>
                {{ notification.body }}
            </div>
            <button v-if="notification.link" @click="this.$router.push(notification.link.split('.com/')[1])">View</button>
        </div>
    </div>
</template>

<script>
export default {
    name: 'NotificationComponent',
    data() {
        return {
            notifications: undefined
        }
    },
    methods: {
        
    },
    mounted() {
        this.$users.getNotifications().then(res => {
            this.notifications = res
        })
    }
}
</script>