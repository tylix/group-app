<template>
    <div class="expanded" v-if="this.expanded">
        <div class="emember__list">
            <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
            <UsernameComponent v-for="member in this.member" :user="member" show-avatar direction="column"
                :options="{ items: [] }" :custom-card="this.customCardComponent" />
        </div>
        <div class="member__right__side">
            <div class="member__invited">
                <div class="invited__header">
                    <h3>Invited</h3>
                    <i @click="this.searchInvited = !this.searchInvited"
                        :class="'search__btn bx bx-' + (this.searchInvited ? 'minus' : 'plus')" />
                </div>
                <div class="invited__body">
                    <i v-if="this.loadingInvited && this.invitedMember === undefined" class="bx bx-loader-alt bx-spin" />
                    <div v-else>
                        <div v-for="(member, index) in this.invitedMember" :key="index">
                            <div>
                                <div class="invited__member">
                                    <UsernameComponent :user="member" show-avatar />
                                </div>
                                <hr class="member__invited__hr"  v-if="index < this.invitedMember?.length - 1" />
                            </div>
                        </div>
                        <hr class="invited__hr" />
                        <div v-for="(link, index) in this.invitedLinks" :key="index">
                            <div class="invited__link">
                                <UsernameComponent :user="member.find(member => member.uid === link.issuer)" show-avatar :show-name="false" />
                                <p>{{ link.token }}</p>
                                <div class="link__description">
                                    <p>Used: {{ link.used }}</p>
                                    <p>Expire: {{ link.expire == -1 ? 'Never' : link.expire }}</p>
                                </div>
                            </div>
                            <hr class="member__invited__hr" v-if="index < this.invitedLinks.length - 1" />
                        </div>
                        <p v-if="this.invitedMember?.length == 0 && this.invitedLinks.length == 0"
                            :style="{ paddingBottom: '20px' }">Nothing here.</p>
                    </div>
                </div>
            </div>
            <div class="member__requests">
                <div class="requests__header">
                    <h3>Requests</h3>
                </div>
                <div class="requests__body">
                </div>
            </div>
        </div>
    </div>
    <div class="collapsed" v-else>
        <div class="member__header">
            <h3 class="member__title">Member</h3>
            <i @click="this.searchMember = !this.searchMember"
                :class="'search__btn bx bx-' + (this.searchMember ? 'minus' : 'plus')" />
        </div>
        <div :class="[searchMember ? 'search__member' : 'n__search__member']">
            <div class="search__bar">
                <i class="bx bx-search" />
                <input class="user__search" type="text" placeholder="Search member" @input="this.isTyping = true"
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
            <div v-if="this.$groups.getGroupRole(this.group, this.getUser().uid) <= 0" class="group__invite"
                @click="this.setupInviteLink = !this.setupInviteLink">
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
        <hr class="search__hr" />
        <div class="member__list">
            <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
            <UsernameComponent v-for="member in this.member" :user="member" show-avatar :show-name="false" online-status />
        </div>
    </div>
</template>

<script>
import _ from 'lodash';
import UsernameComponent from "@/components/UsernameComponent.vue";
import MemberCard from './MemberCard.vue';

