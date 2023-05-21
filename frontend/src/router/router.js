import {createRouter, createWebHistory} from "vue-router";
import {app} from "@/main.js";

export let loading = false;

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {path: '/', meta: {auth: true}, name: 'Home', component: () => import('@/view/HomeView.vue')},
        {path: '/dashboard', meta: {auth: true}, name: 'Dashboard', component: () => import('@/view/DashboardView.vue')},
        {path: '/login', meta: {auth: false}, name: 'Login', component: () => import('@/view/auth/LoginView.vue')},
        {path: '/signup', meta: {auth: false}, name: 'Signup', component: () => import('@/view/auth/SignupView.vue')},
        {path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/view/404.vue')},
        {path: '/groups', meta: {auth: true}, name: 'Groups', component: () => import('@/view/groups/GroupsView.vue')},
        {path: '/settings', meta: {auth: true}, name: 'Settings', component: () => import('@/view/account/SettingsView.vue')},
        {path: '/otp', meta: {auth: false}, name: 'OTP', component: () => import('@/view/auth/OtpView.vue')},
        {path: '/account/:username', meta: {auth: true}, props: true, name: 'Account', component: () => import('@/view/account/AccountView.vue')},
        /*{path: '/groups/:id', meta: {auth: true}, props: true, name: 'GroupsBy', component: () => import('@/view/groups/GroupsView.vue')},*/
    ]
});

export default router

router.beforeEach((to, from, next) => {
    const response = app.config.globalProperties.$auth.changeRoute(to)
    loading = true
    if (response.path === undefined)
        next()
    else next(response)
})

router.afterEach(() => {
    loading = false
})