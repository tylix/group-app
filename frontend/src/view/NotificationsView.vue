<template>
  <div class="notification-header">
      <h1>Notifications</h1>
  </div>
  <div class="notifications">
      <div class="notification" v-for="request in this.groupRequests" :key="request.id">
          <div class="notification-header">
              <h2>{{ request.name }} want's you to join their group.</h2>
              <p>{{ request.description }}</p>
          </div>
          <div class="notification-footer">
              <div class="notification-button" @click="this.acceptInvitation(request)">
                  <i class="bx bx-check"/>
              </div>
              <div class="notification-button">
                  <i class="bx bx-x"/>
              </div>
          </div>
      </div>
  </div>
</template>

<script>
export default {
    name: "NotificationsView",
    data() {
        return {
            groupRequests: []
        }
    },
    mounted() {
        this.loadNotifications()
    },
    methods: {
        loadNotifications() {
            this.loadGroupRequests()
        },
        loadGroupRequests() {
            this.groupRequests = []
            this.$groups.getRequests(JSON.parse(localStorage.getItem('user')).uid).then(requests => {
                this.groupRequests = requests
            })
        },
        acceptInvitation(request) {
            this.$groups.joinGroup(request.gId).then(() => {
                this.loadNotifications()
            })
        }
    }
}
</script>

<style>
.notifications {
    left: 10%;
    top: 10%;
    position: fixed;
}

.notification {
    width: 340px;
    height: 150px;
    background-color: var(--color-background-mute);
    border-radius: 10px;
    margin-bottom: 10px;
    padding: 10px;

}

.notification-header {
    display: flex;
    flex-direction: column;
}

.notification-footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
}

.notification-button {
    cursor: pointer;
    padding: 5px 10px;
    border-radius: 5px;
    margin-left: 10px;
}

.notification-button:hover {
    background-color: var(--color-background-soft);
    transition: 0.9s;
}
</style>