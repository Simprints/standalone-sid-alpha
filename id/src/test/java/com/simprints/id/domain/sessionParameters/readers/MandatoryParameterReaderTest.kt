package com.simprints.id.domain.sessionParameters.readers

import com.simprints.id.domain.callout.Callout
import com.simprints.id.domain.callout.CalloutAction
import com.simprints.id.domain.callout.CalloutParameter
import com.simprints.id.domain.callout.CalloutParameters
import com.simprints.id.testUtils.assertThrows
import com.simprints.id.testUtils.mock
import org.junit.Assert.assertEquals
import org.junit.Test


class MandatoryParameterReaderTest {

    private val anyCalloutAction = mock<CalloutAction>()

    private val key = "key"
    private val value = "value"

    private val calloutParameter = CalloutParameter(key, value)

    private val emptyCallout = Callout(anyCalloutAction, CalloutParameters(setOf()))
    private val calloutWithStringParameter = Callout(anyCalloutAction, CalloutParameters(setOf(calloutParameter)))


    private val missingParameterError = Error()
    private val invalidParameterTypeError = Error()

    private val mandatoryStringParameterReader = MandatoryParameterReader(key, String::class,
        missingParameterError, invalidParameterTypeError)

    private val mandatoryIntParameterReader = MandatoryParameterReader(key, Int::class,
        missingParameterError, invalidParameterTypeError)

    @Test
    fun testReadFromSucceedsWhenCalloutContainsParameterWithValidType() {
        assertEquals(value, mandatoryStringParameterReader.readFrom(calloutWithStringParameter))
    }

    @Test
    fun testReadFromThrowsErrorWhenCalloutDoesNotContainParameter() {
        assertThrows(missingParameterError) {
            mandatoryStringParameterReader.readFrom(emptyCallout)
        }
    }

    @Test
    fun testReadFromThrowsErrorWhenCalloutContainsParameterWithInvalidType() {
        assertThrows(invalidParameterTypeError) {
            mandatoryIntParameterReader.readFrom(calloutWithStringParameter)
        }
    }

}