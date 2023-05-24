<template>
    <p class="chat__connecting" v-if="!this.connected">Connecting{{ this.dots }}</p>
    <div class="expanded" v-if="this.expanded">
        <div class="chat__body">
            <div ref="chat" class="chat__messages">
                <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
                <div v-else>
                    <div class="chat__message" v-for="(message, index) in this.messages" :key="index">
                        <UsernameComponent class="chat__user" :user="message.name === 'You' ? this.getUser() : message.name"
                            v-if="index === 0 || this.messages[index - 1].userId !== message.userId"
                            :style="{ color: this.$groups.getAvatarColor(message.userId) }" />
                        <div>
                            <div class="chat__line"
                                :style="{ backgroundColor: this.$groups.getAvatarColor(message.userId) }" />
                            <p class="chat__content">{{ message.content }}</p>

                            <p class="hover">
                                {{ this.$groups.time_ago(message.timestamp) }}
                            </p>
                        </div>
                    </div>
                    <div class="chat__queue" v-for="(message, index) in this.messageQueue" :key="index">
                        {{ message }}
                    </div>
                </div>
            </div>

            <div class="chat__input">
                <textarea class="chat__textarea" placeholder="Type a message" v-model="this.message"
                    @keyup.enter.shift="this.sendMessage" />
                <button class="chat__send" @click="this.sendMessage">Send</button>
            </div>
        </div>
    </div>
    <div class="collapsed" v-else>
        <div class="chat__header">
            <h3 class="chat__title">Chat</h3>
        </div>
        <div class="chat__body">
            <div ref="chat" class="chat__messages">
                <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
                <div v-else>
                    <div class="chat__message" v-for="(message, index) in this.messages" :key="index">
                        <UsernameComponent class="chat__user" :user="message.name === 'You' ? this.getUser() : message.name"
                            v-if="index === 0 || this.messages[index - 1].userId !== message.userId"
                            :style="{ color: this.$groups.getAvatarColor(message.userId) }" />
                        <div>
                            <div class="chat__line"
                                :style="{ backgroundColor: this.$groups.getAvatarColor(message.userId) }" />
                            <p class="chat__content">{{ message.content }}</p>

                            <p class="hover">
                                {{ this.$groups.time_ago(message.timestamp) }}
                            </p>
                        </div>
                    </div>
                    <div class="chat__queue" v-for="(message, index) in this.messageQueue" :key="index">
                        {{ message }}
                    </div>
                </div>
            </div>
            <div class="chat__input">
                <textarea class="chat__textarea" placeholder="Type a message" v-model="this.message"
                    @keyup.enter.shift="this.sendMessage" />
                <button class="chat__send" @click="this.sendMessage">Send</button>
            </div>
        </div>
    </div>
</template>

<script>
import UsernameComponent from "@/components/UsernameComponent.vue";

