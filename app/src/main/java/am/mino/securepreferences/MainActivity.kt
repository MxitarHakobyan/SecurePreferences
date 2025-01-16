package am.mino.securepreferences

import am.mino.securepreferences.ui.theme.SecurePreferencesTheme
import am.mino.secureprefs.SecurePreferences
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

const val STRING_KEY = "StringKey"
const val STRING_KEY_WHICH_DOES_NOT_EXISTS = "StringKey Which does not exists"
const val INT_KEY = "IntKey"
const val INT_KEY_WHICH_DOES_NOT_EXISTS = "IntKey Which does not exists"
const val LONG_KEY = "LongKey"
const val LONG_KEY_WHICH_DOES_NOT_EXISTS = "LongKey Which does not exists"
const val FLOAT_KEY = "FloatKey"
const val FLOAT_KEY_WHICH_DOES_NOT_EXISTS = "FloatKey Which does not exists"
const val BOOLEAN_KEY = "BooleanKey"
const val BOOLEAN_KEY_WHICH_DOES_NOT_EXISTS = "BooleanKey Which does not exists"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecurePreferencesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Presentation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Presentation(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val securePrefs = SecurePreferences.SecurePrefs.create(
        sharedPrefs = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE),
        alias = "Alias for encryption",
        password = "Don't Share It"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        securePrefs.putString(STRING_KEY, "Some String")
        Text(
            text = "String value: ${securePrefs.getString(STRING_KEY)}",
            modifier = modifier.padding(top = 8.dp)
        )
        Text(
            text = "String value: ${securePrefs.getString(STRING_KEY_WHICH_DOES_NOT_EXISTS)}",
            modifier = modifier.padding(top = 8.dp)
        )
        Text(
            text = "String value: ${
                securePrefs.getString(
                    STRING_KEY_WHICH_DOES_NOT_EXISTS,
                    "Default value"
                )
            }",
            modifier = modifier.padding(top = 8.dp)
        )

        securePrefs.putInt(INT_KEY, 13)
        Text(
            text = "Int value: ${securePrefs.getInt(INT_KEY, 0)}",
            modifier = modifier.padding(top = 8.dp)
        )
        Text(
            text = "Int value: ${securePrefs.getInt(INT_KEY_WHICH_DOES_NOT_EXISTS, 0)}",
            modifier = modifier.padding(top = 8.dp)
        )

        securePrefs.putLong(LONG_KEY, 15L)
        Text(
            text = "Long value: ${securePrefs.getLong(LONG_KEY, 0)}",
            modifier = modifier.padding(top = 8.dp)
        )
        Text(
            text = "Long value: ${securePrefs.getInt(LONG_KEY_WHICH_DOES_NOT_EXISTS, 0)}",
            modifier = modifier.padding(top = 8.dp)
        )

        securePrefs.putFloat(FLOAT_KEY, 15f)
        Text(
            text = "Float value: ${securePrefs.getFloat(FLOAT_KEY, 0f)}",
            modifier = modifier.padding(top = 8.dp)
        )
        Text(
            text = "Float value: ${securePrefs.getFloat(FLOAT_KEY_WHICH_DOES_NOT_EXISTS, 0.0f)}",
            modifier = modifier.padding(top = 8.dp)
        )

        securePrefs.putBoolean(BOOLEAN_KEY, true)
        Text(
            text = "Boolean value: ${securePrefs.getBoolean(BOOLEAN_KEY, false)}",
            modifier = modifier.padding(top = 8.dp)
        )
        Text(
            text = "Boolean value: ${
                securePrefs.getBoolean(
                    BOOLEAN_KEY_WHICH_DOES_NOT_EXISTS,
                    false
                )
            }",
            modifier = modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PresentationPreview() {
    SecurePreferencesTheme {
        Presentation()
    }
}