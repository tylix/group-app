<template>
    <div class="cgroup">
        <div class="header">
            <h2>Create new group</h2>
        </div>
        <div class="cr-general">
            <div class="name">
                <h4 class="gname">Name</h4>
                <input type="text" v-model="this.data.name" :placeholder="this.names[Math.floor(Math.random() * this.names.length)]">
            </div>
            <div class="description">
                <h4>Description</h4>
                <input type="text" v-model="this.data.description" :placeholder="'Description'">
            </div>
        </div>
        <div class="cr-member">
            <h4>Invite Member</h4>
        </div>
        <div class="actions">
            <div @click="this.createGroup" class="bcgroup">
                <button>Create</button>
            </div>
            <div class="draft">
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
.cgroup {
    position: fixed;
    left: 35%;
    top: 4%;
    width: 50%;
    background-color: var(--color-background-mute);
    border-radius: 10px;
}

.header {
    padding: 20px 20px;
}

.cr-general {
    padding: 10px 20px;
    font-size: 18px;
    display: flex;
    align-content: center;
}

.cr-general input {
    width: 90%;
    padding: 5px 10px;
    border: none;
    background-color: var(--color-background-soft);
    border-radius: 5px;
    outline: none;
    font-size: 14px;
    color: var(--color-text);
}

.cr-general input::placeholder {
    opacity: 20%;
}

.actions {
    display: flex;
    padding: 10px 20px;
}

.bcgroup {
    margin-left: auto;
}

.bcgroup button {
    padding: 5px 10px;
    border: none;
    background-color: var(--color-green);
    border-radius: 5px;
    outline: none;
    font-size: 14px;
    color: var(--color-text);
    cursor: pointer;
}

.draft {
    margin-left: 10px;
}

.draft button {
    padding: 5px 10px;
    border: none;
    background-color: var(--color-background-soft);
    border-radius: 5px;
    outline: none;
    font-size: 14px;
    color: var(--color-text);
    cursor: pointer;
}

.gname:after {
    content: '*';
    color: red;
    font-size: 15px;
    opacity: 65%;
    position: fixed;
}

.cr-member {
    padding: 10px 20px;
    font-size: 18px;
    display: flex;
    align-content: center;
}


</style>