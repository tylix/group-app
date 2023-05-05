<template>
    <div class="group__search">
        <div class="search__bar">
            <input type="text" v-model="this.search" placeholder="Search..."/>
        </div>
    </div>
    <div class="groups__view">
        <div class="group__">
            <div :class="this.selectedGroup?.id === group.id ? 'group__expanded' : 'group__list'" v-for="group in this.groups" v-if="!this.loading" :key="group.id"
                 :draggable="true" @dragstart="dragStart(group, $event)"
                 @dragend="dragEnd"
                 @dragover="dragOver"
                 @drop="drop(group)">
                <div class="group__header">
                    <img class="group__icon" v-if="group.icon">
                    <i v-else class="group__icon bx bx-group"/>
                    <h2>{{ group.name }}</h2>
                    <h3>{{ group.description }}</h3>
                    <i class="group__pin bx bx-pin"/>
                </div>
                <hr class="header__hr"/>

                <div class="group__body" :draggable="true" >
                    <component class="group__component" v-for="category in this.groupCategories" :key="category.name"
                               :is="category.component" :group="group" :expanded="this.selectedGroup?.id === group.id"/>
                </div>
                <div class="group__footer">
                    <i class="group__move bx bx-move"/>
                </div>
            </div>
            <i v-if="this.loading" class="group__loading bx bx-loader-alt bx-spin"/>
            <i class="group__add bx bx-plus" @click="this.toggleCreateGroup" v-else/>
        </div>
    </div>
  <!--<div class="groups">
      <div class="search-section">
          <div class="search-bar">
              <i class="bx bx-search"/>
              <input type="text" v-model="this.search" placeholder="Search for groups"/>
          </div>
          <div @click="this.toggleCreateGroup" class="create">
              <i :class="'bx bx-' + (this.createGroup ? 'minus' : 'plus')"/>
          </div>
      </div>
      <div class="group-list">
          <div class="group" v-for="group in this.groups" :key="group.id"
               @click="this.toggleSelectGroup(group)">
              <div class="group-header">
                  <h3>{{ group.name }}</h3>
                  <i class="bx bx-chevron-right" v-if="this.selectedGroup === group"/>
                  <i class="bx bx-chevron-left" v-else/>
              </div>
              <p>{{ group.description }}</p>
          </div>
      </div>
  </div>

  <div class="categories" v-if="this.selectedGroup">
      <div class="tabs">
          <h2>{{this.selectedGroup.name}}</h2>
          <div class="tab" v-for="category in this.groupCategories" :key="category.name"
               @click="this.selectedCategory = category.component">
              <h3 :style="{textDecoration: this.selectedCategory === category.component ?  'underline' : 'none'}">
                  {{ category.name }}
              </h3>
              <i class="icon" :class="category.icon"/>
          </div>
      </div>

      <div class="category" v-if="this.selectedCategory">
          <component :is="this.selectedCategory" :group="this.selectedGroup" @close="this.update" @update="this.updateGroupSettings"/>
      </div>
  </div>

  <CreateGroupComponent v-if="this.createGroup && !this.selectedGroup" @close="this.update"/>-->
    <CreateGroupComponent v-if="this.createGroup && !this.selectedGroup" @close="this.update"/>
</template>

<script>
import MemberComponent from "@/components/groups/MemberComponent.vue";
import ChatComponent from "@/components/groups/ChatComponent.vue";
import CreateGroupComponent from "@/components/groups/CreateGroupComponent.vue";
import SettingsComponent from "@/components/groups/SettingsComponent.vue";

export default {
    name: "GroupsView",
    components: {MemberComponent, CreateGroupComponent},
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
            ],
            groups: []
        }
    },
    /*props: {
        id: {
            type: String,
            required: false
        }
    },*/
    mounted() {
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
            console.log(group)
            this.selectedCategory = undefined
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
        }
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
//border: 1px solid red;
}

.groups__view {
    position: fixed;
    top: 15%;
    left: 5%;
    height: 80%;
    width: 90%;
    border-radius: 10px;
//border: 1px solid red; display: flex; flex-direction: row;
    -webkit-user-select: none;
    gap: 10px;
    overflow-x: scroll;
    overflow-y: hidden;
}

.groups__view::-webkit-scrollbar {
    height: 4px;
    border-radius: 50px;
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
    width: 300px;
    border: 1px solid var(--color-background-mute);
    border-radius: 10px;
    transition: all 0.4s ease-in-out;
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
    left: 7%;
    padding-right: 60px;
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

.group__body {
}

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



.create {
    cursor: pointer;
    padding: 6px 10px;
    font-size: 25px;
    border-radius: 5px;
    margin-left: 10px;
}

.create:hover {
    color: var(--color-heading);
    transition: 0.9s;
}

.groups {
    -webkit-user-select: none;
    user-select: none;
    position: fixed;
    left: 7%;
    top: 20%;
    background-color: var(--color-background-soft);
    border-radius: 10px;
    padding: 20px 20px;
    width: 24%;
}

.search-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.search-bar {
    display: flex;
    align-items: center;
    border: 1px solid var(--color-background-mute);
    border-radius: 5px;
    padding: 5px 10px;
    width: 85%;
}

.search-bar i {
    font-size: 20px;
    padding-right: 5px;
}

.search-bar input {
    border: none;
    background-color: var(--color-background-soft);
    font-size: 18px;
    padding: 5px 10px;
    outline: none;
    color: var(--color-text);
    width: 91%;
}

.group-list {
    margin-top: 20px;
}

.group {
    cursor: pointer;
    background-color: var(--color-background-soft);
    padding: 8px 10px;
    border-radius: 5px;
}

.group:hover {
    background-color: var(--color-background-mute);
}

.group-header {
    display: flex;
    justify-content: space-between;
}

.group-header i {
    font-size: 20px;
    padding-top: 4px;
}

.group-header h3 {
    font-size: 18px;
}

.categories {
    -webkit-user-select: none;
    user-select: none;
    position: fixed;
    left: 32%;
    top: 4%;
    background-color: var(--color-background-soft);
    border-radius: 10px;
    width: 64%;
}

.category {
    align-items: center;
    padding: 10px 20px;
}

.tabs {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 20px;
}

.tab {
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;
    padding: 5px 10px;
    border-radius: 5px;
}

.tab i {
    font-size: 20px;
    margin-left: 5px;
}

.tab:hover {
    background-color: var(--color-background-mute);
}

.tabs h2 {
    font-size: 24px;
    margin-right: 30px;
}

</style>