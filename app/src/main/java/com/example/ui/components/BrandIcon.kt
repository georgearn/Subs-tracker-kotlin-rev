package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.theme.getCategoryColor

data class BrandStyle(
    val backgroundColor: Color,
    val iconTint: Color? = null
)

private val BrandDrawables: Map<String, Int> = mapOf(
    "1password" to R.drawable.ic_brand_1password,
    "adobe" to R.drawable.ic_brand_adobe,
    "amazon" to R.drawable.ic_brand_amazon,
    "applemusic" to R.drawable.ic_brand_applemusic,
    "bitwarden" to R.drawable.ic_brand_bitwarden,
    "canva" to R.drawable.ic_brand_canva,
    "chatgpt" to R.drawable.ic_brand_chatgpt,
    "claude" to R.drawable.ic_brand_claude,
    "discord" to R.drawable.ic_brand_discord,
    "dropbox" to R.drawable.ic_brand_dropbox,
    "expressvpn" to R.drawable.ic_brand_expressvpn,
    "figma" to R.drawable.ic_brand_figma,
    "github" to R.drawable.ic_brand_github,
    "google" to R.drawable.ic_brand_google_color,
    "grammarly" to R.drawable.ic_brand_grammarly,
    "hbomax" to R.drawable.ic_brand_hbomax,
    "hulu" to R.drawable.ic_brand_hulu,
    "icloud" to R.drawable.ic_brand_icloud,
    "lastpass" to R.drawable.ic_brand_lastpass,
    "linkedin" to R.drawable.ic_brand_linkedin,
    "microsoft" to R.drawable.ic_brand_microsoft_color,
    "netflix" to R.drawable.ic_brand_netflix,
    "nordvpn" to R.drawable.ic_brand_nordvpn,
    "notion" to R.drawable.ic_brand_notion,
    "openai" to R.drawable.ic_brand_openai,
    "paypal" to R.drawable.ic_brand_paypal,
    "playstation" to R.drawable.ic_brand_playstation,
    "protonvpn" to R.drawable.ic_brand_protonvpn,
    "slack" to R.drawable.ic_brand_slack,
    "soundcloud" to R.drawable.ic_brand_soundcloud,
    "spotify" to R.drawable.ic_brand_spotify,
    "steam" to R.drawable.ic_brand_steam,
    "surfshark" to R.drawable.ic_brand_surfshark,
    "telegram" to R.drawable.ic_brand_telegram,
    "twitch" to R.drawable.ic_brand_twitch,
    "wordpress" to R.drawable.ic_brand_wordpress,
    "xbox" to R.drawable.ic_brand_xbox,
    "youtube" to R.drawable.ic_brand_youtube,
    "zoom" to R.drawable.ic_brand_zoom
)

