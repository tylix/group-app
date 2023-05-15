<template>
    <div class="expanded" v-if="this.expanded">
        <div class="emember__list">
            <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
            <UsernameComponent v-for="member in this.member" :user="member" show-avatar :show-name="false" online-status/>
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
                    <button class="invite__btn" @click="this.createInviteLink(result)">Invite</button>
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
                <button class="invite__link__btn" @click="createInviteLink()">Generate</button>
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

export default {
    name: "MemberComponent",
    components: { UsernameComponent },
    props: {
        group: {
            type: Object,
            required: true
        },
        expanded: {
            type: Boolean,
            required: true
        }
    },
    data() {
        return {
            member: [],
            selectedMember: undefined,
            searchMember: false,
            searchQuery: undefined,
            isTyping: false,
            isLoading: false,
            searchResult: [],
            loading: false,
            setupInviteLink: false,
            expireAfter: '30min',
            maxUses: '0',
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
                { value: '0', text: 'No limit', amount: 0 },
                { value: '1', text: 'One time', amount: 1 },
                { value: '5', text: '5 times', amount: 5 },
                { value: '10', text: '10 times', amount: 10 },
                { value: '25', text: '25 times', amount: 25 },
                { value: '50', text: '50 times', amount: 50 },
                { value: '100', text: '100 times', amount: 100 },
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

            this.$groups.createInvite(this.group.id, expireItem.milliseconds, maxUsesItem.amount, user?.uid).then(invite => {
                console.log(invite)
                this.$toast.showNotification('Invite link created.', 10 * 1000, 'success')
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
}

.emember__list img {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin: 0 10px;
    cursor: pointer;
}








.member__avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin: 0 10px;
    cursor: pointer;
}


.member-list {
    width: 100%;
    height: 100%;
    background-color: var(--color-background);
    padding: 10px;
    border-radius: 10px;
}

.member {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 80%;
    height: 20px;
    background-color: var(--color-background);
    margin: 20px 4px;
    left: 10%;
}

.member-edit {
    cursor: pointer;
    width: 20px;
    height: 20px;
}

.header {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    height: 20px;
    margin: 10px 4px;
}

.invite-btn {
    /*background-color: var(--color-green); border: none; border-radius: 5px; padding: 5px; color: var(--color-text);*/
}

.search-results {
    width: 100%;
    height: 100%;
    background-color: var(--color-background);
    padding: 10px;
    border-radius: 10px;
    margin-top: 10px;
}


.search-box {
    display: flex;
    align-items: center;
    border: 1px solid var(--color-background-mute);
    border-radius: 5px;
    padding: 5px 10px;
    width: 100%;
}

.search-box i {
    font-size: 20px;
    padding-right: 5px;
}

.member hr {
    position: absolute;
    width: 100%;
    margin-bottom: 40px;
    border: 1px solid var(--color-background-mute);
}
</style>