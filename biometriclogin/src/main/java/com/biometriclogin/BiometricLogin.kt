package com.biometriclogin
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object BiometricLogin {


    private const val KEY_ALIAS = "biometric_login_key"

    fun basicLogin(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errString.toString())
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }





    fun encryptAndStore(
        activity: FragmentActivity,
        secret: String,
        onSuccess: (ByteArray, ByteArray) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val cipher = getCipher()
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

            val biometricPrompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        result.cryptoObject?.cipher?.let {
                            val encrypted = it.doFinal(secret.toByteArray())
                            onSuccess(encrypted, it.iv)
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        onError(errString.toString())
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Encrypt and Store")
                .setSubtitle("Authenticate to store securely")
                .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()

            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))

        } catch (e: Exception) {
            onError(e.message ?: "Encryption failed")
        }
    }





    fun decrypt(
        activity: FragmentActivity,
        encryptedData: ByteArray,
        iv: ByteArray,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val cipher = getCipher()
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv))

            val biometricPrompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        result.cryptoObject?.cipher?.let {
                            val decrypted = it.doFinal(encryptedData)
                            onSuccess(String(decrypted))
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        onError(errString.toString())
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Decrypt")
                .setSubtitle("Authenticate to decrypt")
                .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()

            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))

        } catch (e: Exception) {
            onError(e.message ?: "Decryption failed")
        }
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance("AES/CBC/PKCS7Padding")
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        return if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.getKey(KEY_ALIAS, null) as SecretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keySpecBuilder = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                setUserAuthenticationRequired(true)
                setInvalidatedByBiometricEnrollment(true)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    setUserAuthenticationParameters(
                        0,
                        KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
                    )
                }
            }
            keyGenerator.init(keySpecBuilder.build())
            keyGenerator.generateKey()
        }
    }

}