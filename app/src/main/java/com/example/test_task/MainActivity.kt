package com.example.test_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.test_task.ui.theme.Test_taskTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

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
                    StockList(quotesViewState)
                }
            }
        }
    }
}

@Composable
fun StockList(quotesViewState: QuotesViewState) {
    LazyColumn() {
        items(quotesViewState.quotes) { quote ->
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row {
                            AsyncImage(
                                modifier = Modifier
                                    .size(24.dp),
                                model = "https://tradernet.ru/logos/get-logo-by-ticker?ticker=${quote.ticker.lowercase()}",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )
                            Text(
                                modifier = Modifier.weight(1f),
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
                                    modifier = Modifier.padding(4.dp),
                                    text = "+12.3%"
                                )
                            }
                        }

                        Row {
                            quote.lastStock?.let {
                                Text(
                                    text = quote.lastStock,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            quote.name?.let {
                                Text(
                                    text = " | ${quote.name}",
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Test_taskTheme {
        StockList(
            QuotesViewState(
                quotes = listOf(
                    Quote(
                        ticker = "ticker",
                        percentChanges = "percentChanges",
                        lastStock = "lastStock",
                        name = "name",
                        lastPriceDeal = "lastPriceDeal",
                    )
                )
            )
        )
    }
}