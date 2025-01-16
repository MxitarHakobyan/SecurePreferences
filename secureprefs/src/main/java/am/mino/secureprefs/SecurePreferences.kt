package am.mino.secureprefs

import android.content.SharedPreferences

// Interface for managing secure preferences
interface SecurePreferences {
    // Save a String value securely
    fun putString(key: String, value: String?)

    // Save an Int value securely
    fun putInt(key: String, value: Int)

    // Save a Long value securely
    fun putLong(key: String, value: Long)

    // Save a Float value securely
    fun putFloat(key: String, value: Float)

    // Save a Boolean value securely
    fun putBoolean(key: String, value: Boolean)

    // Retrieve a securely stored String value, or return the default if not found
    fun getString(key: String, defaultValue: String? = null): String?

    // Retrieve a securely stored Int value, or return the default if not found
    fun getInt(key: String, defaultValue: Int): Int

    // Retrieve a securely stored Long value, or return the default if not found
    fun getLong(key: String, defaultValue: Long): Long

    // Retrieve a securely stored Float value, or return the default if not found
    fun getFloat(key: String, defaultValue: Float): Float

    // Retrieve a securely stored Boolean value, or return the default if not found
    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    // Register a listener for changes in SharedPreferences
    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)

    // Unregister a previously registered listener for SharedPreferences changes
    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)

    // Check if a key exists in the preferences
    fun contains(key: String): Boolean

    // Remove a key-value pair from the preferences
    fun remove(key: String)

    // Clear all stored preferences
    fun clear()

    // Factory object for creating instances of SecurePreferences
    object SecurePrefs {
        /**
         * Factory method to create a SecurePreferences implementation.
         *
         * @param sharedPrefs Shared preferences where should be stored your encrypted data.
         * @param alias Unique alias for the encryption key in the Android Keystore.
         * @param password Password used for key management.
         * @return An instance of SecurePreferencesImpl.
         */
        fun create(
            sharedPrefs: SharedPreferences,
            alias: String,
            password: String,
        ): SecurePreferences {
            // Returns an instance of the SecurePreferencesImpl class
            return SecurePreferencesImpl(
                sharedPrefs = sharedPrefs,
                alias = alias,
                password = password,
            )
        }
    }
}