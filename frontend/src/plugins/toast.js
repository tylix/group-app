import {createApp, h} from 'vue'
import Toast from "@/components/Toast.vue";

export default {
    install: (app) => {
        const w = {
            showNotification: (message, timeout = 3000, color = 'success') => {
                const toast = createApp({
                    render() {
                        return h(Toast, {
                            message,
                            timeout,
                            backgroundColor: color
                        })
                    }
                })
                const toastContainer = document.createElement('div')
                document.body.appendChild(toastContainer)

                toast.mount(toastContainer)

                setTimeout(() => {
                    toastContainer.remove()
                    document.body.removeChild(toastContainer)
                }, timeout)
            }
        }

        app.config.globalProperties.$toast = w
    }
}