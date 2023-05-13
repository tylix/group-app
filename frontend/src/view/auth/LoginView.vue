<template>
    <div class="login">
        <h3>Login to Chat-App</h3>
        <div class="login-inputs">
            <div class="l-username">
                <label for="login">Username</label>
                <input id="login" type="text" @keyup.enter="this.login()" required v-model="this.username">
            </div>
            <div class="l-password">
                <label for="password">Password</label>
                <input id="password" type="password" @keyup.enter="this.login()" v-model="this.password">

                <p class="forgot-password" @click="this.$router.push('/forgot-password')">Forgot Password?</p>
            </div>
            <button :disabled="this.loading" @click="this.login()">
                <i class="spin bx bx-loader bx-spin" v-if="this.loading" />
                <p v-else>Login</p>
            </button>

            <div class="signin__with">
                <p>Or sign in with</p>
                <div class="signin__with__icons">
                    <a class="bx bxl-apple"></a>
                    <a :href="'https://github.com/login/oauth/authorize?scope=user:email&client_id=' + this.getGitHubClientId()" class="bx bxl-github" />
                    <a class="bx bxl-discord"></a>
                </div>
            </div>
        </div>
        <div class="l-signup">
            <p>Don't have an account?</p>
            <button @click="this.$router.push('/signup')">Sign Up</button>
        </div>
    </div>
</template>

<script>
export default {
    name: "LoginView",
    data() {
        return {
            username: '',
            password: '',
            loading: false
        }
    },
    methods: {
        login() {
            if (this.username === '' || this.password === '') {
                this.$toast.showNotification('Please fill in all fields', 5000, 'error')
                return
            }
            this.message = ''
            this.loading = true
            this.$auth.login(this.username, this.password).then(() => {
                this.loading = false
            }).catch(() => {
                this.loading = false
                this.$toast.showNotification('Invalid username or password', 5000, 'error')
            });
        },
        getGitHubClientId() {
            return import.meta.env.VITE_GITHUB_CLIENT_ID
        }
    },
    mounted() {
        const siw = this.$route.query.siw
        const code = this.$route.query.code
        if (siw && code) {
            this.loading = true
            this.$auth.signInWith(siw, code).then(() => {
                this.$router.push('/')
            }).catch(() => {
                this.$toast.showNotification('Failed to sign in with ' + siw, 5000, 'error')
            })
        }
    }
}
</script>

<style>
.login {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: fixed;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
}

.login-inputs {
    margin-top: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: var(--color-blue-soft);
    width: 320px;
    height: 300px;
    padding-top: 25px;
    border-radius: 5px;
}

.login-inputs input {
    width: 250px;
    height: 30px;
    border-radius: 5px;
    border: none;
    margin-top: 5px;
    background-color: var(--color-background-modern);
    color: var(--color-text);
    font-size: 15px;
    padding-left: 8px;
}

.login-inputs button {
    margin-top: 25px;
    width: 60%;
    height: 30px;
    border: none;
    border-radius: 5px;
    background-color: var(--color-green);
    cursor: pointer;
    opacity: 60%;
}

.login-inputs button:hover {
    opacity: 100%;
    transition: 0.2s ease-in-out;
}

.l-username {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.l-password {
    margin-top: 10px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.l-password .forgot-password {
    margin-top: 2px;
    font-size: 12px;
    color: var(--color-text-muted);
    cursor: pointer;
    position: absolute;
    left: 140px;
    white-space: nowrap;
    opacity: 80%;
}

.l-password .forgot-password:hover {
    opacity: 100%;
}

.l-signup {
    margin-top: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.l-signup p {
    font-size: 12px;
    color: var(--color-text-muted);
}

.l-signup button {
    margin-top: 5px;
    width: 60%;
    height: 30px;
    border: none;
    border-radius: 5px;
    background-color: var(--color-blue);
    cursor: pointer;
    opacity: 40%;
}

.l-signup button:hover {
    opacity: 80%;
    transition: 0.2s ease-in-out;
}

.login-inputs button .spin {
    margin-top: 5px;
    font-size: 20px;
    color: var(--color-text);
}

.signin__with {
    margin-top: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.signin__with p {
    font-size: 12px;
    color: var(--color-text-muted);
}

.signin__with__icons {
    margin-top: 5px;
    display: flex;
    flex-direction: row;
    align-items: center;
}

.signin__with__icons a {
    font-size: 25px;
    color: var(--color-text-muted);
    margin-left: 5px;
    margin-right: 5px;
    cursor: pointer;
}

.signin__with__icons a:hover {
    color: var(--color-text);
    transition: 0.3s ease-in-out;
    background-color: var(--color-blue-soft);
}
</style>