/**
 *  Flume Smart Home Water Monitor DH
 *
 *  Copyright 2021 tronikos
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
metadata {
	definition (
        name: "Flume Smart Home Water Monitor DH",
        namespace: "tronikos",
        author: "tronikos",
        mnmn: "SmartThingsCommunity",
        vid: "d665a967-eccd-3ac7-bb72-b3d4c626f4dd",
    ) {
        capability "Actuator"
        capability "Sensor"
        // attributes: water: dry|wet
        capability "Water Sensor"
        // commands: refresh
        capability "Refresh"
        // attributes: listElement
        // commands: setListElement(thisCurrentMinFlow|todayFlow|monthFlow|yearFlow)
        capability "mediapepper07880.watermeterflow"
        // attributes: waterMeterMonitorState: paused|monitoring
        // commands: setWaterMeterMonitorState(paused|monitoring)
        capability "mediapepper07880.watermetermonitorstatus"
        // Following attributes aren't exposed in the UI. They are only for automations such as webCoRE.
        attribute "batteryLevel", "string"
        attribute "awayMode", "boolean"
        attribute "connected", "boolean"
        attribute "thisCurrentMinFlow", "number"
        attribute "todayFlow", "number"
        attribute "monthFlow", "number"
        attribute "yearFlow", "number"
    }

    simulator {
    }

    preferences {
        input (
            name: "pauseTimeLimitMinutes",
            type: "number",
            title: "Time limit for pause in minutes",
            description: "After this number of minutes have passed, monitoring will resume",
            required: false,
        )
        input (
            name: "configLoggingLevelIDE",
            title: "IDE Live Logging Level",
            type: "enum",
            options: [
                "0" : "None",
                "1" : "Error",
                "2" : "Warn or above",
                "3" : "Info or above",
                "4" : "Debug or above",
                "5" : "Trace or above",
            ],
            defaultValue: "5",
            required: false,
        )
    }
}

void installed() {
    logger("installed() called", "trace")
    state.wetDry = "dry" // Real status even if paused
    sendEvent(name: "water", value: "dry")
    sendEvent(name: "waterMeterMonitorState", value: "monitoring")
    runIn(3, refresh)
}

void updated() {
    logger("updated() called", "trace")
}

void parse(String description) {
    logger("parse called with $description", "trace")
}

void setWaterMeterMonitorState(String argument) {
    logger("setWaterMeterMonitorState($argument) called", "trace")
    sendEvent(name: "waterMeterMonitorState", value: argument)
    switch (argument) {
        case "paused":
            // Remove wet while paused
            sendEvent(name: "water", value: "dry")
            if (settings.pauseTimeLimitMinutes > 0) {
                logger("will run setWaterMeterMonitorState(monitoring) " +
                       "in ${settings.pauseTimeLimitMinutes} minute(s)", "info")
                runIn(settings.pauseTimeLimitMinutes * 60, setWaterMeterMonitorStateMonitoring)
            }
            break
        case "monitoring":
            // Update real status in case it had been paused
            sendEvent(name: "water", value: state.wetDry)
            break
        default:
            logger("Unhandled $argument in setWaterMeterMonitorState", "error")
            break
    }
}

void setWaterMeterMonitorStateMonitoring() {
    logger("${settings.pauseTimeLimitMinutes} minute(s) passed. Calling setWaterMeterMonitorState(monitoring)", "info")
    setWaterMeterMonitorState("monitoring")
}

void setWater(String wetDry) {
    logger("setWater($wetDry) called", "info")
    state.wetDry = wetDry
    if (wetDry == "wet" && device.currentValue("waterMeterMonitorState") == "paused") {
        logger("waterMeterMonitorState is currently paused. " +
               "Will change water to wet when it switches to monitoring", "info")
        return
    }
    sendEvent(name: "water", value: wetDry)
}

void setListElementWithLastArgument() {
    logger("setListElementWithLastArgument() called", "trace")
    String setListElementArgument = (state.setListElementLastArgument != null) ?
                                    state.setListElementLastArgument : "todayFlow"
    setListElement(setListElementArgument)
}

void setListElement(String argument) {
    logger("setListElement($argument) called", "trace")
    state.setListElementLastArgument = argument
    String flow = String.format("%,.2f", device.currentValue(argument).toDouble())
    String duration
    switch (argument) {
        case "thisCurrentMinFlow":
            duration = "Minute"
            break
        case "todayFlow":
            duration = "Today"
            break
        case "monthFlow":
            duration = "This month"
            break
        case "yearFlow":
            duration = "This year"
            break
        default:
            logger("Unhandled $argument in setListElement", "error")
            break
    }
    sendEvent(name: "listElement", value: "$duration: $flow gallons")
}

void refresh() {
    logger("refresh() called", "trace")
    parent.pollAlerts()
    parent.pollWaterUsage(device)
    // commented out since such changes are infrequent and aren't UI visible anyway
    // parent.pollDevices()
}

void logger(String msg, String level = "debug") {
    int loggingLevelIDE = (settings.configLoggingLevelIDE != null) ? settings.configLoggingLevelIDE.toInteger() : 5
    switch (level) {
        case "error":
            if (loggingLevelIDE >= 1) { log.error msg }
            break
        case "warn":
            if (loggingLevelIDE >= 2) { log.warn msg }
            break
        case "info":
            if (loggingLevelIDE >= 3) { log.info msg }
            break
        case "debug":
            if (loggingLevelIDE >= 4) { log.debug msg }
            break
        case "trace":
            if (loggingLevelIDE >= 5) { log.trace msg }
            break
        default:
            log.debug msg
            break
    }
}
