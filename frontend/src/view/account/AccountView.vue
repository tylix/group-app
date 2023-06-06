<template>
    <div class="profile" v-if="this.user">
        <div class="left-side">
            <img alt="avatar" :src="this.user.avatar ? 'data:image/png;base64,' + this.user.avatar : '/default.png'"/>
            <h1>{{ this.user.username }}</h1>
            <p>{{ this.user.firstName }} {{ this.user.lastName }}</p>

        </div>
        <div class="right-side">
            <button v-if="!this.isSelf">Invite as Friend</button>
        </div>
    </div>
    <div v-else>
        <i class="spinner bx bx-loader bx-spin"/>
    </div>
</template>

<script>
export default {
    name: "AccountView",
    data() {
        return {
            user: undefined,
            isSelf: false
        }
    },
    props: {
        username: {
            type: String,
            default: undefined
        }
    },
    mounted() {
        this.loadUser()
    },
    updated() {
        if (this.isSelf && this.user !== JSON.parse(localStorage.getItem('user')) || !this.isSelf && this.user?.username !== this.username)
            this.loadUser()
    },
    methods: {
        loadUser() {
            this.user = undefined
            this.isSelf = false
            if (this.username) {
                const selfUser = JSON.parse(localStorage.getItem('user'))
                if (selfUser.username === this.username) {
                    this.user = selfUser
                    this.isSelf = true
                    return
                }
                this.$users.getByUsername(this.username).then((response) => {
                    this.user = response
                })
            }
        }
    }
}
</script>

<style>
.profile {
    position: fixed;
    left: 15%;
    top: 10%;
}

.spinner {
    position: fixed;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    font-size: 30px;
}

.profile .left-side {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.left-side img {
    height: 200px;
    width: 200px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 20px;
}

.left-side h1 {
    font-size: 30px;
    margin-bottom: 10px;
}

.left-side p {
    font-size: 20px;
    margin-bottom: 20px;
}

.right-side {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: fixed;
    right: 10%;
    top: 10%;
}

.right-side button {
    margin-top: 20px;
    padding: 10px 20px;
    border-radius: 10px;
    border: none;
    background-color: var(--color-background-mute);
    color: var(--color-text);
    font-size: 15px;
    cursor: pointer;
}
</style>