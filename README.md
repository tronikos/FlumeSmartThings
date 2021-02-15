# FlumeSmartThings
SmarThings Smart App and Device Handler for [Flume Smart Home Water Monitor](https://flumewater.com/).

## Features
- Leak sensor
  - Changes to wet on low flow leaks
  - Changes to wet on any usage alert (configurable)
  - Changes to dry when notification is deleted in the flume app or when it's read (configurable)
  - Wet/dry status can be permanently or temporarily paused (with configurable time lime)
- Water usage for the last full minute, today, this month, this year is shown in the device page as well as the dashboard
  - water usage stats, connection status, battery level, away mode are exposed as attributes for webCoRE automations
- When SmartThings changes to away, Flume can be put in away mode that notifies emergency contacts
- Supports multiple Flume devices
- Configurable polling frequencies
- To avoid rate limitting there is support for proxy running somewhere in your LAN 

## Installation
If you have [GitHub IDE integration](https://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html) you need to:
1. Go to "My SmartApps"
2. Add GitHub repository with: owner: tronikos, name: FlumeSmartThings, branch: main
3. Update from Repo -> FlumeSmartThings
4. Check tronikos:Flume Smart Home Water Monitor
5. Check Publish
6. Click Execute Update
7. Click Edit Properies of the new installed SmartApp
8. Expand Settings
9. Enter FlumeAPI_Key and FlumeAPI_Secret. You can get these from https://portal.flumewater.com/settings by expanding API Access
10. Go to "My Device Handlers"
11. Update from Repo -> FlumeSmartThings
12. Check tronikos:Flume Smart Home Water Monitor DH
13. Check Publish
14. Click Execute Update

### Proxy
Flume API has a rate limit of 120 requests per hour per IP address. SmartThings SmartApps run on the cloud so this limit is shared among other users of this SmartApp. To avoid rate limiting it's recommended to setup a proxy running in your local network that the SmartThings hub will locally connect to.

The proxy is implemeted in Node.js, see [code](https://github.com/tronikos/FlumeSmartThings/blob/main/flumewater-proxy/index.js), that can run pretty much anywhere, e.g. Windows/Linux/macOS/Android etc. In my case, my hub is a SmartThings Link for NVIDIA SHIELD, so the proxy runs on the NVIDIA SHIELD that is always on. I used Termux. Do a web search how to run Node.js on whatever machine you have available. It needs to be in the same LAN as your SmartThings hub. And then enter lcoal_ip:port in the SmartApp settings.

## Screenshots
![alt text](https://raw.githubusercontent.com/tronikos/FlumeSmartThings/main/screenshots/flume-device.png "Device screenshot")

![alt text](https://raw.githubusercontent.com/tronikos/FlumeSmartThings/main/screenshots/flume-smartapp-settings.png "SmartApp settings screenshot")


Based on https://github.com/getterdone/FlumeWaterMeter
