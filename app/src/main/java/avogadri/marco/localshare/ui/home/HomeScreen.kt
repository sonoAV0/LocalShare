package avogadri.marco.localshare.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WifiTethering
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import avogadri.marco.localshare.data.AppContainer
import avogadri.marco.localshare.data.local.db.HistoryEntity
import avogadri.marco.localshare.data.local.db.TransferDirection
import avogadri.marco.localshare.ui.components.LocalShareLogo
import avogadri.marco.localshare.ui.permissions.rememberWifiDirectPermissionRequester
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onStartTransfer: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recentHistory by viewModel.recentHistory.collectAsStateWithLifecycle()
    val requestWifiDirectPermissions = rememberWifiDirectPermissionRequester(onGranted = onStartTransfer)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LocalShareLogo(size = 36.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "LocalShare",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        DeviceStatusCard(uiState = uiState, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            onClick = requestWifiDirectPermissions,
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WifiTethering,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "New Transfer",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = "Connect to a nearby device and share files",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HomeActionCard(
                icon = Icons.Outlined.History,
                label = "History",
                onClick = onOpenHistory,
                modifier = Modifier.weight(1f),
            )
            HomeActionCard(
                icon = Icons.Outlined.Settings,
                label = "Settings",
                onClick = onOpenSettings,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Recent transactions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (recentHistory.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        )
                        Text(
                            text = "No transactions yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                ) {
                    val cardHeight = (maxHeight - 16.dp) / 3
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        recentHistory.forEach { entry ->
                            RecentTransactionCard(
                                entry = entry,
                                onClick = onOpenHistory,
                                modifier = Modifier.height(cardHeight),
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RecentTransactionCard(
    entry: HistoryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val isSent = entry.direction == TransferDirection.SENT
            Surface(
                shape = CircleShape,
                color = if (isSent) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = if (isSent) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    contentDescription = null,
                    tint = if (isSent) MaterialTheme.colorScheme.onPrimaryContainer
                           else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(10.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.fileName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
                Text(
                    text = "${entry.peerDeviceId.take(17)}  •  ${formatTimestamp(entry.timestampMillis)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun DeviceStatusCard(uiState: HomeUiState, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.PhoneAndroid,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "This device (UUID)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = uiState.deviceId.take(8),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            when {
                uiState.isRegistering -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
                uiState.errorMessage != null -> Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = "Registration failed",
                    tint = MaterialTheme.colorScheme.error,
                )
                else -> Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Registered",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

private val timestampFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

private fun formatTimestamp(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(timestampFormatter)

private val HomeViewModelFactory = viewModelFactory {
    initializer {
        HomeViewModel(
            AppContainer.deviceIdProvider,
            AppContainer.deviceRepository,
            AppContainer.historyRepository,
        )
    }
}
