# 🎮 RenameMod — Fabric 1.21.4

Rename any item and auto-load its texture/model from a **Resource Pack**!

---

## 📥 How To Use

1. Install **Fabric Loader** (1.21.4) + **Fabric API**
2. Put `RenameMod.jar` in your `mods/` folder
3. Create or download a **Resource Pack** (see below)
4. Put the Resource Pack in your `resourcepacks/` folder and enable it in-game
5. Press **R** while holding an item → type a name → press Rename!

---

## 📂 Resource Pack Structure

Your resource pack **must** follow this exact structure:

```
my_resource_pack/
├── pack.mcmeta
└── assets/
    └── minecraft/
        ├── models/
        │   └── item/
        │       └── fire_sword.json        ← Model file
        └── textures/
            └── item/
                └── fire_sword.png         ← Texture (16x16 / 32x32 / 64x64)
```

### pack.mcmeta (pack_format = 48 for 1.21.4)
```json
{
  "pack": {
    "pack_format": 48,
    "description": "My Custom Textures"
  }
}
```

### Model File Example (fire_sword.json)
```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "minecraft:item/fire_sword"
  }
}
```

**Parent types:**
- `minecraft:item/handheld` → Sword, axe type items (angled in hand)
- `minecraft:item/generated` → Normal items like potions, books (flat)

---

## 🔧 Build From Source

```bash
git clone https://github.com/YourName/renamemod.git
cd renamemod
./gradlew build
```

Output JAR will be in `build/libs/`

---

## 🤝 How It Works

1. You press **R** → A GUI opens
2. You type a name (e.g. `fire_sword`) → only lowercase + numbers + underscores
3. The name is sent to the **server** as a packet
4. Server stores the name in the item's **NBT data**
5. Every client that has this mod + the matching Resource Pack will **auto-load** the custom texture
6. If a client does **not** have the Resource Pack → original texture shows (fallback)

---

## ⚠️ Requirements
- Minecraft **1.21.4**
- Fabric Loader **0.16.10+**
- Fabric API **0.115.10+**
- Java **21**
