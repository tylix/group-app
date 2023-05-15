import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default {
    install: (app) => {

        let stompClient = undefined
        let isConnected = false

        const w = {
            connect() {
                const socket = new SockJS('https://maximilianwiegmann.com/ws');
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

            subscribeChat(gId, sub) {
                if (isConnected) {
                    stompClient.subscribe('/channel/' + gId, sub);
                    const uid = JSON.parse(localStorage.getItem('user')).uid;
                    w.send("/app/chat/" + gId + "/addUser", JSON.stringify({
                        userId: uid,
                        type: 'JOIN'
                    }));
                }
            },

            send(path, data, headers = {}) {
                if (isConnected) {
                    stompClient.send(path, headers, data);
                }
            }
        }

        app.config.globalProperties.$websocket = w
    }
}