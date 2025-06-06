package com.simprints.infra.enrolment.records.realm.store.models

import androidx.annotation.Keep
import com.simprints.core.ExcludedFromGeneratedTestCoverageReports
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

@Keep
@ExcludedFromGeneratedTestCoverageReports("Data model definition for Realm table")
class DbProject : RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var description: String = ""
    var creator: String = ""
    var imageBucket: String = ""
    var updatedAt: String = ""
}
