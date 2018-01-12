package com.simprints.id.tools.delegates

import com.simprints.id.data.prefs.improvedSharedPreferences.ImprovedSharedPreferences
import com.simprints.id.exceptions.unsafe.NonPrimitiveTypeError
import isPrimitive
import timber.log.Timber
import kotlin.reflect.KProperty

/**
 * Delegate to read/write values of primitive types ([Byte], [Short], [Int], [Long], [Float], [Double],
 * [String] or [Boolean]) from the Shared Preferences.
 *
 * Thread-safety: a property delegated to PrimitivePreference can be accessed concurrently
 * from different threads.
 *
 * Lazy-initialization: reads to Shared Preferences are performed not on instantiation but on first
 * access.
 *
 * Caching: the value of the property is cached to reduce the number of reads and writes to the
 * Shared Preferences: at most one read on first access, and one write per set.
 *
 * @author etienne@simprints.com
 */
class PrimitivePreference<T: Any>(private val preferences: ImprovedSharedPreferences,
                                  private val key: String,
                                  private val defValue: T) {

    init {
        if (!defValue.isPrimitive()) {
            throw NonPrimitiveTypeError.forTypeOf(defValue)
        }
    }

    private var value: T by lazyVar {
        Timber.d("PrimitivePreference read $key from Shared Preferences")
        preferences.getPrimitive(key, defValue)
    }

    @Synchronized
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        Timber.d("PrimitivePreference.getValue $key")
        return value
    }

    @Synchronized
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Timber.d("PrimitivePreference.setValue $key")
        this.value = value
        Timber.d("PrimitivePreference write $key to Shared Preferences")
        preferences.edit()
                .putPrimitive(key, value)
                .apply()
    }

}