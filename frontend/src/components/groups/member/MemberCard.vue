<template>
    <div class="member__card">
        <div class="card__header">

        </div>
        <div>
            <button>Remove</button>
            <select>
                <option>Admin</option>
                <option>Member</option>
            </select>
        </div>
    </div>
</template>

<script>
export default {
    name: "MemberCad",
    props: {
        user: {
            type: Object,
            required: true
        }
    },
    data() {
        return {

        }
    }
}
</script>

<style>
</style>