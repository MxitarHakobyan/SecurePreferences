# SecurePreferences

**SecurePreferences** is a simple and secure way to store sensitive data in Android applications. It provides an easy-to-use interface for encrypting and storing preferences, ensuring that your data is safe from unauthorized access.

## Features

- **Encryption at Rest**: Data is encrypted and decrypted automatically when stored and retrieved.
- **Easy to Use**: A simple API for working with shared preferences.
- **AES Encryption**: Uses AES encryption with a key derived from a secure, user-specific key.
- **Seamless Integration**: Integrates easily with existing Android applications using SharedPreferences.
- **Android-Specific**: Tailored for Android with consideration for performance and security.

## Installation

To get started with SecurePreferences, include the following in your app-level `build.gradle`:

```gradle
dependencies {
    implementation("io.github.mxitarhakobyan:securepreferences:1.0.1")
}
```

## Usage

```kotlin
val securePrefs = SecurePreferences.SecurePrefs.create(
    sharedPrefs = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE),
    alias = "Alias for encryption",
    password = "Don't Share It"
)
```