private val BrandStyles: Map<String, BrandStyle> = mapOf(
    "1password" to BrandStyle(backgroundColor = Color(0xFF0A85EA)),
    "adobe" to BrandStyle(backgroundColor = Color(0xFFEB1000)),
    "amazon" to BrandStyle(backgroundColor = Color(0xFF232F3E), iconTint = Color(0xFFFF9900)),
    "applemusic" to BrandStyle(backgroundColor = Color(0xFFFA243C)),
    "bitwarden" to BrandStyle(backgroundColor = Color(0xFF175DDC)),
    "canva" to BrandStyle(backgroundColor = Color(0xFF00C4CC)),
    "chatgpt" to BrandStyle(backgroundColor = Color(0xFF10A37F)),
    "claude" to BrandStyle(backgroundColor = Color(0xFFD97706)),
    "discord" to BrandStyle(backgroundColor = Color(0xFF5865F2)),
    "dropbox" to BrandStyle(backgroundColor = Color(0xFF0061FF)),
    "expressvpn" to BrandStyle(backgroundColor = Color(0xFFDA3940)),
    "figma" to BrandStyle(backgroundColor = Color(0xFFF24E1E)),
    "github" to BrandStyle(backgroundColor = Color(0xFF24292E)),
    "google" to BrandStyle(backgroundColor = Color(0xFFFFFFFF)),
    "grammarly" to BrandStyle(backgroundColor = Color(0xFF15C39A)),
    "hbomax" to BrandStyle(backgroundColor = Color(0xFF5822B4)),
    "hulu" to BrandStyle(backgroundColor = Color(0xFF1CE783), iconTint = Color(0xFF08331A)),
    "icloud" to BrandStyle(backgroundColor = Color(0xFF3699DB)),
    "lastpass" to BrandStyle(backgroundColor = Color(0xFFD32D27)),
    "linkedin" to BrandStyle(backgroundColor = Color(0xFF0A66C2)),
    "microsoft" to BrandStyle(backgroundColor = Color(0xFFFFFFFF)),
    "netflix" to BrandStyle(backgroundColor = Color(0xFF141414), iconTint = Color(0xFFE50914)),
    "nordvpn" to BrandStyle(backgroundColor = Color(0xFF4687FF)),
    "notion" to BrandStyle(backgroundColor = Color(0xFF2E3038)),
    "openai" to BrandStyle(backgroundColor = Color(0xFF10A37F)),
    "paypal" to BrandStyle(backgroundColor = Color(0xFF0079C1)),
    "playstation" to BrandStyle(backgroundColor = Color(0xFF003791)),
    "protonvpn" to BrandStyle(backgroundColor = Color(0xFF6D4AFF)),
    "slack" to BrandStyle(backgroundColor = Color(0xFF4A154B)),
    "soundcloud" to BrandStyle(backgroundColor = Color(0xFFFF5500)),
    "spotify" to BrandStyle(backgroundColor = Color(0xFF1DB954)),
    "steam" to BrandStyle(backgroundColor = Color(0xFF171A21), iconTint = Color(0xFF66C0F4)),
    "surfshark" to BrandStyle(backgroundColor = Color(0xFF1EA896)),
    "telegram" to BrandStyle(backgroundColor = Color(0xFF24A1DE)),
    "twitch" to BrandStyle(backgroundColor = Color(0xFF9146FF)),
    "wordpress" to BrandStyle(backgroundColor = Color(0xFF21759B)),
    "xbox" to BrandStyle(backgroundColor = Color(0xFF107C10)),
    "youtube" to BrandStyle(backgroundColor = Color(0xFFFF0000)),
    "zoom" to BrandStyle(backgroundColor = Color(0xFF2D8CFF))
)

fun findBrandStyle(name: String?): BrandStyle? {
    if (name.isNullOrBlank()) return null
    val flat = name.lowercase().filter { it.isLetterOrDigit() }
    for ((brand, style) in BrandStyles) {
        if (flat.contains(brand)) {
            return style
        }
    }
    return null
}

fun findBrandDrawableId(name: String?): Int? {
    if (name.isNullOrBlank()) return null
    val flat = name.lowercase().filter { it.isLetterOrDigit() }
    for ((brand, resId) in BrandDrawables) {
        if (flat.contains(brand)) {
            return resId
        }
    }
    return null
}

@Composable
fun ServiceIcon(
    name: String,
    category: String? = "Other",
    size: Dp = 44.dp,
    shapeType: String = "rounded" // "rounded" or "circle"
) {
    val brandResId = findBrandDrawableId(name)
    val brandStyle = findBrandStyle(name)
    val shape = if (shapeType == "circle") CircleShape else RoundedCornerShape(size * 0.28f)
    val categoryColor = getCategoryColor(category)

    if (brandResId != null) {
        val backgroundColor = brandStyle?.backgroundColor ?: categoryColor
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            val iconTint = brandStyle?.iconTint
            if (iconTint != null) {
                Image(
                    painter = painterResource(id = brandResId),
                    contentDescription = name,
                    modifier = Modifier.size(size * 0.72f),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(iconTint)
                )
            } else {
                Image(
                    painter = painterResource(id = brandResId),
                    contentDescription = name,
                    modifier = Modifier.size(size * 0.72f),
                    contentScale = ContentScale.Fit
                )
            }
        }
    } else {
        val initial = if (name.isNotBlank()) name.trim().take(1).uppercase() else "?"
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(categoryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.44f).sp
            )
        }
    }
}
