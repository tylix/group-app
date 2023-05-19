<template>
    <div class="tfa-body">
        <div class="s-buttons">
            <button v-if="!this.hasOtp" @click="this.toggleSetup()">Setup</button>
            <button v-else>Unlink</button>
        </div>
        <div class="setup" v-if="this.setup">
            <div class="close-sup" @click="this.toggleSetup()">
                <i class="close-i bx bx-x" />
            </div>
            <i class="spin bx bx-loader bx-spin" v-if="this.loading" />
            <div v-if="this.qr" class="qr">
                <img :src="'data:image/jpeg;base64, ' + this.qr.qr" alt="QR Code" width="220" height="220" />
                <p>Scan this QR code with your authenticator app</p>
                <p>Or enter this code manually:</p>
                <p class="secret">{{ this.qr.secret }}</p>
                <div class="input">
                    <label for="verify">Verify Code</label>
                    <input required :style="{ backgroundColor: 'var(--color-background-modern)' }"
                        pattern="^[0-9]{3} [0-9]{3}$" @input="this.formatOtp()" v-model="this.code" id="verify" type="text">
                    <button @click="this.verifyCode()">Submit</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "TwoFactorSettingComponent",
    data() {
        return {
            hasOtp: undefined,
            setup: false,
            loading: false,
            qr: undefined,
            code: undefined
        }
    },
    methods: {
        toggleSetup() {
            if (this.loading) return
            if (this.setup) {
                this.setup = false
                this.qr = undefined
                this.code = undefined
                return
            }
            this.setup = true
            this.loading = true
            this.$auth.generateOTPQR().then(res => {
                this.qr = res
                this.loading = false
            })
        },
        verifyCode() {
            if (!this.code) {
                this.$toast.showNotification('Please enter a code', 5000, 'error')
                return
            }
            if(this.code.length < 7) {
                this.$toast.showNotification('Please enter a valid code', 5000, 'error')
                return
            }
            const otp = this.code.replace(/\s/g, '')
            this.$auth.verifyCode(otp).then(() => {
                this.$auth.logout()
                this.$toast.showNotification('Two Factor Authentication has been setup', 5000)
            }).catch(er => {
                this.$toast.showNotification('Verification failed.', 5000, 'error')
            })
        },
        formatOtp() {
            this.code = this.code.replace(/\D/g, '');

            if (this.code.length > 3) {
                this.code = this.code.slice(0, 3) + ' ' + this.code.slice(3, 6);
            }
        }
    },
    mounted() {
        this.hasOtp = this.$auth.isOtpEnabled()
    }
}
</script>

<style>
.setup .input {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-top: 20px;
}

.setup .input button {
    margin-top: 20px;
}

.setup .secret {
    -webkit-user-select: text;
    font-size: 17px;
}

.setup {
    height: 60%;
    width: 42%;
    background-color: var(--color-background-modern-mute);
    border-radius: 10px;
    position: fixed;
    left: 30%;
    top: 10%;
    padding: 20px;
    z-index: 100;
}

.close-sup {
    position: absolute;
    right: 10px;
    top: 10px;
    cursor: pointer;
    height: 20px;
    width: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--color-background-mute);
    border-radius: 50%;
}

.close-i {
    font-size: 30px;
    cursor: pointer;
}

.qr {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
}

.spin {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    font-size: 50px;
}

.input input {
    border-radius: 10px;
    text-align: center;
}

.input button {
    background-color: var(--color-blue-mute);
    font-size: 16px;
}
</style>