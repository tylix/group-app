<script>
import Navbar from '@/components/Navbar.vue'
import {defineComponent} from "vue";
import {loading} from "@/router/router";

export default defineComponent({
    components: {Navbar},
    data() {
        return {
            sidebar: false,
            loading: loading
        }
    },
    methods: {
        existUser() {
            return localStorage.getItem('user') !== null
        }
    },
    updated() {
        this.sidebar = localStorage.getItem('token') !== null
    },
    beforeUnmount() {
        this.$websocket.disconnect()
    },
    mounted() {
        if(localStorage.getItem('token') !== null)
            this.$websocket.connect()

        this.sidebar = localStorage.getItem('token') !== null
        if (localStorage.getItem('color') !== null) {

            const styleSheet = document.styleSheets[1];
            const rules = styleSheet.cssRules || styleSheet.cssRules;

            for (let i = 0; i < rules.length; i++) {
                const rule = rules[i];
                if (!rule.media) continue
                rule.media.mediaText = "(prefers-color-scheme: " + localStorage.getItem('color') + ")";
                break
            }
        }
    }
})

</script>

<template>
    <i v-if="this.loading" class="spin bx bx-loader bx-spin"/>
    <router-view v-else/>
    <Navbar v-if="(this.$route.name !== 'Login' && this.$route.name !=='OTP' && this.$route.name !== 'Signup') && this.existUser() && !this.loading"/>
</template>

<style>
</style>
