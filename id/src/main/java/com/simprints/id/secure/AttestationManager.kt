package com.simprints.id.secure

import com.google.android.gms.safetynet.SafetyNetClient
import com.google.common.io.BaseEncoding
import com.simprints.id.secure.models.AttestToken
import com.simprints.id.secure.models.Nonce
import io.reactivex.Single

class AttestationManager {

   fun requestAttestation(safetyNetClient: SafetyNetClient, nonce: Nonce): Single<AttestToken> {

       return Single.create<AttestToken> { emitter ->

           safetyNetClient.attest(BaseEncoding.base64().decode(nonce.value), "AIzaSyAGYfgKYVGHsRJwrPnbNEwLrFfbbNdlAyE")
               .addOnSuccessListener { attestationResponse ->
                   val result = attestationResponse.jwsResult
                   val attestToken = AttestToken(result)
                   emitter.onSuccess(attestToken)
               }
               .addOnFailureListener { e ->
                   print(e)
                   emitter.onError(e)
               }
       }
   }
}