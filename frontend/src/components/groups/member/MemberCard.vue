<template>
    <div class="member__card">
        <div class="card__header">
            <div class="card__name">
                {{ this.mem }}
                <h3>{{ this.user.firstName }} {{ this.user.lastName }}</h3>
                <h4>{{ this.user.username }}</h4>
                <h5>Joined group {{ this.$groups.time_ago(this.memberData.timestamp) }}</h5>
            </div>
        </div>
        <div>
            <button>Remove</button>
            <select>
                <option v-for="(item, index) in this.roles" :key="index" :disabled="this.memberData.role >= item.id"
                    :selected="this.memberData.role === item.id">
                    {{ item.name }}</option>
            </select>
        </div>
    </div>
</template>

<script>
import { registerRuntimeHelpers } from '@vue/compiler-core';

export default {
    name: "MemberCad",
    props: {
        user: {
            type: Object,
            required: true
        },
        memberData: {
            type: Object,
            required: true
        }
    },
    data() {
        return {
            roles: [
                {
                    name: 'Owner',
                    id: 0,
                },
                {
                    name: 'Admin',
                    id: -1
                },
                {
                    name: 'Member',
                    id: 1
                }]
        }
    },
    methods: {
        getUser() {
            if (!localStorage.getItem('user')) return null
            return JSON.parse(localStorage.getItem('user'))
        }
    }
}
</script>

<style></style>