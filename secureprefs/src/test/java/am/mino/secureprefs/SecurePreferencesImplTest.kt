package am.mino.secureprefs

import android.content.SharedPreferences
import android.util.Base64
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class SecurePreferencesImplTest {

    @RelaxedMockK
    lateinit var sharedPreferences: SharedPreferences

    @RelaxedMockK
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var securePreferences: SecurePreferencesImpl

    private val alias = "test_alias"
    private val password = "test_password"

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.putString(any(), any()) } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.remove(any()) } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.clear() } returns sharedPreferencesEditor

        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), any()) } answers {
            java.util.Base64.getEncoder().encodeToString(firstArg())
        }

        every { Base64.decode(any<String>(), any()) } answers {
            java.util.Base64.getDecoder().decode(firstArg<String>())
        }

        FakeAndroidKeyStore.setup

        // Spy on SecurePreferencesImpl
        securePreferences = spyk(
            SecurePreferencesImpl(
                sharedPrefs = sharedPreferences,
                alias = alias,
                password = password,
            )
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test putString encrypts and saves data`() {
        val key = "test_key"
        val value = "test_value"
        val encryptedValue = "encrypted_value"

        every { securePreferences.encryptDataWithKeystore(value) } returns encryptedValue

        securePreferences.putString(key, value)

        verify(exactly = 1) { sharedPreferencesEditor.putString(key, encryptedValue) }
        verify(exactly = 1) { sharedPreferencesEditor.apply() }
    }

    @Test
    fun `test putInt encrypts and saves data`() {
        val key = "test_key"
        val value = 1
        val encryptedValue = "encrypted_value"

        every { securePreferences.encryptDataWithKeystore(value.toString()) } returns encryptedValue

        securePreferences.putInt(key, value)

        verify(exactly = 1) { sharedPreferencesEditor.putString(key, encryptedValue) }
        verify(exactly = 1) { sharedPreferencesEditor.apply() }
    }

    @Test
    fun `test getString decrypts and retrieves data`() {
        val key = "test_key"
        val defaultValue = "default_value"
        val encryptedValue = "encrypted_value"
        val decryptedValue = "decrypted_value"

        every { sharedPreferences.getString(key, defaultValue) } returns encryptedValue
        every { securePreferences.decryptDataWithKeystore(encryptedValue) } returns decryptedValue

        val result = securePreferences.getString(key, defaultValue)

        assertEquals(decryptedValue, result)
    }

    @Test
    fun `test remove removes key`() {
        val key = "test_key"

        securePreferences.remove(key)

        verify(exactly = 1) { sharedPreferencesEditor.remove(key) }
        verify(exactly = 1) { sharedPreferencesEditor.apply() }
    }

    @Test
    fun `test clear clears all data`() {
        securePreferences.clear()

        verify(exactly = 1) { sharedPreferencesEditor.clear() }
        verify(exactly = 1) { sharedPreferencesEditor.apply() }
    }
}