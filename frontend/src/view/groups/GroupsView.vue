<template>
    <div class="group__search">
        <div class="search__bar">
            <input type="text" v-model="this.search" placeholder="Search..." />
        </div>
    </div>
    <div class="groups__view">
        <div class="join__group" v-if="this.joinGroup">
            <div class="join__group__header">
                <h2>Join Group</h2>
                <h3>{{ this.joinGroup.name }}</h3>
                <h4>{{ this.joinGroup.description }}</h4>
            </div>
            <div class="join__group__member">
                <h4>Members</h4>
                <div class="join__group__member__list">
                    <div class="join__group__member__item" v-for="member in this.joinGroup.member" :key="member.id">
                        <img class="join__group__member__item__icon" v-if="member.icon" />
                        <i v-else class="join__group__member__item__icon bx bx-user" />
                        <h5>{{ member }}</h5>
                    </div>
                </div>
            </div>
            <div class="join__group__actions">
                <div @click="this.joinGroup" class="join__group__actions__join">
                    <button>Join</button>
                </div>
                <div @click="this.cancelJoin()" class="join__group__actions__cancel">
                    <button>Cancel</button>
                </div>
            </div>
        </div>
        <div class="group__" v-else>
            <div :class="this.selectedGroup?.id === group.id ? 'group__expanded' : 'group__list'"
                v-for="group in this.groups" v-if="!this.loading" :key="group.id" :draggable="true"
                @dragstart="dragStart(group, $event)" @dragend="dragEnd" @dragover="dragOver" @drop="drop(group)">
                <div class="group__header">
                    <img class="group__icon" v-if="group.icon">
                    <i v-else class="group__icon bx bx-group" />
                    <div class="group__title" @click="this.toggleSelectGroup(group)">
                        <h2>{{ group.name }}</h2>
                        <h5>{{ group.description }}</h5>
                    </div>
                    <div class="expanded__categories" v-if="this.selectedGroup?.id === group.id">
                        <p class="expanded__category" v-for="category in this.groupCategories"
                            @click="this.selectedCategory = category" :style="{
                                color: this.selectedCategory === category ? 'var(--color-text)' : 'var(--color-text-light)',
                            }">
                            <i :class="category.icon" />
                            {{ category.name }}
                        </p>
                    </div>
                    <i class="group__pin bx bx-pin" />
                </div>
                <hr class="header__hr" />

                <div class="group__body" :draggable="true">
                    <div v-for="(category, index) in this.groupCategories" :key="index">
                        <div
                            v-if="this.selectedCategory === undefined ? true : this.selectedGroup?.id === group.id ? this.selectedCategory?.name === category?.name : true">

                            <component class="group__component" :is="category.component" :group="group"
                                :expanded="this.selectedGroup?.id === group.id" />
                            <hr class="body__hr" v-if="this.selectedGroup?.id !== group.id && index !== this.groupCategories.length - 1" />
                        </div>
                    </div>
                </div>
                <div class="group__footer">
                    <i class="group__move bx bx-move" />
                </div>
            </div>
            <i v-if="this.loading" class="group__loading bx bx-loader-alt bx-spin" />
            <i v-if="!this.loading && !this.createGroup" class="group__add bx bx-plus" @click="this.toggleCreateGroup" />
            <CreateGroupComponent v-if="this.createGroup && !this.selectedGroup" @close="this.update"
                @cancel="this.createGroup = false" />
        </div>
    </div>
</template>

<script>
import MemberComponent from "@/components/groups/MemberComponent.vue";
import ChatComponent from "@/components/groups/ChatComponent.vue";
import CreateGroupComponent from "@/components/groups/CreateGroupComponent.vue";
import SettingsComponent from "@/components/groups/SettingsComponent.vue";

