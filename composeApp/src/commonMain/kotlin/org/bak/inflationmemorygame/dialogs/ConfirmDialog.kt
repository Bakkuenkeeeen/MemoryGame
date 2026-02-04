package org.bak.inflationmemorygame.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.confirm_dialog_cancel
import inflationmemorygame.composeapp.generated.resources.confirm_dialog_ok
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmDialog(
    modifier: Modifier,
    title: StringResource? = null,
    message: StringResource,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .padding(all = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {
        if (title != null) {
            Text(text = stringResource(title), fontSize = 18.sp)
        }
        Text(text = stringResource(message), fontSize = 16.sp)
        Row(
            modifier = Modifier.align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(Res.string.confirm_dialog_cancel))
            }
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(Res.string.confirm_dialog_ok))
            }
        }
    }
}