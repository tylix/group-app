<template>
    <div class="expanded" v-if="this.expanded">
        <button class="save__btn" v-if="JSON.stringify(this.group) !== JSON.stringify(this.updatedGroup)" @click="this.saveGroup">Save
        </button>
        <div class="settings__categories" v-for="(category, index) in this.categories" :key="index"
            @click="this.selectedCategory = index" :style="this.selectedCategory === index ? {
                backgroundColor: 'var(--color-blue-soft)',
            } : {}">
            {{ category }}
        </div>
        <div class="settings__settings">
            <div class="settings__item" v-for="(setting, index) in this.getItems(this.selectedCategory)" :key="index">
                <label>{{ setting.name }}</label>
                <input v-if="setting.type === 'input'" v-model="setting.model" type="text" />

                <button v-else-if="setting.type === 'button'" :style="{ backgroundColor: setting.data.color }"
                    @click="this.button(setting)">
                    {{ setting?.data.text }}
                </button>
                <div v-else-if="setting.type === 'switch'">
                    <div class="hover-text">
                        <label class="switch">
                            <input type="checkbox" v-model="setting.model" @click="this.switch(setting)"
                                :disabled="setting.data.disabled" />
                            <span class="slider round" />
                        </label>
                        <span class="tooltip-text" id="left" v-if="setting.data.disabled">You have to enable your two factor
                            authentication!</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="collapsed" v-else>
        <div class="settings__header">
            <h3 class="settings__title">Settings</h3>
        </div>

    </div>
    <!--<div class="header">
        <h1>Settings</h1>

        <button v-if="JSON.stringify(this.group) !== JSON.stringify(this.updatedGroup)" @click="this.saveGroup">Save
        </button>
    </div>
    <div class="settings">
        <div class="settings-content">
            <div class="scategory" v-for="(category, index) in this.categories" :key="index">
                <div class="cheader" @click="this.expandedCategories[index] = !this.expandedCategories[index]">
                    <h3>{{ category }}</h3>
                    <i :class="'bx bx-chevron-' + (this.expandedCategories[index] ? 'down' : 'up')"/>
                </div>
                <div v-for="item in getItems(index)" :key="item.name" v-if="this.expandedCategories[index]">
                    <div class="settings-main-item-input"
                         v-if="item.role !== undefined ? this.$groups.getGroupRole(this.group, this.user.uid) <= item.role : true">
                        <p>{{ item.name }}</p>
                        <input v-model="item.model" type="text" v-if="item.type === 'input'"/>
                        <button v-else-if="item.type === 'button'" :style="{ backgroundColor: item.data.color }"
                                @click="this.button(item)">
                            {{ item?.data.text }}
                        </button>
                        <div v-else-if="item.type === 'switch'">
                            <div class="hover-text">
                                <label class="switch">
                                    <input type="checkbox" v-model="item.model" @click="this.switch(item)" :disabled="item.data.disabled"/>
                                    <span class="slider round"/>
                                </label>
                                <span class="tooltip-text" id="left" v-if="item.data.disabled">You have to enable your two factor authentication!</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>-->
</template>

<script>
export default {
    name: "SettingsComponent",
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
            user: undefined,
            updatedGroup: undefined,
            categories: [
                'General',
                'Chat',
                'Advanced',
            ],
            selectedCategory: 0,
            items: [
                {
                    name: 'Name',
                    type: 'input',
                    category: 0,
                    model: this.group.name
                },
                {
                    name: 'Description',
                    type: 'input',
                    category: 0,
                    model: this.group.description
                },
                {
                    name: 'Require Two Factor Authentication',
                    type: 'switch',
                    category: 2,
                    role: 0,
                    model: this.group.settingMap.requireTfa,
                    data: {
                        disabled: !this.$auth.isOtpEnabled()
                    }
                },
                {
                    name: 'Delete group',
                    type: 'button',
                    category: 2,
                    role: 0,
                    data: {
                        text: 'Delete',
                        color: 'var(--color-red)',
                    }
                },
                {
                    name: 'Leave group',
                    type: 'button',
                    category: 2,
                    role: 1,
                    data: {
                        text: 'Leave',
                        color: 'var(--color-red)',
                    }
                },
                {
                    name: 'Clear chat',
                    type: 'button',
                    category: 1,
                    role: 0,
                    data: {
                        text: 'Clear',
                        color: 'var(--color-red)',
                    }
                },
                {
                    name: 'Clear your chat',
                    type: 'button',
                    category: 1,
                    role: 1,
                    data: {
                        text: 'Clear',
                        color: 'var(--color-red)',
                    }
                },
                {
                    name: 'Delete your messages',
                    type: 'button',
                    category: 1,
                    role: 1,
                    data: {
                        text: 'Delete',
                        color: 'var(--color-red)',
                    }
                }
            ]
        }
    },
    methods: {
        getItems(index) {
            return this.items.filter(item => item.category === index)
        },
        button(item) {
            if (item.name === 'Delete group') {
                this.$groups.deleteGroup(this.group.id).then(() => {
                    this.$emit('close')
                })
                return
            }
            if (item.name === 'Leave group') {
                let group = this.group
                group.member = group.member.filter(member => member.id !== this.user.uid)
                this.$groups.leaveGroup(group.id).then(() => {
                    this.$emit('close')
                })
            }
        },
        switch(item) {
            console.log(this.updatedGroup)
        },
        saveGroup() {
            this.$groups.updateGroup(this.updatedGroup).then(() => {
                this.$emit('update', this.updatedGroup)
            })
        }
    },
    watch: {
        items: {
            handler(val) {
                this.updatedGroup.name = val[0].model
                this.updatedGroup.description = val[1].model
                this.updatedGroup.settingMap.requireTfa = val[2].model
            },
            deep: true
        }
    },
    mounted() {
        this.updatedGroup = JSON.parse(JSON.stringify(this.group))
        this.user = JSON.parse(localStorage.getItem('user'))
    }
}
</script>

