# Awesome-SID

This repository is a fork of [Simprints-Android-ID](https://github.com/Simprints/Android-Simprints-ID).
The original Simprints-Android-ID project is difficult to use because it relies on closed-source components and connects to a closed-source backend. In this new repository, I've made changes to make it more accessible by using open-source alternatives.
## Changes Made

1. **Disable Login Flow:**
    - Set a default project ID in `AuthStore` to bypass the login flow.

2. **Remove Third-Party Dependencies:**
    - Removed dependencies hosted in private repositories:
        - NEC
        - SimMatcher
        - ROC
        - Secugen SDKs

3. **Add New Face Biometric SDK:**
    - Integrated a new face biometric SDK that uses:
        - Google MLKit for face detection
        - FaceNet for template extraction and matching

4. **New Configuration Files:**
    - Created a new `google-services.json`.
    - Created a new `debug.keystore`.

5. **UI Updates:**
    - Changed the app's colors.
    - Updated the app name.

---

For more details, visit the original [Simprints-Android-ID repository](https://github.com/Simprints/Android-Simprints-ID).

[Full Diff Between Awesome-SID and Simprints-Android-ID repository](https://github.com/Simprints/Android-Simprints-ID/compare/main...meladRaouf:Awesome-SID:main)
