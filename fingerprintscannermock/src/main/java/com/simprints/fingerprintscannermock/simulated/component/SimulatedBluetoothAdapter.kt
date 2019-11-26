package com.simprints.fingerprintscannermock.simulated.component

import com.simprints.fingerprintscanner.component.bluetooth.BluetoothComponentAdapter
import com.simprints.fingerprintscanner.component.bluetooth.BluetoothComponentDevice
import com.simprints.fingerprintscannermock.simulated.SimulatedScannerManager


class SimulatedBluetoothAdapter(private val simulatedScannerManager: SimulatedScannerManager) : BluetoothComponentAdapter {

    override fun isNull(): Boolean = simulatedScannerManager.isAdapterNull

    override fun isEnabled(): Boolean = simulatedScannerManager.isAdapterEnabled

    override fun getRemoteDevice(macAddress: String): BluetoothComponentDevice = simulatedScannerManager.getScannerWithAddress(macAddress)

    override fun cancelDiscovery(): Boolean = true

    override fun getBondedDevices(): Set<BluetoothComponentDevice> = simulatedScannerManager.pairedScanners
}