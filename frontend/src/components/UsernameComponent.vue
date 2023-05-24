<template>
    <div class="username" @mouseover="this.toggleHover(true)" @mouseleave="this.toggleHover(false)">
        <div class="name" @click="this.redirect()" :style="{ 'flex-direction': this.direction }">
            <img v-if="this.showAvatar" alt="avatar"
                :src="this.user.avatar ? 'data:image/jpeg;base64, ' + this.user.avatar : '/default.png'" />
            <i v-if="this.onlineStatus" class="member__dot" />
            <p v-if="this.showName">{{ this.fullName ? this.user.firstName + ' ' + this.user.lastName :
                this.user.username }}</p>
        </div>
        <div v-if="this.hover" class="username-hover">
            <component v-if="this.customCard" :is="this.customCard" :user="this.user" />
            <div v-else class="user__header">
                <img alt="avatar"
                    :src="this.user.avatar ? 'data:image/jpeg;base64, ' + this.user.avatar : '/default.png'" />
                <h4>{{ this.user.firstName + ' ' + this.user.lastName }}</h4>
                <p>{{ this.user.username }}</p>
            </div>
            <button class="profile__btn" @click="this.redirect()">Profile</button>
        </div>
    </div>
</template>

<script>
export default {
    name: "UsernameComponent",
    props: {
        user: {
            data: Object,
            required: true
        },
        showAvatar: {
            type: Boolean,
            default: false
        },
        fullName: {
            type: Boolean,
            default: false
        },
        showName: {
            type: Boolean,
            default: true
        },
        onlineStatus: {
            type: Boolean,
            default: false
        },
        direction: {
            type: String,
            default: 'row'
        },
        customCard: {
            type: Object,
            default: undefined
        }
    },
    data() {
        return {
            hover: false,
            timer: undefined
        }
    },
    methods: {
        toggleHover(hover) {
            clearTimeout(this.timer)
            if (hover) {
                this.timer = setTimeout(() => {
                    this.hover = true
                }, 500)
            } else {
                this.hover = false
            }
        },
        redirect() {
            this.$router.push('/account/' + this.user.username)
        }
    }
}
</script>

<style>
.username {
    cursor: pointer;
}

.username-hover {
    position: absolute;
    background-color: var(--color-background-modern-mute);
    border: 1px solid var(--color-background);
    border-radius: 5px;
    width: 300px;
    height: auto;
    cursor: default;
    z-index: 100;
    display: flex;
    padding: 20px 20px;
}

.user__header {
    direction: flex;
    flex-direction: row;
    align-items: center;
}

.profile__btn {
    position: absolute;
    bottom: 10px;
    right: 10px;
    background-color: var(--color-blue);
}

.name img {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    margin-right: 10px;
}

.username-hover img {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-right: 10px;
}

.name {
    display: flex;
    align-items: center;
}

.member__dot {
    display: inline-block;
    width: 10px;
    height: 10px;
    background-color: var(--color-green);
    box-shadow: 0 0 0 3px var(--color-background-modern);
    border-radius: 90%;
    position: absolute;
    margin: 24px 0 0 20px;
}
</style>