<template>
    <StatsComponent @loaded="this.updateLoading" />
    <i v-if="this.loading" class="chart-loader bx bx-loader-alt bx-spin"></i>
</template>

<script>
import StatsComponent from '../components/admin/StatsComponent.vue';

export default {
    name: "AdminView",
    data() {
        return {
            loading: true
        };
    },
    methods: {
        updateLoading(o) {
            this.loading = o
        }
    },
    mounted() {
    },
    components: { StatsComponent }
}
</script>

<style>
.chart-loader {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 40px;
}
</style>