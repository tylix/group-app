import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default {
    install: (app) => {

        let stompClient = undefined
        let isConnected = false

        let disconnectSub = undefined

        const w = {
            connect() {
                const socket = new SockJS('https://api.maximilianwiegmann.com/ws');
                //const socket = new SockJS('http://127.0.0.1:8080/ws');
                stompClient = Stomp.over(socket)

                stompClient.connect({
                    Authorization: 'Bearer ' + localStorage.getItem('token')
                }, () => {
                    isConnected = true
                    window.addEventListener('beforeunload', () => {
                        w.disconnect()
                    })
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

            subscribeChat(gId, sub, callback, disSub) {
                if (isConnected) {
                    disconnectSub = disSub
                    console.log('Subscribing to chat ' + gId)
                    stompClient.subscribe('/channel/' + gId, sub);
                    const uid = JSON.parse(localStorage.getItem('user')).uid;
                    w.send("/app/chat/" + gId + "/addUser", JSON.stringify({
                        userId: uid,
                        type: 'JOIN'
                    }));
                    callback()
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
            },

            isConnected() {
                return isConnected
            }

        }

        app.config.globalProperties.$websocket = w
    }
}