package com.linuxjet.lib.isy.listeners;

import com.linuxjet.lib.isy.entity.ISYEvent;

/**
 * Created by jamespet on 10/18/15.
 */
public interface ISYEventListener {
  public void onHeartbeat(ISYEvent event);
  public void onTriggerEvent(ISYEvent event);
  public void onDriverEvent(ISYEvent event);
  public void onNodeUpdate(ISYEvent event);
  public void onSystemConfigUpdate(ISYEvent event);
  public void onSystemStatusUpdate(ISYEvent event);
  public void onInternetStatusUpdate(ISYEvent event);
  public void onProgressUpdate(ISYEvent event);
  public void onSecurtyEvent(ISYEvent event);
  public void onAlertEvent(ISYEvent event);
  public void onADRFLEXEvent(ISYEvent event);
  public void onClimateEvent(ISYEvent event);
  public void onAMISEPEvent(ISYEvent event);
  public void onExternalEnergyEvent(ISYEvent event);
  public void onUPBLinkerEvent(ISYEvent event);
  public void onUPBAdderEvent(ISYEvent event);
  public void onUPBStatusEvent(ISYEvent event);
  public void onGasMeterEvent(ISYEvent event);
  public void onZigbeeEvent(ISYEvent event);
  public void onELKEvent(ISYEvent event);
  public void onDeviceLinkerEvent(ISYEvent event);
  public void onZWaveEvent(ISYEvent event);
  public void onBillingEvent(ISYEvent event);
  public void onPortalEvent(ISYEvent event);
  public void onStatusUpdate(ISYEvent event);
  public void onDefault(ISYEvent event);

}