export default {
    name: "ChatComponent",
    components: { UsernameComponent },
    props: {
        group: {
            type: Object,
            required: true
        },
        expanded: {
            type: Boolean,
            required: true
        }
    },
    data() {
        return {
            message: undefined,
            messages: [],
            loading: false,
            connected: false,
            messageQueue: [],
            timer: undefined,
            dots: ''
        }
    },
    mounted() {
        if (this.group) {
            this.loading = true
            let length = this.group.messages.length;
            this.group.messages.forEach(message => {
                const newMessage = {
                    content: this.decrypt(message.content, import.meta.env.VITE_PUB_KEY).toString(this.$CryptoJS.enc.Utf8),
                    timestamp: message.timestamp,
                    userId: message.userId,
                    name: '',
                    username: ''
                }
                if (message.userId === this.getUser().uid) {
                    newMessage.name = 'You';
                    newMessage.username = this.getUser().username
                    this.messages.push(newMessage)
                    this.checkMessages(length)
                } else {
                    this.$users.getAcountCached(message.userId).then(user => {
                        if (user === undefined) {
                            length--;
                        } else {
                            newMessage.name = user;
                            this.messages.push(newMessage)
                        }
                        this.checkMessages(length)
                    });
                }
            });
            if (this.group.messages.length === 0) this.loading = false
        }
        this.connect()
    },
    unmounted() {
    },
    beforeUnmount() {
    },
    methods: {
        checkMessages(length) {
            if (length === this.messages.length) {
                this.messages.sort((a, b) => {
                    return a.timestamp - b.timestamp
                })
                this.scrollDown()
                this.loading = false
            }
        },
        scrollDown() {
            const chat = this.$refs.chat;
            this.$nextTick(() => {
                chat.scrollTop = chat.scrollHeight;
            });
        },
        getUser() {
            return JSON.parse(localStorage.getItem('user'))
        },
        connect() {
            if (!this.$websocket.isConnected()) {
                this.timer = setInterval(() => {
                    if (this.$websocket.isConnected()) {
                        this.connect()
                        clearInterval(this.timer)
                    } else {
                        this.dots += '.'
                        if (this.dots.length > 3) this.dots = ''
                    }
                }, 500)
                return
            }
            this.$websocket.subscribeChat(this.group.id, this.onMessageReceived, () => this.connected = true)
        },
        sendMessage() {
            if (this.message === undefined || this.message === '' || this.message === null || this.message === ' ')
                return;
            const data = JSON.stringify({
                userId: this.getUser().uid,
                type: 'CHAT',
                content: this.encrypt(this.message),
                timestamp: Date.now(),
                username: this.getUser().username
            })
            this.message = undefined;
            if (!this.$websocket.isConnected() || !this.connected) {
                this.messageQueue.push(data);
                this.scrollDown()
                return;
            }
            this.$websocket.send("/app/chat/" + this.group.id + "/sendMessage", data);
        },
        onMessageReceived(payload) {
            const message = JSON.parse(payload.body);
            if (message.userId && message.type && message.type === 'CHAT') {
                const newMessage = {
                    content: this.decrypt(message.content),
                    timestamp: message.timestamp,
                    userId: message.userId,
                    name: '',
                    username: ''
                }
                if (message.userId === this.getUser().uid) {
                    newMessage.name = 'You';
                    newMessage.username = this.getUser().username
                    this.messages.push(newMessage)
                    this.scrollDown()
                } else {
                    this.$users.getAccount(message.userId).then(user => {
                        newMessage.name = user;
                        newMessage.username = user.username
                        this.messages.push(newMessage)
                        this.scrollDown()
                    });
                }
            } else {
                console.log(message)
            }
        },
        decrypt(message) {
            return this.$CryptoJS.AES.decrypt(message, import.meta.env.VITE_PUB_KEY).toString(this.$CryptoJS.enc.Utf8)
        },
        encrypt(message) {
            const secretKey = import.meta.env.VITE_PUB_KEY
            return this.$CryptoJS.AES.encrypt(message, secretKey).toString();
        },
        disconnect() {
            if (this.stompClient) {
                this.stompClient.disconnect();
            }
        },

    },
    watch: {
        expanded: function (val) {
            if (val)
                setTimeout(() => {
                    this.scrollDown()
                }, 100)
            else
                setTimeout(() => {
                    this.scrollDown()
                }, 500);
        },
        loading: function (val) {
            this.scrollDown()
        },
        connected: function (val) {
            if (val) {
                this.messageQueue.forEach(message => {
                    this.$websocket.send("/app/chat/" + this.group.id + "/sendMessage", message);
                })
                this.messageQueue = [];
                clearInterval(this.timer)
                this.timer = undefined;
            }
        }
    }
}
</script>

<style>
.chat__header {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 0 25px;
}

.chat__title {
    font-size: 17px;
    padding: 0 10px;
    color: var(--color-text-muted)
}

.chat__body {
    display: flex;
    flex-direction: column;
    height: calc(100% - 3rem);
    border-radius: 5px;
    overflow: hidden;
    margin: 0 25px;
}

.chat__input {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    height: 3rem;
    gap: 9px;
}

.chat__input textarea::-webkit-scrollbar {
    width: 0px;
    display: none;
    background: transparent;
}

.chat__input button {
    background-color: var(--color-blue-mute);
    color: var(--color-text);
}

.chat__connecting {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    height: 100%;
}

.chat__textarea {
    width: 80%;
    height: 70%;
    border: none;
    outline: none;
    resize: none;
    border-radius: 5px;
    padding: 10px;
    font-size: 15px;
    color: var(--color-text);
    background-color: var(--color-background);
}

.chat__send {}

.chat__textarea {
    background-color: var(--color-background-modern-mute);
}

.chat__line {
    width: 1px;
    height: 100%;
    position: absolute;
}

.chat__content {
    margin-left: 10px;
    width: 95%;
    line-break: anywhere;
}

.chat__user {
    width: 15%;
}

.chat__messages {
    max-height: 400px;
    widows: 100px;
    overflow: scroll;
    overflow-x: hidden;
}

.hover {
    display: none;
    position: absolute;
    top: 0px;
    right: 0px;
    font-size: 11px;
    opacity: 0.5;
}

.chat__message:hover .hover {
    display: block;
}


.chat__messages::-webkit-scrollbar {
    width: 0px;
    background: transparent;
    display: none;
}
</style>