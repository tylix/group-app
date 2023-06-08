<template>
    <div v-if="!this.loading" class="contacts">
        <!--<div v-if="this.request">
            {{ this.request }}
            <button @click="this.$users.acceptRequest(this.request.targetUId)">Accept</button>
        </div>-->
        <div class="contacts__categories">
            <div class="contacts__category" v-for="(item, index) in this.categories" :key="index"
                @click="this.selectedCategory = index" :style="this.selectedCategory === index ? {backgroundColor: 'var(--color-background-modern-mute)'} : {}">
                <i :class="item.icon" />
                <p>{{ item.name }}</p>
                <div class="contact__amount" v-if="item.amount">
                    <p>{{ item.amount }}</p>
                </div>
            </div>
        </div>
        <div class="contacts__list">
            <component :is="this.categories[this.selectedCategory].component" :contacts="this.contacts"
                :requests="this.requests" @update="this.loadContacts()"/>
        </div>
    </div>
    <i v-else class="chart-loader bx bx-loader-alt bx-spin" />
</template>

<script>
import ContactsComponent from "@/components/contacts/ContactsComponent.vue"
import RequestComponent from "@/components/contacts/RequestComponent.vue"

export default {
    name: 'ContactView',
    data() {
        return {
            contacts: undefined,
            requests: undefined,
            request: undefined,
            loading: true,
            selectedCategory: 0,
            categories: [
                {
                    name: 'Contacts',
                    icon: 'bx bxs-contact',
                    component: ContactsComponent
                },
                {
                    name: 'Requests',
                    icon: 'bx bx-user-plus',
                    component: RequestComponent,
                    amount: 0
                },
                {
                    name: 'Suggestions',
                    icon: 'bx bx-radar'
                },
                {
                    name: 'Blocked',
                    icon: 'bx bx-block'
                }
            ]
        }
    },
    mounted() {
        this.loadContacts()
    },
    updated() {
        this.loadRequest()
    },
    methods: {
        loadRequest() {
            const requestId = this.$route.query.request
            if (requestId && !this.request) {
                this.$users.getRequest(requestId).then(req => {
                    this.request = req
                })
            }
        },
        loadContacts() {
            this.loading = true

            this.loadRequest()
            this.$users.getContacts().then(res => {
                this.contacts = res

                this.$users.getRequests().then(res => {
                    this.requests = res
                    this.categories[1].amount = this.requests.length

                    this.loading = false
                })
            })
        }
    }
}
</script>

<style>
.contacts {
    left: 50%;
    top: 15%;
    transform: translate(-50%, -50%);
    position: fixed;
    width: 60%;
}

.contacts__categories {
    display: flex;
    gap: 50px;
}

.contacts__category {
    cursor: pointer;
    border: 1px solid var(--color-background);
    border-radius: 5px;
    padding: 8px 20px;
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 17px;
    left: 10%;
}

.contacts__category:hover {
    background: var(--color-background-modern-mute);
    transition: 0.3s ease-in-out;
}

.contacts__category i {
    font-size: 20px;
}

.contacts__list {
    position: absolute;
    width: 100%;
}

.contact__amount {
    background-color: var(--color-red);
    width: 18px;
    height: 18px;
    border-radius: 50px;
}

.contact__amount p {
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    position: absolute;
    font-weight: bold;
}
</style>