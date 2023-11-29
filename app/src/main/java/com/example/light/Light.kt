package com.example.light

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@Composable
fun Light() {
    var switchState by remember { mutableStateOf(false) }
    var switchState1 by remember { mutableStateOf(false) }
    var valueSlider by remember { mutableStateOf(0f) }
    val context = LocalContext.current

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference: DatabaseReference = database.reference.child("light_status")
    val databaseReference1: DatabaseReference = database.reference.child("light_status1")
    val databaseReference2: DatabaseReference = database.reference.child("brightness")

    LaunchedEffect(databaseReference) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.value as Boolean
                switchState = status
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        databaseReference.addValueEventListener(listener)
    }
    LaunchedEffect(databaseReference1) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.value as Boolean
                switchState1 = status
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        databaseReference1.addValueEventListener(listener)
    }

    LaunchedEffect(databaseReference2) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val brightness = (snapshot.value as? Number)?.toFloat() ?: 0f
                valueSlider = brightness
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        databaseReference2.addValueEventListener(listener)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = SpaceBetween
        ) {
            Text(text = stringResource(R.string.light_name))
            Switch(
                checked = switchState,
                onCheckedChange = {
                    switchState = it
                    databaseReference.setValue(it)
                    Toast.makeText(context, "Light status set to $it", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            IndicatorBox(isOn = switchState)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalArrangement = SpaceBetween
            ) {
                Text(text = stringResource(R.string.light_name1))
                Switch(
                    checked = switchState1,
                    onCheckedChange = {
                        switchState1 = it
                        databaseReference1.setValue(it)
                        Toast.makeText(context, "Light status set to $it", Toast.LENGTH_SHORT)
                            .show()
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                IndicatorBox(isOn = switchState1)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_brightness_medium),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Slider(
                    value = valueSlider * 100,
                    onValueChange = {
                        valueSlider = it / 100
                        databaseReference2.setValue(valueSlider)
                    },
                    valueRange = 0f..100f,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(text = "${valueSlider.toInt()}")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Center
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                        .background(Color.Yellow.copy(alpha = valueSlider))
                )
            }
        }
    }
}

@Composable
fun IndicatorBox(isOn: Boolean) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(if (isOn) Color.Green else Color.Red)
    )
}
