package com.example.basicalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basicalarm.ui.theme.BasicAlarmTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        setContent {
            BasicAlarmTheme { AlarmManager() }
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "foxandroidReminderChannel"
            val descriptionText = "Channel For Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("foxandroid", name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}


@Composable
fun AlarmManager(modifier: Modifier = Modifier) {
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    var timeText by remember { mutableStateOf("No time selected") }
    var alarmMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    )
    {
        Text(
            text = "Set an Alarm",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = timeText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold, // Làm chữ in đậm
                fontSize = 36.sp // Kích thước chữ 14sp
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = alarmMessage,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                showTimePicker(context) { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                    timeText = "Selected Time: $hour:$minute"}
            },
            modifier = Modifier.fillMaxWidth(0.8f) // Nút rộng 80% màn hình
        ) {
            Text(
                text = "Select Time",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = alarmMessage,
            onValueChange = { alarmMessage = it },
            label = { Text("Enter Alarm Message") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                setAlarm(context, selectedHour, selectedMinute, alarmMessage)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = "Set Alarm",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                cancelAlarm(context)},
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = "Cancel Alarm",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
        }
    }
}
fun showTimePicker(context: Context, onTimeSelected: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        onTimeSelected(selectedHour, selectedMinute)
    }, hour, minute, true).show()
}
fun cancelAlarm(context: Context) {
    val intent = Intent(AlarmClock.ACTION_DISMISS_ALARM).apply {
        putExtra(AlarmClock.EXTRA_MESSAGE, "Cancel Alarm")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
        Toast.makeText(context, "Alarm canceled", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "No alarm app available", Toast.LENGTH_SHORT).show()
    }
}

fun setAlarm(context: Context, hour: Int, minute: Int, message: String) {
    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_HOUR, hour)
        putExtra(AlarmClock.EXTRA_MINUTES, minute)
        putExtra(AlarmClock.EXTRA_MESSAGE, message.ifEmpty { "Alarm!" })
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
        Toast.makeText(context, "Alarm set for $hour:$minute", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "No alarm app available", Toast.LENGTH_LONG).show()
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AlarmManager(
    )
}