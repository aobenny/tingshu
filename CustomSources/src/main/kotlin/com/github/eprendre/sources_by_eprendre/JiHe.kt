package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlDirectExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup

object JiHe: TingShu(){
    override fun getSourceId(): String {
        return "c6672f095d1248b58898c379d8989306"
    }

    override fun getUrl(): String {
        return "https://www.gcores.com/radios"
    }

    override fun getName(): String {
        return "机核 GCORES"
    }

    override fun getDesc(): String {
        return "推荐指数:2星 ⭐⭐\n机核从2010年开始一直致力于分享游戏玩家的生活，以及深入探讨游戏相关的文化。我们开发原创的电台以及视频节目，一直在不断寻找民间高质量的内容创作者。 我们坚信游戏不止是游戏，游戏中包含的科学，文化，历史等各个层面的知识和故事，它们同时也会辐射到二次元甚至电影的领域，这些内容非常值得分享给热爱游戏的您。"
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        return Pair(emptyList(), 1)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "机核", listOf(
                CategoryTab("电台", "https://www.gcores.com/radios?page=1")
            )
        )

        return listOf(menu1)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config(false).get()
        val totalPage = 132
        val currentPage = doc.selectFirst(".pagination_item.is_active > a").text().toInt()
        val nextUrl = doc.selectFirst(".pagination_next > a").absUrl("href")

        val list = ArrayList<Book>()
        val elementList = doc.select(".original-radio")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst(".original_imgArea").attr("style")
                .replace("background-image:url(", "")
                .replace(")", "")
            val bookUrl = element.selectFirst(".am_card_inner > .am_card_content.original_content").absUrl("href")
            val title = element.selectFirst(".am_card_title").text()
            val author = ""
            val artist = ""
            val status = element.selectFirst(".avatar_text > h3").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
            })
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {
            val doc = Jsoup.connect(bookUrl).config(false).get()
            val downloadUrl = doc.selectFirst("svg[data-icon=download]").parent().absUrl("href")
            Episode("1", downloadUrl).apply {
                episodes.add(this)
            }
        }
        return BookDetail(episodes)
    }
}