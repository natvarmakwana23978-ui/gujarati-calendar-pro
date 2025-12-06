package com.gujaraticalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gujaraticalendar.ui.theme.GujaratiCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GujaratiCalendarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // હેડર
        Text(
            text = "ગુજરાતી કેલેન્ડર એપ",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // આજની તારીખ
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📅 આજનું પંચાંગ",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // તારીખ વિગતો
                DateDetailRow(label = "વિક્રમ સંવત:", value = "૨૦૮૨")
                DateDetailRow(label = "ગુજરાતી મહિનો:", value = "કારતક")
                DateDetailRow(label = "તિથી:", value = "સુદ-૧")
                DateDetailRow(label = "વાર:", value = "રવિવાર")
                DateDetailRow(label = "પક્ષ:", value = "સુદ પક્ષ")
                DateDetailRow(label = "ચોઘડીયા:", value = "લાભ")
                DateDetailRow(label = "તહેવાર:", value = "બેસતુ વર્ષ")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // બટન્સ
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { /* વિજેટ ઉમેરો */ }) {
                Text(text = "➕ વિજેટ ઉમેરો")
            }
            
            Button(onClick = { /* જન્મદિવસ ઉમેરો */ }) {
                Text(text = "🎂 જન્મદિવસ")
            }
        }
    }
}

@Composable
fun DateDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
