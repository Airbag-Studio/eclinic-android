package it.airbagstudio.ticare.pages.patientDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.shimmerBrush
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun GridButton(
    image: Painter,
    label: String,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable {
                if (!isLoading) {
                    onClick()
                }
            }
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val boxModifier = if (isLoading) {
            Modifier.background(shimmerBrush())
        } else {
            Modifier
        }
        val badgeOpacity = if (badgeCount > 0 && !isLoading) 1f else 0f
        Spacer(modifier = Modifier.weight(0.2f))
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .alpha(badgeOpacity)
                .padding(end = 16.dp)
                .zIndex(1f)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
                    .widthIn(min = 24.dp)
                    .padding(horizontal = 5.dp),
                text = "$badgeCount",
                color = MaterialTheme.colorScheme.errorContainer,
                textAlign = TextAlign.Center,
                lineHeight = 25.sp
            )
        }


        BoxWithConstraints {
            val boxWithConstraintsScope = this
            //You can use this scope to get the minWidth, maxWidth, minHeight, maxHeight in dp and constraints
            val height = if(boxWithConstraintsScope.maxHeight > 120.dp ) 80.dp else boxWithConstraintsScope.maxHeight - 40.dp
            Column {
                Image(
                    modifier = Modifier.height(height = height),
                    painter = image,
                    contentDescription = label,
                    alpha = if (isLoading) 0f else 1f
                )
            }
        }
/*
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier

                .heightIn(0.dp, 25.dp)
                .weight(0.7f)
                .then(boxModifier)
        ) {
            Image(
                modifier = Modifier,
                painter = image,
                contentDescription = label,
                alpha = if (isLoading) 0f else 1f
            )
        }

 */
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 4.dp)
                    .height(20.dp)
                    .background(
                        shimmerBrush()
                    )
            ) {

            }
        } else {
            Column(modifier = Modifier.weight(0.3f)) {
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.weight(1f))
            }

        }
        //Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
@Preview
private fun GridButtonPreview() {
    AppTheme() {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(280.dp)
        ) {
            GridButton(
                modifier = Modifier.weight(1f),
                image = painterResource(id = R.drawable.ic_wounds),
                label = "ferite",
                isLoading = true,
                badgeCount = 23
            ) {

            }
            GridButton(
                modifier = Modifier.weight(1f),
                image = painterResource(id = R.drawable.ic_wounds),
                label = "ferite",
                isLoading = false,
                badgeCount = 23
            ) {

            }
            GridButton(
                modifier = Modifier.weight(1f),
                image = painterResource(id = R.drawable.ic_wounds),
                label = "ferite",
                isLoading = false,
                badgeCount = 2
            ) {

            }
        }
    }
}