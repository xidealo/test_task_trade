package com.example.test_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.test_task.ui.Shimmer
import com.example.test_task.ui.theme.Test_taskTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

const val LOADING_IMAGE_LINK = "https://tradernet.ru/logos/get-logo-by-ticker?ticker="

class MainActivity : ComponentActivity() {

    val viewModel: QuotesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Test_taskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val quotesViewState by viewModel.state.collectAsState()
                    QuotesScreen(quotesViewState)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startCheckQuotes()
    }

    override fun onStop() {
        viewModel.stopCheckQuotes()
        super.onStop()
    }

    @Composable
    fun QuotesScreen(quotesViewState: QuotesViewState) {
        if (quotesViewState.isLoading) {
            LoadingScreen()
        } else {
            if (quotesViewState.error == null) {
                QuotesList(quotesViewState)
            } else {
                ErrorScreen(
                    textId = R.string.title_error,
                    action = {
                        quotesViewState.error.errorAction?.invoke()
                    }
                )
            }
        }
    }

    @Composable
    fun LoadingScreen() {
        Column(modifier = Modifier.fillMaxWidth()) {
            repeat(20) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Shimmer(modifier = Modifier.size(24.dp))
                        Shimmer(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .width(60.dp)
                                .height(26.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Shimmer(
                            modifier = Modifier
                                .width(56.dp)
                                .height(30.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Shimmer(
                            modifier = Modifier
                                .width(92.dp)
                                .height(16.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Shimmer(
                            modifier = Modifier
                                .width(72.dp)
                                .height(16.dp)
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .padding(),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 2.dp
                    )
                }
            }
        }
    }

    @Composable
    fun QuotesList(quotesViewState: QuotesViewState) {
        LazyColumn() {
            items(
                quotesViewState.quotes,
                key = {
                    it.ticker
                }
            ) { quote ->
                Column(modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                    ) {

                    }
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Row {
                                AsyncImage(
                                    modifier = Modifier
                                        .sizeIn(maxWidth = 24.dp, maxHeight = 24.dp),
                                    model = "$LOADING_IMAGE_LINK${quote.ticker.lowercase()}",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .weight(1f),
                                    text = quote.ticker,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults
                                        .cardColors(
                                            containerColor =
                                            if (true) {
                                                Color(0xFF71bd41)
                                            } else {
                                                Color(0xFFfc2c54)
                                            },
                                            contentColor = Color.White

                                        ),
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(4.dp),
                                        text = "+12.3%"
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                            ) {
                                quote.lastStock?.let {
                                    Text(
                                        text = quote.lastStock,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                quote.name?.let {
                                    Text(
                                        modifier = Modifier
                                            .weight(1f),
                                        text = " | ${quote.name}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                quote.lastPriceDeal?.let {
                                    Text(
                                        text = "${quote.lastPriceDeal}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                quote.pointChangesFromLastSession?.let {
                                    Text(
                                        text = "${quote.pointChangesFromLastSession}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_right),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .padding(),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 2.dp
                    )
                }
            }
        }
    }

    @Composable
    fun ErrorScreen(
        @StringRes textId: Int,
        action: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background
                )
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = textId),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                modifier = Modifier
                    .padding(bottom = 24.dp),
                onClick = action,
                content = {
                    Text(
                        text = stringResource(id = R.string.action_retry)
                            .uppercase()
                    )
                }
            )
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun QuotesScreenPreview() {
        Test_taskTheme {
            QuotesList(
                QuotesViewState(
                    quotes = listOf(
                        Quote(
                            ticker = "ticker",
                            percentChangesFromLastSession = "percentChanges",
                            lastStock = "lastStock",
                            name = "name",
                            lastPriceDeal = "lastPriceDeal",
                            pointChangesFromLastSession = "+34.2"
                        )
                    )
                )
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun QuotesScreenWithErrorPreview() {
        Test_taskTheme {
            QuotesScreen(
                QuotesViewState(
                    error =
                    QuotesViewState.Error(throwable = Throwable("dsfg"),
                        errorAction = {

                        })
                )
            )
        }
    }
}