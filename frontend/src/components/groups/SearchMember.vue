<template>
    <div>
        <div class="search__bar">
            <i class="bx bx-search" />
            <input class="user__search" type="text" placeholder="Invite member" @input="this.isTyping = true"
                v-model="this.searchQuery" />
        </div>
        <div class="search__results">
            <i v-if="this.isLoading" class="search__member__load bx bx-loader-alt bx-spin" />

            <div class="search__result" v-for="(result, index) in this.searchResult">
                <UsernameComponent :user="result" show-avatar />
                <button class="invite__btn" @click="createInviteLink(result)">Invite</button>
            </div>
            <p v-if="this.searchResult.length === 0 && !isLoading && this.searchQuery">No entry found.</p>
        </div>
        <div class="group__invite" @click="this.setupInviteLink = !this.setupInviteLink">
            <i class="bx bx-link" />
            <p>Create invite link</p>
        </div>
        <div :class="[setupInviteLink ? 'invite__link' : 'n__invite__link']">
            <div class="invite__link__expire">
                <p>Expire after</p>
                <select class="invite__dropdown" id="expire-after-select" v-model="this.expireAfter">
                    <option v-for="expire in this.expireItems" :value="expire.value">{{ expire.text }}</option>
                </select>
                <i class="dropdown__icon bx bx-chevron-down" />
            </div>
            <div class="invite__link__uses">
                <p>Max uses</p>
                <select class="invite__dropdown" id="max-uses-select" v-model="this.maxUses">
                    <option v-for="uses in this.maxUsesItems" :value="uses.value">{{ uses.text }}</option>
                </select>
                <i class="dropdown__icon bx bx-chevron-down" />
            </div>
            <button class="invite__link__btn" @click="createInviteLink()">Generate & Copy</button>
        </div>
    </div>
</template>

<script>
import _ from 'lodash';
import UsernameComponent from "@/components/UsernameComponent.vue";

export default {
    name: 'SearchMember',
    components: {
        UsernameComponent
    },
    data() {
        return {
            searchQuery: undefined,
            isTyping: false,
            isLoading: false,
            searchResult: [],
            setupInviteLink: false,

            expireAfter: '30min',
            maxUses: 0,
            expireItems: [
                { value: '30min', text: '30 min', milliseconds: 1800000 },
                { value: '1h', text: '1 hour', milliseconds: 3600000 },
                { value: '6h', text: '6 hours', milliseconds: 21600000 },
                { value: '12h', text: '12 hours', milliseconds: 43200000 },
                { value: '1d', text: '1 day', milliseconds: 86400000 },
                { value: '7d', text: '7 days', milliseconds: 604800000 },
                { value: 'never', text: 'Never', milliseconds: 0 },
            ],
            maxUsesItems: [
                { text: 'No limit', value: 0 },
                { text: 'One time', value: 1 },
                { text: '5 times', value: 5 },
                { text: '10 times', value: 10 },
                { text: '25 times', value: 25 },
                { text: '50 times', value: 50 },
                { text: '100 times', value: 100 },
            ]
        }
    },
    props: {
        group: {
            type: Object,
            required: true
        },
        member: {
            type: Array,
            required: true
        }
    },
    methods: {
        searchAccount(input) {
            this.searchResult = [];
            if (input === undefined || input === '') {
                return;
            }
            this.isLoading = true;
            this.$users.search(input).then(result => {
                this.searchResult = result.filter(account => {
                    return !this.member.some(member => member.uid === account.uid) && !this.isInvited(account);
                });
                this.isLoading = false;
            })
        },
        createInviteLink(user = undefined) {
            const expireItem = this.expireItems.find(item => item.value === this.expireAfter);
            const maxUsesItem = this.maxUsesItems.find(item => item.value === this.maxUses);

            this.$groups.createInvite(this.group.id, expireItem && !user ? expireItem.milliseconds : -1, maxUsesItem ? maxUsesItem.amount : 0, user?.uid).then(invite => {
                if (user) {
                    this.$toast.showNotification(`${user.username} has been invited to the group.`);
                    return
                }
                const inviteLink = `https://maximilianwiegmann.com/groups?invlink=${invite.data.token}`;
                navigator.clipboard.writeText(inviteLink).then(() => {
                    this.$toast.showNotification('Invite link copied to clipboard');
                }).catch(() => {
                    this.$toast.showNotification('Failed to copy invite link to clipboard', 5000, 'error');
                })
            })
        },
        isInvited(result) {
            return this.group.invited.find(invite => invite?.receiver === result.uid);
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
.search__bar {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 0 25px 25px 25px;
    gap: 10px;
}

.search__results {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    padding: 0 25px 30px;
    gap: 10px;
    overflow: scroll
}

.search__result {
    display: flex;
    justify-content: space-between;
    align-items: center;
    overflow: scroll
}

.invite__btn {
    margin-left: auto;
    background-color: var(--color-blue-mute);
}

.search__hr {
    margin: 0 25px 20px 25px;
    border: 1px solid var(--color-background-soft);
}

.search__member {
    opacity: 1;
    max-height: 200px;
    overflow: hidden;
    transition: max-height 0.5s ease;
    overflow-y: scroll;
}

.invite__link {
    opacity: 1;
    max-height: 200px;
    overflow: hidden;
    transition: max-height 0.5s ease;
}

.n__invite__link {
    max-height: 0;
    overflow: hidden;
    transition: max-height 0.3s ease;
}
</style>