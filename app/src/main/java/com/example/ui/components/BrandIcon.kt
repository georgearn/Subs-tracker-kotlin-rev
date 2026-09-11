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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.theme.getCategoryColor

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
    "google" to R.drawable.ic_brand_google,
    "grammarly" to R.drawable.ic_brand_grammarly,
    "hbomax" to R.drawable.ic_brand_hbomax,
    "hulu" to R.drawable.ic_brand_hulu,
    "icloud" to R.drawable.ic_brand_icloud,
    "lastpass" to R.drawable.ic_brand_lastpass,
    "linkedin" to R.drawable.ic_brand_linkedin,
    "microsoft" to R.drawable.ic_brand_microsoft,
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
    val shape = if (shapeType == "circle") CircleShape else RoundedCornerShape(size * 0.28f)
    val categoryColor = getCategoryColor(category)

    if (brandResId != null) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(Color(0xFF1E1E24)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = brandResId),
                contentDescription = name,
                modifier = Modifier.size(size * 0.78f),
                contentScale = ContentScale.Fit
            )
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
