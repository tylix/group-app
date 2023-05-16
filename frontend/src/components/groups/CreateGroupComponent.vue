<template>
    <div class="create__group">
        <div class="create__group__header">
            <h2>Create new group</h2>
        </div>
        <div class="create__group__general">
            <div class="create__group__name">
                <h4 class="create__group__name__title">Name</h4>
                <input type="text" v-model="this.data.name" :placeholder="this.names[Math.floor(Math.random() * this.names.length)]">
            </div>
            <div class="create__group__description">
                <h4>Description</h4>
                <input type="text" v-model="this.data.description" :placeholder="'Description'">
            </div>
        </div>
        <div class="create__group__member">
            <h4>Invite Member</h4>
        </div>
        <div class="create__group__actions">
            <div @click="this.createGroup" class="create__group__actions__create">
                <button>Create</button>
            </div>
            <div @click="this.$emit('cancel')" class="create__group__actions__draft">
                <button>Cancel</button>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "CreateGroupComponent",
    data() {
        return {
            names: [
                "Fantastic Falcons",
                "Sapphire Squad",
                "Dynamic Dolphins",
                "Midnight Mavericks",
                "Electric Eagles",
                "Radiant Raptors",
                "Phoenix Phantoms",
                "Marvelous Mustangs",
                "Super Sharks",
                "Crimson Crusaders",
                "Majestic Mermaids",
                "Golden Griffins",
                "Sizzling Serpents",
                "Thunder Tigers",
                "Cherry Chefs",
                "Diamond Dragons",
                "Emerald Enchanters",
                "Glorious Gladiators",
                "Ocean Otters",
                "Enchanted Elves",
                "Vibrant Violets",
                "Lively Lions",
                "Polar Penguins",
                "Moonlit Mages",
                "Neon Ninjas",
                "Spicy Spartans",
                "Tropical Toucans",
                "Whimsical Wizards",
                "Wise Wolves",
                "Clever Coyotes",
                "Fire Flames",
                "Funky Ferrets",
                "Gleaming Goblins",
                "Gusty Gusts",
                "Happy Hippos",
                "Hilarious Horses",
                "Icy Igloos",
                "Incredible Insects",
                "Jazzy Jaguars",
                "Juicy Jesters",
                "Kooky Kangaroos",
                "Legendary Llamas",
                "Magical Monkeys",
                "Mythical Minotaurs",
                "Noble Noodles",
                "Outrageous Ostriches",
                "Perky Penguins",
                "Raging Rhinos",
                "Roaring Rockets",
                "Sassy Seagulls"
            ],
            data: {
                name: '',
                description: '',
                created: 0,
                member: [],
                icon: '',
                invited: [],
                requests: [],
                messages: []
            }
        }
    },
    mounted() {
        this.loadMember()
    },
    methods: {
        loadMember() {
        },
        createGroup() {
            let data = this.data;
            data.created = Date.now();
            data.member.push({
                id: JSON.parse(localStorage.getItem('user')).uid,
                timestamp: Date.now(),
                role: 0
            })
            this.$groups.createGroup(data).then(() => {
                this.$emit('close')
            })
        }
    }
}
</script>

<style>
.create__group {
    position: relative;
    margin-top: 20px;
    margin-left: 20px;
    height: 94%;
    width: 340px;
    border: 1px solid var(--color-background-mute);
    border-radius: 10px;
}

.create__group__header {
    padding: 20px 20px;
    color: var(--color-text-muted);
}

.create__group__general {
    padding: 10px 20px;
    font-size: 18px;
    display: flex;
    align-content: center;
}

.create__group__general input {
    width: 90%;
    padding: 5px 10px;
    border: none;
    background-color: var(--color-background-modern-mute);
    border-radius: 5px;
    outline: none;
    font-size: 14px;
    color: var(--color-text);
}

.create__group__general input::placeholder {
    opacity: 20%;
}

.create__group__member {
    padding: 10px 20px;
    font-size: 18px;
    display: flex;
    align-content: center;
}

.create__group__member input {
    width: 90%;
    padding: 5px 10px;
    border: none;
    background-color: var(--color-background-modern-mute);
    border-radius: 5px;
    outline: none;
    font-size: 14px;
    color: var(--color-text);
}

.create__group__member input::placeholder {
    opacity: 20%;
}

.create__group__actions {
    top: 10%;
    display: flex;
    justify-content: center;
    gap: 20px;
    width: 100%;
}

.create__group__actions__create button {
    background-color: var(--color-green-soft);
}

.create__group__actions__draft button {
    background-color: var(--color-text);
}


</style>