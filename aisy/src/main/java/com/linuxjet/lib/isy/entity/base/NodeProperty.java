package com.linuxjet.lib.isy.entity.base;

/**
 * Created by jamespet on 10/23/15.
 */
public class NodeProperty {
  private String Id;
  private int value = 0;
  private String formatted;
  private String UOM;

  public String getFormattedValue() {
    return formatted;
  }

  public void setFormattedValue(String formatted) {
    this.formatted = formatted;
  }

  public String getId() {
    return Id;
  }

  public void setId(String id) {
    Id = id;
  }

  public String getUOM() {
    return UOM;
  }

  public void setUOM(String UOM) {
    this.UOM = UOM;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "id: " + getId() + " | value: " + getValue() + " | formatted: " + getFormattedValue() + " | uom: " + getUOM();
  }
}
