package com.simprints.id.exceptions.safe.setup

import com.simprints.id.exceptions.safe.SimprintsException

class ScannerLowBatteryException(message: String = "ScannerLowBatteryException") :
    SimprintsException(message)