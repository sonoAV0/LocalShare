package avogadri.marco.localshare.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import avogadri.marco.localshare.R

// Variable fonts: a single ttf covers every weight, the system interpolates
// the 'wght' axis for each FontWeight entry registered below.
val InterFontFamily = FontFamily(
    Font(R.font.inter, weight = FontWeight.Normal),
    Font(R.font.inter, weight = FontWeight.Medium),
    Font(R.font.inter, weight = FontWeight.SemiBold),
    Font(R.font.inter, weight = FontWeight.Bold),
)

val RobotoFontFamily = FontFamily(
    Font(R.font.roboto, weight = FontWeight.Normal),
    Font(R.font.roboto, weight = FontWeight.Medium),
    Font(R.font.roboto, weight = FontWeight.Bold),
)
