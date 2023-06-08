<template>
    <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
    <div class="requests" v-else>
        <input class="search__contact" placeholder="Search in requests">

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
    font-size: 23px;
}

.request__icons i {
    cursor: pointer;
}

.requests__amount {
    margin-top: 28px;
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
</style>