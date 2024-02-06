package com.kltn.anigan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kltn.anigan.ImageClass
import com.kltn.anigan.R
import com.kltn.anigan.routes.Routes

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
        ) {
            Header()
            ListButton(navController = navController)
        }
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
                .verticalScroll(rememberScrollState())
        ) {
            Banner()

            Title(text1 = "Edit Your Photos", text2 = "All photos >")
            val defaultLibrary = listOf<ImageClass>(
                ImageClass()
            )
            PhotoLibrary(defaultLibrary)

            Title(text1 = "Magazine Collage", text2 = "See all >")
            PhotoLibrary(defaultLibrary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Column(
        modifier = Modifier.background(color = colorResource(id = R.color.black))
    ){
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
        ) {
            Header()
            ListButton()
        }
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
                .verticalScroll(rememberScrollState())
        ) {
            Banner()

            Title(text1 = "Edit Your Photos", text2 = "All photos >")
            val defaultLibrary = listOf<ImageClass>(
                ImageClass()
            )
            PhotoLibrary(defaultLibrary)

            Title(text1 = "Magazine Collage", text2 = "See all >")
            PhotoLibrary(defaultLibrary)
        }
    }


}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Row (
        modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
    ){
        //Icon menu
        Image(
            painter = painterResource(id = R.drawable.icon_menu),
            contentDescription = "icon_menu",
            modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(17.dp),
            )

        //Icon notification
        Image(
            painter = painterResource(id = R.drawable.icon_bell),
            contentDescription = "icon_menu",
            modifier
                .padding(start = 17.dp, top = 16.dp)
                .size(17.dp)
        )

    }
}

@Composable
private fun FuncButton(imageId: Int, text: String,  onClick: () -> Unit = {}, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "edit_btn",
            modifier.size(50.dp)
            )
        Text(
            text = text,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ListButton(modifier: Modifier = Modifier, navController: NavController ?= null) {
    Row (
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround

    )
    {
        FuncButton(R.drawable.edit_btn, "Edit",
            modifier = modifier)
        FuncButton(R.drawable.ai_tool_btn, "AI Tools",
            onClick = {
                navController?.navigate(Routes.AI_TOOLS.route)
            },
            modifier = modifier)
        FuncButton(R.drawable.collage_btn, "Collage",
            modifier = modifier)
        FuncButton(R.drawable.bg_remove_btn, "Bg\nremover",
            modifier = modifier)
        FuncButton(R.drawable.more_btn, "More",
            modifier = modifier)
    }
}

@Composable
private fun Banner(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.banner),
        contentDescription = "banner",
        modifier.padding(
            horizontal = 12.dp,
            vertical = 30.dp))
}

@Composable
internal fun Title(text1: String, text2: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text1,
            modifier= Modifier.alignByBaseline(),
            color = colorResource(id = R.color.white),
            fontSize = 25.sp
        )
        Text(
            text = text2,
            modifier = Modifier.alignByBaseline(),
            color = colorResource(id = R.color.white),
            fontSize = 15.sp,
        )
    }

}

@Composable
internal fun PhotoLibrary(itemList: List<ImageClass>, modifier: Modifier = Modifier) {
    LazyRow (
        modifier = modifier
            .padding(vertical = 15.dp)
    ){
        items(itemList) {
            Image(
                painter = painterResource(id = it.imageId),
                contentDescription = it.description,
                modifier
                    .padding(start = 12.dp)
                    .size(100.dp)
            )
        }

    }

}