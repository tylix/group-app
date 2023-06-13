<template>
    <div class="preview" v-if="this.joinPreview">
        <div class="member__header">
            <h3 class="member__title">Member</h3>
            <i @click="this.searchMember = !this.searchMember" />
        </div>
        <div class="member__list">
            <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
            <UsernameComponent v-for="member in this.getMember()" :user="member" show-avatar :show-name="false" />
        </div>
    </div>
    <div class="expanded" v-else-if="this.expanded">
        <div class="emember__list">
            <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
            <UsernameComponent v-for="member in this.getMember()" :user="member" show-avatar direction="column"
                :custom-card="this.customCardComponent" online-status
                :member-data="this.member.find(mem => mem.account === member).member" :icon="
                    this.$groups.getGroupRole(this.group, member.uid) === 0 ?
                        { name: 'crown', description: 'Group Owner', color: '#b8860b' } :
                        this.$groups.getGroupRole(this.group, member.uid) === -1 ?
                            { name: 'crown', description: 'Group Admin', color: '#cd7f32' } :
                            {}" />
        </div>
        <div class="member__right__side">
            <div class="member__invited">
                <div class="invited__header">
                    <h3>Invited</h3>
                    <i v-if="this.$groups.getGroupRole(this.group, this.getUser().uid) <= 0"
                        @click="this.searchMember = !this.searchMember"
                        :class="'search__btn bx bx-' + (this.searchMember ? 'minus' : 'plus')" />
                </div>
                <div :class="this.searchMember ? 'invited__body__s' : 'invited__body'">
                    <div v-if="this.searchMember">
                        <SearchMember :group="this.group" :member="this.getMember()" />
                    </div>
                    <div v-else>
                        <i v-if="this.loadingInvited" class="bx bx-loader-alt bx-spin" />
                        <div v-else>
                            <div v-for="(member, index) in this.invitedMember" :key="index">
                                <div>
                                    <div class="invited__member">
                                        <UsernameComponent :user="member" show-avatar />
                                    </div>
                                    <hr class="member__invited__hr" v-if="index < this.invitedMember?.length - 1" />
                                </div>
                            </div>
                            <hr class="invited__hr" v-if="this.invitedMember?.length > 0" />
                            <div v-for="(link, index) in this.invitedLinks" :key="index">
                                <div class="invited__link">
                                    <UsernameComponent v-if="link.issuer" :user="link.issuer" show-avatar />
                                    <p :style="{ '-webkit-user-select': 'text' }">{{ link.token }}</p>
                                    <div class="link__description">
                                        <p>Used: {{ link.used }}</p>
                                        <p>Expire: {{ link.expire == -1 ? 'Never' : this.$groups.time_ago(link.timestamp +
                                            link.expire) }}</p>
                                    </div>
                                </div>
                                <hr class="member__invited__hr" v-if="index < this.invitedLinks.length - 1" />
                            </div>
                            <p v-if="this.invitedMember?.length == 0 && this.invitedLinks.length == 0"
                                :style="{ paddingBottom: '20px' }">Nothing here.</p>
                        </div>
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
            <i v-if="this.$groups.getGroupRole(this.group, this.getUser().uid) <= 0"
                @click="this.searchMember = !this.searchMember"
                :class="'search__btn bx bx-' + (this.searchMember ? 'minus' : 'plus')" />
        </div>
        <div :class="[searchMember ? 'search__member' : 'n__search__member']">
            <SearchMember :group="this.group" :member="this.getMember()" />
        </div>
        <hr class="search__hr" />
        <div class="member__list">
            <i v-if="this.loading" class="bx bx-loader-alt bx-spin" />
            <UsernameComponent v-for="member in this.getMember()" :user="member" show-avatar :show-name="false"
                online-status />
        </div>
    </div>
</template>

<script>
import UsernameComponent from "@/components/UsernameComponent.vue";
import MemberCard from './MemberCard.vue';
import SearchMember from './SearchMember.vue';

export default {
    name: "MemberComponent",
    components: { UsernameComponent, MemberCard, SearchMember },
    props: {
        group: {
            type: Object,
            required: true
        },
        expanded: {
            type: Boolean,
            required: true
        },
        joinPreview: {
            type: Boolean,
            required: false,
            default: false
        }
    },
    data() {
        return {
            customCardComponent: MemberCard,
            member: [],
            invitedMember: undefined,
            invitedLinks: [],
            selectedMember: undefined,
            searchMember: false,
            loading: false,
            loadingInvited: false,
        }
    },
    mounted() {
        this.loadMember()
    },
    methods: {
        getMember() {
            const accounts = []
            this.member.forEach(member => {
                accounts.push(member.account)
            })
            return accounts
        },
        loadMember() {
            this.loading = true
            this.group.member.forEach(member => {
                this.$users.getAccount(member.id).then(account => {
                    this.member.push({ account: account, member: member })
                    if (this.member.length === this.group.member.length) {
                        this.loading = false
                        this.loadInvitedMember()
                        this.member.sort((a, b) => {
                            return a.member.timestamp - b.member.timestamp
                        })
                    }
                })
            })
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
            if (invitedMember.length === 0) {
                this.loadingInvited = false
            }

            this.group.invited.filter(invite => !invite.receiver && !invite.expired).forEach(invite => {
                const inv = {
                    token: invite.token,
                    timestamp: invite.timestamp,
                    expire: invite.expire,
                    receiver: invite.receiver,
                    used: invite.used,
                    maxUses: invite.maxUses,
                    issuer: invite.issuer,
                }
                this.invitedLinks.push(inv)
            })
        },
        getUser() {
            const user = localStorage.getItem('user');
            if (user === null) return undefined;
            return JSON.parse(user);
        }
    },
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
    z-index: 100;
    gap: 13px;
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
    height: 500px;
    overflow: scroll;
}

.member__requests {
    padding: 0 25px;
    border: 1px solid var(--color-background);
    border-radius: 10px;
    width: 35%;
    margin-top: 20px;
}

.member__invited {
    padding: 0 25px;
    border: 1px solid var(--color-background);
    border-radius: 10px;
    width: 35%;
    max-height: 500px;
    padding-bottom: 10px;
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
    transition: 0.5s ease;
}

.invited__body__s {
    transition: 0.5s ease;
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
    margin-bottom: 15px;
}

.invited__link p {
    -webkit-user-select: text;
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