<template>
    <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
    <div class="contact__list" v-else>
        <input class="search__contact" placeholder="Search in contacts" @input="this.isTyping = true"
            v-model="this.searchQuery">

        <p class="contacts__amount">Contacts - {{ this.contactUsers.length }}</p>
        <div class="contacts__contacts">
            <div class="contact__contact__loop" v-for="(contact, index) in this.contactUsers" :key="index">
                <div v-if="this.isSearching(contact.user)">
                    <div class="contact__contact">
                        <UsernameComponent :user="contact.user" show-avatar online-status />
                        <div class="contact__right">
                            <p>Friends since {{ this.$groups.time_ago(contact.contact.timestamp) }}</p>
                            <i class="bx bx-dots-vertical-rounded" @click="this.removeContact(contact.user)" />
                        </div>
                    </div>
                    <hr v-if="index < this.contactUsers.length - 1" />
                </div>
            </div>
            <div class="search__results">
                <i v-if="this.isLoading" class="search__member__load bx bx-loader-alt bx-spin" />

                <div class="search__result" v-for="(result, index) in this.searchResult">
                    <UsernameComponent :user="result" show-avatar />
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import _ from 'lodash';
import UsernameComponent from '@/components/UsernameComponent.vue'

export default {
    name: 'ContactsComponent',
    components: {
        UsernameComponent
    },
    props: {
        contacts: {
            type: Array,
            required: true
        }
    },
    data() {
        return {
            contactUsers: [],
            contactUsersBackup: [],
            loading: true,

            searchQuery: undefined,
            isTyping: false,
            isLoading: false,
            searchResults: []
        }
    },
    mounted() {
        this.loadUser()
    },
    methods: {
        loadUser() {
            this.loading = true
            this.contactUsers = []
            this.contacts.forEach(contact => {
                this.$users.getAcountCached(contact.targetUId).then(user => {
                    this.contactUsers.push({ contact: contact, user: user })

                    if (this.contactUsers.length === this.contacts.length)
                        this.loading = false;
                })
            })

            if (this.contacts.length === 0) this.loading = false
        },
        searchAccount(input) {
            this.searchResult = [];
            if (input === undefined || input === '') {
                return;
            }
            this.isLoading = true;
            this.$users.search(input).then(result => {
                this.searchResult = result.filter(res => {
                    return res.uid !== JSON.parse(localStorage.getItem('user')).uid && !this.contacts.find(req => req.targetUId === res.uid)
                })
                this.isLoading = false;
            })
        },
        isSearching(user) {
            if (this.searchQuery === undefined || this.searchQuery === '') return true
            return JSON.stringify(user).toLowerCase().includes(this.searchQuery.toLowerCase());
        },
        removeContact(user) {
            this.$users.removeContact(user.uid).then(res => {
                this.$toast.showNotification(`${user.username} is no longer in your contacts.`)
                this.$emit('update')
            }).catch(() => {
                this.$toast.showNotification('An error occurred while removing the contact.', 2500, 'error')
            })
        }
    },
    watch: {
        searchQuery: _.debounce(function () {
            this.isTyping = false;
        }, 1000),
        isTyping: function (value) {
            if (!value) {
                this.searchAccount(this.searchQuery);
            }
        }
    }
}
</script>

<style>
.contact__list {
    margin-top: 3%;
    padding: 20px;
}

.contacts__contacts {
    margin-top: 55px;
}

.contact__contact {
    display: flex;
    font-size: 20px;
    margin-top: 15px;
}

.contact__right {
    margin-left: auto;
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 11px;
}

.contact__right i {
    font-size: 20px;
    transition: 0.5s ease-in-out;
}

.contact__right i:hover {
    cursor: pointer;
    color: var(--color-text-muted);
    transition: 0.5s ease-in-out;
}

.search__contact {
    height: 25px;
    width: 94%;
    position: absolute;
    left: 50%;
    transform: translate(-50%, -50%);
    background-color: var(--color-background-modern);
    border: 1px solid var(--color-background-mute);
    border-radius: 5px;
    padding: 20px;
    font-size: 18px;
    text-align: center;
    color: var(--color-text);
}

.contacts__amount {
    margin-top: 28px;
    position: absolute;
    font-size: 13px;
    font-weight: bold;
}

.contact__contact__loop hr {
    margin-top: 15px;
    border: 1px solid var(--color-background-soft);
}
</style>