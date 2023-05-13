import {Stomp} from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default {
    install: (app) => {

        let stompClient = undefined
        let isConnected = false

        const w = {
            connect() {
                const socket = new SockJS('https://api.maximilianwiegmann.com/ws');
                stompClient = Stomp.over(socket)

                stompClient.connect({
                    Authorization: 'Bearer ' + localStorage.getItem('token')
                }, () => {
                    isConnected = true
                })
            },

            disconnect() {
                if (stompClient) {
                    stompClient.disconnect()
                    isConnected = false
                }
            },

            subscribeNotifications() {
                if (isConnected) {

                }
            },

            subscribeChat(gId) {
                if (isConnected) {

                }
            }
        }

        app.config.globalProperties.$websocket = w
    }
}