# 📲 BiometricLogin

A simple and lightweight Android library for biometric authentication.

Supports:
- **Basic Biometric Login**: For simple fingerprint authentication with no encryption.
- **Advanced Biometric Login**: For storing encrypted data securely with biometric authentication.

---

## 📦 Installation

### 1. Add JitPack to your `settings.gradle` (Project-level)

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```


### 2. Add the dependency to your build.gradle (App-level)
```gradle
implementation 'com.github.akshayc04:BiometricLogin:1.0.1'
```

## 🛠️ Usage
# 🔐 Basic Biometric Login
Use this method for simple biometric authentication, without using Keystore encryption:
```kotlin
BiometricLogin.basicLogin(
    activity = this, // Must be a FragmentActivity
    title = "Login",
    subtitle = "Use your fingerprint",
    onSuccess = {
        // Handle success
    },
    onError = { error ->
        // Handle error
    }
)
```


# 🔐 Advanced Biometric Login (with Keystore Encryption)
Use this method to encrypt data using the Android Keystore and perform biometric authentication:
```kotlin
BiometricLogin.encryptAndStore(
    activity = this,
    title = "Encrypt Secret",
    subtitle = "Authenticate to encrypt",
    valueToEncrypt = "MySuperSecret",
    onSuccess = { encrypted ->
        // Store encrypted string
    },
    onError = { error ->
        // Handle error
    }
)
```
🔓 Decrypt Stored Secret
To decrypt data:
```kotlin
BiometricLogin.decrypt(
    activity = this,
    title = "Decrypt Secret",
    subtitle = "Authenticate to decrypt",
    onSuccess = { decrypted ->
        // Use decrypted value
    },
    onError = { error ->
        // Handle error
    }
)
```
# Note:  Use EncryptedSharedPreferences to securely store your key 

## ⚙️ Requirements
Minimum SDK: 23+ (for Biometric support)

# 📄 License
This project is licensed under the Apache 2.0 License – see the LICENSE file for details.








