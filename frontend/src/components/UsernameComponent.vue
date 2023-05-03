<template>
    <div class="username" @mouseover="this.toggleHover(true)" @mouseleave="this.toggleHover(false)">
        <div class="name" @click="this.redirect()">
            <img v-if="this.showAvatar" alt="avatar"
                  :src="this.user.avatar ? 'data:image/jpeg;base64, ' + this.user.avatar : '/default.png'"/>
            <i v-if="this.onlineStatus" class="member__dot"/>
            <p v-if="this.showName">{{ this.fullName ? this.user.firstName + ' ' + this.user.lastName : this.user.username }}</p>
        </div>
        <div v-if="this.hover" class="username-hover">
            <div class="user-header">
                <img alt="avatar"
                     :src="this.user.avatar ? 'data:image/jpeg;base64, ' + this.user.avatar : '/default.png'"/>
                <h4>{{ this.user.firstName + ' ' + this.user.lastName }}</h4>
                <p>{{ this.user.username}}</p>
            </div>
            <button @click="this.redirect()">Profile</button>
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
    position: fixed;
    background-color: var(--color-background-mute);
    border-radius: 5px;
    width: 300px;
    height: 100px;
    cursor: default;
    z-index: 1000;
    display: flex;
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