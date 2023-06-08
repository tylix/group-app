<template>
    <div class="signup">
        <h3>Signup to Chat-App</h3>
        <div class="signup-inputs">
            <div class="l-username">
                <label for="login">Username</label>
                <input id="login" type="text" @keyup.enter="this.signup()" required v-model="this.username">
            </div>
            <div class="l-email">
                <label for="email">Email</label>
                <input id="email" type="email" @keyup.enter="this.signup()" required v-model="this.email">
            </div>
            <div class="l-password">
                <label for="password">Password</label>
                <input id="password" type="password" @keyup.enter="this.signup()" v-model="this.password">
            </div>
            <div class="l-password">
                <label for="password">Confirm Password</label>
                <input id="password" type="password" @keyup.enter="this.signup()" v-model="this.confirmPassword">
            </div>
            <button @click="this.signup()">
                <i class="spin bx bx-loader bx-spin" v-if="this.loading" />
                <p v-else>Signup</p>
            </button>
        </div>
        <div class="signin">
            <p>Already have an account?</p>
            <button @click="this.$router.push('/signin')">Sign in</button>
        </div>
    </div>
</template>

<script>
export default {
    name: "SignupView",
    data() {
        return {
            username: '',
            password: '',
            confirmPassword: '',
            email: '',
            loading: false
        }
    },
    methods: {
        signup() {
            if (this.username === '' || this.password === '' || this.confirmPassword === '' || this.email === '') {
                this.$toast.showNotification('Please fill in all fields', 5000, 'error')
                return
            }
            if (this.password !== this.confirmPassword) {
                this.$toast.showNotification('Passwords do not match', 5000, 'error')
                return
            }
            this.loading = true
            this.$auth.signup(this.username, this.password, this.email).then(() => {
                this.loading = false
            }).catch((err) => {
                switch (err.response.status) {
                    case 400:
                        this.$toast.showNotification('Username or Email already taken.', 5000, 'error')
                        break
                    case 406:
                        this.$toast.showNotification('The password should meet the following criteria: At least one lowercase letter, one uppercase letter, one digit, one special character (@$!%?&), consist of only allowed characters (A-Z, a-z, 0-9, @$!%?&), and have a minimum length of 8 characters.', 5000, 'error')
                        break
                    case 407:
                        this.$toast.showNotification('This is not a valid email format.', 5000, 'error')
                        break
                }
                this.loading = false
            });
        }
    }
}
</script>

<style>
.signup {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: fixed;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    -webkit-user-select: none;
}

.signup-inputs {
    margin-top: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: var(--color-blue-soft);
    width: 320px;
    height: 390px;
    padding-top: 25px;
    border-radius: 5px;
}

.signup-inputs input {
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

.signup-inputs button {
    margin-top: 25px;
    width: 60%;
    height: 30px;
    border: none;
    border-radius: 5px;
    background-color: var(--color-green);
    cursor: pointer;
    opacity: 60%;
}

.signup-inputs button:hover {
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

.l-email {
    margin-top: 10px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.signin {
    margin-top: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.signin p {
    font-size: 12px;
    color: var(--color-text-muted);
}

.signin button {
    margin-top: 5px;
    width: 60%;
    height: 30px;
    border: none;
    border-radius: 5px;
    background-color: var(--color-blue);
    cursor: pointer;
    opacity: 40%;
}

.signin button:hover {
    opacity: 80%;
    transition: 0.2s ease-in-out;
}

.signup-inputs button .spin {
    margin-top: 5px;
    font-size: 20px;
    color: var(--color-text);
}
</style>