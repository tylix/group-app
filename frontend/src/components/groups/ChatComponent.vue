<template>
    <div class="expanded" v-if="this.expanded">
        <p>Big Chat</p>
    </div>
    <div class="collapsed" v-else>
        <div class="chat__header">
            <h3 class="chat__title">Chat</h3>
        </div>
        <div class="chat__body">
            <div class="chat__messages">
                <p v-for="message in this.messages">
                    {{ message.content }}
                </p>
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
            stompClient: undefined,
            message: undefined,
            messages: [],
        }
    },
    mounted() {
        /*if (this.group) {
            this.group.messages.forEach(message => {
                const newMessage = {
                    content: this.$CryptoJS.AES.decrypt(message.content, import.meta.env.VITE_PUB_KEY).toString(this.$CryptoJS.enc.Utf8),
                    timestamp: message.timestamp,
                    userId: message.userId,
                    name: ''
                }
                if (message.userId === JSON.parse(localStorage.getItem('user')).uid) {
                    newMessage.name = 'You';
                    this.messages.push(newMessage)
                    this.messages.sort((a, b) => {
                        return b.timestamp - a.timestamp
                    })
                } else {
                    this.$users.getAccount(message.userId).then(user => {
                        newMessage.name = user;
                        this.messages.push(newMessage)
                        this.messages.sort((a, b) => {
                            return b.timestamp - a.timestamp
                        })
                    });
                }
            });
        }*/
    },
    unmounted() {
    },
    beforeUnmount() {
    },
    methods: {
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
                        return b.timestamp - a.timestamp
                    })
                } else {
                    this.$users.getAccount(message.userId).then(user => {
                        newMessage.name = user;
                        this.messages.push(newMessage)
                        this.messages.sort((a, b) => {
                            return b.timestamp - a.timestamp
                        })
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
        time_ago(time) {
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
    padding: 0 25px;
    height: 3rem;
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

.messages::-webkit-scrollbar {
    width: 0px;
    background: transparent;
    display: none;
}
</style>