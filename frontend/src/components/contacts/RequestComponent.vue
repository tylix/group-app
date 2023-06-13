<template>
    <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
    <div class="requests" v-else>
        <div class="contacts__search__bar">
            <input class="search__contact" placeholder="Search request">
            <i class="search__icon bx bx-search" />
        </div>

        <p class="requests__amount">Requests - {{ this.requests.length }}</p>
        <div class="request__body">
            <div class="reqeusts__requests" v-for="(request, index) in this.requestUsers" :key="index">
                <div class="requests__request">
                    <UsernameComponent :user="request.user" show-avatar />
                    <div class="request__right">
                        <div class="request__icons">
                            <i class="request__accept bx bx-user-plus" @click="this.accept(request)" />
                            <i class="request__deny bx bx-user-x" @click="this.deny(request)" />
                        </div>
                    </div>
                </div>
                <hr v-if="index < this.requestUsers.length - 1" />
            </div>
            <div class="pending__requests" v-if="this.requestedUsers.length > 0">
                <p class="requested__amount">Requested - {{ this.requestedUsers.length }}</p>
                <div class="pending__request" v-for="(item, index) in this.requestedUsers" :key="index">
                    <div class="pending__left">
                        <UsernameComponent :user="item" show-avatar />
                        <p class="pending__text">Pending request...</p>
                    </div>
                    <div class="pending__right">
                        <i class="bx bx-user-minus" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import UsernameComponent from '@/components/UsernameComponent.vue'
export default {
    name: 'RequestComponent',
    components: {
        UsernameComponent
    },
    props: {
        requests: {
            type: Array,
            required: true
        }
    },
    data() {
        return {
            requestUsers: [],
            requestedUsers: [],
            loading: true
        }
    },
    mounted() {
        this.loadUser()
    },
    methods: {
        loadUser() {
            this.loading = true

            this.requests.forEach(request => {
                this.$users.getAcountCached(request.targetUId).then(user => {
                    this.requestUsers.push({ request: request, user: user })

                    if (this.requestUsers.length === this.requests.length) this.loading = false
                })
            })

            this.loadRequested()

            if (this.requests.length === 0) this.loading = false
        },
        accept(request) {
            const uId = request.request.targetUId
            this.$users.acceptRequest(uId).then(res => {
                this.$toast.showNotification(`${request.user.username} is now in your contacts.`)
                this.$emit('update')
            }).catch(() => {
                this.$toast.showNotification('An error occurred while accepting the request.', 2500, 'error')
            })
        },
        deny(request) {
            const uId = request.request.targetUId
            this.$users.denyRequest(uId).then(res => {
                this.$toast.showNotification(`You denied the request from ${request.user.username}.`)
                this.$emit('update')
            }).catch(() => {
                this.$toast.showNotification('An error occurred while accepting the request.', 2500, 'error')
            })
        },
        loadRequested() {
            this.$users.getRequested().then(res => {
                this.requestedUsers = res
                console.log(this.requestedUsers)
            })
        }
    }
}
</script>

<style>
.requests {
    margin-top: 3%;
    padding: 20px;
}

.requests__request {
    display: flex;
    align-items: center;
    margin-top: 15px;
    font-size: 20px;
}

.request__body {
    margin-top: 55px;
}

.reqeusts__requests hr {
    margin-top: 15px;
    border: 1px solid var(--color-background-soft);
}

.request__right {
    margin-left: auto;
}

.request__icons {
    display: flex;
    gap: 10px;
    font-size: 25px;
}

.request__icons i {
    cursor: pointer;
}

.requests__amount {
    margin-top: 35px;
    position: absolute;
    font-size: 13px;
    font-weight: bold;
}

.request__accept:hover {
    color: var(--color-green);
    transition: 0.3s all ease-in-out;
}

.request__deny:hover {
    color: var(--color-red);
    transition: 0.3s all ease-in-out;
}

.pending__requests {
    position: absolute;
    margin-top: 5%;
    width: 100%;
}

.pending__request {
    width: 100%;
    display: flex;
    align-items: center;
    font-size: 20px;
}

.pending__right {
    margin-left: auto;
    font-size: 25px;
}

.pending__right:hover {
    cursor: pointer;
}

.pending__text {
    font-size: 12px;
    left: 40px;
    bottom: 5px;
    color: var(--color-text-muted);
    opacity: 70%;
}

.requested__amount {
    font-size: 13px;
    font-weight: bold;
}
</style>