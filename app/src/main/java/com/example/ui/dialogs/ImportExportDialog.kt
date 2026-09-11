package com.example.ui.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.data.model.SubscriptionEntity
import com.example.logic.SubscriptionCalculations
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.GreenSuccess
import com.example.theme.RedAlert
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

@Composable
fun ImportExportDialog(
    subscriptions: List<SubscriptionEntity>,
    primaryCurrency: String,
    onDismiss: () -> Unit,
    onImportSuccess: (currency: String?, list: List<SubscriptionEntity>, replace: Boolean) -> Unit
) {
    val context = LocalContext.current
    var importText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                if (content != null) {
                    importText = content
                    statusMessage = "Loaded file. Choose Merge or Replace below."
                    isError = false
                }
            } catch (e: Exception) {
                statusMessage = "Failed to read file: ${e.localizedMessage}"
                isError = true
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("import_export_dialog"),
            color = CardBg
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Import & Export",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Export Section
                Text(
                    text = "Export Backup",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Export your ${subscriptions.size} subscriptions as a JSON file.",
                    color = TextMuted,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val json = SubscriptionCalculations.exportToJson(subscriptions, primaryCurrency)
                        shareBackupFile(context, json)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("export_backup_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brand)
                ) {
                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export & Share JSON", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Import Section
                Text(
                    text = "Import Backup",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pick a backup file or paste JSON content below:",
                    color = TextMuted,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { filePickerLauncher.launch("*/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("pick_file_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Brand)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pick JSON File", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = importText,
                    onValueChange = { importText = it },
                    placeholder = { Text("Or paste JSON backup here...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("import_json_input"),
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

                if (statusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMessage!!,
                        color = if (isError) RedAlert else GreenSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Merge or Replace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            executeImport(
                                jsonText = importText,
                                replace = false,
                                onError = { statusMessage = it; isError = true },
                                onSuccess = { cur, list ->
                                    onImportSuccess(cur, list, false)
                                    onDismiss()
                                }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("import_merge_button"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = importText.isNotBlank()
                    ) {
                        Text("Merge", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            executeImport(
                                jsonText = importText,
                                replace = true,
                                onError = { statusMessage = it; isError = true },
                                onSuccess = { cur, list ->
                                    onImportSuccess(cur, list, true)
                                    onDismiss()
                                }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("import_replace_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedAlert),
                        enabled = importText.isNotBlank()
                    ) {
                        Text("Replace All", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

private fun executeImport(
    jsonText: String,
    replace: Boolean,
    onError: (String) -> Unit,
    onSuccess: (String?, List<SubscriptionEntity>) -> Unit
) {
    try {
        val (currency, list) = SubscriptionCalculations.parseImportJson(jsonText)
        if (list.isEmpty()) {
            onError("No subscriptions found in file.")
        } else {
            onSuccess(currency, list)
        }
    } catch (e: Exception) {
        onError("Invalid JSON format: ${e.localizedMessage}")
    }
}

private fun shareBackupFile(context: Context, jsonContent: String) {
    try {
        val exportDir = File(context.cacheDir, "exports")
        exportDir.mkdirs()
        val filename = "subscriptions_backup_${LocalDate.now()}.json"
        val file = File(exportDir, filename)
        file.writeText(jsonContent)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Subscription Tracker Backup")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share backup JSON"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
