package com.linuxjet.lib.isy.listeners;

import com.linuxjet.lib.isy.entity.ISYEvent;

/**
 * Created by jamespet on 10/18/15.
 */
public interface ISYEventListener {
  void onHeartbeat(ISYEvent event);
  void onTriggerEvent(ISYEvent event);
  void onDriverEvent(ISYEvent event);
  void onNodeUpdate(ISYEvent event);
  void onSystemConfigUpdate(ISYEvent event);
  void onSystemStatusUpdate(ISYEvent event);
  void onInternetStatusUpdate(ISYEvent event);
  void onProgressUpdate(ISYEvent event);
  void onSecurtyEvent(ISYEvent event);
  void onAlertEvent(ISYEvent event);
  void onADRFLEXEvent(ISYEvent event);
  void onClimateEvent(ISYEvent event);
  void onAMISEPEvent(ISYEvent event);
  void onExternalEnergyEvent(ISYEvent event);
  void onUPBLinkerEvent(ISYEvent event);
  void onUPBAdderEvent(ISYEvent event);
  void onUPBStatusEvent(ISYEvent event);
  void onGasMeterEvent(ISYEvent event);
  void onZigbeeEvent(ISYEvent event);
  void onELKEvent(ISYEvent event);
  void onDeviceLinkerEvent(ISYEvent event);
  void onZWaveEvent(ISYEvent event);
  void onBillingEvent(ISYEvent event);
  void onPortalEvent(ISYEvent event);
  void onStatusUpdate(ISYEvent event);
  void onDefault(ISYEvent event);

}
