package com.example

import com.example.data.model.Carrier
import com.example.data.model.PackageStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCarrierAutoDetection() {
        assertEquals(Carrier.UPS, Carrier.detectFromTrackingNumber("1Z9999999999999999"))
        assertEquals(Carrier.AMAZON, Carrier.detectFromTrackingNumber("TBA123456789012"))
        assertEquals(Carrier.DHL, Carrier.detectFromTrackingNumber("JJD01827461928"))
        assertEquals(Carrier.CHRONOPOST, Carrier.detectFromTrackingNumber("FW123456789FR"))
        assertEquals(Carrier.COLISSIMO, Carrier.detectFromTrackingNumber("6A12345678901"))
        assertEquals(Carrier.COLISSIMO, Carrier.detectFromTrackingNumber("CC123456789FR"))
        assertEquals(Carrier.MONDIAL_RELAY, Carrier.detectFromTrackingNumber("MR12345678"))
    }

    @Test
    fun testPackageStatusFlags() {
        assertEquals(true, PackageStatus.DELIVERED.isDelivered)
        assertEquals(false, PackageStatus.IN_TRANSIT.isDelivered)
        assertEquals(true, PackageStatus.IN_TRANSIT.isInProgress)
        assertEquals(false, PackageStatus.DELIVERED.isInProgress)
    }
}
