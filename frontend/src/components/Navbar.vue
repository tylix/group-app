<template>
    <nav class="navbar">
        <div class="navbar__left">
            <div class="navbar__logo">Logo</div>
        </div>
        <div :class="this.expanded ? 'navabar__cright' : 'navbar__center'">
            <router-link v-for="item in items" class="router-link" :to="item.route">
                <i :class="item.icon"></i>
                {{ item.name }}
            </router-link>
        </div>
        <div :class="this.expanded ? 'navbar__right_hide' : 'navbar__right'">
            <div class="navbar__notification">
                <i @click="this.showNotifications = !this.showNotifications" class="bx bx-bell"></i>
               
                <NotificationComponent class="notification__component" :show="showNotifications" />
            </div>
            <div class="navbar__profile">
                <img :src="getUser().avatar ? 'data:image/png;base64,' + getUser().avatar : '/default.png'" alt="Avatar" @click="this.dropdownOpen = !this.dropdownOpen" />
                <div class="navbar__dropdown" v-if="dropdownOpen">
                    <p class="navbar__dropdown__title">Welcome, {{ this.getUser().username }}</p>
                    <a @click="this.clickProfile()">My Profile</a>
                    <a @click="this.$router.push('/settings')">Settings</a>
                    <a @click="this.$auth.logout()">Logout</a>
                </div>
            </div>
        </div>
        <div class="navbar__mobile">
            <i @click="this.expand()" class="bx bx-menu" />
        </div>
    </nav>
</template>

<script>
import NotificationComponent from './NotificationComponent.vue'

export default {
    name: "Navbar",
    data() {
        return {
            dropdownOpen: false,
            items: [
                {
                    name: "Home",
                    route: "/",
                    icon: "bx bx-home",
                },
                {
                    name: "Groups",
                    route: "/groups",
                    icon: "bx bx-group",
                },
                {
                    name: "Contacts",
                    route: "/contacts",
                    icon: "bx bxs-user-detail",
                },
                {
                    name: 'Admin',
                    route: '/admin',
                    icon: 'bx bx-shield-alt'
                }
            ],
            expanded: false,
            notifications: [],
            showNotifications: false
        };
    },
    methods: {
        clickProfile() {
            this.$router.push("/account/" + JSON.parse(localStorage.getItem("user")).username);
            this.dropdownOpen = false;
        },
        getUser() {
            return JSON.parse(localStorage.getItem("user"));
        },
        expand() {
            this.expanded = !this.expanded;
        },
    },
    mounted() {
        if (this.getUser())
            this.$users.getNotifications().then(res => {
                this.notifications = res;
                console.log(this.notifications);
            });
    },
    components: { NotificationComponent }
}
</script>

<style>
.navbar {
    -webkit-user-select: none;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: var(--color-blue-soft);
    padding: 10px;
    top: 0;
    left: 0;
    width: 100%;
    position: fixed;
}

.navbar__left {
    flex: 1;
}

.navbar__center {
    flex: 2;
    display: flex;
    justify-content: center;
}

.navbar__center .router-link {
    display: flex;
    align-items: center;
    margin: 0 10px;
    text-decoration: none;
    color: var(--color-text);
    font-size: 18px;
    font-weight: bold;
}

.navbar__right_hide {
    display: none;
    transition: 0.9s;
}

.navbar__center .router-link:hover {
    background-color: unset;
    transition: 0.9s;
    border-radius: 5px;
    color: var(--color-heading);
}

.navbar__center .router-link i {
    margin-right: 5px;
    font-size: 20px;
}

.navbar__notification i {
    cursor: pointer;
}

.navabar__cright {
    flex: 1;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    transition: 0.9s ease-in-out;
}

.navabar__cright .router-link {
    display: flex;
    align-items: center;
    margin: 0 10px;
    text-decoration: none;
    color: var(--color-text);
    font-size: 18px;
    font-weight: bold;
}

.navabar__cright .router-link:hover {
    background-color: unset;
    transition: 0.9s;
    border-radius: 5px;
    color: var(--color-heading);
}

.navabar__cright .router-link i {
    margin-right: 5px;
    font-size: 20px;
}



.navbar__right {
    flex: 1;
    display: flex;
    justify-content: flex-end;
    align-items: center;
}

.navbar__notification,
.navbar__profile {
    margin: 0 10px;
    font-size: 21px;
}


.navbar__profile {
    position: relative;
}

.navbar__dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    background-color: #0d0f13;
    border: none;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 10px;
    min-width: 150px;
    display: none;
}

.navbar__dropdown a {
    display: block;
    padding: 5px 10px;
    text-decoration: none;
    color: var(--color-text);
}

.navbar__dropdown a:hover {
    background-color: #161b22;
    border-radius: 5px;
}

.navbar__dropdown:before {
    content: '';
    position: absolute;
    top: -10px;
    right: 20px;
    border-bottom: 10px solid #0d1117;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
}

.navbar__dropdown:after {
    content: '';
    position: absolute;
    top: -9px;
    right: 20px;
    border-bottom: 9px solid #0d1117;
    border-left: 9px solid transparent;
    border-right: 9px solid transparent;
}

.navbar__dropdown a:last-child {
    border-bottom: none;
}

.navbar__profile:hover .navbar__dropdown {
    display: block;
    z-index: 1000;
}

.navbar__mobile {
    display: none;
}

.navbar__profile img {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    object-fit: cover;
    cursor: pointer;
}

.navbar__dropdown__title {
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 5px;
}
.notification__component {
    width: 300px;
    height: 400px;
    background-color: var(--color-background-modern-mute);
    position: fixed;
    top: 100%;
    left: 50%;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 10px;
    z-index: 1000;
}

/* Mobile styles */
@media (max-width: 768px) {
    .navbar__center {
        display: none;
    }

    .navbar__mobile {
        display: block;
        cursor: pointer;
        font-size: 21px;
        margin-left: 8px;
    }
}
</style>