<style>
.save__btn {
    position: absolute;
    bottom: 0;
    right: 0;
    margin: 10px;
    padding: 10px;
    border-radius: 10px;
    background-color: var(--color-blue);
    color: var(--color-text);
    font-weight: bold;
    font-size: 16px;
    transition: 0.4s ease-in-out;
}

.settings__categories {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 0 25px;
    margin-bottom: 10px;
    margin-left: 10px;
    width: 9%;
    border-radius: 10px;
    color: var(--color-text-muted);
}

.settings__categories:hover {
    cursor: pointer;
    color: var(--color-text);
    transition: 0.4s ease-in-out;
}

.settings__settings {
    position: absolute;
    left: 20%;
    top: 0;
}

.settings__item {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 0 25px;
    margin-bottom: 10px;
    margin-left: 10px;
    width: 500px;
    border-radius: 10px;
    color: var(--color-text-muted);
    font-size: 17px;
}

.settings__item input {
    border: none;
    padding: 5px;
    background-color: var(--color-background-modern-mute);
    border-radius: 5px;
    font-size: 15px;
    color: var(--color-text);
}



.settings__header {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 0 25px;
}

.settings__title {
    font-size: 17px;
    padding: 0 10px;
    color: var(--color-text-muted)
}



.header button {
    background-color: var(--color-green);
}

.settings {
    background-color: var(--color-background);
    border-radius: 10px;
    padding: 10px 10px 20px;
}

.settings-main-item-input {
    margin-top: 10px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
}

.settings-main-item-input input {
    border: none;
    padding: 5px;
    background-color: var(--color-background-mute);
    border-radius: 5px;
    outline: none;
    color: var(--color-text);
    font-size: 14px;
}

.scategory .cheader {
    margin: 20px 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

button {
    border: none;
    padding: 6px 15px;
    border-radius: 5px;
    cursor: pointer;
}

.cheader {
    cursor: pointer;
}

.switch {
    position: relative;
    display: inline-block;
    width: 60px;
    height: 27px;
}

.switch input {
    opacity: 0;
    width: 0;
    height: 0;
}

.slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--color-background-mute);
    -webkit-transition: .4s;
    transition: .4s;
    border-radius: 28px;
}

.slider:before {
    position: absolute;
    content: "";
    height: 20px;
    width: 20px;
    left: 6px;
    bottom: 4px;
    background-color: var(--color-background);
    -webkit-transition: .4s;
    transition: .4s;
    border-radius: 50%;
}

input:checked+.slider {
    background-color: var(--color-green);
}

input:focus+.slider {
    box-shadow: 0 0 1px var(--color-green);
}

input:checked+.slider:before {
    -webkit-transform: translateX(26px);
    -ms-transform: translateX(26px);
    transform: translateX(26px);
}

.slider.round {
    border-radius: 34px;
}

.slider.round:before {
    border-radius: 50%;
}

.tooltip-text {
    visibility: hidden;
    position: absolute;
    z-index: 1;
    width: 180px;
    color: white;
    font-size: 12px;
    background-color: #192733;
    border-radius: 10px;
    padding: 10px 15px 10px 15px;
}

.hover-text:hover .tooltip-text {
    visibility: visible;
}

#left {
    top: -8px;
    right: 120%;
}
</style>