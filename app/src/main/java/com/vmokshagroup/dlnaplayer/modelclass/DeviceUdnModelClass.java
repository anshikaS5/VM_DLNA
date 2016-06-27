package com.vmokshagroup.dlnaplayer.modelclass;

/**
 * Created by anshikas on 15-04-2016.
 */
public class DeviceUdnModelClass {

    public DeviceUdnModelClass(){

    }

    public DeviceUdnModelClass(String uniqueDeviceName) {
        this.uniqueDeviceName = uniqueDeviceName;
    }

    public String uniqueDeviceName;

    public String getUniqueDeviceName() {
        return uniqueDeviceName;
    }

    public void setUniqueDeviceName(String uniqueDeviceName) {
        this.uniqueDeviceName = uniqueDeviceName;
    }
}
