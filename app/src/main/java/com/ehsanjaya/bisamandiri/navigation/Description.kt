package com.ehsanjaya.bisamandiri.navigation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ehsanjaya.bisamandiri.R
import com.ehsanjaya.bisamandiri.ui.theme.dyslexicRegularFontFamily
import com.ehsanjaya.bisamandiri.utilites.IconButtonShimmer
import com.ehsanjaya.bisamandiri.view_models.DescriptionViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Description(
    navController: NavController,
    descriptionViewModel: DescriptionViewModel
) {
    val context = LocalContext.current
    var resetTextToSpeech by remember { mutableStateOf(false) }
    var buttonHomeVisibility by remember { mutableStateOf(false) }

    val instructions = listOf(
        DataDescriptions(
            type = "Dapur",
            descriptions = listOf(
                Data(
                    image = R.drawable.kitchen_sink,
                    title = "Wastafel",
                    description =
                    "tempat untuk mencuci alat makan.\n" +
                            "Tempat untuk mencuci tangan.\n" +
                            "Menjaga dapur tetap rapi."
                ),
                Data(
                    image = R.drawable.frying_pan,
                    title = "Panci",
                    description = "Bentuknya lingkaran.\n" +
                            "Memiliki Pegangan.\n" +
                            "Terbuat dari besi."
                ),
                Data(
                    image = R.drawable.cup,
                    title = "Gelas",
                    description = "Wadah untuk minum.\n" +
                            "Bentuknya Silinder dan transparan.\n" +
                            "Terbuat dari kaca atau plastik."
                ),
                Data(
                    image = R.drawable.plate,
                    title = "Piring",
                    description = "Wadah berbentuk datar.\n" +
                            "digunakan untuk menaruh makanan.\n" +
                            "terbuat dari keramik,kaca,atau plastik."
                ),
                Data(
                    image = R.drawable.bowl,
                    title = "Mangkuk",
                    description = "Wadah berbentuk cekung.\n" +
                            "Dipakai untuk makanan berkuah.\n" +
                            "Lebih tinggi dari pada piring."
                ),
                Data(
                    image = R.drawable.fork,
                    title = "Garpu",
                    description = "Alat makan dengan ujung bercabang.\n" +
                            "Dipakai untuk menusuk atau mengambil makanan."
                ),
                Data(
                    image = R.drawable.teapot,
                    title = "Teko",
                    description = "Wadah untuk menuang air atau teh.\n" +
                            "Ada pegangan dan corong. \n" +
                            "Terbuat dari logam, plastik atau kaca."
                ),
                Data(
                    image = R.drawable.spoon,
                    title = "Sendok",
                    description = "Alat makan berbentuk cekung. \n" +
                            "Dipakai untuk menyendok makanan atau minuman."
                ),
                Data(
                    image = R.drawable.tap,
                    title = "Keran",
                    description = "Alat untuk mengeluarkan Air.\n" +
                            "Bisa dibuka dan ditutup.\n" +
                            "Dipasang di wastafel atau sink."
                )
            )
        ),
        DataDescriptions(
            type = "Kamar Tidur",
            descriptions = listOf(
                Data(
                    image = R.drawable.bed,
                    title = "Kasur",
                    description = "Sebagai alas tidur\n" +
                            "Memberikan dukungan untuk tubuh\n" +
                            "Meningkatkan kualitas tidur"
                )
            )
        ),
        DataDescriptions(
            type = "Ruang Tamu",
            descriptions = listOf(
                Data(
                    image = R.drawable.carpet,
                    title = "Karpet",
                    description = "Pelindung lantai.\n" +
                            "Peredam suara.\n" +
                            "Penghangat ruangan."
                ),
                Data(
                    image = R.drawable.sofa,
                    title = "Sofa",
                    description = "Tempat untuk duduk.\n" +
                            "Ukurannya lebih besar dari kursi.\n" +
                            "Bisa untuk beberapa orang.\n" +
                            "Dilapisi busa dan kain."
                ),
                Data(
                    image = R.drawable.chair,
                    title = "Kursi",
                    description = "Tempat duduk.\n" +
                            "Biasanya punya sandaran.\n" +
                            "Terbuat dari kayu, plastik atau besi.\n" +
                            "Digunakan untuk duduk satu orang."
                ),
                Data(
                    image = R.drawable.table,
                    title = "Meja",
                    description = "Permukaan datar untuk menaruh benda.\n" +
                            "Biasanya punya Empat kaki.\n" +
                            "Terbuat dari kayu, kaca atau plastik.\n" +
                            "Diletakkan di ruang tamu."
                )
            )
        ),
        DataDescriptions(
            type = "Toilet",
            descriptions = listOf(
                Data(
                    image = R.drawable.toilet,
                    title = "Toilet",
                    description = "Nyaman untuk digunakan toilet ini.\n" +
                            "Baik untuk orang dengan cedera (luka pada tubuh).\n" +
                            "Bisa duduk di toilet."
                ),
                Data(
                    image = R.drawable.squat_toilet,
                    title = "Toilet Jongkok",
                    description = "Memudahkan proses buang air besar dengan postur jongkok.\n" +
                            "Membuat otot usus besar rileks.\n" +
                            "Lebih higienis (bersih atau bebas penyakit)."
                ),
                Data(
                    image = R.drawable.urinal,
                    title = "Urinoir",
                    description = "Tempat buang air kecil dengan postur berdiri.\n" +
                            "Mengurangi antrean di toilet.\n" +
                            "Menjaga kebersihan kamar mandi."
                ),
                Data(
                    image = R.drawable.soaking_tub,
                    title = "Bak",
                    description = "Untuk menampung air.\n" +
                            "Sebagai pelengkap kamar mandi.\n" +
                            "Menghemat penggunaan air.\n" +
                            "Mempermudah mengambil air banyak dengan menggunakan gayung."
                ),
            )
        )
    )
    val foundDescriptions = instructions.find { it.type == descriptionViewModel.type.value }

    LaunchedEffect(key1 = Unit) {
        buttonHomeVisibility = false
        delay(500)
        buttonHomeVisibility = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF050404))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        foundDescriptions?.let { it ->
            repeat(it.descriptions.size) { index ->
                val tts = remember { mutableStateOf<TextToSpeech?>(null) }
                var textToSpeak by remember { mutableStateOf("") }

                val description = foundDescriptions.descriptions[index]
                var speaking by remember { mutableStateOf(false) }

                LaunchedEffect(key1 = Unit) {
                    snapshotFlow { resetTextToSpeech }
                        .collect { shouldReset ->
                            if (shouldReset) {
                                tts.value?.stop()
                                tts.value?.shutdown()

                                speaking = false
                                resetTextToSpeech = false
                            }
                        }
                }

                Column(
                    modifier = Modifier
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 10.dp,
                            bottom = 10.dp
                        )
                        .fillMaxWidth()
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(10.dp),
                            spotColor = Color.White,
                            ambientColor = Color.White
                        )
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = Color.White)
                ) {
                    Image(
                        painter = painterResource(description.image),
                        contentDescription = "Image",
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                    )
                    Divider(
                        color = Color(0xFFB6B6B6),
                        thickness = 2f.dp,
                        modifier = Modifier.padding(
                            top = 10.dp,
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 10.dp
                        )
                    )
                    Row(
                        modifier = Modifier
                            .padding(
                                start = 15.dp,
                                bottom = 5.dp
                            )
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = description.title,
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = dyslexicRegularFontFamily,
                                color = Color.Black
                            )
                            Text(
                                text = description.description,
                                fontSize = 10.sp,
                                fontFamily = dyslexicRegularFontFamily,
                                color = Color(0xFF757575)
                            )
                        }
                        AnimatedContent(
                            targetState = speaking,
                            transitionSpec = {
                                fadeIn() + scaleIn() with fadeOut() + scaleOut()
                            },
                            label = "Icon Switch"
                        ) { isSpeaking ->
                            IconButtonShimmer(
                                onClick = {
                                    if (speaking) {
                                        tts.value?.stop()
                                        speaking = false
                                    } else {
                                        textToSpeak = "Disini adalah ${description.title}. Deskripsinya, ${description.description}."

                                        if (tts.value == null) {
                                            tts.value = TextToSpeech(context) { status ->
                                                if (status == TextToSpeech.SUCCESS) {
                                                    val langResult = tts.value?.setLanguage(Locale("id", "ID"))
                                                    if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                                                        langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                                                        return@TextToSpeech
                                                    }
                                                    tts.value?.setSpeechRate(0.7f)
                                                    speaking = true

                                                    tts.value?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                                                        override fun onStart(utteranceId: String?) {
                                                            speaking = true
                                                        }

                                                        override fun onDone(utteranceId: String?) {
                                                            speaking = false
                                                        }

                                                        override fun onError(utteranceId: String?) {
                                                            speaking = false
                                                        }
                                                    })

                                                    tts.value?.speak(
                                                        textToSpeak,
                                                        TextToSpeech.QUEUE_FLUSH,
                                                        null,
                                                        "utteranceId"
                                                    )
                                                }
                                            }
                                        } else {
                                            // TTS already initialized
                                            speaking = true
                                            tts.value?.speak(
                                                textToSpeak,
                                                TextToSpeech.QUEUE_FLUSH,
                                                null,
                                                "utteranceId"
                                            )
                                        }
                                    }
                                },
                                icon = if (isSpeaking) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                iconColor = Color.White,
                                shape = CircleShape,
                                modifier = Modifier
                                    .padding(
                                        start = 10.dp,
                                        end = 10.dp
                                    )
                            )
                        }
                    }
                }
                if (index < foundDescriptions.descriptions.size - 1) {
                    Row(
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 20.dp
                            )
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            color = Color(0xFFB6B6B6),
                            thickness = 2f.dp,
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Arrow Down",
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFB6B6B6)
                        )
                        Divider(
                            color = Color(0xFFB6B6B6),
                            thickness = 2f.dp,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .weight(1f)
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = buttonHomeVisibility,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Box {
                Button(
                    onClick = {
                        resetTextToSpeech = true

                        navController.navigate(Routes.Main) {
                            popUpTo(Routes.Main) {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .padding(5.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(5.dp),
                            spotColor = Color(0xFF9BFFD0),
                            ambientColor = Color.Black
                        ),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D9057),
                        contentColor = Color(0xFFFDF8F7)
                    ),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Text(
                        text = "Home",
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        fontFamily = dyslexicRegularFontFamily,
                        color = Color.White
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd)
                        .offset(
                            x = 10.dp,
                            y = (-10).dp
                        ),
                    tint = Color.White
                )
            }
        }
        Text(
            text = "Copyright - AInovators©, 2025",
            fontSize = 10.sp,
            fontFamily = dyslexicRegularFontFamily,
            color = Color(0xFF8D8E90),
            modifier = Modifier
                .padding(
                    top = 45.dp,
                    bottom = 15.dp,
                    start = 15.dp,
                    end = 15.dp
                )
        )
    }
}

private data class DataDescriptions(
    val type: String,
    val descriptions: List<Data>,
)

private data class Data(
    val image: Int,
    val title: String,
    val description: String
)