export default {
    name: "MemberComponent",
    components: { UsernameComponent, MemberCard },
    props: {
        group: {
            type: Object,
            required: true
        },
        expanded: {
            type: Boolean,
            required: true
        },
    },
    data() {
        return {
            customCardComponent: MemberCard,
            member: [],
            invitedMember: undefined,
            invitedLinks: [],
            selectedMember: undefined,
            searchMember: false,
            searchQuery: undefined,
            isTyping: false,
            isLoading: false,
            searchResult: [],
            loading: false,
            loadingInvited: false,
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
    mounted() {
        this.loadMember()
    },
    methods: {
        loadMember() {
            this.loading = true
            this.group.member.forEach(member => {
                this.$users.getAccount(member.id).then(account => {
                    this.member.push(account)
                    if (this.member.length === this.group.member.length)
                        this.loading = false
                })
            })
            this.loadInvitedMember()
        },
        loadInvitedMember() {
            this.invitedMember = []
            this.loadingInvited = true
            const invitedMember = this.group.invited.filter(invite => invite.receiver && !invite.expired)
            invitedMember.forEach(invite => {
                this.$users.getAccount(invite.receiver).then(account => {
                    this.invitedMember.push(account)
                    if (this.invitedMember.length === invitedMember.length)
                        this.loadingInvited = false
                })
            })
            if (invitedMember.length === 0)
                this.loadingInvited = false

            this.invitedLinks = this.group.invited.filter(invite => !invite.receiver && !invite.expired)
        },
        getUser() {
            const user = localStorage.getItem('user');
            if (user === null) return undefined;
            return JSON.parse(user);
        },
        searchAccount(input) {
            this.searchResult = [];
            if (input === undefined || input === '') {
                return;
            }
            this.isLoading = true;
            this.$users.search(input).then(result => {
                this.searchResult = result.filter(account => {
                    return !this.member.some(member => member.uid === account.uid)
                });
                this.isLoading = false;
            })
        },
        createInviteLink(user = undefined) {
            const expireItem = this.expireItems.find(item => item.value === this.expireAfter);
            const maxUsesItem = this.maxUsesItems.find(item => item.value === this.maxUses);

            this.$groups.createInvite(this.group.id, expireItem ? expireItem.milliseconds : -1, maxUsesItem ? maxUsesItem.amount : 0, user?.uid).then(invite => {
                console.log(invite)

                const inviteLink = invite.data.link;
                navigator.clipboard.writeText(inviteLink).then(() => {
                    this.$toast.showNotification('Invite link copied to clipboard');
                }).catch(() => {
                    this.$toast.showNotification('Failed to copy invite link to clipboard');
                })
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
.member__title {
    font-size: 17px;
    padding: 10px;
    color: var(--color-text-muted)
}

.member__list {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    padding: 0 25px;
}

.user__search {}

.member__header {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 10px 25px;
}

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

.n__search__member {
    max-height: 0;
    overflow: hidden;
    transition: max-height 0.3s ease;
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

.search__member__load {
    left: 50%;
    position: absolute;
}

.search__btn {
    cursor: pointer;
    font-size: 20px;
}

.group__invite {
    display: flex;
    flex-direction: row;
    position: relative;
    text-align: right;
    align-items: center;
    gap: 10px;
    padding: 10px 25px;
    color: var(--color-blue-mute);
    cursor: pointer;
}

.invite__link {
    display: flex;
    flex-direction: column;
    position: relative;
    text-align: left;
    gap: 10px;
    padding: 10px 25px;
}

.invite__link__expire {
    font-size: 12px;
    color: var(--color-text-muted);
}

.invite__link__uses {
    font-size: 12px;
    color: var(--color-text-muted);
}

.invite__dropdown {
    width: 100%;
    height: 100%;
    background-color: var(--color-background-modern-mute);
    border: none;
    padding: 10px;
    border-radius: 10px;
    color: var(--color-text);
    -moz-appearance: none;
    -webkit-appearance: none;
    appearance: none;
    border: none;
}

.dropdown__icon {
    position: absolute;
    right: 10px;
    top: 70%;
    transform: translateY(-50%);
    pointer-events: none;

}

.invite__link__btn {
    background-color: var(--color-green-soft);
    margin-top: 4px;
}

.emember__list {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    padding: 0 25px;
    left: 20px;
    width: 66%;
}

.emember__list img {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin: 0 10px;
    cursor: pointer;
}

.member__right__side {
    display: flex;
    flex-direction: column;
    position: absolute;
    width: 100%;
    top: 0px;
    align-items: flex-end;
    padding-right: 20px;
}

.member__requests {
    padding: 0 25px;
    border: 1px solid var(--color-background);
    border-radius: 10px;
    width: 35%;
    max-height: 500px;
    overflow: scroll;
    margin-top: 20px;
}

.member__invited {
    padding: 0 25px;
    border: 1px solid var(--color-background);
    border-radius: 10px;
    width: 35%;
    max-height: 500px;
    overflow: scroll;
}

.member__invited__hr {
    border: 1px solid var(--color-background-soft);
}

.invited__header {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 10px 10px;
    font-size: 18px;
}

.invited__member {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    padding: 0 25px;
    width: 100%;
    margin: 10px 0;
}

.invited__link {
    display: flex;
    flex-direction: column;
    position: relative;
    text-align: left;
    gap: 10px;
    padding: 5px 25px;
    width: 100%;
}

.invited__body {
    padding-bottom: 20px;
}

.invited__hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 1px solid var(--color-background-modern-mute);
}

.invited__link {
    display: flex;
    flex-direction: row;
    align-items: center;
}

.link__description {
    position: absolute;
    top: 60%;
    left: 21%;
    font-size: 13px;
    color: var(--color-text-muted);
    display: flex;
    gap: 20px;
}
</style>