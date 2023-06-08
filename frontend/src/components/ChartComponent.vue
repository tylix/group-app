<template>
    <div v-if="this.details">
    </div>
    <div v-else class="chart">
        <Line v-if="this.chartType === 'line'" :data="this.chartData" :options="this.chartOptions" />
        <Bar v-else-if="this.chartType === 'bar'" :data="this.chartData" :options="this.chartOptions" />
    </div>
    <div class="trends" v-for="(trendItems, index) in this.trends.filter(trend => trend.length > 0)" :key="index" v-if="this.trends">
        <div class="trend-card" v-for="(trend, index) in trendItems" :key="index">
            <div class="trend-card-title">
                {{ trend.trendType }}
            </div>
            <div class="trend-card-body">
                <p>This period: {{ trend.amountPeriod }}</p>
                <p>Last period: {{ trend.amountLastPeriod }}</p>
                <div class="trend-perc">
                    <i :class="`bx bx-trending-${trend.percentagePeriod < 0 ? 'down' : 'up'}`" :style="{
                        color: trend.percentagePeriod < 0 ? 'var(--color-red)' : 'var(--color-green)'
                    }"/>
                    <p>{{ trend.percentagePeriod }}% {{ trend.percentagePeriod < 0 ? 'decrease' : 'increase' }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { Line, Bar } from 'vue-chartjs'
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
} from 'chart.js'

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
)

export default {
    name: 'ChartComponent',
    data() {
        return {

        }
    },
    components: {
        Line,
        Bar
    },
    props: {
        chartData: {
            type: Object,
            default: null
        },
        chartOptions: {
            type: Object,
            default: null
        },
        chartType: {
            type: String,
            default: null
        },
        details: {
            type: Boolean,
            default: false
        },
        trends: {
            type: Array,

        }
    }
}
</script>

<style>
.chart {
    height: 200px;
    width: 400px;
    position: relative;
}

.trends {
    font-size: 11px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    top: 5%;
    margin-left: 20px;
}

.trend-card {
    border: 2px solid var(--color-background);
    background-color: var(--color-background-modern-mute);
    border-radius: 5px;
    width: 50%;
    margin-left: 10px;
    padding: 8px;
    margin: 10px;
}

.trend-card-title {
    font-size: 14px;
}

.trend-perc {
    display: flex;
    align-items: center;
    gap: 7px;
    font-size: 13px;
}

.trend-perc i {
    font-size: 19px;
}
</style>