export default {
    name: "GroupsView",
    components: { MemberComponent, CreateGroupComponent },
    data() {
        return {
            selectedGroup: undefined,
            selectedCategory: undefined,
            search: '',
            createGroup: false,
            loading: false,
            dragged: undefined,
            groupCategories: [
                {
                    name: 'Member',
                    icon: 'bx bxs-user-detail',
                    component: MemberComponent
                },
                {
                    name: 'Settings',
                    icon: 'bx bxs-cog',
                    component: SettingsComponent
                },
                {
                    name: 'Chat',
                    icon: 'bx bxs-cog',
                    component: ChatComponent
                },
            ],
            groups: [],
            joinGroup: undefined
        }
    },
    /*props: {
        id: {
            type: String,
            required: false
        }
    },*/
    mounted() {
        const invlink = this.$route.query.invlink
        if (invlink) {
            this.$groups.getGroupByInviteLink(invlink).then(group => {
                this.joinGroup = group
            })
        }
        this.loadGroups()
        /*if(this.id) {
            this.$groups.getGroupById(this.id).then(group => {
                this.selectedGroup = group
            })
        }*/
    },
    methods: {
        dragStart(item, event) {
            console.log(event.target.classList)
            if (event.target.classList.contains('group__move')) {
                this.dragged = item;
            } else {
                event.preventDefault();
            }
        },
        dragEnd() {
            this.dragged = undefined
        },
        dragOver(event) {
            event.preventDefault()
        },
        drop(item) {
            if (this.dragged && this.dragged !== item) {
                const dragIndex = this.items.indexOf(this.dragged);
                const dropIndex = this.items.indexOf(item);
                [this.items[dragIndex], this.items[dropIndex]] = [this.items[dropIndex], this.items[dragIndex]];
            }
        },
        loadGroups() {
            this.loading = true
            const user = JSON.parse(localStorage.getItem('user'))
            this.groups = []
            this.$groups.getGroupByUser(user.uid).then(groups => {
                this.groups = groups
                this.loading = false
            })
        },
        toggleSelectGroup(group) {
            this.selectedCategory = this.groupCategories[0]
            this.selectedGroup === group ? this.selectedGroup = undefined : this.selectedGroup = group
            this.createGroup = false
        },
        toggleCreateGroup() {
            this.createGroup = !this.createGroup
            this.selectedGroup = undefined
        },
        update() {
            this.selectedGroup = undefined
            this.createGroup = false
            this.loadGroups()
        },
        updateGroupSettings(data) {
            this.selectedGroup = undefined
            this.selectedGroup = JSON.parse(JSON.stringify(data))
            this.loadGroups()
        },
        cancelJoin() {
            this.joinGroup = false
            
            const currentRoute = this.$router.currentRoute
            const { query, ...params } = currentRoute
            this.$router.replace({...params})
        },
    },
    watch: {
        search: function (val) {
            this.$groups.getGroupByUser().then(groups => {
                this.groups = Object.values(groups).filter(value => {
                    return JSON.stringify(value).toLowerCase().includes(val.toLowerCase());
                });
            })
        }
    }
}
</script>

<style>
.group__search {
    position: fixed;
    top: 9%;
    left: 6%;
}

.search__bar input {
    width: 100%;
    height: 100%;
    outline: none;
    background: none;
    font-size: 22px;
    color: var(--color-text-muted);
    border: 1px solid var(--color-background-mute);
    border-radius: 5px;
    padding-left: 10px;
}

.group__ {
    position: absolute;
    height: 100%;
    display: flex;
    /*border: 1px solid red;*/
}

.groups__view {
    position: fixed;
    top: 15%;
    left: 5%;
    height: 80%;
    width: 90%;
    border-radius: 10px;
    /*border: 1px solid red; display: flex; flex-direction: row;*/
    -webkit-user-select: none;
    gap: 10px;
    overflow-x: scroll;
    overflow-y: hidden;
}

.groups__view::-webkit-scrollbar {
    height: 4px;
    border-radius: 50px;
    display: none;
    background: var(--color-background-soft);
}

.groups__view::-webkit-scrollbar-track {
    border-radius: 10px;
}

.groups__view::-webkit-scrollbar-thumb {
    background: var(--color-background-soft);
    border-radius: 10px;
}

.groups__view::-webkit-scrollbar-thumb:hover {
    background: var(--color-background-mute);
    border-radius: 10px;
}

.groups__view .header__hr {
    margin: 0 20px;
    border: 1px solid var(--color-background-soft);
}

.group__list {
    margin-top: 20px;
    margin-left: 20px;
    height: 94%;
    width: 340px;
    border: 1px solid var(--color-background-mute);
    border-radius: 10px;
    transition: all 0.4s ease-in-out;
    overflow: scroll;
}

.group__header {
    display: flex;
    align-items: center;
    margin: 20px;
    color: var(--color-text-muted);
}

.group__pin {
    margin-left: auto;
    font-size: 20px;
    cursor: pointer;
    padding-right: 10px;
}

.group__icon {
    font-size: 30px;
    margin-right: 10px;
}

.group__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
    margin-left: 10px;
    bottom: 10px;
    right: 10px;
    font-size: 18px;
    position: absolute;
    cursor: grab;
}

.group__add {
    cursor: pointer;
    font-size: 30px;
    color: var(--color-text-muted);
    top: 50%;
    left: 10rem;
    padding-right: 160px;
}

.group__add:hover {
    color: var(--color-heading);
    transition: 0.9s;
}

.group__loading {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 25px;
}

.group__body {}

.group__expanded {
    margin-top: 20px;
    margin-left: 20px;
    height: 94%;
    width: 1394px;
    border: 1px solid var(--color-background-mute);
    border-radius: 10px;
    transition: 0.5s;
}

.category__hr {
    margin: 20px 20px;
    border: 1px solid var(--color-background);
}

.group__component {
    margin-top: 20px;
}

.body__hr {
    margin: 20px 20px;
    border: 1px solid var(--color-background);
}

.expanded__categories {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 20px;
    gap: 30px;
    left: 10cm;
}

.expanded__category {
    display: flex;
    align-items: center;
    cursor: pointer;
    font-size: 18px;
    gap: 5px;
}

.group__title {
    cursor: pointer;
}
</style>