import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.color.China

@Composable
fun HDDebugText(text: String) {
    Text(
        text, style = TextStyle(
            color = China.r_yan_zhi_hong,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
    )
}
