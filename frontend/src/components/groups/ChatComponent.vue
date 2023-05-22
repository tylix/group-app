<template>
    <div class="expanded" v-if="this.expanded">
        <div class="chat__body">
            <div ref="chat" class="chat__messages">
                <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
                <div class="chat__message" v-for="(message, index) in this.messages" :key="index" v-else>
                    <UsernameComponent class="chat__user" :user="message.name === 'You' ? this.getUser() : message.name"
                        v-if="index === 0 || this.messages[index - 1].userId !== message.userId"
                        :style="{ color: this.$groups.getAvatarColor(message.name) }" />
                    <div>
                        <div class="chat__line" :style="{ backgroundColor: this.$groups.getAvatarColor(message.name) }" />
                        <p class="chat__content">{{ message.content }}</p>
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
                <div class="chat__message" v-for="(message, index) in this.messages" :key="index" v-else>
                    <UsernameComponent class="chat__user" :user="message.name === 'You' ? this.getUser() : message.name"
                        v-if="index === 0 || this.messages[index - 1].userId !== message.userId"
                        :style="{ color: this.$groups.getAvatarColor(message.name) }" />
                    <div>
                        <div class="chat__line" :style="{ backgroundColor: this.$groups.getAvatarColor(message.name) }" />
                        <p class="chat__content">{{ message.content }}</p>
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
    <!--<div class="chat-header">
        <h1>Chat</h1>
    </div>
    <div class="chat-body">
        <div class="messages" id="messages">
            <div class="message" v-for="message in this.messages"
                 :style="{'background-color': this.$groups.getColorByMember(this.group, message.userId)}">
                <div>
                    <p>{{ message.name }}</p>
                    <p v-if="message.name === 'You'">You</p>
                    <UsernameComponent :user="message.name" v-else/>
                    <p>{{ this.time_ago(message.timestamp) }}</p>
                </div>
                <p>{{ message.content }}</p>
            </div>
        </div>
        <div class="input">
            <div class="input-actions">
                <i class="bx bx-smile"></i>
                <i class="bx bx-plus"></i>
            </div>
            <textarea placeholder="Type a message" v-model="this.message" @keyup.enter.shift="this.sendMessage"/>
            <button @click="this.sendMessage">Send</button>
        </div>
    </div>-->
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
            loading: false
        }
    },
    mounted() {
        if (this.group) {
            this.loading = true
            this.group.messages.forEach(message => {
                const newMessage = {
                    content: this.decrypt(message.content, import.meta.env.VITE_PUB_KEY).toString(this.$CryptoJS.enc.Utf8),
                    timestamp: message.timestamp,
                    userId: message.userId,
                    name: ''
                }
                if (message.userId === JSON.parse(localStorage.getItem('user')).uid) {
                    newMessage.name = 'You';
                    this.messages.push(newMessage)
                    this.checkMessages()
                } else {
                    this.$users.getAcountCached(message.userId).then(user => {
                        if(user === undefined) return
                        newMessage.name = user;
                        this.messages.push(newMessage)
                        this.checkMessages()
                    });
                }
            });
            if(this.group.messages.length === 0) this.loading = false
        }
        this.connectionSuccess()
    },
    unmounted() {
    },
    beforeUnmount() {
    },
    methods: {
        checkMessages() {
            if (this.group.messages.length === this.messages.length) {
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
        connectionSuccess() {
            this.$websocket.subscribeChat(this.group.id, this.onMessageReceived)
        },
        sendMessage() {
            if (this.message === undefined || this.message === '' || this.message === null || this.message === ' ')
                return;
            this.$websocket.send("/app/chat/" + this.group.id + "/sendMessage", JSON.stringify({
                userId: JSON.parse(localStorage.getItem('user')).uid,
                type: 'CHAT',
                content: this.encrypt(this.message),
                timestamp: Date.now()
            }));
            this.message = undefined;
        },
        onMessageReceived(payload) {
            const message = JSON.parse(payload.body);
            console.log(message)
            if (message.userId && message.type && message.type === 'CHAT') {
                const newMessage = {
                    content: this.decrypt(message.content),
                    timestamp: message.timestamp,
                    userId: message.userId,
                    name: ''
                }
                if (message.userId === JSON.parse(localStorage.getItem('user')).uid) {
                    newMessage.name = 'You';
                    this.messages.push(newMessage)
                    this.messages.sort((a, b) => {
                        return a.timestamp - b.timestamp
                    })
                    this.scrollDown()
                } else {
                    this.$users.getAccount(message.userId).then(user => {
                        newMessage.name = user;
                        this.messages.push(newMessage)
                        this.messages.sort((a, b) => {
                            return a.timestamp - b.timestamp
                        })
                        this.scrollDown()
                    });
                }
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




.chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 1rem;
    height: 3rem;
}

.chat-body {
    display: flex;
    flex-direction: column;
    height: calc(100% - 3rem);
    background-color: var(--color-background);
    border-radius: 5px;
}

.input {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 0 1rem;
    height: 3rem;
}

.input-actions {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 3rem;
}

.input textarea {
    width: 80%;
    height: 70%;
    border: none;
    outline: none;
    resize: none;
    padding: 0 1rem;
    font-size: 1rem;
    background-color: var(--color-background-mute);
    color: var(--color-text);
    border-radius: 10px;
}

.input button {
    opacity: 50%;
}

.message {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 0 1rem;
    height: 3rem;
    border-radius: 5px;
    margin: 0.5rem 0;
    width: 90%;
    left: 5%;
}

.messages {
    height: 400px;
    padding: 0 1rem;
    overflow: scroll;
    display: flex;
    flex-direction: column-reverse;
}

.chat__messages::-webkit-scrollbar {
    width: 0px;
    background: transparent;
    display: none;
}
</style>