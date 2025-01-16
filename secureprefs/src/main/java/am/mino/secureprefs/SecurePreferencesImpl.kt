package am.mino.secureprefs

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.OpenForTesting
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Implementation of the SecurePreferences interface for securely storing and retrieving data.
 * Data is encrypted using the Android Keystore system.
 */
internal class SecurePreferencesImpl(
    private val alias: String, // Alias for the encryption key stored in Keystore
    private val password: String, // Password for accessing the key
    private val sharedPrefs: SharedPreferences,
) : SecurePreferences {

    // Keystore instance to manage cryptographic keys
    private val keyStore = KeyStore.getInstance(KEY_STORE_TYPE).apply { load(null) }

    // Methods to securely save data
    override fun putString(key: String, value: String?) {
        saveToStorage(key, value)
    }

    override fun putInt(key: String, value: Int) {
        saveToStorage(key, value.toString())
    }

    override fun putLong(key: String, value: Long) {
        saveToStorage(key, value.toString())
    }

    override fun putFloat(key: String, value: Float) {
        saveToStorage(key, value.toString())
    }

    override fun putBoolean(key: String, value: Boolean) {
        saveToStorage(key, value.toString())
    }

    // Methods to securely retrieve data
    override fun getString(key: String, defaultValue: String?): String? {
        return getFromStorage(key, defaultValue)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return getFromStorage(key, defaultValue) ?: defaultValue
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return getFromStorage(key, defaultValue) ?: defaultValue
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return getFromStorage(key, defaultValue) ?: defaultValue
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return getFromStorage(key, defaultValue) ?: defaultValue
    }

    // Methods to manage SharedPreferences listeners
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    // Check if a key exists in SharedPreferences
    override fun contains(key: String): Boolean = sharedPrefs.contains(key)

    // Remove a specific key-value pair from SharedPreferences
    override fun remove(key: String) {
        sharedPrefs.edit().remove(key).apply()
    }

    // Clear all entries in SharedPreferences
    override fun clear() {
        sharedPrefs.edit().clear().apply()
    }

    // Save data to SharedPreferences after encrypting it
    private fun saveToStorage(key: String, value: String?) {
        val encryptedValue = value?.let { encryptDataWithKeystore(it) }
        sharedPrefs.edit().putString(key, encryptedValue).apply()
    }

    // Retrieve data from SharedPreferences and decrypt it
    @Suppress("UNCHECKED_CAST")
    private fun <T> getFromStorage(key: String, defaultValue: T?): T? {
        val value = when (defaultValue) {
            is String? -> sharedPrefs.getString(key, defaultValue)
                ?.let { if (it == defaultValue) defaultValue else decryptDataWithKeystore(it) }

            is Int -> sharedPrefs.getString(key, null).let {
                if (it == null) defaultValue else decryptDataWithKeystore(it).toInt()
            }

            is Long -> sharedPrefs.getString(key, null).let {
                if (it == null) defaultValue else decryptDataWithKeystore(it).toLong()
            }

            is Float -> sharedPrefs.getString(key, null).let {
                if (it == null) defaultValue else decryptDataWithKeystore(it).toFloat()
            }

            is Boolean -> sharedPrefs.getString(key, null).let {
                if (it == null) defaultValue else decryptDataWithKeystore(it).toBoolean()
            }

            else -> throw IllegalArgumentException("Unsupported type")
        }
        return value as T?
    }

    // Retrieve or generate a cryptographic key from the Keystore
    @OpenForTesting
    fun getKey(): SecretKey {
        val key = keyStore.getKey(alias, password.toCharArray()) as SecretKey?
        if (key != null) {
            return key
        } else {
            // Generate a new key if it doesn't exist
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_TYPE).apply {
                    init(
                        KeyGenParameterSpec.Builder(
                            alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build()
                    )
                }
            return keyGenerator.generateKey()
        }
    }

    // Encrypt data using the Keystore
    @OpenForTesting
    fun encryptDataWithKeystore(data: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION_TYPE)
        val key = getKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv // Initialization vector
        val encryption = cipher.doFinal(data.toByteArray())

        val encryptedData = Base64.encodeToString(encryption, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)

        // Concatenate IV and encrypted data
        return "$ivBase64$DELIMITERS$encryptedData"
    }

    // Decrypt data using the Keystore
    @OpenForTesting
    fun decryptDataWithKeystore(encryptedData: String): String {
        val parts = encryptedData.split(DELIMITERS)
        val iv = Base64.decode(parts[0], Base64.DEFAULT)
        val cipherText = Base64.decode(parts[1], Base64.DEFAULT)

        val cipher = Cipher.getInstance(TRANSFORMATION_TYPE)
        val spec = GCMParameterSpec(128, iv) // GCM parameter spec
        val key = getKey()
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        val decryptedData = cipher.doFinal(cipherText)
        return String(decryptedData)
    }

    companion object {
        private const val DELIMITERS = ":" // Delimiter to separate IV and encrypted data
        private const val KEY_STORE_TYPE = "AndroidKeyStore" // Keystore type
        private const val TRANSFORMATION_TYPE = "AES/GCM/NoPadding" // Encryption algorithm
    }
}