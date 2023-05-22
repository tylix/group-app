import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default {
    install: (app) => {

        let stompClient = undefined
        let isConnected = false

        const w = {
            connect() {
                //const socket = new SockJS('https://api.maximilianwiegmann.com/ws');
                const socket = new SockJS('http://127.0.0.1:8080/ws');
                stompClient = Stomp.over(socket)

                stompClient.connect({
                    Authorization: 'Bearer ' + localStorage.getItem('token')
                }, () => {
                    isConnected = true
                    console.log('Connected')
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
                    console.log('Subscribing to chat ' + gId)
                    stompClient.subscribe('/channel/' + gId, sub);
                    const uid = JSON.parse(localStorage.getItem('user')).uid;
                    w.send("/app/chat/" + gId + "/addUser", JSON.stringify({
                        userId: uid,
                        type: 'JOIN'
                    }));
                } else {
                    console.log('Not connected')
                }
            },

            send(path, data, headers = {}) {
                if (isConnected) {
                    stompClient.send(path, headers, data);
                }
            },

            getConnectedCount(gId) {
                if (isConnected) {
                    stompClient.send("/app/chat/" + gId + "/connectedCount", {}, '');
                }
            }
        }

        app.config.globalProperties.$websocket = w
    }
}