# PiShield App

The Android companion app for [PiShield](https://github.com/seikouv/pishield.git), a portable network security gateway built on a Raspberry Pi. This app connects to a PiShield Pi over its local "PiShield" WiFi network and talks to its FastAPI backend to give you real-time visibility and control over your device's firewall, DNS filtering, and intrusion detection.

**Main repo (backend + hardware setup):** [github.com/seikouv/pishield](https://github.com/seikouv/pishield.git)

## Screens

- **Dashboard** — live-polling system stats: memory usage, uptime, active connections, threat counts
- **Blocklists** — live multi-series chart of blocked DNS queries by category (ads/tracking/AI), plus whitelist and custom blacklist management
- **DNS Logs** — real-time scrolling log of DNS queries with filter chips (all/blocked/allowed/ads/tracking/ai)
- **Threats** — view, filter, and clear detected intrusion attempts (port scans, SYN/ICMP floods, new device alerts)
- **Devices** — connected devices with hostname lookup and per-device block/unblock, enforced via nftables on the Pi
- **Settings** — Pi connection info, GitHub link, on-demand sync

## Tech Stack

- Kotlin, Jetpack Compose
- Retrofit for networking
- MVVM + StateFlow
- Vico charting library for live graphs
- Theme auto-follows system dark/light mode

## Setup

```bash
git clone https://github.com/seikouv/pishield-app.git
```

1. Open the project in Android Studio
2. Connect your phone to the "PiShield" WiFi network broadcast by your Pi
3. Build and run the app
4. On first launch, enter the API token shown during the Pi's setup process

## About

Companion app for the PiShield capstone project. See the [main repo](https://github.com/seikouv/pishield.git) for backend, hardware, and setup details.
