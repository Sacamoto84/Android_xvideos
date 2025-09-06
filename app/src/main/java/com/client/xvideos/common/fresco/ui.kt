package com.client.xvideos.common.fresco

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Главный экран управления очередью загрузок
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadQueueScreen() {
    val queueState by DownloadQueueManager.queueState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Download Queue",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Кнопка логирования состояния (для отладки)
                    IconButton(
                        onClick = { DownloadQueueManager.logCurrentState() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Log state"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Статистическая карточка
            QueueStatisticsCard(queueState = queueState)

            Spacer(modifier = Modifier.height(16.dp))

            // Управляющие кнопки
            QueueControlButtons(queueState = queueState)

            Spacer(modifier = Modifier.height(16.dp))

            // Список активных загрузок
            ActiveDownloadsList(queueState = queueState)
        }
    }
}

@Composable
fun QueueStatisticsCard(queueState: DownloadQueueState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                queueState.isQueueFull -> MaterialTheme.colorScheme.errorContainer
                queueState.currentSize > queueState.maxQueueSize * 0.7 -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Queue Status",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Анимированный индикатор статуса
                AnimatedQueueStatusIcon(queueState = queueState)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Прогресс бар с анимацией
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Active Downloads",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${queueState.currentSize}/${queueState.maxQueueSize}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val animatedProgress by animateFloatAsState(
                    targetValue = queueState.currentSize.toFloat() / queueState.maxQueueSize.toFloat(),
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                    label = "progress"
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        queueState.isQueueFull -> MaterialTheme.colorScheme.error
                        queueState.currentSize > queueState.maxQueueSize * 0.7 -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Статистика в ряд
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    value = queueState.totalCompleted,
                    label = "Completed",
                    color = Color(0xFF4CAF50)
                )

                StatisticItem(
                    icon = Icons.Default.Error,
                    value = queueState.totalFailed,
                    label = "Failed",
                    color = Color(0xFFF44336)
                )

                StatisticItem(
                    icon = Icons.Default.PlayArrow,
                    value = queueState.currentSize,
                    label = "Active",
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AnimatedQueueStatusIcon(queueState: DownloadQueueState) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                when {
                    queueState.isQueueFull -> MaterialTheme.colorScheme.error
                    queueState.hasActiveDownloads -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (queueState.hasActiveDownloads) {
            // Анимированная иконка загрузки
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Loading",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { rotationZ = rotation }
            )
        } else {
            // Статичная иконка
            Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = "Idle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun QueueControlButtons(queueState: DownloadQueueState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Кнопка отмены всех загрузок
        Button(
            onClick = {
                DownloadQueueManager.cancelAll()
            },
            enabled = queueState.hasActiveDownloads,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White
            ),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cancel All")
        }

        // Кнопка сброса статистики
        OutlinedButton(
            onClick = {
                DownloadQueueManager.resetStatistics()
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.RestartAlt,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset Stats")
        }
    }
}

@Composable
fun ActiveDownloadsList(queueState: DownloadQueueState) {

    Text(
        text = "Active Downloads",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    AnimatedVisibility(
        visible = queueState.activeDownloads.isEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        EmptyQueueState()
    }

    AnimatedVisibility(
        visible = queueState.activeDownloads.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = queueState.activeDownloads,
                key = { it.url }
            ) { downloadItem ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally() + fadeIn(),
                    exit = slideOutHorizontally() + fadeOut()
                ) {
                    DownloadItemCard(
                        item = downloadItem,
                        onCancel = {
                            DownloadQueueManager.cancelDownload(downloadItem.url)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyQueueState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No active downloads",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Queue is ready for new downloads",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DownloadItemCard(
    item: DownloadItem,
    onCancel: () -> Unit
) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Обновляем время каждую секунду для отображения актуальной длительности
    LaunchedEffect(item) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Название файла
                Text(
                    text = item.fileName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // URL (сокращенный)
                Text(
                    text = item.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Длительность загрузки (обновляется в реальном времени)
                val duration = currentTime - item.startTime
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDuration(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Статус и управление
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Анимированный прогресс индикатор
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )

                // Кнопка отмены
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel download",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Компактный виджет для отображения в других экранах
@Composable
fun DownloadQueueWidget(
    modifier: Modifier = Modifier
) {
    val queueState by DownloadQueueManager.queueState.collectAsState()

    AnimatedVisibility(
        visible = queueState.hasActiveDownloads,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    queueState.isQueueFull -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Column {
                        Text(
                            text = "Downloading ${queueState.currentSize} items",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        if (queueState.isQueueFull) {
                            Text(
                                text = "Queue is full",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                TextButton(
                    onClick = { DownloadQueueManager.cancelAll() }
                ) {
                    Text(
                        "Cancel All",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Вспомогательные функции
private fun formatDuration(millis: Long): String {
    val seconds = millis / 1000
    return when {
        seconds < 60 -> "${seconds}s"
        seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
        else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
    }
}

// Пример использования в основном экране
@Composable
fun MainScreenWithDownloadQueue() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Ваш основной контент
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Your Main App Content Here",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Компактный виджет очереди загрузок
        DownloadQueueWidget()
    }
}

// Демо экран с кнопками для тестирования
@Composable
fun DownloadQueueDemo() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Download Queue Demo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Кнопки для тестирования
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    // Симуляция добавления загрузки
                    // В реальном приложении это будет происходить автоматически через Fresco
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Simulate Download")
            }

            Button(
                onClick = { DownloadQueueManager.logCurrentState() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Log State")
            }
        }

        // Компактный виджет
        DownloadQueueWidget()

        Spacer(modifier = Modifier.height(16.dp))

        // Полный экран управления очередью (можно открыть в отдельной активности)
        DownloadQueueScreen()
    }
}