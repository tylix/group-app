<template>
    <div>
        <div class="settings-header">
            <h1>Settings</h1>
        </div>
        <button @click="this.test()">Test</button>
        <div class="settings-g">
            <div class="scategory" v-for="(category, index) in this.categories" :key="index">
                <div class="cheader" @click="this.expandedCategories[index] = !this.expandedCategories[index]">
                    <h3>{{ category }}</h3>
                    <i :class="'bx bx-chevron-' + (this.expandedCategories[index] ? 'down' : 'up')"/>
                </div>
                <div v-for="item in getItems(index)" :key="item.name" v-if="this.expandedCategories[index]">
                    <div class="settings-main-item-input">
                        <p>{{ item.name }}</p>



                        <input v-model="item.model" :type="item.data.inputType ? item.data.inputType : 'text'" v-if="item.type === 'input'"/>

                        <button v-else-if="item.type === 'button'" :style="{ backgroundColor: item.data.color }"
                                @click="this.button(item)">
                            {{ item?.data.text }}
                        </button>

                        <component class="component" :is="item.data.path" v-else-if="item.type === 'component'"/>


                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import TwoFactorSettingComponent from "@/components/settings/TwoFactorSettingComponent.vue";
export default {
    name: "SettingsView",
    data() {
        return {
            categories: [
                'Profile',
                'Chat',
                'Security',
                'Advanced',
            ],
            expandedCategories: [],
            items: [
                {
                    name: 'Username',
                    type: 'input',
                    category: 0,
                    model: JSON.parse(localStorage.getItem('user')).username,
                    data: {
                        inputType: 'text'
                    }
                },
                {
                    name: 'First Name',
                    type: 'input',
                    category: 0,
                    model: JSON.parse(localStorage.getItem('user')).firstName,
                    data: {
                        inputType: 'text'
                    }
                },
                {
                    name: 'Last Name',
                    type: 'input',
                    category: 0,
                    model: JSON.parse(localStorage.getItem('user')).lastName,
                    data: {
                        inputType: 'text'
                    }
                },
                {
                    name: 'Two Factor',
                    type: 'component',
                    category: 2,
                    data: {
                        path: TwoFactorSettingComponent
                    }
                }
            ]
        }
    },
    methods: {
        getItems(index) {
            return this.items.filter(item => item.category === index)
        },
        test() {
            this.$toast.showNotification('Test', 5000, 'info')
        }
    },
    mounted() {
        this.expandedCategories = this.categories.map(() => true)
    }
}
</script>

<style>
.settings-g {
    -webkit-user-select: none;
    position: fixed;
    left: 15%;
    background-color: var(--color-background-mute);
    border-radius: 10px;
    width: 70%;
}

.scategory .cheader {
    margin: 20px 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.cheader {
    cursor: pointer;
}

.settings-main-item-input {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 10px 20px;
}

.settings-main-item-input p {
    margin: 0;
}

.settings-main-item-input input {
    width: 70%;
    border: none;
    border-bottom: 1px solid var(--color-background);
    background-color: transparent;
    outline: none;
    padding: 5px;
    font-size: 16px;
}

.settings-main-item-input button {
    border: none;
    padding: 6px 15px;
    border-radius: 5px;
    cursor: pointer;
}

</style>