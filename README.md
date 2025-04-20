# 🧭 Konum Plugin

A simple Minecraft plugin that allows players to **save custom locations** with a name and **teleport to them via an interactive GUI**.

---

## 📦 Features

- `/konum <name>` — Save your current location with a custom name.
- `/konum` — Open a GUI to see your saved locations.
- Click on a location in the GUI to teleport instantly.
- All locations are saved in the plugin's `config.yml`.
- **Limit of 10 saved locations per player.**
- No economy, no teleport costs — just simple convenience.

---

## 📋 Commands

| Command         | Description                               |
|-----------------|-------------------------------------------|
| `/konum <name>` | Saves your current location with `<name>` |
| `/konum`        | Opens the GUI with your saved locations    |

---

## 🔢 Location Limit

Each player can save up to **10 locations**.  
Trying to save more than that will show an error message and block the save.

---

## 🗂 Config Structure

Saved locations are stored per player under their UUID:

```yaml
locations:
  <player-uuid>:
    home:
      world: world
      x: 100.0
      y: 64.0
      z: 100.0
      yaw: 0.0
      pitch: 0.0
```

No configuration needed — the plugin handles it automatically.

---

## 🔧 Installation

1. Drop the JAR into your server’s `/plugins/` folder.
2. Restart or reload the server.
3. Done! Use `/konum` to begin.

---

## 🛠 Development Info

- Built with **Java 17+**
- Tested on **PaperMC 1.19–1.21**

---

## ✅ To-Do Ideas

- GUI-based deletion of saved locations
- Customizable messages
- Teleport cooldowns or delays
  
---

## 🧑‍💻 Contributing

Got ideas? Found a bug? Want to sprinkle in some code?  
Contributions, suggestions, and feedback are **super welcome**. Let’s grow this plugin together! 💚

---

> Made with ❤️ by Can  
> Keep an eye out for updates – this is just the beginning! 🌟
