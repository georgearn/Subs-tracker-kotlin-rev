package com.example.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite

@Composable
fun RenameCardDialog(
    cardNumber: String,
    currentAlias: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var aliasText by remember { mutableStateOf(currentAlias ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Rename Card ${if (cardNumber.isNotBlank()) "•••• $cardNumber" else ""}",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Give this payment card a recognizable name:",
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = aliasText,
                    onValueChange = { aliasText = it },
                    placeholder = { Text("e.g. Main Visa, Work Revolut", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_alias_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = CardBgElevated,
                        unfocusedContainerColor = CardBgElevated,
                        focusedBorderColor = Brand,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(aliasText.trim())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Brand),
                modifier = Modifier.testTag("save_card_alias_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        },
        containerColor = CardBg
    )
}
