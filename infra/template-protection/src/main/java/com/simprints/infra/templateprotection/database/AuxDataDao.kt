package com.simprints.infra.templateprotection.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
internal interface AuxDataDao {
    @Query("select * from DbAuxData where subjectId=:subjectId limit 1")
    suspend fun getAuxData(subjectId: String): DbAuxData?

    @Insert
    suspend fun saveAuxData(auxData: DbAuxData)

    @Query("delete from DbAuxData")
    suspend fun deleteAll()
}
