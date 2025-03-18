# Sandalone SID Alpha

This repository is a fork of [Simprints-Android-ID](https://github.com/Simprints/Android-Simprints-ID).
The original Simprints-Android-ID project depends on closed-source components and connects to a closed-source backend. In this repository, We've made it more accessible by incorporating open-source alternatives.

## Biometric library access

Add `GITHUB_USERNAME` and `GITHUB_TOKEN` variables with corresponding 
values to either `local.properties` file or the environment variables.

Visit [GitHub Tokens](https://github.com/settings/tokens/new) and create a new "Classic" token if required.

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
    - Integrated SimFace library

4. **New Configuration Files:**
    - Created a new `google-services.json`.
    - Created a new `debug.keystore`.

5. **UI Updates:**
    - Changed the app's colors.
    - Updated the app name.

---

For more details, visit the original [Simprints-Android-ID repository](https://github.com/Simprints/Android-Simprints-ID).
