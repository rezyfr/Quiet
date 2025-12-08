# Quiet!
A clone of **BuzzKill** — currently in development.

Quiet! helps you take control of your notifications. Create rules to automatically mute, dismiss, batch, or delay notifications based on app, text, or time of day. Built with Jetpack Compose.

---

## 🚧 Project Status
**Work in progress.**  
Many features are incomplete and APIs may change

---

## ✨ Features (WIP)

- **Notification Filtering**  
  - Match by package name  
  - Phrase/text matching  
  - Filter by time, bluetooth, and many to come  

- **Actions**  
  - Dismiss/Mute  
  - Batch and delivery later

- **Notification History**  
  - View past notifications stored in Room DB  
  - See which rule affected each notification

---

## 🧱 Architecture

- **Kotlin + Jetpack Compose**
- **Room Database**
- **Koin** for dependency injection  
- **Coroutines / Flows** for async pipelines  

---

### Build & Run
1. Clone the repository  
2. Open in Android Studio  
3. Build and run on a device  
4. Enable **Notification Access** for Quiet!  

---

## 📌 Roadmap

- [x] Basic Rule Engine  
- [x] Phrase matching  
- [x] Package filtering  
- [x] Time window rules  
- [ ] Digest batching engine  
- [ ] Rule editor UI  
- [ ] Import/Export rules  


