package com.example.showcaseapp.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class MockBackendInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val segments = request.url.encodedPathSegments
        val body = when {
            segments == listOf("apps") -> appsJson
            segments.size == 2 && segments.first() == "apps" -> findAppJson(segments[1])
            segments == listOf("categories") -> categoriesJson
            segments == listOf("popular") -> popularJson
            else -> null
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(if (body == null) 404 else 200)
            .message(if (body == null) "Not found" else "OK")
            .body((body ?: """{"error":"Not found"}""").toResponseBody(JSON))
            .build()
    }

    private fun findAppJson(id: String): String? {
        val marker = """"id":"$id""""
        val startIndex = appsJson.indexOf(marker)
        if (startIndex == -1) return null

        var objectStart = startIndex
        while (objectStart > 0 && appsJson[objectStart] != '{') objectStart--

        var depth = 0
        for (index in objectStart until appsJson.length) {
            when (appsJson[index]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) return appsJson.substring(objectStart, index + 1)
                }
            }
        }
        return null
    }

    private val popularJson: String
        get() = """
            [
              ${findAppJson("paywise").orEmpty()},
              ${findAppJson("metrogo").orEmpty()},
              ${findAppJson("goshelp").orEmpty()}
            ]
        """.trimIndent()

    private val categoriesJson = """
        [
          {"name":"Финансы","count":2},
          {"name":"Инструменты","count":2},
          {"name":"Игры","count":2},
          {"name":"Транспорт","count":2},
          {"name":"Государственные","count":2}
        ]
    """.trimIndent()

    private val appsJson = """
        [
          {
            "id":"paywise",
            "name":"PayWise",
            "developer":"Digital Finance Lab",
            "category":"Финансы",
            "shortDescription":"Учет расходов, переводы и бюджет в одном приложении",
            "fullDescription":"PayWise помогает контролировать ежедневные расходы, планировать бюджет по категориям и быстро переводить деньги между счетами.",
            "ageRating":"0+",
            "iconGradient":["#18A058","#096BDE"],
            "popular":true,
            "screenshots":[
              {"id":"paywise-1","title":"Баланс и расходы","accentColor":"#18A058","backgroundColor":"#EAF8EF"},
              {"id":"paywise-2","title":"План бюджета","accentColor":"#096BDE","backgroundColor":"#EAF3FF"},
              {"id":"paywise-3","title":"История операций","accentColor":"#F59E0B","backgroundColor":"#FFF7E6"}
            ]
          },
          {
            "id":"coinbox",
            "name":"CoinBox",
            "developer":"FinCraft Studio",
            "category":"Финансы",
            "shortDescription":"Копилка целей и простая аналитика накоплений",
            "fullDescription":"CoinBox превращает накопления в понятные цели: отпуск, техника, учеба или резервный фонд.",
            "ageRating":"6+",
            "iconGradient":["#0EA5E9","#22C55E"],
            "popular":false,
            "screenshots":[
              {"id":"coinbox-1","title":"Цели накоплений","accentColor":"#0EA5E9","backgroundColor":"#E0F2FE"},
              {"id":"coinbox-2","title":"Прогноз","accentColor":"#22C55E","backgroundColor":"#DCFCE7"},
              {"id":"coinbox-3","title":"Напоминания","accentColor":"#64748B","backgroundColor":"#F1F5F9"}
            ]
          },
          {
            "id":"notekit",
            "name":"NoteKit",
            "developer":"North Tools",
            "category":"Инструменты",
            "shortDescription":"Заметки, списки и быстрые голосовые идеи",
            "fullDescription":"NoteKit подходит для учебы и личных задач: создавайте заметки, закрепляйте важное и возвращайтесь к нему через поиск.",
            "ageRating":"0+",
            "iconGradient":["#F97316","#EF4444"],
            "popular":false,
            "screenshots":[
              {"id":"notekit-1","title":"Список заметок","accentColor":"#F97316","backgroundColor":"#FFF7ED"},
              {"id":"notekit-2","title":"Редактор","accentColor":"#EF4444","backgroundColor":"#FEF2F2"},
              {"id":"notekit-3","title":"Метки","accentColor":"#14B8A6","backgroundColor":"#F0FDFA"}
            ]
          },
          {
            "id":"scanpro",
            "name":"ScanPro",
            "developer":"Pocket Office",
            "category":"Инструменты",
            "shortDescription":"Сканер документов с экспортом в PDF",
            "fullDescription":"ScanPro распознает границы документа, улучшает контраст и собирает страницы в аккуратный PDF.",
            "ageRating":"0+",
            "iconGradient":["#334155","#06B6D4"],
            "popular":false,
            "screenshots":[
              {"id":"scanpro-1","title":"Камера","accentColor":"#334155","backgroundColor":"#F8FAFC"},
              {"id":"scanpro-2","title":"Обрезка","accentColor":"#06B6D4","backgroundColor":"#ECFEFF"},
              {"id":"scanpro-3","title":"PDF","accentColor":"#DC2626","backgroundColor":"#FEF2F2"}
            ]
          },
          {
            "id":"pixelrun",
            "name":"Pixel Run",
            "developer":"Level Up Games",
            "category":"Игры",
            "shortDescription":"Аркадный раннер с короткими динамичными забегами",
            "fullDescription":"Pixel Run предлагает быстрые уровни, яркие локации и таблицу рекордов.",
            "ageRating":"8+",
            "iconGradient":["#A855F7","#EC4899"],
            "popular":false,
            "screenshots":[
              {"id":"pixelrun-1","title":"Первый уровень","accentColor":"#A855F7","backgroundColor":"#FAF5FF"},
              {"id":"pixelrun-2","title":"Бонусы","accentColor":"#EC4899","backgroundColor":"#FDF2F8"},
              {"id":"pixelrun-3","title":"Рекорды","accentColor":"#FACC15","backgroundColor":"#FEFCE8"}
            ]
          },
          {
            "id":"chesswave",
            "name":"Chess Wave",
            "developer":"Mind Board",
            "category":"Игры",
            "shortDescription":"Шахматные задачи и партии с друзьями",
            "fullDescription":"Chess Wave помогает тренировать тактику, решать ежедневные задачи и играть короткие партии.",
            "ageRating":"12+",
            "iconGradient":["#111827","#F97316"],
            "popular":false,
            "screenshots":[
              {"id":"chesswave-1","title":"Доска","accentColor":"#111827","backgroundColor":"#F9FAFB"},
              {"id":"chesswave-2","title":"Задачи","accentColor":"#F97316","backgroundColor":"#FFF7ED"},
              {"id":"chesswave-3","title":"Профиль","accentColor":"#2563EB","backgroundColor":"#EFF6FF"}
            ]
          },
          {
            "id":"metrogo",
            "name":"MetroGo",
            "developer":"Transit Soft",
            "category":"Транспорт",
            "shortDescription":"Маршруты метро, избранное и время в пути",
            "fullDescription":"MetroGo строит маршруты по метро, показывает пересадки и сохраняет избранные станции.",
            "ageRating":"0+",
            "iconGradient":["#DC2626","#2563EB"],
            "popular":true,
            "screenshots":[
              {"id":"metrogo-1","title":"Карта метро","accentColor":"#DC2626","backgroundColor":"#FEF2F2"},
              {"id":"metrogo-2","title":"Маршрут","accentColor":"#2563EB","backgroundColor":"#EFF6FF"},
              {"id":"metrogo-3","title":"Избранное","accentColor":"#F59E0B","backgroundColor":"#FFFBEB"}
            ]
          },
          {
            "id":"busline",
            "name":"BusLine",
            "developer":"Move City",
            "category":"Транспорт",
            "shortDescription":"Расписание автобусов и ближайшие остановки",
            "fullDescription":"BusLine помогает найти ближайшую остановку, посмотреть расписание и оценить время прибытия транспорта.",
            "ageRating":"0+",
            "iconGradient":["#10B981","#F59E0B"],
            "popular":false,
            "screenshots":[
              {"id":"busline-1","title":"Остановки","accentColor":"#10B981","backgroundColor":"#ECFDF5"},
              {"id":"busline-2","title":"Расписание","accentColor":"#F59E0B","backgroundColor":"#FFFBEB"},
              {"id":"busline-3","title":"Маршруты","accentColor":"#0EA5E9","backgroundColor":"#F0F9FF"}
            ]
          },
          {
            "id":"goshelp",
            "name":"ГосПомощь",
            "developer":"Городские сервисы",
            "category":"Государственные",
            "shortDescription":"Запись, заявления и статусы обращений",
            "fullDescription":"ГосПомощь объединяет популярные государственные услуги: запись в ведомства, отправку заявлений и отслеживание статусов.",
            "ageRating":"0+",
            "iconGradient":["#2563EB","#14B8A6"],
            "popular":true,
            "screenshots":[
              {"id":"goshelp-1","title":"Услуги","accentColor":"#2563EB","backgroundColor":"#EFF6FF"},
              {"id":"goshelp-2","title":"Заявления","accentColor":"#14B8A6","backgroundColor":"#F0FDFA"},
              {"id":"goshelp-3","title":"Статусы","accentColor":"#16A34A","backgroundColor":"#F0FDF4"}
            ]
          },
          {
            "id":"cityid",
            "name":"City ID",
            "developer":"Urban Digital",
            "category":"Государственные",
            "shortDescription":"Личный кабинет жителя и городские уведомления",
            "fullDescription":"City ID показывает важные городские уведомления, обращения и записи.",
            "ageRating":"6+",
            "iconGradient":["#0F766E","#84CC16"],
            "popular":false,
            "screenshots":[
              {"id":"cityid-1","title":"Главная","accentColor":"#0F766E","backgroundColor":"#F0FDFA"},
              {"id":"cityid-2","title":"Уведомления","accentColor":"#84CC16","backgroundColor":"#F7FEE7"},
              {"id":"cityid-3","title":"Профиль","accentColor":"#475569","backgroundColor":"#F8FAFC"}
            ]
          }
        ]
    """.trimIndent()

    private companion object {
        val JSON = "application/json; charset=utf-8".toMediaType()
    }
}
