<template>
    <div class="toast-container" :class="{'show': show}" :style="{backgroundColor: this.toColor()}">
        <div class="toast-message">{{ message }}</div>
        <button class="toast-close" @click="hide">&times;</button>
    </div>
</template>

<script>
export default {
    name: "Toast",
    props: {
        message: String,
        timeout: {
            type: Number,
            default: 3000
        },
        backgroundColor: {
            type: String,
            default: 'warning'
        }
    },
    data() {
        return {
            show: false
        }
    },
    mounted() {
        this.show = true;
        setTimeout(() => {
            this.hide();
        }, this.timeout);
    },
    methods: {
        hide() {
            this.show = false;
        },
        toColor() {
            switch (this.backgroundColor) {
                case 'success':
                    return 'green';
                case 'error':
                    return 'var(--color-red)';
                case 'warning':
                    return 'yellow';
                case 'info':
                    return 'var(--color-blue-soft)';
            }
        }
    }
}
</script>

<style>
.toast-container {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 999;
    padding: 10px;
    border-radius: 5px;
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.5);
    opacity: 0;
    transform: translateY(-20px);
    transition: opacity 0.3s, transform 0.3s;
    width: 290px;
}

.toast-container.show {
    opacity: 1;
    transform: translateY(0);
}

.toast-message {
    margin-bottom: 5px;
}

.toast-close {
    position: absolute;
    top: 5px;
    right: 5px;
    background-color: transparent;
    border: none;
    color: white;
    font-size: 16px;
    cursor: pointer;
}
</style>