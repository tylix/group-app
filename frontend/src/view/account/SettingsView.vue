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
    <ConfirmComponent v-if="this.confirm" @confirm="this.confirm = undefined" @cancel="this.confirm = undefined"/>
</template>

<script>
import TwoFactorSettingComponent from "@/components/settings/TwoFactorSettingComponent.vue";
import ConfirmComponent from "../../components/ConfirmComponent.vue";
export default {
    name: "SettingsView",
    data() {
        return {
            categories: [
                "Profile",
                "Chat",
                "Security",
                "Advanced",
            ],
            confirm: undefined,
            expandedCategories: [],
            items: [
                {
                    name: "Username",
                    type: "input",
                    category: 0,
                    model: JSON.parse(localStorage.getItem("user")).username,
                    data: {
                        inputType: "text"
                    }
                },
                {
                    name: "First Name",
                    type: "input",
                    category: 0,
                    model: JSON.parse(localStorage.getItem("user")).firstName,
                    data: {
                        inputType: "text"
                    }
                },
                {
                    name: "Last Name",
                    type: "input",
                    category: 0,
                    model: JSON.parse(localStorage.getItem("user")).lastName,
                    data: {
                        inputType: "text"
                    }
                },
                {
                    name: "Two Factor",
                    type: "component",
                    category: 2,
                    data: {
                        path: TwoFactorSettingComponent
                    }
                },
                {
                    name: "Delete Account",
                    type: "button",
                    category: 3,
                    data: {
                        text: "Delete",
                        color: "red"
                    }
                }
            ]
        };
    },
    methods: {
        getItems(index) {
            return this.items.filter(item => item.category === index);
        },
        test() {
            this.$toast.showNotification("Test", 5000, "info");
        },
        button(item) {
            if (item.name === "Delete Account") {
                this.confirm = this.confirm ? undefined : 'delete'
            }
        },
        confirmAction() {
            if(this.confirm === 'delete') {

            }
        }
    },
    mounted() {
        this.expandedCategories = this.categories.map(() => true);
    },
    components: { ConfirmComponent }
}
</script>

<style>
.settings-header {
    top: 10%;
    position: fixed;
}

.settings-g {
    -webkit-user-select: none;
    position: fixed;
    left: 15%;
    top: 10%;
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
    width: 50%;
    border: none;
    border-bottom: 1px solid var(--color-background-modern);
    background-color: var(--color-background-modern-mute);
    outline: none;
    padding: 5px;
    font-size: 16px;
    color: var(--color-text);
}

.settings-main-item-input button {
    border: none;
    padding: 6px 15px;
    border-radius: 5px;
    cursor: pointer;
}

</style>