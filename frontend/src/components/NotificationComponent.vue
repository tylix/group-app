<template>
    <div class="notify__body">
        <p class="read__all" @click="this.readAll()">Read all</p>
        <i v-if="!this.notifications" class="loader bx bx-loader-alt bx-spin" />
        <div class="notifications" v-else>
            <div class="notification" v-for="(notification, index) in this.notifications" :key="index">
                <div class="notify__title" @click="this.read(notification)">
                    <i v-if="!notification.read" class="bx bxs-circle"></i>
                    <p>{{ notification.title }}</p>
                    <p class="notify__timestamp">{{ this.$groups.time_ago(notification.timestamp) }}</p>
                </div>
                <div :class="this.selectedNotification === notification.id ? 'notify__body' : 'notify__body__c'">
                    {{ notification.body }}
                    <button v-if="notification.link"
                        @click="this.$router.push(notification.link.split('.com/')[1])">View</button>
                </div>
                <hr v-if="index !== this.notifications.length - 1" />
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'NotificationComponent',
    data() {
        return {
            notifications: undefined,
            selectedNotification: undefined
        }
    },
    methods: {
        readAll() {
            this.$users.readAllNotifications().then(res => {
                this.notifications = res.body
                    this.notifications.sort((a, b) => {
                        return b.timestamp > a.timestamp
                    })
            })
        },

        read(notification) {
            this.selectedNotification ? this.selectedNotification = undefined : this.selectedNotification = notification.id
            if (!notification.read)
                this.$users.readNotification(notification.id).then(res => {
                    this.notifications = res.body
                    this.notifications.sort((a, b) => {
                        return b.timestamp > a.timestamp
                    })
                })
        }
    },
    mounted() {
        this.$users.getNotifications().then(res => {
            this.notifications = res
                    this.notifications.sort((a, b) => {
                        return b.timestamp > a.timestamp
                    })
        })
        this.$websocket.registerNotificationSub((notification) => {
            this.notifications.unshift(notification)
        })
    }
}
</script>

<style>
.notifications {
    overflow-y: scroll;
    height: 100%;
}

.loader {
    position: absolute;
    left: 50%;
    top: 10%;
}

.notification {
    padding: 10px;
}

.notification:hover {
    background-color: var(--color-background-modern-mute);
}

.notifications hr {
    margin: 10px 0;
    border-color: var(--color-background-modern);
}

.notify__title {
    cursor: pointer;
    font-weight: bold;
    font-size: 18px;
    margin-bottom: 5px;
    display: flex;
    align-items: center;
    gap: 10px;
}

.notify__title:hover {
    color: var(--color-text-muted);
    transition: color 0.5s;
}

.notify__title i {
    font-size: 10px;
    color: var(--color-red);
}

.notify__body {
    height: 100%;
    display: flex;
    flex-direction: column;
    opacity: 1;
    transition: opacity 1s;
    font-size: 15px;
}

.notify__body__c {
    opacity: 0;
    display: none;
    transition: opacity 1s;
}

.notify__body button {
    background-color: var(--color-red);
    color: var(--color-text);
    border: none;
    padding: 5px 10px;
    border-radius: 5px;
    cursor: pointer;
    margin-top: 10px;
}

.notify__timestamp {
    font-size: 12px;
    color: var(--color-text-muted);
    margin-left: auto;
}

.read__all {
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 10px;
    cursor: pointer;
}
</style>