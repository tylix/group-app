<template>
    <div class="otp signup" v-if="this.signup">
        <h3>Configure your OTP</h3>

        <div class="otp-input otp-setup">
            <div class="otp-qr" v-if="this.qr">
                <img :src="'data:image/jpeg;base64, ' + this.qr.qr" alt="QR Code" width="220" height="220"/>
                Or enter secret manually: <p class="secret">{{ this.qr.secret }}</p>
            </div>
            <div class="otp-input-">
                <label for="otp">Authentication app code</label>
                <input id="otp" type="text" pattern="^[0-9]{3} [0-9]{3}$" @input="this.formatOtp()" v-model="this.otp">
            </div>
            <button @click="this.sendOtp()">
                <i class="spin bx bx-loader-alt bx-spin" v-if="this.loading"/>
                <p v-else>Confirm</p>
            </button>
        </div>
        <button class="otp-skip" @click="this.$router.push(localStorage.getItem('redirect') ? localStorage.getItem('redirect') : '/dashboard')">Skip</button>
    </div>
    <div class="otp" v-else>
        <h3>Enter your OTP</h3>

        <div class="otp-input">
            <div class="otp-input-">
                <label for="otp">6 digit code</label>
                <input id="otp" type="text" pattern="^[0-9]{3} [0-9]{3}$" @input="this.formatOtp()" v-model="this.otp">
            </div>
            <button @click="this.sendOtp()">
                <i class="spin bx bx-loader-alt bx-spin" v-if="this.loading"/>
                <p v-else>Confirm</p>
            </button>
        </div>
    </div>
</template>

<script>
export default {
    name: "OtpView",
    data() {
        return {
            otp: '',
            token: '',
            signup: false,
            qr: undefined,
            loading: false
        }
    },
    methods: {
        sendOtp() {
            this.loading = true
            const otp = this.otp.replace(/\s/g, '')
            this.$auth.sendOtp(otp, this.token).then(() => {
                this.checkStatus()
            }).catch(() => {
                this.$toast.showNotification('Invalid OTP', 5000, 'error')
                this.loading = false
            });
        },
        checkStatus() {
            this.loading = true
            const status = this.$auth.getStatus(this.token)
            status.then(res => {
                this.$auth.setToken({data: {token: res.token}})
                this.loading = false
            }).catch(() => {
                this.loading = false
                this.$toast.showNotification('An error occurred while checking your authentication status.', 5000, 'error')
            })
        },
        verifyCode() {
            this.loading = true
            const otp = this.otp.replace(/\s/g, '')
            this.$auth.verifyCode(otp).then(() => {
                this.$auth.logout()
                this.$toast.showNotification('Two Factor Authentication has been setup', 5000)
            }).catch(er => {
                this.loading = false
                console.error(er)
            })
        },
        configure() {
            this.signup = true
            this.$auth.generateOTPQR().then(res => {
                this.qr = res
            })
        },
        formatOtp() {
            this.otp = this.otp.replace(/\D/g, '');

            if (this.otp.length > 3) {
                this.otp = this.otp.slice(0, 3) + ' ' + this.otp.slice(3, 6);
            }
        }
    },
    created() {
        this.token = this.$route.query.token;
        const parsedToken = this.$auth.parseJwt(this.token)
        if (!this.token || parsedToken.exp < Date.now() / 1000) {
            this.$router.push('/login')
            return
        }
        if (parsedToken.signup)
            this.configure()
    },
    watch: {
        otp() {
            if (this.otp.length === 7) {
                if(this.signup) {
                    this.verifyCode()
                    return
                }
                this.sendOtp()
            }
        }
    }
}
</script>

<style>
.otp {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: fixed;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    -webkit-user-select: none;
}

.otp-input {
    margin-top: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: var(--color-blue-soft);
    width: 320px;
    height: 190px;
    padding-top: 25px;
    border-radius: 5px;
}

.otp-setup {
    height: 440px;
    width: 500px;
}

.otp-input input {
    width: 250px;
    height: 30px;
    border-radius: 5px;
    border: none;
    margin-top: 5px;
    background-color: var(--color-background-modern);
    color: var(--color-text);
    font-size: 15px;
    padding-left: 8px;
    text-align: center;
}

.otp-input button {
    margin-top: 25px;
    width: 60%;
    height: 30px;
    border: none;
    border-radius: 5px;
    background-color: var(--color-blue);
    cursor: pointer;
    opacity: 60%;
}

.otp-input button:hover {
    opacity: 100%;
    transition: 0.2s ease-in-out;
}

.otp-input- {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.otp-skip {
    position: absolute;
    font-size: 13px;
    right: 5px;
    bottom: 5px;
    border: none;
    background-color: transparent;
    cursor: pointer;
    color: var(--color-text-muted);
}

.otp-skip:hover {
    color: var(--color-text);
    transition: 0.2s ease-in-out;
}

.otp-input button .spin {
    margin-top: 5px;
    font-size: 20px;
    color: var(--color-text);
}

.otp-qr {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-bottom: 15px;
}

.secret {
    -webkit-user-select: all;
}
</style>