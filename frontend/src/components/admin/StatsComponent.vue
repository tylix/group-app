<template>
    <div class="range__picker">
        <div class="range__input">
            <div>
                <label for="from">Period from</label>
                <input type="number" id="from" v-model="this.period.from">
            </div>
            <div>
                <label for="to">Period to</label>
                <input type="number" id="to" v-model="this.period.to">
            </div>

            <button @click="this.loadStats()">Update</button>

            <div>
                <button v-for="(item, index) in this.period.chartPeriods" :key="index"
                    @click="{ this.period.chartPeriod = index; this.loadStats() }"
                    :style="{ backgroundColor: index === this.period.chartPeriod ? 'var(--color-background-soft)' : 'var(--color-background)' }">
                    {{ item }}
                </button>
            </div>
        </div>

        <div>
            {{ new Date(this.period.from) }}
            {{ new Date(this.period.to) }}
        </div>
    </div>
    <div class="charts" v-if="!this.loading">
        <div v-for="(item, index) in this.statsTypes" :key="index" class="chart-container">
            <ChartComponent v-if="this.charts[item.name].chartData.datasets[0].data.length > 0"
                :chartData="this.charts[item.name].chartData" :chartOptions="this.charts[item.name].options"
                :chartType="'line'" :trends="this.charts[item.name].trends" />
        </div>
    </div>
</template>

<script>
import ChartComponent from '@/components/ChartComponent.vue'

export default {
    name: 'StatsComponent',
    components: { ChartComponent },
    data() {
        return {
            selectedDate: null,
            dateFormat: 'yyyy-MM-dd',
            statistics: [],
            statsTypes: [
                {
                    name: 'GROUPS',
                    lables: ['Groups', 'Group Member']
                },
                {
                    name: 'MESSAGES',
                    lables: ['Messages']
                },
                {
                    name: 'ACCOUNTS',
                    lables: ['Accounts']
                },
                {
                    name: 'NOTIFICATIONS',
                    lables: ['Total Notifications']
                }
            ],
            period: {
                from: 1686044630569,
                to: new Date().getTime(),
                chartPeriod: 0,
                chartPeriods: [
                    'Monthly',
                    'Weekly',
                    'Daily',
                    'Hourly'
                ]
            },
            charts: {},
            loading: true
        }
    },
    mounted() {
        this.loadStats()
    },
    methods: {
        loadStats() {
            this.loading = true
            this.$emit('loaded', true)
            const colors = [
                "#102A43",
                "#475569",
                "#6B7D8B"
            ]
            this.statsTypes.forEach(type => {
                const dataSets = []
                type.lables.forEach((label, index) => {
                    dataSets.push({
                        label: label,
                        backgroundColor: colors[index],
                        borderColor: colors[index],
                        data: [],
                        tension: 0.1
                    })
                })
                this.charts[type.name] = {
                    trends: [],
                    chartData: {
                        labels: [],
                        datasets: dataSets
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            yAxes: [{
                                ticks: {
                                    beginAtZero: true
                                }
                            }]
                        }
                    }
                }
            })


            const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September',
                'October', 'November', 'December']

            this.statistics = []
            let statsObject = []

            this.$stats.getStats(this.period.from, this.period.to).then(res => {
                this.statistics = res

                this.statsTypes.forEach((item) => {
                    const stats = this.getStats(item.name);

                    stats.forEach((stat, index) => {

                        stat.data.forEach(data => {
                            const timestamp = data.timestamp
                            const lable = this.period.chartPeriod === 0 ? new Date(timestamp).getMonth() : this.period.chartPeriod === 2 ? new Date(timestamp).getDate() : new Date(timestamp).getFullYear()

                            if (!statsObject[index])
                                statsObject[index] = []

                            if (statsObject[index][lable])
                                statsObject[index][lable].push(data)
                            else
                                statsObject[index][lable] = [data]
                        })

                        statsObject.forEach((object, itemIndex) => {
                            if (object)
                                object.forEach((data, index) => {
                                    const lable = this.period.chartPeriod === 0 ? months[index] : this.period.chartPeriod === 2 ? `${index}.` : 0
                                    const groups = data.length

                                    if (!this.charts[item.name].chartData.labels.includes(lable))
                                        this.charts[item.name].chartData.labels.push(lable)

                                    this.charts[item.name].chartData.datasets[itemIndex].data.push(groups)

                                    statsObject[itemIndex][index] = []
                                })
                        })
                        statsObject[index] = undefined
                        this.charts[item.name].trends.push(stat.trends)
                    })

                })

                this.loading = false
                this.$emit('loaded', false)
            }).catch(err => {
                this.$toast.showNotification('Something went wrong.', 2500, 'error')
            })


        },
        getStats(type = 'GROUPS') {
            if (!this.statistics[type]) return undefined
            return this.statistics[type]
        }
    }
}
</script>

<style>
.charts {
    top: 20%;
    width: 90%;
    height: 60%;
    position: fixed;
    display: flex;
}

.range__picker {
    position: fixed;
    top: 10%;
    gap: 10px;
}

.range__input {
    display: flex;
    gap: 50px;
}
</style>