<template>
    <div class="expanded" v-if="this.expanded">
        Member list:
    </div>
    <div v-else class="collapsed">
        <h3 class="member__title">Member</h3>
        <div class="member__list">
            <i v-if="this.loading" class="bx bx-loader-alt bx-spin"/>
            <UsernameComponent v-for="member in this.member" :user="member" show-avatar :show-name="false" online-status/>
        </div>
    </div>
  <!--<div class="header">
      <h1>Member</h1>
      <button style="background-color: var(--color-background-mute); margin-left: 250px;  color: var(--color-text)">
          Requests
      </button>
      <button style="background-color: var(--color-background-mute); color: var(--color-text)">Invited</button>
      <i @click="this.searchMember = !this.searchMember" :class="'bx bx-' + (this.searchMember ? 'minus' : 'plus')"/>
  </div>
  <div class="member-list">
      <div class="search-box" v-if="this.searchMember">
          <i class="bx bx-search"/>
          <input class="user-search" type="text" placeholder="Search member" @input="this.isTyping = true"
                 v-model="this.searchQuery"/>
      </div>
      <div class="search-results" v-if="this.searchMember">
          <p v-if="this.isLoading">Loading...</p>
          <div class=member v-for="(result, index) in searchResult">
              <UsernameComponent :user="result" avatar/>
              <button class="invite-btn" :style="
              {
                  'background-color': this.$groups.isInvited(group, result) ? 'var(--color-blue)' : 'var(--color-green)',
              }
                  " @click="this.inviteRevokeMember(result)">
                  {{ this.$groups.isInvited(group, result) ? 'Revoke' : 'Invite' }}
              </button>
              <hr v-if="index > 0"/>
          </div>
          <p v-if="this.searchResult.length === 0 && !isLoading && this.searchQuery">No entry found.</p>
      </div>
      <div class="member" v-for="member in this.member" v-else>
          <UsernameComponent avatar :user="member"/>
          <div class="member-edit">
              <i class="bx bx-chevron-up" v-if="this.selectedMember === member"/>
              <i class="bx bx-chevron-down" v-else/>
          </div>
      </div>
  </div>-->
</template>

<script>
import _ from 'lodash';
import UsernameComponent from "@/components/UsernameComponent.vue";

export default {
    name: "MemberComponent",
    components: {UsernameComponent},
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
            loading: false
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
        inviteRevokeMember(member) {
            const group = JSON.parse(JSON.stringify(this.group));
            if (this.$groups.isInvited(group, member))
                group.invited = group.invited.filter(invited => invited.id !== member.uid);
            else
                group.invited.push({id: member.uid, timestamp: Date.now(), role: 0});
            this.$groups.updateGroup(group).then(() => {
                this.searchMember = false
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
    padding: 10px 25px;
    color: var(--color-text-muted)
}

.member__list {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    padding: 0 25px;
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

.header i {
    cursor: pointer;
    font-size: 24px;
}

.invite-btn {
//background-color: var(--color-green); border: none; border-radius: 5px; padding: 5px; color: var(--color-text);
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

.search-box input {
    border: none;
    background-color: var(--color-background);
    font-size: 15px;
    padding: 5px 10px;
    outline: none;
    color: var(--color-text);
    width: 120%;
}

.member hr {
    position: absolute;
    width: 100%;
    margin-bottom: 40px;
    border: 1px solid var(--color-background-mute);
}
</style>